package east.orientation.microlesson.socket.request;

import java.io.UnsupportedEncodingException;

/**
 * @author ljq
 * @date 2019/1/18
 * @description
 */

public class PublishFileQueryRequest extends BytesRequest {
    private String clazz;
    private String type;
    private String fileName;
    private String userId;
    private String free;

    public PublishFileQueryRequest(String clazz, String type, String fileName, String userId, String free) {
        this.clazz = clazz;
        this.type = type;
        this.fileName = fileName;
        this.userId = userId;
        this.free = free;
    }

    @Override
    public byte[] getContent() {
        try {
            return String.format("Orntcmd=pubfile_query,data=class=%s,type=%s,filename=%s,userid=%s,fee=%s", clazz,type,fileName,userId,free).getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }
}
