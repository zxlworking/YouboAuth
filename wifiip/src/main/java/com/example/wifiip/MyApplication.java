package com.example.wifiip;

import android.app.Application;

import com.example.wifiip.common.GlobalCrashHandler;

/**
 * Created by zxl on 2018/9/6.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        GlobalCrashHandler.getInstance(this);

    }

}
