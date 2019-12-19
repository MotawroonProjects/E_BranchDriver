package com.creative.share.apps.e_branchdriver.activities_fragments.activity_profile;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.creative.share.apps.e_branchdriver.R;
import com.creative.share.apps.e_branchdriver.databinding.ActivityProfileBinding;
import com.creative.share.apps.e_branchdriver.interfaces.Listeners;
import com.creative.share.apps.e_branchdriver.language.LanguageHelper;
import com.creative.share.apps.e_branchdriver.models.UserModel;
import com.creative.share.apps.e_branchdriver.preferences.Preferences;

import java.util.Locale;

import io.paperdb.Paper;

public class ProfileActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityProfileBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;





    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        initView();
    }




    private void initView()
    {
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.setUserModel(userModel);



    }



    @Override
    public void back() {
        finish();
    }

}
