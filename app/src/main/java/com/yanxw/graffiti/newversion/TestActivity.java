package com.yanxw.graffiti.newversion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.yanxw.graffiti.CommonUtils;
import com.yanxw.graffiti.R;
import com.yanxw.graffiti.SoftKeyBoardListener;
import com.yanxw.graffiti.WritingActivity;
import com.yanxw.graffiti.newversion.model.WritingWords;

import java.io.File;

import static com.yanxw.graffiti.WritingActivity.TYPE_EDIT;
import static com.yanxw.graffiti.WritingActivity.TYPE_WRITING;
import static com.yanxw.graffiti.newversion.AnnotationConstants.STATUS_DRAG;
import static com.yanxw.graffiti.newversion.AnnotationConstants.STATUS_DRAG_TEXT;
import static com.yanxw.graffiti.newversion.AnnotationConstants.STATUS_DRAW;
import static com.yanxw.graffiti.newversion.AnnotationConstants.STATUS_ERASER;
import static com.yanxw.graffiti.newversion.AnnotationConstants.STATUS_WRITING_TEXT;
import static com.yanxw.graffiti.newversion.ClickCheckItr.sClickRange;

public class TestActivity extends AppCompatActivity implements AnnotationListener, AnnotationInterface {

    public static final int REQUEST_WRITING = 1001;
    public static final int REQUEST_EDIT_WRITING = 1002;

    private float mScale = 1;
    private AnnotationView mAnnotationView;
    private EditText mEdtText;
    private int mCurrentStatus = STATUS_DRAG;
    private boolean keyboardIsShow = false;

    private boolean isWritingDown = false;
    private PointF mWritingDownPoint;
    private View mRecycleBin;
    private int deleteY;

    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d("tag", "@@@@ onTextChanged:" + s.toString());
            mAnnotationView.changeText(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mEdtText = findViewById(R.id.edt_text);
        mEdtText.addTextChangedListener(mTextWatcher);

        mRecycleBin = findViewById(R.id.ll_recycle_bin);

        final FrameLayout flContainer = findViewById(R.id.fl_container);
        flContainer.post(() -> {
            try {
                PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(new File("/sdcard/test_sign.pdf"), ParcelFileDescriptor.MODE_READ_ONLY));
                PdfRenderer.Page page = renderer.openPage(0);
                int width = page.getWidth();
                int height = page.getHeight();
                int containerWidth = flContainer.getWidth();
                int containerHeight = flContainer.getHeight();
                if (containerWidth != 0 && containerHeight != 0) {
                    float scaleWidth = (float) containerWidth / width;
                    float scaleHeight = (float) containerHeight / height;
                    if (scaleWidth > scaleHeight) {
                        width = (int) (page.getWidth() * scaleHeight);
                        height = containerHeight;
                    } else {
                        width = containerWidth;
                        height = (int) (page.getHeight() * scaleWidth);
                    }
                }
                //以下三行处理图片存储到本地出现黑屏的问题，这个涉及到背景问题
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                canvas.drawColor(Color.WHITE);
                canvas.drawBitmap(bitmap, 0, 0, null);
                Rect r = new Rect(0, 0, width, height);
                page.render(bitmap, r, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                page.close();

                mAnnotationView = new AnnotationView(TestActivity.this, bitmap, TestActivity.this, TestActivity.this);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                flContainer.addView(mAnnotationView, params);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.btn_undo).setOnClickListener(v -> mAnnotationView.undo(true));

        findViewById(R.id.btn_add_text).setOnClickListener(v -> {
            codeSetEdit("");
            mAnnotationView.addRectText();
            keyboardIsShow = true;
            Tools.showSoftKeyBoard(TestActivity.this, mEdtText);
        });

        findViewById(R.id.btn_draw).setOnClickListener(v -> mCurrentStatus = STATUS_DRAW);

        findViewById(R.id.btn_eraser).setOnClickListener(v -> mCurrentStatus = STATUS_ERASER);

        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {

            }

            @Override
            public void keyBoardHide(int height) {
                mAnnotationView.resetEdit();
            }
        });

        findViewById(R.id.btn_add_write).setOnClickListener(v -> {
            mAnnotationView.restore();
            WritingActivity.sEditWords = null;
            WritingActivity.startActivity(TestActivity.this, TYPE_WRITING, REQUEST_WRITING);
        });

    }

    private void codeSetEdit(String text) {
        mEdtText.removeTextChangedListener(mTextWatcher);
        mEdtText.setText(text);
        if (!TextUtils.isEmpty(text)) {
            mEdtText.setSelection(text.length());
        }
        mEdtText.addTextChangedListener(mTextWatcher);
    }

    @Override
    public void onDrawDown(MotionEvent event) {
        Tools.hideKeyBoard(this);
        keyboardIsShow = false;
        mCurrentStatus = STATUS_DRAW;
    }

    @Override
    public void onTextDown(MotionEvent event) {
        mCurrentStatus = STATUS_DRAG_TEXT;
        isWritingDown = true;
    }

    @Override
    public void onWritingDown(MotionEvent event) {
        mCurrentStatus = STATUS_WRITING_TEXT;
        mWritingDownPoint = new PointF(event.getX(), event.getY());
        isWritingDown = true;
    }

    @Override
    public void onWritingMove(MotionEvent event) {
        if (isWritingDown && (Math.abs(event.getX() - mWritingDownPoint.x) > sClickRange
                || Math.abs(event.getY() - mWritingDownPoint.y) > sClickRange)) {
            isWritingDown = false;
            mRecycleBin.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onWritingUp(MotionEvent event) {
        mRecycleBin.setVisibility(View.GONE);
        if (event.getY() > deleteY) {
            mAnnotationView.deleteWord();
        }
    }

    @Override
    public void onSizeChange(int imgWidth, int imgHeight, int w, int h) {
        int diffHeight = (h - imgHeight) / 2;
        deleteY = imgHeight + diffHeight;
        int marginBottom = CommonUtils.dip2px(28) + diffHeight;
//        Log.d("tag", "@@@@ imgWidth:" + imgWidth + " imgHeight:" + imgHeight + " w:" + w + " h:" + h + " marginBottom:" + marginBottom);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mRecycleBin.getLayoutParams();
        params.bottomMargin = marginBottom;
        mRecycleBin.setLayoutParams(params);
    }

    @Override
    public void showKeyboard(String text) {
        codeSetEdit(text);
        keyboardIsShow = true;
        Tools.showSoftKeyBoard(TestActivity.this, mEdtText);
    }

    @Override
    public void formatText(String text) {
        codeSetEdit(text);
    }

    @Override
    public void goEditWriting(WritingWords writingWords) {
        WritingActivity.sEditWords = writingWords.getWordBitmaps();
        WritingActivity.startActivity(this, TYPE_EDIT, REQUEST_EDIT_WRITING);
    }

    @Override
    public int getCurrentStatus() {
        return mCurrentStatus;
    }

    @Override
    public void onFling(int direct) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_WRITING) {
                mAnnotationView.addWritingWords(WritingActivity.sWritingWords);
            } else if (requestCode == REQUEST_EDIT_WRITING) {
                mAnnotationView.editWritingWords(WritingActivity.sEditWords);
                WritingActivity.sEditWords = null;
            }
        }
    }
}
