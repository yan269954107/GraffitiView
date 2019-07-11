package com.yanxw.graffiti;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class TestViewActivity extends AppCompatActivity {

    EditText mEdtScale;
    EditText mEdtTranslateX;
    EditText mEdtTranslateY;
    EditText mEdtCenterX;
    EditText mEdtCenterY;

    TestView mTestView;
    RelativeLayout mRlRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_view);

        mEdtScale = findViewById(R.id.edt_scale);
        mEdtTranslateX = findViewById(R.id.edt_translate_x);
        mEdtTranslateY = findViewById(R.id.edt_translate_y);
        mEdtCenterX = findViewById(R.id.edt_center_x);
        mEdtCenterY = findViewById(R.id.edt_center_y);
        mTestView = findViewById(R.id.tv_test);

        findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float scale = getFloat(mEdtScale);
                if (scale == 0) {
                    scale = 1;
                }
                float translateX = getFloat(mEdtTranslateX);
                float translateY = getFloat(mEdtTranslateY);
                float centerX = getFloat(mEdtCenterX);
                float centerY = getFloat(mEdtCenterY);

                mTestView.setParams(scale, centerX, centerY, translateX, translateY);
            }
        });
    }

    private float getFloat(EditText editText) {
        String str = editText.getText().toString().trim();
        if (TextUtils.isEmpty(str)) return 0;
        return Float.parseFloat(str);
    }
}
