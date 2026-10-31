# Kafka Spring Boot Demo

一个完整的 Spring Boot Kafka 集成演示项目，展示如何在 Spring Boot 应用中集成 Kafka 消息队列，并提供美观的 Web 界面进行消息管理和实时监控。

## 📋 项目特性

- ✅ Spring Boot 集成 Kafka 消息生产者和消费者
- ✅ 美观的 Web UI 界面，实时显示 Kafka 消息统计
- ✅ 一键发送消息到 Kafka
- ✅ 实时监控消息延迟（lag）和消费进度
- ✅ 自动刷新统计信息（30秒一次）
- ✅ 响应式设计，支持移动设备
- ✅ RESTful API 接口
- ✅ Java 8 兼容

## 📁 项目结构

```
KafkaSpringBootDemo/
├── src/
│   └── main/
│       ├── java/org/bigdata/kafka/springbootdemo/
│       │   ├── KafkaSpringBootDemoApplication.java    # Spring Boot 主应用入口
│       │   ├── controller/
│       │   │   ├── HomeController.java                # 首页控制器
│       │   │   ├── MyKafkaController.java             # Kafka 消息发送控制器
│       │   │   └── StatsController.java               # 统计信息 API 控制器
│       │   ├── consumer/
│       │   │   └── MyConsumer.java                    # Kafka 消费者监听器
│       │   └── service/
│       │       └── KafkaStatsService.java             # Kafka 统计服务
│       └── resources/
│           ├── templates/
│           │   └── index.html                         # HTML 模板（页面结构）
│           ├── static/
│           │   ├── css/
│           │   │   └── style.css                      # 样式表（UI 样式）
│           │   └── js/
│           │       └── app.js                         # 前端脚本（交互逻辑）
│           └── application.yml                        # 应用配置文件
├── pom.xml                                            # Maven 依赖配置
└── README.md                                          # 项目说明文档
```

## 📖 代码结构说明

### 后端代码

#### 1. **KafkaSpringBootDemoApplication.java** - 应用主入口
Spring Boot 应用的入口点，启用 Kafka 功能。

```java
@SpringBootApplication
@EnableKafka
public class KafkaSpringBootDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(KafkaSpringBootDemoApplication.class, args);
    }
}
```

**关键注解**:
- `@SpringBootApplication`: 标记为 Spring Boot 应用
- `@EnableKafka`: 启用 Kafka 消息队列功能

#### 2. **HomeController.java** - 首页控制器
处理首页请求，返回 HTML 模板。

```java
@Controller
public class HomeController {
    @GetMapping("/")
    public String index() {
        return "index";  // 返回 templates/index.html
    }
}
```

**功能**:
- 处理 `GET /` 请求
- 返回首页模板 `index.html`

#### 3. **MyKafkaController.java** - Kafka 生产者
向 Kafka 发送消息的控制器。

```java
@RestController
@RequestMapping("/msg")
public class MyKafkaController {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @RequestMapping("/send")
    public String sendMessage(){
        kafkaTemplate.send("test1", 0, "key", "this is a message!");
        return "send success!";
    }
}
```

**详细说明**:
- `@RestController`: 返回 JSON 响应的控制器
- `KafkaTemplate`: Spring 提供的 Kafka 模板，用于发送消息
- `send()`: 发送消息到主题 `test1`，分区 0
- 返回发送成功的消息

#### 4. **StatsController.java** - 统计 API
提供 Kafka 统计信息的 REST API。

```java
@RestController
@RequestMapping("/api")
public class StatsController {
    @Autowired
    private KafkaStatsService kafkaStatsService;
    
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return kafkaStatsService.getKafkaStats();
    }
}
```

**功能**:
- `GET /api/stats`: 返回 JSON 格式的统计信息
- 包含消息总数、消费者偏移、延迟等数据

#### 5. **KafkaStatsService.java** - 统计服务
获取 Kafka 消息统计信息的业务逻辑层。

**核心功能**:
- 使用 AdminClient 获取主题分区信息
- 获取消费者组的消费偏移
- 计算消息延迟（lag = 总消息数 - 已消费消息数）
- 返回 Map 格式的统计数据

**返回数据**:
```json
{
    "topic": "test1",
    "groupId": "testGroup",
    "totalMessages": 157,
    "consumerOffset": 93,
    "lag": 64,
    "bootstrapServers": "hadoop01.hdp.com:6667"
}
```

#### 6. **MyConsumer.java** - Kafka 消费者
监听并处理来自 Kafka 的消息。

```java
@Component
public class MyConsumer {
    @KafkaListener(topics = "test1", groupId = "testGroup")
    public void consume(String message) {
        System.out.println("Received message: " + message);
    }
}
```

**说明**:
- `@KafkaListener`: 标记为 Kafka 消息监听器
- `topics = "test1"`: 监听的主题
- `groupId = "testGroup"`: 所属的消费者组
- `consume()`: 接收并处理消息的方法

### 前端代码

#### 1. **index.html** - 页面结构（70 行）

**特点**:
- 清晰的语义 HTML 结构
- 使用 Thymeleaf 模板引擎
- 引入外部 CSS 和 JavaScript

**主要部分**:
- 页面标题和副标题
- 基本信息展示区
- 消息统计信息卡片
- 操作按钮（发送消息、刷新统计）
- 响应信息展示区
- API 文档

#### 2. **style.css** - 样式表（310 行）

**包含内容**:
- **基础样式**
  - 全局字体、颜色、间距
  - 容器布局和阴影效果
  
- **组件样式**
  - 标题和段落样式
  - 状态信息显示
  - 统计卡片（渐变背景、悬停效果）
  - 按钮样式（颜色、过渡动画）
  - 响应消息提示

- **响应式设计**
  - 平板设备 (768px 以下)
  - 手机设备 (480px 以下)
  - 弹性布局和网格

- **交互效果**
  - 按钮悬停动画
  - 卡片阴影和过渡
  - 加载状态提示

#### 3. **app.js** - 前端脚本（85 行）

**关键函数**:

| 函数名 | 功能 | 说明 |
|--------|------|------|
| `sendMessage()` | 发送消息 | 调用 `/msg/send` API，显示发送结果 |
| `refreshStats()` | 刷新统计 | 调用 `/api/stats` API，获取最新数据 |
| `updateStatsDisplay(data)` | 更新显示 | 将统计数据渲染到页面 |
| `displayStatsError()` | 显示错误 | 错误时显示 "连接失败" |
| `initializeApp()` | 初始化 | 页面加载时执行，设置自动刷新 |

**工作流程**:
```
页面加载 → initializeApp() → refreshStats() → 自动每30秒调用refreshStats()
点击按钮 → sendMessage() → 发送消息 → 自动刷新统计
```

### 配置文件

#### application.yml - 应用配置

```yaml
server:
  port: 8080

spring:
  kafka:
    bootstrap-servers: hadoop01.hdp.com:6667  # Kafka 服务器地址
    producer:
      retries: 3                              # 发送失败重试次数
      batch-size: 16384                       # 批处理消息大小
      buffer-memory: 33554432                 # 缓冲区大小
      acks: 1                                 # 确认级别（1=leader确认）
      key-serializer: ...StringSerializer
      value-serializer: ...StringSerializer
    consumer:
      group-id: default-group                 # 消费者组 ID
      enable-auto-commit: false               # 禁用自动提交偏移
      auto-offset-reset: earliest             # 无偏移时从最早消息开始
      key-deserializer: ...StringDeserializer
      value-deserializer: ...StringDeserializer
      max-poll-records: 500                   # 单次拉取最大消息数
    listener:
      ack-mode: MANUAL_IMMEDIATE              # 手动立即确认消息
```

**配置说明**:
- **bootstrap-servers**: Kafka 集群地址，多个地址用逗号分隔
- **producer**: 生产者配置，影响消息发送性能
- **consumer**: 消费者配置，影响消息消费行为
- **listener**: 监听器配置，影响消息确认方式

## 🚀 启动方法

### 前置要求

1. **Java 环境**
   ```bash
   # 检查 Java 版本（需要 Java 8+）
   java -version
   ```

2. **Kafka 环境**
   - Kafka 集群已启动并运行
   - 默认地址: `hadoop01.hdp.com:6667`
   - 已创建主题: `test1`
   - 消费者组: `testGroup`

3. **Maven** (可选，项目内包含 mvnw)
   ```bash
   # 检查 Maven 版本
   mvn -version
   ```

### 启动方式

#### 方式 1：使用 Maven Wrapper（推荐）

```bash
# 进入项目目录
cd KafkaSpringBootDemo

# 清理并构建项目
./mvnw clean package -DskipTests

# 启动应用
./mvnw spring-boot:run
```

#### 方式 2：直接运行 JAR 包

```bash
# 构建 JAR 包
./mvnw clean package -DskipTests

# 运行 JAR 包
java -jar target/KafkaSpringBootDemo-0.0.1-SNAPSHOT.jar
```

**启动参数示例**:
```bash
# 自定义 Kafka 服务器
java -jar target/KafkaSpringBootDemo-0.0.1-SNAPSHOT.jar \
  --spring.kafka.bootstrap-servers=kafka.example.com:9092

# 自定义服务器端口
java -jar target/KafkaSpringBootDemo-0.0.1-SNAPSHOT.jar \
  --server.port=8888
```

#### 方式 3：IDE 开发运行

**IntelliJ IDEA**:
1. 打开项目
2. 找到 `KafkaSpringBootDemoApplication.java`
3. 右键 → Run 或 Shift+F10

**Eclipse**:
1. 右键项目 → Run As → Spring Boot App

### 验证应用启动

启动成功后，会看到日志：
```
Started KafkaSpringBootDemoApplication in 1.756 seconds (JVM running for 1.962)
Tomcat started on port(s): 8080 (http) with context path ''
```

## 🌐 使用应用

### 打开浏览器

```
http://localhost:8080/
```

### 页面功能

1. **📋 基本信息区**
   - 显示应用状态
   - 服务器地址和 Kafka 连接信息

2. **📊 消息统计区**
   四个统计卡片显示实时数据：
   - **消息总数**: 主题 `test1` 中的所有消息数
   - **消费者偏移**: 消费者组 `testGroup` 已消费的消息数
   - **消息延迟**: 未被消费的消息数（lag）
   - **消费进度**: 消费百分比 (%)

3. **📤 操作按钮**
   - **发送消息到 Kafka**: 向主题发送一条测试消息
   - **🔄 刷新统计**: 立即获取最新统计数据

4. **📝 响应信息**
   - 显示发送消息的结果
   - 显示可能的错误信息

5. **📚 API 文档**
   - 列出所有可用的 API 端点
   - 说明使用方法

### 工作流程示例

```
1. 打开 http://localhost:8080
   ↓
2. 页面加载后自动显示当前统计信息
   ↓
3. 点击"发送消息到 Kafka"按钮
   ↓
4. 消息发送成功，页面显示"✅ send success!"
   ↓
5. 1秒后自动刷新统计信息
   ↓
6. 消息总数和偏移量增加 1
```

## 📡 API 文档

### 1. 发送消息

```http
GET /msg/send
```

**功能**: 向 Kafka 主题发送一条测试消息

**请求参数**: 无

**响应**:
```
200 OK
Content-Type: text/plain

send success!
```

**使用示例**:
```bash
# 使用 curl
curl http://localhost:8080/msg/send

# 使用浏览器
http://localhost:8080/msg/send

# 使用 JavaScript
fetch('/msg/send')
  .then(r => r.text())
  .then(data => console.log(data))
```

### 2. 获取统计信息

```http
GET /api/stats
```

**功能**: 获取 Kafka 消息统计信息

**请求参数**: 无

**响应**:
```json
200 OK
Content-Type: application/json

{
    "topic": "test1",
    "groupId": "testGroup",
    "totalMessages": 157,
    "consumerOffset": 93,
    "lag": 64,
    "bootstrapServers": "hadoop01.hdp.com:6667"
}
```

**返回字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| `topic` | String | Kafka 主题名称 |
| `groupId` | String | 消费者组 ID |
| `totalMessages` | Integer | 主题中的消息总数 |
| `consumerOffset` | Integer | 消费者当前偏移量（已消费消息数） |
| `lag` | Integer | 消息延迟（未消费的消息数） |
| `bootstrapServers` | String | Kafka 服务器地址 |

**使用示例**:
```bash
# 使用 curl
curl http://localhost:8080/api/stats

# 使用 curl 和 jq 格式化输出
curl http://localhost:8080/api/stats | jq '.'

# 使用 JavaScript
fetch('/api/stats')
  .then(r => r.json())
  .then(data => console.log(data.totalMessages))

# 提取特定字段
curl http://localhost:8080/api/stats | jq '.totalMessages, .lag'
```

## 🛠 故障排除

### 问题 1: 应用无法连接到 Kafka

**症状**:
```
Bootstrap broker hadoop01.hdp.com:6667 (id: -1 rack: null) disconnected
Connection to node -1 could not be established. Broker may not be available.
```

**解决方案**:
1. 验证 Kafka 是否已启动
   ```bash
   jps | grep Kafka
   ```

2. 检查 Kafka 服务器地址和端口
   ```bash
   # 在 application.yml 中修改
   spring.kafka.bootstrap-servers: hadoop01.hdp.com:6667
   ```

3. 测试网络连接
   ```bash
   ping hadoop01.hdp.com
   telnet hadoop01.hdp.com 6667
   ```

### 问题 2: 首页无法加载（404 错误）

**症状**: 访问 http://localhost:8080 显示 404

**解决方案**:
1. 检查应用是否启动
   ```bash
   curl -v http://localhost:8080/
   ```

2. 查看应用日志是否有错误
3. 确认端口 8080 未被占用
   ```bash
   netstat -an | grep 8080
   ```

### 问题 3: 统计信息显示错误

**症状**: API 返回错误或数据为 0

**解决方案**:
1. 确认主题已创建
   ```bash
   ./kafka-topics.sh --list --bootstrap-server hadoop01.hdp.com:6667
   ```

2. 检查消费者组是否存在
   ```bash
   ./kafka-consumer-groups.sh --list --bootstrap-server hadoop01.hdp.com:6667
   ```

3. 查看应用日志获取详细错误信息

### 问题 4: 消息未被消费

**症状**: 发送消息后，消费者偏移不增加

**解决方案**:
1. 检查 MyConsumer 监听器是否启动
2. 查看日志是否有错误输出
3. 确认消费者组 ID 与配置一致

## 📚 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.18 | Web 框架 |
| Apache Kafka | 3.1.2 | 消息队列 |
| Thymeleaf | 3.0.15 | 模板引擎 |
| Maven | 3.9.16 | 构建工具 |
| Java | 8+ | 编程语言 |
| Tomcat | 9.0.83 | Web 服务器 |

## 📦 项目依赖

```xml
<dependencies>
    <!-- Spring Boot Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Boot Kafka -->
    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka</artifactId>
    </dependency>

    <!-- Thymeleaf 模板引擎 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>

    <!-- 测试框架 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 💡 快速命令参考

```bash
# 构建项目
./mvnw clean package -DskipTests

# 启动应用
./mvnw spring-boot:run

# 运行 JAR
java -jar target/KafkaSpringBootDemo-0.0.1-SNAPSHOT.jar

# 发送测试消息
curl http://localhost:8080/msg/send

# 获取统计信息
curl http://localhost:8080/api/stats | jq

# 连续发送 5 条消息
for i in {1..5}; do curl http://localhost:8080/msg/send; sleep 1; done
```

## 🎯 项目架构

```
┌─────────────────────────────────────────────────────────┐
│                    Web Browser                          │
│              (html/css/javascript)                      │
└────────────────────┬────────────────────────────────────┘
                     │ HTTP
                     ↓
┌─────────────────────────────────────────────────────────┐
│                  Spring Boot 应用                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │ HomeController│  │Kafka Producer│ │StatsController│   │
│  └──────────────┘  └──────────────┘  └──────────────┘   │
│                          │                                │
│                    ┌─────┴─────┐                         │
│                    ↓           ↓                         │
│            ┌────────────────────────┐                   │
│            │   Kafka Template       │                   │
│            │   KafkaStatsService    │                   │
│            │   MyConsumer           │                   │
│            └────────────────────────┘                   │
└────────────────────┬────────────────────────────────────┘
                     │ TCP
                     ↓
        ┌──────────────────────────────┐
        │   Apache Kafka Cluster       │
        │  ┌──────────────────────┐    │
        │  │  Topic: test1        │    │
        │  │  Partitions: [0, 1]  │    │
        │  └──────────────────────┘    │
        │  Consumer Group: testGroup   │
        └──────────────────────────────┘
```

## 🔄 数据流向

```
发送消息流程:
用户点击按钮 → JavaScript fetch() → HomeController
→ MyKafkaController.sendMessage() → KafkaTemplate.send()
→ Kafka Topic → MyConsumer.consume() → 消费消息

获取统计流程:
JavaScript fetch() → StatsController.getStats()
→ KafkaStatsService.getKafkaStats() → AdminClient
→ 查询分区和偏移信息 → 返回 JSON
→ JavaScript 更新页面显示
```

## 📝 提交日志

最近的主要提交:
- `77dfe94` - Refactor: Separate frontend resources from HTML template
- `f79e4e6` - Add web UI and Kafka statistics monitoring
- `62912c8` - Add Kafka tutorial series to README
- `e520e05` - springboot代码

## 📞 获取帮助

如有问题:
1. 查看应用日志
2. 检查 Kafka 连接状态
3. 查看 GitHub Issues
4. 查阅 Spring Boot 和 Kafka 官方文档

## 📄 许可证

MIT License

## 👨‍💻 作者

Lansingcode

---

**项目地址**: [GitHub - KafkaSpringBootDemo](https://github.com/Lansingcode/KafkaSpringBootDemo)

**最后更新**: 2026-10-31

⭐ 如果项目对你有帮助，欢迎 Star！
