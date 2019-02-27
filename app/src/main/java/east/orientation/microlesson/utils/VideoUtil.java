package east.orientation.microlesson.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author ljq
 * @date 2018/12/29
 * @description
 */

public class VideoUtil {
    /**
     * 得视频某一帧的缩略图
     *
     * @param videoPath 视频地址
     * @param timeUs 微秒，注意这里是微秒 1秒 = 1 * 1000 * 1000 微妙
     *
     * @return 截取的图片
     */
    public static Bitmap getVideoThumbnail(String videoPath, long timeUs) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        try {
            media.setDataSource(videoPath);
        } catch (RuntimeException e) {
            return null;
        }


        // 获取第一个关键帧
        // OPTION_CLOSEST 在给定的时间，检索最近一个帧，这个帧不一定是关键帧。
        // OPTION_CLOSEST_SYNC 在给定的时间，检索最近一个关键帧。
        // OPTION_NEXT_SYNC 在给定时间之后，检索一个关键帧。
        // OPTION_PREVIOUS_SYNC 在给定时间之前，检索一个关键帧。
        return media.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

        // 得到视频第一帧的缩略图
        // return media.getFrameAtTime();
    }
}
