package east.orientation.microlesson.mvp.presenter;

import east.orientation.microlesson.mvp.presenter.base.BasePresenter;
import east.orientation.microlesson.mvp.contract.LaunchContract;

/**
 * @author ljq
 * @date 2018/12/12
 * @description
 */

public class LaunchPresenter extends BasePresenter<LaunchContract.View> implements LaunchContract.Presenter {

    @Override
    public void actionShowMain() {
        ifViewAttached(LaunchContract.View::showMain);
    }
}
