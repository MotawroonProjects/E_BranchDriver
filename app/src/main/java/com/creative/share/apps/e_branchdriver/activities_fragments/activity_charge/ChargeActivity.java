package com.creative.share.apps.e_branchdriver.activities_fragments.activity_charge;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.creative.share.apps.e_branchdriver.R;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_balance.BalanceActivity;
import com.creative.share.apps.e_branchdriver.databinding.ActivityChargeBinding;
import com.creative.share.apps.e_branchdriver.interfaces.Listeners;
import com.creative.share.apps.e_branchdriver.language.LanguageHelper;
import com.creative.share.apps.e_branchdriver.models.CopunModel;
import com.creative.share.apps.e_branchdriver.models.UserModel;
import com.creative.share.apps.e_branchdriver.preferences.Preferences;
import com.creative.share.apps.e_branchdriver.remote.Api;
import com.creative.share.apps.e_branchdriver.share.Common;
import com.creative.share.apps.e_branchdriver.tags.Tags;

import java.io.IOException;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
            Common.CloseKeyBoard(this,binding.edtIdentity);
            binding.edtIdentity.setError(null);

            if (userModel!=null)
            {
                if (identity.isEmpty())
                {
                    Intent intent = new Intent(this, BalanceActivity.class);
                    startActivity(intent);
                }else
                    {
                       addBalance(identity);
                    }

            }else
            {
                Common.CreateDialogAlert(this,getString(R.string.please_sign_in_or_sign_up));
            }

        });


    }

    private void addBalance(String copoun_num)
    {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        try {

            Api.getService(Tags.base_url)
                    .addBalance(userModel.getId(),copoun_num)
                    .enqueue(new Callback<CopunModel>() {
                        @Override
                        public void onResponse(Call<CopunModel> call, Response<CopunModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                Intent intent = new Intent(ChargeActivity.this, BalanceActivity.class);
                                startActivity(intent);
                                Toast.makeText(ChargeActivity.this,getString(R.string.suc), Toast.LENGTH_SHORT).show();
                            } else {

                                try {

                                    Log.e("error", response.code() + "_" + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if (response.code() == 500) {
                                    Toast.makeText(ChargeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                                } else if (response.code() == 422) {
                                    Toast.makeText(ChargeActivity.this, R.string.coupon_not_av, Toast.LENGTH_SHORT).show();


                                }
                                else {
                                    Toast.makeText(ChargeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<CopunModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(ChargeActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ChargeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {
            dialog.dismiss();

        }
    }

    @Override
    public void back() {
        finish();
    }
}
