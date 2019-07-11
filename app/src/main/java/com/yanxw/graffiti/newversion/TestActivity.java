package com.yanxw.graffiti.newversion;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.yanxw.graffiti.R;

import java.io.File;

public class TestActivity extends AppCompatActivity {

    private float mScale = 1;
    private MarkView mMarkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        final FrameLayout flContainer = findViewById(R.id.fl_container);
        flContainer.post(new Runnable() {
            @Override
            public void run() {
                try {
                    PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(new File("/sdcard/test.pdf"), ParcelFileDescriptor.MODE_READ_ONLY));
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

                    mMarkView = new MarkView(TestActivity.this, bitmap);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                    flContainer.addView(mMarkView, params);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.btn_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMarkView.undo();
            }
        });
    }
}
