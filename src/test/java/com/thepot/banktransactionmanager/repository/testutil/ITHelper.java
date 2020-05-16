package com.thepot.banktransactionmanager.repository.testutil;


import com.thepot.banktransactionmanager.config.JdbcConnection;
import com.thepot.banktransactionmanager.model.transaction.TransactionState;
import com.thepot.banktransactionmanager.model.transaction.TransactionType;

import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.util.Currency;

public class ITHelper {

    private final JdbcConnection jdbcConnection;

    public ITHelper(JdbcConnection jdbcConnection) {
        this.jdbcConnection = jdbcConnection;
    }


    public ResultSet getTransaction(long transactionId) throws SQLException {
        PreparedStatement statement = jdbcConnection.getConnection().prepareStatement("select transaction_id, account_id, reference, transaction_state, state_updated_at, transaction_type, currency, amount from transaction where transaction_id = ? ");
        statement.setLong(1, transactionId);

        return statement.executeQuery();
    }

    public long createTransaction(long accountId, String reference, TransactionType transactionType, Currency currency, BigDecimal amount) throws SQLException {
        PreparedStatement statement = jdbcConnection.getConnection().prepareStatement(
                "insert into transaction(account_id, reference, transaction_state, state_updated_at, transaction_type, currency, amount) values(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        statement.setLong(1, accountId);
        statement.setString(2, reference);
        statement.setString(3, TransactionState.CREATED.name());
        statement.setTimestamp(4, Timestamp.from(Instant.now()));
        statement.setString(5, transactionType.name());
        statement.setString(6, currency.getCurrencyCode());
        statement.setBigDecimal(7, amount);
        statement.executeUpdate();
        ResultSet resultSet = statement.getGeneratedKeys();
        resultSet.next();
        return resultSet.getLong(1);
    }

    public void clearTransactions() throws SQLException {
        PreparedStatement statement = jdbcConnection.getConnection().prepareStatement("delete from transaction");
        statement.executeUpdate();
    }
}
