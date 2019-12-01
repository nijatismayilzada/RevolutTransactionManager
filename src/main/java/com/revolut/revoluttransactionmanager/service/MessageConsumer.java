package com.revolut.revoluttransactionmanager.service;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

@Service
public class MessageConsumer {
    private final static String URL = "tcp://localhost:61616";
    private final static String QUEUE_NAME_COMPLETED = "revolut.transaction.completed.event";
    private final static String QUEUE_NAME_FAILED = "revolut.transaction.failed.event";

    private javax.jms.MessageConsumer transactionCompletedConsumer;
    private javax.jms.MessageConsumer transactionFailedConsumer;

    @Inject
    public MessageConsumer() throws JMSException {
        Connection connection = ((ConnectionFactory) new ActiveMQConnectionFactory(URL)).createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        transactionCompletedConsumer = session.createConsumer(session.createQueue(QUEUE_NAME_COMPLETED));
        transactionFailedConsumer = session.createConsumer(session.createQueue(QUEUE_NAME_FAILED));
    }

    public javax.jms.MessageConsumer getTransactionCompletedConsumer() {
        return transactionCompletedConsumer;
    }

    public javax.jms.MessageConsumer getTransactionFailedConsumer() {
        return transactionFailedConsumer;
    }

    public String getQueueNameCompleted() {
        return QUEUE_NAME_COMPLETED;
    }

    public String getQueueNameFailed() {
        return QUEUE_NAME_FAILED;
    }
}
