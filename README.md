# 在线课程管理系统 (Online Course Management System)

## 项目简介

本项目是一个面向教育机构的**在线课程管理系统**，旨在打通“教、学、管”全流程，实现课程发布、在线学习、作业评估、社区互动等功能的数字化管理。系统采用前后端分离架构，后端基于 Spring Boot 提供 RESTful API，前端使用 Vue 3 构建动态页面，数据库选用 MySQL 8.0。项目严格遵循软件工程规范，涵盖需求分析、数据库设计、系统实现与优化测试等完整开发周期，适用于课程实践与教学演示。

## 主要功能

### 学生端
- 注册/登录、个人信息维护
- 浏览/搜索课程、查看课程详情
- 报名/购买课程（模拟支付）
- 在线学习（视频、资料）、进度跟踪
- 提交作业/考试、查看成绩与反馈
- 讨论区发帖、回复、互动

### 教师端
- 认证申请与资料维护
- 课程创建、章节编排、资源上传（视频、课件）
- 作业/考试布置（支持多种题型）
- 批改作业、评分反馈
- 查看学生学习进度与成绩统计
- 讨论区管理（置顶、删除）

### 管理员端
- 用户管理（学生/教师账户冻结、删除）
- 教师资质审核
- 课程审核（上架/下架）
- 系统配置（注册开关、认证要求等）
- 数据统计报表（课程报名、收入、活跃度等）

### 通用功能
- RBAC 权限控制（基于角色的菜单/按钮级权限）
- JWT 无状态认证
- 操作日志审计
- 数据可视化统计图表

## 技术栈

| 层次       | 技术选型                                                                 |
|------------|--------------------------------------------------------------------------|
| 前端       | Vue 3 + Vue Router + Pinia + Element Plus + Axios + ECharts             |
| 后端       | Java 17 + Spring Boot 3.3.6 + Spring Security + JWT + Spring Data JPA   |
| 数据库     | MySQL 8.0 + HikariCP 连接池                                              |
| 构建工具   | Maven (后端) + NPM (前端)                                                |
| 接口文档   | Swagger / Knife4j (可选)                                                 |
| 开发工具   | IntelliJ IDEA + WebStorm + Git                                           |

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+ 与 NPM
- MySQL 8.0
- Maven 3.6+

### 安装步骤

1. **克隆仓库**
   ```bash
   git clone https://github.com/your-repo/online-course-management.git
   cd online-course-management
   ```

2. **后端配置与启动**
   - 创建数据库（如 `ocms_db`），执行项目中的 SQL 脚本（位于 `sql/` 目录）初始化表结构。
   - 修改后端配置文件 `src/main/resources/application.yml` 中的数据库连接信息。
   - 使用 Maven 打包并运行：
     ```bash
     cd backend
     mvn clean install
     mvn spring-boot:run
     ```
   - 后端默认运行在 `http://localhost:8080`，API 文档访问 `http://localhost:8080/swagger-ui.html`（若集成）。

3. **前端配置与启动**
   - 进入前端目录，安装依赖：
     ```bash
     cd frontend
     npm install
     ```
   - 修改接口地址（如 `.env.development` 文件中的 `VITE_API_BASE_URL`）。
   - 启动开发服务器：
     ```bash
     npm run dev
     ```
   - 前端默认运行在 `http://localhost:5173`，浏览器打开即可访问。

4. **默认账户**
   - 管理员：admin / 123456（需提前插入数据）
   - 教师：teacher / 123456
   - 学生：student / 123456

## 项目结构

```
online-course-management/
├── backend/                     # 后端源码
│   ├── src/main/java/...        # 业务代码 (controller, service, repository, entity)
│   ├── src/main/resources/      # 配置文件、Mapper XML
│   └── pom.xml                  # Maven 依赖
├── frontend/                    # 前端源码
│   ├── src/
│   │   ├── api/                 # API 请求封装
│   │   ├── views/               # 页面组件
│   │   ├── router/              # 路由配置
│   │   ├── store/               # Pinia 状态管理
│   │   └── ...
│   ├── package.json             # NPM 依赖
│   └── vite.config.js           # Vite 配置
├── sql/                         # 数据库初始化脚本
└── README.md
```

## 主要设计亮点

- **分层架构**：严格遵循 Controller-Service-Repository 分层，职责清晰。
- **设计模式应用**：状态模式（工单流转）、策略模式（派单算法）、责任链模式（JWT 过滤器链）、AOP（日志审计）。
- **数据库优化**：索引设计、事务控制、触发器审计、连接池调优。
- **安全控制**：JWT 无状态认证 + Spring Security 方法级权限控制。
- **前后端分离**：RESTful API + WebSocket 实时推送（可选）。

## 贡献指南

欢迎提交 Issue 或 Pull Request。请确保代码风格符合项目规范，并补充必要的测试用例。

## 许可证

本项目仅供学习交流使用，未经许可不得用于商业用途。

---

**项目状态**：课程设计完成，功能闭环可演示。后续可扩展真实硬件接入、消息队列、微服务化等方向。


<img width="776" height="385" alt="image" src="https://github.com/user-attachments/assets/d5245a04-6f43-44f3-9d85-67e5b101f1d8" />
<img width="801" height="497" alt="image" src="https://github.com/user-attachments/assets/acfed3bb-71d1-44d1-bbd6-25e55742e483" />
<img width="764" height="494" alt="image" src="https://github.com/user-attachments/assets/03f41237-6257-440c-af37-2d9c9768e2c3" />
<img width="692" height="397" alt="image" src="https://github.com/user-attachments/assets/69b121f5-a443-4902-a5a3-34f624910e4a" />
<img width="691" height="222" alt="image" src="https://github.com/user-attachments/assets/312ecc2a-a617-4a64-85a2-8411bfa02a8d" />
<img width="691" height="328" alt="image" src="https://github.com/user-attachments/assets/bb484a82-c07c-48ef-bc0e-27f1e03390b9" />
<img width="672" height="201" alt="image" src="https://github.com/user-attachments/assets/1c65a591-5e95-4880-b202-74173bef419c" />
<img width="679" height="305" alt="image" src="https://github.com/user-attachments/assets/49d48e80-1975-48dc-a2fe-97d1b59761e4" />
<img width="705" height="218" alt="image" src="https://github.com/user-attachments/assets/62dd48f1-53c2-4f15-a171-3ab4e36e61c2" />
<img width="742" height="431" alt="image" src="https://github.com/user-attachments/assets/49720822-98b7-4b0c-a687-aaa800d28a53" />
<img width="692" height="381" alt="image" src="https://github.com/user-attachments/assets/a985b129-78c6-4684-a280-a65c28959b76" />
<img width="692" height="307" alt="image" src="https://github.com/user-attachments/assets/7dd50153-cd6a-41ca-9ba4-b526098e4dae" />
<img width="692" height="189" alt="image" src="https://github.com/user-attachments/assets/94c8b697-2ea0-41f6-8e48-917728ab0425" />


