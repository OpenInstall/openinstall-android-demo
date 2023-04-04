package com.fm.openinstalldemo.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class Tools {

    public static String getAppKey(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle metaData = applicationInfo.metaData;
            if (null != metaData) {
                return metaData.getString("com.openinstall.APP_KEY");
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        return null;
    }

}
