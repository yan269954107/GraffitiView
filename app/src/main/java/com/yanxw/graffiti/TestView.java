package com.yanxw.graffiti;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * TestView
 * Created by yanxinwei on 2019-06-17.
 */
public class TestView extends View {

    private Paint mPaint;
    private float mScale = 1;
    private float mTranslateX = 0, mTranslateY = 0, mCenterX = 0, mCenterY = 0;
    private Bitmap mBitmap;
    private Canvas mBitmapCanvas;

    public TestView(Context context) {
        this(context, null);
    }

    public TestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("tag", "@@@@ TestView onSizeChanged w:" + w + " h:" + h + " oldw:" + oldw + " oldh:" + oldh);
        if (w == 0 || h == 0) return;
        mBitmap = Bitmap.createBitmap(w, h - 200, Bitmap.Config.ARGB_8888);
        mBitmapCanvas = new Canvas(mBitmap);
        mPaint.setColor(Color.BLUE);
        mBitmapCanvas.drawRect(0, 0, 100, 100, mPaint);
        mPaint.setColor(Color.YELLOW);
        mBitmapCanvas.drawRect(100, 0, 200, 100, mPaint);
        mPaint.setColor(Color.RED);
        mBitmapCanvas.drawRect(0, 100, 100, 200, mPaint);
        mPaint.setColor(Color.GREEN);
        mBitmapCanvas.drawRect(100, 100, 200, 200, mPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.scale(mScale, mScale, mCenterX, mCenterY);
        canvas.translate(mTranslateX, mTranslateY);
        canvas.drawBitmap(mBitmap, 0, 100, null);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setParams(float scale, float centerX, float centerY, float translateX, float translateY) {
        mScale = scale;
        mCenterX = centerX;
        mCenterY = centerY;
        mTranslateX = translateX;
        mTranslateY = translateY;
        postInvalidate();
    }
}
