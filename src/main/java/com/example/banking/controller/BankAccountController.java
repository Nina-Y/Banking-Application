package com.example.banking.controller;

import com.example.banking.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account") // http://localhost:8080/swagger-ui/index.html // https://banking-application-0xkl.onrender.com/swagger-ui/index.html
public class BankAccountController {

    @Autowired
    private BankService bankService;

    @PostMapping("/deposit")
    public ResponseEntity<Object> deposit(@RequestParam double amount) {
        return bankService.deposit(amount);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Object> withdraw(@RequestParam double amount) {
        return bankService.withdraw(amount);
    }

    @GetMapping("/balance")
    public ResponseEntity<Object> getBalance() {
        return bankService.getBalance();
    }

    @PostMapping("/transfer")
    public ResponseEntity<Object> transfer(@RequestParam double amount) {
        return bankService.transfer(amount);
    }

    @PostMapping("/receive")
    public ResponseEntity<Object> receiveTransfer(@RequestParam double amount) {
        return bankService.receiveTransfer(bankService.getRecipientAccount(), amount);
    }
}
