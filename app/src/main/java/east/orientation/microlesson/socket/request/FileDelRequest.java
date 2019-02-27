package east.orientation.microlesson.socket.request;

import java.io.UnsupportedEncodingException;

/**
 * @author ljq
 * @date 2018/12/10
 */

public class FileDelRequest extends BytesRequest {
    private String type;
    private String fileName;

    public FileDelRequest(String type, String fileName) {
        this.type = type;
        this.fileName = fileName;
    }

    @Override
    public byte[] getContent() {
        try {
            return String.format("Orntcmd=filedel_type,data=%s,%s", type, fileName).getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
