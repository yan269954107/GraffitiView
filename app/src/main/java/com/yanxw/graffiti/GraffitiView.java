package com.yanxw.graffiti;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import static com.yanxw.graffiti.Params.TOUCH_MODE_DOUBLE;
import static com.yanxw.graffiti.Params.TOUCH_MODE_NULL;
import static com.yanxw.graffiti.Params.TOUCH_MODE_SINGLE;

/**
 * GraffitiView
 * Created by yanxinwei on 2017/3/2.
 */

public class GraffitiView extends View {

    private Context mContext;

    private Bitmap mPicBitmap;  //用于绘制照片的bitmap

    private String mPicPath;

    private int mResId;

    private Paint mPaint;

    private Graffiti mGraffiti;
    private GraffitiCoordinate mCoordinate;

    private int mTouchMode = 0;

    public GraffitiView(Context context, String picPath) {
        super(context);
        mPicPath = picPath;
        init(context);
    }

    public GraffitiView(Context context, int resId) {
        super(context);
        mResId = resId;
        init(context);
    }

    public GraffitiView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void init(Context context) {
        mContext = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mGraffiti = new Graffiti();
        mCoordinate = new GraffitiCoordinate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mPicPath != null) {
            mPicBitmap = ImageUtils.decodeBitmap(mPicPath, w, h);
        } else if (mResId != 0) {
            mPicBitmap = ImageUtils.decodeBitmap(mContext, mResId, w, h);
        } else {
            throw new IllegalArgumentException("mPicPath and mResId at least one is not empty");
        }
        mGraffiti.init(mPicBitmap);

        int picWidth = mPicBitmap.getWidth();
        int picHeight = mPicBitmap.getHeight();

        mCoordinate.initParams(picWidth, picHeight, w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Params.DrawParams drawParams = mCoordinate.getCanvasScale();
        float scale = drawParams.scale;

        canvas.scale(scale, scale);

        mGraffiti.draw();
        canvas.drawBitmap(mGraffiti.getGraffitiBitmap(), drawParams.left, drawParams.top, mPaint);
        Log.d("tag", "@@@@ onDraw scale : " + scale + " left ：" + drawParams.left + " top : " + drawParams.top);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //这里的switch 一定要写成event.getAction() & MotionEvent.ACTION_MASK
        //举例ACTION_POINTER_DOWN事件的action为261，不按位与的话识别不出来
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Log.d("tag", "@@@@ ACTION_DOWN");
                mTouchMode = TOUCH_MODE_SINGLE;
                actionDown(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.d("tag", "@@@@ ACTION_MOVE");
                if (mTouchMode == TOUCH_MODE_SINGLE) {
                    drawGraffiti(event.getX(), event.getY());
                } else if (mTouchMode == TOUCH_MODE_DOUBLE) {
                    mCoordinate.processPointerMove(event);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.d("tag", "@@@@ ACTION_UP");
                mTouchMode = TOUCH_MODE_NULL;
                actionUp();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d("tag", "@@@@ ACTION_POINTER_DOWN event.getAction() : " + event.getAction());
                mTouchMode++;
                if (mTouchMode == TOUCH_MODE_DOUBLE) {
                    mCoordinate.processPointerDown(event);
                    mGraffiti.undo(mPicBitmap);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.d("tag", "@@@@ ACTION_POINTER_UP");
                mTouchMode--;
                if (mTouchMode == TOUCH_MODE_SINGLE) {
//                    mGraffiti.undo(mPicBitmap);
                }
                break;
        }
        return true;
    }

    private void actionDown(float x, float y) {
        mCoordinate.processDown(x, y);

        mGraffiti.actionDown(mCoordinate.getMoveParams());
        mGraffiti.actionMove(mCoordinate.getQuadParams());
    }

    private void drawGraffiti(float x, float y) {
        mCoordinate.processMove(x, y);

        mGraffiti.actionMove(mCoordinate.getQuadParams());

        Log.d("tag", "@@@@ drawGraffiti");
        invalidate();
    }

    private void actionUp() {
        invalidate();
    }

    public void undo() {
        mGraffiti.undo(mPicBitmap);
        invalidate();
    }
}
