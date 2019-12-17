package com.creative.share.apps.e_branchdriver.activities_fragments.activity_order_details.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.creative.share.apps.e_branchdriver.R;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_order_details.OrderDetailsActivity;
import com.creative.share.apps.e_branchdriver.databinding.FragmentStoreOrderDetailsBinding;
import com.creative.share.apps.e_branchdriver.models.OrderModel;
import com.creative.share.apps.e_branchdriver.models.UserModel;
import com.creative.share.apps.e_branchdriver.preferences.Preferences;
import com.creative.share.apps.e_branchdriver.remote.Api;
import com.creative.share.apps.e_branchdriver.tags.Tags;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Store_Order_Details extends Fragment implements OnMapReadyCallback {
    private static final String TAG="ID";

    private OrderDetailsActivity activity;
    private FragmentStoreOrderDetailsBinding binding;
    private UserModel userModel;
    private Preferences preferences;
    private int order_id;
    private FragmentMapTouchListener fragment;
    private GoogleMap mMap;
    private OrderModel orderModel;
    private float zoom =9.5f;


    public static Fragment_Store_Order_Details newInstance(int order_id)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(TAG,order_id);

        Fragment_Store_Order_Details fragment_store_order_details = new Fragment_Store_Order_Details();
        fragment_store_order_details.setArguments(bundle);
        return fragment_store_order_details;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_store_order_details,container,false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        preferences = Preferences.newInstance();
        activity = (OrderDetailsActivity) getActivity();
        userModel = preferences.getUserData(activity);
        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            order_id = bundle.getInt(TAG);

        }

        initMap();



    }

    private void initMap() {
        fragment = (FragmentMapTouchListener) getChildFragmentManager().findFragmentById(R.id.map);

        if (fragment!=null)
        {
            fragment.getMapAsync(this);

        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (googleMap != null) {
            mMap = googleMap;
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity, R.raw.maps));
            mMap.setTrafficEnabled(false);
            mMap.setBuildingsEnabled(false);
            mMap.setIndoorEnabled(true);
            fragment.setListener(() -> binding.scrollView.requestDisallowInterceptTouchEvent(true));
            getOrderData();
        }

    }

    private void getOrderData() {

        Api.getService(Tags.base_url)
                .getSingleOrder(order_id)
                .enqueue(new Callback<OrderModel>() {
                    @Override
                    public void onResponse(Call<OrderModel> call, Response<OrderModel> response) {
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
                                Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<OrderModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);
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

    private void updateUI(OrderModel body) {
        orderModel = body;
        binding.setModel(body);
        addMarker();
        binding.scrollView.setVisibility(View.VISIBLE);
    }

    private void addMarker() {
        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(new LatLng(orderModel.getMarket().getLatitude(),orderModel.getMarket().getLongitude())));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(orderModel.getMarket().getLatitude(),orderModel.getMarket().getLongitude()),zoom));
    }
}
