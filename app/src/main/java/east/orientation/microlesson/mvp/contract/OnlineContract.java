package east.orientation.microlesson.mvp.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import java.util.List;

import east.orientation.microlesson.local.dao.Project;

/**
 * @author ljq
 * @date 2019/1/18
 * @description
 */

public interface OnlineContract {
    interface View extends MvpView {
        void notifyChanged();
        void showMessage(String msg);
        void downSuccess(Project project);
    }

    interface Presenter extends MvpPresenter<View> {
        void refresh(List<Project> projects);
        void loadMore();
        void down(Project project);
    }
}
