# Assessment Tool Backend (Spring Boot + MySQL)

A robust REST API backend for the Assessment Tool built with **Java 21**, **Spring Boot 3**, **Spring Data JPA**, and **MySQL**.

---

## 🛠️ Tech Stack & Requirements
* **Java 21 (LTS)**
* **Spring Boot 3.3.4**
* **MySQL Server 8.0+**
* **Maven** (Supported by IntelliJ IDEA / STS / Eclipse out of the box)

---

## 🚀 Setup & Running Guide

### 1. Configure MySQL Database
Open `src/main/resources/application.properties` and verify your MySQL credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/assessment_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### 2. Open in Java IDE
* **IntelliJ IDEA:**
  1. Open IntelliJ -> `File` -> `Open...` -> Select this `backend` folder.
  2. Wait for Maven dependencies to sync.
  3. Run `AssessmentToolApplication.java`.
* **Spring Tool Suite (STS) / Eclipse:**
  1. `File` -> `Import` -> `Existing Maven Projects` -> Select this `backend` folder.
  2. Right-click project -> `Run As` -> `Spring Boot App`.

### 3. API Endpoints
Base URL: `http://localhost:8080/api`

| Method | Endpoint | Description |
|---|---|---|
| `GET` / `POST` | `/api/users` | List users or register a new user |
| `GET` / `PUT` / `PATCH` / `DELETE` | `/api/users/{id}` | Manage specific user profile |
| `GET` / `POST` | `/api/assessments` | Retrieve or create assessments |
| `GET` / `PUT` / `DELETE` | `/api/assessments/{id}` | Modify or delete assessments |
| `GET` / `POST` | `/api/submissions` | View or submit student exam attempts |
| `GET` / `POST` | `/api/reports` | Get admin analytics & reports |

---

## 🔄 Automatic Data Initialization
On first startup, `DataInitializer.java` will automatically create initial users (`admin@gmail.com`, `educator@gmail.com`, `student@gmail.com`), sample assessments, and reports in MySQL if the database is fresh.
