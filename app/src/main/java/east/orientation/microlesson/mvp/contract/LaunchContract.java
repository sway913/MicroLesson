package east.orientation.microlesson.mvp.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

/**
 * @author ljq
 * @date 2018/12/12
 * @description
 */

public interface LaunchContract {

    interface View extends MvpView {
        void showMain();
    }

    interface Presenter extends MvpPresenter<View> {
        void actionShowMain();
    }
}
