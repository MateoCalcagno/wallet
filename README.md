# 💳 Wallet App

![Status](https://img.shields.io/badge/estado-en%20desarrollo%20continuo-blue)

Aplicación de billetera virtual desarrollada con **Java, Spring Boot y React**. Permite a los usuarios registrarse, autenticarse mediante JWT, gestionar su saldo y realizar operaciones financieras de manera simple y segura.

---

## ✨ Características

* Registro y autenticación de usuarios
* Autorización mediante JWT
* Gestión de saldo personal
* Depósitos y retiros de dinero
* Transferencias por CBU o Alias
* Historial de transacciones paginado
* Generación automática de CBU
* Alias personalizable
* Perfil de usuario
* Persistencia de datos con PostgreSQL

---

## 🛠️ Tecnologías utilizadas

### Backend
* Java 21
* Spring Boot
* Spring Security
* JWT
* Spring Data JPA
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

---

## 🏗️ Arquitectura

El backend está organizado utilizando una **arquitectura modular y en capas**.

Cada módulo representa una funcionalidad del sistema:

```text
auth/
user/
wallet/
transaction/
```

Dentro de cada módulo se sigue la siguiente estructura:

```text
Controller
   ↓
Service
   ↓
Repository
   ↓
Database
```

Además, se utilizan:

* DTOs para la comunicación entre cliente y servidor
* Validaciones de datos
* Manejo centralizado de excepciones
* Seguridad basada en JWT

---

## 🧩 Patrones de diseño

- **Factory** — creación de transacciones y wallets (`TransactionFactory`, `WalletFactory`)
- **Strategy** — métodos de depósito intercambiables con distintas comisiones (`DebitCardDepositStrategy`, `CreditCardDepositStrategy`, `BankTransferDepositStrategy`)

---

## 🧪 Testing

* Tests unitarios con JUnit 5 y Mockito
* Tests de integración con MockMvc y H2
* Cobertura de código con JaCoCo
* CI con GitHub Actions

---

## 🚀 Funcionalidades

### Autenticación
* Registro de usuarios
* Inicio de sesión
* Generación de token JWT
* Protección de endpoints

### Wallet
* Consulta de saldo
* Depósitos y retiros
* Actualización de alias

### Transferencias
* Transferencia mediante Alias o CBU
* Validación de saldo disponible
* Registro automático de movimientos

### Historial
* Consulta de movimientos
* Paginación de resultados
* Visualización de depósitos, retiros y transferencias

---

## 🐘 Base de datos

Levantar PostgreSQL utilizando Docker:

```bash
docker compose up -d
```

---

## ▶️ Ejecución del backend

```bash
cd backend
./mvnw spring-boot:run
```

Servidor:

```text
http://localhost:8080
```

---

## ⚛️ Ejecución del frontend

```bash
cd frontend
npm install
npm run dev
```

Aplicación:

```text
http://localhost:5173
```

---

## 🌐 Endpoints principales

### Autenticación
```http
POST /auth/login
```

### Usuarios
```http
POST /users
GET /users/me
```

### Wallet
```http
GET /wallet/me
POST /wallet/deposit
POST /wallet/withdraw
PATCH /wallet/alias
```

### Transferencias
```http
POST /transactions/transfer
GET /transactions/history
```

---

## ⚙️ Variables de entorno

Copiá el archivo de ejemplo y completá con tus valores:

```bash
cp .env.example .env
```

---

## 👨‍💻 Autor

**Mateo Calcagno**
Analista en Computación 
