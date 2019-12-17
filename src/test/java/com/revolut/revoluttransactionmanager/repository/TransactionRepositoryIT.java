package com.revolut.revoluttransactionmanager.repository;

import com.revolut.revoluttransactionmanager.config.JdbcConnection;
import com.revolut.revoluttransactionmanager.model.request.TransactionRequest;
import com.revolut.revoluttransactionmanager.model.transaction.Transaction;
import com.revolut.revoluttransactionmanager.model.transaction.TransactionState;
import com.revolut.revoluttransactionmanager.model.transaction.TransactionType;
import com.revolut.revoluttransactionmanager.repository.testutil.ITHelper;
import com.revolut.revoluttransactionmanager.util.TestHelper;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TransactionRepositoryIT {

    public static final long ACCOUNT_ID = 111L;
    public static final String REFERENCE = "test";
    private JdbcConnection jdbcConnection;
    private TransactionRepository transactionRepository;
    private ITHelper itHelper;

    @Before
    public void setUp() throws Exception {
        jdbcConnection = new JdbcConnection();
        transactionRepository = new TransactionRepository(jdbcConnection);
        itHelper = new ITHelper(jdbcConnection);
    }

    @Test
    public void createTransaction_giveValidTransaction_canInsertItSuccessfully() throws SQLException {
        TransactionRequest transactionRequest = TestHelper.getTransactionRequest(ACCOUNT_ID, BigDecimal.TEN, Currency.getInstance("GBP"), REFERENCE, TransactionType.REVOLUT_SIMPLE_INCREASE);

        long transactionId = transactionRepository.createTransaction(transactionRequest);

        ResultSet resultSet = itHelper.getTransaction(transactionId);

        assertThat(resultSet.next(), is(true));
        assertThat(resultSet.getLong("transaction_id"), is(transactionId));
        assertThat(resultSet.getLong("account_id"), is(ACCOUNT_ID));
        assertThat(resultSet.getBigDecimal("amount").compareTo(BigDecimal.TEN), is(0));
        assertThat(resultSet.getString("currency"), is("GBP"));
        assertThat(resultSet.getString("reference"), is(REFERENCE));
        assertThat(resultSet.getString("transaction_type"), is(TransactionType.REVOLUT_SIMPLE_INCREASE.name()));
    }

    @Test
    public void getTransactionById_giveExistingTransaction_canFetchIt() throws SQLException {
        long transactionId = itHelper.createTransaction(ACCOUNT_ID, REFERENCE, TransactionType.REVOLUT_SIMPLE_INCREASE, Currency.getInstance("GBP"), BigDecimal.TEN);

        Transaction transaction = transactionRepository.getTransactionById(transactionId);
        assertThat(transaction.getTransactionId(), is(transactionId));
        assertThat(transaction.getAccountId(), is(ACCOUNT_ID));
        assertThat(transaction.getAmount().compareTo(BigDecimal.TEN), is(0));
        assertThat(transaction.getCurrency().getCurrencyCode(), is("GBP"));
        assertThat(transaction.getReference(), is(REFERENCE));


    }


    @Test
    public void updateTransaction_giveExistingTransaction_canUpdateItsState() throws SQLException {
        long transactionId = itHelper.createTransaction(ACCOUNT_ID, REFERENCE, TransactionType.REVOLUT_SIMPLE_INCREASE, Currency.getInstance("GBP"), BigDecimal.TEN);

        transactionRepository.updateTransaction(transactionId, TransactionState.FAILED);


        ResultSet resultSet = itHelper.getTransaction(transactionId);

        assertThat(resultSet.next(), is(true));
        assertThat(resultSet.getLong("transaction_id"), is(transactionId));
        assertThat(resultSet.getString("transaction_state"), is(TransactionState.FAILED.name()));


    }
}
