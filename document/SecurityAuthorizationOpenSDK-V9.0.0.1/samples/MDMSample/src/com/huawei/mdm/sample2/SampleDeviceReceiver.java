package com.huawei.mdm.sample2;

import com.huawei.mdm.sample2.R;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;


public class SampleDeviceReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDisabled(Context context, Intent intent) {

    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        // TODO Auto-generated method stub
        return context.getString(R.string.disable_warning);
    }

}