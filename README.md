# transferMoney

##How to build.
    mvn clean package

## How to Run
    java -jar ./target/money-transfers-with-deps-1.0-SNAPSHOT.jar
    
## End Points
    GET /allAcounts
    GET /account/id/:byid
    GET /account/number/:bynumber
    GET /account/balance/:bybalance
    GET /alltransfers
    GET /transfers/id/:bytransferId
    GET /transfers/sender/:bysender
    POST /transfer/dotransfer 
    ({"fromAccId":snederId,"toAccId":receiveId,"amount":intamount})
    
    