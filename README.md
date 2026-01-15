# 🛠 Spec-Forge API

![Status](https://img.shields.io/badge/Status-In_Development-orange?style=for-the-badge)

![Java Version](https://img.shields.io/badge/Java-25+-6DB33F?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4+-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9+-6DB33F?style=for-the-badge&logo=apache-maven&logoColor=white)

![Architecture](https://img.shields.io/badge/Arch-Hexagonal-6DB33F?style=for-the-badge)
![License](https://img.shields.io/badge/License-AGPL--3.0-blue?style=for-the-badge)

**Spec-Forge API** is an engine designed to transform raw data sources like **JSON examples** and **JSON schemas** into
a unified **Intermediate Representation (IR)**. This IR is then used to generate clean, production-ready **Java** and 
**TypeScript** codebases.

---

## 🏗️ Current Status

This project is currently under **active development**.

---

## 🚀 Quick Start

### 📋 Prerequisites

Ensure you have the following environment configurations before starting:

* **Java 25+**

### 💻 Run Locally

Execute the following commands in your terminal to clone the repository and start the service:

```bash
# Clone the repository
git clone https://github.com/volodymyr-oleksiienko/spec-forge-api.git

# Navigate into the project directory
cd spec-forge-api

# Build application using the Maven Wrapper
./mvnw clean install

# Run the application using the Maven Wrapper
./mvnw spring-boot:run -pl spec-forge-api-infra
```

Once the application has started, the API will be available at:
> `http://localhost:8080`


Application health check available at:
> `http://localhost:8080/actuator/health`

---

## 🧰 Tech Stack

* **Language:** Java 25+
* **Framework:** Spring Boot 4+
* **Build Tool:** Maven 3.9+

---

## 🏛️ Architecture Spec-Forge

Spec-Forge uses a **Ports & Adapters (Hexagonal) architecture** to maintain a **pure business logic core**.

---

### 🛡️ Core

- **Pure Java 25 logic**
- **Zero external dependencies**
- Contains:
    - Intermediate Representation (IR)
    - Transformation rules
- Dependency purity is strictly enforced via **`maven-enforcer-plugin`**

---

### 🔌 Infrastructure

- **Spring Boot**
- Adapters and IO

---

### 🧩 Project Structure

Spec-Forge is built as a Maven Multi-Module project to enforce architectural boundaries.

```
.
├── spec-forge-api-core/      # Pure Business Logic
│   ├── src/main/java         # IR Definitions, Transformation Rules
│   └── pom.xml               # ZERO external dependencies (Enforced)
│
├── spec-forge-api-infra/     # Framework & Adapters
│   ├── src/main/java         # Spring Boot Controllers, IO
│   ├── src/main/resources    # Application Config
│   └── pom.xml               # Depends on core + Spring Boot
│
└── pom.xml                   # Parent POM (Dependency Mgmt)
```

---

## ⚖️ License

This project is licensed under the **GNU Affero General Public License v3.0 (AGPL-3.0)**.

### Special licensing

If you require **special licensing conditions** (e.g. commercial use, closed-source distribution, or other exceptions),
**you must contact the project author directly** to discuss alternative licensing options.