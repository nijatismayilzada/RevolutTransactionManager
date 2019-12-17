package com.revolut.revoluttransactionmanager.util;

import com.revolut.revoluttransactionmanager.model.request.TransactionRequest;
import com.revolut.revoluttransactionmanager.model.transaction.Transaction;
import com.revolut.revoluttransactionmanager.model.transaction.TransactionType;

import java.math.BigDecimal;
import java.util.Currency;

public class TestHelper {

    public static Transaction getTransaction(long transactionId, long accountId, BigDecimal amount, Currency currency) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setAccountId(accountId);
        transaction.setAmount(amount);
        transaction.setCurrency(currency);
        transaction.setTransactionType(TransactionType.REVOLUT_SIMPLE_INCREASE);
        return transaction;
    }

    public static TransactionRequest getTransactionRequest(long accountId, BigDecimal amount, Currency currency, String reference, TransactionType transactionType) {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAccountId(accountId);
        transactionRequest.setAmount(amount);
        transactionRequest.setCurrency(currency);
        transactionRequest.setReference(reference);
        transactionRequest.setTransactionType(transactionType);
        return transactionRequest;
    }
}
