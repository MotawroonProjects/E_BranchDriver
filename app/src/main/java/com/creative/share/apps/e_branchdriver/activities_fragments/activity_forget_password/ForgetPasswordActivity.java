package com.creative.share.apps.e_branchdriver.activities_fragments.activity_forget_password;

import android.content.Context;
import android.os.Bundle;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.creative.share.apps.e_branchdriver.R;
import com.creative.share.apps.e_branchdriver.databinding.ActivityForgetPasswordBinding;
import com.creative.share.apps.e_branchdriver.interfaces.Listeners;
import com.creative.share.apps.e_branchdriver.language.LanguageHelper;
import com.creative.share.apps.e_branchdriver.models.UserModel;
import com.creative.share.apps.e_branchdriver.preferences.Preferences;
import com.creative.share.apps.e_branchdriver.share.Common;

import java.util.Locale;

import io.paperdb.Paper;

public class ForgetPasswordActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityForgetPasswordBinding binding;
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
        binding = DataBindingUtil.setContentView(this,R.layout.activity_forget_password);
        initView();
    }

    private void initView() {
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang",Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);

        binding.btnSend.setOnClickListener(view -> checkData());


    }

    private void checkData() {

        String email = binding.edtEmail.getText().toString().trim();
        if (!email.isEmpty()&& Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Common.CloseKeyBoard(this,binding.edtEmail);
            binding.edtEmail.setError(null);
        }else
            {
                if (email.isEmpty())
                {
                    binding.edtEmail.setError(getString(R.string.field_req));
                }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    binding.edtEmail.setError(getString(R.string.inv_email));

                }
            }
    }

    @Override
    public void back() {
        finish();
    }
}
