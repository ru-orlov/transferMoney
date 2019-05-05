package com.services;

import com.db.SQLiteJDBC;
import com.model.Account;
import com.model.Transfer;
import com.model.TransferStatement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.model.Transfer.ID;
import static com.model.Transfer.TRANSFER_FROM;
import static com.model.Transfer.TRANSFER_TO;
import static com.model.Transfer.TRANSFER_AMOUNT;
import static com.model.Transfer.TRANSFER_DATE;

public class TransferService {

    private static final String GET_ALL_TRANSFERS = "SELECT _id, fromAccId, toAccId, amount, transferDate FROM transfers;";
    private static final String GET_TRANSFER_BY_ID = "SELECT _id, fromAccId, toAccId, amount, transferDate FROM transfers WHERE _id = ?;";
    private static final String GET_TRANSFER_BY_SENDER_NUMBER = "SELECT _id, fromAccId, toAccId, amount, transferDate FROM transfers WHERE fromAccId = ?;";
    private static final String DO_TRANSFER = "INSERT INTO transfers (fromAccId, toAccId, amount, transferDate) values (?, ?, ?, ?);";
    private static final String ACCOUNT_CHANGE_AMOUNT = "UPDATE accounts SET balance = ? WHERE _id = ?;";

    private static final String PLUS = "plus";
    private static final String MINUS = "minus";

    private SQLiteJDBC sqLiteJDBC;

    private AccountService accountService;

    public TransferService(SQLiteJDBC sqLiteJDBC, AccountService accountService) {
        this.accountService = accountService;
        this.sqLiteJDBC = sqLiteJDBC;
    }

    public List<Transfer> getAllTransfers(){
        Connection connection;
        Statement stmt;
        try {
            connection = sqLiteJDBC.getConnection();
            stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(GET_ALL_TRANSFERS);
            List<Transfer> transfers = resultSetToTransfers(resultSet);
            resultSet.close();
            stmt.close();
            return transfers;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Transfer getTransferById(int id){
        Connection connection;
        try {
            connection = sqLiteJDBC.getConnection();
            PreparedStatement pstmt  = connection.prepareStatement(GET_TRANSFER_BY_ID);
            {
                pstmt.setInt(1, id);
                ResultSet resultSet = pstmt.executeQuery();
                Transfer accounts = resultSetToTransfer(resultSet);
                resultSet.close();
                pstmt.close();
                return accounts;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Transfer getAccountBySender(String senderNumber){
        Connection connection;
        try {
            connection = sqLiteJDBC.getConnection();
            PreparedStatement pstmt  = connection.prepareStatement(GET_TRANSFER_BY_SENDER_NUMBER);
            {
                pstmt.setString(1, senderNumber);
                ResultSet resultSet = pstmt.executeQuery();
                Transfer accounts = resultSetToTransfer(resultSet);
                resultSet.close();
                pstmt.close();
                return accounts;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Transfer doTransfer(TransferStatement transferStatement){
        Connection connection;
        try {
            connection = sqLiteJDBC.getConnection();
            changeAmount(transferStatement.fromAccId, transferStatement.amount, "minus");
            changeAmount(transferStatement.toAccId, transferStatement.amount, "plus");
            PreparedStatement pstmt  = connection.prepareStatement(DO_TRANSFER);
            {
                pstmt.setInt(1, transferStatement.fromAccId);
                pstmt.setInt(2, transferStatement.toAccId);
                pstmt.setInt(3, transferStatement.amount);
                pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                pstmt.executeUpdate();
                pstmt.close();
                return getTransferById(pstmt.getGeneratedKeys().getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String changeAmount(int idAccount, int amount, String action){
        Account account = accountService.getAccountById(idAccount);
        int newBalance;
        if (MINUS.equals(action)) {
            if (account.balance < amount) {
                return "Error: not enough money on Accoutd " + account.id;
            } else {
                newBalance = account.balance - amount;
            }
        } else {
            newBalance = account.balance + amount;
        }
        Connection connection;
        try {
            connection = sqLiteJDBC.getConnection();
            PreparedStatement pstmt  = connection.prepareStatement(ACCOUNT_CHANGE_AMOUNT);
            {
                pstmt.setInt(1, newBalance);
                pstmt.setLong(2, account.id);
                pstmt.executeUpdate();
                pstmt.close();
                return "Transfer complete";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Ok";
    }

    private List<Transfer> resultSetToTransfers(ResultSet resultSet) throws SQLException {
        List<Transfer> transfers = new ArrayList<>();
        while ( resultSet.next() ) {
            transfers.add(resultSetToTransfer(resultSet));
        }
        return transfers;
    }

    private Transfer resultSetToTransfer(ResultSet resultSet) throws SQLException {
        Transfer transfer = new Transfer();
        transfer.id = resultSet.getInt(ID);
        transfer.fromAcc = resultSet.getInt(TRANSFER_FROM);
        transfer.toAcc = resultSet.getInt(TRANSFER_TO);
        transfer.transferDate = resultSet.getDate(TRANSFER_DATE);
        transfer.amount = resultSet.getString(TRANSFER_AMOUNT);
        return transfer;
    }
}
