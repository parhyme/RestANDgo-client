package com.sample.sendmobiledata;

import com.google.gson.annotations.SerializedName;

public class MobileInfo {
    String model_name;
    String id;//IMEI or android id
    String android_id;//IMEI or android id
    int battery_level;
    String android_version;

    public MobileInfo(String model_name, String id, String androidId, int battery_level, String version) {
        this.model_name = model_name;
        this.id = id;
        this.android_id = androidId;
        this.battery_level = battery_level;
        this.android_version = version;
    }

    public MobileInfo(String model_name, String android_id, int battery_level, String android_version) {
        this.model_name = model_name;
        this.android_id = android_id;
        this.battery_level = battery_level;
        this.android_version = android_version;
    }

    public String getModel_name() {
        return model_name;
    }

    public void setModel_name(String model_name) {
        this.model_name = model_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAndroidId() {
        return android_id;
    }

    public void setAndroidId(String androidId) {
        this.android_id = androidId;
    }

    public int getBattery_level() {
        return battery_level;
    }

    public void setBattery_level(int battery_level) {
        this.battery_level = battery_level;
    }

    public String getVersion() {
        return android_version;
    }

    public void setVersion(String version) {
        this.android_version = version;
    }
}
