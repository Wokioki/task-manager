## Requirements

Before running the project, make sure you have installed:

- Java 17+
- Node.js
- npm
- Docker
- Git

## Environment Variables

### Frontend

Create a file:

```txt
client/.env
```

Use this content:

```env
REACT_APP_API_URL=http://localhost:8080/api
```

You can also check:

```txt
client/.env.example
```

### Backend

For local development, the backend connects to PostgreSQL on port `5433`.

Example values are available in:

```txt
server/.env.example
```

## Run Full Project with Docker

The project can be started with Docker Compose.

It starts:

- PostgreSQL database
- Spring Boot backend
- React frontend served by Nginx

From the project root folder:

```bash
docker compose up --build -d
```

After startup, the services will be available at:

```txt
Frontend: http://localhost:3000
Backend:  http://localhost:8080
Database: localhost:5433
```

Check running containers:

```bash
docker ps
```

Expected containers:

```txt
task-manager-frontend
task-manager-backend
task-manager-postgres
```

Stop all containers:

```bash
docker compose down
```

Stop containers and delete PostgreSQL data volume:

```bash
docker compose down -v
```

Warning: `docker compose down -v` removes database data.

## Docker Files

```txt
task-manager/
├── docker-compose.yml
├── client/
│   ├── Dockerfile
│   ├── .dockerignore
│   └── nginx.conf
└── server/
    ├── Dockerfile
    └── .dockerignore
```

## Local Development Without Docker

You can also run services manually.

### Run PostgreSQL only

From the project root:

```bash
docker compose up postgres -d
```

PostgreSQL will be available at:

```txt
localhost:5433
```

Database credentials:

```txt
Database: task_manager
User: task_user
Password: task_password
```

### Run Backend locally

From the `server` folder:

```bash
cd server
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
cd server
.\mvnw.cmd spring-boot:run
```

Backend runs at:

```txt
http://localhost:8080
```

### Run Frontend locally

From the `client` folder:

```bash
cd client
npm install
npm start
```

Frontend runs at:

```txt
http://localhost:3000
```

## API Endpoints

### Auth

```txt
POST /api/auth/register
POST /api/auth/login
```

### Tasks

All task endpoints require JWT token:

```http
Authorization: Bearer <token>
```

```txt
GET    /api/tasks
GET    /api/tasks/{id}
POST   /api/tasks
PUT    /api/tasks/{id}
DELETE /api/tasks/{id}
```

## Task Query Parameters

The task list supports pagination, search, sorting, and status filtering.

Example:

```txt
GET /api/tasks?page=0&size=5&sort=createdAt,desc
```

Search by title:

```txt
GET /api/tasks?q=jwt
```

Filter active tasks:

```txt
GET /api/tasks?done=false
```

Filter completed tasks:

```txt
GET /api/tasks?done=true
```

## Example Auth Request

### Register

```http
POST /api/auth/register
Content-Type: application/json
```

```json
{
  "username": "test",
  "email": "test@test.com",
  "password": "123456"
}
```

### Login

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "email": "test@test.com",
  "password": "123456"
}
```

## Example Task Request

```http
POST /api/tasks
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "title": "Learn JWT",
  "description": "Build protected task endpoints"
}
```

## Development Notes

This project is built as a learning full-stack application.

For local development:

- PostgreSQL runs in Docker
- Backend runs locally on port `8080`
- Frontend runs locally on port `3000`
- JWT token is stored in browser `localStorage`