# 🖥 Maebanjumpen API – Backend for Housekeeper Hiring Platform

[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Spring Boot](https://img.shields.io/badge/Backend-SpringBoot-darkgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/Database-MySQL-blue?logo=mysql)](https://www.mysql.com/)

> This repository contains the **backend API** for the Maebanjumpen platform. It is a robust **Spring Boot** application that handles user authentication, service booking requests, and database management for the mobile app.

---

## ✨ Key Features

* **Role-based Authentication:** Manages user roles such as customer, housekeeper, and admin.
* **RESTful Endpoints:** Provides secure and well-documented API endpoints for all platform functionalities.
* **Data Persistence:** Interacts with a **MySQL** database to store and retrieve all user and booking information.
* **Efficient Service Management:** Handles the core business logic for searching, booking, and tracking housekeeper services.

---

## 🛠 Technology Stack

| **Backend** | **Database** | **Tools** |
| :---------- | :----------- | :-------- |
| ☕ Spring Boot (Java) | 🗄 MySQL | 🛠 Postman · IntelliJ IDEA · Git |

---

## 🚀 Getting Started

### ✅ Prerequisites

* **Java JDK 17+**
* **MySQL Server**
* **Maven** (usually included with Spring Boot projects via `./mvnw`)

---

### 🔧 Installation and Running

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/Chanakan060703/maebanjumpen-intellij-API](https://github.com/Chanakan060703/maebanjumpen-intellij-API)
    cd maebanjumpen-intellij-API
    ```
2.  **Configure the database:** Open `src/main/resources/application.properties` and update the MySQL connection settings with your credentials.
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    ```
3.  **Run the application:**
    ```bash
    ./mvnw spring-boot:run
    ```

---

## 🔗 Related Repositories

This backend is the API for the **Maebanjumpen mobile application**.

[![📱 Mobile App Code](https://img.shields.io/badge/📱_Mobile_App_Code-0A66C2?style=for-the-badge&logo=github&logoColor=white)](https://github.com/Chanakan060703/maebanjumpen)

---

## 🤝 Contributing

Contributions are welcome! Please fork the repository, create a feature branch, and submit a pull request with your changes.

---

## 📜 License

This project is licensed under the [MIT License](LICENSE).

---

## 📬 Contact  

👤 **Chanakan Kongyen**  
* **Email:** Chonakankongyen@gmail.com
* **GitHub:** [github.com/Chanakan060703](https://github.com/Chanakan060703)
