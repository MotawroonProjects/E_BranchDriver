package com.creative.share.apps.e_branchdriver.models;

import java.io.Serializable;
import java.util.List;

public class OrderDataModel implements Serializable {

    private int current_page;
    private List<OrderModel> data;

    public int getCurrent_page() {
        return current_page;
    }

    public List<OrderModel> getData() {
        return data;
    }
}
