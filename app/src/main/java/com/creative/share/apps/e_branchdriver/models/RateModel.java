package com.creative.share.apps.e_branchdriver.models;

import java.io.Serializable;
import java.util.List;

public class RateModel implements Serializable {
    private double rating_total;
    private List<Rate> rating_in_week;

    public double getRating_total() {
        return rating_total;
    }

    public List<Rate> getRating_in_week() {
        return rating_in_week;
    }

    public class Rate implements Serializable
    {
        private String date;
        private double rating;

        public String getDate() {
            return date;
        }

        public double getRating() {
            return rating;
        }
    }
}
