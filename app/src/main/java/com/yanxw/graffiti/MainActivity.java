package com.yanxw.graffiti;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private GraffitiView mGraffitiView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fl_container);
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.demo2bg).copy(Bitmap.Config.ARGB_8888, true);
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath().concat(File.separator).concat("demo2bg.png");
        mGraffitiView = new GraffitiView(this, R.mipmap.demo2bg);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        frameLayout.addView(mGraffitiView, params);
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
