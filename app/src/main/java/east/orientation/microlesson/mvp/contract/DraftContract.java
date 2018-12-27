package east.orientation.microlesson.mvp.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

/**
 * @author ljq
 * @date 2018/12/11
 * @description
 */

public interface DraftContract {
    interface View extends MvpView {

    }

    interface Presenter extends MvpPresenter<View> {

    }
}
