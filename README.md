#  WaterAPP Backend – API Documentation (v1.0)

**Backend:** Spring Boot (Java)  
**Auth:** JWT  
**Base Path:** `/api/*`

This document contains the complete API reference for **WaterAPP**, including:

- User Authentication  
- Water Sensor Data  
- Pollution Reports  
- Community Forum  
- Auto Alert System  
- Global Status Codes & Headers  

---

##  Table of Contents
- [1. User API (`/api/users`)](#1-user-api)
- [2. Sensor API (`/api/sensor`)](#2-sensor-api)
- [3. Pollution Report API (`/api/reports`)](#3-pollution-report-api)
- [4. Community API (`/api/community`)](#4-community-api)
- [5. Alert API (`/api/alerts`)](#5-alert-api)
- [6. Global Headers](#6-global-headers)
- [7. Global Status Codes](#7-global-status-codes)
- [8. Base URL](#8-base-url)

---

#  1. User API (`/api/users`)
## 1.1 Register  
### `POST /api/users/register`
Create a new user.

#### Request Body
```json
{
  "name": "Yan Han",
  "email": "yan@example.com",
  "password": "123456",
  "role": "USER"
}
#### Response
{
  "id": 1,
  "name": "Yan Han",
  "email": "yan@example.com",
  "role": "USER",
  "createdAt": "2025-01-02T10:00:00"
}
