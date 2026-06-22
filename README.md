# 💳 Nova Wallet

[![Live Demo](https://img.shields.io/badge/demo-live-brightgreen?style=for-the-badge&logo=vercel)](https://nova-wallet-mu.vercel.app)

Una billetera digital full-stack que permite a los usuarios gestionar su dinero: depósitos, retiros, transferencias entre usuarios y seguimiento de transacciones.

---

## 🧱 Stack tecnológico

### Backend
- **Java 21** + **Spring Boot 3.5**
- **Spring Security** con autenticación JWT
- **Spring Data JPA** + **PostgreSQL**
- **Brevo (Sendinblue)** para envío de emails
- **Springdoc OpenAPI** (Swagger UI)
- **Lombok**, **Maven**, **Docker**

### Frontend
- **React 19** + **Vite 8**
- **React Router DOM v7**
- **Tailwind CSS v4**
- **Framer Motion**
- **Axios**

---

## ✨ Funcionalidades

- Registro de usuarios con verificación por email (PIN)
- Login con JWT y rutas protegidas
- Dashboard con saldo y resumen de cuenta
- Depósito de dinero (débito, crédito, transferencia bancaria)
- Retiro de fondos
- Transferencia entre usuarios via CBU o alias
- Historial de transacciones paginado con filtros por tipo
- Estadísticas de movimientos
- Recuperación de contraseña por email
- Actualización de alias de billetera
- Tour de onboarding para nuevos usuarios
- CI/CD con GitHub Actions (tests + deploy automático a Render)

---

## 🎨 Patrones de diseño

| Patrón | Dónde se aplica |
|---|---|
| **Strategy** | Métodos de depósito: `DebitCardDepositStrategy`, `CreditCardDepositStrategy`, `BankTransferDepositStrategy`. Se selecciona la implementación en runtime según el método de pago. |
| **Factory** | `WalletFactory` genera CBU y alias al crear una billetera. `TransactionFactory` construye transacciones según su tipo (depósito, retiro, transferencia). |
| **Observer** | Al completarse una transferencia se publica un `TransferCompletedEvent`. El `TransferNotificationListener` lo escucha de forma asíncrona y envía emails al emisor y receptor. |

---

## 📁 Estructura del proyecto

```
wallet/
├── backend/                  # API REST - Spring Boot
│   ├── src/main/java/com/mateo/wallet/
│   │   ├── auth/             # Login, JWT
│   │   ├── user/             # Registro, perfil, recuperación de contraseña
│   │   ├── wallet/           # Saldo, depósito, retiro, alias, CBU
│   │   ├── transaction/      # Transferencias, historial
│   │   ├── verification/     # Verificación de email por PIN
│   │   ├── common/           # Excepciones globales, auditoría, email
│   │   └── config/           # Security, JPA, OpenAPI
│   ├── docker-compose.yml
│   ├── Dockerfile
│   └── pom.xml
│
└── frontend/                 # SPA - React + Vite
    └── src/
        ├── pages/            # Login, Register, Dashboard, Transfer, etc.
        ├── components/       # Layout, NavItem, AuthPanel, OnboardingTour...
        ├── hooks/            # useDashboard, useTransactionHistory...
        ├── api/              # Configuración de Axios
        └── utils/
```

---

## ⚙️ Variables de entorno

### Backend — `.env`

Copiá el archivo de ejemplo y completá los valores:

```bash
cp .env.example .env
```

| Variable | Descripción |
|---|---|
| `DB_HOST` | Host de PostgreSQL (ej: `localhost`) |
| `DB_PORT` | Puerto de PostgreSQL (ej: `5432`) |
| `DB_NAME` | Nombre de la base de datos |
| `DB_USERNAME` | Usuario de la base de datos |
| `DB_PASSWORD` | Contraseña de la base de datos |
| `JWT_SECRET` | Secreto para firmar tokens (mín. 32 chars) |
| `JWT_EXPIRATION` | Expiración del token en ms (default: `86400000` = 24h) |
| `BREVO_API_KEY` | API key de Brevo para envío de emails |
| `BREVO_SENDER_EMAIL` | Email remitente |
| `BREVO_SENDER_NAME` | Nombre remitente |
| `CORS_ALLOWED_ORIGIN` | Origen permitido (default: `http://localhost:5173`) |

### Frontend — `.env`

```env
VITE_API_URL=http://localhost:8080
```

---

## 🚀 Cómo ejecutar el proyecto

### Prerrequisitos

- Java 21
- Maven 3.9+
- Node.js 18+
- Docker y Docker Compose

### 1. Base de datos con Docker

```bash
cd backend
docker-compose up -d
```

Esto levanta una instancia de PostgreSQL 16 lista para usar.

### 2. Backend

```bash
cd backend
./mvnw spring-boot:run
```

El servidor arranca en `http://localhost:8080`.  
La documentación Swagger está disponible en `http://localhost:8080/swagger-ui.html`.

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

El cliente arranca en `http://localhost:5173`.

---

## 🔌 Endpoints principales

### Auth
| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/auth/login` | Login, retorna JWT |

### Users
| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/users` | Registro de usuario |
| `GET` | `/users/me` | Perfil del usuario autenticado |
| `POST` | `/users/send-verification` | Envía PIN de verificación al email |
| `POST` | `/users/verify-pin` | Verifica el PIN |
| `POST` | `/users/forgot-password/send-verification` | Envía PIN para recuperar contraseña |
| `POST` | `/users/forgot-password/reset` | Resetea la contraseña |
| `POST` | `/users/check-availability` | Verifica disponibilidad de email/DNI |

### Wallet
| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/wallet/me` | Saldo y datos de la billetera |
| `POST` | `/wallet/deposit` | Depositar fondos |
| `POST` | `/wallet/withdraw` | Retirar fondos |
| `PATCH` | `/wallet/alias` | Actualizar alias |

### Transactions
| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/transactions/transfer` | Transferir a otro usuario (CBU o alias) |
| `GET` | `/transactions/history` | Historial paginado (con filtro por tipo) |

---

## 🧪 Tests

```bash
cd backend
./mvnw test
```

El proyecto incluye tests de integración y unitarios para los módulos de autenticación, usuarios, billetera y transacciones. La cobertura se genera con JaCoCo en `target/site/jacoco`.

---

## 🌐 Deploy

| Capa | Plataforma | URL |
|---|---|---|
| Frontend | [Vercel](https://vercel.com) | https://nova-wallet-mu.vercel.app |
| Backend | [Render](https://render.com) | https://wallet-backend-9cm4.onrender.com |

El pipeline de CI/CD corre los tests automáticamente en cada push a `main`. Si pasan, se dispara el deploy al backend en Render via webhook.

---

## 👨‍💻 Autor

**Mateo Calcagno**  
Analista en Computación

---

## 📄 Licencia

MIT
