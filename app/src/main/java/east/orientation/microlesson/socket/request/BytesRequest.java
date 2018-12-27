package east.orientation.microlesson.socket.request;


import com.xuhao.didi.core.iocore.interfaces.ISendable;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import east.orientation.microlesson.utils.BytesUtils;


/**
 * @author ljq
 * @date 2018/12/10
 */

public abstract class BytesRequest implements ISendable {

    public abstract byte[] getContent();

    @Override
    public byte[] parse() {
        byte[] body = getContent();
        ByteBuffer bb = ByteBuffer.allocate(4 + body.length);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put(BytesUtils.intToBytes(body.length));
        bb.put(body);
        return bb.array();
    }
}
