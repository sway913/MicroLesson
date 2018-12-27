package com.stroke;

import com.stroke.common.CommonUtil;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Region;
import android.util.FloatMath;
import android.util.Log;

public class Eraser extends DrawElement {

    /**************************************************************************
     * Property
     **************************************************************************/

    /**
     * 擦除速度,Highest > Hig > Normal > Low > Lowest
     *
     * @author Administrator
     */
    public enum SpeedLevel {
        /**
         * 很快
         */
        Highest(0),

        /**
         * 较快
         */
        High(1),

        /**
         * 普通
         */
        Normal(2),

        /**
         * 较慢
         */
        Low(3),

        /**
         * 很慢
         */
        Lowest(4);

        private int value;

        private SpeedLevel(int val) {
            this.value = val;
        }

        public int getValue() {
            return this.value;
        }
    }

    /**
     * canvas对象
     */
    protected Canvas mCanvas;

    /**
     * 设置Canvas
     *
     * @param canvas
     */
    public void setCanvas(Canvas canvas) {
        this.mCanvas = canvas;
    }


    //////////////////////////////////////////////////////////////////
    /**
     * 橡皮擦宽度
     */
    protected float mStrokeWidth = MIN_STROKE_WIDTH;

    /**
     * 设置橡皮擦宽度
     *
     * @param width
     */
    public void setEraserWidth(float width) {
        if (width >= MIN_STROKE_WIDTH) {
            mStrokeWidth = width;
        }
    }

    /**
     * 获取橡皮擦宽度
     *
     * @return
     */
    public float getEraserWidth() {
        return mStrokeWidth;
    }

    /****************************************************/
    /**
     * 是否显示橡皮擦
     */
    protected boolean mVisiable = false;

    /**
     * 设置是否显示橡皮擦
     *
     * @param enable
     */
    public void setVisiable(boolean enable) {
        mVisiable = enable;
    }

    /**
     * 获取是否显示橡皮擦
     *
     * @return
     */
    public boolean getVisiable() {
        return mVisiable;
    }
    /****************************************************/

    /**
     * 当前擦除速度
     */
    protected SpeedLevel mLevel = SpeedLevel.Low;

    /**
     * 获取擦除速度
     *
     * @return
     */
    public SpeedLevel getSpeedLevel() {
        return mLevel;
    }

    /**
     * 设置擦除速度
     *
     * @param level
     */
    public void setSpeedLevel(SpeedLevel level) {
        if (level != mLevel) {
            mLevel = level;
            mSpeedTime = mSpeedTimes[mLevel.getValue()];
        }
    }

    /**************************************************************************
     * Variable
     **************************************************************************/
    /**
     * 橡皮擦最小宽度
     */
    public static final float MIN_STROKE_WIDTH = 8f;

    /**
     * 两个点的最小距离
     */
    public final static float MIN_DISTANCE = 10f;


    /**************************************************
     * protected variable
     *************************************************/

    protected Paint mPaint;


    /**************************************************
     * private variable
     *************************************************/
    private final static String TAG = "Eraser";

    /**
     * 是否第一个点
     */
    private boolean mFirstPoint = false;


    /**
     * 前一个点
     */
    private PointF mPrevPoint;

    /**
     * 垂直于mPrevPoint，与mPrevPoint距离为mStrokeWidth的点，方向为上
     */
    private PointF mPrevA;

    /**
     * 垂直于mPrevPoint，与mPrevPoint距离为mStrokeWidth的点，方向为下
     */
    private PointF mPrevB;

    /**
     * 速度对应的时间间隔数组
     */
    private int mSpeedTimes[] = {50, 100, 200, 500, 800};

    /**
     * 当前速度对应的时间间隔
     */
    private int mSpeedTime = 50;

    /**
     * 上一个点的时间戳
     */
    private long mPrevTime = 0;

    /**************************************************************************
     * Constructor
     **************************************************************************/
    /**
     * 构造函数
     */
    public Eraser() {
        initialize();
    }

    /**
     * 初始化
     */
    private void initialize() {

        initRegion();
        initPoint();
        initPaint();

        //初始化擦除速度
        setSpeedLevel(SpeedLevel.Low);
        //设置显示
        mVisiable = false;
    }

    /**
     * 初始化region
     */
    private void initRegion() {
        mRegion = new Region();
    }

    /**
     * 初始化paint
     */
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1f);
    }

    /**
     * 初始化point
     */
    private void initPoint() {
        mPrevPoint = new PointF();
        mPrevA = new PointF(-1f, -1f);
        mPrevB = new PointF(-1f, -1f);
    }


    /**************************************************************************
     * Private Methods
     **************************************************************************/
    /**
     * 计算当前点与上一个点的时间戳t，只有t大于设定的时间，才会处理擦除动作
     *
     * @return
     */
    protected boolean computeTimeSpan() {
        long curTime = System.currentTimeMillis();
        if (mPrevTime != 0) {
            int timeSpan = (int) (curTime - mPrevTime);

            if (timeSpan > mSpeedTime) {
                //Log.d(TAG, "computeTimeSpan,span="+timeSpan);
                mPrevTime = curTime;
                return true;
            }
        } else {
            mPrevTime = curTime;
        }


        return false;
    }


    /**
     * 计算擦除笔迹对应的区域r，改区域将用于判断是否与Stroke的区域相交
     *
     * @param curPoint
     * @return
     */
    protected boolean computeRegion(PointF curPoint) {

        /************************************************
         *
         * A----------------C
         * |                |
         * prev           current
         * |                |
         * B----------------D
         */

//		if(!end && !computeTimeSpan()){
//			return false;
//		}
        //向量
        PointF vec = new PointF(curPoint.x - mPrevPoint.x, curPoint.y - mPrevPoint.y);
        //向量长度
        float lenVec = (float) Math.sqrt(vec.x * vec.x + vec.y * vec.y);
        //垂直向量
        PointF perp = new PointF(vec.x * mStrokeWidth / lenVec, vec.y * mStrokeWidth / lenVec);

        //与curPoint垂直，距离curPoint为mStrokeWidth的点
        PointF pointC = new PointF(curPoint.x - perp.y, curPoint.y + perp.x);
        PointF pointD = new PointF(curPoint.x + perp.y, curPoint.y - perp.x);

        if (mPrevA.x == -1f) {
            mPrevA.set(mPrevPoint.x - perp.y, mPrevPoint.y + perp.x);
            mPrevB.set(mPrevPoint.x + perp.y, mPrevPoint.y - perp.x);
        }

        //计算path
        Path path = new Path();
        path.moveTo(mPrevA.x, mPrevA.y);
        path.lineTo(mPrevB.x, mPrevB.y);
        path.lineTo(pointD.x, pointD.y);
        path.lineTo(pointC.x, pointC.y);
        path.close();
        if (mVisiable) {
            mCanvas.drawPath(path, mPaint);
        }


        mPrevPoint.set(curPoint);
        mPrevA.set(pointC);
        mPrevB.set(pointD);

        //计算path的region
        return mRegion.setPath(path, CommonUtil.clip());


    }


    /**************************************************************************
     * Protected Methods
     **************************************************************************/


    /**************************************************************************
     * Public Methods
     **************************************************************************/
    /**
     * 添加点，计算Eraser笔迹对应的区域
     *
     * @param x
     * @param y
     * @param pressure
     * @param end
     * @return 返回true表示要计算Eraser的区域与Stroke的区域是否相交
     */
    public boolean addPoint(float x, float y, float pressure, boolean end) {

        boolean ret = false;

        PointF point = new PointF(x, y);

        if (!mFirstPoint) {
            mFirstPoint = true;
            //保存上一个点
            mPrevPoint.set(point);
            //上一个点的垂直点清零
            mPrevA.set(-1f, -1f);
            //保存上一个点的时间戳
            mPrevTime = System.currentTimeMillis();

        } else {
            if (!end) {
                //这里最好计算一下距离，而且添加点的时候最好把MotionEvent的History加进来
                if (CommonUtil.getDistance(point, mPrevPoint) >= MIN_DISTANCE &&
                        computeTimeSpan()) {
                    ret = computeRegion(point);
                }
            } else {

                ret = computeRegion(point);
                //重置第一个点的标记
                mFirstPoint = false;

            }

        }
        return ret;
    }


    /**
     * 添加点，不根据时戳，只根据距离计算Eraser笔迹对应的区域
     *
     * @param x
     * @param y
     * @param pressure
     * @param end
     * @return 返回true表示要计算Eraser的区域与Stroke的区域是否相交
     */
    public boolean addPointDistance(float x, float y, float pressure, boolean end) {

        PointF point = new PointF(x, y);

        if (!mFirstPoint) {
            mFirstPoint = true;
            // 保存上一个点
            mPrevPoint = point;
            // 上一个点的垂直点清零
            mPrevA.set(-1f, -1f);
            // 保存上一个点的时间戳
            mPrevTime = System.currentTimeMillis();

        } else {
            if (!end) {
                // 这里最好计算一下距离，而且添加点的时候最好把MotionEvent的History加进来
                if (CommonUtil.getDistance(point, mPrevPoint) >= (MIN_DISTANCE * Scale)) {
                    computeRegion(point);
                    return true;
                }
            } else {

                computeRegion(point);
                // 重置第一个点的标记
                mFirstPoint = false;
                return true;
            }

        }

        return false;
    }

}
