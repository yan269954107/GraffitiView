package com.yanxw.graffiti.newversion;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.view.MotionEvent;

import java.util.List;

/**
 * AnnotationUtils
 * Created by yanxinwei on 2019-06-28.
 */
public class AnnotationUtils {

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

//    public static int getAngle(PointF point1, PointF point2) {
//        float x1 = point1.x, x2 = point2.x;
//        float y1 = point1.y, y2 = point2.y;
//        float x = Math.abs(x1 - x2);
//        float y = Math.abs(y1 - y2);
//        double z = Math.sqrt(x * x + y * y);
//        return (int) Math.round((Math.asin(y / z) / Math.PI * 180));
//    }

    public static float getDegrees(MotionEvent event) {
        float delta_x = (event.getX(0) - event.getX(1));
        float delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    public static int getMaxWordsLine(List<List<Bitmap>> lineWords) {
        int max = 0;
        for (List<Bitmap> bitmaps : lineWords) {
            if (bitmaps.size() > max) {
                max = bitmaps.size();
            }
        }
        return max;
    }

}
