package east.orientation.microlesson.update;

/**
 * Created by ljq on 2018/11/12.
 */

public class UploadInfo {
    public String type;
    public String clazz;// 分类
    public String free;// 0 免费  1 收费
    public String path;
    public String fileName;
    public long size;
    public long offset;
    public int length;
    public byte[] content;

    @Override
    public String toString() {
        return path + "\n" + type + "-" + fileName + "-" + size + "-" + offset + "-" + length + "-" + content;
    }
}
