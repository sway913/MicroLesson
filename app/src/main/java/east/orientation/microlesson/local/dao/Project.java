package east.orientation.microlesson.local.dao;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author ljq
 * @date 2018/11/27
 */
@Entity
public class Project implements Parcelable {
    @Id(autoincrement = true)
    private Long id;
    private String ownerId;// project拥有人Id
    private String userId;// 用户id
    private String userName;// 用户
    private String title;// 标题
    private String fileName;// 文件名
    private String clazz;// 分类
    private String free;// 0 免费 1 收费
    private String thumbnail;// 封面
    private String filePath;// 视频路径
    private String time;//
    private long size;

    protected Project(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        ownerId = in.readString();
        userId = in.readString();
        userName = in.readString();
        title = in.readString();
        fileName = in.readString();
        clazz = in.readString();
        free = in.readString();
        thumbnail = in.readString();
        filePath = in.readString();
        time = in.readString();
        size = in.readLong();
    }

    @Generated(hash = 1796566676)
    public Project(Long id, String ownerId, String userId, String userName,
            String title, String fileName, String clazz, String free,
            String thumbnail, String filePath, String time, long size) {
        this.id = id;
        this.ownerId = ownerId;
        this.userId = userId;
        this.userName = userName;
        this.title = title;
        this.fileName = fileName;
        this.clazz = clazz;
        this.free = free;
        this.thumbnail = thumbnail;
        this.filePath = filePath;
        this.time = time;
        this.size = size;
    }

    @Generated(hash = 1767516619)
    public Project() {
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClazz() {
        return this.clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getFree() {
        return this.free;
    }

    public void setFree(String free) {
        this.free = free;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return userId + " - " +userName+" - "+fileName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(ownerId);
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeString(title);
        dest.writeString(fileName);
        dest.writeString(clazz);
        dest.writeString(free);
        dest.writeString(thumbnail);
        dest.writeString(filePath);
        dest.writeString(time);
        dest.writeLong(size);
    }

    public String getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
