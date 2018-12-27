package east.orientation.microlesson.local.model;

/**
 * @author ljq
 * @date 2018/12/25
 * @description
 */

public class LocalVoice {
    private String path;// 路径
    private int duration;// 时长
    private int page;// 当前页
    private boolean isPageFirst;// 是否当前页第一条

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public boolean isPageFirst() {
        return isPageFirst;
    }

    public void setPageFirst(boolean pageFirst) {
        isPageFirst = pageFirst;
    }
}
