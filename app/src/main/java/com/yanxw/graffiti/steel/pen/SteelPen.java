package com.yanxw.graffiti.steel.pen;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.yanxw.graffiti.steel.config.ControllerPoint;


/**
 * 钢笔
 *
 * @author king
 * @since 2018/06/15
 */
public class SteelPen extends BasePen {

    @Override
    protected void doPreDraw(Canvas canvas, Paint paint, boolean needCalculate) {
        if (mHWPointCurrent.isEmpty()) {
//            Log.d("tag", "@@@@ mHWPointCurrent.isEmpty() : ");
            try {
                throw new NullPointerException();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        int startIndex = 0;
        if (mCurPoint == null) {
            mCurPoint = mHWPointCurrent.get(0);
            startIndex = 1;
        }
        for (int i = startIndex; i < mHWPointCurrent.size(); i++) {
            ControllerPoint point = mHWPointCurrent.get(i);
            if (paint == null) {
                drawToPoint(canvas, point, mPaint);
            } else {
                drawToPoint(canvas, point, paint);
            }
            mCurPoint = point;
            if (needCalculate) {
                compareBounds(point);
            }
        }
        mHWPointCurrent.clear();
    }

    @Override
    protected void doMove(double curDis) {
        int steps = 1 + (int) curDis / STEP_FACTOR;
        double step = 1.0 / steps;
        for (double t = 0; t < 1.0; t += step) {
            ControllerPoint point = mBezier.getPoint(t);
            if (!mHWPointList.isEmpty()) {
                ControllerPoint controllerPoint = mHWPointList.get(mHWPointList.size() - 1);
                if (point.equals(controllerPoint)) {
                    continue;
                }
            }
            mHWPointList.add(point);
            mHWPointCurrent.add(point);
        }
    }

    @Override
    protected void doDraw(Canvas canvas, ControllerPoint point, Paint paint) {
        drawLine(canvas, mCurPoint.x, mCurPoint.y, mCurPoint.width, point.x,
                point.y, point.width, paint);
    }

    /**
     * 绘制方法，实现笔锋效果
     */
    private void drawLine(Canvas canvas, double x0, double y0, double w0, double x1, double y1, double w1, Paint paint) {
        //求两个数字的平方根 x的平方+y的平方在开方记得X的平方+y的平方=1，这就是一个圆
        double curDis = Math.hypot(x0 - x1, y0 - y1);
        int steps;
        //绘制的笔的宽度是多少，绘制多少个椭圆
        if (paint.getStrokeWidth() < 6) {
            steps = 1 + (int) (curDis / 2);
        } else if (paint.getStrokeWidth() > 60) {
            steps = 1 + (int) (curDis / 4);
        } else {
            steps = 1 + (int) (curDis / 3);
        }
        double deltaX = (x1 - x0) / steps;
        double deltaY = (y1 - y0) / steps;
        double deltaW = (w1 - w0) / steps;
        double x = x0;
        double y = y0;
        double w = w0;

        for (int i = 0; i < steps; i++) {
            RectF oval = new RectF();
//            float top = (float) (y - w / 2.0f);
//            float left = (float) (x - w / 4.0f);
//            float right = (float) (x + w / 2.0f);
//            float bottom = (float) (y + w / 4.0f);
            float top = (float) (y - w * 2);
            float left = (float) (x - w);
            float right = (float) (x + w * 2);
            float bottom = (float) (y + w);
//            Log.d("tag", "@@@@ top:" + top + " left:" + left + " right:" + right + " bottom:" + bottom + " w:" + w + " paintWidth:" + paint.getStrokeWidth());
            oval.set(left, top, right, bottom);
            //最基本的实现，通过点控制线，绘制椭圆
            canvas.drawOval(oval, paint);
            x += deltaX;
            y += deltaY;
            w += deltaW;
        }
    }
}
