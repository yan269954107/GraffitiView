package com.yanxw.graffiti.newversion;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import com.yanxw.graffiti.R;
import com.yanxw.graffiti.newversion.model.WritingWords;

import java.io.File;
import java.util.List;

import static com.yanxw.graffiti.newversion.AnnotationConstants.STATUS_DRAG;

public class PdfiumActivity extends AppCompatActivity implements AnnotationListener, AnnotationInterface{

    public static final String TAG = "PdfiumActivity";

    FrameLayout flContainer;
    private AnnotationView mAnnotationView;
    private int mCurrentStatus = STATUS_DRAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itext_pdf);
        flContainer = findViewById(R.id.fl_container);
        flContainer.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ParcelFileDescriptor fd = ParcelFileDescriptor.open(new File("/sdcard/test_sign.pdf"), ParcelFileDescriptor.MODE_READ_ONLY);
                    PdfiumCore pdfiumCore = new PdfiumCore(PdfiumActivity.this);
                    PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
                    int pageCount = pdfiumCore.getPageCount(pdfDocument);
                    Log.d("PdfiumActivity", "@@@@ pageCount :" + pageCount);

                    pdfiumCore.openPage(pdfDocument, 0);

                    int width = pdfiumCore.getPageWidthPoint(pdfDocument, 0);
                    int height = pdfiumCore.getPageHeightPoint(pdfDocument, 0);
                    int containerWidth = flContainer.getWidth();
                    int containerHeight = flContainer.getHeight();
                    if (containerWidth != 0 && containerHeight != 0) {
                        float scaleWidth = (float) containerWidth / width;
                        float scaleHeight = (float) containerHeight / height;
                        if (scaleWidth > scaleHeight) {
                            width = (int) (pdfiumCore.getPageWidthPoint(pdfDocument, 0) * scaleHeight);
                            height = containerHeight;
                        } else {
                            width = containerWidth;
                            height = (int) (pdfiumCore.getPageHeightPoint(pdfDocument, 0) * scaleWidth);
                        }
                    }
                    Bitmap bitmap = Bitmap.createBitmap(width, height,
                            Bitmap.Config.RGB_565);
                    pdfiumCore.renderPageBitmap(pdfDocument, bitmap, 0, 0, 0, width, height, true);

                    mAnnotationView = new AnnotationView(PdfiumActivity.this, bitmap, PdfiumActivity.this, PdfiumActivity.this);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                    flContainer.addView(mAnnotationView, params);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void printInfo(PdfiumCore core, PdfDocument doc) {
        PdfDocument.Meta meta = core.getDocumentMeta(doc);
        Log.e(TAG, "title = " + meta.getTitle());
        Log.e(TAG, "author = " + meta.getAuthor());
        Log.e(TAG, "subject = " + meta.getSubject());
        Log.e(TAG, "keywords = " + meta.getKeywords());
        Log.e(TAG, "creator = " + meta.getCreator());
        Log.e(TAG, "producer = " + meta.getProducer());
        Log.e(TAG, "creationDate = " + meta.getCreationDate());
        Log.e(TAG, "modDate = " + meta.getModDate());

        printBookmarksTree(core.getTableOfContents(doc), "-");

    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    @Override
    public int getCurrentStatus() {
        return mCurrentStatus;
    }

    @Override
    public void onFling(int direct) {

    }

    @Override
    public void onDrawDown(MotionEvent event) {

    }

    @Override
    public void onTextDown(MotionEvent event) {

    }

    @Override
    public void onWritingDown(MotionEvent event) {

    }

    @Override
    public void onWritingMove(MotionEvent event) {

    }

    @Override
    public void onWritingUp(MotionEvent event) {

    }

    @Override
    public void onSizeChange(int imgWidth, int imgHeight, int w, int h) {

    }

    @Override
    public void showKeyboard(String text) {

    }

    @Override
    public void formatText(String text) {

    }

    @Override
    public void goEditWriting(WritingWords writingWords) {

    }
}
