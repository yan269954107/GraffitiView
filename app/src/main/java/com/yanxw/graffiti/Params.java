package com.yanxw.graffiti;

/**
 * Params
 * Created by yanxinwei on 2017/3/6.
 */

public interface Params {

    int TOUCH_MODE_NULL = 0;
    int TOUCH_MODE_SINGLE = 1;
    int TOUCH_MODE_DOUBLE = 2;

    class DrawParams {

        float scale;
        float left;
        float top;

        public DrawParams(float scale, float left, float top) {
            this.scale = scale;
            this.left = left;
            this.top = top;
        }
    }

    class PathMoveParams {
        float x;
        float y;
    }

    class PathQuadParams {
        float x1;
        float y1;
        float x2;
        float y2;
    }

}
