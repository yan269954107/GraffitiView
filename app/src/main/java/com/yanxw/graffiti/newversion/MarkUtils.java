package com.yanxw.graffiti.newversion;

import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

/**
 * MarkUtils
 * Created by yanxinwei on 2019-06-11.
 */
public class MarkUtils {

    /**
     * 计算两指间的距离
     *
     * @param event
     * @return
     */
    public static float getPointDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);

    }

    /**
     * 取两指的中心点坐标
     *
     * @param event
     * @return
     */
    public static PointF getMidPoint(MotionEvent event) {
        float midX = (event.getX(1) + event.getX(0)) / 2;
        float midY = (event.getY(1) + event.getY(0)) / 2;
        return new PointF(midX, midY);
    }

    /**
     * 将rectF的左上角归为0
     * @param rectF
     */
    public static void moveRectToZero(RectF rectF) {
        rectF.left = 0;
        rectF.top = 0;
        rectF.bottom = rectF.bottom - rectF.top;
        rectF.right = rectF.right - rectF.left;
    }

}
