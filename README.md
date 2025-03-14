# 🔧 MechanicReviewBackEnd

## 📋 Overview

MechanicReviewBackEnd is a Kotlin-based backend application that provides API services for a mechanic review platform. Users can search for mechanics, read reviews, and share their own experiences, helping others make informed decisions about automotive services.

## 🌟 Features

- 👤 **User Authentication**: Secure JWT-based authentication system
- 🔍 **Mechanic Search**: Find mechanics by name, location, or specialties
- ⭐ **Review System**: Rate and review mechanics with detailed feedback
- 📊 **Rating Analytics**: View aggregate ratings and statistics for mechanics
- 🔒 **Security**: Encrypted passwords and protected endpoints

## 🛠️ Tech Stack

- **Framework**: [Ktor](https://ktor.io/) - Lightweight Kotlin web framework
- **Database**: H2 with [Exposed](https://github.com/JetBrains/Exposed) ORM
- **Authentication**: JWT (JSON Web Tokens)
- **Connection Pooling**: HikariCP
- **Password Security**: BCrypt
- **Serialization**: Kotlin Serialization
- **Logging**: Logback

## 🏗️ Architecture

The application follows a clean architecture pattern with clear separation of concerns:

```
src/main/kotlin/
├── database/           # Database configuration and table definitions
├── models/             # Data classes for the domain
├── plugins/            # Ktor plugins and route configurations
├── repositories/       # Data access layer
├── services/           # Business logic and authentication services
└── Main.kt             # Application entry point
```

## 🚀 Getting Started

### Prerequisites

- JDK 17 or higher
- Gradle (wrapper included)

### Running the Application

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/MechanicReviewBackEnd.git
   cd MechanicReviewBackEnd
   ```

2. Build and run the application:
   ```bash
   ./gradlew run
   ```

3. The server will start on `http://localhost:8080`

## 📡 API Endpoints

### Authentication

- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - User login
- `GET /api/auth/validate` - Validate JWT token

### Mechanics

- `POST /api/mechanics` - Add a new mechanic (authenticated)
- `GET /api/mechanics/{id}` - Get a specific mechanic's details
- `GET /api/mechanics` - Search mechanics with filters

### Reviews

- `POST /api/reviews` - Create a new review (authenticated)
- `GET /api/reviews/{id}` - Get a specific review
- `GET /api/reviews/mechanic/{mechanicId}` - Get all reviews for a mechanic
- `GET /api/reviews/user` - Get all reviews by the authenticated user

## 🔐 Security

- All passwords are hashed using BCrypt
- JWT tokens with configurable expiration
- Protected routes using authentication middleware
- CORS configuration for frontend access

## 📊 Data Models

### User
Stores user account information with secure password hashing.

### Mechanic
Contains details about automotive service providers including location, contact information, and specialties.

### Review
Captures user feedback with ratings for different aspects of service (quality, price, customer service).

## 🚧 Development

### Building

```bash
./gradlew build
```

### Testing

```bash
./gradlew test
```

## 📝 License

[MIT License](LICENSE)

## 📞 Contact

For questions or feedback, please reach out to [tamerthedark@hotmail.com](mailto:tamerthedark@hotmail.com)
