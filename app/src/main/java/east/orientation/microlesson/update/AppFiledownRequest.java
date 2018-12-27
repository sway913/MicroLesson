package east.orientation.microlesson.update;


import java.io.UnsupportedEncodingException;

import east.orientation.microlesson.socket.request.BytesRequest;


/**
 * Created by ljq on 2018/11/5.
 */

public class AppFiledownRequest extends BytesRequest {
    private String type;
    private String apkName;
    private long offset;
    private int length;

    public AppFiledownRequest(String type, String apkName, long offset, int length) {
        this.type = type;
        this.apkName = apkName;
        this.offset = offset;
        this.length = length;
    }

    @Override
    public byte[] getContent() {

        try {
            return String.format("Orntcmd=filedown_type,data=%s,%s,%s,%s", type, apkName, offset, length).getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
