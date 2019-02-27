package east.orientation.microlesson.rxbus.event;

/**
 * Created by ljq on 2018/6/27.
 */

public class UpdateUserMessage {
    private String name;

    public UpdateUserMessage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
