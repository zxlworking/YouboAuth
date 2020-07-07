package cn.iubo.youboauth;

import android.Manifest;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.android.app.AppOpsManagerEx;
import com.huawei.android.app.admin.DevicePackageManager;
import com.huawei.android.app.admin.DeviceRestrictionManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
    };

//    private String[] mdm_permissions = new String[]{
//            "com.huawei.permission.sec.MDM_PHONE_MANAGER"
//    };

    private DeviceRestrictionManager mDeviceRestrictionManager = null;
    private DevicePolicyManager mDevicePolicyManager = null;
    private ComponentName mAdminName = null;
    private TextView mStatusText;
    private Button wifiDisableBtn;
    private Button wifiEnableBtn;
    private TextView mStatusText2;
    private Button googleDisableBtn;
    private Button googleEnableBtn;
    private TextView mStatusText3;
    private Button captureDisableBtn;
    private Button captureEnableBtn;

    @SuppressWarnings("static-access")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission();

        AppOpsManagerEx appManager = new AppOpsManagerEx();
        try {
            appManager.setMode(AppOpsManagerEx.TYPE_OPEN_WIFI, "cn.iubo.youboauth", AppOpsManagerEx.MODE_ALLOWED);
        } catch (Exception e) {
            //Toast.makeText(this,"AppOpsManagerEx Exception" + e.toString(),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceRestrictionManager = new DeviceRestrictionManager();
        mAdminName = new ComponentName(this, DeviceReceiver.class);

        initSampleView();
        //updateState();
        new SampleEula(this, mDevicePolicyManager, mAdminName, mStatusText).show();

    }

    private void requestPermission() {
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                || PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                || PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                || PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
        }
//        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, "com.huawei.permission.sec.MDM_PHONE_MANAGER")){
//            Toast.makeText(this,"No MDM permission",Toast.LENGTH_SHORT).show();
//            ActivityCompat.requestPermissions(this, mdm_permissions, 2);
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isPermissionOk = true;
        if (requestCode == 1) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    isPermissionOk = false;
                    break;
                }
            }
        }

//        if (requestCode == 2) {
//            for (int result : grantResults) {
//                if (result != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this,"MDM permission granted",Toast.LENGTH_SHORT).show();
//                    break;
//                }
//            }
//        }
    }

    private void initSampleView() {
        mStatusText = (TextView) findViewById(R.id.wifiStateTxt);
        wifiDisableBtn = (Button) findViewById(R.id.disableWifi);
        wifiEnableBtn = (Button) findViewById(R.id.enableWifi);
        mStatusText2 = (TextView) findViewById(R.id.googleAccountStateTxt);
        googleDisableBtn = (Button) findViewById(R.id.disableGoogleAccount);
        googleEnableBtn = (Button) findViewById(R.id.enableGoogleAccount);
        mStatusText3 = (TextView) findViewById(R.id.screenCaptureStateTxt);
        captureDisableBtn = (Button) findViewById(R.id.disableScreenCapture);
        captureEnableBtn = (Button) findViewById(R.id.enableScreenCapture);

        wifiDisableBtn.setOnClickListener(new SampleOnClickListener());
        wifiEnableBtn.setOnClickListener(new SampleOnClickListener());
        googleDisableBtn.setOnClickListener(new SampleOnClickListener());
        googleEnableBtn.setOnClickListener(new SampleOnClickListener());
        captureDisableBtn.setOnClickListener(new SampleOnClickListener());
        captureEnableBtn.setOnClickListener(new SampleOnClickListener());
    }


    private void updateState() {
        if(!isActiveMe()) {
            mStatusText.setText(getString(R.string.state_not_actived));
            mStatusText2.setText(getString(R.string.state_not_actived));
            mStatusText3.setText(getString(R.string.state_not_actived));

            Toast.makeText(this,getString(R.string.state_not_actived),Toast.LENGTH_SHORT).show();
            return;
        }

        if (mDeviceRestrictionManager != null) {
            mDeviceRestrictionManager.setWifiDisabled(mAdminName, true);

            List<String> list = new ArrayList<>();
            list.add("cn.iubo.youboauth");
            DevicePackageManager devicePackageManager = new DevicePackageManager();
            devicePackageManager.addDisallowedUninstallPackages(mAdminName,list);
        }

        boolean isWifiDisabled = false;
        try {
            isWifiDisabled = mDeviceRestrictionManager.isWifiDisabled(mAdminName);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
        if (isWifiDisabled) {
            mStatusText.setText(R.string.state_restricted);
        } else {
            mStatusText.setText(getString(R.string.state_nomal));
        }



//        boolean isWifiDisabled = false;
//        boolean isGoogleAccountDisabled = false;
//        boolean isScreenCaptureDisabled = false;
//        try {
//            isWifiDisabled = mDeviceRestrictionManager.isWifiDisabled(mAdminName);
//            isGoogleAccountDisabled = mDeviceRestrictionManager.isGoogleAccountDisabled(mAdminName);
//            isScreenCaptureDisabled = mDeviceRestrictionManager.isScreenCaptureDisabled(mAdminName);
//        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), e.getMessage(),
//                    Toast.LENGTH_SHORT).show();
//        }
//        if (isWifiDisabled) {
//            mStatusText.setText(R.string.state_restricted);
//        } else {
//            mStatusText.setText(getString(R.string.state_nomal));
//        }
//        if (isGoogleAccountDisabled) {
//            mStatusText2.setText(R.string.state_restricted);
//        } else {
//            mStatusText2.setText(getString(R.string.state_nomal));
//        }
//        if (isScreenCaptureDisabled) {
//            mStatusText3.setText(R.string.state_restricted);
//        } else {
//            mStatusText3.setText(getString(R.string.state_nomal));
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateState();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isActiveMe() {
        if(mDevicePolicyManager == null || !mDevicePolicyManager.isAdminActive(mAdminName)) {
            return false;
        } else {
            return true;
        }
    }

    private class SampleOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            boolean disableWfi = false;
            boolean disableGoogleAccount = false;
            boolean disableScreenCapture = false;

            if (v.getId() == R.id.disableWifi) {
                disableWfi = true;
            } else if (v.getId() == R.id.enableWifi) {
                disableWfi = false;
            }else if(v.getId() == R.id.disableGoogleAccount){
                disableGoogleAccount = true;
            }else if(v.getId() == R.id.enableGoogleAccount){
                disableGoogleAccount = false;
            }
            else if(v.getId() == R.id.disableScreenCapture){
                disableScreenCapture = true;
            }
            else if(v.getId() == R.id.enableScreenCapture){
                disableScreenCapture = false;
            }

            try {
                if (mDeviceRestrictionManager != null) {
                    mDeviceRestrictionManager.setWifiDisabled(mAdminName, disableWfi);
//                    mDeviceRestrictionManager.setGoogleAccountDisabled(mAdminName, disableGoogleAccount);
//                    mDeviceRestrictionManager.setScreenCaptureDisabled(mAdminName, disableScreenCapture);
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
            updateState();
        }
    }
}
