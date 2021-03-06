package com.yanxw.graffiti.newversion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.yanxw.graffiti.newversion.model.RectText;
import com.yanxw.graffiti.newversion.model.WritingWords;
import com.yanxw.graffiti.steel.config.PenConfig;
import com.yanxw.graffiti.steel.config.PointsPath;
import com.yanxw.graffiti.steel.config.SteelConfig;
import com.yanxw.graffiti.steel.pen.BasePen;
import com.yanxw.graffiti.steel.pen.SteelPen;
import com.yanxw.graffiti.steel.util.DisplayUtil;

import java.util.LinkedList;
import java.util.List;

import static com.yanxw.graffiti.newversion.AnnotationConstants.DIRECT_LEFT;
import static com.yanxw.graffiti.newversion.AnnotationConstants.DIRECT_RIGHT;
import static com.yanxw.graffiti.newversion.AnnotationConstants.FLING_VELOCITY_THRESHOLD;
import static com.yanxw.graffiti.newversion.AnnotationConstants.OPT_REDRAW;
import static com.yanxw.graffiti.newversion.AnnotationConstants.OPT_REDRAW_KEYBOARD;
import static com.yanxw.graffiti.newversion.AnnotationConstants.STATUS_DRAG;
import static com.yanxw.graffiti.newversion.AnnotationConstants.STATUS_DRAW;
import static com.yanxw.graffiti.newversion.AnnotationConstants.VIEW_TYPE_FULL;
import static com.yanxw.graffiti.newversion.AnnotationConstants.VIEW_TYPE_WRITING;

/**
 * MarkView
 * Created by yanxinwei on 2019-06-05.
 */
public class AnnotationView extends View {

    private Bitmap mPicBitmap;
    private Bitmap mPathBitmap;
    private Canvas mPathCanvas;

    private Paint drawPaint;
    private Paint mPaint;

    //坐标转换工具
    private ConvertXY mConvertXY;

    private int strokeWidth;
    private BasePen mSteelPen;

    private int mTouchMode = 0;
    private boolean isDoubleMove = false;

    private AnnotationListener mAnnotationListener;
    private AnnotationInterface mAnnotationInterface;
    private GestureDetector mGestureDetector;
    private AnnotationDrawListener mDrawListener;

    private Eraser mEraser;
    private InputText mInputText;

    private WritingViews mWritingViews;

    private int mType = VIEW_TYPE_FULL;

    private WritingInterface mWritingInterface;

    public AnnotationView(Context context, Bitmap picBitmap, AnnotationInterface annotationInterface, AnnotationListener annotationListener) {
        super(context);

        mAnnotationInterface = annotationInterface;
        mAnnotationListener = annotationListener;

        initPaint(picBitmap);

        strokeWidth = DisplayUtil.dip2px(getContext(), SteelConfig.PEN_SIZES[PenConfig.PAINT_SIZE_LEVEL]);

        initPaint(0.5F);

        mEraser = new Eraser(this);
        mInputText = new InputText(context, mAnnotationListener);

        if (mAnnotationInterface != null) {
            initGestureDetector(context);
        }

        mWritingViews = new WritingViews(drawPaint);
    }

    public AnnotationView(Context context, WritingInterface writingInterface) {
        super(context);
        mWritingInterface = writingInterface;
        strokeWidth = DisplayUtil.dip2px(getContext(), SteelConfig.PEN_SIZES[5]);
        mType = VIEW_TYPE_WRITING;
        initPaint(4);
        mAnnotationInterface = new AnnotationInterface() {
            @Override
            public int getCurrentStatus() {
                return STATUS_DRAW;
            }

            @Override
            public void onFling(int direct) {

            }
        };
    }

    private void initPaint(float minWidth) {
        drawPaint = new Paint();

        mPaint = new Paint();
        mPaint.setColor(PenConfig.PAINT_COLOR);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAlpha(0xFF);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeMiter(1.0f);

        mSteelPen = new SteelPen();
        mSteelPen.setPaint(mPaint);
        mSteelPen.setMinWidth(minWidth);

        if (mConvertXY != null) {
            mSteelPen.setConvertXY(mConvertXY);
        }
    }

    private void initPaint(Bitmap picBitmap) {
        if (picBitmap != null) {
            mPicBitmap = picBitmap;
        }
        mPathBitmap = Bitmap.createBitmap(mPicBitmap.getWidth(), mPicBitmap.getHeight(), Bitmap.Config.ARGB_4444);
        mPathCanvas = new Canvas(mPathBitmap);
//        mPathCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
    }

    private void initGestureDetector(Context context) {
        mGestureDetector = new GestureDetector(context, new MyGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                Log.d("tag", "@@@@ e1X:" + e1.getX() + " e1Y:" + e1.getY() + " e2X:" +
                        e2.getX() + " e2Y:" + e2.getY() + " velocityX:" + velocityX + " velocityY:" + velocityY);

                float distanceX = e1.getX() - e2.getX();
                float distanceY = e1.getY() - e2.getY();
                if (Math.abs(distanceX) < 2 * Math.abs(distanceY)) {
                    return false;
                }
                if (Math.abs(velocityX) < FLING_VELOCITY_THRESHOLD) {
                    return false;
                }

                if (velocityX < 0) {
                    mAnnotationInterface.onFling(DIRECT_LEFT);
                }
                if (velocityX > 0) {
                    mAnnotationInterface.onFling(DIRECT_RIGHT);
                }
                return false;
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mType == VIEW_TYPE_FULL) {
            int imgWidth = mPicBitmap.getWidth();
            int imgHeight = mPicBitmap.getHeight();

            Log.d("tag", "#### w:" + w + " h:" + h + " imageWidth:" + imgWidth + " imageHeight:" + imgHeight);

            mConvertXY = new ConvertXY(imgWidth, imgHeight, w, h);
            mSteelPen.setConvertXY(mConvertXY);
            mInputText.setConvertXY(mConvertXY);
            mWritingViews.setConvertXY(mConvertXY);

            mAnnotationListener.onSizeChange(imgWidth, imgHeight, w, h);
        } else {
            mConvertXY = new ConvertXY(w, h, w, h);
            mSteelPen.setConvertXY(mConvertXY);

            mPathBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
            mPathCanvas = new Canvas(mPathBitmap);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mType == VIEW_TYPE_FULL) {
            return typeFullTouch(event);
        } else {
            return typeWritingTouch(event);
        }
    }

    private boolean typeFullTouch(MotionEvent event) {
        if (mAnnotationInterface.getCurrentStatus() == AnnotationConstants.STATUS_ERASER) {
            int action = event.getAction() & MotionEvent.ACTION_MASK;
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP) {
                mEraser.onEvent(mConvertXY.convert2BitmapXY(event.getX(), event.getY()), action == MotionEvent.ACTION_UP);
            }
            return true;
        }

        mGestureDetector.onTouchEvent(event);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
//                Log.d("tag", "@@@@ ACTION_DOWN x:" + event.getX() + " y:" + event.getY());
                mTouchMode = AnnotationConstants.TOUCH_MODE_SINGLE;

                if (mWritingViews.downWriting(event)) {
                    mAnnotationListener.onWritingDown(event);
                } else if (mInputText.downTextRect(event)) {
                    mAnnotationListener.onTextDown(event);
                } else if (mAnnotationInterface.getCurrentStatus() == STATUS_DRAG) {
                    mConvertXY.singlePointerDown(event);
                } else {
                    mSteelPen.onTouchEvent(event, mPathCanvas);

                    mAnnotationListener.onDrawDown(event);
                    boolean isNeedRedraw = mInputText.resetEdit();
                    if (isNeedRedraw) invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.d("tag", "@@@@ ACTION_MOVE");
                if (mTouchMode == AnnotationConstants.TOUCH_MODE_SINGLE) {
                    if (isDoubleMove) return true;
                    if (mAnnotationInterface.getCurrentStatus() == AnnotationConstants.STATUS_DRAG_TEXT) {
                        boolean isNeedRedraw = mInputText.moveTextRect(event);
                        if (isNeedRedraw) invalidate();
                    } else if (mAnnotationInterface.getCurrentStatus() == AnnotationConstants.STATUS_WRITING_TEXT) {
                        boolean isNeedRedraw = mWritingViews.moveWriting(event);
                        if (isNeedRedraw) invalidate();
                        mAnnotationListener.onWritingMove(event);
                    } else if (mAnnotationInterface.getCurrentStatus() == STATUS_DRAG) {
                        mConvertXY.singlePointerMove(event);
                    } else {
                        mSteelPen.onTouchEvent(event, mPathCanvas);
                    }
                    invalidate();
                } else if (mTouchMode == AnnotationConstants.TOUCH_MODE_DOUBLE) {
                    isDoubleMove = true;
                    if (mAnnotationInterface.getCurrentStatus() == AnnotationConstants.STATUS_WRITING_TEXT) {
                        mWritingViews.doublePointerScaleAndRotate(event);
                    } else {
                        mConvertXY.doublePointerMoveAndScale(event);
                    }
                    invalidate();
                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
//                Log.d("tag", "@@@@ ACTION_UP");
                mTouchMode = AnnotationConstants.TOUCH_MODE_NULL;
                if (mAnnotationInterface.getCurrentStatus() == AnnotationConstants.STATUS_DRAG_TEXT) {
                    int result = mInputText.upTextRect(event);
                    if (result == OPT_REDRAW) {
                        invalidate();
                    } else if (result == OPT_REDRAW_KEYBOARD) {
                        invalidate();
                        mAnnotationListener.showKeyboard(mInputText.getCurrentText());
                    }
                } else if (mAnnotationInterface.getCurrentStatus() == AnnotationConstants.STATUS_WRITING_TEXT) {
                    boolean goEdit = mWritingViews.upWriting(event);
                    if (goEdit)
                        mAnnotationListener.goEditWriting(mWritingViews.getCurrentWritingWords());
                    mAnnotationListener.onWritingUp(event);
                } else if (mAnnotationInterface.getCurrentStatus() == STATUS_DRAW) {
                    mSteelPen.onTouchEvent(event, mPathCanvas);
                }
                isDoubleMove = false;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
//                Log.d("tag", "@@@@ ACTION_POINTER_DOWN");
                if (mType == VIEW_TYPE_WRITING) return true;
                mTouchMode++;

                if (mAnnotationInterface.getCurrentStatus() == AnnotationConstants.STATUS_WRITING_TEXT) {
                    mWritingViews.doublePointerDown(event);
                } else if (mTouchMode == AnnotationConstants.TOUCH_MODE_DOUBLE) {
                    mConvertXY.doublePointerDown(event);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
//                Log.d("tag", "@@@@ ACTION_POINTER_UP");
                if (mType == VIEW_TYPE_WRITING) return true;
                mTouchMode--;
                if (mTouchMode == AnnotationConstants.TOUCH_MODE_SINGLE) {
//                    Log.d("tag", "@@@@ ACTION_POINTER_UP");
                }
                break;
        }
        return true;
    }

    private boolean typeWritingTouch(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mSteelPen.onTouchEvent(event, mPathCanvas);
                mWritingInterface.onWritingDown();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mWritingInterface.onWritingUp();
                mSteelPen.onTouchEvent(event, mPathCanvas);
                break;
            case MotionEvent.ACTION_MOVE:
                mSteelPen.onTouchEvent(event, mPathCanvas);
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mSteelPen.draw(mPathCanvas);
        if (mType == VIEW_TYPE_FULL) {
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
        }
        canvas.drawBitmap(mPathBitmap, mConvertXY.getOffsetLeft(), mConvertXY.getOffsetTop(), drawPaint);

        if (mType == VIEW_TYPE_FULL) {
            mInputText.draw(canvas);
            if (mDrawListener != null) {
                mDrawListener.drawDone();
            }
            mWritingViews.draw(canvas);
        }
    }

    public void drawCurrentImg(Canvas canvas) {
        mSteelPen.draw(mPathCanvas);
        canvas.drawBitmap(mPicBitmap, 0, 0, drawPaint);
        canvas.drawBitmap(mPathBitmap, 0, 0, drawPaint);
        mInputText.draw(canvas);
    }

    public void setDrawListener(AnnotationDrawListener drawListener) {
        mDrawListener = drawListener;
    }

    public void changeBitmap(Bitmap bitmap, LinkedList<PointsPath> pointsList, LinkedList<RectText> rectTexts) {
        initPaint(bitmap);
        mConvertXY = new ConvertXY(bitmap.getWidth(), bitmap.getHeight(), getWidth(), getHeight());
        mSteelPen.setConvertXY(mConvertXY);
        mInputText.setConvertXY(mConvertXY);
        mSteelPen.setPointsList(pointsList, mPathCanvas);
        mInputText.setRectTexts(rectTexts);
        invalidate();
    }

    public void initPointStack(LinkedList<PointsPath> pointsStack) {
        mSteelPen.setPointStack(pointsStack);
    }

    public void initRectTextList(LinkedList<RectText> rectTexts) {
        mInputText.setRectTexts(rectTexts);
    }

    public void undo(boolean isPop) {
        mPathBitmap.recycle();
        initPaint(null);
        mSteelPen.undo(mPathCanvas, isPop);
        invalidate();
    }

    public void clearMark() {
        initPaint(null);
        mSteelPen.clear();
        mInputText.clear();
        invalidate();
    }

    public void clearWriting() {
        mPathBitmap = null;
        mPathBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_4444);
        mPathCanvas = new Canvas(mPathBitmap);

        mSteelPen.clear();
        invalidate();
    }

    public RectF drawStack(LinkedList<PointsPath> pointsStack, Canvas canvas) {
        mSteelPen.drawStack(pointsStack, canvas, true);
        return mSteelPen.getPointBounds();
    }

    public void drawStack(Canvas canvas) {
        mSteelPen.drawStack(mSteelPen.getHWPointsList(), canvas, false);
    }

    public RectF drawRectTexts(LinkedList<RectText> rectTexts, Canvas canvas) {
        mInputText.setTextBounds(null);
        LinkedList<RectText> copyRectTexts = new LinkedList<>();
        for (RectText rectText : rectTexts) {
            RectText copyRectText = new RectText(new RectF(rectText.getRect()), rectText.getText(), false);
            copyRectText.processOffset(mConvertXY.getOffsetLeft(), mConvertXY.getOffsetTop());
            copyRectTexts.add(copyRectText);
        }
        mInputText.draw(canvas, true, copyRectTexts);
        return mInputText.getTextBounds();
    }

    public void drawStack(LinkedList<PointsPath> pointsStack) {
        mSteelPen.drawStack(pointsStack, mPathCanvas, false);
    }

    public void setRectTextList(LinkedList<RectText> rectTextList) {
        mInputText.setRectTexts(rectTextList);
    }

    public Bitmap getPathBitmap() {
        return mPathBitmap;
    }

    public void setStrokeWidth(int level) {
        strokeWidth = DisplayUtil.dip2px(getContext(), SteelConfig.PEN_SIZES[level]);
        mPaint.setStrokeWidth(strokeWidth);
        mSteelPen.setPaint(mPaint);
    }

    public void setPathColor(int color) {
        mPaint.setColor(color);
    }

    public LinkedList<PointsPath> getCurrentHWPointsList() {
        return mSteelPen.getHWPointsList();
    }

    public void restore() {
        mConvertXY.restore();
        invalidate();
    }

    //--------------------输入框相关 start---------------------//
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
    //--------------------输入框相关 end---------------------//

    //--------------------手写输入 start---------------------//
    public void addWritingWords(WritingWords writingWords) {
        mWritingViews.addWritingWords(writingWords);
        invalidate();
    }

    public void deleteWord() {
        mWritingViews.deleteWords();
        invalidate();
    }

    public void editWritingWords(List<List<Bitmap>> words) {
        mWritingViews.editWritingWords(words);
        invalidate();
    }
    //--------------------手写输入 end--------------------  -//
}
