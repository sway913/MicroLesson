package east.orientation.microlesson.mvp.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.laifeng.sopcastsdk.controller.StreamController;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

/**
 * @author ljq
 * @date 2018/12/11
 * @description
 */

public interface MakeContract {
    interface View extends MvpView {
        // pic
        void notifyPicChange(List<LocalMedia> mediaList);
        // recorder
        void jumpToCommit(String filePath);
    }

    interface Presenter extends MvpPresenter<View> {
        // pic
        void getDefaultPicList(List<LocalMedia> medias, List<LocalMedia> mediaList);
        // recorder
        void startRecorder(StreamController streamController);
        void stopRecorder(StreamController streamController);
        void actionJumpCommit();
    }
}
