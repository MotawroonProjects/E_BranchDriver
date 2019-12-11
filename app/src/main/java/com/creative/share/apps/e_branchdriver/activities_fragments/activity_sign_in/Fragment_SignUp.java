package com.creative.share.apps.e_branchdriver.activities_fragments.activity_sign_in;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.creative.share.apps.e_branchdriver.R;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_terms.TermsActivity;
import com.creative.share.apps.e_branchdriver.adapters.CityAdapter;
import com.creative.share.apps.e_branchdriver.databinding.DialogSelectImageBinding;
import com.creative.share.apps.e_branchdriver.databinding.FragmentSignUpBinding;
import com.creative.share.apps.e_branchdriver.interfaces.Listeners;
import com.creative.share.apps.e_branchdriver.models.CityDataModel;
import com.creative.share.apps.e_branchdriver.models.ErrorModel;
import com.creative.share.apps.e_branchdriver.models.SignUpModel;
import com.creative.share.apps.e_branchdriver.models.UserModel;
import com.creative.share.apps.e_branchdriver.preferences.Preferences;
import com.creative.share.apps.e_branchdriver.remote.Api;
import com.creative.share.apps.e_branchdriver.share.Common;
import com.creative.share.apps.e_branchdriver.tags.Tags;
import com.google.gson.Gson;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_SignUp extends Fragment implements Listeners.ShowCountryDialogListener, OnCountryPickerListener,Listeners.BackListener,Listeners.SignUpListener{
    private FragmentSignUpBinding binding;
    private SignInActivity activity;
    private String lang;
    private CountryPicker countryPicker;
    private Preferences preferences;
    private SignUpModel signUpModel;
    private final String READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;
    private final int IMG_REQ1 = 1, IMG_REQ2 = 2,IMG_REQ3=3;
    private Uri imgUri1 = null, imgUri2 = null,imgUri3 = null;
    private int selectedType = 0;
    private List<CityDataModel.CityModel> cityModelList;
    private CityAdapter cityAdapter;

    public static Fragment_SignUp newInstance() {
        return new Fragment_SignUp();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    private void initView() {
        cityModelList = new ArrayList<>();
        cityModelList.add(new CityDataModel.CityModel(0,"إختر المدينة","Choose city"));

        signUpModel = new SignUpModel();
        preferences = Preferences.newInstance();
        activity = (SignInActivity) getActivity();
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());

        binding.setLang(lang);
        binding.setShowCountryListener(this);
        binding.setBackListener(this);
        binding.setSignUpListener(this);
        signUpModel.setPhone_code("00966");
        binding.setSignUpModel(signUpModel);
        //createCountryDialog();

        binding.tvSignIn.setOnClickListener(v->activity.back());
        binding.flLicense.setOnClickListener(view -> CreateImageAlertDialog(IMG_REQ1));
        binding.flForm.setOnClickListener(view -> CreateImageAlertDialog(IMG_REQ2));
        binding.flIdentity.setOnClickListener(view -> CreateImageAlertDialog(IMG_REQ3));

        cityAdapter  = new CityAdapter(activity,cityModelList);
        binding.spinnerCity.setAdapter(cityAdapter);

        binding.checkbox.setOnClickListener(view -> {
            if (binding.checkbox.isChecked())
            {
                signUpModel.setAcceptTerms(true);
                Intent intent = new Intent(activity, TermsActivity.class);
                startActivity(intent);
            }else
                {
                    signUpModel.setAcceptTerms(false);

                }
            binding.setSignUpModel(signUpModel);
        });

        binding.spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i==0)
                {
                    signUpModel.setCity_id("");

                }else
                    {
                        int city_id = cityModelList.get(i).getId_city();
                        signUpModel.setCity_id(String.valueOf(city_id));

                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        getAllCities();

    }

    private void getAllCities() {
        ProgressDialog dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .getAllCities()
                .enqueue(new Callback<CityDataModel>() {
                    @Override
                    public void onResponse(Call<CityDataModel> call, Response<CityDataModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            cityModelList.addAll(response.body().getData());
                            cityAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<CityDataModel> call, Throwable t) {
                        dialog.dismiss();
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


    @Override
    public void checkDataSignUp() {

        if (signUpModel.isDataValid(activity))
        {
            Common.CloseKeyBoard(activity,binding.edtName);

            signUp();
        }
    }

    private void signUp()
    {

        final ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        RequestBody name_part = Common.getRequestBodyText(signUpModel.getName());
        RequestBody phone_part = Common.getRequestBodyText(signUpModel.getPhone());
        RequestBody phone_code_part = Common.getRequestBodyText(signUpModel.getPhone_code());
        RequestBody password_part = Common.getRequestBodyText(signUpModel.getPassword());
        RequestBody email_part = Common.getRequestBodyText(signUpModel.getEmail());
        RequestBody city_id_part = Common.getRequestBodyText(signUpModel.getCity_id());
        RequestBody soft_type = Common.getRequestBodyText("1");
        MultipartBody.Part image1 = Common.getMultiPart(activity,imgUri1,"image1");
        MultipartBody.Part image2 = Common.getMultiPart(activity,imgUri3,"image2");
        MultipartBody.Part image3 = Common.getMultiPart(activity,imgUri2,"image3");

        try {
            Api.getService(Tags.base_url)
                    .signUp(name_part,phone_part,phone_code_part,password_part,email_part,city_id_part,soft_type,image1,image2,image3)
                    .enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                activity.displayFragmentConfirmCode(response.body());
                               /* preferences.create_update_userData(activity, response.body());
                                preferences.createSession(activity, Tags.session_login);
                                Intent intent = new Intent(activity, HomeActivity.class);
                                startActivity(intent);
                                activity.finish();*/

                            } else {



                                if (response.code() == 500) {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                                }else if (response.code()==422)
                                {
                                    try {
                                        ErrorModel errorModel = new Gson().fromJson(response.errorBody().string(), ErrorModel.class);

                                        if (errorModel!=null&&errorModel.getErrors()!=null)
                                        {
                                            if (errorModel.getErrors().getPhone() != null) {
                                                Toast.makeText(activity, R.string.ph_ex, Toast.LENGTH_SHORT).show();

                                            }

                                            if (errorModel.getErrors().getEmail() != null) {
                                                Toast.makeText(activity, R.string.em_ex, Toast.LENGTH_SHORT).show();

                                            }

                                            if (errorModel.getErrors().getPhone() == null&&errorModel.getErrors().getEmail()==null) {
                                                Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                            }




                                        }else
                                            {
                                                Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                            }





                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }


                                }else {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                                }

                                try {
                                    Log.e("error",response.code()+"_"+response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<UserModel> call, Throwable t) {
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
        } catch (Exception e) {
            dialog.dismiss();

        }
    }

    private void createCountryDialog()
    {
        countryPicker = new CountryPicker.Builder()
                .canSearch(true)
                .listener(this)
                .theme(CountryPicker.THEME_NEW)
                .with(activity)
                .build();

        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);

        try {
            if (countryPicker.getCountryFromSIM()!=null)
            {
                updatePhoneCode(countryPicker.getCountryFromSIM());
            }else if (telephonyManager!=null&&countryPicker.getCountryByISO(telephonyManager.getNetworkCountryIso())!=null)
            {
                updatePhoneCode(countryPicker.getCountryByISO(telephonyManager.getNetworkCountryIso()));
            }else if (countryPicker.getCountryByLocale(Locale.getDefault())!=null)
            {
                updatePhoneCode(countryPicker.getCountryByLocale(Locale.getDefault()));
            }else
            {
                String code = "+966";
                binding.tvCode.setText(code);
                signUpModel.setPhone_code(code.replace("+","00"));

            }
        }catch (Exception e)
        {
            String code = "+966";
            binding.tvCode.setText(code);
            signUpModel.setPhone_code(code.replace("+","00"));
        }


    }

    @Override
    public void showDialog() {
        countryPicker.showDialog(activity);
    }

    @Override
    public void onSelectCountry(Country country) {
        updatePhoneCode(country);
    }

    private void updatePhoneCode(Country country)
    {
        binding.tvCode.setText(country.getDialCode());
        signUpModel.setPhone_code(country.getDialCode().replace("+","00"));

    }


    private void CreateImageAlertDialog(final int img_req)
    {

        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();


        DialogSelectImageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(activity),R.layout.dialog_select_image,null,false);




        binding.btnCamera.setOnClickListener(v -> {
            dialog.dismiss();
            selectedType = 2;
            Check_CameraPermission(img_req);

        });

        binding.btnGallery.setOnClickListener(v -> {
            dialog.dismiss();
            selectedType = 1;
            CheckReadPermission(img_req);



        });

        binding.btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().getAttributes().windowAnimations= R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
        dialog.show();
    }
    private void CheckReadPermission(int img_req)
    {
        if (ActivityCompat.checkSelfPermission(activity, READ_PERM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{READ_PERM}, img_req);
        } else {
            SelectImage(1,img_req);
        }
    }

    private void Check_CameraPermission(int img_req)
    {
        if (ContextCompat.checkSelfPermission(activity,camera_permission)!= PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(activity,write_permission)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(activity,new String[]{camera_permission,write_permission},img_req);
        }else
        {
            SelectImage(2,img_req);

        }

    }
    private void SelectImage(int type,int img_req) {

        Intent intent = new Intent();

        if (type == 1)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            }else
            {
                intent.setAction(Intent.ACTION_GET_CONTENT);

            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/*");
            startActivityForResult(intent,img_req);

        }else if (type ==2)
        {
            try {
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,img_req);
            }catch (SecurityException e)
            {
                Toast.makeText(activity,R.string.perm_image_denied, Toast.LENGTH_SHORT).show();
            }
            catch (Exception e)
            {
                Toast.makeText(activity,R.string.perm_image_denied, Toast.LENGTH_SHORT).show();

            }


        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == IMG_REQ1) {

            if (selectedType ==1)
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(selectedType,IMG_REQ1);
                } else {
                    Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }else
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED&& grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(selectedType,IMG_REQ1);
                } else {
                    Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == IMG_REQ2) {
            if (selectedType ==1)
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(selectedType,IMG_REQ2);
                } else {
                    Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }else
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED&& grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(selectedType,IMG_REQ2);
                } else {
                    Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }
        }

        else if (requestCode == IMG_REQ3) {

            if (selectedType ==1)
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(selectedType,IMG_REQ3);
                } else {
                    Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }else
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED&& grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(selectedType,IMG_REQ3);
                } else {
                    Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQ1 && resultCode == Activity.RESULT_OK && data != null) {
            if (selectedType == 1)
            {
                imgUri1 = data.getData();
                signUpModel.setImage_license(imgUri1);
                binding.llLicense.setVisibility(View.GONE);
                File file = new File(Common.getImagePath(activity, imgUri1));
                Picasso.with(activity).load(file).fit().into(binding.imageLicense);
            }else if (selectedType ==2)
            {
                binding.llLicense.setVisibility(View.GONE);

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                imgUri1 = getUriFromBitmap(bitmap);
                signUpModel.setImage_license(imgUri1);

                if (imgUri1 != null) {
                    String path = Common.getImagePath(activity, imgUri1);

                    if (path != null) {
                        Picasso.with(activity).load(new File(path)).fit().into(binding.imageLicense);

                    } else {
                        Picasso.with(activity).load(imgUri1).fit().into(binding.imageLicense);

                    }
                }
            }




        } else if (requestCode == IMG_REQ2 && resultCode == Activity.RESULT_OK && data != null) {

            if (selectedType == 1)
            {
                imgUri2 = data.getData();
                signUpModel.setImage_form(imgUri2);

                binding.llForm.setVisibility(View.GONE);
                File file = new File(Common.getImagePath(activity, imgUri2));

                Picasso.with(activity).load(file).fit().into(binding.imageForm);
            }else if (selectedType ==2)
            {

                binding.llForm.setVisibility(View.GONE);

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                imgUri2 = getUriFromBitmap(bitmap);
                signUpModel.setImage_form(imgUri2);

                if (imgUri2 != null) {
                    String path = Common.getImagePath(activity, imgUri2);

                    if (path != null) {
                        Picasso.with(activity).load(new File(path)).fit().into(binding.imageForm);

                    } else {
                        Picasso.with(activity).load(imgUri2).fit().into(binding.imageForm);

                    }
                }
            }



        }
        else if (requestCode == IMG_REQ3 && resultCode == Activity.RESULT_OK && data != null) {

            if (selectedType == 1)
            {
                imgUri3 = data.getData();
                signUpModel.setImage_identity(imgUri3);

                binding.llIdentity.setVisibility(View.GONE);
                File file = new File(Common.getImagePath(activity, imgUri3));

                Picasso.with(activity).load(file).fit().into(binding.imageIdentity);
            }else if (selectedType ==2)
            {

                binding.llIdentity.setVisibility(View.GONE);

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                imgUri3 = getUriFromBitmap(bitmap);
                signUpModel.setImage_identity(imgUri3);

                if (imgUri3 != null) {
                    String path = Common.getImagePath(activity, imgUri3);

                    if (path != null) {
                        Picasso.with(activity).load(new File(path)).fit().into(binding.imageIdentity);

                    } else {
                        Picasso.with(activity).load(imgUri3).fit().into(binding.imageIdentity);

                    }
                }



            }



        }

    }

    private Uri getUriFromBitmap(Bitmap bitmap) {
        String path = "";
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            path = MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, "title", null);
            return Uri.parse(path);

        } catch (SecurityException e) {
            Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();

        }
        return null;
    }



    @Override
    public void back() {
        activity.back();
    }





}
