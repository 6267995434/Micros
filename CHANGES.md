# Feign Client Integration — Change Log

**Date:** 2026-06-07
**Goal:** Wire `user-service` to call `payment-service` over HTTP using Spring Cloud OpenFeign.

**Flow:** `GET /users/{id}` (user-service:8081) → Feign → `GET /payments/{id}/status` (payment-service:8082) → returns merged JSON `{ id, name, paymentStatus }`.

> **Note (revision 2):** All Lombok annotations (`@Data`, `@Getter`, `@Setter`, `@RequiredArgsConstructor`, `@AllArgsConstructor`) were removed from the source files because Lombok was not installed in the IDE — `@Data` was not generating `getStatus()` and `@RequiredArgsConstructor` was not generating the constructor needed to inject `PaymentClient`. Code now uses plain Java with explicit getters/setters/constructors and `@Autowired` field injection. See section **6. Lombok IDE setup** below for the optional way to re-enable Lombok.

---

## 1. New files

### 1.1 `PaymentResponse.java` (DTO)

| Field | Value |
|---|---|
| Path | `user-service/src/main/java/Notes/user_service/dto/PaymentResponse.java` |
| Package | `Notes.user_service.dto` |
| Type | `class` (POJO) |
| Annotations | `@JsonIgnoreProperties(ignoreUnknown = true)` |
| Purpose | Deserializes the JSON returned by `payment-service` (`{userId, status, timestamp}`). |
| Fields | `String userId`, `String status`, `Long timestamp` |
| Accessors | Explicit getters and setters for every field (no Lombok). |

---

### 1.2 `UserResponse.java` (DTO)

| Field | Value |
|---|---|
| Path | `user-service/src/main/java/Notes/user_service/dto/UserResponse.java` |
| Package | `Notes.user_service.dto` |
| Type | `class` (POJO) |
| Annotations | _none_ |
| Purpose | Response body returned by `GET /users/{id}`. |
| Fields | `Long id`, `String name`, `String paymentStatus` |
| Accessors | Explicit all-args constructor + getters and setters for every field. |

---

### 1.3 `PaymentClient.java` (Feign interface)

| Field | Value |
|---|---|
| Path | `user-service/src/main/java/Notes/user_service/client/PaymentClient.java` |
| Package | `Notes.user_service.client` |
| Type | `interface` (Spring generates the impl at runtime) |
| Annotations | `@FeignClient(name = "payment-service", url = "${payment-service.url}")` |
| Purpose | Declarative HTTP client — calling `getPaymentStatus(id)` fires `GET {payment-service.url}/payments/{id}/status`. |
| Methods | `PaymentResponse getPaymentStatus(@PathVariable("userId") String userId)` mapped to `GET /payments/{userId}/status` |

---

### 1.4 `UserService.java` (Service layer)

| Field | Value |
|---|---|
| Path | `user-service/src/main/java/Notes/user_service/service/UserService.java` |
| Package | `Notes.user_service.service` |
| Type | `class` |
| Annotations | `@Service` |
| Purpose | Orchestrates the call to `PaymentClient` and assembles the `UserResponse`. |
| Dependencies | `PaymentClient paymentClient` injected via `@Autowired` field injection. |
| Methods | `UserResponse getUserById(Long id)` — calls Feign, returns merged DTO. |
| Note | `name` is hardcoded to `"Akshay"` for now; replace with a real lookup (DB / repository) later. |

---

## 2. Modified files

### 2.1 `UserController.java`

| Field | Value |
|---|---|
| Path | `user-service/src/main/java/Notes/user_service/controller/UserController.java` |
| Package | `Notes.user_service.controller` |
| Type | `class` |
| Annotations | `@RestController`, `@RequestMapping("/users")` |
| Before | Stub with broken `ResponseEntity<>` placeholder — would not compile. |
| After | Exposes `GET /users/{id}` → delegates to `UserService.getUserById(id)` → returns `ResponseEntity<UserResponse>`. |
| Dependencies | `UserService userService` injected via `@Autowired` field injection. |

---

### 2.2 `user-service/src/main/resources/application.properties`

| Before | After |
|---|---|
| `spring.application.name=user-service` | `spring.application.name=user-service`<br>`server.port=8081`<br>`payment-service.url=http://localhost:8082` |

**Added:**
- `server.port=8081` — fixes the port so payment-service can use 8082.
- `payment-service.url=http://localhost:8082` — resolved by the `${payment-service.url}` placeholder in `@FeignClient`.

---

### 2.3 `payment-service/src/main/resources/application.properties`

| Before | After |
|---|---|
| `spring.application.name=payment-service` | `spring.application.name=payment-service`<br>`server.port=8082` |

**Added:** `server.port=8082` so the two services don't collide on the default 8080.

---

### 2.4 `api-gateway/src/main/resources/application.properties`

| Field | Value |
|---|---|
| Path | `api-gateway/src/main/resources/application.properties` |
| Purpose | Configure Spring Cloud Gateway as the single entry point for external clients. Routes `/users/**` to user-service and `/payments/**` to payment-service. |

**Before:**
```properties
spring.application.name=api-gateway
```

**After:**
```properties
spring.application.name=api-gateway
server.port=8080

# Route 1: /users/** -> user-service (8081)
spring.cloud.gateway.routes[0].id=user-service
spring.cloud.gateway.routes[0].uri=http://localhost:8081
spring.cloud.gateway.routes[0].predicates[0]=Path=/users/**

# Route 2: /payments/** -> payment-service (8082)
spring.cloud.gateway.routes[1].id=payment-service
spring.cloud.gateway.routes[1].uri=http://localhost:8082
spring.cloud.gateway.routes[1].predicates[0]=Path=/payments/**
```

**Key properties explained:**

| Property | Meaning |
|---|---|
| `server.port=8080` | Gateway is the public-facing port; external clients only talk to `:8080`. |
| `routes[N].id` | Logical name for the route (shown in logs / actuator). |
| `routes[N].uri` | Downstream service base URL. |
| `routes[N].predicates[0]=Path=/users/**` | Predicate: forward any request whose path matches `/users/**` to this route's `uri`. |

---

## 3. Pre-existing (no change needed — already correct)

| File | Why it's already OK |
|---|---|
| `user-service/pom.xml` | Already declares `spring-cloud-starter-openfeign` and imports `spring-cloud-dependencies` BOM. Lombok dependency is still present but currently unused — safe to remove later. |
| `UserServiceApplication.java` | Already annotated with `@EnableFeignClients` — required to scan for `@FeignClient` interfaces. |
| `payment-service/.../ApiController.java` | Exposes `GET /payments/{userId}/status` returning `{userId, status, timestamp}` — matches the Feign contract. |
| `api-gateway/pom.xml` | Already declares `spring-cloud-starter-gateway-server-webflux` and imports the Spring Cloud BOM (2025.0.2). |
| `api-gateway/.../ApiGatewayApplication.java` | Plain `@SpringBootApplication` — Spring Cloud Gateway requires no extra annotations; route config is purely declarative. |

---

## 4. Final package layout (user-service)

```
Notes.user_service
├── UserServiceApplication.java       @SpringBootApplication, @EnableFeignClients
├── controller/
│   └── UserController.java           @RestController  → GET /users/{id}
├── service/
│   └── UserService.java              @Service         → orchestration
├── client/
│   └── PaymentClient.java            @FeignClient     → remote HTTP call
└── dto/
    ├── UserResponse.java             response body (plain POJO)
    └── PaymentResponse.java          deserialized payment payload (plain POJO)
```

---

## 5. How to run

Start all three services (order matters — bring up the downstreams first):

1. **payment-service** → port `8082`
2. **user-service**    → port `8081`
3. **api-gateway**     → port `8080`

### Test directly (bypassing the gateway)
```
curl http://localhost:8081/users/1
curl http://localhost:8082/payments/1/status
```

### Test through the gateway (the production-style flow)
```
curl http://localhost:8080/users/1
curl http://localhost:8080/payments/1/status
```

**Expected for `/users/1` (either direct or via gateway):**
```json
{ "id": 1, "name": "Akshay", "paymentStatus": "SUCCESS" }
```

### Request flow through the gateway

```
client ──► api-gateway:8080/users/1
              │
              │  matches predicate Path=/users/**
              ▼
           user-service:8081/users/1
              │
              │  Feign call (PaymentClient)
              ▼
           payment-service:8082/payments/1/status
```

> The spec asked for `"paymentStatus": "PAID"`, but `payment-service` currently returns `"SUCCESS"` (hardcoded at `payment-service/.../ApiController.java:21`). To get `"PAID"`, change that literal — `user-service` just forwards whatever payment-service sends.

---

## 6. Lombok IDE setup (optional)

The `pom.xml` still includes Lombok, so it works at Maven build time. To make Lombok annotations work **inside Eclipse / STS** too:

1. Locate `lombok.jar` in your local Maven repo:
   `~/.m2/repository/org/projectlombok/lombok/<version>/lombok.jar`
2. Run it: `java -jar lombok.jar`
3. Point the installer at `eclipse.exe` / `STS.exe` → click **Install/Update** → restart the IDE.

After that, you can re-introduce `@Data`, `@RequiredArgsConstructor`, etc. and delete the explicit getters/setters/constructors.

---

## 7. Revision history

| Rev | Date | Change |
|---|---|---|
| 1 | 2026-06-07 | Initial Feign client wiring with Lombok annotations. |
| 2 | 2026-06-07 | Removed Lombok from `UserService`, `UserController`, `PaymentResponse`, `UserResponse` because Lombok was not active in the IDE — replaced with explicit getters/setters/constructors and `@Autowired` field injection. |
| 3 | 2026-06-07 | Configured `api-gateway` with two routes (`/users/**` → 8081, `/payments/**` → 8082) and fixed it to port 8080. |
