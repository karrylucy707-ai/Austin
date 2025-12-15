package com.java3y.austin.service.api.impl.service;

import cn.monitor4all.logRecord.annotation.OperationLog;
import com.java3y.austin.common.domain.SimpleTaskInfo;
import com.java3y.austin.common.enums.RespStatusEnum;
import com.java3y.austin.common.pipeline.ProcessContext;
import com.java3y.austin.common.pipeline.ProcessController;
import com.java3y.austin.common.vo.BasicResultVO;
import com.java3y.austin.service.api.domain.BatchSendRequest;
import com.java3y.austin.service.api.domain.SendRequest;
import com.java3y.austin.service.api.domain.SendResponse;
import com.java3y.austin.service.api.impl.domain.SendTaskModel;
import com.java3y.austin.service.api.service.SendService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 发送接口
 *
 * @author 3y
 */
@Service
public class SendServiceImpl implements SendService {

    @Autowired
    @Qualifier("apiProcessController")
    private ProcessController processController;
//为什么将send和batchsend两个逻辑类似的方法分开来写？
    /*
    两个方法代码不同点：
        接受的参数类型不同，sendRequest用来存储消息的是类对象
        而batchSend的BatchSendRequest是List
        然后用封装成相同的数据模型，SendTaskModel，但是需要先进性同一化操作，将send的单个消息也收集成List
    这样写优点：
        符合单一职责原则
        提高了代码的可扩展性
        接口功能清晰明确
    * */
    @Override
    @OperationLog(bizType = "SendService#send", bizId = "#sendRequest.messageTemplateId", msg = "#sendRequest")
    public SendResponse send(SendRequest sendRequest) {
        //前端传的参数被封装到了sendRequest，对sendRequest进行非空判断
        if (ObjectUtils.isEmpty(sendRequest)) {
            return new SendResponse(RespStatusEnum.CLIENT_BAD_PARAMETERS.getCode(), RespStatusEnum.CLIENT_BAD_PARAMETERS.getMsg(), null);
        }
        //拿到sendRequest传来的数据封装到发送消息数据模板，SendTaskModel是ProsessModel责任链数据模型的实现类
        SendTaskModel sendTaskModel = SendTaskModel.builder()
                .messageTemplateId(sendRequest.getMessageTemplateId())
                .messageParamList(Collections.singletonList(sendRequest.getMessageParam()))
                .build();
        //设置code，责任链数据模型，责任链中断标识，执行成功返回值构造责任链上下文数据模型
        ProcessContext context = ProcessContext.builder()
                .code(sendRequest.getCode())
                .processModel(sendTaskModel)
                .needBreak(false)
                .response(BasicResultVO.success()).build();
        //传入责任链上下文模型，构造责任链
        ProcessContext process = processController.process(context);

        return new SendResponse(process.getResponse().getStatus(), process.getResponse().getMsg(), (List<SimpleTaskInfo>) process.getResponse().getData());
    }

    @Override
    @OperationLog(bizType = "SendService#batchSend", bizId = "#batchSendRequest.messageTemplateId", msg = "#batchSendRequest")
    public SendResponse batchSend(BatchSendRequest batchSendRequest) {
        if (ObjectUtils.isEmpty(batchSendRequest)) {
            return new SendResponse(RespStatusEnum.CLIENT_BAD_PARAMETERS.getCode(), RespStatusEnum.CLIENT_BAD_PARAMETERS.getMsg(), null);
        }

        SendTaskModel sendTaskModel = SendTaskModel.builder()
                .messageTemplateId(batchSendRequest.getMessageTemplateId())
                .messageParamList(batchSendRequest.getMessageParamList())
                .build();

        ProcessContext context = ProcessContext.builder()
                .code(batchSendRequest.getCode())
                .processModel(sendTaskModel)
                .needBreak(false)
                .response(BasicResultVO.success()).build();

        ProcessContext process = processController.process(context);

        return new SendResponse(process.getResponse().getStatus(), process.getResponse().getMsg(), (List<SimpleTaskInfo>) process.getResponse().getData());
    }


}
