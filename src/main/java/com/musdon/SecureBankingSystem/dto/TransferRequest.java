package com.musdon.SecureBankingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {
    // account to be debited
    public String sourceAccountNumber;
    // account to be credited
    public String destinationAccountNumber;
    public BigDecimal amount;
}
