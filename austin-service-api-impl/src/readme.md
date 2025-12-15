SendServiceImp对调用发送消息请求进行处理， 前端传的参数被封装到sendRequest
拿到sendRequest传来的数据封装到发送消息数据模板，SendTaskModel是ProsessModel责任链数据模型的实现类
设置code，责任链数据模型，责任链中断标识，执行成功返回值构造责任链上下文数据模型
传入责任链上下文模型，通过@Qulifier注解指定的prosessController的prosess构造责任链

那么责任链是怎么构造的呢
    PiplineControl类负责构造send责任链，通过责任链模板的setProcessList将action的功能类放入list
    （传入参数格式检查，传入参数合法性检查，按照消息模板组装成消息，发送到MQ），
    然后通过apiProcessController将组装好的责任链开放给service调用
### SendServiceImpl 调用发送消息的整体处理流程说明
1. **请求接入**

    * `SendServiceImpl` 负责处理发送消息的业务请求。
    * 前端传入的参数首先被封装为 `SendRequest` 对象。

2. **数据模型封装**

    * 从 `SendRequest` 中获取请求数据，并将其封装为**发送消息的数据模板**。
    * `SendTaskModel` 是 `ProcessModel` 的实现类，用作**责任链中流转的数据模型**。

3. **责任链上下文初始化**

    * 设置：

        * `code`（业务标识）
        * 责任链数据模型（`SendTaskModel`）
        * 责任链中断标识
        * 执行成功后的返回值
    * 上述信息共同构成**责任链上下文数据模型**（Context）。

4. **责任链执行入口**

    * 将责任链上下文模型传入责任链执行入口。
    * 通过 `@Qualifier` 注解指定对应的 `ProcessController`，并调用其 `process` 方法，触发责任链执行。

---

### 责任链是如何构造的？

1. **责任链构造者**

    * `PipelineControl` 类负责构造 **Send 消息责任链**。

2. **责任链模板组装**

    * 通过责任链模板的 `setProcessList` 方法，将各个 **Action 功能节点** 按顺序加入到 `List` 中。
    * Action 节点主要包括：

        1. 参数格式校验
        2. 参数合法性校验
        3. 根据消息模板组装消息
        4. 将消息发送到 MQ

3. **责任链对外暴露**

    * 通过 `ApiProcessController` 将已经组装完成的责任链暴露给 `Service` 层调用。
    * `Service` 层只负责触发执行，不关心责任链内部的具体实现细节。

