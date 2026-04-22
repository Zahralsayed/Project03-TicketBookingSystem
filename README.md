# TicketSystem 🎫

## Project Description
TicketSystem is a robust Spring Boot backend service engineered for the modern event management industry. It acts as a comprehensive bridge between event discovery and secure attendance, empowering users to explore upcoming events across various locations, manage real-time ticket reservations, and maintain a personalized digital profile.

---

## 🚀 Technologies Used
* **Backend:** Java 17, Spring Boot 3.x, Spring Security (JWT)
* **Database:** PostgreSQL, Spring Data JPA, Hibernate
* **Tools:** Maven, Postman, Git

## 🛠 Project Approach
The project follows an N-Tier architecture, separating concerns between Controllers, Services, and Repositories. 
* **Security:** Implemented JWT-based authentication for secure role-based access.
* **Logic:** Designed a hybrid deletion strategy—standard users are hard-deleted, while **Administrators** are soft-deleted by updating their status to `INACTIVE` to preserve system history.
* **Personalization:** Users can update their profiles (phone, address, and profile picture) using `MultipartFile` handling with partial update logic.

## 🚧 Major Hurdles
* **Multipart Requests:** Managing complex `form-data` requests in Postman to sync file uploads with text-based record updates.
* **Concurrency:** Resolving race conditions during deletion testing to ensure "Idempotent" API responses.

## 📋 Planning & Design
* [Trello Board -  User Stories](https://trello.com/b/m5IkpMpD/ticket-booking-system) - Who the users are and what they need.
* [ERD Diagram]- Database schema and relationships.
<img width="1472" height="686" alt="TicketSystemERD" src="https://github.com/user-attachments/assets/4f2af530-7d73-409a-aec9-9a64a9586e59" />

  

## 💻 Installation & Setup

### Prerequisites
* JDK 17
* PostgreSQL 
* Maven

### To Get Started
To get a local copy up and running, follow these simple steps:

1. Clone the Repo: git clone [Repository Link](https://github.com/Zahralsayed/Project03-TicketBookingSystem)
2. Database Setup: Create a PostgreSQL database named ticketsystem.
3. Configure: Update src/main/resources/application.properties with your database credentials.
4. Run: Execute mvn spring-boot:run.

