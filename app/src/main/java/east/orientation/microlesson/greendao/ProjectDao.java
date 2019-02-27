package east.orientation.microlesson.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import east.orientation.microlesson.local.dao.Project;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PROJECT".
*/
public class ProjectDao extends AbstractDao<Project, Long> {

    public static final String TABLENAME = "PROJECT";

    /**
     * Properties of entity Project.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property OwnerId = new Property(1, String.class, "ownerId", false, "OWNER_ID");
        public final static Property UserId = new Property(2, String.class, "userId", false, "USER_ID");
        public final static Property UserName = new Property(3, String.class, "userName", false, "USER_NAME");
        public final static Property Title = new Property(4, String.class, "title", false, "TITLE");
        public final static Property FileName = new Property(5, String.class, "fileName", false, "FILE_NAME");
        public final static Property Clazz = new Property(6, String.class, "clazz", false, "CLAZZ");
        public final static Property Free = new Property(7, String.class, "free", false, "FREE");
        public final static Property Thumbnail = new Property(8, String.class, "thumbnail", false, "THUMBNAIL");
        public final static Property FilePath = new Property(9, String.class, "filePath", false, "FILE_PATH");
        public final static Property Time = new Property(10, String.class, "time", false, "TIME");
        public final static Property Size = new Property(11, long.class, "size", false, "SIZE");
    }


    public ProjectDao(DaoConfig config) {
        super(config);
    }
    
    public ProjectDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PROJECT\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"OWNER_ID\" TEXT," + // 1: ownerId
                "\"USER_ID\" TEXT," + // 2: userId
                "\"USER_NAME\" TEXT," + // 3: userName
                "\"TITLE\" TEXT," + // 4: title
                "\"FILE_NAME\" TEXT," + // 5: fileName
                "\"CLAZZ\" TEXT," + // 6: clazz
                "\"FREE\" TEXT," + // 7: free
                "\"THUMBNAIL\" TEXT," + // 8: thumbnail
                "\"FILE_PATH\" TEXT," + // 9: filePath
                "\"TIME\" TEXT," + // 10: time
                "\"SIZE\" INTEGER NOT NULL );"); // 11: size
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PROJECT\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Project entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String ownerId = entity.getOwnerId();
        if (ownerId != null) {
            stmt.bindString(2, ownerId);
        }
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(3, userId);
        }
 
        String userName = entity.getUserName();
        if (userName != null) {
            stmt.bindString(4, userName);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(5, title);
        }
 
        String fileName = entity.getFileName();
        if (fileName != null) {
            stmt.bindString(6, fileName);
        }
 
        String clazz = entity.getClazz();
        if (clazz != null) {
            stmt.bindString(7, clazz);
        }
 
        String free = entity.getFree();
        if (free != null) {
            stmt.bindString(8, free);
        }
 
        String thumbnail = entity.getThumbnail();
        if (thumbnail != null) {
            stmt.bindString(9, thumbnail);
        }
 
        String filePath = entity.getFilePath();
        if (filePath != null) {
            stmt.bindString(10, filePath);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(11, time);
        }
        stmt.bindLong(12, entity.getSize());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Project entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String ownerId = entity.getOwnerId();
        if (ownerId != null) {
            stmt.bindString(2, ownerId);
        }
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(3, userId);
        }
 
        String userName = entity.getUserName();
        if (userName != null) {
            stmt.bindString(4, userName);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(5, title);
        }
 
        String fileName = entity.getFileName();
        if (fileName != null) {
            stmt.bindString(6, fileName);
        }
 
        String clazz = entity.getClazz();
        if (clazz != null) {
            stmt.bindString(7, clazz);
        }
 
        String free = entity.getFree();
        if (free != null) {
            stmt.bindString(8, free);
        }
 
        String thumbnail = entity.getThumbnail();
        if (thumbnail != null) {
            stmt.bindString(9, thumbnail);
        }
 
        String filePath = entity.getFilePath();
        if (filePath != null) {
            stmt.bindString(10, filePath);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(11, time);
        }
        stmt.bindLong(12, entity.getSize());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Project readEntity(Cursor cursor, int offset) {
        Project entity = new Project( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // ownerId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // userId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // userName
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // title
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // fileName
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // clazz
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // free
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // thumbnail
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // filePath
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // time
            cursor.getLong(offset + 11) // size
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Project entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setOwnerId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setUserId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setUserName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setTitle(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setFileName(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setClazz(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setFree(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setThumbnail(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setFilePath(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setTime(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setSize(cursor.getLong(offset + 11));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Project entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Project entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Project entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
