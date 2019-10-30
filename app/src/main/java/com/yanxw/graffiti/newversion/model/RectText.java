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
    private RectF mSavedRect;
    private String mSavedText;
    private boolean isClean = false;

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

    public RectText(RectF rect, String text, boolean isEdit) {
        mRect = rect;
        mText = text;
        this.isEdit = isEdit;
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

    public RectF getSavedRect() {
        return mSavedRect;
    }

    public void setSavedRect(RectF savedRect) {
        mSavedRect = savedRect;
    }

    public String getSavedText() {
        return mSavedText;
    }

    public void setSavedText(String savedText) {
        mSavedText = savedText;
    }

    public void setSaved() {
        mSavedRect = new RectF(mRect);
        mSavedText = mText;
    }

    public boolean isClean() {
        return isClean;
    }

    public void setClean(boolean clean) {
        isClean = clean;
    }

    public boolean resetSaved() {
        if (mSavedRect == null || mSavedText == null) return true;
        if (!mSavedRect.equals(mRect)) {
            mRect = new RectF(mSavedRect);
        }
        if (!mSavedText.equals(mText)) {
            mText = mSavedText;
        }
        return false;
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

    public void processOffset(float offsetX, float offsetY) {
        isEdit = false;
        mRect.left = mRect.left - offsetX;
        mRect.right = mRect.right - offsetX;
        mRect.top = mRect.top - offsetY;
        mRect.bottom = mRect.bottom - offsetY;
    }
}
