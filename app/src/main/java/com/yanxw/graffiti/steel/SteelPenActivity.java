package com.yanxw.graffiti.steel;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.yanxw.graffiti.R;
import com.yanxw.graffiti.steel.widget.PaintView;

public class SteelPenActivity extends AppCompatActivity {

    PaintView mPaintView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steel_pen);
        mPaintView = findViewById(R.id.paint_view);
        findViewById(R.id.btn_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPaintView.undo();
            }
        });
    }
}
