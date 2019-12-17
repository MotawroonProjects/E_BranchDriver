package com.creative.share.apps.e_branchdriver.models;

import java.io.Serializable;
import java.util.List;

public class CancelReasonsDataModel implements Serializable {

    private List<CancelModel> data;

    public List<CancelModel> getData() {
        return data;
    }

    public static class CancelModel implements Serializable
    {
        private int id;
        private String ar_title;
        private String en_title;


        public CancelModel(int id, String ar_title, String en_title) {
            this.id = id;
            this.ar_title = ar_title;
            this.en_title = en_title;
        }

        public int getId() {
            return id;
        }

        public String getAr_title() {
            return ar_title;
        }

        public String getEn_title() {
            return en_title;
        }
    }
}
