package com.creative.share.apps.e_branchdriver.interfaces;

import com.creative.share.apps.e_branchdriver.models.ContactUsModel;

public interface Listeners {


    interface LoginListener {
        void checkDataLogin();
    }
    interface SkipListener
    {
        void skip();
    }
    interface BackListener
    {
        void back();
    }
    interface ShowCountryDialogListener
    {
        void showDialog();
    }

    interface SignUpListener {
        void checkDataSignUp();
    }

    interface ContactListener
    {
        void sendContact(ContactUsModel contactUsModel);
    }
}
