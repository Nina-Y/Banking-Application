package com.example.banking.service;

import com.example.banking.model.BankAccount;
import org.springframework.stereotype.Service;

@Service
public class BankService {

    private final BankAccount myAccount = new BankAccount(0.0);
    private final BankAccount recipientAccount = new BankAccount(1000.0);

    public String deposit(double amount) {
        if (amount > 0) {
            myAccount.setBalance(myAccount.getBalance() + amount); // Update balance
            return "Successfully deposited: EUR" + amount + ". New Balance: EUR" + myAccount.getBalance();
        } else {
            return "Deposit amount must be positive.";
        }
    }

    public String withdraw(double amount) {
        if (amount > 0 && amount <= myAccount.getBalance()) {
            myAccount.setBalance(myAccount.getBalance() - amount); // Update balance
            return "Successfully withdrew: EUR" + amount + ". New Balance: EUR" + myAccount.getBalance();
        } else if (amount > myAccount.getBalance()) {
            return "Insufficient balance. Current Balance: EUR" + myAccount.getBalance();
        } else {
            return "Withdrawal amount must be positive.";
        }
    }

    public String getBalance() {
        return "Current Balance: EUR" + myAccount.getBalance();
    }

    public String transfer(double amount) {
        if (amount > 0 && amount <= myAccount.getBalance()) {
            myAccount.setBalance(myAccount.getBalance() - amount);
            recipientAccount.setBalance(recipientAccount.getBalance() + amount);
            return "Successfully transferred: EUR" + amount + ". Your New Balance: EUR" + myAccount.getBalance() +
                    ". Recipient Balance: EUR" + recipientAccount.getBalance();
        } else {
            return "Transfer failed. Please check the amount and try again.";
        }
    }

    public String receiveTransfer(BankAccount sender, double amount) {
        if (amount > 0 && sender.getBalance() >= amount) {
            sender.setBalance(sender.getBalance() - amount);
            myAccount.setBalance(myAccount.getBalance() + amount);
            return "Successfully received transfer of: EUR" + amount + ". Your New Balance: EUR" + myAccount.getBalance() +
                    ". Sender's New Balance: EUR" + sender.getBalance();
        } else if (amount > sender.getBalance()) {
            return "Transfer failed. Sender does not have sufficient balance. Sender's Current Balance: EUR" + sender.getBalance();
        } else {
            return "Transfer amount must be positive.";
        }
    }

    public BankAccount getRecipientAccount() {
        return recipientAccount;
    }
}
