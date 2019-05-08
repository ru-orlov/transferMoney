package com;

import com.api.AccountController;
import com.api.ServerController;
import com.api.TransferController;
import com.db.SQLiteJDBC;
import com.services.AccountService;
import com.services.ServerService;
import com.services.TransferService;
import com.transformers.JsonTransformer;

import static spark.Spark.get;
import static spark.Spark.post;

public class EndPoint {

    private final JsonTransformer jsonTransformer = new JsonTransformer();

    private ServerController serverController;
    private AccountController accountController;
    private TransferController transferController;

    public EndPoint(SQLiteJDBC sqLiteJDBC){
        serverController = new ServerController(new ServerService());
        accountController = new AccountController(new AccountService(sqLiteJDBC));
        transferController = new TransferController(new TransferService(sqLiteJDBC, new AccountService(sqLiteJDBC)));
        sqLiteJDBC.memoryRestore();
    }

    public void serverAPI() {
        injectServerController();
        injectAccountController();
        injectTransferController();
    }

    private void injectServerController(){
        get("/", serverController.emptyAnswer());
    }

    private void injectAccountController(){
        get("/allAcounts", accountController.getAllAccounts(), jsonTransformer);
        get("/account/id/:id", accountController.getAccountById(), jsonTransformer);
        get("/account/number/:number", accountController.getAccountByNumber(), jsonTransformer);
        get("/account/balance/:balance", accountController.getAccountByBalance(), jsonTransformer);
    }

    private void injectTransferController(){
        get("/alltransfers", transferController.getAllTransfers(), jsonTransformer);
        get("/transfers/id/:transferId", transferController.getTransferById(), jsonTransformer);
        get("/transfers/sender/:fromAccId", transferController.getTransferBySender(), jsonTransformer);
        post("/transfer/dotransfer", transferController.doTransfer(), jsonTransformer);
    }

    public static void main(String[] args) {
        SQLiteJDBC SQLiteJDBC = new SQLiteJDBC();
        EndPoint endPoint = new EndPoint(SQLiteJDBC);
        endPoint.serverAPI();
    }

}
