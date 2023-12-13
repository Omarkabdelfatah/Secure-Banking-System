package com.musdon.SecureBankingSystem.service.impl;

import com.musdon.SecureBankingSystem.dto.*;
import com.musdon.SecureBankingSystem.entity.User;
import com.musdon.SecureBankingSystem.repository.UserRepository;
import com.musdon.SecureBankingSystem.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    TransactionService transactionService;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        /**
         * Creating an account - saving a new user into the db
         * Check if the user has an account
         */
        if(userRepository.existsByEmail(userRequest.getEmail())){
            BankResponse response = BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
            return response;
        }

        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .email(userRequest.getEmail())
                .accountBalance(BigDecimal.ZERO)
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(newUser);

        // send email alert
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("CONGRATULATIONS! Your Account has been Created Successfully," +
                                " \nYour Account Details: \n" +
                "Account Name: " + savedUser.getFirstName()+" "+savedUser.getLastName()+
                        "\nAccount number: "+savedUser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() +" "+savedUser.getLastName()+" "+savedUser.getOtherName())
                        .build())
                .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        // check if the account number exists in the db
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(foundUser.getAccountNumber())
                        .accountName(foundUser.getFirstName()+" "+foundUser.getLastName())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        // check if the account number exists in the db
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist){
            return AccountUtils.ACCOUNT_EXISTS_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName()+ " " + foundUser.getLastName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        // check if the account number exists in the db
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);

        // save the transaction
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();

        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName()+" "+userToCredit.getLastName())
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(userToCredit.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        // check uf the account exists
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());

        // check if the amount you intended to withdraw is not more than the current balance
        BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmount().toBigInteger();

        if(availableBalance.intValue() < debitAmount.intValue() ){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            userRepository.save(userToDebit);

            // save the transaction
            TransactionDto transactionDto = TransactionDto.builder()
                    .accountNumber(userToDebit.getAccountNumber())
                    .transactionType("DEBIT")
                    .amount(request.getAmount())
                    .build();

            transactionService.saveTransaction(transactionDto);

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountName(userToDebit.getFirstName()+" "+userToDebit.getLastName())
                            .accountBalance(userToDebit.getAccountBalance())
                            .accountNumber(userToDebit.getAccountNumber())
                            .build())
                    .build();
        }
    }

    @Override
    public BankResponse transfer(TransferRequest request) {
        //get the account to debit (check if it exists)
        //check the amount i'm debited is not more than the current balance
        //debit the account
        //get the account to credit
        //credit the account
        boolean isDestinationAccountExist = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        if(!isDestinationAccountExist){
                return BankResponse.builder()
                        .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                        .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                        .accountInfo(null)
                        .build();
        }

        User sourceAccountUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());
        if(request.getAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        // Debit the source account
        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(sourceAccountUser);

        String sourceUserName = sourceAccountUser.getFirstName()+" "+sourceAccountUser.getLastName()+" "+sourceAccountUser.getOtherName();

        // send email alert to the debited account
        EmailDetails debitAlert = EmailDetails.builder()
                .subject("DEBIT ALERT")
                .recipient(sourceAccountUser.getEmail())
                .messageBody("The Amount : "+request.getAmount()+" has been deducted from your balance! \nYour Current Balance is : "+sourceAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlert(debitAlert);

        // Credit the destination account
        User destinationAccountUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getAmount()));
        userRepository.save(destinationAccountUser);

        //String recipientUserName = destinationAccountUser.getFirstName()+" "+destinationAccountUser.getLastName()+" "+destinationAccountUser.getOtherName();

        // Send email alert to the credited account
        EmailDetails creditAlert = EmailDetails.builder()
                .subject("CREDIT ALERT")
                .recipient(destinationAccountUser.getEmail())
                .messageBody("The Amount : "+request.getAmount() + " has been sent to your account from : "+sourceUserName+"\nYour Current Balance is : "+destinationAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlert(creditAlert);

        // save the transaction
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(destinationAccountUser.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();

        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                .accountInfo(null)
                .build();
    }


}
