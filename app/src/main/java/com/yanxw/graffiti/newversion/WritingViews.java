package com.yanxw.graffiti.newversion;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.yanxw.graffiti.CollectionsUtil;
import com.yanxw.graffiti.newversion.model.WritingWords;

import java.util.ArrayList;
import java.util.List;

import static com.yanxw.graffiti.newversion.writingview.HandWritingView.BITMAP_MARGIN;
import static com.yanxw.graffiti.newversion.writingview.HandWritingView.BITMAP_SIDE_LENGTH;
import static com.yanxw.graffiti.newversion.writingview.WritingView.INPUT_RECT_HEIGHT;

/**
 * WritingViews
 * Created by yanxinwei on 2019-11-11.
 */
public class WritingViews extends ClickCheckItr {

    public static float MAX_SCALE = 3.0F;
    public static float MIN_SCALE = 1.0F;

    private List<WritingWords> mWritingWords = new ArrayList<>();
    private Paint mBitmapPaint;
    private ConvertXY mConvertXY;

    private float mLastDistance;
    private WritingWords mCurrentWritingWords = null;
    private float mLastDegrees;

    public WritingViews(Paint paint) {
        mBitmapPaint = paint;
    }

    public void draw(Canvas canvas) {

        for (WritingWords words : mWritingWords) {
            RectF rectF = words.getRectF();
            float left = rectF.left;
            float top = rectF.top + (float) (INPUT_RECT_HEIGHT - BITMAP_SIDE_LENGTH) / 2;
            List<List<Bitmap>> lineWords = words.getWordBitmaps();
            canvas.save();
            canvas.scale(words.getScale(), words.getScale(), rectF.centerX(), rectF.centerY());
            canvas.rotate(words.getDegrees(), rectF.centerX(), rectF.centerY());
            for (int i = 0; i < lineWords.size(); i++) {
                for (Bitmap bitmap : lineWords.get(i)) {
                    canvas.drawBitmap(bitmap, left, top, mBitmapPaint);
                    left += BITMAP_SIDE_LENGTH + BITMAP_MARGIN;
                }
                left = rectF.left;
                top += INPUT_RECT_HEIGHT;
            }
            canvas.restore();
        }

    }

    public void addWritingWords(WritingWords writingWords) {
        RectF rectF = writingWords.getRectF();
        PointF pointF = mConvertXY.getCanvasCenter();
//        Log.d("tag", "@@@@ addWritingWords rectF:" + rectF + " pointF:" + pointF + " centerX:" + rectF.centerX() + " centerY:" + rectF.centerY());
        if (rectF != null) {
            writingWords.offsetRect(pointF.x - rectF.centerX(), pointF.y - rectF.centerY());
//            Log.d("tag", "@@@@ offset rectF:" + rectF);
            mWritingWords.add(writingWords);
        }
    }

    public void setConvertXY(ConvertXY convertXY) {
        mConvertXY = convertXY;
    }

    public boolean downWriting(MotionEvent event) {
        if (CollectionsUtil.isEmpty(mWritingWords)) return false;
        PointF pointF = mConvertXY.convert2ViewXY(event.getX(), event.getY());
        for (int i = mWritingWords.size() - 1; i >= 0; i--) {
            WritingWords writingWords = mWritingWords.get(i);
            boolean isContains = writingWords.getTransformedRectF().contains(pointF.x, pointF.y);
//            Log.d("tag", "@@@@ scaledRectF:" + writingWords.getTransformedRectF() + " x:" + pointF.x + " y:" + pointF.y);
            if (isContains) {
                mCurrentWritingWords = writingWords;
                mLastPointF = pointF;
                mDownPointF = pointF;
                mDownTime = System.currentTimeMillis();
                return true;
            }
        }
        return false;
    }

    public boolean moveWriting(MotionEvent event) {
        if (CollectionsUtil.isEmpty(mWritingWords) || mCurrentWritingWords == null) return false;
        PointF pointF = mConvertXY.convert2ViewXY(event.getX(), event.getY());
        float moveX = pointF.x - mLastPointF.x;
        float moveY = pointF.y - mLastPointF.y;
        RectF rectF = mCurrentWritingWords.getRectF();
        rectF.offset(moveX, moveY);

        RectF scaledRectF = mCurrentWritingWords.getTransformedRectF();
        scaledRectF.offset(moveX, moveY);

        mLastPointF = pointF;
        return true;
    }

    public boolean upWriting(MotionEvent event) {
        if (CollectionsUtil.isEmpty(mWritingWords) || mCurrentWritingWords == null) return false;
        PointF pointF = mConvertXY.convert2ViewXY(event.getX(), event.getY());
        return checkClick(pointF);
    }

    public void doublePointerDown(MotionEvent event) {
        mLastDistance = AnnotationUtils.getPointDistance(event);
        mLastDegrees = AnnotationUtils.getDegrees(event);
    }

    public void doublePointerScaleAndRotate(MotionEvent event) {
        if (mCurrentWritingWords == null) return;
        float distance = AnnotationUtils.getPointDistance(event);
        float diff = distance - mLastDistance;
        float diffScale = diff / mLastDistance;
        float scale = (1 + diffScale) * mCurrentWritingWords.getScale();
        mLastDistance = distance;
        if (scale > MAX_SCALE) scale = MAX_SCALE;
        if (scale < MIN_SCALE) scale = MIN_SCALE;

        float theDegrees = AnnotationUtils.getDegrees(event);
        float degrees = theDegrees - mLastDegrees + mCurrentWritingWords.getDegrees();
        mLastDegrees = theDegrees;

        mCurrentWritingWords.setScaleAndDegrees(scale, degrees);
    }

    public WritingWords getCurrentWritingWords() {
        return mCurrentWritingWords;
    }

    public void deleteWords() {
        if (mCurrentWritingWords != null) {
            mWritingWords.remove(mCurrentWritingWords);
        }
    }

    public void editWritingWords(List<List<Bitmap>> words) {
        if (mCurrentWritingWords != null) {
            if (CollectionsUtil.isEmpty(words) || (words.size() == 1 && CollectionsUtil.isEmpty(words.get(0)))) {
                deleteWords();
                return;
            }
            List<List<Bitmap>> lineWords = new ArrayList<>(words);
            mCurrentWritingWords.setWordBitmaps(lineWords);
        }
    }
}
