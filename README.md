# AccWe Hospital Appointment Management System

## Objectives
The main objective is to complete the implementation and development of the appointment management system for the backend project, which uses Java 8. Key tasks include:

- Implement the creation of appointments through the API, respecting test specifications to ensure code reliability.

- Addressing the lack of tests for existing entities by developing JUnit tests for each entity and for their controllers.

- Maintain clean and secure code standards to ensure that the code is free of bugs and vulnerabilities.

- Create the Dockerfiles for both the MySQL database and the microservice with a multi-stage approach: test, compile and run. To prepare the deployment of the API using Kubernetes

## Technologies

- Java 8 or higher
- Spring Boot using Maven
- Spring Data JPA
- MySQL
- Docker

## Test

The project includes JUnit tests for each entity and their controllers. The tests are located in the src/test/java directory.

To run the tests, use the following command:

```bash
mvn test
```



## Dockerization

Dockerfiles are provided to deploy both the application and the MySQL database:

- To build and run the MySQL database, use Dockerfile.mysql.
- To build and run the microservice, use Dockerfile.maven.

## Deployment with Docker

### Build Docker Images

Database (MySQL): Navigate to the main directory of the repository where the Dockerfile.mysql is located and build the Docker image:
```bash
docker build -t accwe-mysql -f Dockerfile.mysql .
```

Microservice: Navigate to the main directory of the repository where the Dockerfile.maven is located and build the Docker image:
```bash
docker build -t accwe-maven -f Dockerfile.maven .
```

### Running Docker Containers
Use your own environment variables to run the containers.

```bash
MYSQL_ROOT_PASSWORD=your_root_password
MYSQL_DATABASE=your_database
MYSQL_USER=your_user
MYSQL_PASSWORD=your_password

SPRING_DATASOURCE_URL=jdbc:mysql://db_ip_address:3306/your_database
SPRING_DATASOURCE_USERNAME=your_user
SPRING_DATASOURCE_PASSWORD=your_password
```

Database (MySQL): Run the Docker container:
```bash
docker run -d --name accwe-mysql -p 3306:3306 accwe-mysql
```

Microservice: Run the Docker container:
```bash
docker run -d --name accwe-maven -p 8080:8080 accwe-maven
```
## Run containers in local environment

### Create the network
```bash
docker network create accwe-network
```

### Run MySQL database
```bash
docker run -d --name accwe-mysql -p 3306:3306 \
  --network accwe-network \
  -e MYSQL_ROOT_PASSWORD=your_root_password \
  -e MYSQL_DATABASE=your_database \
  -e MYSQL_USER=your_user \
  -e MYSQL_PASSWORD=your_password \
  accwe-mysql  
  ```
### Run the Microservice
```bash
docker run -d --name accwe-maven -p 8080:8080 \
  --network accwe-network \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://accwe-mysql:3306/your_database \
  -e SPRING_DATASOURCE_USERNAME=your_user \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  accwe-maven
```
## Use the API

### Appointment
- POST http://localhost:8080/api/appointment - Create a new appointment
    - Request body example:
```json
{
  "doctor": {
    "firstName": "Name",
    "lastName": "LastName",
    "age": 34,
    "email": "eamil@hospital.com"
  },
  "patient": {
    "firstName": "Name",
    "lastName": "LastName",
    "age": 44,
    "email": "email@email.com"
  },
  "room": {
    "roomName": "Cardiology"
  },
  "startsAt": "20:30 01/04/2026",
  "finishesAt": "21:30 01/04/2026"
}
```
- GET http://localhost:8080/api/appointments - Get all appointments 
- GET http://localhost:8080/api/appointments/{id} - Get an appointment by id
- DELETE http://localhost:8080/api/appointments/{id} - Delete an appointment by id
- DELETE http://localhost:8080/api/appointments - Delete all appointments

### Doctor
- POST http://localhost:8080/api/doctor - Create a new doctor

    - Request body example:
```json
{
  "firstName": "Name",
  "lastName": "LastName",
  "age": 34,
  "email": "doctor@hospital.net"
}
```

- GET http://localhost:8080/api/doctors - Get all doctors
- GET http://localhost:8080/api/doctors/{id} - Get a doctor by id
- DELETE http://localhost:8080/api/doctors/{id} - Delete a doctor by id
- DELETE http://localhost:8080/api/doctors - Delete all doctors

### Patient
- POST http://localhost:8080/api/patient - Create a new patient

    - Request body example:
```json
{
  "firstName": "Name",
  "lastName": "LastName",
  "age": 44,
  "email": "patient@email.net"
}
```
- GET http://localhost:8080/api/patients - Get all patients
- GET http://localhost:8080/api/patients/{id} - Get a patient by id
- DELETE http://localhost:8080/api/patients/{id} - Delete a patient by id
- DELETE http://localhost:8080/api/patients - Delete all patients

### Room
- POST http://localhost:8080/api/room - Create a new room
  - Request body example:
```json
{
  "roomName": "Cardiology"
}
```
- GET http://localhost:8080/api/rooms - Get all rooms
- GET http://localhost:8080/api/rooms/{roomName} - Get a room by id
- DELETE http://localhost:8080/api/rooms/{roomName} - Delete a room by id
- DELETE http://localhost:8080/api/rooms - Delete all rooms

## Comments

Although the tests are successful, I have observed unexpected behaviors that I cannot correct without injecting repositories or modifying the entities:

- When saving new appointments, new patients, doctors, and rooms are always created. This may not be what was expected from the application.
- Due to the aforementioned issue, and because the Room entity has the field roomName as a primary key, JPA does not allow the creation of two appointments in the same room, raising an org.hibernate.exception.ConstraintViolationException.

