# Code Improvement Suggestions

## 1. JWT Secret is Hardcoded (Security Risk)

**File:** `src/main/java/com/example/demo/service/JWTService.java` — line 19

**Problem:**
```java
private static final String SECRET_KEY = "w7vQ8FhX2G9JkZ3vM1xQeL2uT0aR5bC6dE7fH8iJ9kLmNoPqRsTuVwXyZaBcDeFg";
```
The JWT signing secret is hardcoded directly in the source code. Anyone with access to the repository can read it and forge valid tokens.

**Fix:**
Move the secret to `application.properties`:
```properties
jwt.secret=your-secret-key-here
```
Then load it in `JWTService`:
```java
@Value("${jwt.secret}")
private String secretKey;
```

---

## 2. JWT Token Expires in 24 Minutes, Not 24 Hours

**File:** `src/main/java/com/example/demo/service/JWTService.java` — line 56

**Problem:**
```java
.setExpiration(new java.util.Date(System.currentTimeMillis() + 1000 * 60 * 24))
```
`1000 * 60 * 24` equals 1,440,000 milliseconds — which is only **24 minutes**. Users will be logged out unexpectedly.

**Fix:**
```java
.setExpiration(new java.util.Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
```
`1000 * 60 * 60 * 24` = 86,400,000 milliseconds = 24 hours.

---

## 3. Staff Endpoints Have No Authentication

**File:** `src/main/java/com/example/demo/config/SecurityConfiguration.java` — line 32

**Problem:**
```java
.requestMatchers("/api/menu/**", "/api/orders/**", "/api/staff/**").permitAll()
```
All staff and order endpoints are publicly accessible with no authentication required. Anyone can update order statuses or view all orders without logging in.

**Fix:**
Restrict staff routes to authenticated users with the `STAFF` role:
```java
.requestMatchers("/api/staff/**").hasRole("STAFF")
.requestMatchers("/api/orders/**").authenticated()
.requestMatchers("/api/menu/**").permitAll()
```

---

## 4. Duplicate Order Status Transition Logic

**Files:**
- `src/main/java/com/example/demo/service/OrderService.java` — `validateStatusTransition()` method
- `src/main/java/com/example/demo/service/StaffService.java` — `ALLOWED_TRANSITIONS` map

**Problem:**
The valid order status transitions are defined in two separate places. If the business rules change, both places must be updated, which is easy to miss and can cause inconsistent behaviour.

**Fix:**
Define the transition rules once — either in `OrderStatus.java` as a method or in a single shared service — and have both `OrderService` and `StaffService` reference it.

---

## 5. Customer and User Are Two Separate Tables for the Same Concept

**Files:**
- `src/main/java/com/example/demo/entity/User.java`
- `src/main/java/com/example/demo/entity/Customer.java`

**Problem:**
`Customer` has its own `customers` table with `email` and `password` columns, but `User` already stores the same data. When a customer registers, their data ends up duplicated across both tables. This adds unnecessary complexity.

**Fix:**
Remove the `Customer` entity and `customers` table. Store all user data in the `users` table and use the `Role` field to distinguish customers from staff.

---

## 6. CustomerController Is Redundant

**File:** `src/main/java/com/example/demo/controller/CustomerController.java`

**Problem:**
The `POST /api/customers/login` endpoint returns a hardcoded string `"Login successful!"` and does not perform any real authentication. The `POST /api/customers/register` endpoint duplicates functionality already handled by `POST /api/v1/auth/register`.

**Fix:**
Remove `CustomerController` entirely. Direct all registration and login through `AuthenticationController` (`/api/v1/auth/register` and `/api/v1/auth/authenticate`).

---

## 7. System.out.println in Production Code

**File:** `src/main/java/com/example/demo/config/JWTAuthenticationFilter.java` — line 37

**Problem:**
```java
System.out.println("JWT filter checking path: " + path);
```
This prints a log line for every single HTTP request to stdout. In production this creates noise and can affect performance.

**Fix:**
Replace with an SLF4J logger at `DEBUG` level so it only appears when needed:
```java
private static final Logger log = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

log.debug("JWT filter checking path: {}", path);
```

---

## 8. Generic RuntimeException Used for Business Errors

**Files:**
- `src/main/java/com/example/demo/service/OrderService.java`
- `src/main/java/com/example/demo/auth/AuthenticationService.java`

**Problem:**
Errors like "Order not found" or "User not found" throw a plain `RuntimeException`. The `ApiExceptionHandler` does not catch `RuntimeException`, so the client receives a `500 Internal Server Error` instead of a meaningful `404 Not Found`.

**Fix:**
Create custom exception classes (as already done for menu items) and handle them in `ApiExceptionHandler`:
```java
// New exception
public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long id) {
        super("Order not found with id: " + id);
    }
}

// In ApiExceptionHandler
@ExceptionHandler(OrderNotFoundException.class)
public ResponseEntity<ApiErrorResponse> handleOrderNotFound(OrderNotFoundException ex) {
    return ResponseEntity.status(404).body(new ApiErrorResponse(404, "Not Found", ex.getMessage()));
}
```

---

## Priority Summary

| # | Issue | Priority |
|---|-------|----------|
| 1 | JWT secret hardcoded | High |
| 3 | Staff endpoints unprotected | High |
| 2 | JWT expires in 24 minutes | Medium |
| 8 | RuntimeException returns 500 | Medium |
| 4 | Duplicate status transition logic | Medium |
| 5 | Duplicate Customer/User tables | Medium |
| 6 | CustomerController is redundant | Low |
| 7 | System.out.println in filter | Low |
