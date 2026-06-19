# 💳 Nova Wallet

Billetera virtual desarrollada con **Java, Spring Boot, PostgreSQL y React**.

Permite registrarse, autenticarse mediante JWT, administrar saldo, realizar depósitos, retiros y transferencias, consultar movimientos y gestionar datos bancarios de forma segura.

---

## ✨ Características

* Registro de usuarios
* Login con JWT
* Recuperación de contraseña por email
* Gestión de saldo
* Depósitos y retiros
* Transferencias por CBU o Alias
* Historial de transacciones paginado
* Generación automática de CBU
* Alias personalizable
* Skeleton loading y feedback visual de operaciones

---

## 🛠️ Tecnologías

### Backend

* Java 
* Spring Boot
* Spring Security
* Spring Data JPA
* JWT
* PostgreSQL
* Maven

### Frontend

* React
* React Router
* Axios
* Tailwind CSS
* Vite

### Infraestructura

* Docker
* Docker Compose
* GitHub Actions

---

## 🏗️ Arquitectura

```text
auth/
user/
wallet/
transaction/
```

Estructura por capas:

```text
Controller
   ↓
Service
   ↓
Repository
   ↓
Database
```

Incluye DTOs, validaciones, manejo global de excepciones y seguridad basada en JWT.

---

## 🧩 Patrones de diseño

* **Factory** — creación de wallets y transacciones
* **Strategy** — métodos de depósito con distintas comisiones
* **Observer** — notificación de eventos de transacciones

---

## 🧪 Testing

* JUnit 5
* Mockito
* MockMvc
* H2 Database
* JaCoCo
* GitHub Actions CI

---

## 🚀 Funcionalidades

### Autenticación

* Registro de usuarios
* Login
* Verificación por email
* Recuperación de contraseña

### Wallet

* Consulta de saldo
* Depósitos
* Retiros
* Actualización de alias

### Transferencias

* Por CBU o Alias
* Validación de saldo
* Registro automático de movimientos

### Historial

* Consulta de movimientos
* Paginación
* Visualización de operaciones

---

## ▶️ Ejecución

### Base de datos

```bash
docker compose up -d
```

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

---

## 🌐 Endpoints principales

### Auth

```http
POST /auth/login
```

### Users

```http
POST /users
GET /users/me
POST /users/send-verification
POST /users/verify-pin
POST /users/forgot-password/reset
```

### Wallet

```http
GET /wallet/me
POST /wallet/deposit
POST /wallet/withdraw
PATCH /wallet/alias
```

### Transactions

```http
POST /transactions/transfer
GET /transactions/history
```

---

## 👨‍💻 Autor

**Mateo Calcagno**

Analista en Computación • Backend Developer 
