# WalkTogetherApp

WalkTogether is a comprehensive desktop application built with Java (Swing) and PostgreSQL. It serves as a community platform where users can discover walking/hiking routes, join scheduled events, earn points based on route difficulties, and compete on a global leaderboard.

This project was developed with a strong emphasis on Database Management System (DBMS) concepts, utilizing advanced PostgreSQL features such as custom functions, triggers, and views to handle business logic directly at the database level.

## 🚀 Key Features

### 👤 User Features
* **Interactive Dashboard:** Search for routes, filter by difficulty, and discover unjoined routes.
* **Event Participation:** Join scheduled walking events with real-time quota tracking.
* **Gamification & Badges:** Earn points based on route difficulty and average steps. Unlock badges (Bronze, Silver, Gold, Platinum, Diamond) automatically as you progress.
* **Leaderboard:** Compete with other users globally based on total accumulated points.
* **Social System:** Invite other users to events you are participating in and manage incoming invitations.
* **Activity Logs:** View your detailed past activity report generated directly from the database.

### 🛡️ Admin Features
* **Route Management:** Add new routes (with difficulty levels and steps) and delete existing ones safely.
* **Event Creation:** Schedule new events for specific routes with custom quotas.
* **User Management:** Add new users and promote existing users to 'Admin' roles.
* **System Statistics:** View real-time database-driven statistics (e.g., Most Popular Route of the Week, Most Active User).

## 🛠️ Technology Stack

* **Frontend / GUI:** Java Swing (Nimbus Look and Feel), Custom Dark Theme UI.
* **Backend:** Java (JDK), JDBC API.
* **Database:** PostgreSQL.

### 🗄️ Advanced Database Architecture
* **Triggers:** Automated point distribution upon event completion (`trg_award_points`) and strict event quota validation (`trg_check_quota`).
* **Stored Procedures & Functions:** Complex logic handling for score calculations, user badges, unjoined routes filtering, and system statistics.
* **Views:** Optimized leaderboard data retrieval (`leaderboard_view`).

## ⚙️ Installation & Setup

### 1. Database Setup
* Ensure you have PostgreSQL installed and running on `localhost:5432`.
* Create a new database named `VTYSProject`.
* Open the `VTYSProjectQuery.sql` file in pgAdmin or your preferred SQL client.
* Execute the entire script to create the schema, sequences, functions, triggers, and populate the database with default mock data.

### 2. Project Configuration
* Clone this repository to your local machine.
* Open the project in your IDE (e.g., IntelliJ IDEA, Eclipse).
* Ensure the PostgreSQL JDBC Driver (`postgresql-42.7.8.jar`) is added to your project's dependencies/libraries.
* Open `src/DBHelper.java` and update the database credentials to match your local setup:
  
  ```java
  private static final String databaseName = "jdbc:postgresql://localhost:5432/VTYSProject";
  private static final String userName = "postgres";
  private static final String userPassword = "your_password_here"; // Update this before running!

### 3. Run The App
* Compile and run WalkTogetherApp.java to start the application.

### 4. Default Login Credentials
* Admin: Berkay | 1234
* User: Hasan | 1234
