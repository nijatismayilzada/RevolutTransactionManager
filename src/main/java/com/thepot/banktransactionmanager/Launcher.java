package com.thepot.banktransactionmanager;

import org.apache.activemq.broker.BrokerService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.hk2.api.JustInTimeInjectionResolver;
import org.glassfish.hk2.utilities.GreedyResolver;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Launcher {

    private final static Logger LOG = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) {
        Server server = new Server(8082);
        try {
            server.setHandler(getServletContextHandler());
            server.start();

            BrokerService broker = new BrokerService();
            broker.addConnector("tcp://localhost:61616");
            broker.start();

            server.join();
        } catch (Exception ex) {
            LOG.error("Failed to start embedded jetty server.", ex);
        } finally {
            server.destroy();
        }
    }

    private static ServletContextHandler getServletContextHandler() {
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        contextHandler.setContextPath("/");
        contextHandler.addServlet(getServletHolder(), "/*");
        return contextHandler;
    }

    private static ServletHolder getServletHolder() {
        ServletHolder servletHolder = new ServletHolder(new ServletContainer(getResourceConfig()));
        servletHolder.setInitOrder(1);
        return servletHolder;
    }

    private static ResourceConfig getResourceConfig() {
        return new ResourceConfig().packages("com.thepot.banktransactionmanager").register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(GreedyResolver.class).to(JustInTimeInjectionResolver.class);
            }
        });
    }
}
