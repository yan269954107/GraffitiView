package com.yanxw.graffiti.newversion.model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

/**
 * RectText
 * Created by yanxinwei on 2019-07-12.
 */
public class RectText {

    private RectF mRect;
    private String mText;
    private RectF mCloseRect;
    private boolean isEdit = true;

    public RectText() {
    }

    public RectText(RectF rect, String text) {
        mRect = rect;
        mText = text;
    }

    public RectText(RectF rect, String text, RectF closeRect) {
        mRect = rect;
        mText = text;
        mCloseRect = closeRect;
    }

    public RectF getCloseRect() {
        return mCloseRect;
    }

    public void setCloseRect(RectF closeRect) {
        mCloseRect = closeRect;
    }

    public RectF getRect() {
        return mRect;
    }

    public void setRect(RectF rect) {
        mRect = rect;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public PointF getCloseCenter(float radius) {
        return new PointF(mCloseRect.left + radius, mCloseRect.top + radius);
    }

    public void setCloseRect(float halfCloseWidth) {
        float closeLeft = mRect.right - halfCloseWidth;
        float closeRight = mRect.right + halfCloseWidth;
        float closeTop = mRect.top - halfCloseWidth;
        float closeBottom = mRect.top + halfCloseWidth;
        mCloseRect = new RectF(closeLeft, closeTop, closeRight, closeBottom);
    }

    public void drawClose(Canvas canvas, float closeRadius, Paint closeBgPaint, Paint closeLinePaint, float closePadding) {
        PointF centerPoint = getCloseCenter(closeRadius);
        canvas.drawCircle(centerPoint.x, centerPoint.y, closeRadius, closeBgPaint);
        float startX = mCloseRect.left + closePadding;
        float startY = mCloseRect.top + closePadding;
        float stopX = mCloseRect.right - closePadding;
        float stopY = mCloseRect.bottom - closePadding;
        canvas.drawLine(startX, startY, stopX, stopY, closeLinePaint);

        startX = mCloseRect.right - closePadding;
        stopX = mCloseRect.left + closePadding;
        canvas.drawLine(startX, startY, stopX, stopY, closeLinePaint);
    }
}
