package com.yanxw.graffiti.newversion;

import com.yanxw.graffiti.CommonUtils;

/**
 * AnnotationConstants
 * Created by yanxinwei on 2019-06-06.
 */
public interface AnnotationConstants {

    int TOUCH_MODE_NULL = 0;
    int TOUCH_MODE_SINGLE = 1;
    int TOUCH_MODE_DOUBLE = 2;

    float FLIP_DISTANCE = CommonUtils.dp2px(80);
    float FLING_VELOCITY_THRESHOLD = 4000;

    int STATUS_DRAW = 1;
    int STATUS_DRAG = 2;
    int STATUS_ERASER = 3;
    int STATUS_DRAG_TEXT = 4;
    int STATUS_WRITING_TEXT = 5;

    int DIRECT_LEFT = 1;
    int DIRECT_RIGHT = 2;

    int TYPE_LOOK = 1;
    int TYPE_MARK = 2;

    int TYPE_RECT_NORMAL = 1;
    int TYPE_RECT_CLOSE = 2;

    int OPT_NULL = 1;
    int OPT_REDRAW = 2;
    int OPT_REDRAW_KEYBOARD = 3;

    int VIEW_TYPE_FULL = 1;
    int VIEW_TYPE_WRITING = 2;

    int sTextPaddingLeft = CommonUtils.dp2px(5);
    int sDashWidth = CommonUtils.dp2px(2);

}
