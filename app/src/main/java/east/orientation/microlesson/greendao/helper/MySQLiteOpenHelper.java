package east.orientation.microlesson.greendao.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;

import east.orientation.microlesson.greendao.DaoMaster;
import east.orientation.microlesson.greendao.ProjectDao;


/**
 * SQLiteOpenHelper
 */
public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {
    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        // 更新你需要更新的类
        MigrationHelper.migrate(db, ProjectDao.class);
    }
}
