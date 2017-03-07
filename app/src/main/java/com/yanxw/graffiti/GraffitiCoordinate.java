package com.yanxw.graffiti;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

/**
 * GraffitiCoordinate 涂鸦的坐标管理
 * Created by yanxinwei on 2017/3/6.
 */

public class GraffitiCoordinate {

    private float mCenterTop, mCenterLeft; //图片居中时左上角的坐标
    private float mCenterScale;  //画布居中时的比例

    private float mOffsetX = 0, mOffsetY = 0; // 偏移量，图片真实偏移量为　mCentreLeft + mOffsetX
    private float mScale = 1.0f; // 缩放倍数, 图片真实的缩放倍数为 mCenterScale * mScale

    private float mLastTouchX, mLastTouchY, mTouchX, mTouchY;  //触摸的坐标

    private float mToucheCentreXOnGraffiti, mToucheCentreYOnGraffiti;//两点触摸的时候换算成实际缩放比例后的中心点

    private float mDownDist;  //记录两指刚按下时的距离

    private boolean isFirstPointerMove = true;

    public GraffitiCoordinate() {
    }

    public void initParams(int picWidth, int picHeight, int viewWidth, int viewHeight) {
        float widthScale = picWidth * 1f / viewWidth;
        float heightScale = picHeight * 1f / viewHeight;

        int centerWidth, centerHeight;

        if (widthScale > heightScale) {
            mCenterScale = 1 / widthScale;
            centerWidth = viewWidth;
            centerHeight = (int) (picHeight * mCenterScale);
        } else {
            mCenterScale = 1 / heightScale;
            centerWidth = (int) (picWidth * mCenterScale);
            centerHeight = viewHeight;
        }

        mCenterLeft = (viewWidth - centerWidth) / 2f;
        mCenterTop = (viewHeight - centerHeight) / 2f;

        Log.d("tag", "@@@@ mCenterScale : " + mCenterScale + " mCenterLeft : " + mCenterLeft
                + " mCenterTop : " + mCenterTop);
    }

    public Params.DrawParams getCanvasScale() {
        float scale = mCenterScale * mScale;
        float left = (mCenterLeft + mOffsetX) / scale;
        float top = (mCenterTop + mOffsetY) / scale;
        Log.d("tag", "@@@@ getCanvasScale mCenterScale : " + mCenterScale + " mScale : " + mScale);
        return new Params.DrawParams(scale, left, top);
    }

    public void processDown(float x, float y) {
        mTouchX = mLastTouchX = x;
        mTouchY = mLastTouchY = y;

        //为了仅点击时也能出现绘图，模拟滑动一个像素点
        mTouchX++;
        mTouchY++;
    }

    public void processPointerDown(MotionEvent event) {
        PointF pointF = getMid(event);
        mDownDist = spacing(event);// 两点按下时的距离

        mToucheCentreXOnGraffiti = screenToBitmapX(pointF.x);
        mToucheCentreYOnGraffiti = screenToBitmapY(pointF.y);

//        Log.d("tag", "@@@@ processPointerDown mCenterScale : " + mCenterScale + " mScale : " + mScale);
//        mCenterScale = mCenterScale * mScale;
//        Log.d("tag", "@@@@ processPointerDown mCenterScale : " + mCenterScale + " mScale : " + mScale);
//
//        mCenterLeft = (mCenterLeft + mOffsetX) / mCenterScale;
//        mCenterTop = (mCenterTop + mOffsetY) / mCenterScale;

        Log.d("tag", "@@@@ processPointerDown mDownDist : " + mDownDist);

        isFirstPointerMove = true;
    }

    public void processPointerMove(MotionEvent event) {

        // 这里是一个权宜处理，因为单指的移动会触发重绘，而在双指按下事件里需要重新计算缩放比例，
        // 问题是最后一次单指移动触发的重绘是在双指按下事件里重新计算缩放比例之后，导致会有跳动
        // 所以先放到双指移动处理里面
        if (isFirstPointerMove) {
            isFirstPointerMove = false;
            Log.d("tag", "@@@@ processPointerDown mCenterScale : " + mCenterScale + " mScale : " + mScale);
            mCenterScale = mCenterScale * mScale;
            Log.d("tag", "@@@@ processPointerDown mCenterScale : " + mCenterScale + " mScale : " + mScale);

            mCenterLeft = (mCenterLeft + mOffsetX) / mCenterScale;
            mCenterTop = (mCenterTop + mOffsetY) / mCenterScale;
        }

        PointF ptf = getMid(event);
        float dist = spacing(event);// 两点按下时的距离

        Log.d("tag", "@@@@ processPointerMove mCenterScale : " + mCenterScale + " mScale : " + mScale);
        mScale = dist / mDownDist;
        Log.d("tag", "@@@@ processPointerMove mCenterScale : " + mCenterScale + " mScale : " + mScale);
        mOffsetX = toTransX(ptf.x, mToucheCentreXOnGraffiti);
        mOffsetY = toTransY(ptf.y, mToucheCentreYOnGraffiti);


    }

    public Params.PathMoveParams getMoveParams() {
        Params.PathMoveParams moveParams = new Params.PathMoveParams();
        moveParams.x = screenToBitmapX(mLastTouchX);
        moveParams.y = screenToBitmapY(mLastTouchY);
        return moveParams;
    }

    public Params.PathQuadParams getQuadParams() {
        Params.PathQuadParams quadParams = new Params.PathQuadParams();
        quadParams.x1 = screenToBitmapX(mLastTouchX);
        quadParams.y1 = screenToBitmapY(mLastTouchY);
        quadParams.x2 = screenToBitmapX((mTouchX + mLastTouchX) / 2);
        quadParams.y2 = screenToBitmapY((mTouchY + mLastTouchY) / 2);
        return quadParams;
    }

    public void processMove(float x, float y) {
        mLastTouchX = mTouchX;
        mLastTouchY = mTouchY;
        mTouchX = x;
        mTouchY = y;
    }

    /**
     * 将触摸的屏幕坐标转换成实际图片中的坐标
     */
    public float screenToBitmapX(float touchX) {
        return (touchX - mCenterLeft - mOffsetX) / (mCenterScale * mScale);
    }

    public float screenToBitmapY(float touchY) {
        return (touchY - mCenterTop - mOffsetY) / (mCenterScale * mScale);
    }

    /**
     * 计算两指间的距离
     *
     * @param event
     * @return
     */
    public float spacing(MotionEvent event) {
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
    public PointF getMid(MotionEvent event) {
        float midX = (event.getX(1) + event.getX(0)) / 2;
        float midY = (event.getY(1) + event.getY(0)) / 2;
        return new PointF(midX, midY);
    }

    //通过触点的坐标和实际图片中的坐标,得到当前图片的起始点坐标
    public final float toTransX(float touchX, float graffitiX) {
        return -graffitiX * (mCenterScale * mScale) + touchX - mCenterLeft;
    }

    public final float toTransY(float touchY, float graffitiY) {
        return -graffitiY * (mCenterScale * mScale) + touchY - mCenterTop;
    }
}
