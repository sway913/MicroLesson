package east.orientation.microlesson.mvp.presenter;



import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

import east.orientation.microlesson.mvp.presenter.base.BasePresenter;
import east.orientation.microlesson.mvp.contract.CreateContract;

/**
 * @author ljq
 * @date 2018/11/27
 */

public class CreatePresenter extends BasePresenter<CreateContract.View> implements CreateContract.Presenter {


    @Override
    public void actionGetPics() {
        ifViewAttached(CreateContract.View::showSelector);
    }

    @Override
    public void actionShowDraft() {
        ifViewAttached(CreateContract.View::showDraft);
    }

    @Override
    public void actionShowPrepare(List<LocalMedia> mediaList) {
        ifViewAttached(view -> view.showPrepare(mediaList));
    }
}
