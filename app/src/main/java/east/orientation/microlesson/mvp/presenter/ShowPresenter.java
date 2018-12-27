package east.orientation.microlesson.mvp.presenter;

import east.orientation.microlesson.mvp.presenter.base.BasePresenter;
import east.orientation.microlesson.mvp.contract.ShowContract;
/**
 * @author ljq
 * @date 2018/11/27
 */

public class ShowPresenter extends BasePresenter<ShowContract.View> implements ShowContract.Presenter {

    @Override
    public void attachView(ShowContract.View view) {
        super.attachView(view);
    }


    @Override
    public void getProject() {

    }
}
