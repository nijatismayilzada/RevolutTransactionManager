package com.thepot.banktransactionmanager.repository;

import com.thepot.banktransactionmanager.config.JdbcConnection;
import com.thepot.banktransactionmanager.model.exception.SQLRuntimeException;
import com.thepot.banktransactionmanager.model.exception.TransactionRuntimeException;
import com.thepot.banktransactionmanager.model.request.TransactionRequest;
import com.thepot.banktransactionmanager.model.transaction.Transaction;
import com.thepot.banktransactionmanager.model.transaction.TransactionState;
import com.thepot.banktransactionmanager.model.transaction.TransactionType;
import com.thepot.banktransactionmanager.model.transaction.Transactions;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Currency;

@Service
public class TransactionRepository {
    private static final String INSERT_TRANSACTION =
            "insert into transaction(account_id, reference, transaction_state, state_updated_at, transaction_type, currency, amount) values(?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_TRANSACTION_BY_ID =
            "select transaction_id, account_id, reference, transaction_state, state_updated_at, transaction_type, currency, amount from transaction where transaction_id = ? ";

    private static final String SELECT_TRANSACTIONS_BY_ACCOUNT_ID =
            "select transaction_id, account_id, reference, transaction_state, state_updated_at, transaction_type, currency, amount from transaction where account_id = ? ";

    private static final String UPDATE_TRANSACTION_STATE = "update transaction set transaction_state = ?, state_updated_at = ? where transaction_id = ?";

    private final JdbcConnection jdbcConnection;

    @Inject
    public TransactionRepository(JdbcConnection jdbcConnection) {
        this.jdbcConnection = jdbcConnection;
    }

    public long createTransaction(TransactionRequest transactionRequest) {
        try {
            PreparedStatement statement = jdbcConnection.getConnection().prepareStatement(INSERT_TRANSACTION, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, transactionRequest.getAccountId());
            statement.setString(2, transactionRequest.getReference());
            statement.setString(3, TransactionState.CREATED.name());
            statement.setTimestamp(4, Timestamp.from(Instant.now()));
            statement.setString(5, transactionRequest.getTransactionType().name());
            statement.setString(6, transactionRequest.getCurrency().getCurrencyCode());
            statement.setBigDecimal(7, transactionRequest.getAmount());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            return resultSet.getLong(1);
        } catch (SQLException e) {
            throw new TransactionRuntimeException(e);
        }
    }

    public Transaction getTransactionById(long transactionId) {
        try {
            PreparedStatement statement = jdbcConnection.getConnection().prepareStatement(SELECT_TRANSACTION_BY_ID);
            statement.setLong(1, transactionId);

            ResultSet resultSet = statement.executeQuery();

            Transaction transaction = new Transaction();
            if (resultSet.next()) {
                transaction = extractTransaction(resultSet);
            }

            return transaction;

        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public Transactions getTransactionsByAccountId(long accountId) {
        try {
            PreparedStatement statement = jdbcConnection.getConnection().prepareStatement(SELECT_TRANSACTIONS_BY_ACCOUNT_ID);
            statement.setLong(1, accountId);

            ResultSet resultSet = statement.executeQuery();

            Transactions transactions = new Transactions();
            transactions.setTransactionList(new ArrayList<>());

            while (resultSet.next()) {
                transactions.getTransactionList().add(extractTransaction(resultSet));
            }

            return transactions;

        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    private Transaction extractTransaction(ResultSet resultSet) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(resultSet.getLong(1));
        transaction.setAccountId(resultSet.getLong(2));
        transaction.setReference(resultSet.getString(3));
        transaction.setTransactionState(TransactionState.valueOf(resultSet.getString(4)));
        transaction.setStateUpdatedAt(resultSet.getTimestamp(5).toInstant());
        transaction.setTransactionType(TransactionType.valueOf(resultSet.getString(6)));
        transaction.setCurrency(Currency.getInstance(resultSet.getString(7)));
        transaction.setAmount(resultSet.getBigDecimal(8));
        return transaction;
    }

    public void updateTransaction(long transactionId, TransactionState transactionState) {
        try {
            PreparedStatement statement = jdbcConnection.getConnection().prepareStatement(UPDATE_TRANSACTION_STATE);
            statement.setString(1, transactionState.name());
            statement.setTimestamp(2, Timestamp.from(Instant.now()));
            statement.setLong(3, transactionId);
            statement.executeUpdate();
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected != 1) {
                throw new TransactionRuntimeException(String.format("Could not update transaction %s to state %s. %s rows affected", transactionId, transactionState, rowsAffected));
            }
        } catch (SQLException e) {
            throw new TransactionRuntimeException(e);
        }
    }
}
