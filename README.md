﻿# 用户管理中心

## 简介
用户管理中心是一个使用SpringBoot和MyBatis-Plus构建的Web后端应用程序。
它提供了全面的用户管理系统， 可用作构建需要用户身份验证和授权的应用程序的基础。
可在此程序基础上，扩展其他程序。

## 版本
v1.0

## 技术栈
- SpringBoot
- MyBatis-Plus
- Aop
- knife4j
- fastjson
- ...

## 功能
用户管理中心包括以下功能：

- 登陆相关
  - 登录
  - 注册
  - 登录用户获取
  - 退出
- CURD
  - 添加用户
  - 删除用户
  - 更新用户
  - 列表查询用户
  - 页表查询用户

## 入门指南
### 使用以下命令克隆存储库：
```
git clone https://github.com/your_username/user-management-center.git
```

### 配置数据库
首先将SQL目录下的sql文件运行，在本地环境中创建数据库。   
通过在src/main/resources目录中的application.yml文件中配置数据库连接详细信息来设置数据库。
```yml
port: 访问端口（默认8088）

username: 数据库用户名(root)
password: 数据库密码(123456)
url: 数据库地址(jdbc:mysql://localhost:3306/user-center)
driver-class-name: 数据库驱动(com.mysql.cj.jdbc.Driver)
```

### 访问 
本项目为web后端程序，并无前端界面，但用户可通过swagger或postman访问测试api接口

#### swagger
打开Web浏览器并导航到 `http://localhost:8088/api/doc.html` 访问应用程序。
#### postman
使用postman访问访问接口
- (用户注册) http://localhost:8088/api/user/register
- (用户登录) http://localhost:8088/api/user/login
- (登陆用户获取) http://localhost:8088/api/current
- ...

## 贡献
如果您想要为用户管理中心做出贡献，请遵循以下步骤：

### Fork存储库。
创建您的特性分支（git checkout -b my-new-feature）。  
提交您的更改（git commit -am 'Add some feature'）。  
将您的分支推送到远程存储库（git push origin my-new-feature）。  
创建一个新的Pull Request。
