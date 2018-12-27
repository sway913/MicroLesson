package east.orientation.microlesson.socket.request;

import java.io.UnsupportedEncodingException;

/**
 * @author ljq
 * @date 2018/12/10
 */

public class FileQuerySynRequest extends BytesRequest {

    private String type;

    public FileQuerySynRequest(String type) {
        this.type = type;
    }

    @Override
    public byte[] getContent() {
        try {
            return String.format("Orntcmd=filequery_type,data=%s,P_", type).getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }
}
