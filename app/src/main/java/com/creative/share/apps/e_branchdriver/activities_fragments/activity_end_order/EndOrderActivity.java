package com.creative.share.apps.e_branchdriver.activities_fragments.activity_end_order;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.creative.share.apps.e_branchdriver.R;
import com.creative.share.apps.e_branchdriver.databinding.ActivityEndOrderBinding;
import com.creative.share.apps.e_branchdriver.databinding.DialogSelectImageBinding;
import com.creative.share.apps.e_branchdriver.interfaces.Listeners;
import com.creative.share.apps.e_branchdriver.language.LanguageHelper;
import com.creative.share.apps.e_branchdriver.models.OrderModel;
import com.creative.share.apps.e_branchdriver.models.UserModel;
import com.creative.share.apps.e_branchdriver.preferences.Preferences;
import com.creative.share.apps.e_branchdriver.remote.Api;
import com.creative.share.apps.e_branchdriver.share.Common;
import com.creative.share.apps.e_branchdriver.tags.Tags;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EndOrderActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityEndOrderBinding binding;
    private String lang;
    private OrderModel orderModel;
    private Preferences preferences;
    private UserModel userModel;
    private final String READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;
    private final int IMG_REQ1 = 1, IMG_REQ2 = 2;
    private Uri imgUri1 = null, imgUri2 = null;
    private int selectedType = 0;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_end_order);
        getDataFromIntent();
        initView();
    }


    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("data")) {
            orderModel = (OrderModel) intent.getSerializableExtra("data");
        }
    }

    private void initView() {
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.setModel(orderModel);

        binding.btnBill.setOnClickListener(view -> CreateDialogImageAlert(IMG_REQ1));
        binding.btnOrder.setOnClickListener(view -> CreateDialogImageAlert(IMG_REQ2));
        binding.btnDon.setOnClickListener(view ->
        {
            String client_code = binding.edtMembership.getText().toString().trim();
            if (!client_code.isEmpty()&&
                    imgUri1!=null&&
                    imgUri2!=null
            )
            {
                binding.edtMembership.setError(null);
                Common.CloseKeyBoard(this,binding.edtMembership);
                endOrder(client_code);
            }else
                {
                    if (client_code.isEmpty())
                    {
                        binding.edtMembership.setError(getString(R.string.field_req));
                    }else
                        {
                            binding.edtMembership.setError(null);

                        }

                    if (imgUri1==null)
                    {
                        Toast.makeText(this, R.string.ch_bill_photo, Toast.LENGTH_SHORT).show();
                    }

                    if (imgUri2==null)
                    {
                        Toast.makeText(this, R.string.ch_order_photo, Toast.LENGTH_SHORT).show();
                    }
                }

        });
    }

    private void endOrder(String client_code) {

        final ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        RequestBody code_part = Common.getRequestBodyText(client_code);
        RequestBody order_id_part = Common.getRequestBodyText(String.valueOf(orderModel.getId()));
        RequestBody driver_id_part = Common.getRequestBodyText(String.valueOf(userModel.getId()));

        MultipartBody.Part image1 = Common.getMultiPart(this,imgUri1,"bill_image");
        MultipartBody.Part image2 = Common.getMultiPart(this,imgUri2,"order_image");

        try {
            Api.getService(Tags.base_url)
                    .endOrder(code_part,order_id_part,driver_id_part,image1,image2)
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            dialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {

                                Intent intent = getIntent();
                                setResult(RESULT_OK,intent);
                                finish();
                            } else {



                                if (response.code() == 500) {
                                    Toast.makeText(EndOrderActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                                }else {
                                    Toast.makeText(EndOrderActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                                }

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
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(EndOrderActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(EndOrderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void CreateDialogImageAlert(int req) {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .create();

        DialogSelectImageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_select_image, null, false);

        binding.btnCamera.setOnClickListener(view ->
        {
            dialog.dismiss();
            selectedType = 2;
            checkCameraPermission(req);
        });
        binding.btnGallery.setOnClickListener(view ->
        {
            dialog.dismiss();
            selectedType = 1;
            CheckReadPermission(req);

        });
        binding.btnCancel.setOnClickListener(v -> {
                    dialog.dismiss();

                }

        );
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
        dialog.show();
    }

    private void checkCameraPermission(int req) {
        if (ContextCompat.checkSelfPermission(this, camera_permission) == PackageManager.PERMISSION_GRANTED) {
            SelectImage(req);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{camera_permission, write_permission}, req);
        }
    }

    private void CheckReadPermission(int req) {
        if (ActivityCompat.checkSelfPermission(this, READ_PERM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_PERM}, req);
        } else {
            SelectImage(req);
        }
    }

    private void SelectImage(int req) {

        Intent intent = new Intent();

        if (selectedType == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            } else {
                intent.setAction(Intent.ACTION_GET_CONTENT);

            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/*");
            startActivityForResult(intent, req);

        } else if (selectedType == 2) {
            try {
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, req);
            } catch (SecurityException e) {
                Toast.makeText(this, R.string.perm_image_denied, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, R.string.perm_image_denied, Toast.LENGTH_SHORT).show();

            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == IMG_REQ1 || requestCode == IMG_REQ2) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SelectImage(requestCode);
            } else {
                Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQ1 && resultCode == Activity.RESULT_OK && data != null) {
            if (selectedType == 1) {
                imgUri1 = data.getData();
                File file = new File(Common.getImagePath(this, imgUri1));
                Picasso.with(this).load(file).fit().into(binding.imageBill);
            } else if (selectedType == 2) {


                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                imgUri1 = getUriFromBitmap(bitmap);

                if (imgUri1 != null) {
                    String path = Common.getImagePath(this, imgUri1);

                    if (path != null) {
                        Picasso.with(this).load(new File(path)).fit().into(binding.imageBill);

                    } else {
                        Picasso.with(this).load(imgUri1).fit().into(binding.imageBill);

                    }
                }
            }


        } else if (requestCode == IMG_REQ2 && resultCode == Activity.RESULT_OK && data != null) {
            if (selectedType == 1) {
                imgUri2 = data.getData();
                File file = new File(Common.getImagePath(this, imgUri2));
                Picasso.with(this).load(file).fit().into(binding.imageOrder);

            } else if (selectedType == 2) {


                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                imgUri2 = getUriFromBitmap(bitmap);

                if (imgUri2 != null) {
                    String path = Common.getImagePath(this, imgUri2);

                    if (path != null) {
                        Picasso.with(this).load(new File(path)).fit().into(binding.imageOrder);

                    } else {
                        Picasso.with(this).load(imgUri1).fit().into(binding.imageOrder);

                    }
                }
            }


        }

    }

    private Uri getUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        return Uri.parse(MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "", ""));
    }


    @Override
    public void back() {
        finish();
    }

}
