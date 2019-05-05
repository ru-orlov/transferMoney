package model;

import java.util.Date;

public class Transfer {

    public static final String ID = "_id";
    public static final String TRANSFER_FROM = "fromAccId";
    public static final String TRANSFER_TO = "toAccId";
    public static final String TRANSFER_AMOUNT = "amount";
    public static final String TRANSFER_DATE = "transferDate";

    public long id;
    public long fromAcc;
    public long toAcc;
    public Date transferDate;
    public String amount;
}
