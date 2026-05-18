# login

## 1. Page purpose
- User login entry page.
- Collects username, password, and role, then redirects to the matching dashboard.

## 2. Route and access role
- View: `auth/login.jsp`
- Servlet: `LoginServlet`
- Routes: `GET /auth/login`, `POST /auth/login`
- Access: public

## 3. Frontend structure
- Centered login card
- Username input
- Password input
- Role selector
- Remember checkbox
- Forgot password and register links

## 4. Form fields and variable names
- Form: `loginForm`
- Inputs: `usernameInput`, `passwordInput`, `roleSelect`, `rememberCheckbox`
- Messages: `loginError`

## 5. Servlet methods and request parameters
- `doGet()`: restore a valid remember-me JWT cookie when present, otherwise expose flash message and render JSP
- `doPost()`: read `username`, `password`, `role`, and `rememberMe`
- When `rememberMe` is checked, the servlet writes an HttpOnly JWT cookie and keeps the normal server session flow

## 6. Service and repository functions used
- `AuthService.authenticateUser(username, password, role)`
- `SessionUtil.createSession(...)`
- `SessionUtil.restoreLoginFromRememberMe(...)`
- `JwtUtil.createRememberMeToken(...)`
- `CookieUtil.writeRememberMeCookie(...)`
- `RouteUtil.getDashboardPath(role)`

## 7. JSON fields read or written
- Reads `users.json`
- Uses `username`, `passwordHash`, `role`, `userId`, `active`, and `tokenVersion`

## 8. Validation, edge cases, and manual test checklist
- Empty username/password/role should fail
- Wrong role with correct username should fail
- Successful login should create session and redirect by role
- Remember-me login should restore a new session after the original server session expires
- Logout should clear both the server session and remember-me cookie
- Password reset or administrator account disable/enable should invalidate old remember-me JWTs through `tokenVersion`
