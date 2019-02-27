package east.orientation.microlesson.socket.request;

import android.util.Log;

import java.io.UnsupportedEncodingException;

/**
 * @author ljq
 * @date 2018/12/10
 */

public class FileDownRequest extends BytesRequest {
    private String type;
    private String fileName;
    private long offset;
    private int length;
    private String userId;

    public FileDownRequest(String type, String fileName, long offset, int length, String userId) {
        this.type = type;
        this.fileName = fileName;
        this.offset = offset;
        this.length = length;
        this.userId = userId;
    }

    @Override
    public byte[] getContent() {
        try {
            return String.format("Orntcmd=filedown_type,data=%s,%s,%s,%s,%s", type, fileName, offset, length, userId).getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
