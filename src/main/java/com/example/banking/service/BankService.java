package com.example.banking.service;

import com.example.banking.model.ApiResponse;
import com.example.banking.model.BankAccount;
import com.example.banking.model.BankMapping;
import com.example.banking.repository.BankAccountRepository;
import com.example.banking.repository.BankMappingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class BankService {

    private static final Logger logger = LoggerFactory.getLogger(BankService.class);

    @Value("${bank.account.number}")
    private String myAccountNumber;

    public String getMyAccountNumber() {
        return myAccountNumber;
    }

    private final BankAccountRepository bankAccountRepository;
    private final BankMappingRepository bankMappingRepository;
    private final RestTemplate restTemplate;

    public BankService(BankAccountRepository bankAccountRepository, BankMappingRepository bankMappingRepository, RestTemplate restTemplate) {
        this.bankAccountRepository = bankAccountRepository;
        this.bankMappingRepository = bankMappingRepository;
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void prepopulateAccounts() {
        if (bankAccountRepository.findByAccountNumber("YUSNIN_abcdef123456") == null) {
            bankAccountRepository.save(new BankAccount("YUSNIN_abcdef123456", 1000.0));
        }
        if (bankAccountRepository.findByAccountNumber("SURNUM_abcdef654321") == null) {
            bankAccountRepository.save(new BankAccount("SURNUM_abcdef654321", 1000.0));
        }
    }

    @PostConstruct
    public void initBankMappings() {
        if (bankMappingRepository.findByBankPrefix("SOLDEN") == null) {
            bankMappingRepository.save(new BankMapping(
                    "SOLDEN",
                    "http://13.60.62.171/api/v1/accounts/transfer/external",
                    "SOLDEN_05747398874c,SOLDEN_b000a5fe147f"
            ));
        }
        if (bankMappingRepository.findByBankPrefix("SURNAM") == null) { // my, for tests
            bankMappingRepository.save(new BankMapping(
                    "SURNAM",
                    "https://banking-application-53wg.onrender.com/api/v1/accounts/transfer/external",
                    "SURNUM_abcdef654321"
            ));
        }
    }

    public ResponseEntity<Object> transferToExternalBank(String fromAccountNumber, String toAccountNumber, double amount, String externalApiUrl) {
        logger.info("Initiating outgoing transfer from {} to {} for amount: {}", fromAccountNumber, toAccountNumber, amount);

        BankAccount sender = bankAccountRepository.findByAccountNumber(fromAccountNumber);
        if (sender == null || sender.getBalance() < amount) {
            logger.warn("Transfer failed: Sender account not found or insufficient funds.");
            return ResponseEntity.badRequest().body(new ApiResponse("Sender account not found or insufficient funds"));
        }

        String requestBody;
        try {
            requestBody = new ObjectMapper().writeValueAsString(Map.of(
                    "fromAccountNumber", fromAccountNumber,
                    "toAccountNumber", toAccountNumber,
                    "amount", amount
            ));
        } catch (Exception e) {
            logger.error("Failed to generate JSON: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse("Error generating JSON payload"));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        logger.info("Sending request to external API: {}", externalApiUrl);
        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(externalApiUrl, HttpMethod.POST, request, String.class);
            logger.info("Received response from external API: {}", response.getBody());
        } catch (Exception e) {
            logger.error("Failed to connect to external bank: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse("Failed to connect to external bank: " + e.getMessage()));
        }

        if (response.getStatusCode().is2xxSuccessful()) {
            sender.setBalance(sender.getBalance() - amount);
            bankAccountRepository.save(sender);
            logger.info("Transfer successful! New balance: {}", sender.getBalance());
            return ResponseEntity.ok(new ApiResponse("Transfer successful", sender.getAccountNumber(), sender.getBalance()));
        } else {
            logger.error("Transfer failed. HTTP Status: {}, Response: {}", response.getStatusCode(), response.getBody());
            return ResponseEntity.badRequest().body(new ApiResponse("Transfer failed: " + response.getBody()));
        }
    }

    public ResponseEntity<Object> receiveTransferFromExternal(String toAccountNumber, double amount) {
        logger.info("Receiving external transfer to {} for amount: {}", toAccountNumber, amount);

        BankAccount recipient = bankAccountRepository.findByAccountNumber(toAccountNumber);
        if (recipient == null) {
            logger.error("Recipient account not found");
            return ResponseEntity.badRequest().body(new ApiResponse("Recipient account not found"));
        }

        if (amount > 0) {
            recipient.setBalance(recipient.getBalance() + amount);
            bankAccountRepository.save(recipient);
            logger.info("Received transfer successfully! New balance: {}", recipient.getBalance());
            return ResponseEntity.ok(new ApiResponse("Received transfer successfully", recipient.getAccountNumber(), recipient.getBalance()));
        }

        return ResponseEntity.badRequest().body(new ApiResponse("Transfer failed: invalid amount"));
    }

    public String getExternalBankUrl(String bankPrefix) {
        BankMapping bankMapping = bankMappingRepository.findByBankPrefix(bankPrefix);
        return (bankMapping != null) ? bankMapping.getApiUrl() : null;
    }

    public String getExampleAccounts(String bankPrefix) {
        BankMapping bankMapping = bankMappingRepository.findByBankPrefix(bankPrefix);
        return (bankMapping != null) ? bankMapping.getExampleAccounts() : null;
    }

    public ResponseEntity<Object> transferInternal(String fromAccountNumber, String toAccountNumber, double amount) {
        BankAccount sender = bankAccountRepository.findByAccountNumber(fromAccountNumber);
        BankAccount recipient = bankAccountRepository.findByAccountNumber(toAccountNumber);

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

    public ResponseEntity<Object> getAllAccounts() {
        List<BankAccount> accounts = bankAccountRepository.findAll();
        if (accounts.isEmpty()) {
            return ResponseEntity.ok("No accounts found!");
        }
        return ResponseEntity.ok(accounts);
    }
}
