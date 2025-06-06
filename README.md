# User Management Application

A REST API application for managing users with full CRUD operations, built with Java/Spring Boot.

- ### JWT Authentication:
Secure authentication is implemented using JWT tokens, with each token having a validity of 1 hour. The tokens are required for accessing protected endpoints.

- ### Security Configuration:
All user-related endpoints are secured. Only the /api/v1/users/register and /api/v1/users/login endpoints are publicly accessible. All others require a valid JWT token.

- ### Validation:
Includes custom validation logic for phone numbers to ensure proper formatting and uniqueness in the system.

## Prerequisites

Before running the application, ensure you have the following installed:

### Required Software
- **Java 21** (as specified in the project)
- **Maven 3.8+**
- **MySQL 8.0+**
- **Git** for version control

### Verify Installation
```bash
# Check Java version
java -version

# Check Maven version
mvn -version

# Check MySQL installation
mysql --version
```

## Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/NikolayAngelov90/UserManagementApplication.git
cd UserManagementApplication
```

### 2. Database Setup

#### MySQL Setup:
```sql
-- Connect to MySQL as root
mysql -u root -p

-- The database will be created automatically by the application
-- due to the 'createDatabaseIfNotExist=true' parameter
-- But you can create it manually if preferred:
CREATE DATABASE `user-management-svc`;

-- Verify database creation
SHOW DATABASES;
USE `user-management-svc`;
```

**Note:** The application is configured to automatically create the database `user-management-svc` if it doesn't exist, so manual database creation is optional.

### 3. Configure Application Properties

Update `src/main/resources/application.properties` with your MySQL credentials:

```properties
spring.application.name=UserManagementApplication

# Database configuration
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/user-management-svc?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
logging.level.org.hibernate.persister.entity=ERROR

# JWT secret key (generate your own for production)
token.secret.key=YOUR_SECRET_KEY_HERE

# JWT expiration (1 hour = 3600000ms)
token.expirationms=3600000
```

**Important Security Notes:**
- Replace `YOUR_MYSQL_PASSWORD` with your actual MySQL root password
- Generate a new JWT secret key for production using: `node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"`
- Consider using environment variables for sensitive data in production

## Running the Application

A default admin user with email ```admin@admin.com``` and password ```password``` is created automatically upon application startup.

### Method 1: Using Maven (Recommended)
```bash
# Clean and install dependencies
mvn clean install

# Run the application
mvn spring-boot:run
```

### Method 2: Using JAR file
```bash
# Build JAR file
mvn clean package

# Run the JAR file
java -jar target/UserManagementApplication-0.0.1-SNAPSHOT.jar
```

### Method 3: Development Mode (with auto-reload)
```bash
# Run with Spring Boot DevTools for hot reload
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=dev"
```

### Verify Application is Running
- Open your browser and navigate to: `http://localhost:8080`
- Check application status: `http://localhost:8080/actuator/health` (if Spring Actuator is enabled)

## API Documentation

### Base URL
```
http://localhost:8080
```

### Authentication

This application uses JWT authentication. You need to authenticate before accessing user management endpoints.

#### Authentication Endpoints

##### 1. Register User
- **URL**: `POST /api/v1/register`
- **Content-Type**: `application/json`
- **Request Body**:
```json
{
  "username": "testuser",
  "password": "securepassword123",
  "email": "test@example.com"
}
```
- **Success Response**: `201 Created`
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

##### 2. Login User
- **URL**: `POST /api/v1/login`
- **Content-Type**: `application/json`
- **Request Body**:
```json
{
  "username": "testuser",
  "password": "securepassword123"
}
```
- **Success Response**: `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Protected Endpoints

**Important:** All user management endpoints below require authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### User Management Endpoints

### Endpoints

#### 1. Get All Users
- **URL**: `GET /api/v1/users`
- **Headers**: `Authorization: Bearer <jwt_token>`
- **Query Parameters**:
  - `search` (optional): Search term to filter users
- **Examples**:
  - `GET /api/users` - Get all users
  - `GET /api/users?sort=lastName` - Sort by last name
  - `GET /api/users?sort=dateOfBirth` - Sort by date of birth
  - `GET /api/users?search=john` - Search for users containing "john"
  - `GET /api/users?page=0&size=5` - Get first 5 users
- **Success Response**: `200 OK`

#### 2. Get User by EMAIL
- **URL**: `GET /api/v1/users/by-email`
- **Headers**: `Authorization: Bearer <jwt_token>`
- **Request Parameter**: `email` (String)

#### 3. Update User
- **URL**: `PATCH /api/v1/users/{userId}`
- **Headers**: `Authorization: Bearer <jwt_token>`
- **Path Parameters**: `id` (UUID) - User ID
- **Content-Type**: `application/json`
- **Request Body**: All fields are optional
```json
{
  "firstName": "John Updated",
  "lastName": "Doe Updated",
  "dateOfBirth": "1991-05-15",
  "phoneNumber": "+9876543210",
  "email": "john.updated@example.com"
}
```

#### 4. Delete User
- **URL**: `DELETE /api/v1/users/{userId}`
- **Headers**: `Authorization: Bearer <jwt_token>`
- **Path Parameters**: `id` (UUID) - User ID
- **Example**: `DELETE /api/v1/users/1`
- **Success Response**: `204 No Content`
