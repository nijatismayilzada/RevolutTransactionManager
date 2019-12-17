package com.revolut.revoluttransactionmanager.service;

import com.revolut.revoluttransactionmanager.model.request.TransactionRequest;
import com.revolut.revoluttransactionmanager.model.transaction.TransactionType;
import com.revolut.revoluttransactionmanager.repository.TransactionRepository;
import com.revolut.revoluttransactionmanager.util.TestHelper;
import org.junit.Before;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.Message;
import java.math.BigDecimal;
import java.util.Currency;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TransactionServiceTest {
    private static final int TRANSACTION_ID = 111;
    private TransactionRepository transactionRepository;
    private MessageProducer messageProducer;
    private MessageConsumer messageConsumer;
    private TransactionService transactionService;
    private javax.jms.MessageProducer jmsProducer;
    private javax.jms.MessageConsumer jmsCompletedConsumer;
    private javax.jms.MessageConsumer jmsFailedConsumer;

    @Before
    public void setUp() throws Exception {
        transactionRepository = mock(TransactionRepository.class);
        messageProducer = mock(MessageProducer.class);
        messageConsumer = mock(MessageConsumer.class);
        jmsProducer = mock(javax.jms.MessageProducer.class);
        jmsCompletedConsumer = mock(javax.jms.MessageConsumer.class);
        jmsFailedConsumer = mock(javax.jms.MessageConsumer.class);
        when(messageProducer.getProducer()).thenReturn(jmsProducer);
        when(messageConsumer.getTransactionCompletedConsumer()).thenReturn(jmsCompletedConsumer);
        when(messageConsumer.getTransactionFailedConsumer()).thenReturn(jmsFailedConsumer);
        transactionService = new TransactionService(transactionRepository, messageProducer, messageConsumer);
    }

    @Test
    public void createTransaction_givenCreateTransactionRequest_processesSuccessfully() throws JMSException {
        TransactionRequest transactionRequest = TestHelper.getTransactionRequest(111, BigDecimal.TEN, Currency.getInstance("GBP"), "testing", TransactionType.REVOLUT_SIMPLE_INCREASE);
        when(transactionRepository.createTransaction(transactionRequest)).thenReturn(222L);

        transactionService.createTransaction(transactionRequest);

        verify(transactionRepository).createTransaction(transactionRequest);
        verify(jmsProducer).send(any(Message.class));

    }
}
