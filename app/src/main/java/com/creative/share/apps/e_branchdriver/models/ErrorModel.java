package com.creative.share.apps.e_branchdriver.models;

import java.io.Serializable;
import java.util.List;

public class ErrorModel implements Serializable {

    private Error errors;

    public Error getErrors() {
        return errors;
    }

    public class Error implements Serializable
    {
        private List<String> phone;
        private List<String> email;

        public List<String> getPhone() {
            return phone;
        }

        public List<String> getEmail() {
            return email;
        }
    }
}
