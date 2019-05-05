import api.AccountController;
import api.ServerController;
import api.TransferController;
import db.SQLiteJDBC;
import services.AccountService;
import services.ServerService;
import services.TransferService;
import transformers.JsonTransformer;

import static spark.Spark.*;

public class EndPoint {

    private final JsonTransformer jsonTransformer = new JsonTransformer();

    private ServerController serverController;
    private AccountController accountController;
    private TransferController transferController;

    private EndPoint(SQLiteJDBC sqLiteJDBC){
        serverController = new ServerController(new ServerService());
        accountController = new AccountController(new AccountService(sqLiteJDBC));
        transferController = new TransferController(new TransferService(sqLiteJDBC, new AccountService(sqLiteJDBC)));
        sqLiteJDBC.memoryRestore();
    }

    private void serverAPI() {
        injectServerController();
        injectAccountController();
        injectTransferController();
    }

    private void injectServerController(){
        get("/", serverController.emptyAnswer());
    }

    private void injectAccountController(){
        get("/templateAccount", accountController.templateAccount(), jsonTransformer);
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
