package east.orientation.microlesson.local;

import android.os.Environment;

import java.io.File;

/**
 * Created by ljq on 2018/6/25.
 */

public class Common {
    public static final String APP_KEY = "MicroLesson";
    public static final String SAVE_DIR_NAME = "Projects";
    public static final String DRAFT_DIR = "Draft";

    /**********************************************************/
    /**接口格式相关常量**/
    /**********************************************************/
    public static final String HEAD = "Ornt";// 包头

    // 同步资源相关"192.168.0.139";//
    public static final String SYNC_SERVER_IP = "119.23.238.102";// 服务器ip
    public static final int SYNC_SERVER_PORT = 8888;// 服务器端口

    public static final String KEY_USER = "key_user";
    public static final String KEY_ACCOUNT = "key_account";
    public static final String KEY_PASSWD = "key_passwd";
    public static final String DEFAULT_ACOUNT = "unknow";

    public static final String CMD_LOGIN = "login";
    public static final String CMD_GETIDENTITY = "getidentity";
    public static final String CMD_FILE_UP = "fileup";
    public static final String CMD_FILE_DEL = "filedel";
    public static final String CMD_FILE_UP_TYPE = "fileup_type";
    public static final String CMD_FILE_QUERY_TYPE = "filequery_type";
    public static final String CMD_FILE_DOWN_TYPE = "filedown_type";
    public static final String CMD_FILE_DEL_TYPE = "filedel_type";
    public static final String CMD_FILE_RENAME_TYPE = "filerename_type";
    public static final String CMD_PUBLISH_FILE = "publishfile";
    public static final String CMD_PUBLISH_QUERY = "pubfile_query";

    public static final String TYPE_APK = "0";// apk更新
    public static final String TYPE_PREPARE_LESSONS = "1";// 备课
    public static final String TYPE_MICRO_LESSONS = "2";// 微课

    public static final int CMD_LOGIN_ID = 1;
    public static final int CMD_GETIDENTITY_ID = 10;
    public static final int CMD_FILE_UP_ID = 2;
    public static final int CMD_FILE_QUERY_ID = 3;
    public static final int CMD_FILE_DOWN_ID = 4;
    public static final int CMD_FILE_DEL_ID = 5;
    public static final int CMD_FILE_QUERY_SYN_ID = 6;
    public static final int CMD_FILE_DEL_SYN_ID = 7;
    public static final int CMD_FILE_UPDATED_SYN_ID = 8;
    public static final int RECONNECT_ID = 9;

    public static final int CMD_RESPONSE_SUCCESS = 1;
    public static final int CMD_RESPONSE_FAILED = 0;

    public static final int CMD_FILE_DOWN_HEAD = 1;
    public static final int CMD_FILE_DOWN_CONTENT = 2;
    public static final int CMD_FILE_DOWN_FINISH = 3;
    public static final int CMD_FILE_DOWN_ERROR = 4;


    public static final String KEY_PROJECTS = "projects";
    public static final String KEY_MEDIAS = "medias";
    public static final String KEY_FILE_PATH = "file_path";

    public static final int MAX_SELECTED_COUNT = 9;
    public static final int REQUEST_ADD_PICTURES = 199;
    public static final int REQUEST_CHANGE_PICTURE = 200;

    public static final String DB_NAME = "manager";
    public static final String DB_SCREAT = "123456";
}
