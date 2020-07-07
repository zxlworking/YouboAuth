package cn.iubo.youboauth.testjni;

public class JniUtils {
    static {
        System.loadLibrary("test");
    }

    public static native String getJniString();
}
