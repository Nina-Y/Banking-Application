package com.example.banking.controller;

import com.example.banking.model.BankAccount;
import com.example.banking.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts") // http://localhost:8080/swagger-ui/index.html // https://banking-application-53wg.onrender.com/swagger-ui/index.html
public class BankAccountController {

    @Autowired
    private BankService bankService;

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

    @PostMapping("/transfer/external")
    public ResponseEntity<Object> transfer(@RequestParam String fromAccountNumber, String toAccountNumber, double amount) {

        return bankService.transfer(fromAccountNumber, toAccountNumber, amount);
    }

    @PostMapping("/receive")
    public ResponseEntity<Object> receiveTransfer(@RequestParam String fromAccountNumber, String toAccountNumber, double amount) {
        return bankService.receiveTransfer(fromAccountNumber, toAccountNumber, amount);
    }

    @GetMapping("/public")
    public ResponseEntity<Object> getAllAccounts() {
        return bankService.getAllAccounts();
    }
}
