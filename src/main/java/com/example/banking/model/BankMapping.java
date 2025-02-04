package com.example.banking.model;

import jakarta.persistence.*;

@Entity
@Table(name = "bank_mappings")
public class BankMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String bankPrefix;

    @Column(nullable = false)
    private String apiUrl;

    @Column(nullable = true)
    private String exampleAccounts;

    public BankMapping() {}

    public BankMapping(String bankPrefix, String apiUrl, String exampleAccounts) {
        this.bankPrefix = bankPrefix;
        this.apiUrl = apiUrl;
        this.exampleAccounts = exampleAccounts;
    }

    public Long getId() { return id; }
    public String getBankPrefix() { return bankPrefix; }
    public String getApiUrl() { return apiUrl; }
    public String getExampleAccounts() { return exampleAccounts; }

    public void setId(Long id) { this.id = id; }
    public void setBankPrefix(String bankPrefix) { this.bankPrefix = bankPrefix; }
    public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }
    public void setExampleAccounts(String exampleAccounts) { this.exampleAccounts = exampleAccounts; }
}
