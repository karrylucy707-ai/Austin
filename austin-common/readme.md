## constant —— 常量类

### AustinConstant（项目级常量）

* **BUSINESS_ID_LENGTH**：业务 ID 默认长度
* **BATCH_RECEIVER_SIZE**：批量接收者最大数量（默认 100）
* **SEND_ALL**：发送给全部人的标识（`@all`）
* **CACHE_KEY_PREFIX**：链路追踪缓存 Key 前缀
* **DEFAULT_CREATOR / UPDATOR / TEAM / AUDITOR**：默认创建者 / 更新者 / 团队 / 审核者

### CommonConstant（通用常量）

* **符号常量**：`COMMA`、`COLON`、`SLASH` 等
* **布尔值常量**：`TRUE = 1`、`FALSE = 0`
* **HTTP 相关常量**：`Content-Type`、请求方法等
* **编码格式**：`UTF-8`

### OfficialAccountParamConstant

* 微信公众号参数相关常量

### SendChanelUrlConstant

* 各发送渠道对应的 URL 常量

### ThreadPoolConstant

* 线程池相关常量配置

---

## domain —— 领域模型

### TaskInfo（发送任务信息，核心领域模型）

* **bizId**：业务消息发送 ID（用于链路追踪）
* **messageId**：消息唯一 ID
* **messageTemplateId**：消息模板 ID
* **businessId**：业务 ID
* **receiver**：接收者集合
* **sendChannel**：发送渠道
* **templateType**：模板类型
* **msgType**：消息类型
* **contentModel**：发送内容模型（不同渠道对应不同模型）
* **sendAccount**：发送账号

### RecallTaskInfo

* 撤回任务信息模型

### AnchorInfo

* 锚点信息（用于链路追踪）

### SimpleAnchorInfo

* 简化版锚点信息

### SimpleTaskInfo

* 简化版任务信息

### LogParam

* 日志参数模型

---

## dto —— 数据传输对象

### dto/account —— 账号 DTO

#### 短信账号

* **TencentSmsAccount**
* **YunPianSmsAccount**
* **LinTongSmsAccount**
* **SmsAccount**（通用短信账号）

#### 其他渠道账号

* **WeChatOfficialAccount**：微信服务号账号
* **WeChatMiniProgramAccount**：微信小程序账号
* **AlipayMiniProgramAccount**：支付宝小程序账号
* **EnterpriseWeChatRobotAccount**：企业微信机器人账号
* **DingDingRobotAccount**：钉钉机器人账号
* **DingDingWorkNoticeAccount**：钉钉工作通知账号
* **FeiShuRobotAccount**：飞书机器人账号
* **GeTuiAccount**：个推账号

---

### dto/model —— 内容模型 DTO

> 各渠道发送内容模型，统一继承 **ContentModel**

* **ContentModel**：内容模型基类
* **EmailContentModel**：邮件内容模型
* **SmsContentModel**：短信内容模型
* **PushContentModel**：Push 通知栏内容模型
* **OfficialAccountsContentModel**：微信服务号内容模型
* **MiniProgramContentModel**：微信小程序内容模型
* **AlipayMiniProgramContentModel**：支付宝小程序内容模型
* **EnterpriseWeChatContentModel**：企业微信内容模型
* **EnterpriseWeChatRobotContentModel**：企业微信机器人内容模型
* **DingDingRobotContentModel**：钉钉机器人内容模型
* **DingDingWorkContentModel**：钉钉工作通知内容模型
* **FeiShuRobotContentModel**：飞书机器人内容模型
* **ImContentModel**：IM 站内信内容模型

---

## enums —— 枚举类

### ChannelType（发送渠道类型）

* 定义系统支持的所有发送渠道：IM、PUSH、SMS、EMAIL、微信、钉钉、企业微信、飞书、支付宝等
* 维护 **渠道 → ContentModel Class** 的映射关系
* 提供根据 `code` 获取对应 ContentModel Class 的方法

### 其他枚举

* **MessageType**：消息类型（通知类 / 营销类 / 验证码）
* **TemplateType**：模板类型（实时 / 定时）
* **MessageStatus**：消息状态（初始化、停止、运行等）
* **AuditStatus**：审核状态
* **AnchorState**：锚点状态（链路追踪状态）
* **RespStatusEnum**：响应状态（成功、失败等）
* **DeduplicationType**：去重类型
* **ShieldType**：屏蔽类型
* **SmsStatus**：短信状态
* **SmsSupplier**：短信供应商
* **IdType**：ID 类型
* **FileType**：文件类型
* **SendMessageType**：发送消息类型

### PowerfulEnum

* 增强枚举接口，统一提供 `code`、`description` 等能力

### EnumUtil

* 枚举工具类

---

## pipeline —— 责任链 / 管道模式

### ProcessController（流程控制器）

* 管理多个 **ProcessTemplate**
* 执行责任链流程，顺序遍历流程节点
* 负责前置校验：

    * `context` 校验
    * `businessCode` 校验
    * 模板合法性校验

### ProcessTemplate（流程模板）

* 存储流程节点列表：`List<BusinessProcess>`

### BusinessProcess（业务处理节点接口）

* 定义统一处理方法：

  ```java
  void process(ProcessContext<T> context);
  ```
* 每个流程节点实现该接口

### ProcessContext（责任链上下文）

* **code**：责任链标识
* **processModel**：上下文数据模型
* **needBreak**：是否中断流程
* **response**：处理结果

### ProcessModel

* 流程模型标记接口

### ProcessException

* 流程执行异常

---

## vo —— 视图对象

### BasicResultVO（统一响应封装）

* **status**：响应状态
* **msg**：响应消息
* **data**：返回数据

#### 常用静态方法

* `success()`
* `fail()`
