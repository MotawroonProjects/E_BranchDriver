package com.creative.share.apps.e_branchdriver.models;

import java.io.Serializable;

public class UserModel implements Serializable {
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
    private int city_id;
    private City city;

    public int getId() {
        return id;
    }

    public int getUser_type() {
        return user_type;
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

    public int getCity_id() {
        return city_id;
    }

    public City getCity() {
        return city;
    }

    public int getConfirmation_code() {
        return confirmation_code;
    }

    public class City implements Serializable
    {
        private int id_city;
        private String ar_city_title;
        private String en_city_title;

        public int getId_city() {
            return id_city;
        }

        public String getAr_city_title() {
            return ar_city_title;
        }

        public String getEn_city_title() {
            return en_city_title;
        }
    }
}
