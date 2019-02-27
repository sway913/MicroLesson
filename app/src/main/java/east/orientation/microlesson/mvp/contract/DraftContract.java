package east.orientation.microlesson.mvp.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import java.util.List;

import east.orientation.microlesson.local.dao.Project;

/**
 * @author ljq
 * @date 2018/12/11
 * @description
 */

public interface DraftContract {
    interface View extends MvpView {
        void notifyChange();
    }

    interface Presenter extends MvpPresenter<View> {
        void getProject(List<Project> projects);
    }
}
