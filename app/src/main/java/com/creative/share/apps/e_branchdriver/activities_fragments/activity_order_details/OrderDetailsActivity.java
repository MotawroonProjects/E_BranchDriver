package com.creative.share.apps.e_branchdriver.activities_fragments.activity_order_details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.creative.share.apps.e_branchdriver.R;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_order_details.fragments.Fragment_Client_Order_Details;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_order_details.fragments.Fragment_Store_Order_Details;
import com.creative.share.apps.e_branchdriver.adapters.ViewPagerAdapter;
import com.creative.share.apps.e_branchdriver.databinding.ActivityOrderDetailsBinding;
import com.creative.share.apps.e_branchdriver.interfaces.Listeners;
import com.creative.share.apps.e_branchdriver.language.LanguageHelper;
import com.creative.share.apps.e_branchdriver.models.OrderModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class OrderDetailsActivity extends AppCompatActivity implements Listeners.BackListener{

    private ActivityOrderDetailsBinding binding;
    private String lang;
    private ViewPagerAdapter adapter;
    private List<Fragment> fragmentList;
    private List<String> titles;
    private OrderModel orderModel;
    private int type;



    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_details);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null&&intent.hasExtra("data"))
        {
            orderModel = (OrderModel) intent.getSerializableExtra("data");
            type = intent.getIntExtra("type",0);
        }
    }


    private void initView()
    {
        fragmentList = new ArrayList<>();
        titles = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.tab.setupWithViewPager(binding.pager);
        addFragments_Titles();
        binding.pager.setOffscreenPageLimit(fragmentList.size());
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragments(fragmentList);
        adapter.addTitles(titles);
        binding.pager.setAdapter(adapter);

    }

    private void addFragments_Titles() {

        titles.add(getString(R.string.client));
        titles.add(getString(R.string.store));
        fragmentList.add(Fragment_Client_Order_Details.newInstance(orderModel.getId(),type));
        fragmentList.add(Fragment_Store_Order_Details.newInstance(orderModel.getId()));


    }



    @Override
    public void back() {
        finish();
    }


}
