# ShaadiSeva Hyderabad

Wedding event management platform – connects customers with verified vendors in Hyderabad.

## Architecture

```
shaadiseva-hyderabad/
├── backend/        # Spring Boot 3 (Java 21) – REST API
├── frontend/       # React + Vite + TypeScript – SPA
├── docker-compose.yml
└── .env.example
```

## Local Development

### Prerequisites

| Tool | Version |
|------|---------|
| Java | 21 |
| Maven | 3.9+ |
| Node.js | 20+ |
| Docker + Docker Compose | v2+ |

---

### 1. Start infrastructure (Postgres + MinIO)

```bash
# Copy and fill in environment variables
cp .env.example .env
# At minimum set APP_ADMIN_PASSWORD (min 12 characters)

# Start services
docker compose up -d

# Verify services are healthy
docker compose ps
```

Exposed ports:
- Postgres: `localhost:5432`
- MinIO API: `localhost:9000`
- MinIO Console: `localhost:9001` (login with MINIO_ACCESS_KEY / MINIO_SECRET_KEY)

---

### 2. Create MinIO bucket

After MinIO starts, create the storage bucket using the MinIO client (`mc`) or the web console.

**Option A – web console**
1. Open http://localhost:9001
2. Login with `minioadmin` / `minioadmin` (or your configured credentials)
3. Create bucket named `shaadiseva-docs` (or the value of `MINIO_BUCKET`)

**Option B – mc CLI**
```bash
mc alias set local http://localhost:9000 minioadmin minioadmin
mc mb local/shaadiseva-docs
```

---

### 3. Start the backend

```bash
cd backend

# Export all env vars from your .env file (or use a tool like direnv)
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=shaadiseva
export DB_USER=shaadiseva
export DB_PASSWORD=shaadiseva
export MINIO_ENDPOINT=http://localhost:9000
export MINIO_ACCESS_KEY=minioadmin
export MINIO_SECRET_KEY=minioadmin
export MINIO_BUCKET=shaadiseva-docs
export JWT_SECRET=change-me-to-a-long-random-secret-at-least-64-chars-long
export APP_ADMIN_PASSWORD=YourSecureAdminPassword123!

mvn spring-boot:run
```

The backend starts on **http://localhost:8080**.

Flyway migrations run automatically on startup:
- `V1` – Core schema (users, vendors, bookings, etc.)
- `V2` – OTP schema
- `V3` – Seed categories

The admin user (`admin@shaadiseva.com`) is created/updated on first startup using `APP_ADMIN_PASSWORD`.  
**The application will refuse to start if `APP_ADMIN_PASSWORD` is blank or missing.**

---

### 4. Start the frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend starts on **http://localhost:5173** and proxies `/api` requests to the backend.

---

## API Overview

### Auth endpoints (public)

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/auth/login` | Password login (vendor / admin). Body: `{ username, password }` |
| `POST` | `/api/auth/otp/send` | Send OTP to customer phone. Body: `{ phone }` |
| `POST` | `/api/auth/otp/verify` | Verify OTP and issue JWT. Body: `{ phone, otp }` |

> **Note:** OTP is logged to backend stdout only (mock implementation). Check the backend console for the OTP value during development.

### Vendor endpoints (VENDOR / ADMIN role)

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/vendor/applications` | Apply as vendor (public) |
| `POST` | `/api/vendor/applications/{id}/documents` | Upload document (multipart: `docType` + `file`) |
| `GET`  | `/api/vendor/documents/{id}/download` | Download/proxy document from MinIO |

### Admin endpoints (ADMIN role)

| Method | Path | Description |
|--------|------|-------------|
| `GET`  | `/api/admin/vendor-applications` | List pending applications |
| `POST` | `/api/admin/vendor-applications/{id}/approve` | Approve application (validates mandatory docs) |
| `POST` | `/api/admin/vendor-applications/{id}/reject` | Reject application. Body: `{ reason }` |

Mandatory documents required for approval: `GST`, `PAN`, `AADHAAR`, `BUSINESS_REG`, `ADDRESS_PROOF`

---

## Environment Variables

See [`.env.example`](.env.example) for all available environment variables and their defaults.

---

## Frontend Pages

| Path | Description |
|------|-------------|
| `/customer/login` | Customer OTP login (2-step) |
| `/vendor/apply` | Vendor registration form |
| `/vendor/login` | Vendor password login |
| `/admin/login` | Admin password login |
| `/customer/dashboard` | Customer placeholder dashboard |
| `/vendor/dashboard` | Vendor placeholder dashboard |
| `/admin/dashboard` | Admin placeholder dashboard |