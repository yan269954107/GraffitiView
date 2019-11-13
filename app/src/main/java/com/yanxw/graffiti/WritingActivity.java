package com.yanxw.graffiti;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.yanxw.graffiti.newversion.AnnotationView;
import com.yanxw.graffiti.newversion.WritingInterface;
import com.yanxw.graffiti.newversion.model.WritingWords;
import com.yanxw.graffiti.newversion.writingview.WritingView;

import java.util.List;

import static com.yanxw.graffiti.newversion.writingview.HandWritingView.BITMAP_SIDE_LENGTH;

public class WritingActivity extends AppCompatActivity implements WritingInterface {

    public static final int TYPE_WRITING = 1;
    public static final int TYPE_EDIT = 2;

    public static WritingWords sWritingWords;
    public static List<List<Bitmap>> sEditWords;

    private RelativeLayout mRlWritingContainer;
    private AnnotationView mAnnotationView;
    private WritingView mWritingView;

    private static final int GEN_PIC_TIME = 600;
    private static final int WORD_SCALED_LEVEL = 1;

    private int type;

    public static void startActivity(Activity context, int type, int requestCode) {
        Intent intent = new Intent(context, WritingActivity.class);
        intent.putExtra("type", type);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

        type = getIntent().getIntExtra("type", TYPE_WRITING);

        mRlWritingContainer = findViewById(R.id.rl_writing_container);
        mWritingView = findViewById(R.id.writing_view);
        if (type == TYPE_EDIT && sEditWords != null) {
            mWritingView.setWords(sEditWords);
        }
        mAnnotationView = new AnnotationView(WritingActivity.this, this);
        mAnnotationView.setPathColor(Color.RED);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mRlWritingContainer.addView(mAnnotationView, params);

        findViewById(R.id.btn_change_line).setOnClickListener(v -> {
            mWritingView.addLine();
        });

        findViewById(R.id.btn_delete).setOnClickListener(v -> {
            mWritingView.removeWord();
        });

        findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());

        findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            if (type == TYPE_WRITING) {
                WritingWords writingWords = mWritingView.getWritingWords();
                if (writingWords == null) {
                    setResult(RESULT_CANCELED);
                } else {
                    sWritingWords = writingWords;
                    setResult(RESULT_OK);
                }
            } else {
                setResult(RESULT_OK);
            }
            finish();
        });

    }

    Runnable mRunnable = () -> {
        Bitmap pathBitmap = mAnnotationView.getPathBitmap();
        Bitmap scaledBitmap = Bitmap.createBitmap(pathBitmap.getWidth(), pathBitmap.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(scaledBitmap);
        float scaleX = (float) BITMAP_SIDE_LENGTH / pathBitmap.getWidth() * WORD_SCALED_LEVEL;
        float scaleY = (float) BITMAP_SIDE_LENGTH / pathBitmap.getHeight() * WORD_SCALED_LEVEL;
        canvas.scale(scaleX, scaleY);
        mAnnotationView.drawStack(canvas);
        scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, BITMAP_SIDE_LENGTH * WORD_SCALED_LEVEL, BITMAP_SIDE_LENGTH * WORD_SCALED_LEVEL);
//        String base64 = ImageUtils.bitmapToBase64(scaledBitmap);
//        Log.d("tag", "@@@@ base64:" + base64);
        mAnnotationView.clearWriting();
        mWritingView.addWord(scaledBitmap);
    };

    @Override
    public void onWritingDown() {
        Handlers.removeRunnable(mRunnable);
    }

    @Override
    public void onWritingUp() {
        Handlers.postDelayed(mRunnable, GEN_PIC_TIME);
    }
}
