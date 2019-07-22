package com.yanxw.graffiti;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by xiaoxu.yxx on 2015/7/20.
 */
public final class Handlers {
    public final static Handler MAIN = new Handler(Looper.getMainLooper());

    public static void postMain(Runnable runnable) {
        MAIN.post(runnable);
    }

    public static void postDelayed(Runnable runnable, long delayMillis) {
        MAIN.postDelayed(runnable, delayMillis);
    }

}
