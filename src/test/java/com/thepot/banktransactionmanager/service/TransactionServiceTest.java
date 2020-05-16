package com.thepot.banktransactionmanager.service;

import com.thepot.banktransactionmanager.model.request.TransactionRequest;
import com.thepot.banktransactionmanager.model.transaction.TransactionType;
import com.thepot.banktransactionmanager.repository.TransactionRepository;
import com.thepot.banktransactionmanager.util.TestHelper;
import org.junit.Before;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.Message;
import java.math.BigDecimal;
import java.util.Currency;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {
    private static final long TRANSACTION_ID = 222L;
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
        TransactionRequest transactionRequest = TestHelper.getTransactionRequest(111, BigDecimal.TEN, Currency.getInstance("GBP"), "testing", TransactionType.SIMPLE_INCREASE);
        when(transactionRepository.createTransaction(transactionRequest)).thenReturn(TRANSACTION_ID);

        long transactionId = transactionService.createTransaction(transactionRequest);

        assertThat(transactionId, is(TRANSACTION_ID));
        verify(transactionRepository).createTransaction(transactionRequest);
        verify(jmsProducer).send(any(Message.class));

    }
}
