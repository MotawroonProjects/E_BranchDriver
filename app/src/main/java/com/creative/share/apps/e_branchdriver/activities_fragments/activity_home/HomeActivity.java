package com.creative.share.apps.e_branchdriver.activities_fragments.activity_home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.creative.share.apps.e_branchdriver.R;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_charge.ChargeActivity;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_contact_us.ContactUsActivity;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_home.fragments.Fragment_Available_Order;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_home.fragments.Fragment_Delivered_Order;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_home.fragments.Fragment_Discarded_Order;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_home.fragments.Fragment_Pending_Order;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_home.fragments.Fragment_Stumble_Order;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_rate.RateActivity;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_sign_in.SignInActivity;
import com.creative.share.apps.e_branchdriver.adapters.ViewPagerAdapter;
import com.creative.share.apps.e_branchdriver.language.LanguageHelper;
import com.creative.share.apps.e_branchdriver.models.UserModel;
import com.creative.share.apps.e_branchdriver.preferences.Preferences;
import com.creative.share.apps.e_branchdriver.share.Common;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private CircleImageView image;
    private TextView tvName;
    private  Toolbar toolbar;
    private TabLayout tab;
    private ViewPager pager;
    private List<Fragment> fragmentList;
    private List<String> titles;
    private ViewPagerAdapter adapter;
    private UserModel userModel;
    private Preferences preferences;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();

    }

    private void initView() {
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);

        fragmentList = new ArrayList<>();
        titles = new ArrayList<>();

        toolbar= findViewById(R.id.toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        tab = findViewById(R.id.tab);
        pager = findViewById(R.id.pager);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        image = navigationView.getHeaderView(0).findViewById(R.id.image);
        tvName = navigationView.getHeaderView(0).findViewById(R.id.tvName);

        if (userModel!=null)
        {
            tvName.setText(userModel.getFull_name());
            Log.e("id",userModel.getId()+"__");
        }

        tab.setupWithViewPager(pager);
        addFragments_Titles();
        pager.setOffscreenPageLimit(fragmentList.size());

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragments(fragmentList);
        adapter.addTitles(titles);
        pager.setAdapter(adapter);







    }

    private void addFragments_Titles() {
        fragmentList.add(Fragment_Available_Order.newInstance());
        fragmentList.add(Fragment_Pending_Order.newInstance());
        fragmentList.add(Fragment_Delivered_Order.newInstance());
        fragmentList.add(Fragment_Discarded_Order.newInstance());
        fragmentList.add(Fragment_Stumble_Order.newInstance());

        titles.add(getString(R.string.available_order));
        titles.add(getString(R.string.pending_order));
        titles.add(getString(R.string.delivered_order));
        titles.add(getString(R.string.discarded));
        titles.add(getString(R.string.stumble));



    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.profile:
                break;
            case R.id.availableOrder:
                pager.setCurrentItem(0);
                break;
            case R.id.pending:
                pager.setCurrentItem(1);

                break;
            case R.id.delivered:
                pager.setCurrentItem(2);

                break;
            case R.id.discard:
                pager.setCurrentItem(3);

                break;
            case R.id.stumble:
                pager.setCurrentItem(4);

                break;
            case R.id.charge:

                navigateToBalanceActivity();
                break;
            case R.id.contact:
                navigateToContactUsActivity();
                break;

            case R.id.rate:
                if (userModel!=null)
                {
                    navigateToRateActivity();

                }else
                {
                    Common.CreateDialogAlert(this,getString(R.string.please_sign_in_or_sign_up));
                }
                break;
            case R.id.setting:
                break;
            case R.id.logout:
                logout();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void logout() {

        if (userModel==null)
        {
            navigateToSignInActivity();
        }else
            {
                preferences.clear(this);
                navigateToSignInActivity();

            }

    }

    private void navigateToSignInActivity() {

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        },200);
    }

    private void navigateToBalanceActivity() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, ChargeActivity.class);
            startActivity(intent);
        },200);

    }

    private void navigateToRateActivity() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, RateActivity.class);
            startActivity(intent);
        },200);

    }

    private void navigateToContactUsActivity() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, ContactUsActivity.class);
            startActivity(intent);
        },200);

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }else
            {
                finish();
            }
    }
}
