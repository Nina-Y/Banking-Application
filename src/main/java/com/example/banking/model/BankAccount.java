package com.example.banking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "bank_accounts")
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

        @Pattern(regexp = "[A-Z]{6}_[a-z0-9]{12}", message = "Invalid account number format.")
    private String accountNumber;

    private double balance;

    public BankAccount() {}

    public BankAccount(String accountNumber, double balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
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

    public Long getId() {
        return id;
    }
}
