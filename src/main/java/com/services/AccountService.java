package com.services;

import com.db.SQLiteJDBC;
import com.model.Account;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import static com.model.Account.ID;
import static com.model.Account.ACCOUNT_NUMBER;
import static com.model.Account.ACCOUNT_BALANCE;


public class AccountService {

    private static final String GET_ALL_ACCOUNTS = "SELECT _id, numberAcc, balance FROM accounts;";
    private static final String GET_ACCOUNT_BY_ID = "SELECT _id, numberAcc, balance FROM accounts WHERE _id = ?;";
    private static final String GET_ACCOUNT_BY_NUMBER = "SELECT _id, numberAcc, balance FROM accounts WHERE numberAcc = ?;";
    private static final String GET_ACCOUNT_BY_BALANCE = "SELECT _id, numberAcc, balance FROM accounts WHERE balance = ?;";

    private final SQLiteJDBC sqLiteJDBC;

    public AccountService(SQLiteJDBC sqLiteJDBC) {
        this.sqLiteJDBC = sqLiteJDBC;
    }

    public List<Account> getAllAccounts(){
        Connection connection;
        Statement stmt;
        try {
            connection = sqLiteJDBC.getConnection();
            stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(GET_ALL_ACCOUNTS);
            List<Account> accounts = resultSetToAccounts(resultSet);
            resultSet.close();
            stmt.close();
            return accounts;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Account getAccountById(int id){
        Connection connection;
        try {
            connection = sqLiteJDBC.getConnection();
            PreparedStatement pstmt  = connection.prepareStatement(GET_ACCOUNT_BY_ID);
            {
                pstmt.setInt(1, id);
                ResultSet resultSet = pstmt.executeQuery();
                Account accounts = resultSetToAccount(resultSet);
                resultSet.close();
                pstmt.close();
                return accounts;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Account getAccountByNumber(String number){
        Connection connection;
        try {
            connection = sqLiteJDBC.getConnection();
            PreparedStatement pstmt  = connection.prepareStatement(GET_ACCOUNT_BY_NUMBER);
            {
                pstmt.setString(1, number);
                ResultSet resultSet = pstmt.executeQuery();
                Account account = resultSetToAccount(resultSet);
                resultSet.close();
                pstmt.close();
                return account;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Account> getAccountByBalance(String number){
        Connection connection;
        try {
            connection = sqLiteJDBC.getConnection();
            PreparedStatement pstmt  = connection.prepareStatement(GET_ACCOUNT_BY_BALANCE);
            {
                pstmt.setString(1, number);
                ResultSet resultSet = pstmt.executeQuery();
                List<Account> accounts = resultSetToAccounts(resultSet);
                resultSet.close();
                pstmt.close();
                return accounts;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Account> resultSetToAccounts(ResultSet resultSet) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        while ( resultSet.next() ) {
            Account account = new Account();
            account.id = resultSet.getInt(ID);
            account.number = resultSet.getInt(ACCOUNT_NUMBER);
            account.balance = resultSet.getInt(ACCOUNT_BALANCE);
            accounts.add(account);
        }
        return accounts;
    }

    private Account resultSetToAccount(ResultSet resultSet) throws SQLException {
        Account account = new Account();
        while ( resultSet.next() ) {
            account.id = resultSet.getInt(ID);
            account.number = resultSet.getInt(ACCOUNT_NUMBER);
            account.balance = resultSet.getInt(ACCOUNT_BALANCE);
        }
        return account;
    }
}
