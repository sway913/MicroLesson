package east.orientation.microlesson.greendao.helper;

import android.text.TextUtils;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

import east.orientation.microlesson.app.MicroApp;
import east.orientation.microlesson.greendao.ProjectDao;
import east.orientation.microlesson.local.dao.Project;

/**
 * @author ljq
 * @date 2019/1/18
 * @description
 */

public class DatabaseUtil {
    private static ProjectDao sProjectDao = MicroApp.getInstance().getDaoSession().getProjectDao();

    public static long insertProject(Project project) {

        return sProjectDao.insertOrReplace(project);
    }

    public static void updateProject(Project project) {
        sProjectDao.update(project);
    }

    public static Project getProject(Long id) {
        Project project = sProjectDao.queryBuilder()
                .where(ProjectDao.Properties.Id.eq(id)).unique();
        return project;
    }

    public static Project getProject(String ownerId,String fileName) {
        return sProjectDao.queryBuilder()
                .where(ProjectDao.Properties.OwnerId.eq(ownerId),
                        ProjectDao.Properties.FileName.eq(fileName)).unique();
    }

    public static Project exist(Project project,String ownerId) {
        Project p = sProjectDao.queryBuilder().where(ProjectDao.Properties.OwnerId.eq(ownerId),
                ProjectDao.Properties.UserId.eq(project.getUserId()),
                ProjectDao.Properties.FileName.eq(project.getFileName())).unique();
        if (p != null)
            return p;

        return null;
    }

    public static List<Project> getProjects(String ownerId) {
        return sProjectDao.queryBuilder().where(ProjectDao.Properties.OwnerId.eq(ownerId)).list();
    }

    public static void delete(Long id) {
        sProjectDao.deleteByKey(id);
    }
}
