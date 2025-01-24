package com.example.banking.controller;

import com.example.banking.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class BankAccountController {

    @Autowired
    private BankService bankService;

    @PostMapping("/deposit")
    public String deposit(@RequestParam double amount) {
        return bankService.deposit(amount);
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam double amount) {
        return bankService.withdraw(amount);
    }

    @GetMapping("/balance")
    public String getBalance() {
        return bankService.getBalance();
    }

    @PostMapping("/transfer")
    public String transfer(@RequestParam double amount) {
        return bankService.transfer(amount);
    }

    @PostMapping("/receive")
    public String receiveTransfer(@RequestParam double amount) {
        return bankService.receiveTransfer(bankService.getRecipientAccount(), amount);
    }
}
