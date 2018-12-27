package com.stroke;

import android.graphics.Region;

public abstract class DrawElement {
    /**************************************************************************
     * Property
     **************************************************************************/

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

    protected Region mRegion;

    /**
     * 获取Region
     *
     * @return
     */
    public Region getRegion() {
        return mRegion;
    }

    /**
     * 缩放比例
     */
    protected float Scale = 1f;

    /**
     * 获取缩放比例
     *
     * @return
     */
    public float getScale() {
        return Scale;
    }

    /**
     * 设置缩放比例
     *
     * @param scale
     */
    public void setScale(float scale) {
        Scale = scale;
    }


    /**
     * x坐标偏移
     */
    protected float OffsetX = 0f;

    /**
     * 获取x坐标偏移
     *
     * @return
     */
    public float getOffsetX() {
        return OffsetX;
    }

    /**
     * 设置x坐标偏移
     *
     * @param offsetX
     */
    public void setOffsetX(float offsetX) {
        OffsetX = offsetX;
    }

    /**
     * y坐标偏移
     */
    protected float OffsetY = 0f;

    /**
     * 获取y坐标偏移
     *
     * @return
     */
    public float getOffsetY() {
        return OffsetY;
    }

    /**
     * 设置y坐标偏移
     *
     * @param offsetY
     */
    public void setOffsetY(float offsetY) {
        OffsetY = offsetY;
    }
}
