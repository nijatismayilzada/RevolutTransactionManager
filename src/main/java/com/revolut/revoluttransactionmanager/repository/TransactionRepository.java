package com.revolut.revoluttransactionmanager.repository;

import com.revolut.revoluttransactionmanager.config.JdbcConnection;
import com.revolut.revoluttransactionmanager.model.exception.SQLRuntimeException;
import com.revolut.revoluttransactionmanager.model.exception.TransactionRuntimeException;
import com.revolut.revoluttransactionmanager.model.request.TransactionRequest;
import com.revolut.revoluttransactionmanager.model.transaction.Transaction;
import com.revolut.revoluttransactionmanager.model.transaction.TransactionAction;
import com.revolut.revoluttransactionmanager.model.transaction.TransactionState;
import com.revolut.revoluttransactionmanager.model.transaction.TransactionType;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Currency;

@Service
public class TransactionRepository {
    private static final String INSERT_TRANSACTION =
            "insert into transaction(account_id, reference, transaction_state, state_updated_at, transaction_type, transaction_action, currency, amount) values(?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_TRANSACTION_BY_ID =
            "select transaction_id, account_id, reference, transaction_state, state_updated_at, transaction_type, transaction_action, currency, amount from transaction where transaction_id = ? ";

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
            statement.setString(6, transactionRequest.getTransactionAction().name());
            statement.setString(7, transactionRequest.getCurrency().getCurrencyCode());
            statement.setBigDecimal(8, transactionRequest.getAmount());
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
                transaction.setTransactionId(resultSet.getLong(1));
                transaction.setAccountId(resultSet.getLong(2));
                transaction.setReference(resultSet.getString(3));
                transaction.setTransactionState(TransactionState.valueOf(resultSet.getString(4)));
                transaction.setStateUpdatedAt(resultSet.getTimestamp(5).toInstant());
                transaction.setTransactionType(TransactionType.valueOf(resultSet.getString(6)));
                transaction.setTransactionAction(TransactionAction.valueOf(resultSet.getString(7)));
                transaction.setCurrency(Currency.getInstance(resultSet.getString(8)));
                transaction.setAmount(resultSet.getBigDecimal(9));
            }

            return transaction;

        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
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
