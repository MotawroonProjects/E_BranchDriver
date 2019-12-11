package com.creative.share.apps.e_branchdriver.models;

import java.io.Serializable;
import java.util.List;

public class CityDataModel implements Serializable {

    private List<CityModel> data;

    public List<CityModel> getData() {
        return data;
    }

    public static class CityModel implements Serializable{
        private int id_city;
        private String ar_city_title;
        private String en_city_title;

        public CityModel(int id_city, String ar_city_title, String en_city_title) {
            this.id_city = id_city;
            this.ar_city_title = ar_city_title;
            this.en_city_title = en_city_title;
        }

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
