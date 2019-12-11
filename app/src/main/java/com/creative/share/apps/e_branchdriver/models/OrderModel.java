package com.creative.share.apps.e_branchdriver.models;

import java.io.Serializable;
import java.util.List;

public class OrderModel implements Serializable {

    private int id;
    private int user_id;
    private int market_id;
    private int branch_id;
    private int transporter_id;
    private int order_type;
    private double longitude;
    private double latitude;
    private String address;
    private int status;
    private String cancel_reason;
    private String  title;
    private long order_date;
    private double total_cost;
    private double rating;
    private double distance;
    private String order_code;
    private String bill_image;
    private String order_image;
    private int order_tracker;
    private double driver_market_distance;
    private List<OrderDetails> order_details;
    private User user;
    private Market market;

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getMarket_id() {
        return market_id;
    }

    public int getBranch_id() {
        return branch_id;
    }

    public int getTransporter_id() {
        return transporter_id;
    }

    public int getOrder_type() {
        return order_type;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getAddress() {
        return address;
    }

    public int getStatus() {
        return status;
    }

    public String getCancel_reason() {
        return cancel_reason;
    }

    public String getTitle() {
        return title;
    }

    public long getOrder_date() {
        return order_date;
    }

    public double getTotal_cost() {
        return total_cost;
    }

    public double getRating() {
        return rating;
    }

    public double getDistance() {
        return distance;
    }

    public String getOrder_code() {
        return order_code;
    }

    public String getBill_image() {
        return bill_image;
    }

    public String getOrder_image() {
        return order_image;
    }

    public int getOrder_tracker() {
        return order_tracker;
    }

    public double getDriver_market_distance() {
        return driver_market_distance;
    }

    public List<OrderDetails> getOrder_details() {
        return order_details;
    }

    public User getUser() {
        return user;
    }

    public Market getMarket() {
        return market;
    }

    public class OrderDetails implements Serializable
    {
        private int id;
        private int order_id;
        private int product_id;
        private int amount;
        private double total_price;
        private int offer_id;

        public int getId() {
            return id;
        }

        public int getOrder_id() {
            return order_id;
        }

        public int getProduct_id() {
            return product_id;
        }

        public int getAmount() {
            return amount;
        }

        public double getTotal_price() {
            return total_price;
        }

        public int getOffer_id() {
            return offer_id;
        }
    }

    public class User implements Serializable
    {
        private int id;
        private int user_type;
        private int confirmation_code;
        private String name;
        private String phone_code;
        private String phone;
        private String email;
        private String full_name;
        private String membership_code;
        private String logo;
        private String address;
        private double latitude;
        private double longitude;
        private int balance;
        private double rate;


        public int getId() {
            return id;
        }

        public int getUser_type() {
            return user_type;
        }

        public int getConfirmation_code() {
            return confirmation_code;
        }

        public String getName() {
            return name;
        }

        public String getPhone_code() {
            return phone_code;
        }

        public String getPhone() {
            return phone;
        }

        public String getEmail() {
            return email;
        }

        public String getFull_name() {
            return full_name;
        }

        public String getMembership_code() {
            return membership_code;
        }

        public String getLogo() {
            return logo;
        }

        public String getAddress() {
            return address;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public int getBalance() {
            return balance;
        }

        public double getRate() {
            return rate;
        }
    }

    public class Market implements Serializable
    {
        private int id;
        private int user_type;
        private int confirmation_code;
        private String name;
        private String phone_code;
        private String phone;
        private String email;
        private String full_name;
        private String membership_code;
        private String logo;
        private String address;
        private double latitude;
        private double longitude;
        private int balance;
        private double rate;


        public int getId() {
            return id;
        }

        public int getUser_type() {
            return user_type;
        }

        public int getConfirmation_code() {
            return confirmation_code;
        }

        public String getName() {
            return name;
        }

        public String getPhone_code() {
            return phone_code;
        }

        public String getPhone() {
            return phone;
        }

        public String getEmail() {
            return email;
        }

        public String getFull_name() {
            return full_name;
        }

        public String getMembership_code() {
            return membership_code;
        }

        public String getLogo() {
            return logo;
        }

        public String getAddress() {
            return address;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public int getBalance() {
            return balance;
        }

        public double getRate() {
            return rate;
        }

    }
}
