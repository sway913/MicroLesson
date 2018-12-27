package east.orientation.microlesson.rxbus.event;

/**
 * Created by ljq on 2018/8/31.
 */

public class ResponseMessage {
    private String cmd;
    private boolean isSuccess;
    private String response;
    private byte[] bytes;

    public ResponseMessage(String cmd, boolean isSuccess, String response, byte[] bytes) {
        this.cmd = cmd;
        this.isSuccess = isSuccess;
        this.response = response;
        this.bytes = bytes;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
