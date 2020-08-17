package com.sample.sendmobiledata;

import androidx.appcompat.app.AppCompatActivity;

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
import com.sample.sendmobiledata.models.MobileInfo;
import com.sample.sendmobiledata.services.InfoService;

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

    public static final String ANDROID_ID = "android_id";
    public static final String BATTERY_LEVEL = "battery_level";
    public static final String MODEL_NAME = "model_name";
    public static final String ANDROID_VERSION = "android_version";

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

        showInfo();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(getBaseContext(), InfoService.class);
                serviceIntent.putExtra(ANDROID_ID,IMEINumber);
                serviceIntent.putExtra(MODEL_NAME, buildModel);
                serviceIntent.putExtra(BATTERY_LEVEL,batteryLevel);
                serviceIntent.putExtra(ANDROID_VERSION,buildVersion);

                startService(serviceIntent);
            }
        });

    }

    /**
     *  show mobile info in the text view.
     */
    private void showInfo() {

        String content = "";

        content += "ID: " + IMEINumber + "\n";
        content += "MODEL: " + buildModel + "\n";
        content += "VERSION: " + buildVersion + "\n";

        tv.setText(content);
    }

    /**
     *  As of Android Q, the os simply doesn't return IMEI number of the
     * device to third-party apps, therefore we get another unique id called ANDROID_ID.
    */
    public static String getDeviceId(Context context) {
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
        /* getting the battery level
        and normalizing the value.
        */
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
                tv.append("Battery: " + batteryLevel);//appending now because it updates the value last
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);

    }

}