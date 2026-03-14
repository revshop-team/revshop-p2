<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=0:ff7a18,100:ffb347&height=200&section=header&text=REVSHOP%20P2&fontSize=45&fontColor=ffffff&animation=fadeIn&fontAlignY=35"/>
</p>

<p align="center">
  <b>Full-Stack Monolithic E-Commerce Web Application</b><br>
  <i>Revature Training Program | Phase 2 Project | PES Mandya Batch</i>
</p>

<p align="center">
  <a href="https://github.com/revshop-team">
    <img src="https://img.shields.io/badge/Organization-RevShop%20Team-black?style=for-the-badge&logo=github"/>
  </a>
  <img src="https://img.shields.io/badge/Status-Completed-green?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Architecture-Layered%20Monolithic-blue?style=for-the-badge"/>
</p>



# Welcome to RevShop

**RevShop P2** is a full-stack monolithic **E-Commerce web application** developed as part of the **Revature Training Program (Phase 2)** by the **PES Mandya batch**.

The platform simulates a real-world **online marketplace** where buyers and sellers interact through features such as:

* Product browsing
* Cart management
* Order processing
* Inventory management
* Reviews and ratings
* Notifications
* Simulated payment processing

The system follows **enterprise-level development practices** including layered architecture, secure authentication, automated testing, and CI/CD integration.



# Technology Stack & Tools

<p align="center">

<img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white"/>
<img src="https://img.shields.io/badge/Spring%20MVC-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/>
<img src="https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge"/>
<img src="https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"/>
<img src="https://img.shields.io/badge/Oracle%20DB-F80000?style=for-the-badge&logo=oracle&logoColor=white"/>
<img src="https://img.shields.io/badge/JDBC-000000?style=for-the-badge"/>
<img src="https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white"/>

</p>

<p align="center">

<img src="https://img.shields.io/badge/Jenkins-D24939?style=for-the-badge&logo=jenkins&logoColor=white"/>
<img src="https://img.shields.io/badge/SonarQube-4E9BCD?style=for-the-badge&logo=sonarqube&logoColor=white"/>
<img src="https://img.shields.io/badge/JUnit-25A162?style=for-the-badge"/>
<img src="https://img.shields.io/badge/Mockito-FFCA28?style=for-the-badge"/>
<img src="https://img.shields.io/badge/JaCoCo-BB0000?style=for-the-badge"/>
<img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white"/>

</p>



# System Architecture

The application follows a **Layered Monolithic Architecture** implementing the **Model-View-Controller (MVC)** design pattern.

### Architectural Components

* Layered Monolithic Architecture
* MVC (Model-View-Controller)
* Role-Based Authentication & Authorization
* Oracle Database with PL/SQL Integration
* Structured Logging and Exception Handling
* Continuous Integration using Jenkins
* Automated Testing & Code Coverage Monitoring
* Static Code Analysis using SonarQube, PMD, and Checkstyle



# Project Structure

```
revshop-p2
│
├── .scannerwork/                 # SonarQube analysis files
│
├── src
│   ├── main
│   │   ├── java/com/revshop
│   │   │   ├── config/           # Security and application configuration
│   │   │   ├── controller/       # MVC Controllers
│   │   │   ├── entity/           # JPA entity classes
│   │   │   ├── exceptions/       # Custom exception handling
│   │   │   ├── repo/             # Spring Data JPA repositories
│   │   │   ├── serviceInterfaces/# Service layer interfaces
│   │   │   ├── serviceImpl/      # Business logic implementation
│   │   │   └── RevshopApplication.java
│   │
│   │   └── resources
│   │       ├── static/
│   │       │   └── images/       # Static assets
│   │       ├── templates/        # Thymeleaf HTML templates
│   │       ├── application.properties
│   │       ├── log4j2.xml
│   │       └── logback-spring.xml
│
│   └── test
│       ├── java/com/revshop
│       │   ├── controller/
│       │   ├── entity/
│       │   ├── exception/
│       │   ├── repo/
│       │   ├── service/
│       │   └── RevshopApplicationTests.java
│       │
│       └── resources
│           └── application.test.properties
│
├── .gitattributes
├── .gitignore
├── HELP.md
├── P2_REVSHOP_DOCUMENTATION.pdf
├── README.md
├── pom.xml
└── sonar-project.properties
```



# Feature Overview

| Buyer Features                              | Seller Features                        |
| ------------------------------------------- | -------------------------------------- |
| User registration and secure authentication | Seller registration and authentication |
| Browse and search products                  | Add products to catalog                |
| View product details, ratings, reviews      | Update product details                 |
| Add items to cart                           | Delete products                        |
| Checkout with payment simulation            | Inventory management                   |
| Order placement & order history             | Pricing and discount management        |
| Receive notifications                       | View customer orders                   |
| Wishlist management                         | Monitor reviews and ratings            |
| Track order status                          | Low stock alerts                       |



# Project Modules

* Authentication & Account Management
* Product & Inventory Management
* Cart & Order Processing
* Notification System
* Role-Based Access Control
* Web UI using Thymeleaf



# Team Members

| Photo                                                            | Name              | GitHub                               |
| ---------------------------------------------------------------- | ----------------- | ------------------------------------ |
| <img src="https://github.com/shanthankumar05.png" width="70"/>   | Shanthan Kumar    | https://github.com/shanthankumar05   |
| <img src="https://github.com/Akshaya150803.png" width="70"/>     | Akshaya           | https://github.com/Akshaya150803     |
| <img src="https://github.com/Mutluru-Prashanth.png" width="70"/> | Mutluru Prashanth | https://github.com/Mutluru-Prashanth |
| <img src="https://github.com/BGunashree.png" width="70"/>        | Gunashree         | https://github.com/BGunashree        |
| <img src="https://github.com/abdulkareemmomin.png" width="70"/>  | Abdulkareem       | https://github.com/abdulkareemmomin  |




# Repository Information

**Organization:** https://github.com/revshop-team

**Project:** RevShop P2 – E-Commerce Application

**Training Program:** CTS TRNG-00002359

**Batch:** PES Mandya

**Architecture:** Layered Monolithic (MVC)



# Setup and Run Instructions

## Prerequisites

* Java JDK 17+
* Apache Maven
* Oracle Database
* Git
* IDE (IntelliJ / Eclipse / VS Code)



## Clone the Repository

```
git clone https://github.com/revshop-team/revshop-p2.git
cd revshop-p2
```



## Database Setup

Create a schema in Oracle:

```
CREATE USER revshop IDENTIFIED BY password;
GRANT CONNECT, RESOURCE TO revshop;
```



## Configure Database

Update:

```
src/main/resources/application.properties
```

Example:

```
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:xe
spring.datasource.username=revshop
spring.datasource.password=revshop123
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
```



## Build Project

```
mvn clean install
```

This will:

* Compile code
* Run tests
* Generate build artifact (WAR/JAR)



## Run Application

### Run using Maven

```
mvn spring-boot:run
```

### Deploy to Tomcat

```
mvn clean package
```

Copy WAR file to:

```
Tomcat/webapps
```



## Access Application

```
http://localhost:8090
```



# Default Roles

**Buyer**

* Browse products
* Add to cart
* Place orders
* Manage wishlist

**Seller**

* Add and manage products
* Inventory control
* View orders
* Monitor stock



# Conclusion

This project was developed as part of the Revature Java Full Stack Training Program by the PES Mandya batch and reflects collaborative software development practices including version control, code quality enforcement, and automated testing.



###  Thank You ⭐
