package com.yanxw.graffiti.newversion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.yanxw.graffiti.steel.config.PenConfig;
import com.yanxw.graffiti.steel.config.SteelConfig;
import com.yanxw.graffiti.steel.pen.BasePen;
import com.yanxw.graffiti.steel.pen.SteelPen;
import com.yanxw.graffiti.steel.util.DisplayUtil;

import static com.yanxw.graffiti.newversion.AnnotationInterface.OPT_REDRAW;
import static com.yanxw.graffiti.newversion.AnnotationInterface.OPT_REDRAW_KEYBOARD;

/**
 * MarkView
 * Created by yanxinwei on 2019-06-05.
 */
public class MarkView extends View {

    private Bitmap mPicBitmap;
    private Bitmap mPathBitmap;
    private Canvas mPathCanvas;

    private Context mContext;

    private Paint drawPaint;
    private Paint mPaint;

    //坐标转换工具
    private ConvertXY mConvertXY;

    private int strokeWidth;
    private BasePen mStokeBrushPen;

    private int mTouchMode = 0;
    private boolean isDoubleMove = false;

    private MarkListener mMarkListener;
    private AnnotationInterface mAnnotationInterface;
    private InputText mInputText;

    private Eraser mEraser;

    public MarkView(Context context, Bitmap bitmap, MarkListener markListener, AnnotationInterface annotationInterface) {
        super(context);
        mContext = context;
        mPicBitmap = bitmap;
        mPathBitmap = Bitmap.createBitmap(mPicBitmap.getWidth(), mPicBitmap.getHeight(), Bitmap.Config.ARGB_4444);
        mPathCanvas = new Canvas(mPathBitmap);
        mPathCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

        strokeWidth = DisplayUtil.dip2px(getContext(), SteelConfig.PEN_SIZES[PenConfig.PAINT_SIZE_LEVEL]);

        drawPaint = new Paint();
        drawPaint.setAntiAlias(true);
        drawPaint.setFilterBitmap(true);

        mPaint = new Paint();
        mPaint.setColor(PenConfig.PAINT_COLOR);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAlpha(0xFF);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeMiter(1.0f);

        mStokeBrushPen = new SteelPen();
        mStokeBrushPen.setPaint(mPaint);
        if (mConvertXY != null) {
            mStokeBrushPen.setConvertXY(mConvertXY);
        }

        mMarkListener = markListener;
        mAnnotationInterface = annotationInterface;

        mInputText = new InputText(context, markListener);
        mEraser = new Eraser(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int imgWidth = mPicBitmap.getWidth();
        int imgHeight = mPicBitmap.getHeight();

        Log.d("tag", "@@@@ w:" + w + " h:" + h + " imageWidth:" + imgWidth + " imageHeight:" + imgHeight);

        float initLeft, initTop;

        if (w == imgWidth) {
            initLeft = 0;
            initTop = (h - imgHeight) * 1f / 2;
        } else {
            initLeft = (w - imgWidth) * 1f / 2;
            initTop = 0;
        }

        mConvertXY = new ConvertXY(initTop, initLeft, w, h);
        mStokeBrushPen.setConvertXY(mConvertXY);
        mInputText.setConvertXY(mConvertXY);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mAnnotationInterface.getCurrentStatus() == AnnotationInterface.STATUS_ERASER) {
            int action = event.getAction() & MotionEvent.ACTION_MASK;
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP) {
                mEraser.onEvent(mConvertXY.convert2BitmapXY(event.getX(), event.getY()), action == MotionEvent.ACTION_UP);
            }
            return true;
        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
//                Log.d("tag", "@@@@ ACTION_DOWN");
                mTouchMode = MarkConstants.TOUCH_MODE_SINGLE;

                if (mInputText.downTextRect(event)) {
                    mMarkListener.onTextDown(event);
                } else {
                    mStokeBrushPen.onTouchEvent(event, mPathCanvas);

                    if (mMarkListener != null) {
                        mMarkListener.onDrawDown(event);
                        boolean isNeedRedraw = mInputText.resetEdit();
                        if (isNeedRedraw) invalidate();
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
//                Log.d("tag", "@@@@ ACTION_MOVE");
                if (mAnnotationInterface.getCurrentStatus() == AnnotationInterface.STATUS_DRAG_TEXT) {
                    boolean isNeedRedraw = mInputText.moveTextRect(event);
                    if (isNeedRedraw) invalidate();
                } else {
                    if (mTouchMode == MarkConstants.TOUCH_MODE_SINGLE) {
//                    Log.d("tag", "@@@@ ACTION_MOVE 1");
                        if (isDoubleMove) return true;
                        mStokeBrushPen.onTouchEvent(event, mPathCanvas);
                        invalidate();

                    } else if (mTouchMode == MarkConstants.TOUCH_MODE_DOUBLE) {
//                    Log.d("tag", "@@@@ ACTION_MOVE 2");
                        isDoubleMove = true;
                        mConvertXY.doublePointerMove(event);
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
//                Log.d("tag", "@@@@ ACTION_UP");
                mTouchMode = MarkConstants.TOUCH_MODE_NULL;
                if (mAnnotationInterface.getCurrentStatus() == AnnotationInterface.STATUS_DRAG_TEXT) {
                    int result = mInputText.upTextRect(event);
                    if (result == OPT_REDRAW) {
                        invalidate();
                    } else if (result == OPT_REDRAW_KEYBOARD) {
                        invalidate();
                        mMarkListener.showKeyboard(mInputText.getCurrentText());
                    }
                } else {
                    mStokeBrushPen.onTouchEvent(event, mPathCanvas);
                    isDoubleMove = false;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
//                Log.d("tag", "@@@@ ACTION_POINTER_DOWN");
                mTouchMode++;
                if (mAnnotationInterface.getCurrentStatus() == AnnotationInterface.STATUS_DRAG_TEXT) {

                } else {
                    if (mTouchMode == MarkConstants.TOUCH_MODE_DOUBLE) {
                        mConvertXY.doublePointerDown(event);
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
//                Log.d("tag", "@@@@ ACTION_POINTER_UP");
                mTouchMode--;
                if (mTouchMode == MarkConstants.TOUCH_MODE_SINGLE) {
//                    Log.d("tag", "@@@@ ACTION_POINTER_UP");
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mStokeBrushPen.draw(mPathCanvas);

        PointF pointF = mConvertXY.getDownPointF();
        float centerX = 0;
        float centerY = 0;
        if (pointF != null) {
            centerX = pointF.x;
            centerY = pointF.y;
        }
        canvas.scale(mConvertXY.getScale(), mConvertXY.getScale(), centerX, centerY);
        canvas.translate(mConvertXY.getTranslateX(), mConvertXY.getTranslateY());
        canvas.drawBitmap(mPicBitmap, mConvertXY.getOffsetLeft(), mConvertXY.getOffsetTop(), drawPaint);
        canvas.drawBitmap(mPathBitmap, mConvertXY.getOffsetLeft(), mConvertXY.getOffsetTop(), drawPaint);

//        Log.d("tag", "@@@@ scale:" + mConvertXY.getScale() + " centerX:" + centerX
//                + " centerY:" + centerY + " translateX:" + mConvertXY.getTranslateX() + " translateY:"
//                + mConvertXY.getTranslateY() + " offsetLeft:" + mConvertXY.getOffsetLeft()
//                + " offsetTop:" + mConvertXY.getOffsetTop());

        mInputText.draw(canvas);
    }

//    private Rect getDirtyRect(float lastX, float lastY, float x, float y) {
//        return new Rect(Tools.getCeilInt(lastX), Tools.getCeilInt(lastY),
//                Tools.getCeilInt(x), Tools.getCeilInt(y));
//    }

    public void undo(boolean isPop) {

        mPathBitmap.recycle();
        mPathBitmap = Bitmap.createBitmap(mPicBitmap.getWidth(), mPicBitmap.getHeight(), Bitmap.Config.ARGB_4444);
        mPathCanvas = new Canvas(mPathBitmap);

        mStokeBrushPen.undo(mPathCanvas, isPop);

        invalidate();

    }


    //----------------------rectText start----------------------//
    public void addRectText() {
        mInputText.addRectText();
        invalidate();
    }

    public void changeText(String text) {
        mInputText.changeText(text);
        invalidate();
    }

    public void resetEdit() {
        if (mInputText.resetEdit()) invalidate();
    }
    //----------------------rectText end----------------------//

}
