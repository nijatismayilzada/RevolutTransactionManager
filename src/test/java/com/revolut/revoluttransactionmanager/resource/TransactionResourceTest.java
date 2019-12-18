package com.revolut.revoluttransactionmanager.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.revoluttransactionmanager.model.request.TransactionRequest;
import com.revolut.revoluttransactionmanager.model.transaction.Transaction;
import com.revolut.revoluttransactionmanager.model.transaction.TransactionType;
import com.revolut.revoluttransactionmanager.model.transaction.Transactions;
import com.revolut.revoluttransactionmanager.service.TransactionService;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransactionResourceTest extends JerseyTest {
    private static final long ACCOUNT_ID = -111L;
    private static final long TRANSACTION_ID = -999L;
    private static final String CURRENCY_CODE = "GBP";

    private TransactionService transactionService;
    private TransactionRequest transactionRequest;
    private Transaction transaction;

    @Override
    protected Application configure() {
        transactionService = mock(TransactionService.class);
        return new ResourceConfig().register(new TransactionResource(transactionService));
    }

    @Before
    public void setup() {
        transactionRequest = new TransactionRequest();
        transactionRequest.setAccountId(ACCOUNT_ID);
        transactionRequest.setTransactionType(TransactionType.REVOLUT_SIMPLE_INCREASE);
        transactionRequest.setReference("test");
        transactionRequest.setCurrency(Currency.getInstance(CURRENCY_CODE));
        transactionRequest.setAmount(BigDecimal.TEN);

        transaction = new Transaction();
        transaction.setAccountId(ACCOUNT_ID);
        transaction.setTransactionId(TRANSACTION_ID);
    }

    @Test
    public void createTransaction_whenCreateTransactionRequestPosted_thenCreateNewTransactionId() throws JsonProcessingException {
        when(transactionService.createTransaction(transactionRequest)).thenReturn(TRANSACTION_ID);

        Response response = target("/transactions/create").request()
                .post(Entity.json(new ObjectMapper().writeValueAsString(transactionRequest)));

        assertThat("Http Response should be OK", response.getStatus(), is(OK.getStatusCode()));
        assertThat("Response should contain transaction id " + ACCOUNT_ID, response.readEntity(String.class), is(String.valueOf(TRANSACTION_ID)));
    }

    @Test
    public void createTransaction_whenRepositoryFails_thenRespondInternalServerError() throws JsonProcessingException {
        when(transactionService.createTransaction(transactionRequest)).thenThrow(new RuntimeException());

        Response response = target("/transactions/create").request()
                .post(Entity.json(new ObjectMapper().writeValueAsString(transactionRequest)));

        assertThat("Http Response should be INTERNAL_SERVER_ERROR", response.getStatus(), is(INTERNAL_SERVER_ERROR.getStatusCode()));
    }


    @Test
    public void getTransactionById_whenTransactionIsAvailable_thenReturnTransactionDetails() {
        when(transactionService.getTransactionById(TRANSACTION_ID)).thenReturn(transaction);

        Response response = target("/transactions/transaction-id").path(String.valueOf(TRANSACTION_ID)).request().get();

        assertThat("Http Response should be OK", response.getStatus(), is(OK.getStatusCode()));
        assertThat("Response should contain transaction details: " + transaction.toString(), response.readEntity(Transaction.class), is(transaction));
    }

    @Test
    public void getTransactionById_whenRepositoryFailsToFetchTransactionDetails_thenRespondInternalServerError() {
        when(transactionService.getTransactionById(TRANSACTION_ID)).thenThrow(new RuntimeException());

        Response response = target("/transactions/transaction-id").path(String.valueOf(TRANSACTION_ID)).request().get();

        assertThat("Http Response should be INTERNAL_SERVER_ERROR", response.getStatus(), is(INTERNAL_SERVER_ERROR.getStatusCode()));
    }


    @Test
    public void getTransactionsByAccountId_whenTransactionsOfAccountAreAvailable_thenReturnAllTransactionDetails() {
        Transactions transactions = new Transactions();
        transactions.setTransactionList(Collections.singletonList(transaction));

        when(transactionService.getTransactionsByAccountId(ACCOUNT_ID)).thenReturn(transactions);

        Response response = target("/transactions/account-id").path(String.valueOf(ACCOUNT_ID)).request().get();

        assertThat("Http Response should be OK", response.getStatus(), is(OK.getStatusCode()));
        assertThat("Response should contain transactions: " + transactions.toString(), response.readEntity(Transactions.class), is(transactions));
    }

    @Test
    public void getTransactionsByAccountId_whenRepositoryFailsToFetchTransactionDetails_thenRespondInternalServerError() {
        when(transactionService.getTransactionsByAccountId(ACCOUNT_ID)).thenThrow(new RuntimeException());

        Response response = target("/transactions/account-id").path(String.valueOf(ACCOUNT_ID)).request().get();

        assertThat("Http Response should be INTERNAL_SERVER_ERROR", response.getStatus(), is(INTERNAL_SERVER_ERROR.getStatusCode()));
    }
}
