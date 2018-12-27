package com.stroke;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.graphics.SPointF;


public abstract class Stroke extends DrawElement {
    /**************************************************************************
     * Enum
     **************************************************************************/

    /**
     * 绘图动作
     *
     * @author tonny
     */
    public enum DrawingAction {
        /**
         * 开始
         */
        Down(0),

        /**
         * 结束
         */
        Up(1),

        /**
         * 移动
         */
        Move(2);

        private int value;

        private DrawingAction(int val) {
            this.value = val;
        }

        public int getValue() {
            return this.value;
        }
    }

    /**
     * 笔型
     *
     * @author tonny
     */
    public enum StrokeStyle {
        /**
         * 铅笔
         */
        Pencil(0),
        /**
         * 圆珠笔
         */
        BallPoint(1),

        /**
         * 钢笔
         */
        Pen(2),

        /**
         * 水笔
         */
        FoutainPen(3),

        /**
         * 毛笔
         */
        Brush(4);


        private int value;

        private StrokeStyle(int val) {
            this.value = val;
        }

        public int getValue() {
            return this.value;
        }

    }

    /**
     * 渲染模式
     *
     * @author Administrator
     */
    public enum RenderStyle {
        /**
         * 笔锋
         */
        Sharp(0),

        /**
         * 笔锋
         */
        Round(1);

        private int value;

        private RenderStyle(int val) {
            this.value = val;
        }

        public int getValue() {
            return this.value;
        }


    }

    /**
     * 笔迹状态
     *
     * @author Administrator
     */
    public enum StrokeStatus {
        /**
         * 正常
         */
        Normal(0),
        /**
         * 准备擦除
         */
        Erasing(1),

        /**
         * 已擦除
         */
        Erased(2);

        private int value;

        private StrokeStatus(int val) {
            this.value = val;
        }

        public int getValue() {
            return this.value;
        }
    }


    /**************************************************************************
     * Property
     **************************************************************************/
    /**
     * 渲染模式
     */
    private RenderStyle mRenderStyle = RenderStyle.Sharp;

    /**
     * 获取渲染模式
     *
     * @return
     */
    public RenderStyle getRenderStyle() {
        return mRenderStyle;
    }

    public void setRenderStyle(RenderStyle style) {
        mRenderStyle = style;
    }

    /**
     * 笔型
     */
    private StrokeStyle mStrokeStyle;

    /**
     * 设置笔型
     *
     * @param style
     */
    public void setStrokeStyle(StrokeStyle style) {
        this.mStrokeStyle = style;
        this.mStrokeWidth = getWidth();
    }

    /**
     * 获取笔型
     *
     * @return
     */
    public StrokeStyle getStrokeStyle() {
        return this.mStrokeStyle;
    }

    /**
     * 颜色
     */
    protected int mColor = 0;

    /**
     * 设置颜色
     *
     * @param color
     */
    public void setColor(int color) {
        this.mColor = color;

        if (mPaint == null) {
            initPaint();
        } else {
            mPaint.setColor(mColor);
        }
    }

    /**
     * 获取颜色
     *
     * @return
     */
    public int getColor() {
        return this.mColor;
    }

    /**
     * Canvas对象
     */
    protected Canvas mCanvas = null;

    /**
     * 设置Canvas
     *
     * @param canvas
     */
    public void setCanvas(Canvas canvas) {
        if (canvas != null) {
            mCanvas = canvas;
        }

    }

    /**
     * 笔迹Id
     */
    protected int mId = -1;

    /**
     * 获取笔迹Id
     *
     * @return
     */
    public int getID() {
        return mId;
    }

    /**
     * 设置笔迹Id
     *
     * @param id
     */
    public void setID(int id) {
        this.mId = id;
    }


    /**
     * 笔迹状态
     */
    protected StrokeStatus mStatus;

    /**
     * 获取笔迹状态
     *
     * @return
     */
    public StrokeStatus getStatus() {
        return mStatus;
    }

    /**
     * 设置笔迹状态
     *
     * @param status
     */
    public void setStatus(StrokeStatus status) {
        mStatus = status;
    }

    /**
     * 获取坐标点数组
     *
     * @return
     */
    public List<SPointF> getSPointFList() {
        return mSPointFList;
    }

    /**
     * 设置坐标点数组
     *
     * @param spList
     */
    public void setSPointFList(List<SPointF> spList) {
        if (spList != null && spList.size() >= 2) {
            this.mSPointFList = new ArrayList<SPointF>(spList);
        }
    }
    /**************************************************************************
     * Variable
     **************************************************************************/
    /**
     * 画笔
     */
    protected Paint mPaint = null;

    /**
     * Stroke笔迹路径
     */
    protected Path mGlobalPath = null;

    /**
     * 笔迹宽度
     */
    protected float mStrokeWidth = 1f;
    /**
     * 坐标轨迹集合
     */
    protected ArrayList<SPointF> mSPointFList = null;

    /**
     * 笔型对应的笔迹宽度
     */
    protected float[] mStrokeWidths = {1f, 2f, 4f, 10f, 20f};

//	/**
//	 * 笔型对应的笔迹宽度
//	 */
//	protected float[] mStrokeWidths = { 1f, 3f, 6f, 12f, 24f };

    /**
     * 绘制点的计数索引
     */
    protected int mDrawIndex = 0;


    private final static String TAG = "Stroke";

    /**************************************************************************
     * Constructor
     **************************************************************************/
    /**
     * 构造函数
     */
    public Stroke() {
        initialize();
    }


    /**************************************************************************
     * Private Methods
     **************************************************************************/
    private void initialize() {
        //颜色初始化
        mColor = android.graphics.Color.BLACK;

        //笔型
        mStrokeStyle = StrokeStyle.Pen;

        //宽度
        mStrokeWidth = getWidth();

        //初始化画笔
        initPaint();

        //初始化坐标点集合
        if (mSPointFList == null) {
            mSPointFList = new ArrayList<SPointF>();
        }

        //笔迹路径
        if (mGlobalPath == null) {
            mGlobalPath = new Path();
        }

        //绘制点计数器
        mDrawIndex = 0;

        //id
        this.mId = -1;

        //状态
        mStatus = StrokeStatus.Normal;

    }


    private void initPaint() {
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setColor(this.mColor);
            mPaint.setStyle(Paint.Style.FILL);
        }

    }

    /**************************************************************************
     * Protected Methods
     **************************************************************************/
    /**
     * 获取笔迹宽度
     *
     * @return
     */
    public float getWidth() {
        return mStrokeWidth;
        //return getWidth(this.mStrokeStyle);
    }

    /**
     * 获取笔迹宽度
     *
     * @param style
     * @return
     */
    protected float getWidth(StrokeStyle style) {
        return mStrokeWidths[style.getValue()];
    }
    /**************************************************************************
     * Public Methods
     **************************************************************************/
    /**
     * add by ljq
     * 设置笔迹宽度
     *
     * @param width
     */
    public void setWidth(float width) {
        mStrokeWidth = width;
    }

    /**
     * 创建新的笔迹
     *
     * @param style 模式 笔锋 or not
     * @return
     */
    public static Stroke createStroke(RenderStyle style) {
        Stroke stroke = null;
        if (style == RenderStyle.Sharp) {
            stroke = new SharpStroke();
        } else {
            stroke = new RoundStroke();
        }

        return stroke;
    }

    /**
     * 添加坐标点数据并绘制
     *
     * @param x        x坐标
     * @param y        y坐标
     * @param pressure 压力值，必须大于0.001f，小于等于1.0f
     * @param end      是否是Stroke的最后一个点
     */
    public abstract void addPoint(float x, float y, float pressure, boolean end);

    /**
     * 添加坐标点集合并绘制
     *
     * @param spList 坐标点集合
     */
    public abstract void addPoints(LinkedList<SPointF> spList);

    /**
     * 用指定的Canvas重绘
     *
     * @param canvas
     */
    public abstract void onDraw(Canvas canvas);

    /**
     * 重绘
     */
    public abstract void invalidate();


    /**
     * 是否与Region相交
     *
     * @param rgn
     * @return
     */
    public abstract boolean intersect(Region rgn);


    /**
     * 重置绘图数据。当Stroke的Style发生改变时，重绘前调用此方法
     */
    public void resetDraw() {
        //当前绘制的点索引清零
        mDrawIndex = 0;
        //清除绘图路径
        mGlobalPath.reset();
        //region
        if (mRegion != null) {
            mRegion = null;
        }
    }

    /**
     * 重置绘图，清理点数据。当重新定义Stroke的点数据时，先调用此方法
     */
    public void reset() {
        resetDraw();
        //清除点集合
        mSPointFList.clear();
    }

    /**
     * 比较对象
     */
    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }

        if (!(object instanceof Stroke)) {
            return false;
        }

        if (this == object) {
            return true;
        }

        Stroke other = (Stroke) object;
        if (other.getID() == this.getID()) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return String.format("id: %d ", mId);
    }


}
