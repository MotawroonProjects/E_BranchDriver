package com.creative.share.apps.e_branchdriver.activities_fragments.activity_rate;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.creative.share.apps.e_branchdriver.R;
import com.creative.share.apps.e_branchdriver.adapters.RateAdapter;
import com.creative.share.apps.e_branchdriver.databinding.ActivityRateBinding;
import com.creative.share.apps.e_branchdriver.interfaces.Listeners;
import com.creative.share.apps.e_branchdriver.language.LanguageHelper;
import com.creative.share.apps.e_branchdriver.models.RateModel;
import com.creative.share.apps.e_branchdriver.models.UserModel;
import com.creative.share.apps.e_branchdriver.preferences.Preferences;
import com.creative.share.apps.e_branchdriver.remote.Api;
import com.creative.share.apps.e_branchdriver.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RateActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityRateBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;
    private RateAdapter  adapter;
    private List<RateModel.Rate> rateList;
    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_rate);
        initView();
    }

    private void initView() {
        rateList = new ArrayList<>();
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang",Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RateAdapter(rateList,this);
        binding.recView.setAdapter(adapter);
        getRate();
    }

    private void getRate() {

        Api.getService(Tags.base_url)
                .getRate(userModel.getId())
                .enqueue(new Callback<RateModel>() {
                    @Override
                    public void onResponse(Call<RateModel> call, Response<RateModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {

                            updateUI(response.body());
                        } else {

                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(RateActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(RateActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RateModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(RateActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RateActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void updateUI(RateModel rateModel) {
        binding.setRate(rateModel.getRating_total());
        rateList.clear();
        rateList.addAll(rateModel.getRating_in_week());
        if (rateModel.getRating_in_week().size()>0)
        {
            binding.tvNoRate.setVisibility(View.GONE);
        }else
            {
                binding.tvNoRate.setVisibility(View.VISIBLE);

            }
    }

    @Override
    public void back() {
        finish();
    }
}
