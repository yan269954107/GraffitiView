package com.yanxw.graffiti.newversion;

import android.view.MotionEvent;

import com.yanxw.graffiti.newversion.model.WritingWords;

/**
 * AnnotationListener
 * Created by yanxinwei on 2019-07-12.
 */
public interface AnnotationListener {

    void onDrawDown(MotionEvent event);

    void onTextDown(MotionEvent event);

    void onWritingDown(MotionEvent event);

    void onWritingMove(MotionEvent event);

    void onWritingUp(MotionEvent event);

    void onSizeChange(int imgWidth, int imgHeight, int w, int h);

    void showKeyboard(String text);

    void formatText(String text);

    void goEditWriting(WritingWords writingWords);

}
