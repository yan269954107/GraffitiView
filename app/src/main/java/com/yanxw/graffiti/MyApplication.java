package com.yanxw.graffiti;

import android.app.Application;
import android.content.Context;

/**
 * MyApplication
 * Created by yanxinwei on 2019-07-12.
 */
public class MyApplication extends Application {

    private static Application mApplication;

    public static Context getInstance() {
        return mApplication;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mApplication = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
