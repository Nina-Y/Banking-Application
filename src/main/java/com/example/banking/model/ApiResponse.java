package com.example.banking.model;

public class ApiResponse {
    private String message;
    private double balance;
    private String accountNumber;

    public ApiResponse(String message, double balance, String accountNumber) {
        this.message = message;
        this.balance = balance;
        this.accountNumber = accountNumber;
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
