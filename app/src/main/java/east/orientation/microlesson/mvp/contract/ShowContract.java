package east.orientation.microlesson.mvp.contract;

import android.content.Context;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import java.util.List;

import east.orientation.microlesson.local.dao.Project;

/**
 * @author ljq
 * @date 2018/11/27
 */

public interface ShowContract {
    interface View extends MvpView {
        void notifyChange();
        void updateUser(String name);
        void updateList();
        void showDeleteMsg(String msg);
        void removeItem(int position);
    }

    interface Presenter extends MvpPresenter<View> {
        void getProject(List<Project> projects);
        void actionGetUser(Context context);
        void deleteProject(Project project,int position);
    }
}
