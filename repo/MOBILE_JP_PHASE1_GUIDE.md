# 移动端一期改动说明

## 1. 这次直接修改了哪些文件

### 入口与全局配置
- [index.html](/G:/AdressMap/repo/index.html)
  - 应用网页标题改成 `販売店営業アプリ`
- [manifest.json](/G:/AdressMap/repo/manifest.json)
  - 应用名称、H5 标题改成日文项目名称
- [pages.json](/G:/AdressMap/repo/pages.json)
  - 页面路由标题改成日文
  - 新增了销售店详情、拜访记录新增页面路由
  - 底部 tab 改成 `ホーム / 販売店 / マイページ`
- [config.js](/G:/AdressMap/repo/config.js)
  - 去掉若依相关配置
  - 改成当前移动端项目名称、协议名称、站点占位信息

### 新增的业务 API 文件
- [api/system/outlet.js](/G:/AdressMap/repo/api/system/outlet.js)
  - 销售店相关接口
- [api/system/history.js](/G:/AdressMap/repo/api/system/history.js)
  - 拜访记录相关接口
- [api/system/monthlySales.js](/G:/AdressMap/repo/api/system/monthlySales.js)
  - 月次销售台数相关接口

### 新增的公共工具
- [utils/outlet.js](/G:/AdressMap/repo/utils/outlet.js)
  - 商流判断
  - 最新拜访记录提取
  - 最新月次销售提取
  - 数字、日期、备注格式化
  - 拜访记录提交时的 payload 组装

### 新增页面
- [pages/outlet/detail.vue](/G:/AdressMap/repo/pages/outlet/detail.vue)
  - 销售店详情页
  - 展示基本信息、最近拜访、月次销售台数
- [pages/history/create.vue](/G:/AdressMap/repo/pages/history/create.vue)
  - 新增拜访记录页

### 重做/大改页面
- [pages/index.vue](/G:/AdressMap/repo/pages/index.vue)
  - 从默认首页改成移动端首页看板
- [pages/work/index.vue](/G:/AdressMap/repo/pages/work/index.vue)
  - 从原来的示例工作台改成销售店列表页
- [pages/login.vue](/G:/AdressMap/repo/pages/login.vue)
  - 登录页日文化，去掉若依品牌
- [pages/register.vue](/G:/AdressMap/repo/pages/register.vue)
  - 注册页日文化，去掉若依品牌
- [pages/mine/index.vue](/G:/AdressMap/repo/pages/mine/index.vue)
  - 我的页面改成和当前销售店业务更贴近的入口
- [pages/mine/help/index.vue](/G:/AdressMap/repo/pages/mine/help/index.vue)
  - FAQ 改成当前业务说明
- [pages/mine/about/index.vue](/G:/AdressMap/repo/pages/mine/about/index.vue)
  - 关于页改成当前项目信息
- [pages/mine/info/index.vue](/G:/AdressMap/repo/pages/mine/info/index.vue)
  - 个人资料页日文化
- [pages/mine/info/edit.vue](/G:/AdressMap/repo/pages/mine/info/edit.vue)
  - 编辑资料页日文化
- [pages/mine/pwd/index.vue](/G:/AdressMap/repo/pages/mine/pwd/index.vue)
  - 修改密码页日文化
- [pages/mine/setting/index.vue](/G:/AdressMap/repo/pages/mine/setting/index.vue)
  - 设置页日文化

## 2. 现在移动端一期主要做成了什么

### 首页
- 看销售店总数
- 看最近 30 天拜访情况
- 看直近月次销售台数汇总
- 看最近更新的销售店

### 销售店列表页
- 关键字搜索
- 按地区筛选
- 看每个销售店的主商流
- 看最近月次销售
- 看最近拜访记录摘要

### 销售店详情页
- 看基本信息
- 看商流
- 看最近一条拜访
- 看最近月次销售
- 看拜访记录列表
- 看月次销售列表

### 拜访记录新增页
- 输入备注
- 自动按当前用户角色组装商流相关提交数据

### 我的页面相关
- 登录/注册
- 个人资料查看与修改
- 修改密码
- 设置
- 帮助
- 关于

## 3. 这次用到了哪些组件

### 原生/基础组件
- `view`
- `text`
- `image`
- `input`
- `textarea`
- `button`
- `scroll-view`
- `swiper`
- `swiper-item`
- `web-view`

### uni-app / uni-ui 组件
- `uni-list`
- `uni-list-item`
- `uni-forms`
- `uni-forms-item`
- `uni-easyinput`
- `uni-data-checkbox`
- `uni-title`
- `uni-link`
- `uni-card`

## 4. 页面和组件对应关系

### 登录/注册
- 主要用了：
  - `view`
  - `image`
  - `input`
  - `button`

### 首页看板
- 主要用了：
  - `scroll-view`
  - `view`
  - `text`

### 销售店列表
- 主要用了：
  - `scroll-view`
  - `input`
  - `view`
  - `text`

### 销售店详情
- 主要用了：
  - `scroll-view`
  - `view`
  - `text`

### 拜访记录新增
- 主要用了：
  - `view`
  - `textarea`
  - `button`

### 我的/帮助/关于/资料/设置
- 主要用了：
  - `uni-list`
  - `uni-list-item`
  - `uni-forms`
  - `uni-forms-item`
  - `uni-easyinput`
  - `uni-data-checkbox`
  - `uni-title`
  - `uni-link`
  - `uni-card`

## 5. 一句话理解这次改动

这次不是“小修小补”，而是把 `repo` 这个原本偏若依通用模板的 uniapp，先改造成了一个“能围绕销售店、拜访记录、月次销售数据运行”的移动端一期版本，同时把主要可见页面统一成了日文。
