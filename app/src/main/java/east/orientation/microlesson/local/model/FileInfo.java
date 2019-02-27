package east.orientation.microlesson.local.model;

/**
 * Created by ljq on 2018/11/15.
 */

public class FileInfo {
    public String fileName;
    public long fileSize;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileInfo)) return false;

        FileInfo fileInfo = (FileInfo) o;

        if (fileSize != fileInfo.fileSize) return false;
        return fileName.equals(fileInfo.fileName);
    }

    @Override
    public int hashCode() {
        int result = fileName.hashCode();
        result = 31 * result + (int) (fileSize ^ (fileSize >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return fileName + " - " + fileSize;
    }
}
