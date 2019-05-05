package com.api;

import com.google.gson.Gson;
import com.model.Transfer;
import com.model.TransferStatement;
import com.services.TransferService;
import spark.Route;

public class TransferController {

    TransferService transferService;

    public TransferController(TransferService transferService){
        this.transferService = transferService;
    }

    public Route getAllTransfers() {
        return (request, response) -> transferService.getAllTransfers();
    }

    public Route getTransferById() {
        return (request, response) -> {
            String id = request.params(":transferId");
            return transferService.getTransferById(Integer.parseInt(id));
        };
    }

    public Route getTransferBySender() {
        return (request, response) -> {
            String senderNumber = request.params(":fromAccId");
            return transferService.getAccountBySender(senderNumber);
        };
    }

    public Route doTransfer() {
        return (request, response) -> {
            Gson gson = new Gson();
            TransferStatement transferStatement = gson.fromJson(request.body(), TransferStatement.class);
            Transfer transfer = transferService.doTransfer(transferStatement);
            response.status(201);
            return transfer;
        };
    }
}
