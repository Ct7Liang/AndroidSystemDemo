package com.ct7liang.androidsystemapi;

import android.app.Application;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        CrashReport.initCrashReport(getApplicationContext(), "CrashReport.initCrashReport(getApplicationContext(), \"注册时申请的APPID\", false); ", false);
    }
}
