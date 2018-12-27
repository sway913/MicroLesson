package com.stroke;

import java.util.Stack;

import android.R.integer;

public class History {
    /**
     * 历史动作
     *
     * @author Administrator
     */
    public enum HistoryAction {
        /**
         * 添加
         */
        Add(0),
        /**
         * 删除
         */
        Delete(1);

        private int value;

        private HistoryAction(int val) {
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
     * 历史动作
     */
    protected HistoryAction mAction = HistoryAction.Add;

    /**
     * 获取当前取出的历史动作
     *
     * @return
     */
    public HistoryAction getAction() {
        return mAction;
    }


    protected Stroke mStroke = null;

    /**
     * 获取当前取出的Stroke
     *
     * @return
     */
    public Stroke getStroke() {
        return mStroke;
    }

    /**
     * 可存储上限
     */
    public static final int MAX_HISTORY_SIZE = 50;
    protected int mHistroySize = MAX_HISTORY_SIZE;

    /**
     * 设置历史的可存储上限
     *
     * @param size
     */
    public void setHistorySize(int size) {
        if (size > 0 && size <= MAX_HISTORY_SIZE) {
            mHistroySize = size;
        }
    }

    /**
     * 获取历史的可存储上限
     *
     * @return
     */
    public int getHistorySize() {
        return mHistroySize;
    }

    /**
     * 历史动作
     */
    protected Stack<HistoryAction> historyActions = new Stack<History.HistoryAction>();

    /**
     * 添加的历史笔迹
     */
    protected Stack<Stroke> addedStrokes = new Stack<Stroke>();

    /**
     * 删除的历史笔迹
     */
    protected Stack<Stroke> deletedStrokes = new Stack<Stroke>();
    /**************************************************************************
     * Variable
     **************************************************************************/


    /**************************************************************************
     * Constructor
     **************************************************************************/


    /**************************************************************************
     * Private Methods
     **************************************************************************/


    /**************************************************************************
     * Protected Methods
     **************************************************************************/


    /**************************************************************************
     * Public Methods
     **************************************************************************/
    /**
     * 把新增的笔迹添加到历史
     *
     * @param stroke
     */
    public void pushAdd(Stroke stroke) {
        if (historyActions.size() == mHistroySize) {
            historyActions.remove(0);
        }
        //if(historyActions.size() < mHistroySize){
        if (stroke.getID() != -1 && !addedStrokes.contains(stroke)) {
            historyActions.push(HistoryAction.Add);
            addedStrokes.add(stroke);
        }

        //}
    }

    /**
     * 把删除的笔迹添加到历史
     *
     * @param stroke
     */
    public void pushDel(Stroke stroke) {
        if (historyActions.size() == mHistroySize) {
            historyActions.remove(0);
        }
        //if(historyActions.size() < mHistroySize){
        if (stroke.getID() != -1 && !deletedStrokes.contains(stroke)) {
            historyActions.push(HistoryAction.Delete);
            deletedStrokes.add(stroke);
        }
        //}
    }

    /**
     * 取出历史
     *
     * @return
     */
    public boolean pop() {
        if (historyActions.size() > 0) {
            this.mAction = historyActions.pop();
            if (mAction == HistoryAction.Add) {
                mStroke = addedStrokes.pop();
            } else {
                mStroke = deletedStrokes.pop();
            }

            return true;
        }
        return false;
    }

    /**
     * 清除历史
     */
    public void clear() {
        historyActions.clear();
        addedStrokes.clear();
        deletedStrokes.clear();
        mStroke = null;
    }
}
