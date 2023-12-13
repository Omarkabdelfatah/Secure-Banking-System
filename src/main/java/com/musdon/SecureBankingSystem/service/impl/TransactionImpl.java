package com.musdon.SecureBankingSystem.service.impl;

import com.musdon.SecureBankingSystem.dto.TransactionDto;
import com.musdon.SecureBankingSystem.entity.Transaction;
import com.musdon.SecureBankingSystem.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class TransactionImpl implements TransactionService{

    @Autowired
    TransactionRepository transactionRepository;
    @Override
    public void saveTransaction(TransactionDto transactionDto) {

        Transaction transaction = Transaction.builder()
                    .transactionType(transactionDto.getTransactionType())
                    .accountNumber(transactionDto.getAccountNumber())
                    .amount(transactionDto.getAmount())
                    .status("SUCCESS!")
                    .build();
        transactionRepository.save(transaction);

        System.out.println("Transaction Saved Successfully");
    }

}
