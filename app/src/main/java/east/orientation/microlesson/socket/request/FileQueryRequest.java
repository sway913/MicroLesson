package east.orientation.microlesson.socket.request;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;

/**
 * @author ljq
 * @date 2018/12/10
 */

public class FileQueryRequest extends BytesRequest {
    private String type;//类型
    private String key;//文件关键字
    private String time;//时间

    public FileQueryRequest(String type, String key, String time) {
        this.type = type;
        this.key = key;
        this.time = time;
    }

    @Override
    public byte[] getContent() {
        try {
            String content = String.format("Orntcmd=filequery_type,data=%s,", type);
            if (!TextUtils.isEmpty(key)) {
                content += key + ",";
            }
            if (!TextUtils.isEmpty(time)) {
                content += time;
            }
            return content.getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
