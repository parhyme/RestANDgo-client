package com.sample.sendmobiledata;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.android.device.DeviceName;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView tv;
    private Button button;

    private String IMEINumber;
    private String buildModel;
    private String buildVersion;
    private int batteryLevel;
    private static final int REQUEST_CODE = 101;

    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private MobileInfo mobileInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DeviceName.init(this);

        tv= (TextView) findViewById(R.id.tv);
        button= (Button) findViewById(R.id.sendbtn);

        IMEINumber = getDeviceId(this);
        cnfBatteryLevel();
        buildModel = DeviceName.getDeviceName();
        buildVersion = Build.VERSION.RELEASE;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.3.2:8000/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        String content = "";

        content += "ID: " + IMEINumber + "\n";
        content += "MODEL: " + buildModel + "\n";
        content += "VERSION: " + buildVersion + "\n";
        content += "Battery: " + batteryLevel + "\n";

        tv.setText(content);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                creatMobileInfo();

            }
        });

    }

    private void creatMobileInfo() {
        String id =  UUID.randomUUID().toString();
        mobileInfo = new MobileInfo(buildModel,id,IMEINumber,batteryLevel,buildVersion);

        Call<MobileInfo> call = jsonPlaceHolderApi.creatMobileInfo(mobileInfo);

        call.enqueue(new Callback<MobileInfo>() {
            @Override
            public void onResponse(Call<MobileInfo> call, Response<MobileInfo> response) {
                if(!response.isSuccessful()){
                    tv.setText(response.message());
                    return;
                }

                MobileInfo mfResponse = response.body();

                String content = "";

                content += "ID: " + mfResponse.getAndroidId() + "\n";
                content += "MODEL: " + mfResponse.getModel_name() + "\n";
                content += "CODE: " + response.code() + "\n";

                tv.setText(content);

            }


            @Override
            public void onFailure(Call<MobileInfo> call, Throwable t) {
                Toast.makeText(getBaseContext(),"SEND FAILED "+ t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public static String getDeviceId(Context context) {
        /* As of Android Q, the os simply doesn't return IMEI number of the
        device to third-party apps, therefore we get another unique id called ANDROID_ID.
        */
        String deviceId;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId();
            } else {
                deviceId = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }

        return deviceId;
    }

    private void cnfBatteryLevel() {
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int level = -1;
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }
                batteryLevel = level;
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);

    }

}