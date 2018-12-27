package com.stroke;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import com.stroke.Stroke.StrokeStyle;
import com.stroke.common.CommonUtil;

import android.app.NativeActivity;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.SPointF;

import android.graphics.StrokeJNI;
import android.util.FloatMath;
import android.util.Log;

/**
 * 有笔锋的笔迹
 *
 * @author tonny
 */
public class SharpStroke extends Stroke {

    private static final boolean BINDING_FRAMEWORK = true;

//	static{
//		try {
//			
//			//load static library
//			System.loadLibrary("stroke");
//			
//			
//		} 
//		catch(UnsatisfiedLinkError e){
//            System.err.println("loadLibrary error:" + e.toString());
//        }
//
//	}
    /**************************************************************************
     * Property
     **************************************************************************/
    /**
     * 获取渲染模式
     */
    public RenderStyle getRenderStyle() {
        return RenderStyle.Sharp;
    }


    /**************************************************************************
     * Variable
     **************************************************************************/

    private final static String TAG = "SharpStroke";
    private StrokeJNI mStrokeJNI = null;
    /**************************************/
    //坐标、向量相关
    private static int MIN_DRAW_BEGIN_SIZE = 2;
    private static int MIN_DRAW_SIZE = 3;

    /**************************************************************************
     * Constructor
     **************************************************************************/
    /**
     * 构造函数
     */
    public SharpStroke() {
        super();

        mStrokeJNI = new StrokeJNI(mStrokeWidth);
        initialize();
    }


    /**************************************************************************
     * Private Methods
     **************************************************************************/
    private void initialize() {
    }


    /**
     * 初始化绘制
     *
     * @param sp0
     * @param sp1
     */
    private void drawBegin(SPointF sp0, SPointF sp1) {
        mStrokeJNI.drawBegin(sp0, sp1);
        //当前绘制索引
        mDrawIndex += 2;


    }

    /**
     * 绘制下一个点
     *
     * @param spoint
     */
    private void drawNext(SPointF spoint) {

        Path path = new Path();

        mStrokeJNI.drawNext(spoint, path);
        //绘制当前点的path
        mCanvas.drawPath(path, mPaint);
        //把当前点路径添加到stroke路径中
        mGlobalPath.addPath(path);

        //当前绘制索引增1
        mDrawIndex++;
    }

    /**
     * 绘制最后一个点
     *
     * @param spoint
     */
    private void drawEnd(SPointF spoint) {

        Path path = new Path();

        mStrokeJNI.drawEnd(spoint, path);
        //绘制当前点的path
        this.mCanvas.drawPath(path, mPaint);

        //把当前点路径添加到Stroke路径
        this.mGlobalPath.addPath(path);

        //当前绘制索引增1
        mDrawIndex++;
    }


    /**
     * 根据点集合绘制Stroke
     */
    private void drawPoints() {
        int count = mSPointFList.size();
        if (count < MIN_DRAW_SIZE || mDrawIndex == count) {
            return;
        }

        resetDraw();

        Iterator<SPointF> iterator = mSPointFList.iterator();
        while (iterator.hasNext()) {
            if (mDrawIndex == 0) {
                drawBegin(iterator.next(), iterator.next());
            } else if (mDrawIndex == count - 1) {
                drawEnd(iterator.next());
            } else {
                drawNext(iterator.next());
            }
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


    /**************************************************************************
     * Public Methods
     **************************************************************************/
    @Override
    public void setStrokeStyle(StrokeStyle style) {
        // TODO Auto-generated method stub
        super.setStrokeStyle(style);
        mStrokeJNI.setStrokeWidth(mStrokeWidth);
    }

    /**
     * add by ljq
     * 设置笔记宽度
     *
     * @param width
     */
    public void setWidth(float width) {
        mStrokeWidth = width;
        mStrokeJNI.setStrokeWidth(mStrokeWidth);
    }

    @Override
    public void setScale(float scale) {
        // TODO Auto-generated method stub
        super.setScale(scale);
        mStrokeJNI.setTransform(scale, OffsetX, OffsetY);
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
        // TODO Auto-generated method stub
        if (mCanvas == null || mSPointFList == null) {
            return;
        }

        SPointF spoint = new SPointF(x, y, pressure);
        mSPointFList.add(spoint);
        if (!end) {
            if (mSPointFList.size() == MIN_DRAW_BEGIN_SIZE) {
                drawBegin(mSPointFList.get(0), spoint);
            } else if (mSPointFList.size() >= MIN_DRAW_SIZE) {
                drawNext(spoint);
            }
        } else if (mSPointFList.size() >= MIN_DRAW_SIZE) {
            drawEnd(spoint);

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
        if (mCanvas == null || spList == null || spList.size() < MIN_DRAW_SIZE) {
            return;
        }
        //清除绘图数据
        reset();

        mSPointFList = new ArrayList<SPointF>(spList);

        drawPoints();
    }

    /**
     * 用指定的Canvas重绘
     *
     * @param canvas
     */
    public void onDraw(Canvas canvas) {
        if (mCanvas == null) {
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

    /**
     * 重绘
     */
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

//	/**
//	 * 重置绘图数据。当Stroke的Style发生改变时，重绘前调用此方法
//	 */
//    public void resetDraw()
//    {
//        //当前绘制的点索引清零
//        mDrawIndex = 0;
//        //清除绘图路径
//        mGlobalPath.reset();
//    }
//
//    /**
//     * 重置绘图，清理点数据。当重新定义Stroke的点数据时，先调用此方法
//     */
//    public void reset()
//    {
//        resetDraw();
//        //清除点集合
//        mSPointFList.clear();
//    }


    /**
     * 是否与Region相交
     *
     * @param rgn
     * @return
     */
    @Override
    public boolean intersect(Region rgn) {
        if (rgn == null || rgn.isEmpty()) {
            return false;
        }

        if (mSPointFList.size() < MIN_DRAW_SIZE) {
            return false;
        }

        if (mDrawIndex < mSPointFList.size()) {
            return false;
        }

        if (this.mRegion == null) {
            this.mRegion = new Region();
            mRegion.setPath(mGlobalPath, CommonUtil.clip());
        }

        Region dst = new Region(rgn);
        return dst.op(mRegion, Region.Op.INTERSECT);

    }


}
