package com.creative.share.apps.e_branchdriver.models;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;

import com.creative.share.apps.e_branchdriver.R;


public class SignUpModel extends BaseObservable {

    private String name;
    private String email;
    private String phone_code;
    private String phone;
    private String password;
    private String city_id;
    private boolean isAcceptTerms ;
    private Uri image_license;
    private Uri image_form;
    private Uri image_identity;


    public ObservableField<String> error_name = new ObservableField<>();
    public ObservableField<String> error_email = new ObservableField<>();
    public ObservableField<String> error_phone_code = new ObservableField<>();
    public ObservableField<String> error_phone = new ObservableField<>();
    public ObservableField<String> error_password = new ObservableField<>();
    public ObservableField<String> error_about_me = new ObservableField<>();



    public boolean isDataValid(Context context)
    {
        Log.e("name",name);
        Log.e("email",email);
        Log.e("phone_code",phone_code);
        Log.e("phone",phone);
        Log.e("password",password);
        Log.e("city_id",city_id);
        Log.e("image_license",image_license.toString());
        Log.e("image_form",image_form.toString());
        Log.e("image_identity",image_identity.toString());


        if (!TextUtils.isEmpty(name)&&
                !TextUtils.isEmpty(email)&&
                Patterns.EMAIL_ADDRESS.matcher(email).matches()&&
                !TextUtils.isEmpty(phone_code)&&
                !TextUtils.isEmpty(phone)&&
                phone.length()==10&&
                password.length()>=6&&
                !TextUtils.isEmpty(city_id)&&
                image_license!=null&&
                image_form!=null&&
                image_identity!=null&&
                isAcceptTerms
        )
        {
            error_name.set(null);
            error_email.set(null);
            error_phone_code.set(null);
            error_phone.set(null);
            error_password.set(null);
            error_about_me.set(null);

            return true;
        }else
        {

            if (name.isEmpty())
            {
                error_name.set(context.getString(R.string.field_req));
            }else
            {
                error_name.set(null);
            }

            if (email.isEmpty())
            {
                error_email.set(context.getString(R.string.field_req));

            }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                error_email.set(context.getString(R.string.inv_email));
            }
            else
            {
                error_email.set(null);
            }

            if (phone.isEmpty())
            {
                error_phone.set(context.getString(R.string.field_req));
            }else if (phone.length()!=10)
            {
                Toast.makeText(context, R.string.inv_phone, Toast.LENGTH_SHORT).show();
            }else
            {
                error_phone.set(null);
            }

            if (phone.isEmpty())
            {
                error_phone.set(context.getString(R.string.field_req));
            }else
            {
                error_phone.set(null);
            }

            if (password.isEmpty())
            {
                error_password.set(context.getString(R.string.field_req));
            }else if (password.length()<6)
            {
                error_password.set(context.getString(R.string.pass_short));
            }else
            {
                error_password.set(null);
            }



            if (city_id.isEmpty())
            {
                Toast.makeText(context, R.string.ch_dept, Toast.LENGTH_SHORT).show();

            }

            if (!isAcceptTerms)
            {
                Toast.makeText(context, R.string.cnt_sign_accept, Toast.LENGTH_SHORT).show();

            }


            if (image_license==null)
            {
                Toast.makeText(context, R.string.ch_img_license, Toast.LENGTH_SHORT).show();
            }

            if (image_identity==null)
            {
                Toast.makeText(context, R.string.ch_img_identity, Toast.LENGTH_SHORT).show();
            }
            if (image_form==null)
            {
                Toast.makeText(context, R.string.ch_img_form, Toast.LENGTH_SHORT).show();
            }


            return false;
        }
    }

    public SignUpModel() {
        this.phone_code = "";
        notifyPropertyChanged(BR.phone_code);
        this.phone="";
        notifyPropertyChanged(BR.phone);
        this.password = "";
        notifyPropertyChanged(BR.password);
        this.name = "";
        notifyPropertyChanged(BR.name);
        this.email = "";
        notifyPropertyChanged(BR.email);
        this.city_id ="";
        image_form = null;
        image_identity = null;
        image_license = null;




    }


    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);

    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);

    }

    @Bindable
    public String getPhone_code() {
        return phone_code;
    }

    public void setPhone_code(String phone_code) {
        this.phone_code = phone_code;
        notifyPropertyChanged(BR.phone_code);

    }

    @Bindable
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);

    }
    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);

    }


    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }


    public boolean isAcceptTerms() {
        return isAcceptTerms;
    }

    public void setAcceptTerms(boolean acceptTerms) {
        isAcceptTerms = acceptTerms;
    }

    public Uri getImage_license() {
        return image_license;
    }

    public void setImage_license(Uri image_license) {
        this.image_license = image_license;
    }

    public Uri getImage_form() {
        return image_form;
    }

    public void setImage_form(Uri image_form) {
        this.image_form = image_form;
    }

    public Uri getImage_identity() {
        return image_identity;
    }

    public void setImage_identity(Uri image_identity) {
        this.image_identity = image_identity;
    }
}
