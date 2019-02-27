package east.orientation.microlesson.mvp.presenter;

import android.support.annotation.NonNull;

import east.orientation.microlesson.mvp.presenter.base.BasePresenter;
import east.orientation.microlesson.mvp.contract.MainContract;

/**
 * @author ljq
 * @date 2018/11/27
 */

public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {

    @Override
    public void attachView(@NonNull MainContract.View view) {
        super.attachView(view);
    }


    @Override
    public void actionOnline() {
        ifViewAttached(MainContract.View::online);
    }

    @Override
    public void actionShow() {
        ifViewAttached(MainContract.View::show);
    }

    @Override
    public void actionCreate() {
        ifViewAttached(MainContract.View::create);
    }
}
