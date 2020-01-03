package cn.iubo.youboauth.common;

import android.os.Environment;

/**
 * Created by zxl on 2018/9/6.
 */

public class Constants {

    public static final String ROOT_DIR_PATH = Environment.getExternalStorageDirectory().getPath();
    public static final String APP_DIR_PATH = ROOT_DIR_PATH + "/" + "cn.iubo.youboauth";
    public static final String APP_PICTURE_PATH = APP_DIR_PATH + "/" + "picture";
    public static final String APP_CRASH_PATH = APP_DIR_PATH + "/" + "crash";
    public static final String APP_LRC_PATH = APP_DIR_PATH + "/" + "lrc";

}
