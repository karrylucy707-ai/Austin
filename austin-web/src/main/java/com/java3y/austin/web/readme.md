🟦 austin-web 模块（main目录）
🟨 controller（对外 REST 接口）
    包含：
        SendController（发送）
        MessageTemplateController（模板管理）
        ChannelAccountController（渠道账号）
        MaterialController（素材上传）
        DataController（数据查询）
        OfficialAccountController / MiniProgramController / AlipayMiniProgramController / RefreshTokenController
        微信、支付宝、小程序 Token 绑定与刷新
        HealthController（健康检查）
🟩 service / service.impl（业务服务）
    作用：
        MessageTemplateServiceImpl：模板 CRUD、软删、复制、定时任务启动/暂停（调用 XXL-Job）、状态初始化/重置。
        ChannelAccountServiceImpl：渠道账号 CRUD 与查询。
        MaterialServiceImpl：素材落地存储、路径返回。
        DataServiceImpl：数据查询实现（给 DataController 用）
🟦 vo（视图对象）
    包括：
        MessageTemplateParam/Vo：模板列表查询入参与分页结果。
        DataParam：数据查询条件。
        UploadResponseVo：上传返回。
        amis 子包（CommonAmisVo、EchartsVo、SmsTimeLineVo、UserTimeLineVo）： 为 amis 前端格式化输出。
        RequestLogDTO：请求日志 DTO。
🟧 utils（工具类）
    如：
        LoginUtils：登录/鉴权辅助（是否需登录，读取上下文）。
        Convert4Amis：把实体/列表转为 amis 需要的扁平结构，处理测试内容占位符等。
        AnchorStateUtils：锚点状态工具（链路状态枚举转换）。
        SpringFileUtils：文件操作辅助。
🟪 config（Web 配置）
    包含：
        CommonConfiguration：注册 FastJson HTTP 消息转换器。
        CrossConfig：跨域配置。
        SwaggerConfiguration：Swagger 文档配置。
        WeChatLoginConfig：微信相关登录/回调配置。

🟥 annotation / aspect（注解 & 切面）
    AustinAspect
    AustinResult

🟦 advice（返回增强）
    AustinResponseBodyAdvice：统一响应包装。

🟩 exception（异常处理）
    包含：
        CommonException / ExceptionHandlerAdvice：自定义异常与全局异常处理。

🟨 handler（公众号/小程序事件）
    SubscribeHandler / UnSubscribeHandler / ScanHandler：处理公众号/小程序关注、取消关注、扫码等事件回调。