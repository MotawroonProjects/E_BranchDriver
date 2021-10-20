package com.creative.share.apps.e_branchdriver.models;

import java.io.Serializable;

public class CopunModel implements Serializable {
    private int id;
    private String user_id;
    private String coupon_id;
    private String created_at;

    private Copun coupon;

    public int getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getCoupon_id() {
        return coupon_id;
    }

    public Copun getCopun() {
        return coupon;
    }

    public String getCreated_at() {
        return created_at;
    }

    public class Copun implements Serializable
    {
        private int id;
        private String coupon_number;
        private String value;
        private String is_used;
        private String is_active;
        private String is_deleted;
        private String created_at;

        public int getId() {
            return id;
        }

        public String getCoupon_number() {
            return coupon_number;
        }

        public String getValue() {
            return value;
        }

        public String getIs_used() {
            return is_used;
        }

        public String getIs_active() {
            return is_active;
        }

        public String getIs_deleted() {
            return is_deleted;
        }

        public String getCreated_at() {
            return created_at;
        }
    }


}
