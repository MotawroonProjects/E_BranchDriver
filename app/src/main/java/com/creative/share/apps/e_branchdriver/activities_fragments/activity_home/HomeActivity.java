package com.creative.share.apps.e_branchdriver.activities_fragments.activity_home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
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
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_profile.ProfileActivity;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_rate.RateActivity;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_sign_in.SignInActivity;
import com.creative.share.apps.e_branchdriver.adapters.ViewPagerAdapter;
import com.creative.share.apps.e_branchdriver.databinding.DialogLanguageBinding;
import com.creative.share.apps.e_branchdriver.interfaces.Listeners;
import com.creative.share.apps.e_branchdriver.language.LanguageHelper;
import com.creative.share.apps.e_branchdriver.models.OrderModel;
import com.creative.share.apps.e_branchdriver.models.UserModel;
import com.creative.share.apps.e_branchdriver.preferences.Preferences;
import com.creative.share.apps.e_branchdriver.remote.Api;
import com.creative.share.apps.e_branchdriver.share.Common;
import com.creative.share.apps.e_branchdriver.tags.Tags;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Listeners.OrderActionListener {

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private CircleImageView image;
    private TextView tvName;
    private Toolbar toolbar;
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
            EventBus.getDefault().register(this);
            updateToken();
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

    private void updateFragments()
    {
        Fragment_Available_Order fragment_available_order = (Fragment_Available_Order) adapter.getItem(0);
        Fragment_Pending_Order  fragment_pending_order = (Fragment_Pending_Order) adapter.getItem(1);
        Fragment_Delivered_Order fragment_delivered_order = (Fragment_Delivered_Order) adapter.getItem(2);
        Fragment_Discarded_Order fragment_discarded_order = (Fragment_Discarded_Order) adapter.getItem(3);
        Fragment_Stumble_Order fragment_stumble_order = (Fragment_Stumble_Order) adapter.getItem(4);

        fragment_available_order.getOrder(true);
        fragment_pending_order.getOrder(true);
        fragment_delivered_order.getOrder(true);
        fragment_discarded_order.getOrder(true);
        fragment_stumble_order.getOrder(true);


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.profile:
                if (userModel!=null)
                {
                    navigateToProfileActivity();


                }else
                {
                    Common.CreateDialogAlert(this,getString(R.string.please_sign_in_or_sign_up));
                }
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
                CreateLangDialog();
                break;
            case R.id.logout:
                logout();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }


    private void CreateLangDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .create();

        DialogLanguageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_language, null, false);
        String lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        if (lang.equals("ar")) {
            binding.rbAr.setChecked(true);
        } else {
            binding.rbEn.setChecked(true);

        }
        binding.btnCancel.setOnClickListener((v) ->
                dialog.dismiss()

        );
        binding.rbAr.setOnClickListener(view -> {
            dialog.dismiss();
            new Handler()
                    .postDelayed(() -> refreshActivity("ar"), 1000);
        });
        binding.rbEn.setOnClickListener(view -> {
            dialog.dismiss();
            new Handler()
                    .postDelayed(() -> refreshActivity("en"), 1000);
        });
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
        dialog.show();
    }

    private void refreshActivity(String lang) {
        preferences.selectedLanguage(this, lang);
        Paper.book().write("lang", lang);
        LanguageHelper.setNewLocale(this, lang);
        Intent intent = getIntent();
        finish();
        startActivity(intent);

    }

    private void navigateToProfileActivity() {

        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
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


    @Override
    public void onSuccess() {
        updateFragments();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ListenNotificationChange(OrderModel order_model)
    {
            if(fragmentList!=null){
                Fragment_Available_Order fragment_finshied_order= (Fragment_Available_Order) fragmentList.get(0);
                new Handler()
                        .postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fragment_finshied_order.getOrder(false);
                                pager.setCurrentItem(1);}
                        },1);
            }}
    private void updateToken() {
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            String token = task.getResult().getToken();
                            //Log.e("s",token);
                            Api.getService(Tags.base_url)
                                    .updateToken(userModel.getId(), token,1)
                                    .enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                            if (response.isSuccessful()) {
                                                try {
                                                    Log.e("Success", "token updated");
                                                } catch (Exception e) {
                                                    //  e.printStackTrace();
                                                }
                                            }
                                            else {
                                                try {
                                                    Log.e("error",response.code()+"_"+response.errorBody().string());
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }


                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            try {
                                                Log.e("Error", t.getMessage());
                                            } catch (Exception e) {
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

}
