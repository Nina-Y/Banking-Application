package com.example.banking.controller;

import com.example.banking.model.BankAccount;
import com.example.banking.service.BankService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/v1/accounts") // http://localhost:8080/swagger-ui/index.html // https://banking-application-53wg.onrender.com/swagger-ui/index.html
public class BankAccountController {

    private static final Logger logger = LoggerFactory.getLogger(BankService.class);

    private final RestTemplate restTemplate;

    public BankAccountController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    private BankService bankService;

    /*@PostMapping("/transfer/external")
    public ResponseEntity<Object> transferExternal(@RequestParam String fromAccountNumber,String toAccountNumber, double amount) {
        return bankService.transferToExternalBank(fromAccountNumber, toAccountNumber, amount);
    }*/

    @PostMapping("/transfer/external")
    public ResponseEntity<Object> transferExternal(@RequestBody String fromAccountNumber,String toAccountNumber, double amount) {
        logger.info("Transfer started");
        return bankService.receiveTransferFromExternal(toAccountNumber, amount);
    }

    @PostMapping("/transfer/internal")
    public ResponseEntity<Object> transfer(@RequestParam String fromAccountNumber, String toAccountNumber, double amount) {

        return bankService.transferInternal(fromAccountNumber, toAccountNumber, amount);
    }

    @PostMapping("/addNewAccount")
    public ResponseEntity<Object> createAccount(@RequestBody BankAccount newAccount) {
        return bankService.addNewAccount(newAccount);
    }

    @DeleteMapping("/deleteAccount")
    public ResponseEntity<Object> deleteAccount(@RequestParam String accountNumber) {
        return bankService.deleteAccount(accountNumber);
    }

    @PostMapping("/deposit")
    public ResponseEntity<Object> deposit(@RequestParam String accountNumber, double amount) {

        return bankService.deposit(accountNumber, amount);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Object> withdraw(@RequestParam String accountNumber, double amount) {

        return bankService.withdraw(accountNumber, amount);
    }

    @GetMapping("/balance")
    public ResponseEntity<Object> getBalance(@RequestParam String accountNumber) {

        return bankService.getBalance(accountNumber);
    }

    /*@PostMapping("/receive")
    public ResponseEntity<Object> receiveTransfer(@RequestParam String fromAccountNumber, String toAccountNumber, double amount) {
        return bankService.receiveTransfer(fromAccountNumber, toAccountNumber, amount);
    }*/

    @GetMapping("/public")
    public ResponseEntity<Object> getAllAccounts() {
        return bankService.getAllAccounts();
    }

    @GetMapping("/internal")
    public String getInternalBank() {
        return "Hello from bank of Nina!";
    }

    @GetMapping("/external")
    public String getExternalBank()  {
        return restTemplate.getForObject("https://banking-application-53wg.onrender.com/api/v1/accounts/internal", String.class);
    }
}
