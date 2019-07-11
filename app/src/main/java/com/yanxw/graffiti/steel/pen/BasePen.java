package com.yanxw.graffiti.steel.pen;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

import com.yanxw.graffiti.newversion.ConvertXY;
import com.yanxw.graffiti.steel.config.ControllerPoint;
import com.yanxw.graffiti.steel.config.PenConfig;
import com.yanxw.graffiti.steel.util.Bezier;

import java.util.ArrayList;
import java.util.Stack;

/**
 * 画笔基类
 *
 * @author king
 * @since 2018/06/15
 */
public abstract class BasePen {
    /**
     * 绘制计算的次数，数值越小计算的次数越多
     */
    public static final int STEP_FACTOR = 20;
    protected Stack<ArrayList<ControllerPoint>> mHWPointsStack = new Stack<>();
    protected ArrayList<ControllerPoint> mHWPointList = new ArrayList<>();
    protected ArrayList<ControllerPoint> mHWPointCurrent = new ArrayList<>();
    protected ControllerPoint mLastPoint = new ControllerPoint(0, 0);
    protected Paint mPaint;

    /**
     * 笔的宽度信息
     */
    private double mBaseWidth;

    private double mLastVel;
    private double mLastWidth;

    protected Bezier mBezier = new Bezier();

    protected ControllerPoint mCurPoint;

    private ConvertXY mConvertXY;

    public void setPaint(Paint paint) {
        mPaint = paint;
        mBaseWidth = paint.getStrokeWidth();
    }

    public void setConvertXY(ConvertXY convertXY) {
        mConvertXY = convertXY;
    }

    public void draw(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        //点的集合少 不去绘制
        if (mHWPointCurrent == null || mHWPointCurrent.size() < 1) {
            return;
        }
        doPreDraw(canvas);
    }

    private int lastId = 0;//记录最先/最后的手指id

    public boolean onTouchEvent(MotionEvent event, Canvas canvas) {
        // event会被下一次事件重用，这里必须生成新的，否则会有问题
        int action = event.getAction() & event.getActionMasked();
        MotionEvent event2 = MotionEvent.obtain(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
//                Log.d("tag", "@@@@ ACTION_DOWN");
                lastId = event2.getPointerId(0);
                onDown(event2, canvas);
                return true;
            case MotionEvent.ACTION_MOVE:
//                Log.d("tag", "@@@@ ACTION_MOVE");
                if (lastId != event2.getPointerId(event2.getActionIndex())) {
                    return true;
                }
                onMove(event2, canvas);
                return true;
            case MotionEvent.ACTION_UP:
//                Log.d("tag", "@@@@ ACTION_UP");
                lastId = event2.getPointerId(0);
                onUp(event2, canvas);
                return true;
            default:
                break;
        }
        return false;
    }

    /**
     * 按下的事件
     */
    public void onDown(MotionEvent event, Canvas canvas) {
        if (mPaint == null) {
            throw new NullPointerException("paint不能为null");
        }
        mHWPointList = new ArrayList<>();
//        mHWPointsStack.add(mHWPointList);
        ControllerPoint curPoint = getConvertPoint(event);
        //记录down的控制点的信息
        mLastWidth = 0.7 * mBaseWidth;
        //down下的点的宽度
        curPoint.width = (float) mLastWidth;
        mLastVel = 0;
        //记录当前的点
        mLastPoint = curPoint;
        mCurPoint = null;
    }

    /**
     * 手指移动的事件
     */
    public void onMove(MotionEvent event, Canvas canvas) {
        ControllerPoint curPoint = getConvertPoint(event);
        double deltaX = curPoint.x - mLastPoint.x;
        double deltaY = curPoint.y - mLastPoint.y;
        //deltaX和deltay平方和的二次方根 想象一个例子 1+1的平方根为1.4 （x²+y²）开根号
        //同理，当滑动的越快的话，deltaX+deltaY的值越大，这个越大的话，curDis也越大
        double curDis = Math.hypot(deltaX, deltaY);
        //我们求出的这个值越小，画的点或者是绘制椭圆形越多，这个值越大的话，绘制的越少，笔就越细，宽度越小
        double curVel = curDis * PenConfig.DIS_VEL_CAL_FACTOR;
        double curWidth;
        //点的集合少，我们得必须改变宽度,每次点击的down的时候，这个事件
        if (mHWPointList.size() < 2) {

            curWidth = calcNewWidth(curVel, mLastVel, curDis, 1.7,
                    mLastWidth);
            curPoint.width = (float) curWidth;
            mBezier.init(mLastPoint, curPoint);
        } else {
            mLastVel = curVel;
            curWidth = calcNewWidth(curVel, mLastVel, curDis, 1.7,
                    mLastWidth);
            curPoint.width = (float) curWidth;
            mBezier.addNode(curPoint);
        }
        //每次移动的话，这里赋值新的值
        mLastWidth = curWidth;
        doMove(curDis);
        mLastPoint = curPoint;
    }

    private ControllerPoint getConvertPoint(MotionEvent event) {
        ControllerPoint curPoint;
        if (mConvertXY == null) {
            curPoint = new ControllerPoint(event.getX(), event.getY());
        } else {
            PointF pointF = mConvertXY.convert2BitmapXY(event.getX(), event.getY());
            curPoint = new ControllerPoint(pointF.x, pointF.y);
        }
        return curPoint;
    }

    /**
     * 手指抬起来的事件
     */
    public void onUp(MotionEvent mElement, Canvas canvas) {
        if (mHWPointList.size() == 0) {
            return;
        }
        mHWPointsStack.add(mHWPointList);
//        mCurPoint = new ControllerPoint(mElement.getX(), mElement.getY());
//        double deltaX = mCurPoint.x - mLastPoint.x;
//        double deltaY = mCurPoint.y - mLastPoint.y;
//        double curDis = Math.hypot(deltaX, deltaY);
//        mCurPoint.width = 0;
//
//        mBezier.addNode(mCurPoint);
//
//        int steps = 1 + (int) curDis / STEP_FACTOR;
//        double step = 1.0 / steps;
//        for (double t = 0; t < 1.0; t += step) {
//            ControllerPoint point = mBezier.getPoint(t);
//            mHWPointList.add(point);
//        }
//        mBezier.end();
//        for (double t = 0; t < 1.0; t += step) {
//            ControllerPoint point = mBezier.getPoint(t);
//            mHWPointList.add(point);
//        }
//        draw(canvas);
//        clear();
    }

    /**
     * 计算新的宽度信息
     */
    public double calcNewWidth(double curVel, double lastVel, double curDis,
                               double factor, double lastWidth) {
        double calVel = curVel * 0.6 + lastVel * (1 - 0.6);
        double vfac = Math.log(factor * 2.0f) * (-calVel);
        double calWidth = mBaseWidth * Math.exp(vfac);
        return calWidth;
    }

    /**
     * 清除缓存的触摸点
     */
    public void clear() {
        mHWPointList.clear();
    }

    /**
     * 绘制
     * 当现在的点和触摸点的位置在一起的时候不用去绘制
     */
    protected void drawToPoint(Canvas canvas, ControllerPoint point, Paint paint) {
        if ((mCurPoint.x == point.x) && (mCurPoint.y == point.y)) {
            return;
        }
        doDraw(canvas, point, paint);
    }


    /**
     * 判断笔是否为空
     */
    public boolean isNullPaint() {
        return mPaint == null;
    }

    /**
     * 移动的时候的处理方法
     */
    protected abstract void doMove(double f);

    /**
     * 绘制方法
     */
    protected abstract void doDraw(Canvas canvas, ControllerPoint point, Paint paint);

    /**
     * onDraw之前的操作
     */
    protected abstract void doPreDraw(Canvas canvas);

    public void undo(Canvas bitmapCanvas) {
        if (mHWPointsStack.size() > 0) {
            mHWPointsStack.pop();
        }
        mHWPointCurrent.clear();
        for (ArrayList<ControllerPoint> controllerPoints : mHWPointsStack) {
            mCurPoint = null;
            mHWPointCurrent.addAll(controllerPoints);
            Log.d("tag", "@@@@ mHWPointCurrent size:" + mHWPointCurrent.size());
            doPreDraw(bitmapCanvas);
        }
    }
}
