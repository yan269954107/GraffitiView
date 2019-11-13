package com.yanxw.graffiti.newversion;

import android.graphics.PointF;
import android.util.Log;

import com.yanxw.graffiti.CommonUtils;

/**
 * ClickCheckItr
 * Created by yanxinwei on 2019-11-11.
 */
public abstract class ClickCheckItr {

    public static final int sClickRange = CommonUtils.dp2px(2);
    public static final long sClickTime = 300;

    protected PointF mLastPointF;
    protected long mDownTime;
    protected PointF mDownPointF;

    protected boolean checkClick(PointF pointF) {
        float moveX = Math.abs(mDownPointF.x - pointF.x);
        float moveY = Math.abs(mDownPointF.y - pointF.y);
        long time = System.currentTimeMillis() - mDownTime;
        Log.d("tag", "@@@@ moveX:" + moveX + " moveY:" + moveY + " time:" + time);
        return moveX < sClickRange && moveY < sClickRange && time < sClickTime;
    }
}
