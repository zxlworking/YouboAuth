package cn.iubo.youboauth.common;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.widget.Toast;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Created by zxl on 2018/11/14.
 */

public class GlobalCrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "GlobalCrashHandler";

    private static GlobalCrashHandler mGlobalCrashHandler;

    private Context mContext;

    private Handler mMainHandler;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    /** 存储设备信息和异常信息 **/
    private Map<String, String> mInfos = new HashMap<String, String>();

    private GlobalCrashHandler(Context context){
        mContext = context;
        mMainHandler = new Handler(Looper.getMainLooper());
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static GlobalCrashHandler getInstance(Context context){
        if(null == mGlobalCrashHandler){
            synchronized (GlobalCrashHandler.class){
                if(null == mGlobalCrashHandler){
                    synchronized (GlobalCrashHandler.class){
                        mGlobalCrashHandler = new GlobalCrashHandler(context);
                    }
                }
            }
        }
        return mGlobalCrashHandler;
    }

    private void showToast(final String msg){
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext,msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (mDefaultHandler != null && !handlerException(ex)) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            // 程序休眠1s后退出
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Process.killProcess(Process.myPid());
            System.exit(1);
        }

    }

    /**
     * 5、处理异常<br>
     * <br>
     *
     * 5.1 收集设备参数信息<br>
     * 5.2 弹出窗口提示信息<br>
     * 5.3 保存log和crash到文件<br>
     * 5.4 发送log和crash到服务器<br>
     *
     * @param ex
     * @return 是否处理了异常
     */
    protected boolean handlerException(Throwable ex) {
        if (ex == null) {
            return false;
        } else {

            // 5.1 收集设备参数信息
            collectDeviceInfo(mContext);

            // 5.2 弹出窗口提示信息
            new Thread(new Runnable() {
                public void run() {
                    Looper.prepare();
                    Toast.makeText(mContext, "很抱歉，程序出小差了呢~~", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }).start();

            // 5.3 保存log和crash到文件
            saveLogAndCrash(ex);
            // 5.4 发送log和crash到服务器
//            sendLogAndCrash();

            return true;
        }

    }

    /**
     * 5.1 收集设备信息
     *
     * @param ctx
     */
    protected void collectDeviceInfo(Context ctx) {
        mInfos.put("versionName", CommonUtils.getVersionName(ctx));
        mInfos.put("versionCode", String.valueOf(CommonUtils.getVersionCode(ctx)));
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                mInfos.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 5.3 保存log和crash到文件
     *
     * @param ex
     */
    protected void saveLogAndCrash(Throwable ex) {
        StringBuffer sb = new StringBuffer();

        sb.append("[DateTime: " + System.currentTimeMillis() + "]\n");
        sb.append("[DeviceInfo: ]\n");
        // 遍历infos
        for (Map.Entry<String, String> entry : mInfos.entrySet()) {
            String key = entry.getKey().toLowerCase(Locale.getDefault());
            String value = entry.getValue();
            sb.append("  " + key + ": " + value + "\n");
        }
        // 将错误写到writer中
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        pw.close();
        String result = writer.toString();
        sb.append("[Excetpion: ]\n");
        sb.append(result);

        // 5.3.1 记录异常到特定文件中
        saveToCrashFile(sb.toString());

    }

    /**
     * 5.3.1写入文本
     *
     * @param crashText
     */
    protected void saveToCrashFile(String crashText) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String date = sf.format(new Date());

        // 创建文件（自己写的操作文件相关的工具类）
        File crashFile = FileUtils.createFileAndFolder(date + "_ " + mContext.getPackageName() + ".txt", Constants.APP_CRASH_PATH);

        // 追加文本（自己写的操作文件相关的工具类）
        FileUtils.appendToFile(crashFile, crashText);

    }
}
