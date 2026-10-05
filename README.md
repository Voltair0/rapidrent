# RapidRent

RapidRent is a comprehensive car rental management platform built with Spring Boot. It facilitates interactions between car providers, clients looking to rent, and administrators managing the platform. The application provides role-based access, reservation handling, document verification, and a secure authentication system.

## Features

* **Multi-Role Dashboards:** Dedicated interfaces and dashboards for Clients, Providers, and Administrators.
* **Car Management:** Providers can list, manage, and track the status of their vehicles.
* **Reservation System:** Clients can browse available cars and book reservations.
* **Document Verification:** Integrated document upload and verification system for clients and providers.
* **Secure Authentication:** JWT-based user authentication and role-based access control (Spring Security).
* **Responsive Frontend:** Server-side rendered views using Thymeleaf, supplemented with custom CSS and JavaScript.

## Tech Stack

* **Backend:** Java, Spring Boot, Spring Security, Spring Data JPA
* **Frontend:** Thymeleaf, HTML5, CSS3, Vanilla JavaScript
* **Security:** JSON Web Tokens (JWT) for secure authentication
* **Build Tool:** Maven

## Project Structure

```text
rapidrent/
├── src/main/java/com/rapidrent/rapidrent/
│   ├── config/       # Configuration files and demo data initializers
│   ├── controller/   # Web and REST controllers (Admin, Auth, Car, Client, etc.)
│   ├── dto/          # Data Transfer Objects for API requests and responses
│   ├── model/        # JPA Entities (User, Car, Reservation, Role, etc.)
│   ├── repository/   # Data access layer interfaces
│   ├── security/     # JWT filters, user details services, and security config
│   └── service/      # Business logic (Auth, Car, Reservation, Documents)
├── src/main/resources/
│   ├── static/       # Static assets (CSS, JS)
│   ├── templates/    # Thymeleaf HTML templates (dashboards, forms, auth)
│   └── application.properties # Main application configuration
├── pom.xml           # Maven project dependencies
└── mvnw / mvnw.cmd   # Maven wrapper scripts

```

## Getting Started

### Prerequisites

* Java Development Kit (JDK) 17 or higher
* Maven (Optional, as the project includes a Maven Wrapper)

### Installation & Setup

1. **Clone the repository:**
```bash
git clone 
cd rapidrent

```


2. **Configure the Database:**
Update the `src/main/resources/application.properties` file with your local database credentials and JWT secret key if necessary.
3. **Build the project:**
```bash
./mvnw clean install

```


4. **Run the application:**
```bash
./mvnw spring-boot:run

```


5. **Access the application:**
Open your browser and navigate to `http://localhost:8080`.

## User Roles

* **Admin:** Oversees the entire platform, manages user accounts, verifies documents, and views platform-wide metrics.
* **Provider:** Lists cars for rent, manages vehicle availability, and reviews incoming reservation requests.
* **Client:** Browses available vehicles, submits rental requests, and tracks active reservations.
