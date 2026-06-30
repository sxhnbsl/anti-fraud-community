# 反诈骗平台 Web管理后台

基于 Vue 3 + Element Plus + Vite 开发的管理后台系统

## 技术栈

- **前端框架**: Vue 3
- **UI组件库**: Element Plus
- **构建工具**: Vite
- **路由管理**: Vue Router
- **状态管理**: Pinia
- **HTTP客户端**: Axios

## 项目结构

```
3-web-admin/
├── src/
│   ├── api/              # API接口
│   │   ├── user.js       # 用户相关接口
│   │   ├── knowledge.js  # 知识库接口
│   │   ├── case.js       # 案例接口
│   │   ├── post.js       # 帖子接口
│   │   ├── comment.js    # 评论接口
│   │   └── report.js     # 举报接口
│   ├── views/            # 页面组件
│   │   ├── Login.vue     # 登录页
│   │   ├── Dashboard.vue # 首页
│   │   ├── user/         # 用户管理
│   │   ├── knowledge/    # 知识库管理
│   │   ├── case/         # 案例管理
│   │   ├── post/         # 帖子管理
│   │   ├── comment/      # 评论管理
│   │   └── report/       # 举报管理
│   ├── layout/           # 布局组件
│   ├── router/           # 路由配置
│   ├── utils/            # 工具函数
│   ├── App.vue           # 根组件
│   └── main.js           # 入口文件
├── index.html            # HTML模板
├── package.json          # 项目配置
└── vite.config.js        # Vite配置
```

## 功能模块

### 1. 用户管理
- 查看全部用户
- 审核新注册用户
- 删除用户
- 用户状态管理

### 2. 知识库管理
- 查看知识列表
- 添加知识
- 编辑知识
- 删除知识
- 支持图片上传

### 3. 案例管理
- 查看案例列表
- 添加案例
- 编辑案例
- 删除案例
- 支持图片上传

### 4. 帖子管理
- 查看帖子列表
- 审核帖子
- 删除帖子
- 按审核状态筛选

### 5. 评论管理
- 查看评论列表
- 审核评论
- 删除评论
- 按审核状态筛选

### 6. 举报管理
- 查看举报列表
- 查看举报详情
- 处理举报
- 按处理状态筛选

## 快速开始

### 1. 安装依赖

```bash
cd 3-web-admin
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

访问 http://localhost:3000

### 3. 构建生产版本

```bash
npm run build
```

### 4. 预览生产版本

```bash
npm run preview
```

## 开发说明

### VS Code推荐插件

- Volar - Vue 3语法支持
- ESLint - 代码检查
- Prettier - 代码格式化

### 后端API地址

开发环境代理配置在 `vite.config.js` 中：

```javascript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

确保后端服务运行在 http://localhost:8080

### 登录功能

登录接口：`POST /api/auth/login`

请求参数：
```json
{
  "username": "admin",
  "password": "password"
}
```

## 注意事项

1. 确保后端服务已启动
2. 确保数据库已配置
3. 首次登录需要管理员账号
4. API接口需要根据实际后端接口调整

## 项目特色

- 响应式设计，适配不同屏幕
- 完整的权限控制
- 优雅的UI界面
- 丰富的交互体验
- 模块化代码结构

## 后续优化

- 添加文件上传功能
- 添加数据统计图表
- 添加操作日志
- 添加批量操作
- 添加数据导出功能
