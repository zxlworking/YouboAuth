package cn.iubo.youboauth.common;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.zxl.common.DebugUtil;


public class CommonUtils {
    public static final String TAG = "CommonUtils";

    /** 判断是否是快速点击 */
    private static long lastClickTime;

    public static String getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static int px2dip(int pxValue){
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public static float dip2px(float dipValue){
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return  (dipValue * scale + 0.5f);
    }

    public static int screenWidth(){
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int screenHeight(){
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        DebugUtil.d(TAG,"isFastDoubleClick::timeD = " + timeD);
        if (0 < timeD && timeD < 500) {

            return true;
        }
        lastClickTime = time;
        return false;

    }

    public static final void hideIputKeyboard(Activity activity) {
        InputMethodManager mInputKeyBoard = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            mInputKeyBoard.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }
}
