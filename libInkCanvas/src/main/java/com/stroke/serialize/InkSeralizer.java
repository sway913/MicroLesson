package com.stroke.serialize;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.protobuf.InvalidProtocolBufferException;
import com.stroke.RoundStroke;
import com.stroke.SharpStroke;
import com.stroke.Stroke;
import com.stroke.Stroke.StrokeStyle;
import com.stroke.common.FileUtil;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.SPointF;
import android.os.SystemClock;
import android.util.Log;

public class InkSeralizer {

    /**
     * 序列化笔迹数组到文件
     *
     * @param context
     * @param strokeList
     * @param filePath
     */
    public static void serializeStrokes(Context context, List<Stroke> strokeList, String filePath) {
        if (strokeList == null || strokeList.size() == 0) {
            FileUtil.delete(context, filePath);
            return;
        }

        StrokesProto.Strokes_.Builder strokesBuilder = StrokesProto.Strokes_.newBuilder();
        Log.e("Serialize", "start-- " + SystemClock.currentThreadTimeMillis());
        Iterator<Stroke> strokeIterator = strokeList.iterator();
        while (strokeIterator.hasNext()) {
            StrokesProto.Stroke_.Builder stBuilder = StrokesProto.Stroke_.newBuilder();

            Stroke stroke = strokeIterator.next();
            stBuilder.setId(stroke.getID());
            stBuilder.setColor(stroke.getColor());
            stBuilder.setWidth((int) stroke.getWidth());
            stBuilder.setRenderStyle(stroke.getRenderStyle() == Stroke.RenderStyle.Round ? StrokesProto.RenderStyle.Round : StrokesProto.RenderStyle.Sharp);
            stBuilder.setStyle(stroke.getStrokeStyle().getValue());
            stBuilder.setScale(stroke.getScale());
            stBuilder.setOffsetX(stroke.getOffsetX());
            stBuilder.setOffsetY(stroke.getOffsetY());

            List<SPointF> spointList = stroke.getSPointFList();

            Iterator<SPointF> spointIterator = spointList.iterator();
            while (spointIterator.hasNext()) {
                SPointF spoint = spointIterator.next();
                StrokesProto.SPointF_.Builder sfBuilder = StrokesProto.SPointF_.newBuilder();
                sfBuilder.setX(spoint.X);
                sfBuilder.setY(spoint.Y);
                sfBuilder.setPressure(spoint.Pressure);

                stBuilder.addSPointFlist(sfBuilder);
            }
            strokesBuilder.addStrokeList(stBuilder);
        }
        FileUtil.write(context, filePath, strokesBuilder.build().toByteArray());
        Log.e("Serialize", "end-- " + SystemClock.currentThreadTimeMillis());
    }

    /**
     * 从文件中读取数据，反序列化为stroke数组
     *
     * @param context
     * @param file
     * @param canvas
     * @return
     */
    public static List<Stroke> deserializeStrokes(Context context, String file,
                                                  Canvas canvas) {

        byte[] data = FileUtil.read(context, file);
        if (data.length == 0) {
            return null;
        }

        StrokesProto.Strokes_ strokes = null;
        try {
            strokes = StrokesProto.Strokes_.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        List<StrokesProto.Stroke_> stList = strokes.getStrokeListList();
        Iterator<StrokesProto.Stroke_> stListIterator = stList.iterator();

        List<Stroke> strokeList = new LinkedList<Stroke>();
        while (stListIterator.hasNext()) {
            StrokesProto.Stroke_ st = stListIterator.next();

            Stroke stroke;
            if (st.getRenderStyle() == StrokesProto.RenderStyle.Round) {
                stroke = new RoundStroke();
            } else {

                stroke = new SharpStroke();
            }
            stroke.setCanvas(canvas);

            stroke.setID(st.getId());
            stroke.setStrokeStyle(StrokeStyle.values()[st.getStyle()]);
            stroke.setColor(st.getColor());
            stroke.setWidth(st.getWidth());
            stroke.setRenderStyle(st.getRenderStyle() == StrokesProto.RenderStyle.Round ? Stroke.RenderStyle.Round : Stroke.RenderStyle.Sharp);
            stroke.setScale(st.getScale());
            stroke.setOffsetX(st.getOffsetX());
            stroke.setOffsetY(st.getOffsetY());

            List<SPointF> spointList = new ArrayList<SPointF>();
            List<StrokesProto.SPointF_> spList = st.getSPointFlistList();
            Iterator<StrokesProto.SPointF_> spIterator = spList.iterator();
            while (spIterator.hasNext()) {
                StrokesProto.SPointF_ sp = spIterator.next();

                SPointF spoint = new SPointF(sp.getX(), sp.getY(), sp.getPressure());

                spointList.add(spoint);
            }

            stroke.setSPointFList(spointList);
            strokeList.add(stroke);
        }

        return strokeList;
    }


}
