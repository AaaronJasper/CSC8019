# CSC8019 Coffee Station API

## Setup

### 1. Install Docker

- **macOS / Windows**: [Docker Desktop](https://www.docker.com/products/docker-desktop)
- **Linux**: [Docker Engine Install Guide](https://docs.docker.com/engine/install/)

Verify:
```bash
docker --version
docker compose version
```

### 2. Start the Project

```bash
docker compose up --build
```

Start in background:
```bash
docker compose up -d
```

Stop:
```bash
docker compose down
```

Stop and remove volumes (clears database):
```bash
docker compose down -v
```

The app runs at `http://localhost:8080`.

---

## API Endpoints

### Authentication

| Method | URL | Description |
|--------|-----|-------------|
| `POST` | `/api/v1/auth/register` | Register a new user account |
| `POST` | `/api/v1/auth/authenticate` | Log in and receive a JWT token |
| `DELETE` | `/api/v1/auth/logout` | Log out and invalidate the current token |

**POST /api/v1/auth/register** — `application/json`
```json
{
  "firstname": "John",
  "lastname": "Doe",
  "email": "john@example.com",
  "password": "secret123",
  "role": "CUSTOMER"
}
```
- `role` must be either `CUSTOMER` or `STAFF`.
- Returns a JWT token on success.

**POST /api/v1/auth/authenticate** — `application/json`
```json
{
  "email": "john@example.com",
  "password": "secret123"
}
```
- Returns a JWT token on success.

**Response for register and authenticate:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**DELETE /api/v1/auth/logout**
- Requires `Authorization: Bearer <token>` header.
- Blacklists the token so it can no longer be used.
- Returns `204 No Content` on success.

---

### Menu

| Method | URL | Description |
|--------|-----|-------------|
| `GET` | `/api/menu` | Get all menu items |
| `GET` | `/api/menu/{id}` | Get a single menu item by ID |
| `POST` | `/api/menu` | Create a new menu item |
| `PUT` | `/api/menu/{id}` | Update an existing menu item |
| `DELETE` | `/api/menu/{id}` | Soft-delete a menu item |

**POST /api/menu** — `multipart/form-data`
```
name        String  (required, max 200)
description String  (required, max 2000)
imgUrl      String  (required, max 2048)
rating      Decimal (required, 0.0–5.0, 1 decimal place)
category    String  (required, max 100)
itemCount   Integer (required, >= 0)
```

**PUT /api/menu/{id}** — `multipart/form-data` (all fields optional)
```
name        String  (max 200)
description String  (max 2000)
imgUrl      String  (max 2048)
rating      Decimal (0.0–5.0, 1 decimal place)
category    String  (max 50)
itemCount   Integer (>= 1)
```

---

### Menu Item Size Prices

| Method | URL | Description |
|--------|-----|-------------|
| `POST` | `/api/menu/size-prices` | Add a size/price to a menu item |
| `PUT` | `/api/menu/size-prices/{id}` | Update an existing size/price entry |

**POST /api/menu/size-prices** — `multipart/form-data` or `application/json`
```
menuItemId  Long    (required)
size        String  (required, e.g. "REGULAR" or "LARGE")
price       Decimal (required, 0.00–1000.00, 2 decimal places)
```

**PUT /api/menu/size-prices/{id}** — `multipart/form-data` or `application/json` (all fields optional)
```
size        String
price       Decimal
```

---

### Orders

| Method | URL | Description |
|--------|-----|-------------|
| `POST` | `/api/orders/order` | Place a new order |
| `GET` | `/api/orders/staff/active` | Get all active orders (staff view) |
| `GET` | `/api/orders/customer/{customerId}` | Get order history for a customer |
| `GET` | `/api/orders/{orderId}` | Get a specific order by ID |
| `PUT` | `/api/orders/{orderId}/status` | Update an order's status |

**POST /api/orders/order** — `application/json`
```json
{
  "customerId": 1,
  "guestName": "John",
  "menuItemIds": [1, 2],
  "sizes": ["REGULAR", "LARGE"],
  "quantities": [1, 2]
}
```
- `customerId` is optional — omit for guest orders, provide `guestName` instead.
- `menuItemIds`, `sizes`, and `quantities` must be the same length.

**GET /api/orders/staff/active** — returns orders with status `NEW`, `ACCEPTED`, `PREPARING`, or `READY`.

**PUT /api/orders/{orderId}/status** — `application/json`
```json
{
  "newStatus": "ACCEPTED"
}
```

Valid order status transitions:
```
NEW        → ACCEPTED or CANCELLED
ACCEPTED   → PREPARING or CANCELLED
PREPARING  → READY or CANCELLED
READY      → COLLECTED or CANCELLED
COLLECTED  → (terminal)
CANCELLED  → (terminal)
```

Orders stuck in `NEW` or `ACCEPTED` for more than **15 minutes** are automatically cancelled by the system.

---

### Staff Orders

| Method | URL | Description |
|--------|-----|-------------|
| `GET` | `/api/staff/orders` | Get all active orders, newest first |
| `GET` | `/api/staff/orders/archive` | Get all archived (completed/cancelled) orders |
| `PATCH` | `/api/staff/orders/{id}/status?status={STATUS}` | Update an order's status |

**PATCH /api/staff/orders/{id}/status** — status passed as a query parameter.

Example:
```
PATCH /api/staff/orders/5/status?status=PREPARING
```

Orders are archived automatically when they reach `COLLECTED` or `CANCELLED` status and will no longer appear in the active list.

---

### Demo

| Method | URL | Description |
|--------|-----|-------------|
| `GET` | `/api/v1/demo-controller` | Returns a hello message from a secured endpoint |
