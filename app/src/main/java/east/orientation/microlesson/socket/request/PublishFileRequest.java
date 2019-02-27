package east.orientation.microlesson.socket.request;

import java.io.UnsupportedEncodingException;

/**
 * @author ljq
 * @date 2019/1/18
 * @description
 */

public class PublishFileRequest extends BytesRequest {
    private String clazz;// 学科分类
    private String type;// 类型
    private String fileName;// 文件名
    private String free;// 0/1 免费0，收费1

    public PublishFileRequest(String clazz, String type, String fileName, String free) {
        this.clazz = clazz;
        this.type = type;
        this.fileName = fileName;
        this.free = free;
    }

    @Override
    public byte[] getContent() {
        try {
            return String.format("Orntcmd=publishfile,data=class=%s,type=%s,filename=%s,fee=%s", clazz,type,fileName,free).getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }
}
