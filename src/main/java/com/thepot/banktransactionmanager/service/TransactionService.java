package com.thepot.banktransactionmanager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thepot.banktransactionmanager.model.exception.TransactionRuntimeException;
import com.thepot.banktransactionmanager.model.request.TransactionRequest;
import com.thepot.banktransactionmanager.model.transaction.Transaction;
import com.thepot.banktransactionmanager.model.transaction.TransactionEvent;
import com.thepot.banktransactionmanager.model.transaction.TransactionState;
import com.thepot.banktransactionmanager.model.transaction.Transactions;
import com.thepot.banktransactionmanager.repository.TransactionRepository;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Service
public class TransactionService implements MessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(TransactionService.class);
    private final TransactionRepository transactionRepository;
    private final MessageProducer messageProducer;
    private final MessageConsumer messageConsumer;

    @Inject
    public TransactionService(TransactionRepository transactionRepository, MessageProducer messageProducer, MessageConsumer messageConsumer) throws JMSException {
        this.transactionRepository = transactionRepository;
        this.messageProducer = messageProducer;
        this.messageConsumer = messageConsumer;
        messageConsumer.getTransactionCompletedConsumer().setMessageListener(this);
        messageConsumer.getTransactionFailedConsumer().setMessageListener(this);
    }

    public long createTransaction(TransactionRequest transactionRequest) {
        try {
            long transactionId = transactionRepository.createTransaction(transactionRequest);

            TransactionEvent transactionEvent = new TransactionEvent();
            transactionEvent.setTransactionId(transactionId);
            TextMessage textMessage = new ActiveMQTextMessage();
            textMessage.setText(new ObjectMapper().writeValueAsString(transactionEvent));

            messageProducer.getProducer().send(textMessage);
            return transactionId;
        } catch (Exception e) {
            throw new TransactionRuntimeException(e);
        }
    }

    public Transaction getTransactionById(long transactionId) {
        return transactionRepository.getTransactionById(transactionId);
    }

    public Transactions getTransactionsByAccountId(long accountId) {
        return transactionRepository.getTransactionsByAccountId(accountId);
    }

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            TransactionEvent transactionEvent = new ObjectMapper().readValue(textMessage.getText(), TransactionEvent.class);

            //Each of the queues should have their own listener.
            //To keep things simple with Jersey and HK2 context, for now transaction service handles both.
            ActiveMQDestination destination = (ActiveMQDestination) textMessage.getJMSDestination();
            if (destination.getPhysicalName().equals((messageConsumer.getQueueNameCompleted()))) {
                transactionRepository.updateTransaction(transactionEvent.getTransactionId(), TransactionState.COMPLETED);
            } else if (destination.getPhysicalName().equals((messageConsumer.getQueueNameFailed()))) {
                transactionRepository.updateTransaction(transactionEvent.getTransactionId(), TransactionState.FAILED);
            } else {
                throw new TransactionRuntimeException(String.format("Unknown event: %s", message));
            }
        } catch (Exception e) {
            LOG.error("Failed to process message. message: {}", message, e);
            throw new TransactionRuntimeException(e);
        }


    }
}
