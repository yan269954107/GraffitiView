package com.yanxw.graffiti;

import android.util.Log;
import android.view.MotionEvent;

/**
 * GraffitiGesture
 * Created by yanxinwei on 2017/3/6.
 */

public class GraffitiGesture {

    private GraffitiView mGraffitiView;

    public GraffitiGesture(GraffitiView graffitiView) {
        mGraffitiView = graffitiView;
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("tag", "@@@@ ACTION_DOWN x : " + event.getX() + " y : " + event.getY());
//                actionDown(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("tag", "@@@@ ACTION_MOVE x : " + event.getX() + " y : " + event.getY());
//                actionMove(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.d("tag", "@@@@ ACTION_UP x : " + event.getX() + " y : " + event.getY());
//                actionUp();
                break;
        }
        return true;
    }
}
