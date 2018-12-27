package east.orientation.microlesson.socket.request;

import java.io.UnsupportedEncodingException;

/**
 * @author ljq
 * @date 2018/12/10
 */

public class LoginRequest extends BytesRequest {
    private String account;
    private String password;

    public LoginRequest(String account, String password) {
        this.account = account;
        this.password = password;
    }

    @Override
    public byte[] getContent() {
        byte[] content = new byte[0];
        try {
            content = String.format("Orntcmd=login,data=%s,%s", account, password).getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return content;
    }
}
