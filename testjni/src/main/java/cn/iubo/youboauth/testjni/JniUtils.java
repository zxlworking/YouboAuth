package cn.iubo.youboauth.testjni;

public class JniUtils {
    static {
        System.loadLibrary("JNISample");
    }

    public static native String getJniString();
}
