package east.orientation.microlesson.mvp.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

/**
 * @author ljq
 * @date 2018/11/27
 */

public interface CreateContract {
    interface View extends MvpView {
        void showSelector();
        void showDraft();
        void showPrepare(List<LocalMedia> mediaList);
    }

    interface Presenter extends MvpPresenter<View> {
            void actionGetPics();
            void actionShowPrepare(List<LocalMedia> mediaList);
            void actionShowDraft();
    }
}
