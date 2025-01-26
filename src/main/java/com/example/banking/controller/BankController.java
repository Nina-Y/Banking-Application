package com.example.banking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RequestMapping("/api/v1/bank")
@RestController
public class BankController {

    private final RestTemplate restTemplate;

    public BankController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/internal")
    public String getInternalBank() {
        return "Hello from bank of Nina!";
    }

    @GetMapping("/external")
    public String getExternalBank() {
        return restTemplate.getForObject("https://bank-zmcj.onrender.com/api/v1/bank/internal", String.class);
    }

}
