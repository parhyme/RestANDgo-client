package com.sample.sendmobiledata;

import com.sample.sendmobiledata.models.MobileInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * An interface to call and interact with the RESTful API.
 */
public interface JsonPlaceHolderApi {

    @POST("post-model/")
    Call<MobileInfo> creatMobileInfo(@Body MobileInfo mobileInfo);
}
