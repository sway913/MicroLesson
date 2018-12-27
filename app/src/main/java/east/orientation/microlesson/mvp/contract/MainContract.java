package east.orientation.microlesson.mvp.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

/**
 * @author ljq
 * @date 2018/11/27
 */

public interface MainContract {

    interface View extends MvpView {
        void show();

        void create();
    }

    interface Presenter extends MvpPresenter<View> {
        void actionShow();

        void actionCreate();
    }
}
