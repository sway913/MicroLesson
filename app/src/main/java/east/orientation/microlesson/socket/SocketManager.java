package east.orientation.microlesson.socket;

import android.content.Context;
import android.os.HandlerThread;
import android.os.Process;
import android.util.Log;


import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketFactory;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.LinkedBlockingQueue;


import east.orientation.microlesson.socket.request.BytesRequest;
import east.orientation.microlesson.socket.request.FileDelRequest;
import east.orientation.microlesson.socket.request.FileDelSynRequest;
import east.orientation.microlesson.socket.request.FileDownRequest;
import east.orientation.microlesson.socket.request.FileQueryRequest;
import east.orientation.microlesson.socket.request.FileQuerySynRequest;
import east.orientation.microlesson.socket.request.FileUpRequest;
import east.orientation.microlesson.socket.request.FileUpdateSynRequest;
import east.orientation.microlesson.socket.request.GetidentityRequest;
import east.orientation.microlesson.socket.request.LoginRequest;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;


/**
 * @author ljq
 * @date 2018/12/10
 */

public class SocketManager {
    private static final String TAG = "SocketManager";

    private static final String IP = "119.23.238.102";//"192.168.0.142" "119.23.238.102"
    private static final int PORT = 8888;

    private HandlerThread sHandlerThread;
    private ResponseHandler sResponseHandler;
    private SocketListener SocketListener;
    private IConnectionManager mConnectionManager;
    private ConnectionInfo mConnectionInfo;
    private OkSocketOptions mOkOptions;
    private SocketActionAdapter mSocketActionAdapter = new SocketActionAdapter() {
        @Override
        public void onSocketIOThreadStart(String action) {
            super.onSocketIOThreadStart(action);
            Log.d(TAG, "onSocketIOThreadStart");
        }

        @Override
        public void onSocketIOThreadShutdown(String action, Exception e) {
            super.onSocketIOThreadShutdown(action, e);
            Log.d(TAG, "onSocketIOThreadShutdown" + e);
        }

        @Override
        public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
            super.onSocketDisconnection(info, action, e);
            if (SocketListener != null) SocketListener.disconnected();
            Log.d(TAG, "onSocketDisconnection" + e);

        }

        @Override
        public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
            super.onSocketConnectionSuccess(info, action);
            if (SocketListener != null) SocketListener.connected();
            // 连接成功则发送心跳
            Log.d(TAG, "onSocketConnectionSuccess");

        }

        @Override
        public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
            super.onSocketConnectionFailed(info, action, e);
            if (SocketListener != null) SocketListener.connectFailed();
            Log.d(TAG, "onSocketConnectionFailed" + e);

        }

        @Override
        public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
            super.onSocketReadResponse(info, action, data);
            byte[] bytes = data.getBodyBytes();
            //Log.d(TAG, "receive size:" + bytes.length);
            sResponseHandler.obtainMessage(ResponseHandler.RESPONSE, bytes).sendToTarget();

        }

        @Override
        public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
            super.onSocketWriteResponse(info, action, data);
            //Log.d(TAG,"发送 ："+data.parse().length);
            sResponseHandler.obtainMessage(ResponseHandler.RESPONSE, data.parse()).sendToTarget();
        }

        @Override
        public void onPulseSend(ConnectionInfo info, IPulseSendable data) {
            super.onPulseSend(info, data);
            //Log.d(TAG,"心跳已发送");
        }
    };

    /**
     * 得到 Observable
     * @param <T> 指定的泛型类型
     * @return Observable
     */
    private static <T> Observable<T> createData(final T t) {
        return Observable.create(emitter -> {
            try {
                emitter.onNext(t);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    private SocketManager() {

    }

    public static SocketManager getInstance() {
        return Load.INSTANCE;
    }

    private static class Load {
        private static final SocketManager INSTANCE = new SocketManager();
    }

    public void init() {
        // 连接信息
        mConnectionInfo = new ConnectionInfo(IP, PORT);

        // 连接参数配置
        mOkOptions = new OkSocketOptions.Builder(OkSocketOptions.getDefault())
                .setWritePackageBytes(1024 * 1024)// 设置每个包的长度
                .setReadByteOrder(ByteOrder.LITTLE_ENDIAN)// 设置低位在前 高位在后
                .build();
        // 开启通道
        mConnectionManager = OkSocket.open(mConnectionInfo);

        // 设置当前连接的参数配置
        mConnectionManager.option(mOkOptions);
        // 注册回调
        mConnectionManager.registerReceiver(mSocketActionAdapter);
        // 发起连接
        if (!mConnectionManager.isConnect()) {
            mConnectionManager.connect();
        }

        // Starting thread Handler 开启handler线程
        sHandlerThread = new HandlerThread(SocketManager.class.getSimpleName(), Process.THREAD_PRIORITY_MORE_FAVORABLE);
        sHandlerThread.start();
        sResponseHandler = new ResponseHandler(sHandlerThread.getLooper());

    }

    public void registerSocketListener(SocketListener socketListener) {
        SocketListener = socketListener;
    }

    public void unregisterSocketListener() {
        if (SocketListener != null) SocketListener = null;
    }

    public void send(BytesRequest request) {
        if (mConnectionManager != null)
            mConnectionManager.send(request);
    }

    public boolean isConnect() {
        if (mConnectionManager != null)
            return mConnectionManager.isConnect();
        return false;
    }

    public void disConnect() {
        if (mConnectionManager != null) {
            mConnectionManager.disconnect();
            mConnectionManager.unRegisterReceiver(mSocketActionAdapter);
        }
    }

    public void connect() {
        if (mConnectionManager != null)
            mConnectionManager.connect();
    }

    /**
     * 登录命令
     *
     * @param account  账号q
     * @param password 密码
     * @return 发送字符串
     */
    public void login(String account, String password) {
        SocketManager.getInstance().send(new LoginRequest(account, password));
    }

    /**
     * 查询用户信息
     *
     * @return
     */
    public void getidentity() {
        SocketManager.getInstance().send(new GetidentityRequest());
    }

    /**
     * 上传命令
     *
     * @param fileName 文件名
     * @return 发送字符串
     */
    public void fileup(String type, String fileName, long size, long offset, int length, ByteBuffer data) {
        SocketManager.getInstance().send(new FileUpRequest(type, fileName, size, offset, length, data.array()));
    }

    /**
     * 查询命令 已下载文件无 A_/P_ 前缀
     *
     * @return
     */
    public void filequery(String type, String key, String time) {
        SocketManager.getInstance().send(new FileQueryRequest(type, key, time));
    }

    /**
     * 查询命令 查询新的文件
     *
     * @return
     */
    public void filequery_syn(String type) {
        SocketManager.getInstance().send(new FileQuerySynRequest(type));
    }

    /**
     * 下载命令
     *
     * @param fileName 文件名
     * @return
     */
    public void filedown(String type, String fileName, long offset, int length) {
        SocketManager.getInstance().send(new FileDownRequest(type, fileName, offset, length));
    }

    /**
     * 删除文件
     *
     * @param fileName 文件名
     * @return
     */
    public void filedel(String type, String fileName) {
        SocketManager.getInstance().send(new FileDelRequest(type, fileName));
    }

    /**
     * 删除消息
     *
     * @param fileName 文件名
     * @return
     */
    public void filedel_syn(String type, String fileName) {
        SocketManager.getInstance().send(new FileDelSynRequest(type, fileName));
    }

    /**
     * 下载成功回复
     *
     * @param fileName 文件名
     * @return
     */
    public void fileupdated_syn(String type, String fileName) {
        SocketManager.getInstance().send(new FileUpdateSynRequest(type, fileName));
    }

}
