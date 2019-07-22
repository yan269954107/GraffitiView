package com.yanxw.graffiti.newversion;

import android.view.MotionEvent;

/**
 * MarkListener
 * Created by yanxinwei on 2019-07-12.
 */
public interface MarkListener {

    void onDrawDown(MotionEvent event);

    void onTextDown(MotionEvent event);

    void showKeyboard(String text);

    void formatText(String text);

}
