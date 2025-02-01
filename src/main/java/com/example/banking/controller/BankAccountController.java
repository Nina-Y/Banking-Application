package com.example.banking.controller;

import com.example.banking.dto.TransactionDto;
import com.example.banking.model.ApiResponse;
import com.example.banking.model.BankAccount;
import com.example.banking.service.BankService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

// http://localhost:8080/swagger-ui/index.html
// https://banking-application-53wg.onrender.com/swagger-ui/index.html
// https://banking-application-53wg.onrender.com/api/v1/accounts/public

@RestController
@RequestMapping("/api/v1/accounts")
public class BankAccountController {

    private static final Logger logger = LoggerFactory.getLogger(BankService.class);

    private final RestTemplate restTemplate;

    public BankAccountController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    private BankService bankService;

    @PostMapping("/transfer/external")
    public ResponseEntity<Object> transferExternal(@Valid @RequestBody TransactionDto transactionDto) {
        logger.info("Processing external transfer from {} to {} for amount {}",
                transactionDto.getFromAccountNumber(), transactionDto.getToAccountNumber(), transactionDto.getAmount());

        if (transactionDto.getToAccountNumber().equals("YUSNIN_abcdef123456")) {
            return bankService.receiveTransferFromExternal(transactionDto.getToAccountNumber(), transactionDto.getAmount());
        }

        String recipientBankPrefix = transactionDto.getToAccountNumber().substring(0, 6);
        String externalApiUrl = bankService.getExternalBankUrl(recipientBankPrefix);

        if (externalApiUrl == null) {
            logger.error("No external API URL found for bank prefix: {}", recipientBankPrefix);
            return ResponseEntity.badRequest().body(new ApiResponse("No external API found for the recipient bank."));
        }

        return bankService.transferToExternalBank(transactionDto.getFromAccountNumber(), transactionDto.getToAccountNumber(),
                transactionDto.getAmount(), externalApiUrl);
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
