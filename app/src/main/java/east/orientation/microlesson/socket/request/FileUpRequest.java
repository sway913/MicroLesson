package east.orientation.microlesson.socket.request;

import java.io.UnsupportedEncodingException;


/**
 * @author ljq
 * @date 2018/12/10
 */

public class FileUpRequest extends BytesRequest {
    private String type;
    private String fileName;
    private long size;
    private long offset;
    private int length;
    private byte[] data;

    public FileUpRequest(String type, String fileName, long size, long offset, int length, byte[] data) {
        this.type = type;
        this.fileName = fileName;
        this.size = size;
        this.offset = offset;
        this.length = length;
        this.data = data;
    }

    @Override
    public byte[] getContent() {
        try {
            byte[] headBytes = String.format("Orntcmd=fileup_type,data=%s,%s,%s,%s,%s,", type, fileName, size, offset, length).getBytes("gbk");
            byte[] totalBytes = new byte[headBytes.length + data.length];

            System.arraycopy(headBytes, 0, totalBytes, 0, headBytes.length);
            System.arraycopy(data, 0, totalBytes, headBytes.length, data.length);

            //Log.i("Upload total",new String(totalBytes,"gbk"));
            return totalBytes;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
