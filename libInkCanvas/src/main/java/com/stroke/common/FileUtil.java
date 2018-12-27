package com.stroke.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.R.string;
import android.content.Context;
import android.graphics.Bitmap;


public class FileUtil {

    /**
     * 删除文件
     *
     * @param context
     * @param file
     */
    public static boolean delete(Context context, String file) {
        return context.deleteFile(file);
    }

    /**
     * 文件是否存在
     *
     * @param context
     * @param fileName
     * @return
     */
    public static boolean exist(Context context, String fileName) {
        String filePath = context.getApplicationContext().getFilesDir().getAbsolutePath() + "/" + fileName;

        File file = new File(filePath);

        return file.exists();
    }

    /**
     * 读取二进制文件
     *
     * @param context
     * @param filePath
     * @return
     */
    public static byte[] read(Context context, String filePath) {
        FileInputStream iStream;
        try {
            iStream = context.openFileInput(filePath);
            byte[] data = new byte[iStream.available()];
            iStream.read(data, 0, data.length);
            iStream.close();
            return data;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new byte[0];
    }

    /**
     * 把二进制数据写入到文件
     *
     * @param context
     * @param filePath
     * @param data
     */
    public static void write(Context context, String filePath, byte[] data) {
        if (data == null || data.length == 0) {
            return;
        }

        try {
            FileOutputStream oStream = context.openFileOutput(filePath, Context.MODE_PRIVATE);
            oStream.write(data, 0, data.length);
            oStream.flush();
            oStream.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * 将图片存储为文件
     *
     * @param bitmap 图片
     */
    public static String createFile(Context context, Bitmap bitmap, String filename) {
        File f = new File(filename);
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

        } catch (IOException e) {
        } finally {
            //bitmap.recycle();
        }

        return f.getAbsolutePath();

    }

}
