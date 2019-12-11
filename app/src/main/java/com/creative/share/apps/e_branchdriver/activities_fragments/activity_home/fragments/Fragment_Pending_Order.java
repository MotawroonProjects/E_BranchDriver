package com.creative.share.apps.e_branchdriver.activities_fragments.activity_home.fragments;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creative.share.apps.e_branchdriver.R;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_home.HomeActivity;
import com.creative.share.apps.e_branchdriver.adapters.OrdersAdapter;
import com.creative.share.apps.e_branchdriver.databinding.FragmentOrdersBinding;
import com.creative.share.apps.e_branchdriver.models.OrderDataModel;
import com.creative.share.apps.e_branchdriver.models.OrderModel;
import com.creative.share.apps.e_branchdriver.models.UserModel;
import com.creative.share.apps.e_branchdriver.preferences.Preferences;
import com.creative.share.apps.e_branchdriver.remote.Api;
import com.creative.share.apps.e_branchdriver.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Pending_Order extends Fragment {
    private HomeActivity activity;
    private FragmentOrdersBinding binding;
    private UserModel userModel;
    private Preferences preferences;
    private int current_page = 1;
    private boolean isLoading = false;
    private OrdersAdapter adapter;
    private List<OrderModel> orderModelList;


    public static Fragment_Pending_Order newInstance()
    {
        return new Fragment_Pending_Order();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_orders,container,false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        preferences = Preferences.newInstance();
        orderModelList = new ArrayList<>();
        activity = (HomeActivity) getActivity();
        userModel = preferences.getUserData(activity);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.recView.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new OrdersAdapter(orderModelList,activity,this);
        binding.recView.setAdapter(adapter);

        binding.recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int total_items = adapter.getItemCount();
                    int lastVisibleItem = ((LinearLayoutManager)(binding.recView.getLayoutManager())).findLastCompletelyVisibleItemPosition();

                    if (lastVisibleItem > 5 && lastVisibleItem == (total_items - 2) && !isLoading) {
                        isLoading = true;
                        int page = current_page + 1;
                        orderModelList.add(null);
                        adapter.notifyDataSetChanged();
                        loadMore(page);

                    }
                }
            }
        });
        getOrder();
    }


    private void getOrder()
    {

        Api.getService(Tags.base_url)
                .getPendingOrders(userModel.getId(), 1)
                .enqueue(new Callback<OrderDataModel>() {
                    @Override
                    public void onResponse(Call<OrderDataModel> call, Response<OrderDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            orderModelList.clear();
                            orderModelList.addAll(response.body().getData());
                            adapter.notifyDataSetChanged();

                            if (orderModelList.size() > 0) {
                                binding.tvNoOrder.setVisibility(View.GONE);
                            } else {
                                binding.tvNoOrder.setVisibility(View.VISIBLE);

                            }
                        } else {

                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<OrderDataModel> call, Throwable t) {
                        try {
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void loadMore(int page)
    {
        Api.getService(Tags.base_url)
                .getPendingOrders(userModel.getId(), page)
                .enqueue(new Callback<OrderDataModel>() {
                    @Override
                    public void onResponse(Call<OrderDataModel> call, Response<OrderDataModel> response) {
                        isLoading = false;
                        orderModelList.remove(orderModelList.size() - 1);
                        adapter.notifyDataSetChanged();
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {

                            current_page = response.body().getCurrent_page();
                            orderModelList.addAll(response.body().getData());
                            adapter.notifyDataSetChanged();
                        } else {
                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<OrderDataModel> call, Throwable t) {
                        try {
                            orderModelList.remove(orderModelList.size() - 1);
                            isLoading = false;
                            adapter.notifyDataSetChanged();

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                });

    }

}
