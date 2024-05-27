# Backend Spring Boot Application

This repository contains the backend Spring Boot application for the Document Management System (DMS).

## Setup

1. Clone this repository:
   ```sh
   git clone <repository-url>
   ```

2. Import the project into your preferred IDE (such as IntelliJ IDEA, Eclipse, or Visual Studio Code).

3. Ensure you have Java JDK 8 or later installed on your system.

4. Configure the application properties (`application.properties` or `application.yml`) to connect to your database and set other required properties.

5. If needed, install dependencies using Maven:
   ```sh
   mvn install
   ```

## Running the Application

You can run the application directly from your IDE or use Maven to build and run it. To run with Maven, use:
   ```sh
   mvn spring-boot:run
   ```

The application will start and be available at `http://localhost:8080`.

## Usage

Once the application is running, it will provide RESTful APIs to interact with the Document Management System (DMS). The frontend applications (Clients App and Documents App) can communicate with this backend to manage users, documents, directories, and other functionalities of the DMS.
