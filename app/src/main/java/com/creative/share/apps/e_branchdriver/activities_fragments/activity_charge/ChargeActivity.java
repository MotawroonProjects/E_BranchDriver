package com.creative.share.apps.e_branchdriver.activities_fragments.activity_charge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.creative.share.apps.e_branchdriver.R;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_balance.BalanceActivity;
import com.creative.share.apps.e_branchdriver.databinding.ActivityChargeBinding;
import com.creative.share.apps.e_branchdriver.interfaces.Listeners;
import com.creative.share.apps.e_branchdriver.language.LanguageHelper;
import com.creative.share.apps.e_branchdriver.models.UserModel;
import com.creative.share.apps.e_branchdriver.preferences.Preferences;
import com.creative.share.apps.e_branchdriver.share.Common;

import java.util.Locale;

import io.paperdb.Paper;

public class ChargeActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityChargeBinding binding;
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
        binding = DataBindingUtil.setContentView(this,R.layout.activity_charge);
        initView();
    }

    private void initView() {
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang",Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);

        binding.btnCharge.setOnClickListener(view ->
        {
            String identity = binding.edtIdentity.getText().toString().trim();
            if(!identity.isEmpty())
            {
                Common.CloseKeyBoard(this,binding.edtIdentity);
                binding.edtIdentity.setError(null);
                if (userModel!=null)
                {
                    Intent intent = new Intent(this, BalanceActivity.class);
                    intent.putExtra("identity",identity);
                    startActivity(intent);
                }else
                    {
                        Common.CreateDialogAlert(this,getString(R.string.please_sign_in_or_sign_up));
                    }

            }else
                {
                    binding.edtIdentity.setError(getString(R.string.field_req));

                }
        });


    }

    @Override
    public void back() {
        finish();
    }
}
