package com.yanxw.graffiti.steel.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.yanxw.graffiti.steel.config.PenConfig;
import com.yanxw.graffiti.steel.config.SteelConfig;
import com.yanxw.graffiti.steel.pen.BasePen;
import com.yanxw.graffiti.steel.pen.SteelPen;
import com.yanxw.graffiti.steel.util.DisplayUtil;

/**
 * SteelView
 * Created by yanxinwei on 2019-06-20.
 */
public class PaintView extends View {

    private Paint drawPaint;
    private Paint mPaint;
    private Canvas mCanvas;
    private Bitmap mBitmap;
    private int strokeWidth;
    private BasePen mStokeBrushPen;

    /**
     * 是否可以撤销
     */
    private boolean mCanUndo;

    private int mWidth;
    private int mHeight;

    private boolean isDrawing = false;//是否正在绘制

    /**
     * 是否有绘制
     */
    private boolean hasDraw = false;

    public PaintView(Context context) {
        this(context, null);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_4444);
        mCanvas = new Canvas(mBitmap);
        mStokeBrushPen = new SteelPen();

        initPaint();
    }

    /**
     * 初始画笔设置
     */
    private void initPaint() {
        strokeWidth = DisplayUtil.dip2px(getContext(), SteelConfig.PEN_SIZES[PenConfig.PAINT_SIZE_LEVEL]);
        drawPaint = new Paint();
        drawPaint.setAntiAlias(false);
        drawPaint.setFilterBitmap(false);
        mPaint = new Paint();
        mPaint.setColor(PenConfig.PAINT_COLOR);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAlpha(0xFF);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeMiter(1.0f);
        mStokeBrushPen.setPaint(mPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mStokeBrushPen.draw(mCanvas);
        canvas.drawBitmap(mBitmap, 0, 0, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mStokeBrushPen.onTouchEvent(event, mCanvas);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = false;
                break;
            case MotionEvent.ACTION_MOVE:
                hasDraw = true;
                mCanUndo = true;
                isDrawing = true;
                break;
            case MotionEvent.ACTION_CANCEL:
                isDrawing = false;
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                break;
            default:
                break;
        }
        invalidate();
        return true;
    }

    /**
     * @return 判断是否有绘制内容在画布上
     */
    public boolean isEmpty() {
        return !hasDraw;
    }

    public void release() {
        destroyDrawingCache();
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    /**
     * 设置画笔大小
     *
     * @param width 大小
     */
    public void setPaintWidth(int width) {
        if (mPaint != null) {
            mPaint.setStrokeWidth(DisplayUtil.dip2px(getContext(), width));
//            eraser.setPaintWidth(DisplayUtil.dip2px(getContext(), width));
            mStokeBrushPen.setPaint(mPaint);
            invalidate();
        }
    }

    /**
     * 设置画笔颜色
     *
     * @param color 颜色
     */
    public void setPaintColor(int color) {
        if (mPaint != null) {
            mPaint.setColor(color);
            mStokeBrushPen.setPaint(mPaint);
            invalidate();
        }
    }

    public void undo() {
        mBitmap.recycle();
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_4444);
        mCanvas = new Canvas(mBitmap);

        mStokeBrushPen.undo(mCanvas, true);

        invalidate();
    }
}
