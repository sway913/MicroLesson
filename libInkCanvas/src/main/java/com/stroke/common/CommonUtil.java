package com.stroke.common;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.Region;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;

public class CommonUtil {
    /**************************************************************************
     * Property
     **************************************************************************/

    private static Region mRegion = new Region();


    private static int mWidth = 0;
    private static int mHeight = 0;

    /**
     * 画布宽度
     *
     * @return
     */
    public static int getWidth() {
        return mWidth;
    }

    /**
     * 画布宽度
     *
     * @param width
     */
    public static void setWidth(int width) {
        if (width > 0) {
            mWidth = width;
        }
    }

    /**
     * 画布高度
     *
     * @return
     */
    public static int getHeight() {
        return mHeight;
    }

    /**
     * 画布高度
     *
     * @param height
     */
    public static void setHeight(int height) {
        if (height > 0) {
            mHeight = height;
        }
    }

    /**
     * 设置画布分辨率，必须在InkCanvas初始化前调用
     *
     * @param width
     * @param height
     */
    public static void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;

        setClip(width, height);
    }

    /**
     * 初始化画布分辨率，必须在InkCanvas初始化前调用
     *
     * @param activity
     */
    public static void initSize(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;
        Log.d("InkCanvas", String.format("width=%d,height=%d", mWidth, mHeight));
        setClip(mWidth, mHeight);
    }

    /**
     * 设置区域，用于橡皮擦
     *
     * @param width
     * @param height
     */
    public static void setClip(int width, int height) {
        if (width > 0 && height > 0) {
            mRegion.set(0, 0, width, height);
        }

    }

    /**
     * 设置Region
     */
    public static void setClip() {
        if (mWidth > 0 && mHeight > 0) {
            mRegion.set(0, 0, mWidth, mHeight);
        }
    }

    /**
     * 获取区域
     *
     * @return
     */
    public static Region clip() {
        return mRegion;
    }

    private static Context mContext = null;

    /**
     * 设置Context
     *
     * @param context
     */
    public static void setContext(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
    }

    public static String mCharsetName = "utf-8";

    /**
     * 设置编码
     *
     * @param charset
     */
    public static void setCharsetName(String charset) {
        mCharsetName = charset;
    }

    /**
     * 设置编码名称
     *
     * @return
     */
    public static String getCharsetName() {
        return mCharsetName;
    }

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
     * 计算两个坐标点的距离
     *
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @return
     */
    public static float getDistance(float x0, float y0, float x1, float y1) {
        float xVec0 = x1 - x0;
        float yVec0 = y1 - y0;
        return (float) Math.sqrt(xVec0 * xVec0 + yVec0 * yVec0);
    }

    /**
     * 计算两个坐标点的距离
     *
     * @param p0
     * @param p1
     * @return
     */
    public static float getDistance(PointF p0, PointF p1) {
        float x = p1.x - p0.x;
        float y = p1.y - p0.y;

        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 获取Wifi IP
     *
     * @return
     */
    public static String getLocalIP() {

        if (mContext == null) {
            return null;
        }

        // 获取wifi服务
        WifiManager wifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
        // 判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            //wifiManager.setWifiEnabled(true);
            return null;
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = String.format("%d.%d.%d.%d",
                (ipAddress & 0xFF),
                ((ipAddress >> 8) & 0xFF),
                ((ipAddress >> 16) & 0xFF),
                (ipAddress >> 24 & 0xFF)
        );
        return ip;
    }


    /**
     * 获取本机IP
     *
     * @return
     */
    public static String getDefaultIP() {
        String ip = null;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        ip = inetAddress.getHostAddress().toString();
                        if (!ip.contains("::")) {
                            return ip;
                        }
                    }
                }
            }
        } catch (SocketException ex) {
        }

        return ip;
    }


}
