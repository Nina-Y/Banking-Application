package com.example.banking.service;

import com.example.banking.model.ApiResponse;
import com.example.banking.model.BankAccount;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class BankService {

    private final BankAccount myAccount = new BankAccount(0.0, "MYBANK_abcdef12345678");
    private final BankAccount recipientAccount = new BankAccount(1000.0, "YOURBK_abcdef87654321");

    public ResponseEntity<Object> deposit(double amount) {
        if (amount > 0) {
            myAccount.setBalance(myAccount.getBalance() + amount);
            return ResponseEntity.ok(
                    new ApiResponse("Deposit successful", myAccount.getBalance(), myAccount.getAccountNumber())
            );
        }
        return ResponseEntity.badRequest().body(new ApiResponse("Deposit amount must be positive"));
    }

    public ResponseEntity<Object> withdraw(double amount) {
        if (amount > 0 && amount <= myAccount.getBalance()) {
            myAccount.setBalance(myAccount.getBalance() - amount);
            return ResponseEntity.ok(
                    new ApiResponse("Withdrawal successful", myAccount.getBalance(), myAccount.getAccountNumber())
            );
        }
        return ResponseEntity.badRequest().body(new ApiResponse(
                "Withdrawal failed: insufficient balance or invalid amount", myAccount.getBalance(), myAccount.getAccountNumber()
        ));
    }

    public ResponseEntity<Object> getBalance() {
        return ResponseEntity.ok(
                new ApiResponse("Balance retrieved", myAccount.getBalance(), myAccount.getAccountNumber())
        );
    }

    public ResponseEntity<Object> transfer(double amount) {
        if (amount > 0 && amount <= myAccount.getBalance()) {
            myAccount.setBalance(myAccount.getBalance() - amount);
            recipientAccount.setBalance(recipientAccount.getBalance() + amount);
            return ResponseEntity.ok(
                    new ApiResponse(
                            "Transfer successful",
                            myAccount.getBalance(),
                            myAccount.getAccountNumber()
                    )
            );
        }
        return ResponseEntity.badRequest().body(new ApiResponse("Transfer failed: insufficient funds or invalid amount", myAccount.getBalance(), myAccount.getAccountNumber()));
    }

    public ResponseEntity<Object> receiveTransfer(BankAccount sender, double amount) {
        if (amount > 0 && sender.getBalance() >= amount) {
            sender.setBalance(sender.getBalance() - amount);
            myAccount.setBalance(myAccount.getBalance() + amount);
            return ResponseEntity.ok(
                    new ApiResponse("Received transfer successfully", myAccount.getBalance(), myAccount.getAccountNumber())
            );
        }
        return ResponseEntity.badRequest().body(new ApiResponse("Transfer failed: invalid amount or insufficient sender balance", myAccount.getBalance(), myAccount.getAccountNumber()));
    }

    public BankAccount getRecipientAccount() {
        return recipientAccount;
    }
}
