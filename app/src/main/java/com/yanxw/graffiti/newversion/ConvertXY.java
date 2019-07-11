package com.yanxw.graffiti.newversion;

import android.graphics.PointF;
import android.view.MotionEvent;

/**
 * ConvertXY
 * Created by yanxinwei on 2019-06-08.
 */
public class ConvertXY {

    private static final float MIN_SCALE = 1.0F;
    private static final float MAX_SCALE = 3.0F;
    private float mScale = 1.0f;
    private float mInitTop, mInitLeft, mOffsetTop, mOffsetLeft;
    private float mTranslateX, mTranslateY;
    private float mMaxTranslateX, mMinTranslateX, mMaxTranslateY, mMinTranslateY;
    private int mWidth, mHeight;
    private float mLastX, mLastY;

    //记录两指刚按下时的距离
    private float mDownDistance, mLastDistance;
    //mDownPointF:双指按下时双指中间点的坐标， mLastPointF:双指移动后的最后一个点,用于计算平移的距离
    private PointF mDownPointF, mLastPointF;

    public ConvertXY(float initTop, float initLeft, int imgWidth, int imgHeight) {
        mInitTop = mOffsetTop = initTop;
        mInitLeft = mOffsetLeft = initLeft;
        mWidth = imgWidth;
        mHeight = imgHeight;
    }

    public PointF convert2BitmapXY(float x, float y) {
        /**
         * 1：canvas.scale(mConvertXY.getScale(), mConvertXY.getScale(), centerX, centerY);
         * 2：canvas.translate(mConvertXY.getTranslateX(), mConvertXY.getTranslateY());
         * 3：canvas.drawBitmap(mPicBitmap, mConvertXY.getOffsetLeft(), mConvertXY.getOffsetTop(), null);
         * 以上是绘制的顺序，1和2都会带来canvas的translate，以下方法就是将canvas上的坐标对应到bitmap上的坐标
         */
        float scaleTranslateX = 0;
        float scaleTranslateY = 0;
        if (mDownPointF != null) {
            scaleTranslateX = mDownPointF.x - mDownPointF.x * mScale;
            scaleTranslateY = mDownPointF.y - mDownPointF.y * mScale;
        }
        float targetX = (x - mTranslateX * mScale - mOffsetLeft * mScale - scaleTranslateX) / mScale;
        float targetY = (y - mTranslateY * mScale - mOffsetTop * mScale - scaleTranslateY) / mScale;
        return new PointF(targetX, targetY);
    }

    public void setLastXY(float x, float y) {
        PointF pointF = convert2BitmapXY(x, y);
        mLastX = pointF.x;
        mLastY = pointF.y;
    }

    public void setLastXYInvalid() {
        mLastX = Float.MIN_VALUE;
        mLastY = Float.MIN_VALUE;
    }

    public boolean checkIsMove(PointF pointF) {
        if (mLastX == Float.MIN_VALUE || mLastY == Float.MIN_VALUE) return false;
        return pointF.x != mLastX || pointF.y != mLastY;
    }

    public float getOffsetTop() {
        return mOffsetTop;
    }

    public float getOffsetLeft() {
        return mOffsetLeft;
    }

    public float getLastX() {
        return mLastX;
    }

    public float getLastY() {
        return mLastY;
    }

    public float getScale() {
        return mScale;
    }

    public PointF getLastPointF() {
        return mLastPointF;
    }

    public void setLastPointF(PointF lastPointF) {
        mLastPointF = lastPointF;
    }

    public float getTranslateX() {
        return mTranslateX;
    }

    public void setTranslateX(float translateX) {
        mTranslateX = translateX;
    }

    public float getTranslateY() {
        return mTranslateY;
    }

    public void setTranslateY(float translateY) {
        mTranslateY = translateY;
    }

    public PointF getDownPointF() {
        return mDownPointF;
    }


    public void doublePointerDown(MotionEvent event) {
        PointF preDownPointF = mDownPointF;
        mLastPointF = mDownPointF = MarkUtils.getMidPoint(event);
        mLastDistance = mDownDistance = MarkUtils.getPointDistance(event);
//        Log.d("tag", "@@@@ distance down:" + mDownDistance);
        //消除两次不同中心点缩放translateXY带来的影响。（在上一次缩放的基础上继续本次缩放，如果不处理的话会造成视图跳动）
        //可查看canvas.scale(float sx, float sy, float px, float py)的实现
        if (preDownPointF != null) {
            float preTranslateX = preDownPointF.x - preDownPointF.x * mScale;
            float downTranslateX = mDownPointF.x - mDownPointF.x * mScale;
            mTranslateX += (preTranslateX - downTranslateX) / mScale;

            float preTranslateY = preDownPointF.y - preDownPointF.y * mScale;
            float downTranslateY = mDownPointF.y - mDownPointF.y * mScale;
            mTranslateY += (preTranslateY - downTranslateY) / mScale;
        }


    }

    public void doublePointerMove(MotionEvent event) {
        //计算缩放比例
        float distance = MarkUtils.getPointDistance(event);
        float diff = distance - mLastDistance;
        float diffScale = diff / mLastDistance;
        mScale = (1 + diffScale) * mScale;
        mLastDistance = distance;
        if (mScale > MAX_SCALE) mScale = MAX_SCALE;
        if (mScale < MIN_SCALE) mScale = MIN_SCALE;

        mMaxTranslateX = ((mDownPointF.x - mOffsetLeft) * mScale - mDownPointF.x) / mScale;
        if (mMaxTranslateX < 0) {
            mMaxTranslateX = 0;
        }
        //缩放中心点到View右侧边缘的距离
        float rightWidth = mWidth - mDownPointF.x;
        float canLeftMoveX = ((rightWidth - mOffsetLeft) * mScale - rightWidth) / mScale;
        if (canLeftMoveX < 0) {
            mMinTranslateX = 0;
        } else {
            mMinTranslateX = -canLeftMoveX;
        }

        mMaxTranslateY = ((mDownPointF.y - mOffsetTop) * mScale - mDownPointF.y) / mScale;
        if (mMaxTranslateY < 0) {
            mMaxTranslateY = 0;
        }
        //缩放中心点到View底部边缘的距离
        float bottomHeight = mHeight - mDownPointF.y;
        float canUpMoveY = ((bottomHeight - mOffsetTop) * mScale - bottomHeight) / mScale;
        if (canUpMoveY < 0) {
            mMinTranslateY = 0;
        } else {
            mMinTranslateY = -canUpMoveY;
        }

        //通过canvas的translate方法做移动
        PointF pointF = MarkUtils.getMidPoint(event);
        float moveX = pointF.x - mLastPointF.x;
        float moveY = pointF.y - mLastPointF.y;
        //抵消因为放大canvas而带来的移动过度（比如放大3倍，手指移动一像素，图就移动3像素）
        mTranslateX += moveX / mScale;
        if (mTranslateX > mMaxTranslateX) mTranslateX = mMaxTranslateX;
        if (mTranslateX < mMinTranslateX) mTranslateX = mMinTranslateX;


        mTranslateY += moveY / mScale;
        if (mTranslateY > mMaxTranslateY) mTranslateY = mMaxTranslateY;
        if (mTranslateY < mMinTranslateY) mTranslateY = mMinTranslateY;

        mLastPointF = pointF;
    }

}
