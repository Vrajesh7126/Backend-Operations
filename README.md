# Backend Operations API

A Spring Boot RESTful API for managing dataset records, supporting CRUD operations, grouping, and sorting with robust validation and exception handling.

---

## Features

- Add, retrieve, group, and sort dataset records
- Field-level validation for all entity fields
- Custom exception handling with clear error messages
- JPA-based persistence with MySQL (configurable)
- Interactive API documentation via Swagger UI
- Unit tests using JUnit and Mockito for controller, service, and repository layers

---

## Getting Started

### Prerequisites

- Java 17 or higher (tested with Java 24)
- Maven 3.8+
- MySQL (or compatible database)

### Configuration

Edit `src/main/resources/application.properties` to set your database connection:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/your_db
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### Build & Run

1. **Build the project:**
    ```sh
    mvn clean install
    ```

2. **Run the application:**
    ```sh
    mvn spring-boot:run
    ```
    
3. **Access the API documentation:**
    - [Swagger UI](http://localhost:8080/swagger-ui.html)  

---

## API Endpoints

- `POST /api/dataset/{datasetName}/record`  
  Add a new record to a dataset.

- `GET /api/dataset/{datasetName}/records?groupBy=fieldName`  
  Get grouped records by a field.

- `GET /api/dataset/{datasetName}/records?sortBy=fieldName&order=asc|desc`  
  Get sorted records.

- See Swagger UI for full details and try out endpoints interactively.

---

## Testing

- Unit tests are written using **JUnit 5** and **Mockito**.
- To run all tests:
    ```sh
    mvn test
    ```

### Test Case Files

- **Controller Layer:**  
  `src/test/java/com/example/Controller/DatasetControllerTest.java`  
  Contains tests for all REST API endpoints, including edge cases such as invalid input, missing fields, and duplicate IDs.

- **Service Layer:**  
  `src/test/java/com/example/Service/DatasetServiceTest.java`  
  Includes tests for business logic, grouping, sorting, validation, and exception scenarios.

- **Repository Layer:**  
  `src/test/java/com/example/Repository/DatasetRepositoryTest.java`  
  Tests custom repository queries and data access logic.

---

## TDD Approach

- Edge cases are covered, including:
    - Duplicate record IDs
    - Invalid field values (e.g., negative age, invalid name/department)
    - Missing required fields
    - Empty dataset queries

---

## Project Structure

```
src/
  main/
    java/com/example/
      Controller/         # REST controllers
      Entity/             # JPA entities
      Exception/          # Custom exceptions & global handler
      Repository/         # Spring Data JPA repositories
      Service/            # Business logic
    resources/
      application.properties
  test/
    java/com/example/
      Controller/
        DatasetControllerTest.java
      Repository/
        DatasetRepositoryTest.java
      Service/
        DatasetServiceTest.java
    resources/
      application.properties
```

---

## Author

- Vrajesh Vaghasiya