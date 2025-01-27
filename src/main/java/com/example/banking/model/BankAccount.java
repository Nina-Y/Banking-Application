package com.example.banking.model;

import jakarta.validation.constraints.Pattern;

public class BankAccount {

    private double balance;

    @Pattern(regexp = "[A-Z]{6}_[a-z0-9]{12}", message = "Invalid account number format.")
    private String accountNumber;

    public BankAccount() {
        this.balance = 0.0;
    }

    public BankAccount(double balance, String accountNumber) {
        this.balance = balance;
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public @Pattern(regexp = "[A-Z]{6}_[a-z0-9]{12}", message = "Invalid account number format.") String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(@Pattern(regexp = "[A-Z]{6}_[a-z0-9]{12}", message = "Invalid account number format.") String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
