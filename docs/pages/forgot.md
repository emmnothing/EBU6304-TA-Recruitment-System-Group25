# forgot

## 1. Page purpose
- Password reset page using email and phone verification.

## 2. Route and access role
- View: `auth/forgot.jsp`
- Servlet: `ForgotPasswordServlet`
- Routes: `GET /auth/forgot`, `POST /auth/forgot`
- Access: public

## 3. Frontend structure
- Wide form card
- Email and phone verification fields
- New password and confirm password fields

## 4. Form fields and variable names
- Form: `forgotForm`
- Inputs: `phoneInput`, `emailInput`, `newPasswordInput`, `confirmPasswordInput`
- Messages: `forgotClientError`, `resetError`

## 5. Servlet methods and request parameters
- `doGet()`: render form
- `doPost()`: read `phoneNumber`, `email`, `newPassword`, `confirmPassword`

## 6. Service and repository functions used
- `AuthService.resetPassword(resetForm)`
- `UserRepository.findAll()`
- `UserRepository.saveAll(users)`

## 7. JSON fields read or written
- Reads `users.json`
- Updates `passwordHash`

## 8. Validation, edge cases, and manual test checklist
- Invalid email should fail
- Invalid phone should fail
- Password mismatch should fail
- Unknown email/phone pair should fail
- Success should redirect to login
