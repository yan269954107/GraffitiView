package com.yanxw.graffiti.steel.config;

import java.util.ArrayList;

/**
 * PointsPath
 * Created by yanxinwei on 2019-06-30.
 */
public class PointsPath {

    private ArrayList<ControllerPoint> mPoints;
    private int color;
    private boolean isSaved = false;
    private boolean isClean = false;

    public PointsPath() {
    }

    public PointsPath(ArrayList<ControllerPoint> points) {
        mPoints = points;
    }

    public PointsPath(ArrayList<ControllerPoint> points, int color) {
        mPoints = points;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public ArrayList<ControllerPoint> getPoints() {
        return mPoints;
    }

    public void setPoints(ArrayList<ControllerPoint> points) {
        mPoints = points;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public boolean isClean() {
        return isClean;
    }

    public void setClean(boolean clean) {
        isClean = clean;
    }
}
