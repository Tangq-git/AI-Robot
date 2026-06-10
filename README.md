# 🤖 AI 聊天机器人 & 智能客服

一款基于 Spring AI 框架构建的智能对话平台，包含通用聊天机器人、基于私有知识库的智能客服两大 AI 应用。

---

## ✨ 项目亮点
- 🧠 支持长上下文对话记忆与持久化，对话体验连贯自然
- 🌐 联网实时搜索能力，让大模型也能获取最新信息
- 📚 私有知识库问答，支持 RAG 检索增强生成
- ⚡ 基于 SSE 的流式对话响应，打字机效果丝滑流畅
- 📦 前后端分离架构，前后端代码完全隔离，便于协作开发与部署

---

## 🛠️ 技术栈

### 后端
- **核心框架**: Spring Boot 3.x, JDK 21
- **AI 能力**: Spring AI, DeepSeek
- **数据存储**: PostgreSQL（业务数据 + 向量存储）
- **中间件/工具**: Docker, MyBatis Plus, RAG, SSE, SearXNG, OkHttp3, Jsoup
- **设计模式**: 自定义 Advisor、Spring Event 发布/订阅、线程池优化

### 前端
- **核心框架**: Vue 3.x + Vite 4
- **UI 组件库**: Ant Design Vue + Tailwind CSS
- **状态管理**: Pinia
- **网络通信**: Axios, EventSource / fetch-event-source
- **核心能力**: 流式 Markdown 渲染、自定义聊天组件、大文件分片上传

---

## 🚀 功能模块

### 1. 聊天记忆与持久化
- 集成 PostgreSQL 作为业务与向量存储数据库，实现会话级长上下文记忆
- 自定义 Advisor 组件，在每次模型调用前后自动注入历史对话
- 消息持久化存储，完整收集流式与非流式响应，为后续分析与审计提供可靠数据

### 2. 联网实时搜索
- 基于 Docker 自部署 SearXNG 聚合搜索引擎
- 后端利用 OkHttp3 并发请求，结合 CompletableFuture 与自定义线程池优化搜索速度
- 使用 Jsoup 清洗网页正文，有效降低外部信息 Token 消耗超 60%
- 让大模型具备实时、准确的信息获取与回答能力

### 3. RAG 检索增强（智能客服）
- 自定义 RAG Advisor 组件，检索私有知识库的向量数据
- 动态构建增强提示词模板，实现“按指定问答语料进行对话”的智能客服能力

### 4. 提示词工程
- 针对联网搜索、客服问答等多场景，编写并优化角色设定、规则约束与上下文管理策略
- 显著提升大模型在复杂任务中的准确性、可靠性与输出一致性

### 5. 后端业务开发
- 基于 Spring Boot 3.x 搭建完整后端服务体系，涵盖对话管理、消息分页、知识库 Markdown 文件管理
- 运用 Spring Event 发布/订阅模式，解耦文件上传后的解析、向量化等异步处理流程
- 提升系统响应速度与代码清晰度，便于维护与扩展

### 6. 前端应用开发
- 使用 Vue 3 与 Vite 构建单页应用，基于 Ant Design Vue 与 Tailwind CSS 打造风格统一的聊天界面与管理页
- 采用 SSE 协议对接后端流式接口，封装可实时渲染的流式 Markdown、聊天对话框、侧边栏等自定义组件
- 使用 Pinia 管理全局模型选择、搜索开关等状态，实现聊天记录与对话列表的滚动分页加载

### 7. 大文件上传
- 实现大文件分片上传、断点续传与秒传功能
- 前端 MD5 计算 + 后端接口设计 + 并发分批上传 + 失败重试优化
- 显著提升文件上传的稳定性与效率

---

## 📂 项目结构
ai-robot/
├── backend/ # Spring Boot 后端项目
├── frontend/ # Vue 3 前端项目
└── .gitignore


---

## 🚀 快速启动

### 后端启动
1. 配置 `application.yml` 中的数据库、API Key 等信息
2. 启动 PostgreSQL 服务
3. 运行 `AiRobotApplication.java`

### 前端启动
```bash
cd frontend
npm install
npm run dev
