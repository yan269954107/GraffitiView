package com.yanxw.graffiti.newversion.model;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;

import com.yanxw.graffiti.newversion.AnnotationUtils;

import java.util.List;

import static com.yanxw.graffiti.newversion.writingview.HandWritingView.BITMAP_MARGIN;
import static com.yanxw.graffiti.newversion.writingview.HandWritingView.BITMAP_SIDE_LENGTH;
import static com.yanxw.graffiti.newversion.writingview.WritingView.INPUT_RECT_HEIGHT;

/**
 * WritingWords
 * Created by yanxinwei on 2019-11-08.
 */
public class WritingWords {

    private List<List<Bitmap>> wordBitmaps;
    private RectF mRectF;
    private RectF mTransformedRectF;
    private float mScale = 1.0F;
    private float mDegrees = 0;

    public WritingWords() {
    }

    public WritingWords(List<List<Bitmap>> wordBitmaps, RectF rectF) {
        this.wordBitmaps = wordBitmaps;
        mRectF = rectF;
        mTransformedRectF = new RectF(rectF);
//        Log.d("tag", "@@@@ mRectF:" + mRectF + " mTransformedRectF:" + mTransformedRectF);
    }

    public List<List<Bitmap>> getWordBitmaps() {
        return wordBitmaps;
    }

    public void setWordBitmaps(List<List<Bitmap>> wordBitmaps) {
        this.wordBitmaps = wordBitmaps;
        int maxSize = AnnotationUtils.getMaxWordsLine(wordBitmaps);
        mRectF.right = mRectF.left + (BITMAP_SIDE_LENGTH + BITMAP_MARGIN) * maxSize - BITMAP_MARGIN;
        mRectF.bottom = mRectF.top + wordBitmaps.size() * INPUT_RECT_HEIGHT;
        genScaledAndRotatedRectF();
    }

    public RectF getRectF() {
        return mRectF;
    }

    public void setRectF(RectF rectF) {
        mRectF = rectF;
    }

    public float getScale() {
        return mScale;
    }

    public void setScale(float scale) {
        mScale = scale;
    }

    public RectF getTransformedRectF() {
        return mTransformedRectF;
    }

    public void setTransformedRectF(RectF transformedRectF) {
        mTransformedRectF = transformedRectF;
    }

    public float getDegrees() {
        return mDegrees;
    }

    public void setDegrees(float degrees) {
        mDegrees = degrees;
        Log.d("tag", "@@@@ degrees:" + degrees);
    }

    public void setScaleAndDegrees(float scale, float degrees) {
        mScale = scale;
        mDegrees = degrees;
        genScaledAndRotatedRectF();
    }

    public void genScaledAndRotatedRectF() {
        RectF rectF = new RectF(mRectF);
//        Log.d("tag", "@@@@ before:" + rectF);
        Matrix matrix = new Matrix();
        matrix.setScale(mScale, mScale, rectF.centerX(), rectF.centerY());
        matrix.setRotate(mDegrees, rectF.centerX(), rectF.centerY());
        matrix.mapRect(rectF);
//        Log.d("tag", "@@@@ after:" + rectF);
        mTransformedRectF = rectF;
    }

    public void offsetRect(float x, float y) {
        mRectF.offset(x, y);
        mTransformedRectF.offset(x, y);
    }
}
