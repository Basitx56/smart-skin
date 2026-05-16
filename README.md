# 🧴 Smart Skin Health Analysis & Routine Generator System

A Java desktop application built with **JavaFX** and **MySQL** that analyzes users' skin profiles and generates personalized skincare routines. Built as a Software Design & Architecture (SDA) course project at FAST NUCES Islamabad.

---

## ✨ Features

- 👤 User registration & login with **bcrypt** password hashing
- 🧬 Skin profile analysis (oily, dry, combination, sensitive, normal)
- 📋 Personalized skincare routine generation (morning & evening)
- ⚠️ Ingredient conflict detection
- 🛍️ Product search & favorites
- 📅 Appointment booking with skincare experts
- 📊 Progress tracking with photo uploads
- ⭐ Expert rating & review system
- 🔔 Notification settings
- 🛡️ Admin panel for user & product management
- 📝 Validation queue for expert-verified routines
- 🌙 Dark theme UI

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17+ |
| UI Framework | JavaFX 17 + FXML |
| Database | MySQL 8.x |
| Build Tool | Maven |
| Password Hashing | BCrypt |
| DB Connector | MySQL Connector/J 8.0.33 |

---

## ⚙️ Prerequisites

Make sure you have the following installed:

- **Java JDK 17+** → [Download](https://www.oracle.com/java/technologies/downloads/)
- **Maven 3.8+** → [Download](https://maven.apache.org/download.cgi)
- **MySQL 8.x** → [Download](https://dev.mysql.com/downloads/)
- **Git**

---

## 🚀 Setup & Run

### 1. Clone the Repository
```bash
git clone https://github.com/Basitx56/smart-skin.git
cd smart-skin
```

### 2. Set Up the Database

Open MySQL and run:
```sql
SOURCE database.sql;
```

Or via terminal:
```bash
mysql -u root -p < database.sql
```

This creates the `smartskin` database with all required tables.

### 3. Configure Database Connection

Edit `src/resources/config.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/smartskin
db.username=root
db.password=your_mysql_password
```

### 4. Build the Project
```bash
mvn clean install
```

### 5. Run the Application
```bash
mvn javafx:run
```

---

## 📁 Project Structure

```
smart-skin/
├── src/
│   ├── Main.java                  # Entry point
│   ├── model/                     # Entity classes (User, Product, etc.)
│   ├── repository/                # Database access layer (DAO pattern)
│   ├── service/                   # Business logic layer
│   ├── ui/                        # JavaFX controllers
│   ├── util/                      # Helpers (Session, Navigation, etc.)
│   └── resources/
│       ├── fxml/                  # UI layout files
│       ├── styles/                # CSS dark theme
│       ├── images/                # App images
│       └── config.properties      # DB configuration
├── database.sql                   # Full database schema
└── pom.xml                        # Maven dependencies
```

---

## 🏗️ Architecture & Design Patterns

This project follows a **3-layer architecture**:

- **Presentation Layer** — JavaFX FXML controllers (`ui/`)
- **Business Logic Layer** — Service classes (`service/`)
- **Data Access Layer** — Repository classes (`repository/`)

### Design Patterns Used
- **Strategy Pattern** — Routine generation per skin type (`RoutineStrategy`, `OilySkinStrategy`, `DrySkinStrategy`, etc.)
- **Repository Pattern** — Data access abstraction
- **Session Manager** — Singleton for user session
- **Factory Pattern** — `RoutineStrategyFactory`
- **Observer Pattern** — Notification system

---

## 👥 User Roles

| Role | Capabilities |
|------|-------------|
| End User | Profile setup, routine generation, appointments, progress tracking |
| Skincare Expert | Validate routines, manage appointments, get rated |
| Admin | Manage users, products, view audit logs |

---

## 📸 Default Admin Credentials

After running `database.sql`, seed the database using `DatabaseSeeder.java` or manually insert an admin user.

---

## 👨‍💻 Developed By

**ABDUL BASIT - AYESHA ANJUM**  
FAST NUCES Islamabad  
Course: Software Design & Architecture (SDA)