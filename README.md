# ♻️ Rawalpindi Waste Management Tracker

> A JavaFX desktop application that simulates a smart, zone-based waste management system for the city of Rawalpindi — featuring role-based dashboards, real-time compliance tracking, fine management, and an immersive 3D animated login screen.

**NUST SEECS | CS-212 Object-Oriented Programming | Section A | Spring 2026**

**Team:** Abdullah Ahmad · Unais Bin Faheem · Abdul Moiz Awan

---

## 📋 Table of Contents

- [About](#-about)
- [Features](#-features)
- [Screenshots](#-screenshots)
- [Tech Stack](#-tech-stack)
- [Architecture & Design Principles](#-architecture--design-principles)
- [Project Structure](#-project-structure)
- [How to Run](#-how-to-run)
- [Waste Categories](#-waste-categories)
- [License](#-license)

---

## 📖 About

Rawalpindi generates over **1,200 tons of waste daily**, with less than 20% currently being recycled. This application simulates how a digital tracking and compliance system could change that.

The system allows **administrators** to manage waste collection zones, monitor household compliance, issue fines, and generate weekly reports. **Household users** can log their waste disposal, view recycling values, check fines, and track their compliance score — all through a polished, modern JavaFX GUI with 3D animations.

The project also includes a **full console-based interface** (`Main.java`) with preloaded demo data across 6 Rawalpindi neighborhoods, municipal truck fleets, and collection route simulations.

---

## ✨ Features

### 🔐 Authentication & Registration
- Role-based login (Admin / User) with credential validation
- New user registration with zone selection (auto-linked to area admin)
- Credentials persisted to local text files

### 📊 Admin Dashboard
- **Live statistics**: total households, waste collected (kg), recycling value (PKR), fines issued, active trucks
- **Zone-filtered views**: admins only see households in their assigned zones
- **Fine issuance panel**: issue fines with custom amounts and reasons directly from the dashboard
- **Weekly report generation** with automated data reset
- **Household activity logs**: view individual user waste history, including custom categories

### 🏠 User Dashboard
- **Personal stats**: monthly fee, total fines, compliance percentage
- **Add Waste**: log waste items across 5 categories with animated feedback
- **Fine History**: view all fines issued to your household
- **Update Info**: change name or address
- **Recent Fine Alert**: unpaid fines highlighted in red

### 🎮 Simulation Engine (Console + GUI)
- Weekly simulation cycles with random events (bin overflow, compliance checks)
- Automatic fine generation for non-compliant households
- Municipal truck dispatch along collection routes
- Full weekly report generation with compliance breakdown

### 🎨 Visual Design
- **3D Animated Login Screen**: falling waste items (paper, wrappers) drop into green bins using JavaFX 3D shapes, lighting, and `AnimationTimer`
- **Rotating 3D Logo** on the login and dashboard screens
- Dark-themed, glassmorphic UI with green accent colors
- Smooth CSS transitions and hover effects

---

## 🖼️ Screenshots

> _Add your screenshots here_
>
> `![Login Screen](screenshots/login.png)`
> `![Admin Dashboard](screenshots/admin-dashboard.png)`
> `![User Dashboard](screenshots/user-dashboard.png)`

---

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| **Java 21** | Core language |
| **JavaFX 22** | GUI framework (controls, 3D graphics, animations) |
| **Maven** | Build & dependency management |
| **CSS** | Custom dark-themed styling (`app.css`) |
| **File I/O** | Data persistence (text files for users, admins, fines) |

---

## 🏗️ Architecture & Design Principles

The project follows **OOP** and **SOLID** design principles:

| Principle | Implementation |
|---|---|
| **Single Responsibility** | Each class has one job — `Simulation` manages game logic, `DataManager` handles I/O, `Report` generates reports, screen classes handle UI |
| **Open/Closed** | `WasteItem` is an abstract base class; new waste types (Organic, Plastic, Electronic, Glass, Metal) extend it without modifying existing code |
| **Liskov Substitution** | All `WasteItem` subclasses are used interchangeably via the base type throughout the system |
| **Interface Segregation** | `Vehicle` base class provides only common vehicle behavior; `MunicipalTruck` adds truck-specific methods |
| **Dependency Inversion** | High-level modules (`Simulation`, screens) depend on abstractions (`WasteItem`, `Vehicle`), not concrete implementations |

### Key Design Patterns
- **Inheritance Hierarchy**: `WasteItem` → `OrganicWaste`, `PlasticWaste`, `ElectronicWaste`, `GlassWaste`, `MetalWaste`
- **Composition**: `Household` contains `WasteBin` objects; `Simulation` composes all domain objects
- **MVC-like Separation**: Screen classes (View) ↔ Simulation (Controller/Model) ↔ DataManager (Persistence)

---

## 📂 Project Structure

```
src/main/java/com/rawalpindi/waste/
├── Main.java                  # Console-based entry point with interactive menu
├── FxMain.java                # JavaFX application entry point
├── Simulation.java            # Core simulation engine (weeks, compliance, dispatch)
├── Household.java             # Household model with waste bins and compliance logic
├── WasteItem.java             # Abstract base + 5 concrete waste subclasses
├── WasteBin.java              # Bin model (Organic, Plastic, Electronic, Glass, Metal)
├── Vehicle.java               # Abstract vehicle base class
├── CollectionRoute.java       # Route model linking trucks to household stops
├── Fine.java                  # Fine model + FineManager for compliance penalties
├── Report.java                # Weekly report generator
├── DataManager.java           # File I/O for saving/loading users, admins, fines
├── DemoDataLoader.java        # Preloads sample Rawalpindi household data
├── CustomExceptions.java      # Custom exception classes
├── LoginScreen.java           # 3D animated login UI
├── RegistrationScreen.java    # New user registration screen
├── MainWindow.java            # Navigation shell (sidebar + content area)
├── DashboardScreen.java       # Admin/User dashboard with live stats
├── AddWasteScreen.java        # Waste logging form with category selection
├── HouseholdsScreen.java      # Admin view of all households + activity logs
├── ReportScreen.java          # Report generation UI
├── UserFineHistoryScreen.java # User's fine history view
└── UserUpdateInfoScreen.java  # Profile update screen

src/main/resources/
├── com/rawalpindi/waste/app.css   # Dark-themed stylesheet
└── images/logo.png                # Application logo
```

---

## 🚀 How to Run

### Prerequisites
- **Java 21+** (JDK)
- **Maven 3.8+**

### Steps

```bash
# 1. Clone the repository
git clone https://github.com/ABDULMOIZ-AWAN/Waste-Management-System.git
cd Waste-Management-System

# 2. Run the JavaFX GUI
mvn javafx:run

# 3. Or run the console version
mvn compile exec:java -Dexec.mainClass="com.rawalpindi.waste.Main"
```

### Default Credentials

| Role | Username | Password |
|---|---|---|
| Admin | *(check `admins.txt`)* | *(check `admins.txt`)* |
| User | `1` (Household ID) | `123456` |

---

## 🗑️ Waste Categories

| Type | Hazard Level | Recycle Value (PKR/kg) | Examples |
|---|---|---|---|
| **Organic** | ⭐ (1/5) | 0 (composted) | Food scraps, garden waste, paper |
| **Plastic** | ⭐⭐⭐ (3/5) | 5–30 | PET bottles, HDPE containers, PVC |
| **Electronic** | ⭐⭐⭐⭐⭐ (5/5) | 20–80 | Phones, laptops, batteries, TVs |
| **Glass** | ⭐⭐ (2/5) | 4–15 | Clear/colored bottles, broken glass |
| **Metal** | ⭐⭐ (2/5) | 17.5–120 | Aluminum cans, copper wire, steel |

---

## 📄 License

This project was developed as a semester project for **CS-212 (OOP)** at **NUST SEECS, Islamabad**. For academic use only.

---

<p align="center">
  <i>Sort smart. Dispose cleanly. Measure change. ♻️</i>
</p>
