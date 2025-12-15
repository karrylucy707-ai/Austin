查看代码中 `Optional` 的具体用法：

## Optional 是什么？

### 基本概念

`Optional<T>` 是一个容器对象，可以：
- 包含一个非 null 的值（`Optional.of(value)`）
- 为空（`Optional.empty()`）

核心思想：显式表示“值可能不存在”，避免直接返回 null 导致空指针异常。

### 为什么使用 Optional？

`messageTemplateDao.findById(messageTemplateId)` 可能找不到数据：
- 如果找到：返回 `Optional.of(messageTemplate)`
- 如果没找到：返回 `Optional.empty()`

如果没有 Optional，可能这样写：
```java
// ❌ 不推荐的方式（可能返回 null）
MessageTemplate messageTemplate = messageTemplateDao.findById(messageTemplateId);
if (messageTemplate == null) {  // 可能空指针
    // 处理
}
```

使用 Optional 的好处：
- 显式表示可能为空
- 减少空指针异常的风险
- 提供更清晰的处理方式

---

## Optional 常用方法

### 1. `isPresent()` - 检查是否有值

```java
if (!messageTemplate.isPresent()) {
    // Optional 为空，没有找到数据
}
```
等价于：
```java
if (messageTemplate == null) {
    // 传统方式
}
```
### 2. `get()` - 获取值
```java
messageTemplate.get()  // 如果 Optional 有值，返回 MessageTemplate 对象
```
注意：如果 Optional 为空，调用 `get()` 会抛出 `NoSuchElementException`，因此通常先调用 `isPresent()` 检查。
### 3. `orElse()` - 提供默认值

```103:103:austin-web/src/main/java/com/java3y/austin/web/service/impl/MessageTemplateServiceImpl.java
        return messageTemplateDao.findById(id).orElse(null);
```
如果 Optional 为空，返回 `null`（或你指定的默认值）。
---

@Bean 是 方法级别 的注解，用于显式声明一个 Spring 容器管理的 Bean。
它告诉 Spring：“这个方法返回的对象应该被注册为 Bean”。

@Qualifier 的作用
核心作用：在多个同类型 Bean 时指定注入哪一个
当 Spring 容器中有多个相同类型的 Bean 时，@Qualifier 指定要注入的 Bean 名称。

--------------------------------------------------------------------------------------------------------------------------------------------------

# `JSON.toJSONString(sendTaskModel.getTaskInfo(), JSONWriter.Feature.WriteClassName)` 解析

## 一、代码功能说明

这行代码使用 **Fastjson** 库将 Java 对象转换为 JSON 字符串，并启用特殊功能来**保留类型信息**。

```java
String message = JSON.toJSONString(
    sendTaskModel.getTaskInfo(),  // 要序列化的对象
    JSONWriter.Feature.WriteClassName  // 序列化特性：写入类名
);
```

## 二、参数详解

### 1. **sendTaskModel.getTaskInfo()**
- 获取 `SendTaskModel` 中的任务信息对象
- 可能是一个自定义的 POJO、Map 或其他 Java 对象
- 示例：
  ```java
  public class TaskInfo {
      private Long taskId;
      private String content;
      private Integer priority;
      // getters/setters
  }
  ```

### 2. **JSONWriter.Feature.WriteClassName**
- Fastjson 的序列化特性之一
- **核心作用**：在 JSON 字符串中**添加 Java 类的类型信息**
- **序列化结果**：生成的 JSON 会包含 `@type` 字段

-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------