package com.sample.sendmobiledata.services;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.jaredrummler.android.device.DeviceName;
import com.sample.sendmobiledata.JsonPlaceHolderApi;
import com.sample.sendmobiledata.MainActivity;
import com.sample.sendmobiledata.R;
import com.sample.sendmobiledata.models.MobileInfo;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A service used to send the mobile information
 * to the RESTful API
 */
public class InfoService extends Service {

    private String IMEINumber;
    private String buildModel;
    private String buildVersion;
    private int batteryLevel;
    private String id;

    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private MobileInfo mobileInfo;

    Call<MobileInfo> call;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();

        id =  UUID.randomUUID().toString();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IMEINumber= intent.getStringExtra(MainActivity.ANDROID_ID);
        batteryLevel = intent.getIntExtra(MainActivity.BATTERY_LEVEL,0);
        buildModel = intent.getStringExtra(MainActivity.MODEL_NAME);
        buildVersion = intent.getStringExtra(MainActivity.ANDROID_VERSION);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.3.2:8000/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        mobileInfo = new MobileInfo(buildModel,id,IMEINumber,batteryLevel,buildVersion);
        call = jsonPlaceHolderApi.creatMobileInfo(mobileInfo);

        call.enqueue(new Callback<MobileInfo>() {
            @Override
            public void onResponse(Call<MobileInfo> call, Response<MobileInfo> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getBaseContext(), response.message(), Toast.LENGTH_LONG).show();
                    return;
                }

                MobileInfo mfResponse = response.body();

                if(response.code()==201 && mfResponse != null) {
                    Toast.makeText(getBaseContext(), "Information was sent Successfully.(ANDROID ID: " +
                            mfResponse.getAndroidId() , Toast.LENGTH_LONG).show();
                    stopSelf(startId);
                }
                else {
                    Toast.makeText(getBaseContext(), "There was a problem sending info", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<MobileInfo> call, Throwable t) {
                Toast.makeText(getBaseContext(),"SEND FAILED "+ t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
    }

}
