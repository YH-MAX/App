# 💧 SmartWater Backend

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen?logo=spring-boot" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Java-17-orange?logo=openjdk" alt="Java 17">
  <img src="https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql" alt="MySQL">
  <img src="https://img.shields.io/badge/JWT-Authentication-yellow?logo=json-web-tokens" alt="JWT">
  <img src="https://img.shields.io/badge/License-MIT-green" alt="License">
</p>

<p align="center">
  <b>A robust RESTful API backend for the SmartWater Monitoring System</b><br>
  Powering real-time water quality monitoring, alerts, and community engagement
</p>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Features](#-features)
- [API Endpoints](#-api-endpoints)
- [Getting Started](#-getting-started)
- [Configuration](#-configuration)
- [Project Structure](#-project-structure)
- [Demo Walkthrough](#-demo-walkthrough)

---

## 🌊 Overview

SmartWater Backend is the core API service for our water quality monitoring platform. It provides:

- **User Authentication** with JWT tokens and email verification
- **Real-time Sensor Data** integration with FastAPI IoT service
- **Alert System** for water quality threshold monitoring
- **Community Platform** for users to share reports and updates
- **Pollution Reporting** with status tracking

The backend serves both our **Android mobile app** and integrates with a **FastAPI sensor data service** for IoT device communication.

---

## 🏗 Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           Mobile App (Android)                         │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │ HTTPS/REST
                                     ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                        SmartWater Backend (Spring Boot)                │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐   │
│  │   User      │  │   Sensor    │  │  Community  │  │   Alert     │   │
│  │ Controller  │  │ Controller  │  │ Controller  │  │ Controller  │   │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘   │
│         │                │                │                │          │
│  ┌──────▼──────────────▼────────────────▼────────────────▼──────┐   │
│  │                     Service Layer                              │   │
│  │  • UserService    • FastApiClient    • CommunityService       │   │
│  │  • EmailService   • AlertService     • FollowService          │   │
│  └───────────────────────────┬────────────────────────────────────┘   │
│                              │                                         │
│  ┌───────────────────────────▼───────────────────────────────────┐   │
│  │                    Security Layer (JWT)                        │   │
│  │  • JwtAuthenticationFilter  • BCrypt Password Encoding         │   │
│  └────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────┘
            │                              │
            ▼                              ▼
┌───────────────────────┐     ┌───────────────────────────────────┐
│    MySQL Database     │     │  FastAPI Sensor Service (Python)  │
│  • Users              │     │  • Real-time sensor data          │
│  • Posts, Replies     │     │  • Device management              │
│  • Alerts, Reports    │     │  • Time-series data               │
└───────────────────────┘     └───────────────────────────────────┘
```

---

## 🛠 Tech Stack

| Technology | Purpose |
|------------|---------|
| **Spring Boot 3.5.7** | Core framework |
| **Spring Security** | Authentication & Authorization |
| **Spring Data JPA** | Database ORM |
| **JWT (jjwt 0.11.5)** | Token-based authentication |
| **MySQL 8.0** | Primary database |
| **Spring Mail** | Email verification & password reset |
| **WebFlux/WebClient** | Async HTTP calls to FastAPI |
| **Lombok** | Reduce boilerplate code |
| **Gradle** | Build tool |

---

## ✨ Features

### 🔐 User Authentication & Management
- JWT-based stateless authentication
- Email verification with token
- Password reset via email
- User profile management (avatar, bio, location)
- BCrypt password encryption

### 📊 Sensor Data Integration
- Proxy to FastAPI sensor service
- Real-time device data (`/device/{deviceId}/latest`)
- Time-range queries (`/me/range`)
- Water quality summary statistics

### 🚨 Alert System
- Automatic alert generation based on thresholds
- pH level monitoring (critical: < 6.0 or > 8.5)
- Temperature monitoring (warning: > 30°C)
- Severity levels: LOW, MEDIUM, HIGH
- User alert history

### 🗣 Community Platform (Twitter-like)
- Create posts with location tags
- Like, Bookmark, Retweet functionality
- Quote tweets with comments
- Reply to posts
- Search posts
- User feed with pagination

### 👥 Social Features
- Follow/Unfollow users
- Followers & Following lists
- User profile pages
- Pagination support

### 📝 Pollution Reporting
- Submit pollution reports with photos
- Location-based reporting
- Admin status updates (PENDING → RESOLVED)
- Auto-generated reports on HIGH severity alerts

### 📶 Bluetooth Device Management
- Pair IoT sensor devices
- Connection status tracking
- Device list per user

### 🔗 FastAPI Gateway Proxy
- Direct proxy to FastAPI sensor service
- Latest readings endpoint
- Historical data with range queries

---

## 📡 API Endpoints

### Authentication (`/api/users`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/register` | Register new user | ❌ |
| POST | `/login` | Login & get JWT | ❌ |
| POST | `/logout` | Logout | ✅ |
| GET | `/verify-email?token=xxx` | Verify email | ❌ |
| POST | `/forgot-password` | Request password reset | ❌ |
| POST | `/reset-password` | Reset password | ❌ |
| GET | `/me` | Get current user profile | ✅ |
| PUT | `/me` | Update profile | ✅ |

### Sensor Data (`/api/sensor`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/device/{deviceId}/latest` | Get latest device reading | ❌ |
| GET | `/me/latest` | Get user's latest reading | ✅ |
| GET | `/me/range?from=&to=` | Get readings in time range | ✅ |
| GET | `/me/summary?from=&to=` | Get water quality summary | ✅ |
| POST | `/upload` | Upload sensor data | ✅ |

### Alerts (`/api/alerts`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/evaluate` | Evaluate water reading | ✅ |
| GET | `/me` | Get user's alert history | ✅ |
| GET | `/` | Get all alerts (admin) | ✅ |

### Community (`/api/community`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/posts` | Create a post | ✅ |
| GET | `/posts` | Get feed (paginated) | ✅ |
| GET | `/posts/{id}` | Get post by ID | ✅ |
| POST | `/posts/{id}/like` | Toggle like | ✅ |
| POST | `/posts/{id}/bookmark` | Toggle bookmark | ✅ |
| POST | `/posts/{id}/retweet` | Retweet | ✅ |
| DELETE | `/posts/{id}/retweet` | Undo retweet | ✅ |
| POST | `/posts/{id}/quote` | Quote tweet | ✅ |
| POST | `/posts/{id}/replies` | Add reply | ✅ |
| GET | `/posts/{id}/replies` | Get replies | ✅ |
| GET | `/search?query=xxx` | Search posts | ✅ |
| GET | `/bookmarks` | Get bookmarks | ✅ |
| GET | `/likes` | Get liked posts | ✅ |

### Reports (`/api/reports`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/` | Create pollution report | ✅ |
| GET | `/me` | Get my reports | ✅ |
| GET | `/` | Get all reports | ✅ |
| PUT | `/{id}/status` | Update status (admin) | ✅ |

### Social (`/api/users`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/{userId}/follow` | Follow user | ✅ |
| DELETE | `/{userId}/follow` | Unfollow user | ✅ |
| GET | `/{userId}/is-following` | Check if following | ✅ |
| GET | `/{userId}/followers` | Get followers | ✅ |
| GET | `/{userId}/following` | Get following | ✅ |
| GET | `/{userId}/profile` | Get user profile | ✅ |
| GET | `/me/profile` | Get my profile | ✅ |

### Bluetooth Devices (`/api/bluetooth`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/pair` | Pair a new device | ✅ |
| POST | `/status` | Update connection status | ✅ |
| GET | `/me/devices` | Get user's paired devices | ✅ |

### Water Gateway (`/api/water`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/latest` | Get latest reading (proxy) | ❌ |
| GET | `/history?range=&value=` | Get history (proxy) | ❌ |
| GET | `/test-connection` | Test FastAPI connection | ❌ |

---

## 🚀 Getting Started

### Prerequisites

- **Java 17** or higher
- **MySQL 8.0** database
- **Gradle** (wrapper included)

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/SmartWaterBackend.git
cd SmartWaterBackend
```

### 2. Configure Database

Create a MySQL database:

```sql
CREATE DATABASE smartwater_db;
```

### 3. Update Configuration

Edit `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/smartwater_db
spring.datasource.username=root
spring.datasource.password=your_password

# JWT Secret (change in production!)
jwt.secret=your-secure-secret-key-here

# Email (for verification)
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

### 4. Run the Application

```bash
# Windows
./gradlew.bat bootRun

# macOS/Linux
./gradlew bootRun
```

The server will start at `http://localhost:8080`

### 5. Verify Installation

```bash
curl http://localhost:8080/actuator/health
# Response: {"status":"UP"}
```

---

## ⚙️ Configuration

### Key Properties

| Property | Description | Default |
|----------|-------------|---------|
| `server.port` | Server port | 8080 |
| `jwt.secret` | JWT signing key | (required) |
| `jwt.expiration` | Token expiry (ms) | 86400000 (24h) |
| `fastapi.base-url` | FastAPI sensor service | http://localhost:8888 |
| `spring.jpa.hibernate.ddl-auto` | Schema generation | update |

### Email Configuration

For Gmail, use an [App Password](https://support.google.com/accounts/answer/185833):

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-16-char-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

## 📁 Project Structure

```
src/main/java/com/smartwater/backend/
├── SmartWaterBackendApplication.java      # Main Spring Boot entry point
│
├── 📦 config/ (4 files)
│   ├── SecurityConfig.java               # Spring Security + JWT config
│   ├── CorsConfig.java                   # CORS configuration
│   ├── FastApiClientConfig.java          # WebClient for FastAPI
│   └── FastApiProperties.java            # FastAPI URL properties
│
├── 🎮 controller/ (8 files)
│   ├── UserController.java               # Auth, registration, profile
│   ├── SensorController.java             # Sensor data (proxy to FastAPI)
│   ├── CommunityController.java          # Posts, likes, bookmarks, replies
│   ├── AlertController.java              # Alert evaluation & history
│   ├── FollowController.java             # Follow/unfollow, followers list
│   ├── PollutionReportController.java    # Pollution report CRUD
│   ├── BluetoothController.java          # IoT device pairing
│   └── WaterProxyController.java         # FastAPI gateway proxy
│
├── 📋 dto/ (20 files)
│   ├── AlertResponse.java
│   ├── BluetoothConnectionStatusRequest.java
│   ├── BluetoothDeviceResponse.java
│   ├── BluetoothPairRequest.java
│   ├── CommunityPostRequest.java
│   ├── CommunityPostResponse.java
│   ├── CommunityReplyRequest.java
│   ├── CommunityReplyResponse.java
│   ├── CreateReportRequest.java
│   ├── PageResponse.java
│   ├── PollutionReportResponse.java
│   ├── ResendVerificationRequest.java
│   ├── SensorDataRequest.java
│   ├── SensorDataResponse.java
│   ├── UpdateReportStatusRequest.java
│   ├── UserProfileResponse.java
│   ├── UserUpdateRequest.java
│   ├── WaterIngestRequest.java
│   ├── WaterQualitySummaryResponse.java
│   └── WaterReadingRequest.java
│
├── ⚠️ exception/ (4 files)
│   ├── ApiError.java                     # Standard error response
│   ├── BadRequestException.java          # 400 errors
│   ├── NotFoundException.java            # 404 errors
│   └── GlobalExceptionHandler.java       # @ControllerAdvice handler
│
├── 🔗 integration/ (1 file)
│   └── FastApiSensorClient.java          # Legacy FastAPI client
│
├── 🗃️ model/ (15 JPA Entities)
│   ├── User.java                         # User account
│   ├── CommunityPost.java                # Community posts
│   ├── CommunityReply.java               # Post replies
│   ├── PostLike.java                     # Like records
│   ├── PostBookmark.java                 # Bookmark records
│   ├── PostRetweet.java                  # Retweet records
│   ├── UserFollow.java                   # Follow relationships
│   ├── Alert.java                        # Alert history
│   ├── PollutionReport.java              # Pollution reports
│   ├── BluetoothDevice.java              # Paired IoT devices
│   ├── BluetoothConnectionStatus.java    # Connection status enum
│   ├── SensorData.java                   # Sensor readings
│   ├── EmailVerificationToken.java       # Email verification
│   ├── PasswordResetToken.java           # Password reset
│   └── WaterQualityStatus.java           # Quality status enum
│
├── 📚 repository/ (13 JPA Repositories)
│   ├── UserRepository.java
│   ├── CommunityPostRepository.java
│   ├── CommunityReplyRepository.java
│   ├── PostLikeRepository.java
│   ├── PostBookmarkRepository.java
│   ├── PostRetweetRepository.java
│   ├── UserFollowRepository.java
│   ├── AlertRepository.java
│   ├── PollutionReportRepository.java
│   ├── BluetoothDeviceRepository.java
│   ├── SensorDataRepository.java
│   ├── EmailVerificationTokenRepository.java
│   └── PasswordResetTokenRepository.java
│
├── 🔐 security/ (2 files)
│   ├── JwtUtil.java                      # JWT token generation/validation
│   └── JwtAuthenticationFilter.java      # Request authentication filter
│
└── ⚙️ service/ (12 files)
    ├── UserService.java                  # User CRUD, authentication
    ├── CommunityService.java             # Posts, likes, bookmarks, retweets
    ├── AlertService.java                 # Alert evaluation logic
    ├── FollowService.java                # Follow/unfollow logic
    ├── PollutionReportService.java       # Report management
    ├── BluetoothService.java             # Device pairing
    ├── EmailService.java                 # Send emails (SMTP)
    ├── EmailVerificationService.java     # Email verification tokens
    ├── PasswordResetService.java         # Password reset flow
    ├── FastApiSensorFacadeService.java   # WebClient calls to FastAPI
    ├── WaterProxyService.java            # RestTemplate proxy to FastAPI
    └── CustomUserDetailsService.java     # Spring Security UserDetails

src/main/resources/
├── application.properties                # Main configuration
└── templates/                            # Email templates (if any)
```

**Total: 79 Java files**

---

## 🎥 Demo Walkthrough

### For Video Demonstration

Here's a suggested flow for demonstrating the backend:

#### 1. **Authentication Flow** (2 min)
```bash
# Register a new user
POST /api/users/register
{
  "email": "demo@example.com",
  "password": "password123",
  "username": "DemoUser"
}

# Login and get JWT
POST /api/users/login
{
  "email": "demo@example.com",
  "password": "password123"
}
# Returns: { "token": "eyJhbG..." }
```

#### 2. **Sensor Data Integration** (2 min)
```bash
# Get latest device reading (no auth)
GET /api/sensor/device/WATER_001/latest

# Get user's readings with auth
GET /api/sensor/me/range?from=2026-01-01T00:00:00&to=2026-01-14T23:59:59
Authorization: Bearer <token>
```

#### 3. **Alert System** (2 min)
```bash
# Evaluate a water reading
POST /api/alerts/evaluate
Authorization: Bearer <token>
{
  "ph": 5.5,          # Critical: pH < 6.0
  "temperature": 35,   # Warning: temp > 30
  "turbidity": 10
}
# Returns alert with severity and message
```

#### 4. **Community Features** (3 min)
```bash
# Create a post
POST /api/community/posts
{
  "content": "Water quality excellent at City Lake! 💧",
  "location": "City Lake"
}

# Like a post
POST /api/community/posts/1/like

# Add a reply
POST /api/community/posts/1/replies
{
  "content": "Great news! Thanks for sharing!"
}
```

#### 5. **Social Features** (2 min)
```bash
# Follow a user
POST /api/users/2/follow

# Get followers
GET /api/users/1/followers

# Get user profile
GET /api/users/me/profile
```

---

## 📊 Database Schema

The system uses the following main tables:

- `users` - User accounts
- `community_posts` - Community posts
- `community_replies` - Post replies
- `post_likes` - Like records
- `post_bookmarks` - Bookmark records
- `post_retweets` - Retweet records
- `user_follows` - Follow relationships
- `alerts` - Alert history
- `pollution_reports` - Pollution reports
- `bluetooth_devices` - Paired IoT devices
- `sensor_data` - Sensor readings
- `email_verification_tokens` - Email verification
- `password_reset_tokens` - Password reset

---

## 🔒 Security Notes

- All passwords are hashed with **BCrypt**
- JWT tokens expire after **24 hours**
- CORS is configured for development (allow all origins)
- Sensitive endpoints require authentication
- Email verification is required for new accounts

---

## 📄 License

This project is licensed under the MIT License.

---

## 👨‍💻 Author

**SmartWater Team**

---

<p align="center">
  Made with ❤️ for cleaner water
</p>
