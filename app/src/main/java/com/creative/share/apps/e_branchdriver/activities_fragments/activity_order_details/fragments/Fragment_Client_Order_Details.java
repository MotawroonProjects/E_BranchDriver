package com.creative.share.apps.e_branchdriver.activities_fragments.activity_order_details.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.creative.share.apps.e_branchdriver.R;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_end_order.EndOrderActivity;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_order_details.OrderDetailsActivity;
import com.creative.share.apps.e_branchdriver.adapters.ProductDetailsAdapter;
import com.creative.share.apps.e_branchdriver.adapters.ReasonsAdapter;
import com.creative.share.apps.e_branchdriver.databinding.AlertCancelReasonBinding;
import com.creative.share.apps.e_branchdriver.databinding.FragmentClientOrderDetailsBinding;
import com.creative.share.apps.e_branchdriver.models.CancelReasonsDataModel;
import com.creative.share.apps.e_branchdriver.models.OrderModel;
import com.creative.share.apps.e_branchdriver.models.UserModel;
import com.creative.share.apps.e_branchdriver.preferences.Preferences;
import com.creative.share.apps.e_branchdriver.remote.Api;
import com.creative.share.apps.e_branchdriver.share.Common;
import com.creative.share.apps.e_branchdriver.tags.Tags;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Client_Order_Details extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "ID";
    private static final String TAG2 = "TYPE";

    private OrderDetailsActivity activity;
    private FragmentClientOrderDetailsBinding binding;
    private UserModel userModel;
    private Preferences preferences;
    private int order_id;
    private FragmentMapTouchListener fragment;
    private GoogleMap mMap;
    private OrderModel orderModel;
    private int type;
    private float zoom = 9.5f;
    private ProductDetailsAdapter adapter;
    private List<CancelReasonsDataModel.CancelModel> cancelModelList;
    private CancelReasonsDataModel.CancelModel cancelModel = null;
    private ReasonsAdapter reasonsAdapter;
    private int reason_type;

    public static Fragment_Client_Order_Details newInstance(int order_id, int type) {
        Bundle bundle = new Bundle();
        bundle.putInt(TAG, order_id);
        bundle.putInt(TAG2, type);
        Fragment_Client_Order_Details fragment_client_order_details = new Fragment_Client_Order_Details();
        fragment_client_order_details.setArguments(bundle);
        return fragment_client_order_details;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_client_order_details, container, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        cancelModelList = new ArrayList<>();

        preferences = Preferences.newInstance();
        activity = (OrderDetailsActivity) getActivity();
        userModel = preferences.getUserData(activity);
        binding.recView.setLayoutManager(new LinearLayoutManager(activity));
        Bundle bundle = getArguments();
        if (bundle != null) {
            order_id = bundle.getInt(TAG);
            type = bundle.getInt(TAG2);
        }

        binding.setOrderType(type);
        initMap();

        binding.llCall.setOnClickListener(view ->
        {

            String phone = orderModel.getUser().getPhone_code().replaceFirst("00", "+") + orderModel.getUser().getPhone();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            startActivity(intent);
        });

        binding.btnMarketProblem.setOnClickListener(view ->
        {
            if (cancelModelList.size() > 0) {
                CreateCancelMarketAlert();
            } else {
                getMarketCancelReasons();
            }
        });

        binding.btnClientProblem.setOnClickListener(view ->
        {
            if (cancelModelList.size() > 0) {
                CreateCancelMarketAlert();
            } else {
                getClientCancelReasons();
            }
        });

        binding.btnPerform.setOnClickListener(view -> startOrder());
        binding.btnMarketSuccess.setOnClickListener(view -> driverReceiveOrderFromMarket());

        binding.btnClientSuccess.setOnClickListener(view -> {
            Intent intent = new Intent(activity, EndOrderActivity.class);
            intent.putExtra("data",orderModel);
            startActivityForResult(intent,1);
        });
    }



    private void initMap() {
        fragment = (FragmentMapTouchListener) getChildFragmentManager().findFragmentById(R.id.map);

        if (fragment != null) {
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
        binding.setStatus(orderModel.getStatus());
        binding.setModel(body);
        addMarker();
        adapter = new ProductDetailsAdapter(orderModel.getOrder_details(), activity);
        binding.recView.setAdapter(adapter);
        binding.scrollView.setVisibility(View.VISIBLE);

    }

    private void addMarker() {
        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(new LatLng(orderModel.getLatitude(), orderModel.getLongitude())));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(orderModel.getLatitude(), orderModel.getLongitude()), zoom));
    }

    private void startOrder() {

        ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.show();

        Api.getService(Tags.base_url)
                .startOrder(order_id, userModel.getId())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {

                            Intent intent = activity.getIntent();
                            intent.putExtra("response", true);
                            activity.setResult(Activity.RESULT_OK, intent);
                            activity.finish();
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
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();

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

    private void driverReceiveOrderFromMarket() {
        ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.show();

        Api.getService(Tags.base_url)
                .receiveOrderFromMarket(order_id, userModel.getId())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {

                            Intent intent = activity.getIntent();
                            intent.putExtra("response", true);
                            activity.setResult(Activity.RESULT_OK, intent);
                            activity.finish();
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
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();

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

    private void driverCancelSendReason() {
        ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.show();

        Api.getService(Tags.base_url)
                .sendReason(order_id, userModel.getId(),reason_type,cancelModel.getId())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {

                            Intent intent = activity.getIntent();
                            intent.putExtra("response", true);
                            activity.setResult(Activity.RESULT_OK, intent);
                            activity.finish();
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
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();

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

    private void getMarketCancelReasons() {

        ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.show();

        Api.getService(Tags.base_url)
                .getMarketReasons()
                .enqueue(new Callback<CancelReasonsDataModel>() {
                    @Override
                    public void onResponse(Call<CancelReasonsDataModel> call, Response<CancelReasonsDataModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            cancelModelList.clear();
                            cancelModelList.addAll(response.body().getData());
                            /*if (response.body().getData().size()>0)
                            {
                                cancelModelList.add(new CancelReasonsDataModel.CancelModel(0,"سبب آخر","Another reason"));
                            }*/

                            reason_type = 0;
                            CreateCancelMarketAlert();


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
                    public void onFailure(Call<CancelReasonsDataModel> call, Throwable t) {
                        try {
                            dialog.dismiss();

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

    private void getClientCancelReasons() {

        ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.show();
        Api.getService(Tags.base_url)
                .getClientReasons()
                .enqueue(new Callback<CancelReasonsDataModel>() {
                    @Override
                    public void onResponse(Call<CancelReasonsDataModel> call, Response<CancelReasonsDataModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            cancelModelList.clear();
                            cancelModelList.addAll(response.body().getData());
                            /*if (response.body().getData().size()>0)
                            {
                                cancelModelList.add(new CancelReasonsDataModel.CancelModel(0,"سبب آخر","Another reason"));
                            }*/
                            reason_type = 1;
                            CreateCancelMarketAlert();


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
                    public void onFailure(Call<CancelReasonsDataModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
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

    private void CreateCancelMarketAlert() {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .create();

        AlertCancelReasonBinding binding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.alert_cancel_reason, null, false);
        binding.setModel(orderModel);
        binding.recView.setLayoutManager(new LinearLayoutManager(activity));
        reasonsAdapter = new ReasonsAdapter(cancelModelList, activity, this);
        binding.recView.setAdapter(reasonsAdapter);
        binding.btnCancel.setOnClickListener(view -> {
            dialog.dismiss();
            cancelModel = null;
        });
        binding.btnSend.setOnClickListener(view ->
        {
            if (cancelModel != null) {
                dialog.dismiss();

                driverCancelSendReason();

            } else {
                Toast.makeText(activity, R.string.ch_reason, Toast.LENGTH_LONG).show();
            }
        });
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
        dialog.show();
    }



    public void setItemData(CancelReasonsDataModel.CancelModel model) {
        this.cancelModel = model;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1&&resultCode==Activity.RESULT_OK&&data!=null)
        {
            Intent intent = activity.getIntent();
            intent.putExtra("response",true);
            activity.setResult(Activity.RESULT_OK,intent);
            activity.finish();
        }
    }
}
