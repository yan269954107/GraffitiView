package com.yanxw.graffiti.newversion;

import android.content.Context;

/**
 * Tools
 * Created by yanxinwei on 2019-06-08.
 */
public class Tools {

    public static int dp2Px(Context context, float dp) {
        return Math.round(context.getResources().getDisplayMetrics().density * dp);
    }

    public static int getCeilInt(float f) {
        return (int) Math.ceil(f);
    }

}
