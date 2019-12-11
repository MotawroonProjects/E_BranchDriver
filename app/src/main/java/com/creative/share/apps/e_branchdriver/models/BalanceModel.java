package com.creative.share.apps.e_branchdriver.models;

import java.io.Serializable;
import java.util.List;

public class BalanceModel implements Serializable {
    private String total_balance;
    private int count_of_orders_this_month;
    private int balance_this_month;
    private List<CopunModel> payments;

    public String getTotal_balance() {
        return total_balance;
    }

    public int getCount_of_orders_this_month() {
        return count_of_orders_this_month;
    }

    public int getBalance_this_month() {
        return balance_this_month;
    }

    public List<CopunModel> getPayments() {
        return payments;
    }
}
