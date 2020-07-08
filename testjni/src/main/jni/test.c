//
// Created by zxl on 7/7/20.
//

#include "cn_iubo_youboauth_testjni_JniUtils.h"

JNIEXPORT jstring JNICALL Java_cn_iubo_youboauth_testjni_JniUtils_getJniString(JNIEnv *env, jclass jc)
{
    return (*env)->NewStringUTF(env,"this is the first time for me to use jni---2");
}