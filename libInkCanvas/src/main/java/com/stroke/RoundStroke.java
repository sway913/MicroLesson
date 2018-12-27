package com.stroke;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Region;
import android.graphics.SPointF;
import android.util.Log;

/**
 * 普通的没有笔锋的笔迹
 *
 * @author Administrator
 */
public class RoundStroke extends Stroke {

    /**************************************************************************
     * Property
     **************************************************************************/
    /**
     * 获取渲染模式
     */
    public RenderStyle getRenderStyle() {
        return RenderStyle.Round;
    }

    /**
     * 设置笔型
     *
     * @param style
     */
    public void setStrokeStyle(StrokeStyle style) {
        super.setStrokeStyle(style);
        if (mPaint != null) {
            mPaint.setStrokeWidth(getWidth());
        }
    }


    /**************************************************************************
     * Variable
     **************************************************************************/
    private static int MIN_DRAW_SIZE = 2;

    private float prevX, prevY;

    private int prevAction = 0;
    /**************************************************************************
     * Constructor
     **************************************************************************/
    /**
     * 构造函数
     */
    public RoundStroke() {
        super();
        initialize();
    }


    /**************************************************************************
     * Private Methods
     **************************************************************************/
    private void initialize() {
        initPaint();
    }

    /**
     * 初始化paint
     */
    private void initPaint() {
        if (mPaint != null) {
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(mStrokeWidth == 1 ? getWidth() : mStrokeWidth);


        }
    }


    /**
     * 绘制Stroke路径到画布
     */
    private void drawStroke() {
        mCanvas.drawPath(mGlobalPath, mPaint);
    }

    /**
     * 绘制Stroke路径到画布
     *
     * @param canvas
     */
    private void drawStroke(Canvas canvas) {
        canvas.drawPath(mGlobalPath, mPaint);
    }

    /**************************************************************************
     * Protected Methods
     **************************************************************************/
    /**
     * 绘制坐标点集合
     */
    protected void drawPoints() {
        int count = mSPointFList.size();
        if (count < MIN_DRAW_SIZE || mDrawIndex == count) {
            return;
        }

        resetDraw();

        Iterator<SPointF> iterator = mSPointFList.iterator();

        //the firs point
        SPointF spoint = iterator.next();

        mGlobalPath.moveTo(spoint.X, spoint.Y);

        prevX = spoint.X;
        prevY = spoint.Y;

        while (iterator.hasNext()) {
            spoint = iterator.next();
            float tempX = (prevX + spoint.X) / 2f;
            float tempY = (prevY + spoint.Y) / 2f;

            //绘制贝塞尔曲线
            mGlobalPath.quadTo(tempX, tempY, spoint.X, spoint.Y);

            //显示到画布
            mCanvas.drawPath(mGlobalPath, mPaint);

            prevX = spoint.X;
            prevY = spoint.Y;


            mDrawIndex++;
        }
    }

    /**************************************************************************
     * Public Methods
     **************************************************************************/
    /**
     * add by ljq
     * 设置笔记宽度
     *
     * @param width
     */
    public void setWidth(float width) {
        mStrokeWidth = width;
        mPaint.setStrokeWidth(mStrokeWidth);
    }

    /**
     * 添加坐标点数据并绘制
     *
     * @param x        x坐标
     * @param y        y坐标
     * @param pressure 压力值，必须大于0.001f，小于等于1.0f
     * @param end      是否是Stroke的最后一个点
     */
    @Override
    public void addPoint(float x, float y, float pressure, boolean end) {

        if (mCanvas == null || mSPointFList == null) {
            return;
        }
        // TODO Auto-generated method stub
        SPointF spoint = new SPointF(x, y, pressure);
        mSPointFList.add(spoint);

        int count = mSPointFList.size();

        if (!end && count == 1) {
            mGlobalPath.moveTo(x, y);
            prevX = x;
            prevY = y;

            mDrawIndex++;
        } else if (count >= MIN_DRAW_SIZE) {
            float tempX = (prevX + x) / 2f;
            float tempY = (prevY + y) / 2f;
            //绘制贝塞尔曲线
            mGlobalPath.quadTo(prevX, prevY, tempX, tempY);

            //显示到画布
            mCanvas.drawPath(mGlobalPath, mPaint);

            prevX = x;
            prevY = y;

            mDrawIndex++;
        }

    }

    /**
     * 添加坐标点集合并绘制
     *
     * @param spList 坐标点集合
     */
    @Override
    public void addPoints(LinkedList<SPointF> spList) {
        // TODO Auto-generated method stub
        if (spList == null || spList.size() < MIN_DRAW_SIZE) {
            return;
        }

        reset();

        mSPointFList = new ArrayList<SPointF>(spList);
        drawPoints();
    }

    /**
     * 重绘
     */
    @Override
    public void invalidate() {

        if (mCanvas == null) {
            return;
        }

        int count = mSPointFList.size();
        if (count < MIN_DRAW_SIZE) {
            return;
        }

        if (mDrawIndex < count) {
            drawPoints();
        } else {
            drawStroke();
        }
    }

    /**
     * 用指定的Canvas重绘
     *
     * @param canvas
     */
    @Override
    public void onDraw(Canvas canvas) {

        if (canvas == null) {
            return;
        }

        int count = mSPointFList.size();
        if (count < MIN_DRAW_SIZE) {
            return;
        }

        if (mDrawIndex < count) {
            drawPoints();
            drawStroke(canvas);
        } else {
            drawStroke(canvas);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (!(other instanceof RoundStroke)) {
            return false;
        }


        if (this == other) {
            return true;
        }


        RoundStroke stroke = (RoundStroke) other;
        if (stroke.getID() == this.getID()) {
            return true;
        }

        return false;
    }

    /**
     * 是否与Region相交
     *
     * @param rgn
     * @return
     */
    @Override
    public boolean intersect(Region rgn) {
        return false;
    }
}
