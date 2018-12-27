package east.orientation.microlesson.socket;

/**
 * @author ljq
 * @date 2018/12/10
 */

public interface SocketListener {
    void connected();

    void connectFailed();

    void disconnected();
}
