package com.musdon.SecureBankingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.query.criteria.JpaSimpleCase;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankResponse {

    private String responseCode;
    private String responseMessage;
    private AccountInfo accountInfo;
}
