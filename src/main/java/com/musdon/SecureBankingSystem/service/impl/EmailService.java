package com.musdon.SecureBankingSystem.service.impl;

import com.musdon.SecureBankingSystem.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
    void sendEmailWithAttachment(EmailDetails emailDetails);
}
