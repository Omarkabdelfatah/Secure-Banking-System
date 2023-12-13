package com.musdon.SecureBankingSystem.repository;

import com.musdon.SecureBankingSystem.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction,String> {

}
