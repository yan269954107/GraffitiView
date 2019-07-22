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
    private float mOffsetTop, mOffsetLeft;
    private float mTranslateX, mTranslateY;
    private float mMaxTranslateX, mMinTranslateX, mMaxTranslateY, mMinTranslateY;
    private int mWidth, mHeight;
    private float mCenterX, mCenterY;

    //记录两指刚按下时的距离
    private float mDownDistance, mLastDistance;
    //mDownPointF:双指按下时双指中间点的坐标， mLastPointF:双指移动后的最后一个点,用于计算平移的距离
    private PointF mDownPointF, mLastPointF;

    public ConvertXY(float initTop, float initLeft, int viewWidth, int viewHeight) {
        mOffsetTop = initTop;
        mOffsetLeft = initLeft;
        mWidth = viewWidth;
        mHeight = viewHeight;
        mCenterX = (float) mWidth / 2;
        mCenterY = (float) mHeight / 2;
    }

    /**
     * 1：canvas.scale(mConvertXY.getScale(), mConvertXY.getScale(), centerX, centerY);
     * 2：canvas.translate(mConvertXY.getTranslateX(), mConvertXY.getTranslateY());
     * 3：canvas.drawBitmap(mPicBitmap, mConvertXY.getOffsetLeft(), mConvertXY.getOffsetTop(), null);
     * 以上是绘制的顺序，1和2都会带来canvas的translate，以下方法就是将canvas上的坐标对应到bitmap上的坐标
     */
    public PointF convert2BitmapXY(float x, float y) {
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

    /**
     * 将点击坐标转换成最外层View的坐标
     * 因为做外层view的canvas会做translate 和 scale操作，该方法就是找到当前点击坐标和原始状态的坐标对应关系
     */
    public PointF convert2ViewXY(float x, float y) {
        float scaleTranslateX = 0;
        float scaleTranslateY = 0;
        if (mDownPointF != null) {
            scaleTranslateX = mDownPointF.x - mDownPointF.x * mScale;
            scaleTranslateY = mDownPointF.y - mDownPointF.y * mScale;
        }
        float targetX = (x - mTranslateX * mScale - scaleTranslateX) / mScale;
        float targetY = (y - mTranslateY * mScale - scaleTranslateY) / mScale;
        return new PointF(targetX, targetY);
    }

    public PointF getViewCanvasCenter() {
        PointF pointF = convert2ViewXY(mCenterX, mCenterY / 3 * 2);
//        Log.d("tag", "@@@@ getViewCanvasCenter pointF:" + pointF);
        return pointF;
    }

    public float getOffsetTop() {
        return mOffsetTop;
    }

    public float getOffsetLeft() {
        return mOffsetLeft;
    }

    public float getScale() {
        return mScale;
    }

    public float getTranslateX() {
        return mTranslateX;
    }

    public float getTranslateY() {
        return mTranslateY;
    }

    public PointF getDownPointF() {
        return mDownPointF;
    }

    public float getCenterX() {
        return mCenterX;
    }

    public float getCenterY() {
        return mCenterY;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
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
