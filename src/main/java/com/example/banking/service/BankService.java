package com.example.banking.service;

import com.example.banking.model.ApiResponse;
import com.example.banking.model.BankAccount;
import com.example.banking.repository.BankAccountRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class BankService {

    private final BankAccountRepository bankAccountRepository;
    private final RestTemplate restTemplate;

    public BankService(BankAccountRepository bankAccountRepository, RestTemplate restTemplate) {
        this.bankAccountRepository = bankAccountRepository;
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void prepopulateAccounts() {
        if (bankAccountRepository.findByAccountNumber("NINYUS_abcdef123456") == null) {
            bankAccountRepository.save(new BankAccount("NINYUS_abcdef123456", 0.0));
        }
        if (bankAccountRepository.findByAccountNumber("NAMSUR_abcdef654321") == null) {
            bankAccountRepository.save(new BankAccount("NAMSUR_abcdef654321", 0.0));
        }
    }

    public ResponseEntity<Object> transferToExternalBank(String senderAccountNumber, String recipientAccountNumber, double amount) {

        BankAccount sender = bankAccountRepository.findByAccountNumber(senderAccountNumber);
        if (sender == null || sender.getBalance() < amount) {
            return ResponseEntity.badRequest().body("Sender account not found or insufficient funds.");
        }

        String externalApiUrl = "https://banking-application-53wg.onrender.com/api/v1/accounts/transfer/external"; // me (test)

        String requestBody = String.format(
                "{\"fromAccountNumber\": \"%s\", \"toAccountNumber\": \"%s\", \"amount\": %.2f}",
                senderAccountNumber, recipientAccountNumber, amount
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(externalApiUrl, HttpMethod.POST, request, String.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to connect to external bank: " + e.getMessage());
        }

        if (response.getStatusCode().is2xxSuccessful()) {
            sender.setBalance(sender.getBalance() - amount);
            bankAccountRepository.save(sender);
            return ResponseEntity.ok("Transfer successful: " + response.getBody());
        } else {
            return ResponseEntity.badRequest().body("Transfer failed: " + response.getBody());
        }
    }

    public ResponseEntity<Object> transferInternal(String senderAccountNumber, String recipientAccountNumber, double amount) {
        BankAccount sender = bankAccountRepository.findByAccountNumber(senderAccountNumber);
        BankAccount recipient = bankAccountRepository.findByAccountNumber(recipientAccountNumber);

        if (sender == null || recipient == null) {
            return ResponseEntity.badRequest().body(new ApiResponse("One or both accounts not found"));
        }

        if (amount > 0 && amount <= sender.getBalance()) {
            sender.setBalance(sender.getBalance() - amount);
            recipient.setBalance(recipient.getBalance() + amount);
            bankAccountRepository.save(sender);
            bankAccountRepository.save(recipient);
            return ResponseEntity.ok(
                    new ApiResponse("Transfer successful", sender.getAccountNumber(), sender.getBalance())
            );
        }

        return ResponseEntity.badRequest().body(new ApiResponse("Transfer failed: insufficient funds or invalid amount"));
    }

    public ResponseEntity<Object> addNewAccount(BankAccount newAccount) {
        if (bankAccountRepository.findByAccountNumber(newAccount.getAccountNumber()) != null) {
            return ResponseEntity.badRequest().body("Account already exists!");
        }
        bankAccountRepository.save(newAccount);
        return ResponseEntity.ok("New account added successfully!");
    }

    public ResponseEntity<Object> deleteAccount(String accountNumber) {
        BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            return ResponseEntity.badRequest().body("Account not found!");
        }
        bankAccountRepository.delete(account);
        return ResponseEntity.ok("Account deleted successfully!");
    }

    public ResponseEntity<Object> deposit(String accountNumber, double amount) {
        BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            return ResponseEntity.badRequest().body(new ApiResponse("Account not found"));
        }

        if (amount > 0) {
            account.setBalance(account.getBalance() + amount);
            bankAccountRepository.save(account);
            return ResponseEntity.ok(
                    new ApiResponse("Deposit successful", account.getAccountNumber(), account.getBalance())
            );
        }

        return ResponseEntity.badRequest().body(new ApiResponse("Deposit amount must be positive"));
    }

    public ResponseEntity<Object> withdraw(String accountNumber, double amount) {
        BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            return ResponseEntity.badRequest().body(new ApiResponse("Account not found"));
        }

        if (amount > 0 && amount <= account.getBalance()) {
            account.setBalance(account.getBalance() - amount);
            bankAccountRepository.save(account);
            return ResponseEntity.ok(
                    new ApiResponse("Withdrawal successful", account.getAccountNumber(), account.getBalance())
            );
        }

        return ResponseEntity.badRequest().body(
                new ApiResponse("Withdrawal failed: insufficient balance or invalid amount")
        );
    }

    public ResponseEntity<Object> getBalance(String accountNumber) {
        BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            return ResponseEntity.badRequest().body(new ApiResponse("Account not found"));
        }

        return ResponseEntity.ok(
                new ApiResponse("Balance retrieved", account.getAccountNumber(), account.getBalance())
        );
    }

    public ResponseEntity<Object> receiveTransfer(String senderAccountNumber, String recipientAccountNumber, double amount) {
        BankAccount sender = bankAccountRepository.findByAccountNumber(senderAccountNumber);
        BankAccount recipient = bankAccountRepository.findByAccountNumber(recipientAccountNumber);

        if (sender == null || recipient == null) {
            return ResponseEntity.badRequest().body(new ApiResponse("One or both accounts not found"));
        }

        if (amount > 0 && sender.getBalance() >= amount) {
            sender.setBalance(sender.getBalance() - amount);
            recipient.setBalance(recipient.getBalance() + amount);
            bankAccountRepository.save(sender);
            bankAccountRepository.save(recipient);
            return ResponseEntity.ok(
                    new ApiResponse("Received transfer successfully", recipient.getAccountNumber(), recipient.getBalance())
            );
        }

        return ResponseEntity.badRequest().body(new ApiResponse("Transfer failed: invalid amount or insufficient sender balance"));
    }

    public ResponseEntity<Object> getAllAccounts() {
        List<BankAccount> accounts = bankAccountRepository.findAll();
        if (accounts.isEmpty()) {
            return ResponseEntity.ok("No accounts found!");
        }
        return ResponseEntity.ok(accounts);
    }
}
