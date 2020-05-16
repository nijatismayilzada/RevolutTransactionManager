package com.thepot.banktransactionmanager.repository;

import com.thepot.banktransactionmanager.config.JdbcConnection;
import com.thepot.banktransactionmanager.model.request.TransactionRequest;
import com.thepot.banktransactionmanager.model.transaction.Transaction;
import com.thepot.banktransactionmanager.model.transaction.TransactionState;
import com.thepot.banktransactionmanager.model.transaction.TransactionType;
import com.thepot.banktransactionmanager.model.transaction.Transactions;
import com.thepot.banktransactionmanager.repository.testutil.ITHelper;
import com.thepot.banktransactionmanager.util.TestHelper;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TransactionRepositoryIT {

    private static final long ACCOUNT_ID = 111L;
    private static final String REFERENCE = "test";
    private TransactionRepository transactionRepository;
    private ITHelper itHelper;

    @Before
    public void setUp() throws Exception {
        JdbcConnection jdbcConnection = new JdbcConnection();
        transactionRepository = new TransactionRepository(jdbcConnection);
        itHelper = new ITHelper(jdbcConnection);
    }

    @Test
    public void createTransaction_giveValidTransaction_canInsertItSuccessfully() throws SQLException {
        TransactionRequest transactionRequest = TestHelper.getTransactionRequest(ACCOUNT_ID, BigDecimal.TEN, Currency.getInstance("GBP"), REFERENCE, TransactionType.SIMPLE_INCREASE);

        long transactionId = transactionRepository.createTransaction(transactionRequest);

        ResultSet resultSet = itHelper.getTransaction(transactionId);

        assertThat(resultSet.next(), is(true));
        assertThat(resultSet.getLong("transaction_id"), is(transactionId));
        assertThat(resultSet.getLong("account_id"), is(ACCOUNT_ID));
        assertThat(resultSet.getBigDecimal("amount").compareTo(BigDecimal.TEN), is(0));
        assertThat(resultSet.getString("currency"), is("GBP"));
        assertThat(resultSet.getString("reference"), is(REFERENCE));
        assertThat(resultSet.getString("transaction_type"), is(TransactionType.SIMPLE_INCREASE.name()));
    }

    @Test
    public void getTransactionById_givenExistingTransaction_canFetchIt() throws SQLException {
        long transactionId = itHelper.createTransaction(ACCOUNT_ID, REFERENCE, TransactionType.SIMPLE_INCREASE, Currency.getInstance("GBP"), BigDecimal.TEN);

        Transaction transaction = transactionRepository.getTransactionById(transactionId);
        assertThat(transaction.getTransactionId(), is(transactionId));
        assertThat(transaction.getAccountId(), is(ACCOUNT_ID));
        assertThat(transaction.getAmount().compareTo(BigDecimal.TEN), is(0));
        assertThat(transaction.getCurrency().getCurrencyCode(), is("GBP"));
        assertThat(transaction.getReference(), is(REFERENCE));
    }

    @Test
    public void getTransactionsByAccountId_givenExistingTransaction_canFetchItByAccountId() throws SQLException {
        itHelper.clearTransactions();
        long transactionId = itHelper.createTransaction(ACCOUNT_ID, REFERENCE, TransactionType.SIMPLE_INCREASE, Currency.getInstance("GBP"), BigDecimal.TEN);

        Transactions transactions = transactionRepository.getTransactionsByAccountId(ACCOUNT_ID);
        assertThat(transactions.getTransactionList().size(), is(1));
        assertThat(transactions.getTransactionList().get(0).getTransactionId(), is(transactionId));
        assertThat(transactions.getTransactionList().get(0).getAccountId(), is(ACCOUNT_ID));
        assertThat(transactions.getTransactionList().get(0).getAmount().compareTo(BigDecimal.TEN), is(0));
        assertThat(transactions.getTransactionList().get(0).getCurrency().getCurrencyCode(), is("GBP"));
        assertThat(transactions.getTransactionList().get(0).getReference(), is(REFERENCE));
    }

    @Test
    public void updateTransaction_giveExistingTransaction_canUpdateItsState() throws SQLException {
        long transactionId = itHelper.createTransaction(ACCOUNT_ID, REFERENCE, TransactionType.SIMPLE_INCREASE, Currency.getInstance("GBP"), BigDecimal.TEN);

        transactionRepository.updateTransaction(transactionId, TransactionState.FAILED);


        ResultSet resultSet = itHelper.getTransaction(transactionId);

        assertThat(resultSet.next(), is(true));
        assertThat(resultSet.getLong("transaction_id"), is(transactionId));
        assertThat(resultSet.getString("transaction_state"), is(TransactionState.FAILED.name()));


    }
}
