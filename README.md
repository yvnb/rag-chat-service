# RAG Chat Storage Microservice

A production-ready backend microservice for storing and managing chat histories from RAG (Retrieval-Augmented Generation) based chatbot systems.

---

## Features
- Create, manage, and delete chat sessions
- Store messages with sender, content, and optional context
- Rename sessions, mark/unmark favorites
- API key authentication with rate limiting
- Centralized logging and global error handling
- Health check endpoints with monitoring
- Swagger/OpenAPI documentation
- Dockerized with PostgreSQL, pgAdmin, and Redis

---

## Tech Stack
- **Spring Boot 3.1.5 + Java 17**
- **PostgreSQL 15**
- **pgAdmin 4**
- **Redis 7**
- **Docker & Docker Compose**

---

## Setup

### 1. Clone repo & build
```bash
git clone <your-repo>
cd rag-chat-storage
./mvnw clean package -DskipTests

# Copy environment file
cp .env.example .env

# Update your API key and passwords in .env
nano .env
```

### 2. Start services
```bash
docker-compose up --build
```

### 3. Verify installation
```bash
# Check health
curl http://localhost:8080/actuator/health

# Expected response: {"status":"UP"}
```

---

## Access

| Service | URL | Credentials |
|---------|-----|-------------|
| **Spring Boot API** | http://localhost:8080 | API Key required |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | API Key required |
| **pgAdmin** | http://localhost:8081 | admin@ragchat.com / adminpassword123 |
| **PostgreSQL** | localhost:5432 | ragchat / securepassword123 |
| **Redis** | localhost:6379 | Password: redispassword123 |

> **Note**: Update credentials in `.env` file before running in production

---

## API Documentation

### Authentication
All endpoints require API key header:
```bash
X-API-Key: your-secret-api-key-here
```

### Core Endpoints

#### Session Management
```bash
# Create session
POST /api/v1/sessions
{
  "title": "My Chat Session",
  "userId": "user123"
}

# Get user sessions
GET /api/v1/sessions?userId=user123&page=0&size=10

# Rename session
PUT /api/v1/sessions/{sessionId}/title
{
  "title": "Updated Title"
}

# Toggle favorite
POST /api/v1/sessions/{sessionId}/favorite

# Delete session
DELETE /api/v1/sessions/{sessionId}
```

#### Message Management
```bash
# Add message
POST /api/v1/sessions/{sessionId}/messages
{
  "sender": "USER",
  "content": "Hello, how are you?",
  "context": "Optional RAG context"
}

# Get messages
GET /api/v1/sessions/{sessionId}/messages?page=0&size=50
```

#### Health & Monitoring
```bash
GET /actuator/health     # Health check
GET /actuator/info       # App info
GET /actuator/metrics    # Metrics
```

---

## Example Usage

```bash
# Set API key
export API_KEY="your-secret-api-key-here"

# Create session
curl -X POST http://localhost:8080/api/v1/sessions \
  -H "Content-Type: application/json" \
  -H "X-API-Key: $API_KEY" \
  -d '{
    "title": "My First Chat",
    "userId": "user123"
  }'

# Add message
curl -X POST http://localhost:8080/api/v1/sessions/{session-id}/messages \
  -H "Content-Type: application/json" \
  -H "X-API-Key: $API_KEY" \
  -d '{
    "sender": "USER",
    "content": "Hello AI!",
    "context": "Initial greeting"
  }'
```

---

## Database Management

### pgAdmin Setup
1. Access pgAdmin at http://localhost:8081
2. Login with credentials from table above
3. Add server:
    - **Host**: `postgres`
    - **Port**: `5432`
    - **Database**: `rag_chat_db`
    - **Username**: `ragchat`
    - **Password**: `securepassword123`

### Direct Database Access
```bash
# Via Docker
docker exec -it rag-chat-postgres psql -U ragchat -d rag_chat_db

# From host (if psql installed)
psql -h localhost -p 5432 -U ragchat -d rag_chat_db
```

---

## Configuration

### Environment Variables (.env)
```bash
# Database
DB_NAME=rag_chat_db
DB_USERNAME=ragchat
DB_PASSWORD=securepassword123

# Redis
REDIS_PASSWORD=redispassword123

# Application
API_KEY=your-secret-api-key-here
RATE_LIMIT_REQUESTS=100
RATE_LIMIT_WINDOW=3600

# Logging
LOG_LEVEL=INFO
SHOW_SQL=false
```

### Rate Limiting
- **Default**: 100 requests/hour per API key
- **Storage**: Redis-backed for distributed limiting
- **Configurable**: Via `RATE_LIMIT_*` environment variables

---

## Development

### Local Development
```bash
# Start only database services
docker-compose up -d postgres redis pgadmin

# Run Spring Boot locally
./mvnw spring-boot:run
```

### Testing
```bash
# Run unit tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report

# Integration tests with TestContainers
./mvnw verify
```

---

## Production Deployment

### Security Checklist
- [ ] Change default API keys and passwords
- [ ] Set `SPRING_JPA_HIBERNATE_DDL_AUTO=validate`
- [ ] Configure SSL/TLS certificates
- [ ] Set up proper logging levels
- [ ] Configure firewall rules
- [ ] Enable Redis authentication

### Docker Production
```bash
# Build production image
docker build -t rag-chat-storage:latest .

# Run with production config
docker run -d \
  --name rag-chat-app \
  --env-file .env.prod \
  -p 8080:8080 \
  rag-chat-storage:latest
```

---

## Monitoring & Logs

### Application Logs
```bash
# Real-time logs
docker-compose logs -f app

# Service-specific logs
docker-compose logs -f postgres
docker-compose logs -f redis
```

### Health Monitoring
```bash
# Service status
docker-compose ps

# Detailed health
curl -s http://localhost:8080/actuator/health | jq .
```

---

## Troubleshooting

### Common Issues

**Service won't start**
```bash
# Check logs
docker-compose logs app

# Verify database
docker-compose exec app pg_isready -h postgres -p 5432
```

**Database connection failed**
```bash
# Reset volumes
docker-compose down -v
docker-compose up -d postgres
```

**API returns 401 Unauthorized**
- Verify `X-API-Key` header is present
- Check API key matches `.env` file

**Rate limit exceeded**
```bash
# Check Redis connection
docker-compose exec redis redis-cli ping
```

---

## API Limits

| Category | Limit     | Window |
|----------|-----------|---------|
| Session Operations | 5/min     | Per API key |
| Message Operations | 5/min  | Per API key |
| Health Checks | Unlimited | - |


## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.