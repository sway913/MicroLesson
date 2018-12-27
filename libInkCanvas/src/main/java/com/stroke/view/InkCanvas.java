package com.stroke.view;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EventListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.stroke.Eraser;
import com.stroke.History;

import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.graphics.SPointF;
import android.graphics.StrokeJNI;

import com.stroke.History.HistoryAction;
import com.stroke.Stroke;
import com.stroke.Stroke.DrawingAction;
import com.stroke.Stroke.RenderStyle;
import com.stroke.Stroke.StrokeStatus;
import com.stroke.Stroke.StrokeStyle;
import com.stroke.common.CommonUtil;
import com.stroke.common.FileUtil;
import com.stroke.serialize.InkSeralizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class InkCanvas extends View {

    /**
     * 绘图事件
     *
     * @author Administrator
     */
    public interface OnDrawingListener extends EventListener {

        /**
         * 绘图事件
         *
         * @param x
         * @param y
         * @param pressure
         * @param action
         */
        public void onDrawing(float x, float y, float pressure, DrawingAction action);
    }

    /**
     * 擦除事件
     *
     * @author Administrator
     */
    public interface OnErasingListener extends EventListener {

        /**
         * 擦除事件
         *
         * @param x
         * @param y
         * @param pressure
         * @param action
         */
        public void onErasing(float x, float y, float pressure, DrawingAction action);
    }
    /**************************************************************************
     * Enum
     **************************************************************************/
    /**
     * 编辑模式
     *
     * @author Administrator
     */
    public enum EditMode {
        /**
         * 普通绘图模式
         */
        Normal(0),

        /**
         * 橡皮擦模式
         */
        Eraser(1),

        /**
         *
         */
        Clone(2);

        private int value;

        private EditMode(int val) {
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
    protected RenderStyle mRenderStyle;

    /**
     * 设置渲染模式
     *
     * @param style
     */
    public void setRenderStyle(RenderStyle style) {
        this.mRenderStyle = style;
    }

    /**
     * 获取渲染模式
     *
     * @return
     */
    public RenderStyle getRenderStyle() {
        return mRenderStyle;
    }

    /**
     * 笔型
     */
    protected StrokeStyle mStrokeStyle;

    /**
     * 设置笔型
     *
     * @param style
     */
    public void setStrokeStyle(StrokeStyle style) {
        this.mStrokeStyle = style;
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
     * 画布宽高
     */
    protected int mCanvasWidth;
    protected int mCanvasHeight;
    /**
     * 颜色
     */
    protected int mColor;

    /**
     * add by ljq
     * 笔迹宽度
     */
    protected float mWidth = 2;

    /**
     * 设置颜色
     *
     * @param color
     */
    public void setColor(int color) {
        this.mColor = color;
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
     * 编辑模式
     */
    protected EditMode mEditMode;

    /**
     * 获取编辑模式
     *
     * @return
     */
    public EditMode getEditMode() {
        return mEditMode;
    }

    /**
     * 设置编辑模式
     *
     * @param mode
     */
    public void setEditMode(EditMode mode) {
        mEditMode = mode;
    }

    /**
     * 缩放比例
     */
    protected float mScale = 1f;

    /**
     * 获取缩放比例
     *
     * @return
     */
    public float getScale() {
        return mScale;
    }

    /**
     * 设置缩放比例
     *
     * @param scale
     */
    public void setScale(float scale) {
        if (scale > 0.01f) {
            mScale = scale;
            if (mEraser != null) {
                mEraser.setScale(scale);
            }
        }
    }

    protected boolean mTouchEnable = false;

    /**
     * 设置触摸是否可用
     *
     * @param enable
     */
    public void setTouchEnable(boolean enable) {
        if (mTouchEnable != enable) {
            mTouchEnable = enable;
        }
    }

    /**
     * 获取触摸是否被禁止
     *
     * @return
     */
    public boolean getTouchEnable() {
        return mTouchEnable;
    }

    /**
     * 获取Canvas对象
     *
     * @return
     */
    public Canvas getCanvas() {
        return mCanvas;
    }

    public int getCanvasWidth() {
        return mCanvasWidth;
    }

    public int getCanvasHeight() {
        return mCanvasHeight;
    }

    public void setCanvasWidth(int canvasWidth) {
        mCanvasWidth = canvasWidth;
    }

    public void setCanvasHeight(int canvasHeight) {
        mCanvasHeight = canvasHeight;
    }

    /**
     * 绘图事件
     */
    protected OnDrawingListener mDrawingListener = null;

    /**
     * 设置绘图事件
     *
     * @param listener
     */
    public void setOnDrawingListener(OnDrawingListener listener) {
        mDrawingListener = listener;
    }

    /**
     * 擦除事件
     */
    protected OnErasingListener mErasingListener = null;

    /**
     * 擦除事件
     *
     * @param listener
     */
    public void setOnErasingListener(OnErasingListener listener) {
        //mEraser.setOnErasingListener(listener);
        mErasingListener = listener;
    }
    /**************************************************************************
     * Variable
     **************************************************************************/
    /**
     * 两个点的最小距离
     */
    public final static float MIN_DISTANCE = 1f;

    /**
     * stroke最大id
     */
    public final static int MAX_STROKE_ID = 65535;

    /**************************************************
     * protected variable
     *************************************************/

    /**
     * Context对象
     */
    protected Context mContext = null;

    /**
     * Canvas对象
     */
    protected Canvas mCanvas = new Canvas();
    /**
     * 画布bitmap
     */
    protected Bitmap mBitmap = null;

    /**
     * 用于清除屏幕的Paint对象
     */
    protected Paint mClearPaint = new Paint();

    /**
     * 笔迹集合
     */
    protected LinkedList<Stroke> mStrokeList = null;

    /**
     * 橡皮擦对象
     */
    protected Eraser mEraser = new Eraser();

    /**
     * 历史对象
     */
    protected History mBackwardHistory = new History();

    /**
     * 用于前进的历史对象
     */
    protected History mForwareHistory = new History();

    /**
     * 保存数据的文件路径
     */
    protected static String mFilePath = /*"/sdcard/strokes";//*/"strokes.data";

    /**
     * 画布是否有新的变化
     */
    protected boolean hasChange;

    /**
     * 当前ID
     */
    protected int mCurrentId = 0;
    /**************************************************
     * private variable
     *************************************************/
    private final static String TAG = "InkCanvas";

    /**
     * 当前临时Stroke
     */
    private Stroke mStroke = null;

    /**
     * 上一个点的x坐标
     */
    private float prevX;

    /**
     * 上一个点的y坐标
     */
    private float prevY;


    /**************************************************************************
     * Constructor
     **************************************************************************/
    /**
     * 构造函数
     *
     * @param context
     */
    public InkCanvas(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        mContext = context;
        initialize();
    }

    /**
     * 构造函数
     *
     * @param context
     * @param attrs
     */
    public InkCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initialize();
    }

    /**************************************************************************
     * Private Methods
     **************************************************************************/
    /**
     * 初始化
     */
    private void initialize() {
        //编辑模式
        mEditMode = EditMode.Normal;
        //渲染模式
        mRenderStyle = RenderStyle.Sharp;
        //笔型
        mStrokeStyle = StrokeStyle.Pen;
        //颜色
        mColor = Color.BLACK;
        //笔迹集合
        mStrokeList = new LinkedList<Stroke>();

        initBackgroundPaint();
    }


    /**
     * 初始化，必须第一时间调用
     */
    public void init() {
        initCanvas();
        //橡皮擦
        mEraser.setCanvas(mCanvas);
        mEraser.setVisiable(true);
    }

    /**
     * 初始化Canvas
     */
    private void initCanvas() {
        //创建底层图像
        mBitmap = Bitmap.createBitmap(
                mCanvasWidth,
                mCanvasHeight,
                Bitmap.Config.ARGB_8888);
        mCanvas.setBitmap(mBitmap);
        mCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

        initErase();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e(TAG,"onSizeChange " + w + "/" + h + "     " + oldw + "/" + oldh);
        if (w > 0 && mCanvasWidth != w && mCanvasHeight != h) {
            mCanvasWidth = w;
            mCanvasHeight = h;
            initCanvas();
        }
    }

    /**
     * 清除画布，释放资源
     */
    protected void clearCanvas() {
        mBitmap.recycle();
        mBitmap = null;

        mEraseBitmap.recycle();
        mEraseBitmap = null;
    }

    /**
     * 擦除canvas内容
     */
    protected void refreshCanvas() {
        mClearPaint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR));
        mCanvas.drawPaint(mClearPaint);
        mClearPaint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC));

        //mCanvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);
    }

    /**
     * 绘制笔迹集合到画布
     */
    protected void drawStrokes() {
        for (Stroke stroke : mStrokeList) {
            if (stroke.getStatus() == StrokeStatus.Normal) {
                //重绘
                stroke.invalidate();
            }
        }
    }


    /**
     * 绘制笔迹集合到指定画布
     *
     * @param canvas
     */
    protected void drawStrokes(Canvas canvas) {
        for (Stroke stroke : mStrokeList) {
            if (stroke.getStatus() == StrokeStatus.Normal) {
                //重绘
                stroke.onDraw(canvas);
            }
        }
    }


    /**
     * 计算当前坐标点与上一个点的距离，并添加到笔迹中
     *
     * @param x        x坐标
     * @param y        y坐标
     * @param pressure 压力值
     * @param end      是否是笔迹的最后一个点
     */
    private void addPoint(float x, float y, float pressure, boolean end) {
        if (CommonUtil.getDistance(prevX, prevY, x, y) > MIN_DISTANCE) {
            mStroke.addPoint(x, y, pressure, end);
            prevX = x;
            prevY = y;

            if (mDrawingListener != null) {
                mDrawingListener.onDrawing(x, y, pressure, DrawingAction.Move);
            }
        }
    }


    /**
     * 创建Stroke对象并添加到列表
     *
     * @return Stroke对象
     */
    public Stroke newStroke() {
        //创建对象
        mStroke = Stroke.createStroke(mRenderStyle);
        //设置Canvas
        mStroke.setCanvas(mCanvas);
        //id
        mStroke.setID(gererateStrokeId());
        //笔型
        mStroke.setStrokeStyle(mStrokeStyle);
        // 笔迹宽度 add by ljq
        mStroke.setWidth(mEditMode == EditMode.Eraser ? mEraseWidth : mWidth);
        //颜色
        mStroke.setColor(mColor);

        //添加到笔迹集合
        addStroke(mStroke);
        //添加到历史
        mBackwardHistory.pushAdd(mStroke);

        return mStroke;
    }

    /**
     * 绘制模式下，处理触屏事件
     *
     * @param event
     */
    private void handleDrawing(MotionEvent event) {
        hasChange = true;
        float x = event.getX();
        float y = event.getY();
        float pressure = event.getPressure();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                //创建stroke对象
                newStroke();
                //添加点到stroke
                mStroke.addPoint(x, y, pressure, false);
                //复制
                if (mDrawingListener != null) {
                    mDrawingListener.onDrawing(x, y, pressure, DrawingAction.Down);
                }
                //保存到上一个点
                prevX = x;
                prevY = y;

                break;
            case MotionEvent.ACTION_MOVE:
                //历史轨迹点
                int n = event.getHistorySize();

                for (int i = 0; i < n; i++) {
                    //添加历史点
                    addPoint(event.getHistoricalX(i),
                            event.getHistoricalY(i),
                            event.getHistoricalPressure(i), false);
                }
                //添加当前点
                addPoint(x, y, pressure, false);
                //重绘
                this.invalidate();

                break;

            case MotionEvent.ACTION_UP:
                //添加最后一个点
                mStroke.addPoint(x, y, pressure, true);
                //复制
                if (mDrawingListener != null) {
                    mDrawingListener.onDrawing(x, y, pressure, DrawingAction.Up);
                }
                //重绘
                this.invalidate();
                // 添加外部接口
                if (mTouchListener != null) mTouchListener.up();
                break;
        }
    }

    /**
     * 擦除笔迹
     */
    private void eraseStroke() {
        for (Stroke stroke : mStrokeList) {
            //判断是否相交
            if (stroke.getStatus() == StrokeStatus.Normal &&
                    stroke.intersect(mEraser.getRegion())) {
                //设置状态为准备擦除
                stroke.setStatus(StrokeStatus.Erasing);
                //保存到历史
                mBackwardHistory.pushDel(stroke);
                //刷新界面
                refresh();
            }

        }
    }

    /**
     * 删除被擦除的笔迹
     */
    private void deleteErasedStrokes() {

        Iterator<Stroke> iterator = mStrokeList.iterator();
        while (iterator.hasNext()) {
            Stroke stroke = iterator.next();
            if (stroke.getStatus() == StrokeStatus.Erasing) {
                //设置状态为已擦除
                stroke.setStatus(StrokeStatus.Erased);
                //从笔迹列表中删除
                iterator.remove();
            }
        }
    }

    protected Canvas mEraseCanvas = new Canvas();
    protected Paint mErasePaint = new Paint();
    protected Paint mEraseClearPaint = new Paint();
    protected Bitmap mEraseBitmap;
    protected float mEraseWidth;

    private void initErase() {
        //创建底层图像
        mEraseBitmap = Bitmap.createBitmap(
                mCanvasWidth,
                mCanvasHeight,
                Bitmap.Config.ARGB_8888);
        mEraseCanvas.setBitmap(mEraseBitmap);
        mEraseCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

        mErasePaint.setAntiAlias(true);
        mErasePaint.setStyle(Paint.Style.STROKE);
        mErasePaint.setStrokeWidth(1f);
        mErasePaint.setColor(Color.LTGRAY);

        mEraseClearPaint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR));
    }

    private void handleErasing(MotionEvent event) {
        hasChange = true;
        float x = event.getX();
        float y = event.getY();
        float pressure = event.getPressure();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //
                mEraseCanvas.drawPaint(mEraseClearPaint);
                mEraseCanvas.drawCircle(event.getX(), event.getY(), mWidth / 2, mErasePaint);

                //创建stroke对象
                newStroke();
                //添加点到stroke
                mStroke.addPoint(x, y, pressure, false);
                //复制
                if (mDrawingListener != null) {
                    mDrawingListener.onDrawing(x, y, pressure, DrawingAction.Down);
                }
                //保存到上一个点
                prevX = x;
                prevY = y;

                break;
            case MotionEvent.ACTION_MOVE:

                mEraseCanvas.drawPaint(mEraseClearPaint);
                mEraseCanvas.drawCircle(event.getX(), event.getY(), mWidth / 2, mErasePaint);

                //历史轨迹点
//				int n = event.getHistorySize();
//
//				for (int i = 0; i < n; i++) {
//					//添加历史点
//					addPoint(event.getHistoricalX(i),
//							event.getHistoricalY(i),
//							event.getHistoricalPressure(i), false);
//				}
                //添加当前点
                addPoint(x, y, pressure, false);
                //重绘
                this.invalidate();

                break;

            case MotionEvent.ACTION_UP:

                mEraseCanvas.drawPaint(mEraseClearPaint);

                //添加最后一个点
                mStroke.addPoint(x, y, pressure, true);
                //复制
                if (mDrawingListener != null) {
                    mDrawingListener.onDrawing(x, y, pressure, DrawingAction.Up);
                }
                //重绘
                this.invalidate();

                // 添加外部接口
                if (mTouchListener != null) mTouchListener.up();
                break;
        }
    }

//	/**
//	 * 橡皮擦模式下，处理触屏事件
//	 * @param event
//	 */
//	private void handleErasing(MotionEvent event){
//		switch(event.getAction()){
//		case MotionEvent.ACTION_DOWN:
//			//添加第一个点
//			mEraser.addPoint(event.getX(), event.getY(), event.getPressure(), false);
//			if(mErasingListener != null){
//				mErasingListener.onErasing(event.getX(), event.getY(), event.getPressure(), DrawingAction.Down);
//			}
//
//			mEraseCanvas.drawPaint(mEraseClearPaint);
//			mEraseCanvas.drawCircle(event.getX(),event.getY(),30,mErasePaint);
//
//			break;
//		case MotionEvent.ACTION_MOVE:
//
//			mEraseCanvas.drawPaint(mEraseClearPaint);
//			mEraseCanvas.drawCircle(event.getX(),event.getY(),30,mErasePaint);
//
//			//添加move的点
//			boolean ret = mEraser.addPoint(event.getX(), event.getY(), event.getPressure(), false);
//			if(ret){
//				if(mEraser.getVisiable()){
//					//显示橡皮擦
//					invalidate();
//				}
//				//擦除笔迹
//				eraseStroke();
//
//				if(mErasingListener != null){
//					mErasingListener.onErasing(event.getX(), event.getY(), event.getPressure(), DrawingAction.Move);
//				}
//			}
//
//
//			break;
//
//		case MotionEvent.ACTION_UP:
//			//最后一个点
//			if(mEraser.addPoint(event.getX(), event.getY(), event.getPressure(), true)){
//				if(mEraser.getVisiable()){
//					invalidate();
//				}
//				//擦除笔迹
//				eraseStroke();
//
//				if(mErasingListener != null){
//					mErasingListener.onErasing(event.getX(), event.getY(), event.getPressure(), DrawingAction.Up);
//				}
//				//从列表中删除已擦除的笔迹
//				deleteErasedStrokes();
//
//			}
//			mEraseCanvas.drawPaint(mEraseClearPaint);
//
//			break;
//		}
//	}

    /**************************************************************************
     * Protected Methods
     **************************************************************************/
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        //super.onDraw(canvas);
        //drawStrokes(canvas);
        if (mBackground) {
            drawBackground();
        }

        canvas.drawBitmap(mBitmap, 0, 0, null);
        canvas.drawBitmap(mEraseBitmap, 0, 0, null);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //不处理手指触屏事件

        if (!mTouchEnable && event.getDevice().getSources() == InputDevice.SOURCE_TOUCHSCREEN) {
            return false;
        }

        if (mEditMode == EditMode.Normal) {
            handleDrawing(event);
        } else if (mEditMode == EditMode.Eraser) {
            handleErasing(event);
        }

        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch(ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //父容器禁止拦截
                if (ev.getDevice().getSources() != InputDevice.SOURCE_TOUCHSCREEN) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if(ev.getDevice().getSources() == InputDevice.SOURCE_TOUCHSCREEN) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    /**
     * 释放资源
     *
     * @param clean 为true时表示调用Bitmap.recycle()，一般为false
     */
    public void dispose(boolean clean) {
        //清除历史列表
        mBackwardHistory.clear();
        mForwareHistory.clear();
        //清除笔迹列表
        for (Stroke stroke : mStrokeList) {
            stroke.reset();
        }
        mStrokeList.clear();

        if (clean) {
            //清除画布
            clearCanvas();
        } else {
            //擦除画布
            refreshCanvas();
        }

    }

    /**
     * 清除绘图
     */
    public void clear() {
        //释放资源
        dispose(false);

        //刷新显示
        invalidate();
        //重置id
        mCurrentId = 0;
    }

    /**
     * 刷新
     */
    public void refresh() {
        refreshCanvas();
        drawStrokes();
        invalidate();
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
        mWidth = width;
        mEraseWidth = width;
    }

    /**
     * 设置橡皮宽度
     *
     * @param eraseWidth
     */
    public void setEraseWidth(float eraseWidth) {
        mEraseWidth = eraseWidth;
    }

    /**
     * 生成新的笔迹ID
     *
     * @return 笔迹ID
     */
    public int gererateStrokeId() {
        mCurrentId++;
        if (mCurrentId > MAX_STROKE_ID) {
            mCurrentId = 0;
        }

        return mCurrentId;
    }

    /**
     * 添加Stroke
     *
     * @param stroke Stroke对象
     */
    public void addStroke(Stroke stroke) {
        if (stroke == null) {
            return;
        }

        if (stroke.getID() == -1) {
            stroke.setID(gererateStrokeId());
        }

        if (!mStrokeList.contains(stroke)) {
            mStrokeList.add(stroke);
        }
    }

    /**
     * 移除Stroke
     *
     * @param stroke Stroke对象
     */
    public void removeStroke(Stroke stroke) {
        if (stroke == null || stroke.getID() == -1) {
            return;
        }

        int index = mStrokeList.indexOf(stroke);
        if (index != -1) {
            mStrokeList.remove(index);
        }
    }


//	/**
//	 * 绘制从客户端同步过来的笔迹
//	 * @param infoList
//	 */
//    public void drawSync(List<DrawingInfo> infoList){
//        for(DrawingInfo info:infoList){
//        	DrawingAction action = info.getAction();
//            if (action == DrawingAction.Down){
//                //创建stroke对象
//                newStroke();
//                //设置缩放比例
//                mStroke.setScale(mScale);
//
//                SPointF spoint = info.getSPointFList().get(0);
//                mStroke.addPoint(spoint.X, spoint.Y, spoint.Pressure, false);
//            }
//            else if (action == DrawingAction.Move){
//                if (mStroke != null){
//                    List<SPointF> spList = info.getSPointFList();
//                    for (SPointF spoint:spList){
//                        mStroke.addPoint(spoint.X, spoint.Y, spoint.Pressure, false);
//                        //重绘
//                        this.invalidate();
//                    }
//                }
//            }
//            else if (action == DrawingAction.Up)
//            {
//                if (mStroke != null)
//                {
//                    SPointF spoint = info.getSPointFList().get(0);
//                    mStroke.addPoint(spoint.X, spoint.Y, spoint.Pressure, true);
//                  //重绘
//                    this.invalidate();
//
//                    mStroke = null;
//                }
//            }
//        }
//    }

    /**
     * 用客户端同步过来的数据擦除笔迹
     *
     * @param spList 坐标点集合
     * @param action 动作
     */
    public void eraseSync(List<SPointF> spList, DrawingAction action) {
        if (action == DrawingAction.Down) {
            SPointF spoint = spList.get(0);
            mEraser.addPointDistance(spoint.X, spoint.Y, SPointF.MaxPressure, false);
        } else if (action == DrawingAction.Move) {
            for (SPointF spoint : spList) {
                boolean erase = mEraser.addPointDistance(spoint.X, spoint.Y, SPointF.MaxPressure, false);
                if (erase) {
                    eraseStroke();
                }
            }

        } else if (action == DrawingAction.Up) {
            SPointF spoint = spList.get(0);
            boolean erase = mEraser.addPointDistance(spoint.X, spoint.Y, SPointF.MaxPressure, true);
            if (erase) {
                eraseStroke();
                deleteErasedStrokes();
            }
        }
    }

    /**
     * 打开或关闭橡皮擦模式
     *
     * @param enable
     */
    public void setErasing(boolean enable) {
        if (enable) {
            setEditMode(EditMode.Eraser);
            setRenderStyle(RenderStyle.Round);
        } else {
            setEditMode(EditMode.Normal);
            setRenderStyle(RenderStyle.Sharp);
        }
    }

    /**
     * 回退
     */
    public void backward() {
        if (mBackwardHistory.pop()) {
            HistoryAction action = mBackwardHistory.getAction();
            Stroke stroke = mBackwardHistory.getStroke();
            if (action == HistoryAction.Add) {
                mForwareHistory.pushDel(stroke);
                removeStroke(mBackwardHistory.getStroke());
            } else {
                stroke.setStatus(StrokeStatus.Normal);
                mForwareHistory.pushAdd(stroke);
                addStroke(stroke);
            }

            refresh();
        }
    }

    /**
     * 前进
     */
    public void forward() {
        if (mForwareHistory.pop()) {
            HistoryAction action = mForwareHistory.getAction();
            Stroke stroke = mForwareHistory.getStroke();
            if (action == HistoryAction.Add) {
                mBackwardHistory.pushDel(stroke);
                removeStroke(mForwareHistory.getStroke());
            } else {
                stroke.setStatus(StrokeStatus.Normal);
                mBackwardHistory.pushAdd(stroke);
                addStroke(stroke);
            }

            refresh();
        }
    }

    /**
     * 保存笔迹到文件
     */
    public void save() {
        // 释放bitmap
        clearCanvas();
        InkSeralizer.serializeStrokes(mContext, mStrokeList, mFilePath);
    }

    /**
     * 保存笔迹到指定文件
     *
     * @param filePath 文件名
     */
    public void saveStrokesAsFile(String filePath) {
        if (null == filePath || filePath.isEmpty()) {
            return;
        }

        InkSeralizer.serializeStrokes(mContext, mStrokeList, filePath);
    }

    public void saveCanvasAsPicSync(final String fileName) {
        hasChange = false;
        new Thread() {
            @Override
            public void run() {
                super.run();
                int width = getWidth();
                int height = getHeight();
                if (width <= 0 && height <= 0) {
                    return;
                }
                Bitmap bitmap = Bitmap.createBitmap(mCanvasWidth, mCanvasHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                canvas.drawColor(Color.WHITE);
                draw(canvas);
                FileUtil.createFile(mContext, bitmap, fileName);
                Log.d(TAG, "sync save : " + fileName);
            }
        }.start();
    }

    public void saveCanvasAsPic(String fileName) {
        int width = getWidth();
        int height = getHeight();
        if (width <= 0 && height <= 0) {
            return;
        }
        Bitmap bitmap = Bitmap.createBitmap(mCanvasWidth, mCanvasHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        draw(canvas);
        FileUtil.createFile(mContext, bitmap, fileName);

    }

    /**
     * 加载笔迹
     */
    public void load() {
        loadFrom(mFilePath);
    }

    /**
     * 从指定文件中加载笔迹
     *
     * @param filePath 文件名称
     */
    public void loadFrom(String filePath) {
        if (mContext != null && mCanvas != null && FileUtil.exist(mContext, filePath)) {
            List<Stroke> strokeList = InkSeralizer.deserializeStrokes(mContext, filePath, mCanvas);
            if (strokeList != null) {
                mStrokeList = new LinkedList<Stroke>(strokeList);
                //重设Id
                for (Stroke stroke : mStrokeList) {
                    stroke.setID(getId());
                }
                //显示
                refresh();

            }
        }
    }

    /**
     * 从指定文件读取图片
     *
     * @param filePath
     */
    public void loadFromPic(final String filePath) {
        clear();
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);

                if (bitmap != null) {
                    drawPicBackGround(bitmap, getWidth(), getHeight());
                    Log.d(TAG, "load :" + filePath);
                    postInvalidate();
                } else {

                    Log.d(TAG, "null");

                }

            }
        }).subscribeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).delay(50, TimeUnit.MILLISECONDS).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer s) throws Exception {

            }
        });
    }

    /**
     * 初始化注册服务器
     *
     * @return
     */
    public boolean initEncryptServer() {
        String ip = CommonUtil.getLocalIP();
        if (ip != null) {
            return StrokeJNI.native_encrypt_server_init(ip, 32562);
        }

        return false;
    }

    /**
     * 关闭注册服务器
     */
    public void closeEncryptServer() {
        StrokeJNI.native_encrypt_server_close();
    }

    private Paint mBackPaint = null;
    private boolean mBackground = false;
    private int mBackgroundColumn = 15;

    private void initBackgroundPaint() {
        if (mBackPaint == null) {
            mBackPaint = new Paint();
            mBackPaint.setAntiAlias(true);
            mBackPaint.setColor(Color.RED);
            mBackPaint.setStyle(Paint.Style.STROKE);
            mBackPaint.setStrokeWidth(1f);
        }
    }

    public void drawBackground() {
//		float width = (float) CommonUtil.getWidth() / (float)mBackgroundColumn;
//
//		float x0 = width/2f, y0= 0;
//		float x1 = x0, y1 = CommonUtil.getHeight();
//
//		for(int i = 0; i < mBackgroundColumn; i++){
//			mCanvas.drawLine(x0, y0, x1, y1, mBackPaint);
//
//			x0 += width;
//			x1 = x0;
//		}

        float height = (float) mCanvasHeight / (float) mBackgroundColumn;

        float y0 = height, x0 = 0;
        float y1 = y0, x1 = mCanvasWidth;

        for (int i = 0; i < mBackgroundColumn; i++) {
            mCanvas.drawLine(x0, y0, x1, y1, mBackPaint);

            y0 += height;
            y1 = y0;
        }
    }

    public void drawPicBackGround(final String filePath, final int width, final int height) {
        Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> emitter) throws Exception {
                Log.e("tag", "draw :"+filePath);

                BitmapFactory.Options opt = new BitmapFactory.Options();
                // 这个isjustdecodebounds很重要
                opt.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(filePath, opt);
                Bitmap bm;

                // 获取到这个图片的原始宽度和高度
                int picWidth = opt.outWidth;
                int picHeight = opt.outHeight;

                // isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
                opt.inSampleSize = 1;
                // 根据屏的大小和图片大小计算出缩放比例
                if (picWidth > picHeight) {
                    if (picWidth > width)
                        opt.inSampleSize = picWidth / width;
                } else {
                    if (picHeight > height)

                        opt.inSampleSize = picHeight / height;
                }
                Log.e(TAG,"sample size "+opt.inSampleSize);
                // 这次再真正地生成一个有像素的，经过缩放了的bitmap
                opt.inJustDecodeBounds = false;
                opt.inSampleSize = opt.inSampleSize*2;
                opt.inPreferredConfig = Bitmap.Config.RGB_565;
                opt.inPurgeable = true;
                opt.inInputShareable = true;
                bm = BitmapFactory.decodeFile(filePath,opt);
                //postInvalidate();
                emitter.onNext(bm);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bm) throws Exception {
                        if (bm == null) return;
                        int w = bm.getWidth();
                        int h = bm.getHeight();
                        float left, top, right, bottom;
                        if (w > h) {
                            right = width;
                            bottom = h * width / w;
                        } else {
                            bottom = height;
                            right = w * height / h;
                        }

                        RectF rectF = new RectF(0, 0, right, bottom);
                        refreshCanvas();
                        mCanvas.drawBitmap(bm, null, rectF, new Paint());
                        invalidate();
                        bm.recycle();
                        bm = null;
                        hasChange = true;
                    }
                });

//        Flowable.create(new FlowableOnSubscribe<String>() {
//            @Override
//            public void subscribe(FlowableEmitter<String> emitter) throws Exception {
//                Log.e("tag", "draw :"+filePath);
//
//                BitmapFactory.Options opt = new BitmapFactory.Options();
//                // 这个isjustdecodebounds很重要
//                opt.inJustDecodeBounds = true;
//                Bitmap bm = BitmapFactory.decodeFile(filePath, opt);
//
//                // 获取到这个图片的原始宽度和高度
//                int picWidth = opt.outWidth;
//                int picHeight = opt.outHeight;
//
//                // isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
//                opt.inSampleSize = 1;
//                // 根据屏的大小和图片大小计算出缩放比例
//                if (picWidth > picHeight) {
//                    if (picWidth > width)
//                        opt.inSampleSize = picWidth / width;
//                } else {
//                    if (picHeight > height)
//
//                        opt.inSampleSize = picHeight / height;
//                }
//                Log.e(TAG,"sample size "+opt.inSampleSize);
//                // 这次再真正地生成一个有像素的，经过缩放了的bitmap
//                opt.inJustDecodeBounds = false;
//
//                bm = BitmapFactory.decodeFile(filePath,opt);
//                if (bm == null) return;
//                int w = bm.getWidth();
//                int h = bm.getHeight();
//                float left, top, right, bottom;
//                if (w > h) {
//                    right = width;
//                    bottom = h * width / w;
//                } else {
//                    bottom = height;
//                    right = w * height / h;
//                }
//
//                RectF rectF = new RectF(0, 0, right, bottom);
//                mCanvas.drawBitmap(bm, null, rectF, new Paint());
//
//                postInvalidate();
//                //emitter.onNext(filePath);
//            }
//        }, BackpressureStrategy.ERROR)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<String>() {
//                    @Override
//                    public void onSubscribe(Subscription s) {
//
//                    }
//
//                    @Override
//                    public void onNext(String path) {
//                        hasChange = true;
//                        invalidate();
//                    }
//
//                    @Override
//                    public void onError(Throwable t) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
    }

    public void drawPicBackGround(Bitmap bitmap, int width, int height) {
        if (bitmap == null) return;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float left, top, right, bottom;
        if (w > h) {
            right = width;
            bottom = h * width / w;
        } else {
            bottom = height;
            right = w * height / h;
        }

        RectF rectF = new RectF(0, 0, right, bottom);
        mCanvas.drawBitmap(bitmap, null, rectF, new Paint());
        hasChange = true;
        invalidate();
    }

    public void enableBackground(boolean enable) {
        mBackground = enable;
    }

    public void setBackgroundColumn(int column) {
        if (column > 0 && column <= 20) {
            mBackgroundColumn = column;
        }
    }

    public boolean isHasChange() {
        return hasChange;
    }

    public void setHasChange(boolean hasChange) {
        this.hasChange = hasChange;
    }

    private TouchListener mTouchListener;

    public void setTouchListener(TouchListener touchListener) {
        mTouchListener = touchListener;
    }

    public interface TouchListener {
        void down();

        void move();

        void up();
    }
}
