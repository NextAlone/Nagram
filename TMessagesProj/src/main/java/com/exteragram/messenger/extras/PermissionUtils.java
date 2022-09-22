/*

 This is the source code of exteraGram for Android.

 We do not and cannot prevent the use of our code,
 but be respectful and credit the original author.

 Copyright @immat0x1, 2022.

*/

package com.exteragram.messenger.extras;

import androidx.annotation.RequiresApi;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.ui.BasePermissionsActivity;

public class PermissionUtils {

    @RequiresApi(api = 23)
    public static boolean isPermissionGranted(String perm) {
        return ApplicationLoader.applicationContext.checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED;
    }
    @RequiresApi(api = 23)
    public static void requestPermissions(Activity activity, int code, String... perms) {
        if (activity == null) return;
        activity.requestPermissions(perms, code);
    }

    public static boolean isVideoPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 33) return isPermissionGranted(Manifest.permission.READ_MEDIA_VIDEO);
        else return isStoragePermissionGranted();
    }

    public static boolean isImagesAndVideoPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 33) return isImagesPermissionGranted() && isVideoPermissionGranted();
        else return isStoragePermissionGranted();
    }
    @RequiresApi(api = 23)
    public static void requestImagesAndVideoPermission(Activity activity) {
        requestImagesAndVideoPermission(activity, BasePermissionsActivity.REQUEST_CODE_EXTERNAL_STORAGE);
    }
    @RequiresApi(api = 23)
    public static void requestImagesAndVideoPermission(Activity activity, int code) {
        if (Build.VERSION.SDK_INT >= 33) requestPermissions(activity, code, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO);
        else requestPermissions(activity, code, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public static boolean isImagesPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 33) return isPermissionGranted(Manifest.permission.READ_MEDIA_IMAGES);
        else return isStoragePermissionGranted();
    }
    @RequiresApi(api = 23)
    public static void requestImagesPermission(Activity activity) {
        requestImagesPermission(activity, BasePermissionsActivity.REQUEST_CODE_EXTERNAL_STORAGE);
    }
    @RequiresApi(api = 23)
    public static void requestImagesPermission(Activity activity, int code) {
        if (Build.VERSION.SDK_INT >= 33) requestPermissions(activity, code, Manifest.permission.READ_MEDIA_IMAGES);
        else requestPermissions(activity, code, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public static boolean isAudioPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 33) return isPermissionGranted(Manifest.permission.READ_MEDIA_AUDIO);
        else return isStoragePermissionGranted();
    }
    @RequiresApi(api = 23)
    public static void requestAudioPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 33) requestPermissions(activity, BasePermissionsActivity.REQUEST_CODE_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_AUDIO);
        else requestPermissions(activity, BasePermissionsActivity.REQUEST_CODE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public static boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (Build.VERSION.SDK_INT >= 33) return isImagesPermissionGranted() && isVideoPermissionGranted() && isAudioPermissionGranted();
            else return isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE);
        } else return true;
    }
    @RequiresApi(api = 23)
    public static void requestStoragePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 33) requestPermissions(activity, BasePermissionsActivity.REQUEST_CODE_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO);
        else requestPermissions(activity, BasePermissionsActivity.REQUEST_CODE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }
}