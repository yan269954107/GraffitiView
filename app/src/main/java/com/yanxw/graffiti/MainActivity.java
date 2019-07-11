package com.yanxw.graffiti;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.yanxw.graffiti.pdf.ImageConf;
import com.yanxw.graffiti.pdf.PDFImageDrawer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private GraffitiView mGraffitiView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fl_container);
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.demo2bg).copy(Bitmap.Config.ARGB_8888, true);
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath().concat(File.separator).concat("demo2bg.png");
        mGraffitiView = new GraffitiView(this, R.mipmap.demo3bg);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        frameLayout.addView(mGraffitiView, params);

        test();
    }

    private void test() {
        try {
            List<ImageConf> imageConfs = new ArrayList<>();
            imageConfs.add(new ImageConf(1, FileUtils.getFilesPath(this).getAbsolutePath().concat(File.separator).concat("mark.png")));
            imageConfs.add(new ImageConf(2, FileUtils.getFilesPath(this).getAbsolutePath().concat(File.separator).concat("mark.png")));
            FileInputStream fileInputStream = new FileInputStream(FileUtils.getFilesPath(this).getAbsolutePath().concat(File.separator).concat("test.pdf"));
            FileOutputStream fileOutputStream = new FileOutputStream(FileUtils.getFilesPath(this).getAbsolutePath().concat(File.separator).concat("gen_test.pdf"));
            PDFImageDrawer.drawImage(fileInputStream, fileOutputStream, imageConfs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.undo:
                mGraffitiView.undo();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
