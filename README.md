[README.md](https://github.com/user-attachments/files/29437661/README.md)
# 防诈骗科普社区平台

> 毕业设计项目 | 三端全栈：微信小程序 + Spring Boot 后端 + Vue 3 管理后台

## 项目简介

以"降低公众防诈骗信息获取门槛"为目标，构建覆盖内容生产、AI 问答、社区互动、审核治理完整链路的防诈骗科普社区平台。

## 技术栈

| 端 | 技术 |
|------|------|
| 微信小程序 | UniApp (Vue 3) |
| 后端 REST API | Spring Boot 3.2.0 + MyBatis + MySQL |
| Web 管理后台 | Vue 3 + Element Plus + Vite |
| AI 服务 | DeepSeek API（Prompt 工程） |
| 内容安全 | 阿里云 TextModerationPlus + 本地敏感词检测 |

## 项目结构

```
├── miniapp/          # 微信小程序（UniApp + Vue 3）
│   ├── pages/        # 17+ 页面
│   ├── components/   # 公共组件
│   ├── utils/        # 工具函数、API 封装
│   └── static/       # 静态资源
├── backend/          # Spring Boot 后端
│   └── src/main/
│       ├── java/com/antifraud/   # Java 源码
│       └── resources/            # 配置文件
├── admin-web/        # Vue 3 Web 管理后台
│   └── src/
│       ├── views/    # 11 个管理模块
│       ├── api/      # API 接口封装
│       └── router/   # 路由配置
├── .env.example      # 环境变量模板
└── .gitignore
```

## 功能模块

- **防诈知识库**：分类/标签/搜索
- **AI 智能问答**：DeepSeek 接入，4 套场景 Prompt，多轮对话
- **真实案例展示**：分类浏览、详情查看
- **防诈测试题库**：自动评分
- **社区论坛**：发帖/评论/点赞/收藏/举报
- **双层内容审核**：本地敏感词（250+）+ 阿里云 AI + 人工兜底
- **正向语境豁免**：30+ 场景关键词，降低误拦截
- **每日签到**：签到统计与历史

## 项目截图

> 将你的截图放入 `screenshots/` 目录，然后替换下方路径

### 微信小程序

| 首页 | 知识库 | AI 问答 |
|------|--------|---------|
| ![](./screenshots/miniapp-home.png) | ![](./screenshots/miniapp-knowledge.png) | ![](./screenshots/miniapp-ai.png) |

| 案例展示 | 社区论坛 | 防诈测试 |
|----------|----------|----------|
| ![](./screenshots/miniapp-cases.png) | ![](./screenshots/miniapp-community.png) | ![](./screenshots/miniapp-test.png) |

### Web 管理后台

| 数据面板 | 内容管理 | 用户管理 |
|----------|----------|----------|
| ![](./screenshots/admin-dashboard.png) | ![](./screenshots/admin-content.png) | ![](./screenshots/admin-users.png) |

| 审核管理 | AI 问答记录 | 系统设置 |
|----------|------------|----------|
| ![](./screenshots/admin-review.png) | ![](./screenshots/admin-ai-logs.png) | ![](./screenshots/admin-settings.png) |

## 快速开始

### 1. 配置环境变量

```bash
cp .env.example .env
# 编辑 .env 填入真实的 API Key 和数据库密码
```

### 2. 启动后端

```bash
cd backend
./mvnw spring-boot:run
```

### 3. 启动管理后台

```bash
cd admin-web
npm install
npm run dev
```

### 4. 启动小程序

使用 HBuilder X 打开 `miniapp/` 目录，运行到微信开发者工具。

## 许可证

本项目仅用于学习与求职展示。
