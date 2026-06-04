# MiniMybatis

一个简化版的 MyBatis ORM 框架实现，用于学习和理解 MyBatis 的核心原理与设计思想。

## 项目简介

MiniMybatis 是一个手写的最小化 MyBatis 实现，涵盖了 MyBatis 的核心功能模块，包括配置解析、SQL 执行、参数处理、结果映射、Mapper 接口代理等。通过阅读和运行本项目，可以深入理解 MyBatis 的工作原理。

## 核心特性

- **XML 配置解析**：支持 `sqlMapConfig.xml` 主配置文件和 `Mapper.xml` 映射文件的解析
- **SqlSession 管理**：提供 `SqlSessionFactory` 和 `SqlSession` 会话管理
- **Executor 执行器**：实现 SQL 执行的核心逻辑
- **Mapper 接口代理**：通过 JDK 动态代理生成 Mapper 接口的实现类
- **参数处理**：支持 `#{param}` 预编译参数和 `${param}` 字符串替换
- **结果集映射**：自动将查询结果映射为 Java 对象
- **SQL 源构建**：支持动态 SQL 解析和 `BoundSql` 构建

## 项目结构

```
src/main/java/com/mini/batis/
├── binding/          # Mapper 接口绑定与代理
│   ├── MapperProxy.java         # Mapper 代理类
│   ├── MapperProxyFactory.java  # 代理工厂
│   ├── MapperMethod.java        # Mapper 方法调用
│   ├── MethodSignature.java     # 方法签名
│   └── SqlCommand.java          # SQL 命令
├── core/             # 配置解析核心
│   ├── XMLConfigBuilder.java    # 主配置解析器
│   └── XMLMapperBuilder.java    # Mapper 配置解析器
├── entity/           # 实体类
│   ├── User.java
│   └── UserQuery.java
├── executor/         # SQL 执行器
│   ├── Executor.java            # 执行器接口
│   ├── SimpleExecutor.java      # 简单执行器
│   ├── ParameterHandler.java    # 参数处理器接口
│   ├── DefaultParameterHandler.java # 默认参数处理器
│   ├── ResultSetHandler.java    # 结果集处理器接口
│   ├── DefaultResultSetHandler.java # 默认结果集处理器
│   ├── StatementHandler.java    # Statement 处理器接口
│   └── SimpleStatementHandler.java # 简单 Statement 处理器
├── mapper/           # Mapper 接口
│   └── UserMapper.java
├── model/            # 核心模型
│   ├── Configuration.java       # 配置类
│   └── MapperStatement.java     # Mapper 语句信息
├── objects/          # 对象模块
├── reflection/       # 反射工具
│   └── ParamValueResolver.java  # 参数值解析器
├── scripting/        # SQL 脚本解析
│   ├── SqlSource.java           # SQL 源接口
│   ├── SqlSourceBuilder.java    # SQL 源构建器
│   ├── BoundSql.java            # 绑定 SQL
│   └── ParameterMapping.java    # 参数映射
└── session/          # 会话管理
    ├── SqlSession.java          # SqlSession 接口
    ├── DefaultSqlSession.java   # 默认 SqlSession 实现
    ├── SqlSessionFactory.java   # SqlSessionFactory 接口
    ├── DefaultSqlSessionFactory.java # 默认工厂实现
    └── SqlSessionFactoryBuilder.java # 工厂构建器
```

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| JDK | 1.8 | Java 开发环境 |
| MySQL Connector | 8.0.33 | MySQL 数据库连接驱动 |
| dom4j | 2.1.4 | XML 解析库 |
| Lombok | 1.18.36 | 简化 Java 代码注解工具 |
| Commons DBCP | 1.4 | 数据库连接池 |
| JUnit | 4.13.2 | 单元测试框架 |

## 快速开始

### 1. 环境准备

- JDK 1.8+
- MySQL 8.0+
- Maven 3.x

### 2. 数据库初始化

执行 `src/main/resources/init.sql` 脚本初始化数据库和测试数据：

```sql
CREATE DATABASE IF NOT EXISTS test_db;
USE test_db;

CREATE TABLE user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100)
);

INSERT INTO user (username, password, email) VALUES 
('john', 'password123', 'john@example.com'),
('jane', 'password456', 'jane@example.com'),
('bob', 'password789', 'bob@example.com');
```

### 3. 配置数据库连接

修改 `src/main/resources/sqlMapConfig.xml` 中的数据库连接信息：

```xml
<property name="url" value="jdbc:mysql://localhost:3306/test_db?characterEncoding=utf-8"/>
<property name="username" value="your_username"/>
<property name="password" value="your_password"/>
```

### 4. 编译运行

```bash
mvn clean compile
```

## 使用示例

### 1. 获取 SqlSession

```java
InputStream inputStream = Resources.getResourceAsStream("sqlMapConfig.xml");
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
SqlSession sqlSession = sqlSessionFactory.openSession();
```

### 2. 使用 SqlSession 执行 SQL

```java
// 查询所有用户
List<User> users = sqlSession.selectList("com.mini.batis.mapper.UserMapper.findAll", null);

// 根据 ID 查询用户
User user = sqlSession.selectOne("com.mini.batis.mapper.UserMapper.findById", 1);

// 插入用户
User newUser = new User();
newUser.setUsername("test");
newUser.setPassword("123456");
newUser.setEmail("test@example.com");
int rows = sqlSession.insert("com.mini.batis.mapper.UserMapper.insertUser", newUser);
```

### 3. 使用 Mapper 接口代理

```java
// 获取 Mapper 代理对象
UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

// 调用 Mapper 方法
List<User> users = userMapper.findAll();
User user = userMapper.findById(1);
int rows = userMapper.insertUser(newUser);
```

### 4. Mapper XML 配置示例

```xml
<mapper namespace="com.mini.batis.mapper.UserMapper">
    <select id="findAll" resultType="com.mini.batis.entity.User">
        SELECT id, username, password, email FROM user
    </select>

    <select id="findById" resultType="com.mini.batis.entity.User" statementType="preparedStatement">
        SELECT id, username, password, email FROM user WHERE id = #{id}
    </select>

    <insert id="insertUser" parameterType="com.mini.batis.entity.User">
        INSERT INTO user (username, password, email)
        VALUES (#{username}, #{password}, #{email})
    </insert>
</mapper>
```

## 核心流程

```
SqlSessionFactoryBuilder.build()
         │
         ▼
SqlSessionFactory (解析配置，创建工厂)
         │
         ▼
SqlSession (创建会话)
         │
    ┌────┴────┐
    ▼         ▼
selectList  getMapper (获取Mapper代理)
    │         │
    ▼         ▼
Executor   MapperProxy
    │         │
    ▼         ▼
StatementHandler.invoke()
    │
    ▼
ParameterHandler (参数处理)
    │
    ▼
ResultSetHandler (结果映射)
```

## 学习要点

通过本项目可以学习到：

1. **ORM 框架设计思想**：如何将数据表映射为 Java 对象
2. **XML 解析技术**：使用 dom4j 解析配置文件
3. **动态代理模式**：JDK 动态代理实现 Mapper 接口绑定
4. **工厂模式**：SqlSessionFactory 的创建和使用
5. **建造者模式**：SqlSessionFactoryBuilder 的构建过程
6. **责任链模式**：Executor、StatementHandler、ParameterHandler、ResultSetHandler 的执行链
7. **反射机制**：通过反射设置参数和获取结果
8. **数据库连接池**：DBCP 连接池的使用

## 许可证

本项目为学习用途，仅供个人学习和研究使用。

## 作者

guyan

## 参考项目

- [MyBatis](https://github.com/mybatis/mybatis-3) - 本项目的主要参考对象
