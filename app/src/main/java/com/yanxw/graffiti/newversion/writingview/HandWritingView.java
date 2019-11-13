package com.yanxw.graffiti.newversion.writingview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.yanxw.graffiti.CollectionsUtil;
import com.yanxw.graffiti.CommonUtils;
import com.yanxw.graffiti.newversion.AnnotationUtils;
import com.yanxw.graffiti.newversion.model.WritingWords;

import java.util.ArrayList;
import java.util.List;

import static com.yanxw.graffiti.newversion.writingview.WritingView.INPUT_RECT_HEIGHT;

/**
 * HandWritingView
 * Created by yanxinwei on 2019-11-04.
 */
public class HandWritingView {

    public static final int BITMAP_SIDE_LENGTH = CommonUtils.dp2px(22);
    public static final int BITMAP_MARGIN = CommonUtils.dp2px(2);

    private List<List<Bitmap>> mLineWords = new ArrayList<>();
    private Paint mBitmapPaint;
    private Context mContext;

    public HandWritingView(Context context, RectF rectF) {
        mContext = context;

        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void draw(Canvas canvas, RectF rectF) {
        float left = rectF.left;
        float top = rectF.top + (float) (INPUT_RECT_HEIGHT - BITMAP_SIDE_LENGTH) / 2;
        for (int i = 0; i < mLineWords.size(); i++) {
            for (Bitmap bitmap : mLineWords.get(i)) {
                canvas.drawBitmap(bitmap, left, top, mBitmapPaint);
                left += BITMAP_SIDE_LENGTH + BITMAP_MARGIN;
            }
            left = rectF.left;
            top += INPUT_RECT_HEIGHT;
        }
    }

    public Bitmap drawWord(RectF rectF) {
        RectF targetRectF = new RectF(rectF);
        targetRectF.bottom = targetRectF.bottom - targetRectF.top;
        targetRectF.left = 0;
        targetRectF.top = 0;
        int maxSize = AnnotationUtils.getMaxWordsLine(mLineWords);
        if (maxSize == 0) return null;
        targetRectF.right = (BITMAP_SIDE_LENGTH + BITMAP_MARGIN) * maxSize - BITMAP_MARGIN;
        Bitmap bitmap = Bitmap.createBitmap((int) targetRectF.width(), (int) targetRectF.height(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas, targetRectF);
        return bitmap;
    }

    public WritingWords getWritingWords(RectF rectF) {
        int maxSize = AnnotationUtils.getMaxWordsLine(mLineWords);
        if (maxSize == 0) return null;
        rectF.right = rectF.left + (BITMAP_SIDE_LENGTH + BITMAP_MARGIN) * maxSize - BITMAP_MARGIN;
        return new WritingWords(mLineWords, rectF);
    }

    public void addWord(Bitmap bitmap) {
        getLastLine().add(bitmap);
    }

    private List<Bitmap> getLastLine() {
        if (CollectionsUtil.isEmpty(mLineWords)) {
            List<Bitmap> bitmaps = new ArrayList<>();
            mLineWords.add(bitmaps);
            return bitmaps;
        } else {
            return mLineWords.get(mLineWords.size() - 1);
        }
    }

    public void addLine() {
        List<Bitmap> bitmaps = new ArrayList<>();
        mLineWords.add(bitmaps);
    }

    public int getAddedLineWidth() {
        int size = getLastLine().size();
        if (size == 0) return 0;
        return (size + 1) * (BITMAP_SIDE_LENGTH + BITMAP_MARGIN);
    }

    public boolean removeWord() {
        List<Bitmap> lastLine = getLastLine();
        if (lastLine.size() == 0) {
            mLineWords.remove(lastLine);
            return true;
        } else {
            lastLine.remove(lastLine.size() - 1);
            return false;
        }
    }

    public void setWords(List<List<Bitmap>> words) {
        mLineWords = words;
    }

    public List<List<Bitmap>> getLineWords() {
        return mLineWords;
    }
}
