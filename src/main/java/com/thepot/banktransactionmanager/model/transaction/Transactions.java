package com.thepot.banktransactionmanager.model.transaction;

import java.util.List;
import java.util.Objects;

public class Transactions {
    private List<Transaction> transactionList;

    public Transactions() {
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transactions that = (Transactions) o;
        return Objects.equals(transactionList, that.transactionList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionList);
    }

    @Override
    public String toString() {
        return "Transactions{" +
                "transactionList=" + transactionList +
                '}';
    }
}
