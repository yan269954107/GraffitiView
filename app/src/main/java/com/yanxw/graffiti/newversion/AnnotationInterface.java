package com.yanxw.graffiti.newversion;

/**
 * AnnotationInterface
 * Created by yanxinwei on 2019-06-28.
 */
public interface AnnotationInterface {

    int STATUS_DRAW = 1;
    int STATUS_DRAG = 2;
    int STATUS_ERASER = 3;
    int STATUS_DRAG_TEXT = 4;

    int DIRECT_LEFT = 1;
    int DIRECT_RIGHT = 2;

    int TYPE_LOOK = 1;
    int TYPE_MARK = 2;

    int TYPE_RECT_NORMAL = 1;
    int TYPE_RECT_CLOSE = 2;

    int OPT_NULL = 1;
    int OPT_REDRAW = 2;
    int OPT_REDRAW_KEYBOARD = 3;

    int getCurrentStatus();

    //dir 1:left  2:right
    void onFling(int direct);

}