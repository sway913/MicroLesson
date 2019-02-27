package east.orientation.microlesson.app;

import android.app.Application;

import com.shuyu.gsyvideoplayer.cache.CacheFactory;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.squareup.leakcanary.LeakCanary;

import org.greenrobot.greendao.database.Database;

import east.orientation.microlesson.greendao.DaoMaster;
import east.orientation.microlesson.greendao.DaoSession;
import east.orientation.microlesson.greendao.helper.MySQLiteOpenHelper;
import east.orientation.microlesson.local.Common;
import east.orientation.microlesson.local.model.User;
import east.orientation.microlesson.socket.SocketManager;
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager;

/**
 * @author ljq
 * @date 2018/11/27
 */

public class MicroApp extends Application {
    public static User sUser;

    private static MicroApp sInstance;

    private MySQLiteOpenHelper mOpenHelper;
    private Database db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        sUser = new User();
        // 网络请求初始化
        SocketManager.getInstance().init();
        // 初始化database
        initDatabase();

//        PlayerFactory.setPlayManager(Exo2PlayerManager.class);//EXO模式
        //PlayerFactory.setPlayManager(SystemPlayerManager.class);//系统模式
//        PlayerFactory.setPlayManager(IjkPlayerManager.class);//ijk模式

//        CacheFactory.setCacheManager(ExoPlayerCacheManager.class);//exo缓存模式，支持m3u8，只支持exo
        //CacheFactory.setCacheManager(ProxyCacheManager.class);//代理缓存模式，支持所有模式，不支持m3u8等

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

    }

    public static synchronized MicroApp getInstance() {
        return sInstance;
    }

    private void initDatabase() {
        mOpenHelper = new MySQLiteOpenHelper(this, Common.DB_NAME, null);//new DaoMaster.DevOpenHelper(this, Common.DB_NAME, null);
        db = mOpenHelper.getEncryptedWritableDb(Common.DB_SCREAT);
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }
}
