
### 核心关系：Topic 是链路的“路由标识”，而非链路本身
可以把消息链路想象成**快递系统**：
- **`Topic` = 城市或区域分拣中心**
- **`消息链路` = 从发货到收货的完整物流路径**

一条完整的消息链路 (`Message Pipeline`) 通常包含以下环节：
```
生产者应用 → [发送至Topic] → 消息队列服务 → [消费者组订阅Topic] → 消费者应用 → 业务处理逻辑
```
**Topic 是图中 `[ ]` 标注的核心路由点。**

---

### 以 Austin 项目为例的消息链路
假设发送一条营销短信，完整链路如下：

1.  **API接收**：`SendServiceImpl.send()` 接收请求
2.  **责任链处理**：参数校验、组装任务模型
3.  **发送至MQ**：`SendMqService.send("austin_sms_topic", taskInfoJson, "marketing")`
    *   **`austin_sms_topic`** 在这里决定了消息进入 **“短信业务流”**。
4.  **MQ存储与分发**：RocketMQ/Kafka 将消息持久化在 `austin_sms_topic`，并分发给订阅了该Topic的消费者组。
5.  **消费者处理**：`SmsConsumer` 服务监听 `austin_sms_topic`，拉取消息。
6.  **下游执行**：消费者调用短信服务商API，最终触达用户手机。
查看项目中 topic 的使用情况，确认不同渠道是否使用不同 topic：

结论：不是按渠道区分 topic，而是按业务类型（发送/撤回）区分 topic。所有渠道的消息都发送到同一个 topic，然后在 handler 层根据 `TaskInfo` 中的渠道信息路由到不同的渠道处理器。

## Topic 的设计方式

### Topic 是按业务类型区分，不是按渠道区分

项目中只配置了两个 topic：

```43:45:austin-web/src/main/resources/application.properties
austin.business.topic.name=austinBusiness
austin.business.recall.topic.name=austinRecall
austin.business.log.topic.name=austinTraceLog
```

- `austinBusiness`：所有发送消息的 topic（无论什么渠道）
- `austinRecall`：所有撤回消息的 topic
- `austinTraceLog`：日志追踪的 topic

---

## 消息发送流程

### 1. API 层：所有渠道的消息都发送到同一个 topic

```

关键点：
- 无论短信、邮件、微信等，都发送到 `sendMessageTopic`（即 `austinBusiness`）
- 渠道信息存储在 `TaskInfo` 的 `sendChannel` 字段中

---

### 2. Handler 层：根据 TaskInfo 中的渠道信息路由

消费消息后，根据 `TaskInfo.sendChannel` 路由到对应的渠道处理器：

关键代码：
```java
handlerHolder.route(taskInfo.getSendChannel()).doHandler(taskInfo);
```
- 根据 `taskInfo.getSendChannel()` 路由
- 例如：`sendChannel = 30` → 短信 Handler，`sendChannel = 40` → 邮件 Handler

---

## 完整消息流转图

```
┌─────────────────────────────────────────────────────────┐
│ API层：SendMqAction                                     │
│ 所有渠道的消息都发送到同一个 topic                      │
│                                                         │
│ TaskInfo {                                              │
│   sendChannel: 30 (短信)                               │
│   receiver: "13800138000"                              │
│   contentModel: SmsContentModel                        │
│ }                                                       │
│                                                         │
│ TaskInfo {                                              │
│   sendChannel: 40 (邮件)                               │
│   receiver: "test@qq.com"                              │
│   contentModel: EmailContentModel                      │
│ }                                                       │
│                                                         │
│ ↓ 都发送到同一个 topic                                 │
│ sendMqService.send("austinBusiness", message, tagId)   │
└─────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│ MQ: austinBusiness Topic                                │
│ 包含所有渠道的消息（短信、邮件、微信、钉钉...）         │
└─────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│ Handler层：Receiver 消费消息                            │
│ @KafkaListener(topics = "austinBusiness")              │
│ 或 RabbitMqReceiver / RedisReceiver                     │
└─────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│ Handler层：SendMessageAction                            │
│ 根据 TaskInfo.sendChannel 路由                         │
│                                                         │
│ handlerHolder.route(sendChannel)                       │
│   ├─ sendChannel=30 → SmsHandler                       │
│   ├─ sendChannel=40 → EmailHandler                     │
│   ├─ sendChannel=50 → OfficialAccountHandler           │
│   ├─ sendChannel=60 → MiniProgramHandler               │
│   └─ ... 其他渠道                                      │
└─────────────────────────────────────────────────────────┘
`
## 配置示例

从配置可以看到，只有业务层面的 topic 区分：

```properties
# 发送消息的 topic（所有渠道共用）
austin.business.topic.name=austinBusiness

# 撤回消息的 topic（所有渠道共用）
austin.business.recall.topic.name=austinRecall

# 日志追踪的 topic
austin.business.log.topic.name=austinTraceLog
```

没有针对渠道的 topic 配置，如：
- ~~austin.business.topic.sms~~
- ~~austin.business.topic.email~~
- ~~austin.business.topic.wechat~~

---

## 总结

- Topic 是按业务类型区分的（发送/撤回），不是按渠道区分。
- 所有渠道（短信、邮件、微信、钉钉等）的消息都发送到同一个 topic：`austinBusiness`。
- 渠道信息存储在 `TaskInfo.sendChannel` 字段中。
- Handler 层根据 `sendChannel` 路由到对应的渠道处理器。
- 这种设计的优势是配置简单、路由灵活、便于批量处理和扩展。
------------------------------------------------------------------------------------------------------------------------

## 总结
消费者可以根据 tagId 拿到不同的消息
`tagId` 的作用：

1. 消息标签：为消息添加标签，便于分类和识别
2. 消息过滤：消费者只消费带有指定标签的消息
3. 环境隔离：多环境/多服务实例共享 topic 时实现隔离
4. 灵活扩展：可按需配置不同的 tagId，无需创建新 topic

在不同 MQ 中的实现：
- Kafka：使用 Header + 过滤策略
- RocketMQ：使用原生 Tag 功能
- Redis/RabbitMQ/EventBus：基本不使用或仅用于日志

这样可以在同一个 topic 中实现消息的分类和选择性消费。