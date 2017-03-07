package com.yanxw.graffiti;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * GraffitiPen
 * Created by yanxinwei on 2017/3/3.
 */

public class GraffitiPen {

    private static final int DEFAULT_SIZE = 20;

    public static final int COLOR_RED = Color.parseColor("#fb4455");

    private int mColor;
    private Paint mPaint;

    public GraffitiPen(int color) {
        mColor = color;
        mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(DEFAULT_SIZE);
        mPaint.setColor(color);

        mPaint.setAntiAlias(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public Paint getPaint() {
        return mPaint;
    }
}
