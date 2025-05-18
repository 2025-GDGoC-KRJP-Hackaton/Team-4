# Disaster Assistant for Blind

A web server that assists blind people during disasters using AI.

## Environment Setup

This project uses environment variables to manage sensitive configuration data. Follow these steps to set up your local environment:

### 1. Create a .env file

Copy the `.env.template` file to a new file named `.env` in the project root:

```bash
cp .env.template .env
```

### 2. Configure your .env file

Edit the `.env` file and fill in your actual values for each environment variable:

```
# Database Configuration
DB_URL=jdbc:mysql://127.0.0.1:3306/disaster_assistant?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

# Redis Configuration
REDIS_HOST=127.0.0.1
REDIS_PORT=6379


# AI Server Configuration
AI_SERVER_URL=http://127.0.0.1:8081/api/ai

# Disaster API Configuration
DISASTER_API_POLLING_INTERVAL=60000
DISASTER_API_URL=https://example-disaster-api.com/data

# Security Configuration
ADMIN_USERNAME=admin
ADMIN_PASSWORD=your_secure_password_here

# JWT Configuration
JWT_SECRET=your_secure_jwt_secret_key_here
JWT_EXPIRATION=86400000
```

### 3. Important Notes

- The `.env` file contains sensitive information and should **never be committed to git**. It's already added to `.gitignore`.
- The `application.properties` file is also excluded from git to prevent accidental commits of sensitive data.
- Use `application.properties.template` as a reference for the required properties.

## Running the Application

Once you've set up your environment variables, you can run the application using Maven:

```bash
./mvnw spring-boot:run
```

## Deployment

For deployment to production environments:

1. Set up environment variables on your server or container platform
2. Do not copy your local `.env` file to production servers
3. For containerized deployments, pass environment variables through your container orchestration platform

## Features

1. User registration and JWT-based authentication
2. Disaster detection via API polling
3. Real-time notifications via WebSockets
4. AI-powered guidance for blind users during disasters
5. Location and media sharing for emergency assistance

## Authentication

This application uses JWT (JSON Web Token) for authentication:

1. When a user logs in, they receive a JWT token
2. This token must be included in the Authorization header for subsequent requests
3. The token contains the user's identity and permissions
4. Tokens expire after a configurable period (default: 24 hours)

Example of using the token in API requests:

```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTYyMDY2NDcyMH0.example_token
```
