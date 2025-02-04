package com.example.banking.repository;

import com.example.banking.model.BankMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankMappingRepository extends JpaRepository<BankMapping, String> {
    BankMapping findByBankPrefix(String bankPrefix);
}
