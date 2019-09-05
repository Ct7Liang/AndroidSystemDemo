package com.ct7liang.androidsystemapi;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        CrashReport.initCrashReport(getApplicationContext(), "04caab8e14", true);
    }
}
