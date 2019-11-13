package com.yanxw.graffiti.newversion.writingview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.yanxw.graffiti.CommonUtils;
import com.yanxw.graffiti.R;
import com.yanxw.graffiti.newversion.model.WritingWords;

import java.util.List;

import static com.yanxw.graffiti.newversion.AnnotationConstants.sDashWidth;
import static com.yanxw.graffiti.newversion.AnnotationConstants.sTextPaddingLeft;

/**
 * WritingView
 * Created by yanxinwei on 2019-11-04.
 */
public class WritingView extends View {

    public static final int INPUT_RECT_HEIGHT = CommonUtils.dp2px(26);
    public static final int INPUT_RECT_MARGIN = CommonUtils.dp2px(80);
    private HandWritingView mHandWritingView;
    private RectF mRectF = null;
    private Paint mBorderPaint;

    public WritingView(Context context) {
        this(context, null);
    }

    public WritingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WritingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(CommonUtils.dp2px(context, 1));
        mBorderPaint.setColor(context.getResources().getColor(R.color.c_white));
        mBorderPaint.setPathEffect(new DashPathEffect(new float[]{sTextPaddingLeft, sDashWidth}, 0));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float top = h / 2 - INPUT_RECT_HEIGHT / 2;
        if (mRectF == null) {
            mRectF = new RectF(INPUT_RECT_MARGIN, top, w - INPUT_RECT_MARGIN, top + INPUT_RECT_HEIGHT);
            mHandWritingView = new HandWritingView(getContext(), mRectF);
        } else {
            mRectF.set(INPUT_RECT_MARGIN, top, w - INPUT_RECT_MARGIN, top + INPUT_RECT_HEIGHT * mHandWritingView.getLineWords().size());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(mRectF, mBorderPaint);
        mHandWritingView.draw(canvas, mRectF);
    }

    public void addWord(Bitmap bitmap) {
        int addedLineWidth = mHandWritingView.getAddedLineWidth();
        if (addedLineWidth <= mRectF.width()) {
            mHandWritingView.addWord(bitmap);
        } else {
            mRectF.bottom = mRectF.bottom + INPUT_RECT_HEIGHT;
            mHandWritingView.addLine();
            mHandWritingView.addWord(bitmap);
        }
        invalidate();
    }

    public void addLine() {
        mRectF.bottom = mRectF.bottom + INPUT_RECT_HEIGHT;
        mHandWritingView.addLine();
        invalidate();
    }

    public void removeWord() {
        boolean isRemoveLine = mHandWritingView.removeWord();
        if (isRemoveLine) {
            mRectF.bottom = mRectF.bottom - INPUT_RECT_HEIGHT;
        }
        invalidate();
    }

    public RectF getRectF() {
        return mRectF;
    }

    public Bitmap drawWords() {
        return mHandWritingView.drawWord(mRectF);
    }

    public WritingWords getWritingWords() {
        return mHandWritingView.getWritingWords(mRectF);
    }

    public void setWords(List<List<Bitmap>> words) {
        mRectF = new RectF();
        mHandWritingView = new HandWritingView(getContext(), mRectF);
        mHandWritingView.setWords(words);
    }
}
