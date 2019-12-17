package com.revolut.revoluttransactionmanager.resource;

import com.revolut.revoluttransactionmanager.model.request.TransactionRequest;
import com.revolut.revoluttransactionmanager.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("transactions")
public class TransactionResource {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionResource.class);
    private final TransactionService transactionService;

    @Inject
    public TransactionResource(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTransaction(TransactionRequest transactionRequest) {
        try {
            return Response.ok(transactionService.createTransaction(transactionRequest)).build();
        } catch (Exception ex) {
            LOG.error("Failed to open new transaction {}", transactionRequest, ex);
            return Response.serverError().build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("transaction-id/{transactionId}")
    public Response getTransactionById(@PathParam("transactionId") long transactionId) {
        try {
            return Response.ok(transactionService.getTransactionById(transactionId)).build();
        } catch (Exception ex) {
            LOG.error("Failed to get transaction for id: {}", transactionId, ex);
            return Response.serverError().build();
        }
    }

}
