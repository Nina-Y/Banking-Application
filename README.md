## Banking Application

### Overview
A Spring Boot application providing essential banking functionalities, including deposits, withdrawals, balance checks, and fund transfers (internal and external).

### **Endpoints**
**Base URL (Localhost):** `http://localhost:8080/api/v1/accounts`  
**Base URL (Deployed):** `https://banking-application-53wg.onrender.com/api/v1/accounts`

#### **Key Endpoints**
- **Deposit:** `POST /deposit`
- **Withdraw:** `POST /withdraw`
- **Check Balance:** `GET /balance?accountNumber={accountNumber}`
- **Internal Transfer:** `POST /transfer/internal`
- **External Transfer:** `POST /transfer/external`
- **Receive Funds:** `POST /receive`
- **List Public Accounts:** `GET /public`

### **Running the Application**
1. Clone repository: `git clone <repository-url>`
2. Navigate to project: `cd banking-application`
3. Build & run: `mvn spring-boot:run`
4. Access API via: `http://localhost:8080/swagger-ui/index.html`

### **Technologies Used**
- Java 21, Spring Boot, REST API
- PostgreSQL (Remote database hosted on Render)
- HTTP Client (for external API calls)
- Render (Cloud Deployment)

### **Future Enhancements**
- Authentication & role-based access
- Improved error handling
- Enhanced external bank integrations