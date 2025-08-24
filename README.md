# Backend Operations API

A Spring Boot RESTful API for managing dataset records, supporting CRUD operations, grouping, and sorting with robust validation and exception handling.

---

## Features

* Add, retrieve, group, and sort dataset records
* Field-level validation for all entity fields
* Custom exception handling with clear error messages
* JPA-based persistence with MySQL (configurable)
* Interactive API documentation via Swagger UI
* Unit tests using JUnit and Mockito for controller, service, and repository layers

---

## Getting Started

### Prerequisites

* Java 17 or higher (tested with Java 24)
* Maven 3.8+
* MySQL (or compatible database)

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

   * [Swagger UI](http://localhost:8080/swagger-ui.html)

---

## API Endpoints

### Add Record

* **POST** `/api/dataset/{datasetName}/record`

**Request Body:**

```json
{
  "id": 101,
  "name": "John Doe",
  "age": 30,
  "department": "HR"
}
```

**Responses:**

* **200 OK**

```json
{
  "message": "Record added successfully",
  "dataset": "employees",
  "recordId": 101
}
```

* **400 Bad Request**

```json
{
  "error": "ID is required",
}
```

```json
{
  "error": "Record with this ID already exists",
}
```

---

### Group Records

* **GET** `/api/dataset/{datasetName}/query?groupBy={fieldName}`

**Example:** `/api/dataset/employees/query?groupBy=department`

**Responses:**

* **200 OK**

```json
{
  "groupedRecords": {
    "HR": [{ "id": 101, "name": "John Doe", "age": 30, "department": "HR" }],
    "IT": [{ "id": 102, "name": "Jane Smith", "age": 25, "department": "IT" }]
  }
}
```

* **400 Bad Request**

```json
{
  "error": "groupBy field cannot be empty",
}
```

```json
{
  "error": "Unsupported groupBy field: invalidField",
}
```

* **404 Not Found**

```json
{
  "error": "No records found for dataset: employees",
}
```

* **500 Internal Server Error**

```json
{
  "error": "Internal server error",
}
```

---

### Sort Records

* **GET** `/api/dataset/{datasetName}/query?sortBy={fieldName}&order=asc|desc`

**Example:** `/api/dataset/employees/query?sortBy=age&order=desc`

**Responses:**

* **200 OK**

```json
{
  "sortedRecords": [
    { "id": 101, "name": "John Doe", "age": 30, "department": "HR" },
    { "id": 102, "name": "Jane Smith", "age": 25, "department": "IT" }
  ]
}
```

* **400 Bad Request**

```json
{
  "error": "Unsupported sortBy field: invalidField",
}
```

```json
{
  "error": "Invalid sort order: descending. Use 'asc' or 'desc'.",
}
```

* **404 Not Found**

```json
{
  "error": "No records found for dataset: employees",
}
```

* **500 Internal Server Error**

```json
{
  "error": "Internal server error",
}
```

---

## Testing

* Unit tests are written using **JUnit 5** and **Mockito**.
* To run all tests:

```sh
mvn test
```

### Test Case Files

* **Controller Layer:**
  `src/test/java/com/example/Controller/DatasetControllerTest.java`
  Covers endpoint responses for success and all exception scenarios.

* **Service Layer:**
  `src/test/java/com/example/Service/DatasetServiceTest.java`
  Covers insert, group, and sort business logic with validations.

* **Repository Layer:**
  `src/test/java/com/example/Repository/DatasetRepositoryTest.java`
  Tests custom queries and data access.

---

## TDD Approach

* Edge cases covered:

  * Duplicate record IDs
  * Invalid field values
  * Missing required fields
  * Empty dataset queries
  * Unsupported groupBy or sortBy fields
  * Invalid sort order

---

## Project Structure

```
src/
  main/
    java/com/example/
      Controller/
      Entity/
      Exception/
      Repository/
      Service/
    resources/
      application.properties
  test/
    java/com/example/
      Controller/
      Repository/
      Service/
    resources/
      application.properties
```

---

## Author

* Vrajesh Vaghasiya
