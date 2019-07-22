package com.yanxw.graffiti.steel.config;


import android.graphics.PointF;

/***
 * 每个点的控制，关心三个因素：笔的宽度，坐标,透明数值
 *
 * @since 2018/06/15
 * @author king
 */
public class ControllerPoint {
    public float x;
    public float y;

    public float width;
    public int alpha = 255;
    private PointF mPointF;

    public ControllerPoint() {
        mPointF = new PointF();
    }

    public ControllerPoint(float x, float y) {
        this.x = x;
        this.y = y;
        mPointF = new PointF(x, y);
    }


    public void set(float x, float y, float w) {
        this.x = x;
        this.y = y;
        this.width = w;

        mPointF.x = x;
        mPointF.y = y;
    }


    public void set(ControllerPoint point) {
        this.x = point.x;
        this.y = point.y;
        this.width = point.width;

        mPointF.x = point.x;
        mPointF.y = point.y;
    }

    public PointF getPoint() {
        return mPointF;
    }
}
