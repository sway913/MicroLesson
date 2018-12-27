package east.orientation.microlesson.update;

import java.io.UnsupportedEncodingException;

import east.orientation.microlesson.socket.request.BytesRequest;


/**
 * Created by ljq on 2018/11/5.
 */

public class AppsQueryRequest extends BytesRequest {
    private String type;
    private String appKey;

    public AppsQueryRequest(String type, String appKey) {
        this.type = type;
        this.appKey = appKey;
    }

    @Override
    public byte[] getContent() {
        try {
            return String.format("Orntcmd=filequery_type,data=%s,%s", type, appKey).getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
