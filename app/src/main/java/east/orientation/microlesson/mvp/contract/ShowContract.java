package east.orientation.microlesson.mvp.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import east.orientation.microlesson.local.model.Project;

/**
 * @author ljq
 * @date 2018/11/27
 */

public interface ShowContract {
    interface View extends MvpView {

    }

    interface Presenter extends MvpPresenter<View> {
        void getProject();
    }
}
