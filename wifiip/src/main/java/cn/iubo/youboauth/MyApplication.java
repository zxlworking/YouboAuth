package cn.iubo.youboauth;

import android.app.Application;

import cn.iubo.youboauth.common.GlobalCrashHandler;


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
