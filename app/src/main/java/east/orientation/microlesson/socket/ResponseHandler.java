package east.orientation.microlesson.socket;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.io.UnsupportedEncodingException;

import east.orientation.microlesson.local.Common;
import east.orientation.microlesson.rxbus.RxBus;
import east.orientation.microlesson.rxbus.event.ResponseMessage;
import east.orientation.microlesson.update.UpdateManager;

/**
 * @author ljq
 * @date 2018/12/10
 */
public class ResponseHandler extends Handler {
    private static final String TAG = "ResponseHandler";

    static final int RESPONSE = 3 << 1;
    static final int REQUEST = 2 << 1;

    ResponseHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        byte[] bytes = (byte[]) msg.obj;
        try {
            if (Common.HEAD.equals(new String(bytes, 0, 4, "gbk"))) {
                String response;
                byte zero = 0;
                if (zero == bytes[bytes.length - 1]) {
                    response = new String(bytes, 0, bytes.length - 1, "gbk");
                } else {
                    response = new String(bytes, 0, bytes.length, "gbk");
                }
                String cmd = response.substring(response.indexOf("=") + 1, response.indexOf(","));
                int index = response.indexOf("=", response.indexOf("=") + 1) + 1;
                String isOk = response.substring(index, index + 1);

                Log.d(TAG, cmd + " - response :" + response);

                ResponseMessage responseMessage = new ResponseMessage(cmd, "1".equals(isOk), response, bytes);
                String type = "";
                if (cmd.equals(Common.CMD_FILE_QUERY_TYPE) || cmd.equals(Common.CMD_FILE_DOWN_TYPE)) {
                    int a0 = response.indexOf(",");
                    int a1 = response.indexOf(",", a0 + 1);
                    int a2 = response.indexOf(",", a1 + 1);

                    if (a1 > 0 && a2 > 0) {
                        type = response.substring(a1 + 1, a2);
                        if (type.equals(UpdateManager.TYPE_APK)) {
                            if (cmd.equals(Common.CMD_FILE_QUERY_TYPE))
                                UpdateManager.handleUpdate("1".equals(isOk), response);
                            else
                                UpdateManager.handleDownload("1".equals(isOk), response, bytes);
                        } else {
                            RxBus.getDefault().post(responseMessage);
                        }
                    }
                } else {
                    RxBus.getDefault().post(responseMessage);
                }
            }
        } catch (UnsupportedEncodingException e) {

        }
    }
}
