package com.creative.share.apps.e_branchdriver.services;


import com.creative.share.apps.e_branchdriver.models.BalanceModel;
import com.creative.share.apps.e_branchdriver.models.CancelReasonsDataModel;
import com.creative.share.apps.e_branchdriver.models.CityDataModel;
import com.creative.share.apps.e_branchdriver.models.CopunModel;
import com.creative.share.apps.e_branchdriver.models.OrderDataModel;
import com.creative.share.apps.e_branchdriver.models.OrderModel;
import com.creative.share.apps.e_branchdriver.models.PlaceGeocodeData;
import com.creative.share.apps.e_branchdriver.models.PlaceMapDetailsData;
import com.creative.share.apps.e_branchdriver.models.RateModel;
import com.creative.share.apps.e_branchdriver.models.TermsModel;
import com.creative.share.apps.e_branchdriver.models.UserModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Service {

    @GET("place/findplacefromtext/json")
    Call<PlaceMapDetailsData> searchOnMap(@Query(value = "inputtype") String inputtype,
                                          @Query(value = "input") String input,
                                          @Query(value = "fields") String fields,
                                          @Query(value = "language") String language,
                                          @Query(value = "key") String key
    );

    @GET("geocode/json")
    Call<PlaceGeocodeData> getGeoData(@Query(value = "latlng") String latlng,
                                      @Query(value = "language") String language,
                                      @Query(value = "key") String key);


    @FormUrlEncoded
    @POST("api/terms")
    Call<TermsModel> getAppData(@Field("type") String type);

    @GET("api/ALl-Cities")
    Call<CityDataModel> getAllCities();

    @FormUrlEncoded
    @POST("api/driver/login")
    Call<UserModel> login(@Field("phone_code") String phone_code,
                          @Field("phone") String phone,
                          @Field("password") String password);

    @Multipart
    @POST("api/driver/register")
    Call<UserModel> signUp(@Part("full_name") RequestBody full_name,
                           @Part("phone") RequestBody phone,
                           @Part("phone_code") RequestBody phone_code,
                           @Part("password") RequestBody password,
                           @Part("email") RequestBody email,
                           @Part("city_id") RequestBody city_id,
                           @Part("software_type") RequestBody software_type,
                           @Part MultipartBody.Part image1,
                           @Part MultipartBody.Part image2,
                           @Part MultipartBody.Part image3

    );


    @FormUrlEncoded
    @POST("api/client/cofirm-code")
    Call<ResponseBody> confirmCode(@Field("user_id") int user_id,
                                   @Field("code") String code
    );

    @FormUrlEncoded
    @POST("api/client/code/send")
    Call<ResponseBody> resendCode(@Field("user_id") int user_id
    );

    @FormUrlEncoded
    @POST("api/driver/orders/available")
    Call<OrderDataModel> getAvailableOrders(@Field("driver_id") int user_id,
                                            @Field("page") int page
    );

    @FormUrlEncoded
    @POST("api/driver/orders/going")
    Call<OrderDataModel> getPendingOrders(@Field("driver_id") int user_id,
                                          @Field("page") int page
    );

    @FormUrlEncoded
    @POST("api/driver/orders/compete")
    Call<OrderDataModel> getDeliveredOrders(@Field("driver_id") int user_id,
                                            @Field("page") int page
    );

    @FormUrlEncoded
    @POST("api/driver/orders/discarded")
    Call<OrderDataModel> getDiscardedOrders(@Field("driver_id") int user_id,
                                            @Field("page") int page
    );

    @FormUrlEncoded
    @POST("api/driver/orders/stumbled")
    Call<OrderDataModel> getStumbledOrders(@Field("driver_id") int user_id,
                                           @Field("page") int page
    );

    @FormUrlEncoded
    @POST("api/driver/balance")
    Call<BalanceModel> getBalance(@Field("driver_id") int driver_id
    );


    @FormUrlEncoded
    @POST("api/payment/create")
    Call<CopunModel> addBalance(@Field("driver_id") int driver_id,
                                @Field("coupon_number") String coupon_number
    );

    @FormUrlEncoded
    @POST("api/driver/rating")
    Call<RateModel> getRate(@Field("driver_id") int driver_id
    );

    @FormUrlEncoded
    @POST("api/single-order")
    Call<OrderModel> getSingleOrder(@Field("order_id") int order_id
    );

    @GET("api/marketCancelReasons")
    Call<CancelReasonsDataModel> getMarketReasons();

    @GET("api/clientCancelReasons")
    Call<CancelReasonsDataModel> getClientReasons();


    @FormUrlEncoded
    @POST("api/driver/order/accept")
    Call<ResponseBody> startOrder(@Field("order_id") int order_id,
                                  @Field("driver_id") int driver_id

    );

    @FormUrlEncoded
    @POST("api/driver/order/receive")
    Call<ResponseBody> receiveOrderFromMarket(@Field("order_id") int order_id,
                                              @Field("driver_id") int driver_id

    );

    @FormUrlEncoded
    @POST("api/driver/order/cancel")
    Call<ResponseBody> sendReason(@Field("order_id") int order_id,
                                  @Field("driver_id") int driver_id,
                                  @Field("type") int type,
                                  @Field("cancel_reason") int cancel_reason

    );


    @Multipart
    @POST("api/driver/order/end")
    Call<ResponseBody> endOrder(@Part("membership_code") RequestBody membership_code,
                                @Part("order_id") RequestBody order_id,
                                @Part("driver_id") RequestBody driver_id,
                                @Part MultipartBody.Part image1,
                                @Part MultipartBody.Part image2

    );
    @FormUrlEncoded
    @POST("api/phone-tokens")
    Call<ResponseBody> updateToken(
            @Field("user_id") int user_id,
            @Field("phone_token") String phone_token,

            @Field("software_type") int software_type
    );
}





