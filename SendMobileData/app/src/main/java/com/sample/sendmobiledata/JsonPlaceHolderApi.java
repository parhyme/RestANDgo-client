package com.sample.sendmobiledata;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface JsonPlaceHolderApi {

    @GET("get-all-data/")
    Call<List<MobileInfo>> getMobileInfos();


    @POST("post-model/")
    Call<MobileInfo> creatMobileInfo(@Body MobileInfo mobileInfo);
}
