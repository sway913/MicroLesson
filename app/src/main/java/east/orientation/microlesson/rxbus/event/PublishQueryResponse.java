package east.orientation.microlesson.rxbus.event;

/**
 * @author ljq
 * @date 2019/1/22
 * @description
 */

public class PublishQueryResponse {
    private String cmd;
    private String code;
    private String response;

    public PublishQueryResponse(String cmd, String code, String response) {
        this.cmd = cmd;
        this.code = code;
        this.response = response;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
