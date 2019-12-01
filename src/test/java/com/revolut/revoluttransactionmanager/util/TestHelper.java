package com.revolut.revoluttransactionmanager.util;

import com.revolut.revoluttransactionmanager.model.request.TransactionRequest;
import com.revolut.revoluttransactionmanager.model.transaction.Transaction;
import com.revolut.revoluttransactionmanager.model.transaction.TransactionAction;
import com.revolut.revoluttransactionmanager.model.transaction.TransactionType;

import java.math.BigDecimal;
import java.util.Currency;

public class TestHelper {

    public static Transaction getTransaction(long transactionId, long accountId, BigDecimal amount, Currency currency, TransactionAction transactionAction) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setAccountId(accountId);
        transaction.setAmount(amount);
        transaction.setCurrency(currency);
        transaction.setTransactionAction(transactionAction);
        transaction.setTransactionType(TransactionType.REVOLUT_SIMPLE);
        return transaction;
    }

    public static TransactionRequest getTransactionRequest(long accountId, BigDecimal amount, Currency currency, String reference, TransactionAction transactionAction, TransactionType transactionType) {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAccountId(accountId);
        transactionRequest.setAmount(amount);
        transactionRequest.setCurrency(currency);
        transactionRequest.setReference(reference);
        transactionRequest.setTransactionAction(transactionAction);
        transactionRequest.setTransactionType(transactionType);
        return transactionRequest;
    }
}
