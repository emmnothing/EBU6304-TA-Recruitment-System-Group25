# register

## 1. Page purpose
- Public registration page for non-admin users.
- Creates a new `TA_APPLICANT` or `MODULE_ORGANISER` account.

## 2. Route and access role
- View: `auth/register.jsp`
- Servlet: `RegisterServlet`
- Routes: `GET /auth/register`, `POST /auth/register`
- Access: public

## 3. Frontend structure
- Wide two-column form card
- Arithmetic verification area
- User type selector for applicant and module organiser only
- Back to login link

## 4. Form fields and variable names
- Form: `registerForm`
- Inputs: `usernameInput`, `emailInput`, `phoneInput`, `roleSelect`, `passwordInput`, `confirmPasswordInput`, `verificationAnswerInput`
- Messages: `registerClientError`, `registerError`

## 5. Servlet methods and request parameters
- `doGet()`: render form
- `doPost()`: read `username`, `email`, `phoneNumber`, `role`, `password`, `confirmPassword`

## 6. Service and repository functions used
- `AuthService.registerUser(registerForm)`
- `UserRepository.findAll()`
- `UserRepository.saveAll(users)`

## 7. JSON fields read or written
- Reads `users.json` for uniqueness checks
- Writes `userId`, `username`, `email`, `phoneNumber`, `passwordHash`, `role`, `createdAt`

## 8. Validation, edge cases, and manual test checklist
- Email format check
- 11-digit phone check
- User type selection check
- `ADMINISTRATOR` must be rejected by the backend even if submitted manually
- Password match check
- Username/email/phone uniqueness
- Successful registration should redirect to login with flash message
