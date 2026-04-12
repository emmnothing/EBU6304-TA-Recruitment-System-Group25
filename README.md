# TA Recruitment System

TA Recruitment System is a role-based teaching assistant recruitment web application built with Jakarta Servlet, JSP, and Maven. The system supports three user roles:

- `TA_APPLICANT`: register, maintain a profile, upload a CV, browse jobs, apply for roles, track application status, and view notifications
- `MODULE_ORGANISER`: publish TA jobs, manage applicants, review applications, update decisions, and view role-specific notifications
- `ADMINISTRATOR`: view overall system metrics, workload summaries, recent job posts, and recent applications

All business data is stored in local JSON files under `storage/json`, which makes the project easy to inspect and demo without configuring a database.

## Project Structure Analysis

The current project follows a clear layered architecture around `Servlet -> Service -> Repository -> JSON storage`:

- `src/main/java/com/bupt/ta/model`: core domain objects and enums such as `User`, `JobPost`, `JobApplication`, `Notification`, `Role`, `JobStatus`, and `ApplicationStatus`
- `src/main/java/com/bupt/ta/dto`: form objects and view-oriented data transfer objects used between servlet and service layers
- `src/main/java/com/bupt/ta/repository`: file-based repositories responsible for reading and writing JSON data
- `src/main/java/com/bupt/ta/service`: business logic for authentication, profile management, job posting, application handling, notifications, dashboards, admin overview, and account deletion
- `src/main/java/com/bupt/ta/servlet`: controller layer grouped by route domain and role
- `src/main/webapp/WEB-INF/views`: JSP pages for each feature page
- `src/main/webapp/assets`: shared CSS and JavaScript assets
- `storage/json`: application data files used as persistent storage
- `docs`: architecture notes, naming conventions, and page-level documentation
- `background`: backlog, prototype materials, survey results, and user stories

Top-level structure:

```text
.
|-- background/
|-- docs/
|-- src/
|   `-- main/
|       |-- java/com/bupt/ta/
|       |   |-- dto/
|       |   |-- model/
|       |   |-- repository/
|       |   |-- service/
|       |   |-- servlet/
|       |   `-- util/
|       `-- webapp/
|           |-- assets/
|           |-- WEB-INF/views/
|           `-- index.jsp
|-- storage/
|   `-- json/
|-- pom.xml
`-- README.md
```

## Core Modules

### Authentication and Session

- Entry page: `src/main/webapp/index.jsp`
- Default redirect: `/auth/login`
- Authentication is handled by `AuthService`
- Session state is managed with `HttpSession`
- Session keys:
  - `currentUserId`
  - `currentUsername`
  - `currentUserRole`

### Applicant Module

Main routes:

- `/applicant/dashboard`
- `/applicant/profile`
- `/applicant/jobs`
- `/applicant/status`
- `/applicant/notifications`
- `/cv/download`
- `/account/delete`

Key behavior:

- Applicants must complete their profile before applying
- CV upload is required before job application
- Supported CV formats are `PDF`, `DOC`, and `DOCX`
- Maximum CV size is `5 MB`
- An applicant cannot apply to the same job twice
- Closed jobs or fully filled jobs cannot receive new applications

### Module Organiser Module

Main routes:

- `/mo/dashboard`
- `/mo/post-job`
- `/mo/post-job/close`
- `/mo/applicants`
- `/mo/applicants/decision`
- `/mo/notifications`

Key behavior:

- Module organisers can create TA job posts
- Job posts can close automatically when the deadline passes
- Job posts can close automatically when vacancies are filled
- Job posts can also be closed manually
- Application decisions supported by the current code are `UNDER_REVIEW`, `SELECTED`, and `REJECTED`
- Decision updates generate applicant notifications automatically

### Administrator Module

Main route:

- `/admin/dashboard`

Key behavior:

- Shows total applicants, jobs, and applications
- Shows open-job and pending-review counts
- Generates applicant workload summaries from selected applications
- Lists recent jobs and recent application activity
- Surfaces storage file locations used by the system

## Main Request Flow

The request lifecycle in the current implementation is:

1. Browser sends a request to a servlet route such as `/auth/login` or `/applicant/jobs`
2. Servlet validates session and request parameters
3. Servlet calls the corresponding service class
4. Service applies business rules and reads or writes JSON through repositories
5. Servlet forwards to JSP or redirects to the next route

## Persistence and Storage

Current data files:

- `storage/json/users.json`: account records and user roles
- `storage/json/applicantProfiles.json`: applicant profile data and CV metadata
- `storage/json/jobs.json`: TA job posts
- `storage/json/applications.json`: submitted applications and decisions
- `storage/json/notifications.json`: user notifications

Runtime upload directory:

- `storage/uploads/cv/`: uploaded CV files

Storage root behavior:

- By default, storage resolves to `storage/` under the project root
- You can override the storage root with the system property `ta.storage.root`

Example:

```powershell
mvn clean package -Dta.storage.root=D:\data\ta-recruitment-storage
```

## Demo Accounts

The repository already contains demo users in `storage/json/users.json`:

| Role | Username | Password |
| --- | --- | --- |
| `TA_APPLICANT` | `applicant1` | `Password123` |
| `MODULE_ORGANISER` | `mo1` | `Password123` |
| `ADMINISTRATOR` | `admin1` | `Password123` |

## Tech Stack

- Java web application packaged as `WAR`
- Maven build
- Jakarta Servlet API `6.0.0`
- Jakarta JSP API `3.1.1`
- Gson `2.11.0`
- JSP + server-side rendering
- JSON file persistence instead of a relational database

## Build and Run

### Environment Requirements

Based on the current `pom.xml`, the project is configured with:

- `packaging`: `war`
- `maven.compiler.release`: `25`

That means the most direct build path is:

- JDK `25`
- Maven `3.9+`
- A Jakarta-compatible servlet container for deployment

Because the project uses `jakarta.servlet-api:6.0.0`, a `Tomcat 10.1+` style deployment target is the safest assumption.

### Build Steps

1. Install a JDK version compatible with the current Maven compiler setting in `pom.xml`
2. Install Maven because this repository does not currently include `mvnw` or `mvnw.cmd`
3. Build the project:

```powershell
mvn clean package
```

4. After a successful build, deploy:

```text
target/ta-recruitment-system.war
```

5. Start your servlet container and open:

```text
http://localhost:8080/ta-recruitment-system/
```

The root page redirects to `/auth/login`.

### If You Are Using JDK 21

In the current local environment, `java -version` reports `21.0.8`, while `pom.xml` is configured for release `25`. If your team standardizes on JDK 21, update the following field before building:

```xml
<maven.compiler.release>21</maven.compiler.release>
```

Only do this if the whole team agrees on the target Java version.

## Key Routes

| Route | Purpose | Role |
| --- | --- | --- |
| `/auth/login` | Login page and authentication | Public |
| `/auth/register` | New account registration | Public |
| `/auth/forgot` | Password reset by email and phone | Public |
| `/applicant/dashboard` | Applicant dashboard | `TA_APPLICANT` |
| `/applicant/profile` | Edit applicant profile and upload CV | `TA_APPLICANT` |
| `/applicant/jobs` | Browse and apply for TA jobs | `TA_APPLICANT` |
| `/applicant/status` | Track application status | `TA_APPLICANT` |
| `/mo/dashboard` | Module organiser dashboard | `MODULE_ORGANISER` |
| `/mo/post-job` | Create and manage TA job posts | `MODULE_ORGANISER` |
| `/mo/applicants` | Review applicants and decisions | `MODULE_ORGANISER` |
| `/admin/dashboard` | System overview | `ADMINISTRATOR` |
| `/cv/download` | Download uploaded CV files | Logged-in users with access |
| `/account/delete` | Delete current account and related data | Logged-in users |

## Current Documentation and Background Material

- `docs/architecture.md`: architecture overview and storage notes
- `docs/naming-conventions.md`: naming and route conventions
- `docs/pages/`: per-page documentation
- `background/prototype/`: static HTML prototypes and prototype PDF
- `background/user_story/`: user story drafts
- `background/backlog/`: backlog and survey material

## Known Notes

- The project currently uses file-based JSON persistence, so concurrent edits or multi-instance deployment are not the main target scenario
- There is no Maven wrapper in the repository at the moment
- No automated test suite is present in the current codebase
- Upload directories are created automatically through `StoragePathUtil`

---

# TA Recruitment System 中文说明

TA Recruitment System 是一个面向助教招聘场景的角色化 Web 应用，基于 Jakarta Servlet、JSP 和 Maven 构建。系统当前支持三类用户角色：

- `TA_APPLICANT`：注册账号、维护个人资料、上传简历、浏览岗位、投递申请、查看申请进度和通知
- `MODULE_ORGANISER`：发布助教岗位、筛选申请人、更新申请结果、查看通知
- `ADMINISTRATOR`：查看系统总体数据、工作量统计、最近岗位和最近申请记录

系统数据统一保存在 `storage/json` 目录下的本地 JSON 文件中，因此无需配置数据库即可完成演示和基本开发。

## 项目结构分析

当前项目采用了比较清晰的分层架构，主链路为 `Servlet -> Service -> Repository -> JSON 存储`：

- `src/main/java/com/bupt/ta/model`：核心领域模型与枚举，例如 `User`、`JobPost`、`JobApplication`、`Notification`、`Role`
- `src/main/java/com/bupt/ta/dto`：表单对象和面向页面展示的数据传输对象
- `src/main/java/com/bupt/ta/repository`：负责读取和写入 JSON 文件的持久层
- `src/main/java/com/bupt/ta/service`：认证、资料维护、岗位发布、申请处理、通知、仪表盘、管理员统计、账号删除等业务逻辑
- `src/main/java/com/bupt/ta/servlet`：控制器层，按角色和功能路由划分
- `src/main/webapp/WEB-INF/views`：JSP 页面模板
- `src/main/webapp/assets`：公共 CSS 和 JavaScript 资源
- `storage/json`：系统运行时使用的数据文件
- `docs`：架构说明、命名规范和页面说明文档
- `background`：原型图、问卷结果、backlog 和 user story 等背景材料

项目顶层目录如下：

```text
.
|-- background/
|-- docs/
|-- src/
|   `-- main/
|       |-- java/com/bupt/ta/
|       |   |-- dto/
|       |   |-- model/
|       |   |-- repository/
|       |   |-- service/
|       |   |-- servlet/
|       |   `-- util/
|       `-- webapp/
|           |-- assets/
|           |-- WEB-INF/views/
|           `-- index.jsp
|-- storage/
|   `-- json/
|-- pom.xml
`-- README.md
```

## 核心模块

### 认证与会话管理

- 入口页面：`src/main/webapp/index.jsp`
- 默认跳转到：`/auth/login`
- 登录认证由 `AuthService` 负责
- 会话状态通过 `HttpSession` 管理
- 当前使用的 Session Key：
  - `currentUserId`
  - `currentUsername`
  - `currentUserRole`

### 申请人模块

主要路由：

- `/applicant/dashboard`
- `/applicant/profile`
- `/applicant/jobs`
- `/applicant/status`
- `/applicant/notifications`
- `/cv/download`
- `/account/delete`

主要功能：

- 申请人必须先完善个人资料后才能投递岗位
- 投递前必须上传简历
- 支持的简历格式为 `PDF`、`DOC`、`DOCX`
- 简历大小限制为 `5 MB`
- 同一申请人不能重复申请同一个岗位
- 已关闭岗位或名额已满的岗位不能继续申请

### 课程负责人模块

主要路由：

- `/mo/dashboard`
- `/mo/post-job`
- `/mo/post-job/close`
- `/mo/applicants`
- `/mo/applicants/decision`
- `/mo/notifications`

主要功能：

- 课程负责人可以发布助教岗位
- 岗位可在截止日期后自动关闭
- 岗位可在招满后自动关闭
- 岗位也支持手动关闭
- 当前代码支持的申请状态包括 `UNDER_REVIEW`、`SELECTED`、`REJECTED`
- 更新申请结果后会自动向申请人发送通知

### 管理员模块

主要路由：

- `/admin/dashboard`

主要功能：

- 查看申请人数、岗位数、申请数等总览信息
- 查看开放岗位数和待审核申请数
- 根据已录用申请生成申请人工作量统计
- 展示最近发布的岗位和最近的申请动态
- 展示系统当前使用的数据文件位置

## 主要请求处理流程

当前代码中的请求处理流程如下：

1. 浏览器访问某个路由，例如 `/auth/login` 或 `/applicant/jobs`
2. Servlet 读取参数并校验登录状态或角色权限
3. Servlet 调用对应的 Service
4. Service 执行业务规则，并通过 Repository 读写 JSON 文件
5. Servlet 将请求转发到 JSP 页面，或重定向到下一个页面

## 数据存储说明

当前使用的数据文件如下：

- `storage/json/users.json`：用户账号和角色信息
- `storage/json/applicantProfiles.json`：申请人资料和简历元数据
- `storage/json/jobs.json`：助教岗位信息
- `storage/json/applications.json`：岗位申请记录和审核结果
- `storage/json/notifications.json`：系统通知

运行时上传目录：

- `storage/uploads/cv/`：用户上传的简历文件

存储根目录规则：

- 默认情况下，系统会使用项目根目录下的 `storage/`
- 也可以通过系统属性 `ta.storage.root` 自定义存储目录

示例：

```powershell
mvn clean package -Dta.storage.root=D:\data\ta-recruitment-storage
```

## 演示账号

仓库当前已经在 `storage/json/users.json` 中提供了演示账号：

| 角色 | 用户名 | 密码 |
| --- | --- | --- |
| `TA_APPLICANT` | `applicant1` | `Password123` |
| `MODULE_ORGANISER` | `mo1` | `Password123` |
| `ADMINISTRATOR` | `admin1` | `Password123` |

## 技术栈

- Java Web 应用，打包方式为 `WAR`
- Maven 构建
- Jakarta Servlet API `6.0.0`
- Jakarta JSP API `3.1.1`
- Gson `2.11.0`
- JSP 服务端渲染
- 基于 JSON 文件的持久化方案，不依赖关系型数据库

## 构建与运行

### 环境要求

根据当前 `pom.xml` 配置，项目的关键构建参数如下：

- `packaging`: `war`
- `maven.compiler.release`: `25`

因此最直接的构建环境是：

- JDK `25`
- Maven `3.9+`
- 支持 Jakarta 规范的 Servlet 容器

由于项目依赖 `jakarta.servlet-api:6.0.0`，部署到 `Tomcat 10.1+` 这一类兼容容器会更稳妥。

### 构建步骤

1. 安装与 `pom.xml` 中编译版本匹配的 JDK
2. 安装 Maven，因为当前仓库没有 `mvnw` 或 `mvnw.cmd`
3. 在项目根目录执行：

```powershell
mvn clean package
```

4. 构建成功后会生成：

```text
target/ta-recruitment-system.war
```

5. 将该 `war` 包部署到 Servlet 容器中，启动后访问：

```text
http://localhost:8080/ta-recruitment-system/
```

系统根路径会自动跳转到 `/auth/login`。

### 如果你当前使用的是 JDK 21

当前本地环境里 `java -version` 显示为 `21.0.8`，但 `pom.xml` 里设置的是 `release 25`。如果你们小组统一使用 JDK 21，可以把下面这项改成 `21` 后再构建：

```xml
<maven.compiler.release>21</maven.compiler.release>
```

这一步建议在团队确认统一 Java 版本后再改。

## 主要路由

| 路由 | 用途 | 角色 |
| --- | --- | --- |
| `/auth/login` | 登录页面与身份认证 | 公共 |
| `/auth/register` | 新用户注册 | 公共 |
| `/auth/forgot` | 通过邮箱和手机号重置密码 | 公共 |
| `/applicant/dashboard` | 申请人首页 | `TA_APPLICANT` |
| `/applicant/profile` | 编辑个人资料并上传简历 | `TA_APPLICANT` |
| `/applicant/jobs` | 浏览并申请助教岗位 | `TA_APPLICANT` |
| `/applicant/status` | 查看申请进度 | `TA_APPLICANT` |
| `/mo/dashboard` | 课程负责人首页 | `MODULE_ORGANISER` |
| `/mo/post-job` | 发布和管理岗位 | `MODULE_ORGANISER` |
| `/mo/applicants` | 查看申请人并处理结果 | `MODULE_ORGANISER` |
| `/admin/dashboard` | 管理员总览页面 | `ADMINISTRATOR` |
| `/cv/download` | 下载简历文件 | 已登录且有权限的用户 |
| `/account/delete` | 删除当前账号及关联数据 | 已登录用户 |

## 现有文档与背景材料

- `docs/architecture.md`：系统架构说明和存储说明
- `docs/naming-conventions.md`：命名规范和路由约定
- `docs/pages/`：页面级说明文档
- `background/prototype/`：原型页面和原型 PDF
- `background/user_story/`：用户故事草稿
- `background/backlog/`：backlog 和调研材料

## 当前注意事项

- 项目目前采用 JSON 文件持久化，更适合课程项目、演示和单实例运行场景
- 仓库当前没有 Maven Wrapper
- 当前代码库中没有自动化测试
- 上传目录会由 `StoragePathUtil` 自动创建
