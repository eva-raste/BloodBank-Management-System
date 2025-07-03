#  Blood Bank Management System 

A secure and feature-rich Blood Bank Management System built with **Java**, **Spring Boot**, **Spring Security**, **Hibernate**, and **JPA**. The system handles end-to-end operations of blood donation and request management across different roles — **Donor**, **Recipient**, and **Admin**.

---

##  Key Features

###  User Authentication & Role-Based Authorization
- Role-based access control for **Donors**, **Recipients**, and **Admins**
- Secure login system with encrypted passwords (using Spring Security)

###  Donor Registration & Validation
- Donor eligibility check based on:
  - Age
  - Weight
  - Hemoglobin level
  - Health condition
- Restricts donation if the donor has donated in the last **6 months**

###  Blood Donation Module
- Allows eligible donors to request donations
- Tracks complete donation history per donor

###  Blood Inventory Management
- Inventory auto-updated after successful donation or request fulfillment
- Maintains quantity by **blood group type**
- Handles compatibility logic (e.g., O- universal donor, AB+ universal recipient)

###  Blood Request Module
- Recipients can request compatible blood types
- Suggests alternatives if requested type is unavailable
- All requests stored as **Transactions** with status tracking

###  Admin Dashboard
- View & manage donor data
- Update donor records
- Access control to prevent unauthorized changes

---

##  Tech Stack

- **Backend:** Java 17, Spring Boot 3.4.x
- **ORM:** Hibernate, Spring Data JPA
- **Security:** Spring Security (Role-based access)
- **Database:** MySQL
- **UI:** Thymeleaf (Spring MVC templating)
- **Build Tool:** Maven
- **Design Pattern:** MVC + Service Layer
