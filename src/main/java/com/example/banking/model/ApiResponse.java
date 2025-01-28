package com.example.banking.model;

public class ApiResponse {
    private String message;
    private double balance;
    private String accountNumber;

    public ApiResponse(String message, String accountNumber, double balance) {
        this.message = message;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public ApiResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public double getBalance() {
        return balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
