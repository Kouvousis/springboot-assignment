# Spring Boot REST API - User Authentication & Order Management

A REST API built with Spring Boot that provides user authentication using JWT tokens and order management functionality.

## Technologies Used

- Java 17 (Amazon Corretto)
- Spring Boot 4.0.1
- Spring Security with JWT
- Hibernate/JPA
- MySQL Database
- Gradle
- Lombok (for reducing boilerplate code)
- Jakarta Validation (for request validation)
- JJWT (JSON Web Token) library version 0.12.6

## Key Dependencies

- `spring-boot-starter-data-jpa` - Database access with JPA/Hibernate
- `spring-boot-starter-security` - Spring Security framework
- `spring-boot-starter-web` - RESTful web services
- `spring-boot-starter-validation` - Bean validation
- `mysql-connector-j` - MySQL database driver
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson` - JWT token generation and validation
- `lombok` - Code generation for getters, setters, constructors

## Git Repository Setup

### Cloning the Repository

If you received this project as a Git repository, clone it using:

```bash
git clone 
cd qnr-assignment
```

## Prerequisites

Before running this application, ensure you have the following installed:

- Java 17 (Amazon Corretto 17)
- MySQL Server (8.0 or higher)
- Gradle (or use the included Gradle wrapper)
- Postman (for testing API endpoints)

## Database Setup

1. Install and start MySQL Server on your local machine.

2. The application will automatically create the database `assignmentdb` when it starts, but you can also create it manually:

```sql
CREATE DATABASE assignmentdb;
```

3. Create a MySQL user or use an existing one with appropriate permissions.

## Configuration

1. Navigate to `src/main/resources/application.properties`

2. Update the following properties with your MySQL credentials:

```properties
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

3. Update the JWT secret key (use a strong, random string):

```properties
jwt.secret-key=YOUR_SECRET_KEY
```

Example of a secure secret key:
```
jwt.secret-key=mySecretKey12345SuperSecureRandomString!@#$%
```

Note: The JWT token expiration is set to 1 hour (3600000 milliseconds). Modify if needed.

## Running the Application

### Option 1: Using Gradle Wrapper (Recommended)

1. Open a terminal in the project root directory.

2. Build the project:

```bash
./gradlew build
```

For Windows:
```bash
gradlew.bat build
```

3. Run the application:

```bash
./gradlew bootRun
```

For Windows:
```bash
gradlew.bat bootRun
```

### Option 2: Using Gradle Installed Globally

1. Build the project:

```bash
gradle build
```

2. Run the application:

```bash
gradle bootRun
```

## Verifying the Application

Once the application starts successfully, you should see output similar to:

```
Started Application in X.XXX seconds
```

The application will run on `http://localhost:8080` by default.

## API Endpoints

### Authentication Endpoints

- **POST** `/api/auth/register` - Register a new user and receive JWT token
- **POST** `/api/auth/login` - Login and receive JWT token
- **POST** `/api/auth/logout` - Logout and blacklist token (requires Authorization header)

### Order Endpoints

- **POST** `/api/orders` - Create a new order (authenticated users only)
- **GET** `/api/orders` - Get all user orders with pagination and sorting
  - Query params: `page` (default: 0), `size` (default: 10), `sortBy` (default: createdAt), `direction` (default: desc)
- **GET** `/api/orders/{id}` - Get order by ID (user can only access their own orders)
- **PUT** `/api/orders/{id}` - Update an order (user can only update their own orders)
- **DELETE** `/api/orders/{id}` - Delete an order (user can only delete their own orders)
- **GET** `/api/orders/status/{status}` - Get orders filtered by status with pagination
  - Query params: `page` (default: 0), `size` (default: 10)
- **GET** `/api/orders/search` - Search orders by query string with pagination
  - Query params: `query` (required), `page` (default: 0), `size` (default: 10)

## Sample Request/Response Examples

### Register User

**Request:**
```json
POST /api/auth/register
{
  "username": "john_doe",
  "password": "SecurePassword123",
  "role": "USER"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john_doe"
}
```

### Login

**Request:**
```json
POST /api/auth/login
{
  "username": "john_doe",
  "password": "SecurePassword123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john_doe"
}
```

### Create Order

**Request:**
```json
POST /api/orders
Authorization: Bearer YOUR_JWT_TOKEN

{
  "description": "Order for office supplies",
  "status": "PENDING"
}
```

**Response:**
```json
{
  "id": 1,
  "description": "Order for office supplies",
  "status": "PENDING",
  "createdAt": "2026-01-19T10:30:00"
}
```

### Get Orders with Pagination

**Request:**
```
GET /api/orders?page=0&size=10&sortBy=createdAt&direction=desc
Authorization: Bearer YOUR_JWT_TOKEN
```

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "description": "Order for office supplies",
      "status": "PENDING",
      "createdAt": "2026-01-19T10:30:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1
}
```

## Using Postman

1. Import the provided Postman collection file into Postman.

2. For authentication endpoints, use the responses to obtain JWT tokens.

3. For protected endpoints (orders), add the JWT token to the request headers:
   - Key: `Authorization`
   - Value: `Bearer YOUR_JWT_TOKEN`

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── org/example/qnrassignment/
│   │       ├── config/
│   │       │   └── SecurityConfig.java
│   │       ├── controller/
│   │       │   ├── AuthenticationController.java
│   │       │   └── OrderController.java
│   │       ├── core/
│   │       │   ├── enums/
│   │       │   │   └── Role.java
│   │       │   └── exceptions/
│   │       │       ├── GlobalExceptionHandler.java
│   │       │       └── ResourceNotFoundException.java
│   │       ├── dto/
│   │       │   ├── AuthenticationDTO.java
│   │       │   ├── CreateOrderDTO.java
│   │       │   ├── LoginDTO.java
│   │       │   ├── LogoutDTO.java
│   │       │   ├── OrderDTO.java
│   │       │   ├── RegisterDTO.java
│   │       │   └── UpdateOrderDTO.java
│   │       ├── model/
│   │       │   ├── AbstractEntity.java
│   │       │   ├── BlacklistedToken.java
│   │       │   ├── Order.java
│   │       │   └── User.java
│   │       ├── repository/
│   │       │   ├── BlacklistTokenRepo.java
│   │       │   ├── OrderRepository.java
│   │       │   └── UserRepository.java
│   │       ├── security/
│   │       │   ├── JwtAuthenticationFilter.java
│   │       │   └── JwtService.java
│   │       ├── service/
│   │       │   ├── AuthService.java
│   │       │   ├── CustomUserDetailsService.java
│   │       │   ├── OrderService.java
│   │       │   └── TokenBlacklistService.java
│   │       └── QnrAssignmentApplication.java
│   └── resources/
│       └── application.properties
└── test/
```
