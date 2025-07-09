# 🖤 Black Wedding Guide — Backend (BWG)

The **Black Wedding Guide (BWG)** backend powers a wedding services platform tailored for diverse cultural representation. It supports user authentication, vendor onboarding, bookings, media uploads, and more — using REST and GraphQL APIs with production-ready architecture.

---

## 🚀 Tech Stack

- **Java 17**
- **Spring Boot 3.x**
- **Maven**
- **PostgreSQL (AWS RDS)**
- **AWS API Gateway**
- **Amazon S3** (for media storage)
- **JWT Authentication**
- **JUnit + Cucumber (BDD Testing)**

---

## 📁 Project Structure
bwg-service/
├── scripts/ # Deployment or DB scripts
├── sql/ # DB schema or seed data
├── src/
│ ├── main/
│ │ ├── java/com/bwg/
│ │ │ ├── config/ # Security, beans, Swagger
│ │ │ ├── domain/ # JPA Entities
│ │ │ ├── repository/ # Spring Data Repositories
│ │ │ ├── resolver/ # GraphQL Resolvers
│ │ │ ├── model/ # DTOs / API Models
│ │ │ ├── projection/ # Custom query projections
│ │ │ ├── service/ # Business logic
│ │ │ ├── controller/ # REST controllers
│ │ └── resources/
│ │ ├── application.properties.example
│ │ └── graphql/ # *.graphqls schema files
│ └── test/
│ └── java/com/bwg/
│ ├── unit/ # JUnit test cases
│ └── acceptance/ # Cucumber (BDD)
│ ├── config/
│ ├── features/
│ ├── runner/
│ ├── steps/
│ └── utils/
└── pom.xml 



---

## ⚙️ Setup & Configuration

### 1. Clone the repository

```bash
git clone https://github.com/Wikiwow786/Black-Wedding-Guide-bwg.git
cd Black-Wedding-Guide-bwg
```

2. Create application.properties
   cp src/main/resources/application.properties.example src/main/resources/application.properties
Then update values:
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://<db-host>:5432/<db-name>
spring.datasource.username=<db-username>
spring.datasource.password=<db-password>


# Storage config
storage.type=S3
# media.storage.local-path=C:/path/to/local/storage
cloud.aws.s3.bucket=<your-s3-bucket-name>
s3.folder=app_data/

▶️ Running the App
mvn clean spring-boot:run

App will be available at:
http://localhost:8080/api/v1

📡 API Access
OpenAPI/Swagger:
http://localhost:8080/api-docs

🧪 Testing
JUnit Unit Tests
mvn test

Cucumber BDD Tests
mvn verify -Dcucumber.options="classpath:features"
✅ Cucumber tests simulate real business scenarios.

📦 Features
✅ Vendor registration and service listings

✅ Booking, payment & wishlist system

✅ Wedding profile for users

✅ GraphQL & REST APIs

✅ JWT secured endpoints

✅ S3 image upload support

✅ Admin/user role-based access

✅ Fully tested using JUnit + Cucumber
