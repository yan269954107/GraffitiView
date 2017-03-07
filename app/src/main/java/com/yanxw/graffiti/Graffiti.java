package com.yanxw.graffiti;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;

import java.util.Stack;

/**
 * Graffiti
 * Created by yanxinwei on 2017/3/6.
 */

public class Graffiti {

    private GraffitiPen mPen;
    private Stack<Path> mPathStack = new Stack<>();

    private Path mCurrentPath;

    private Canvas mGraffitiCanvas;
    private Bitmap mGraffitiBitmap;

    public Graffiti() {
        mPen = new GraffitiPen(GraffitiPen.COLOR_RED);
    }

    public void init(Bitmap srcBitmap) {
        mGraffitiBitmap = srcBitmap.copy(Bitmap.Config.ARGB_8888, true);
        mGraffitiCanvas = new Canvas(mGraffitiBitmap);
    }

    public void draw() {
        if (mCurrentPath != null) {
            mGraffitiCanvas.drawPath(mCurrentPath, mPen.getPaint());
        }
    }

    public Bitmap getGraffitiBitmap() {
        return mGraffitiBitmap;
    }

    public void actionDown(Params.PathMoveParams moveParams) {
//        Log.d("tag", "@@@@ Graffiti actionDown x : " + moveParams.x + " y : " + moveParams.y);
        mCurrentPath = new Path();
        mCurrentPath.moveTo(moveParams.x, moveParams.y);
        mPathStack.push(mCurrentPath);
    }

    public void actionMove(Params.PathQuadParams quadParams) {
//        Log.d("tag", "@@@@ Graffiti actionMove lastX : " + quadParams.x1 + " lastY : " + quadParams.y1 + " x : " + quadParams.x2 + " y : " + quadParams.y2);
        //贝塞尔曲线
        if (mCurrentPath != null) {
            mCurrentPath.quadTo(quadParams.x1, quadParams.y1, quadParams.x2, quadParams.y2);
        }
    }

    public void undo(Bitmap srcBitmap) {
        if (mPathStack.empty()) return;
        mGraffitiBitmap.recycle();
        mGraffitiBitmap = null;
        init(srcBitmap);
        //弹出最近一次的path,并将mCurrentPath置为null,防止重绘的时候将其绘出
        mPathStack.pop();
        mCurrentPath = null;
        for (Path path : mPathStack) {
            mGraffitiCanvas.drawPath(path, mPen.getPaint());
        }
    }

}
