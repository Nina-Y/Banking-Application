## Banking Application

### Spring Boot application providing basic banking functionalities:

- Deposit funds
- Withdraw funds
- Check balance 
- Transfer funds 
- Receive funds

### Endpoints

Base URL: http://localhost:8080/api/account

### Key Endpoints

- Deposit: POST /deposit?amount={value}
- Withdraw: POST /withdraw?amount={value}
- Check Balance: GET /balance
- Transfer: POST /transfer?amount={value}
- Receive: POST /receive?amount={value}

### How to Run

1. Clone the repository: git clone <repository-url>
2. Build and run: mvn spring-boot:run

Access via browser or Postman at http://localhost:8080.

### Technologies Used

Java 21
Spring Boot
REST API

### Future Enhancements

- Database integration
- User authentication
- Improved error handling
- Banking system integration (interaction with other groupmates)