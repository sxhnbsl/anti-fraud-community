# 防诈骗科普交流平台 API 文档

## 基础信息

- **基础URL**: `http://localhost:8080`
- **数据格式**: JSON
- **字符编码**: UTF-8

---

## 1. 用户认证模块

### 1.1 用户注册

**接口地址**: `POST /api/auth/register`

**请求参数**:
```json
{
  "username": "用户名",
  "password": "密码",
  "nickname": "昵称",
  "phone": "手机号",
  "email": "邮箱"
}
```

**返回示例**:
```json
{
  "message": "注册成功",
  "user": {
    "id": 1,
    "username": "test",
    "nickname": "测试用户",
    "phone": "13800138000",
    "email": "test@example.com",
    "role": "ROLE_USER"
  }
}
```

### 1.2 用户登录

**接口地址**: `POST /api/auth/login`

**请求参数**:
```json
{
  "username": "用户名",
  "password": "密码"
}
```

**返回示例**:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "user": {
    "id": 1,
    "username": "test",
    "nickname": "测试用户"
  }
}
```

---

## 2. 防诈骗案例管理模块

### 2.1 创建案例

**接口地址**: `POST /api/cases`

**请求参数**:
```json
{
  "title": "案例标题",
  "content": "案例内容",
  "category": "分类",
  "fraudType": "诈骗类型",
  "victimInfo": "受害者信息",
  "warningTips": "防范提示",
  "authorId": 1
}
```

**返回示例**:
```json
{
  "message": "创建成功",
  "data": {
    "id": 1,
    "title": "网络刷单诈骗案例",
    "content": "案例详情...",
    "category": "网络诈骗",
    "fraudType": "刷单诈骗"
  }
}
```

### 2.2 获取所有案例

**接口地址**: `GET /api/cases`

**返回示例**:
```json
{
  "data": [
    {
      "id": 1,
      "title": "网络刷单诈骗案例",
      "content": "案例详情...",
      "viewCount": 100,
      "likeCount": 50
    }
  ]
}
```

### 2.3 获取单个案例

**接口地址**: `GET /api/cases/{id}`

**返回示例**:
```json
{
  "data": {
    "id": 1,
    "title": "网络刷单诈骗案例",
    "content": "案例详情...",
    "viewCount": 101,
    "likeCount": 50
  }
}
```

### 2.4 按分类获取案例

**接口地址**: `GET /api/cases/category/{category}`

**参数说明**: category 需要URL编码

### 2.5 点赞案例

**接口地址**: `POST /api/cases/{id}/like`

**返回示例**:
```json
{
  "message": "点赞成功"
}
```

### 2.6 删除案例

**接口地址**: `DELETE /api/cases/{id}`

---

## 3. 社区帖子模块

### 3.1 创建帖子

**接口地址**: `POST /api/posts`

**请求参数**:
```json
{
  "title": "帖子标题",
  "content": "帖子内容",
  "authorId": 1
}
```

### 3.2 获取所有帖子

**接口地址**: `GET /api/posts`

### 3.3 获取单个帖子

**接口地址**: `GET /api/posts/{id}`

### 3.4 获取用户帖子

**接口地址**: `GET /api/posts/author/{authorId}`

### 3.5 点赞帖子

**接口地址**: `POST /api/posts/{id}/like`

### 3.6 删除帖子

**接口地址**: `DELETE /api/posts/{id}`

---

## 4. 评论模块

### 4.1 创建评论

**接口地址**: `POST /api/comments`

**请求参数**:
```json
{
  "postId": 1,
  "authorId": 1,
  "content": "评论内容",
  "parentId": null
}
```

### 4.2 获取帖子评论

**接口地址**: `GET /api/comments/post/{postId}`

### 4.3 点赞评论

**接口地址**: `POST /api/comments/{id}/like`

### 4.4 删除评论

**接口地址**: `DELETE /api/comments/{id}`

---

## 5. 防诈骗知识模块

### 5.1 创建知识

**接口地址**: `POST /api/knowledge`

**请求参数**:
```json
{
  "title": "知识标题",
  "content": "知识内容",
  "category": "分类",
  "tags": "标签1,标签2",
  "authorId": 1
}
```

### 5.2 获取所有知识

**接口地址**: `GET /api/knowledge`

### 5.3 获取单个知识

**接口地址**: `GET /api/knowledge/{id}`

### 5.4 按分类获取知识

**接口地址**: `GET /api/knowledge/category/{category}`

### 5.5 按标签获取知识

**接口地址**: `GET /api/knowledge/tag/{tag}`

### 5.6 点赞知识

**接口地址**: `POST /api/knowledge/{id}/like`

### 5.7 删除知识

**接口地址**: `DELETE /api/knowledge/{id}`

---

## 6. 举报模块

### 6.1 创建举报

**接口地址**: `POST /api/reports`

**请求参数**:
```json
{
  "reporterId": 1,
  "reportedType": "post",
  "reportedId": 1,
  "reason": "举报原因",
  "description": "详细描述"
}
```

### 6.2 获取所有举报

**接口地址**: `GET /api/reports`

### 6.3 获取单个举报

**接口地址**: `GET /api/reports/{id}`

### 6.4 获取用户举报

**接口地址**: `GET /api/reports/reporter/{reporterId}`

### 6.5 按状态获取举报

**接口地址**: `GET /api/reports/status/{status}`

**状态值**: pending（待处理）、processed（已处理）、rejected（已拒绝）

### 6.6 更新举报状态

**接口地址**: `PUT /api/reports/{id}/status`

**请求参数**:
```json
{
  "status": "processed"
}
```

### 6.7 删除举报

**接口地址**: `DELETE /api/reports/{id}`

---

## 错误码说明

- **200**: 请求成功
- **400**: 请求参数错误
- **403**: 权限不足
- **404**: 资源不存在
- **500**: 服务器内部错误

---

## 注意事项

1. 所有日期时间格式为：`yyyy-MM-dd HH:mm:ss`
2. 中文参数需要URL编码
3. 分页功能待实现
4. 文件上传功能待实现