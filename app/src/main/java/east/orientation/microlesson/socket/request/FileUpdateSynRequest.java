package east.orientation.microlesson.socket.request;

import android.util.Log;

import java.io.UnsupportedEncodingException;

/**
 * @author ljq
 * @date 2018/12/10
 */

public class FileUpdateSynRequest extends BytesRequest {
    private String type;
    private String fileName;

    public FileUpdateSynRequest(String type, String fileName) {
        this.type = type;
        this.fileName = fileName;
    }

    @Override
    public byte[] getContent() {
        try {
            Log.d("Rename request: ", String.format("Orntcmd=filerename_type,data=%s,P_%s,%s", type, fileName, fileName));
            return String.format("Orntcmd=filerename_type,data=%s,P_%s,%s", type, fileName, fileName).getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
