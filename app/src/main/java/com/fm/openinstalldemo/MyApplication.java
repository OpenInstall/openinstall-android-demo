package com.fm.openinstalldemo;

import android.app.Application;

import com.fm.openinstall.OpenInstall;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        OpenInstall.preInit(this);

    }
}
