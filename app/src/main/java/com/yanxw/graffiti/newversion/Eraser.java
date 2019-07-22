package com.yanxw.graffiti.newversion;

import android.graphics.PointF;
import android.os.Handler;
import android.os.HandlerThread;

import com.yanxw.graffiti.Handlers;
import com.yanxw.graffiti.steel.config.ControllerPoint;
import com.yanxw.graffiti.steel.pen.BasePen;

import java.util.ArrayList;

/**
 * Eraser
 * Created by yanxinwei on 2019-07-18.
 */
public class Eraser {

    private Handler mCheckHandler;
    private HandlerThread mCheckThread;

    private PointF mLastPointF;

    private float mCheckAccuracy = Tools.dp2Px(5);
    private MarkView mMarkView;

    public Eraser(MarkView markView) {
        start();
        mMarkView = markView;
    }

    private void start() {
        if (mCheckThread == null) {
            mCheckThread = new HandlerThread("check_thread");
            mCheckThread.start();
            mCheckHandler = new Handler(mCheckThread.getLooper());
        }
    }

    /**
     * 是否需要做重绘操作
     *
     * @param pointF
     * @return
     */
    public void onEvent(PointF pointF, final boolean isUp) {
        if (pointF.x < 0 || pointF.y < 0) return;
        if (mLastPointF == null) {
            mLastPointF = pointF;
            check(pointF);
        } else {
            if (Math.abs(pointF.x - mLastPointF.x) > mCheckAccuracy || Math.abs(pointF.y - mLastPointF.y) > mCheckAccuracy) {
                check(pointF);
            }
        }

        mCheckHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isUp) mLastPointF = null;
            }
        });

    }

    private void check(final PointF pointF) {
//        Log.d("tag", "@@@@ pointF:" + pointF + " lastPointF:" + mLastPointF);
        mCheckHandler.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < BasePen.sHWPointsList.size(); i++) {
                    ArrayList<ControllerPoint> controllerPoints = BasePen.sHWPointsList.get(i);
                    if (controllerPoints.size() > 1) {
                        for (int j = 0; j < controllerPoints.size() - 1; j++) {
                            if (intersect(pointF, mLastPointF, controllerPoints.get(j).getPoint(), controllerPoints.get(j + 1).getPoint())) {
//                                Log.d("tag", "@@@@ check undo thread:" + Thread.currentThread());
                                BasePen.sHWPointsList.remove(i);
                                Handlers.postMain(new Runnable() {
                                    @Override
                                    public void run() {
                                        mMarkView.undo(false);
                                    }
                                });
                                return;
                            }
                        }
                    } else if (controllerPoints.size() == 1){
                        if (intersect(pointF, mLastPointF, controllerPoints.get(0).getPoint(), controllerPoints.get(0).getPoint())) {
//                            Log.d("tag", "@@@@ check undo thread:" + Thread.currentThread());
                            BasePen.sHWPointsList.remove(i);
                            Handlers.postMain(new Runnable() {
                                @Override
                                public void run() {
                                    mMarkView.undo(false);
                                }
                            });
                            return;
                        }
                    }
                }
                mLastPointF = pointF;
            }
        });
    }

    private double cross(PointF a, PointF b) {
        return (a.x * b.y - a.y * b.x);
    }

    private PointF getPoint(PointF a, PointF b) {
        return new PointF(a.x - b.x, a.y - b.y);
    }

    private boolean intersect(PointF s1, PointF e1, PointF s2, PointF e2) {
//        Log.d("tag", "@@@@ s1:" + s1 + " e1:" + e1 + " s2:" + s2 + " e2:" + e2 + " Thread:" + Thread.currentThread());
        //排斥检测
        if (Math.min(s1.y, e1.y) > Math.max(s2.y, e2.y) ||
                Math.max(s1.y, e1.y) < Math.min(s2.y, e2.y) ||
                Math.min(s1.x, e1.x) > Math.max(s2.x, e2.x) ||
                Math.max(s1.x, e1.x) < Math.min(s2.x, e2.x)) return false;
        //跨立检测
        return cross(getPoint(s1, e2), getPoint(s2, e2)) * cross(getPoint(e1, e2), getPoint(s2, e2)) <= 0 &&
                cross(getPoint(e2, s1), getPoint(e1, s1)) * cross(getPoint(s2, s1), getPoint(e1, s1)) <= 0;
    }

}
