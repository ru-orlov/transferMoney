package com.api;

import com.services.AccountService;
import spark.Route;


public class AccountController {

    AccountService accountService;

    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    public Route getAllAccounts() {
        return (request, response) -> accountService.getAllAccounts();
    }

    public Route getAccountById() {
        return (request, response) -> {
            String id = request.params(":id");
            return accountService.getAccountById(Integer.parseInt(id));
        };
    }

    public Route getAccountByNumber() {
        return (request, response) -> {
            String number = request.params(":number");
            return accountService.getAccountByNumber(number);
        };
    }

    public Route getAccountByBalance() {
        return (request, response) -> {
            String balance = request.params(":balance");
            return accountService.getAccountByBalance(balance);
        };
    }


}
