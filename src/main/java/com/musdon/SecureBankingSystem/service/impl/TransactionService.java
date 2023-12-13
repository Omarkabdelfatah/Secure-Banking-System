package com.musdon.SecureBankingSystem.service.impl;

import com.musdon.SecureBankingSystem.dto.TransactionDto;
import com.musdon.SecureBankingSystem.entity.Transaction;

public interface TransactionService {
    void saveTransaction(TransactionDto transactionDto);
}
