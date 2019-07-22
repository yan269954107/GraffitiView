package com.yanxw.graffiti.newversion;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.yanxw.graffiti.MyApplication;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Tools
 * Created by yanxinwei on 2019-06-08.
 */
public class Tools {

    public static int dp2Px(float dp) {
        return dp2Px(MyApplication.getInstance(), dp);
    }

    public static int dp2Px(Context context, float dp) {
        return Math.round(context.getResources().getDisplayMetrics().density * dp);
    }

    public static int getCeilInt(float f) {
        return (int) Math.ceil(f);
    }

    public static void showSoftKeyBoard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    public static void hideKeyBoard(Activity context) {
        View view = context.getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
