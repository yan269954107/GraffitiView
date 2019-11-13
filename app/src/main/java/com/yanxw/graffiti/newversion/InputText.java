package com.yanxw.graffiti.newversion;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;

import com.yanxw.graffiti.CollectionsUtil;
import com.yanxw.graffiti.CommonUtils;
import com.yanxw.graffiti.R;
import com.yanxw.graffiti.newversion.model.RectText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import static com.yanxw.graffiti.newversion.AnnotationConstants.OPT_NULL;
import static com.yanxw.graffiti.newversion.AnnotationConstants.OPT_REDRAW;
import static com.yanxw.graffiti.newversion.AnnotationConstants.OPT_REDRAW_KEYBOARD;
import static com.yanxw.graffiti.newversion.AnnotationConstants.TYPE_RECT_CLOSE;
import static com.yanxw.graffiti.newversion.AnnotationConstants.TYPE_RECT_NORMAL;
import static com.yanxw.graffiti.newversion.AnnotationConstants.sDashWidth;
import static com.yanxw.graffiti.newversion.AnnotationConstants.sTextPaddingLeft;

/**
 * InputText
 * Created by yanxinwei on 2019-07-12.
 */
public class InputText extends ClickCheckItr{

    private static final int sDefaultWidth = CommonUtils.dp2px(114);
    private static final int sDefaultHeight = CommonUtils.dp2px(23);
    private static final int sCloseWidth = CommonUtils.dp2px(17);
    private static final int sCloseRadius = sCloseWidth / 2;
    private static final int sClosePadding = CommonUtils.dip2px(4.5F);
    private static final int sTextPaddingTop = CommonUtils.dp2px(3);

    private Context mContext;
    private LinkedList<RectText> mRectTexts = new LinkedList<>();
    private RectText mCurrentRectText;
    private RectF mTextBounds;

    private Paint mEditBorderPaint;
    private TextPaint mTextPaint;
    private Paint mCloseBgPaint;
    private Paint mCloseLinePaint;

    private ConvertXY mConvertXY;

    private int mDownType = TYPE_RECT_NORMAL;
    private int mDownIndex = -1;
    private AnnotationListener mAnnotationListener;

    public InputText(Context context, AnnotationListener annotationListener) {
        mContext = context;
        mAnnotationListener = annotationListener;

//        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mBorderPaint.setStyle(Paint.Style.STROKE);
//        mBorderPaint.setStrokeWidth(1);

        mEditBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mEditBorderPaint.setStyle(Paint.Style.STROKE);
        mEditBorderPaint.setStrokeWidth(CommonUtils.dp2px(mContext, 1));
        mEditBorderPaint.setColor(mContext.getResources().getColor(R.color.c_mark_red));
        mEditBorderPaint.setPathEffect(new DashPathEffect(new float[]{sTextPaddingLeft, sDashWidth}, 0));

//        mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mFillPaint.setStyle(Paint.Style.FILL);
//        mFillPaint.setColor(mContext.getResources().getColor(R.color.c_white));

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(CommonUtils.dp2px(11));
        mTextPaint.setColor(Color.BLACK);

        mCloseBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCloseBgPaint.setStyle(Paint.Style.FILL);
        mCloseBgPaint.setColor(mContext.getResources().getColor(R.color.c_mark_red));

        mCloseLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCloseLinePaint.setStyle(Paint.Style.STROKE);
        mCloseLinePaint.setColor(mContext.getResources().getColor(R.color.c_white));
        mCloseLinePaint.setStrokeWidth(CommonUtils.dp2px(1));
    }

    public void setConvertXY(ConvertXY convertXY) {
        mConvertXY = convertXY;
    }

    public void setRectTexts(LinkedList<RectText> rectTexts) {
        mRectTexts = rectTexts;
    }

    public void addRectText() {
        PointF centerPoint = mConvertXY.getInputTextCenter();
        RectText rectText = new RectText();
        float halfWidth = (float) sDefaultWidth / 2;
        float halfHeight = (float) sDefaultHeight / 2;
        float left = centerPoint.x - halfWidth;
        float top = centerPoint.y - halfHeight;
        float right = centerPoint.x + halfWidth;
        float bottom = centerPoint.y + halfHeight;
        RectF rect = new RectF(left, top, right, bottom);
        rectText.setRect(rect);

        rectText.setCloseRect((float) sCloseWidth / 2);

        mRectTexts.add(rectText);

        mCurrentRectText = rectText;
    }

    public void draw(Canvas canvas) {
        draw(canvas, false, mRectTexts);
    }

    public void draw(Canvas canvas, boolean isCalculate, LinkedList<RectText> rectTexts) {
        for (RectText rectText : rectTexts) {

            if (rectText.isClean()) continue;

            RectF rectF = rectText.getRect();
            if (isCalculate) {
                Log.d("tag", "#### draw rectF:" + rectF);
            }
            resolveTextRect(rectText);

//            canvas.drawRect(rectF, mFillPaint);

            String drawText = rectText.getText();
            if (!TextUtils.isEmpty(drawText)) {
                float startY = rectF.top + mTextPaint.getFontSpacing() + sTextPaddingTop;
                String[] drawTexts = drawText.split("\n");
                for (int i = 0; i < drawTexts.length; i++) {
                    canvas.drawText(drawTexts[i], rectF.left + sTextPaddingLeft,
                            startY + mTextPaint.getFontSpacing() * i, mTextPaint);
                }
            }

            if (rectText.isEdit()) {
                canvas.drawRect(rectF, mEditBorderPaint);
                rectText.setCloseRect((float) sCloseWidth / 2);
                rectText.drawClose(canvas, sCloseRadius, mCloseBgPaint, mCloseLinePaint, sClosePadding);
            } else {
//                canvas.drawRect(rectF, mBorderPaint);

                if (isCalculate) {
                    if (mTextBounds == null) {
                        mTextBounds = new RectF(rectF);
                    } else {
                        if (rectF.left < mTextBounds.left) {
                            mTextBounds.left = rectF.left;
                        }
                        if (rectF.top < mTextBounds.top) {
                            mTextBounds.top = rectF.top;
                        }
                        if (rectF.right > mTextBounds.right) {
                            mTextBounds.right = rectF.right;
                        }
                        if (rectF.bottom > mTextBounds.bottom) {
                            mTextBounds.bottom = rectF.bottom;
                        }
                    }
                    Log.d("tag", "#### draw mTextBounds:" + mTextBounds);
                }
            }
        }
    }

    public RectF getTextBounds() {
        return mTextBounds;
    }

    public void setTextBounds(RectF textBounds) {
        mTextBounds = textBounds;
    }

    private void resolveTextRect(RectText rectText) {

        float maxRight = mConvertXY.getWidth() - mConvertXY.getOffsetLeft();
        RectF rectF = rectText.getRect();
        String text = rectText.getText();
        float textWidth = 0, textHeight = (float) sDefaultHeight;
        if (!TextUtils.isEmpty(text)) {
            ArrayList<String> drawTexts = new ArrayList<>(Arrays.asList(text.split("\n")));
            boolean isFormat = false;
            for (int i = 0; i < drawTexts.size(); i++) {
                String drawText = drawTexts.get(i);
                while (rectF.left + mTextPaint.measureText(drawText) + (float) 2 * sTextPaddingLeft > maxRight) {
                    if (drawText.length() > 1) {
                        String prefix = drawText.substring(0, drawText.length() - 1);
                        String suffix = drawText.substring(drawText.length() - 1);
                        drawText = prefix;
                        drawTexts.set(i, drawText);
                        if (i + 1 < drawTexts.size()) {
                            String nextText = drawTexts.get(i + 1);
                            nextText = suffix + nextText;
                            drawTexts.set(i + 1, nextText);
                        } else {
                            drawTexts.add(suffix);
                        }
                        if (!isFormat) {
                            isFormat = true;
                        }
                    } else {
                        break;
                    }
                }
                float width = mTextPaint.measureText(drawText);
                if (textWidth < width) {
                    textWidth = width;
                }
            }

            if (drawTexts.size() > 1) {
                textHeight += mTextPaint.getFontSpacing() * (drawTexts.size() - 1);

                float maxBottom = mConvertXY.getHeight() - mConvertXY.getOffsetTop();
                float rectBottom = rectF.top + textHeight;
                if (rectBottom > maxBottom) {
                    isFormat = true;
                    int count = (int) Math.ceil((rectBottom - maxBottom) / mTextPaint.getFontSpacing());
                    for (int i = 1; i <= count; i++) {
                        drawTexts.remove(drawTexts.size() - 1);
                    }
                    textHeight = textHeight - mTextPaint.getFontSpacing() * count;
                }
            }

            if (isFormat) {
                StringBuilder sb = new StringBuilder();
                for (String drawText : drawTexts) {
                    sb.append(drawText).append("\n");
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                rectText.setText(sb.toString());
                if (mAnnotationListener != null) mAnnotationListener.formatText(sb.toString());
            }
        }
        textWidth += (float) 2 * sTextPaddingLeft;
        if (rectText.isEdit()) {
            if (textWidth > rectF.width()) {
                rectF.right = rectF.left + textWidth;
            }
        } else {
            if (textWidth != rectF.width()) {
                rectF.right = rectF.left + textWidth;
            }
        }
        if (textHeight != rectF.height()) {
            rectF.bottom = rectF.top + textHeight;
        }
    }

    public void changeText(String text) {
        if (mCurrentRectText != null) {
            mCurrentRectText.setText(text);
        }
    }

    public boolean downTextRect(MotionEvent event) {
        if (CollectionsUtil.isEmpty(mRectTexts)) return false;
        PointF pointF = mConvertXY.convert2ViewXY(event.getX(), event.getY());
        for (int i = mRectTexts.size() - 1; i >= 0; i--) {
            RectText rectText = mRectTexts.get(i);
            boolean isContains;
            if (rectText.isEdit()) {
                isContains = rectText.getCloseRect().contains(pointF.x, pointF.y);
                if (isContains) {
                    setDownParams(TYPE_RECT_CLOSE, rectText, pointF, i);
                    return true;
                }
            }
            isContains = rectText.getRect().contains(pointF.x, pointF.y);
            if (isContains) {
                setDownParams(TYPE_RECT_NORMAL, rectText, pointF, i);
                return true;
            }
        }
        return false;
    }

    private void setDownParams(int type, RectText rectText, PointF pointF, int index) {
//        if (mCurrentRectText != null) {
//            mCurrentRectText.setEdit(false);
//        }
        mCurrentRectText = rectText;
        mDownType = type;
        mLastPointF = pointF;
        mDownPointF = pointF;
        mDownTime = System.currentTimeMillis();
        mDownIndex = index;
    }

    public boolean moveTextRect(MotionEvent event) {
        if (mDownType == TYPE_RECT_CLOSE) return false;
        PointF pointF = mConvertXY.convert2ViewXY(event.getX(), event.getY());
        float moveX = pointF.x - mLastPointF.x;
        float moveY = pointF.y - mLastPointF.y;
        RectF rectF = mCurrentRectText.getRect();
        float rectWidth = rectF.right - rectF.left;
        float rectHeight = rectF.bottom - rectF.top;
        rectF.offset(moveX, moveY);
        float minLeft = mConvertXY.getOffsetLeft();
        if (rectF.left < minLeft) {
            rectF.left = minLeft;
            rectF.right = rectF.left + rectWidth;
        }
        float maxRight = mConvertXY.getWidth() - mConvertXY.getOffsetLeft();
        if (rectF.right > maxRight) {
            rectF.right = maxRight;
            rectF.left = rectF.right - rectWidth;
        }
        float minTop = mConvertXY.getOffsetTop();
        if (rectF.top < minTop) {
            rectF.top = minTop;
            rectF.bottom = rectF.top + rectHeight;
        }
        float maxBottom = mConvertXY.getHeight() - mConvertXY.getOffsetTop();
        if (rectF.bottom > maxBottom) {
            rectF.bottom = maxBottom;
            rectF.top = maxBottom - rectHeight;
        }

        mCurrentRectText.setCloseRect((float) sCloseWidth / 2);

        mLastPointF = pointF;
        return true;
    }

    /**
     * 处理手指抬起
     *
     * @param event motion event
     * @return result 1:不做任何处理 2:触发重绘 3：触发重绘且通知activity 弹出键盘
     */
    public int upTextRect(MotionEvent event) {
        PointF pointF = mConvertXY.convert2ViewXY(event.getX(), event.getY());
        if (mDownType == TYPE_RECT_CLOSE) {
            if (checkClick(pointF)) {
                if (mDownIndex != -1) {
                    mRectTexts.get(mDownIndex).setClean(true);
                    mCurrentRectText = null;
                }
                return OPT_REDRAW;
            }
        } else if (mDownType == TYPE_RECT_NORMAL) {
            if (checkClick(pointF)) {
                for (int i = 0; i < mRectTexts.size(); i++) {
                    if (i != mDownIndex) {
                        mRectTexts.get(i).setEdit(false);
                    }
                }
                mCurrentRectText.setEdit(true);
                return OPT_REDRAW_KEYBOARD;
            }
        }
        return OPT_NULL;
    }

    public boolean resetEdit() {
        if (mCurrentRectText != null && mCurrentRectText.isEdit()) {
            if (TextUtils.isEmpty(mCurrentRectText.getText())) {
                mRectTexts.remove(mCurrentRectText);
                mCurrentRectText = null;
            } else {
                mCurrentRectText.setEdit(false);
            }
            return true;
        }
        return false;
    }

    public String getCurrentText() {
        if (mCurrentRectText == null) return "";
        return mCurrentRectText.getText();
    }

    public void clear() {
        mRectTexts.clear();
        mCurrentRectText = null;
    }
}
