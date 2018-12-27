package east.orientation.microlesson.socket.request;

import java.io.UnsupportedEncodingException;

/**
 * @author ljq
 * @date 2018/12/10
 */

public class GetidentityRequest extends BytesRequest {

    @Override
    public byte[] getContent() {
        byte[] content = new byte[0];
        try {
            content = String.format("Orntcmd=getidentity").getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return content;
    }
}
