package com.revolut.revoluttransactionmanager.service;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.jvnet.hk2.annotations.Service;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

@Service
public class MessageProducer {
    private final static String URL = "tcp://localhost:61616";
    private final static String QUEUE_NAME = "revolut.transaction.created.event";

    private javax.jms.MessageProducer producer;

    public MessageProducer() throws JMSException {
        Connection connection = ((ConnectionFactory) new ActiveMQConnectionFactory(URL)).createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        producer = session.createProducer(session.createQueue(QUEUE_NAME));
    }

    public javax.jms.MessageProducer getProducer() {
        return producer;
    }
}
