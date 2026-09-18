# Order Management Service

Part of the **Order Management System (OMS)** — FNB B4 End-to-End Bootcamp Simulation.

Owns the product inventory catalogue and the full order lifecycle, including multi-item orders, stock management with optimistic locking, and delivery flagging.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.3.5 |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| Database | MySQL (`order_db`) |
| ORM | Spring Data JPA / Hibernate |
| Utilities | Lombok 1.18.34 |
| Java | 21 |

---

## Database Schema

### `inventory_item`
| Column | Type | Constraint |
|---|---|---|
| item_id | BIGINT | Primary key, auto-generated |
| item_name | VARCHAR(150) | Not null |
| description | VARCHAR(500) | — |
| price | DECIMAL(10,2) | Not null |
| stock_quantity | INT | Not null |
| version | INT | Not null — optimistic locking |
| created_at / updated_at | TIMESTAMP | — |

### `orders`
| Column | Type | Constraint |
|---|---|---|
| order_id | BIGINT | Primary key, auto-generated |
| customer_id | BIGINT | Logical reference to users.id |
| order_date | TIMESTAMP | Default current timestamp |
| status | ENUM | Not null, default PLACED |
| delivery_needed | BOOLEAN | Not null |
| total_amount | DECIMAL(10,2) | Computed from order items |
| created_at / updated_at | TIMESTAMP | — |

### `order_item`
| Column | Type | Constraint |
|---|---|---|
| order_item_id | BIGINT | Primary key, auto-generated |
| order_id | BIGINT | Foreign key → orders.order_id |
| item_id | BIGINT | Foreign key → inventory_item.item_id |
| quantity | INT | Not null |
| unit_price_at_purchase | DECIMAL(10,2) | Price snapshot at time of sale |
| subtotal | DECIMAL(10,2) | quantity × unit_price_at_purchase |

---

## Order Status Lifecycle

| Path | Flow |
|---|---|
| Delivery required | `PLACED` → `OUT_FOR_DELIVERY` → `DELIVERED` |
| No delivery | `PLACED` → `COMPLETED` |

`DELIVERED` is set when Order Management consumes the `delivery-completed` Kafka event from the Delivery Service. `COMPLETED` is set directly for non-delivery orders.

---

## API Endpoints

All endpoints require a valid JWT Bearer token in the `Authorization` header.

### Inventory (ADMIN only for write operations)

| Method | Endpoint | Role | Description |
|---|---|---|---|
| POST | `/api/inventory` | ADMIN | Add a new inventory item |
| GET | `/api/inventory` | Any | List all inventory items |
| GET | `/api/inventory/{itemId}` | Any | Get item by ID |
| PUT | `/api/inventory/{itemId}` | ADMIN | Update an inventory item |
| DELETE | `/api/inventory/{itemId}` | ADMIN | Delete an inventory item |

**Add item — `POST /api/inventory`**
```json
{
  "itemName": "Laptop",
  "description": "15-inch business laptop",
  "price": 9999.99,
  "stockQuantity": 50
}
```

### Orders

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/orders` | Place a new order (uses JWT customerId) |
| GET | `/api/orders` | List all orders |
| GET | `/api/orders/{orderId}` | Get order by ID |
| GET | `/api/orders/customer/{customerId}` | Get orders by customer |
| PATCH | `/api/orders/{orderId}/status` | Update order status |

**Place order — `POST /api/orders`**
```json
{
  "deliveryNeeded": true,
  "items": [
    { "itemId": 1, "quantity": 2 }
  ]
}
```

**Response (201):**
```json
{
  "orderId": 1,
  "customerId": 3,
  "orderDate": "2026-09-18T10:00:00",
  "status": "PLACED",
  "deliveryNeeded": true,
  "totalAmount": 19999.98,
  "items": [
    {
      "orderItemId": 1,
      "itemId": 1,
      "itemName": "Laptop",
      "quantity": 2,
      "unitPriceAtPurchase": 9999.99,
      "subtotal": 19999.98
    }
  ]
}
```

**Update status — `PATCH /api/orders/{orderId}/status`**
```json
{
  "status": "OUT_FOR_DELIVERY"
}
```

---

## Stock Concurrency Control

`inventory_item` uses JPA `@Version` for optimistic locking. If two customers attempt to purchase the last unit simultaneously:

- The first transaction succeeds and increments the version.
- The second transaction detects a version mismatch (`OptimisticLockException`).
- The service re-reads current stock and either retries or returns a clear error — no overselling occurs.

---

## Running Locally

**Prerequisites:** Java 21, MySQL running on port 3306, User Management Service running on port 8081

1. Create the database:
```sql
CREATE DATABASE order_db;
```

2. Set Java 21:
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.12"
```

3. Start the service:
```powershell
.\mvnw spring-boot:run
```

Service starts on **port 8082**.

---

## Configuration

`src/main/resources/application.yaml`

```yaml
server:
  port: 8082

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/order_db
    username: root
    password: 12345

jwt:
  secret: 404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
```

> The JWT secret must match the one configured in the User Management Service.
