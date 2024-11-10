#include "meth.h"

extern "C" {
jobject getApplication(JNIEnv *env) {
    jobject application = nullptr;
    jclass activity_thread_clz = env->FindClass("android/app/ActivityThread");
    if (activity_thread_clz != nullptr) {
        jmethodID currentApplication = env->GetStaticMethodID(
                activity_thread_clz, "currentApplication", "()Landroid/app/Application;");
        if (currentApplication != nullptr) {
            application = env->CallStaticObjectMethod(activity_thread_clz, currentApplication);
        } else {
            LOGE("Cannot find method: currentApplication() in ActivityThread.");
        }
        env->DeleteLocalRef(activity_thread_clz);
    } else {
        LOGE("Cannot find class: android.app.ActivityThread");
    }

    return application;
}

const char *getApkPath(JNIEnv *env, jobject context) {
    jclass contextClass = env->GetObjectClass(context);
    jmethodID getPackageResourcePathMethod = env->GetMethodID(contextClass,
                                                              "getPackageResourcePath",
                                                              "()Ljava/lang/String;");
    auto packageResourcePath = (jstring) env->CallObjectMethod(context,
                                                                  getPackageResourcePathMethod);

    const char *resourcePath = env->GetStringUTFChars(packageResourcePath, nullptr);

    env->DeleteLocalRef(contextClass);
    env->DeleteLocalRef(packageResourcePath);
    return resourcePath;
}
}
