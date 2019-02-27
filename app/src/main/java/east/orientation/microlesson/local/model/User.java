package east.orientation.microlesson.local.model;

import java.io.Serializable;

/**
 * @author ljq
 * @date 2019/1/4
 * @description
 */

public class User implements Serializable {
    public String account = "unknow";// 未登录 default value
    public String name = "unknow";
    public String password;
    public String userId;
}
