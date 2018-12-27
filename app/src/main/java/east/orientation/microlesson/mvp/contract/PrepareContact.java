package east.orientation.microlesson.mvp.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

/**
 * @author hyb
 * @date 2018/12/11
 */

public interface PrepareContact {
    interface View extends MvpView {
        void showSelector(int request);
        void showMakeFragment(List<LocalMedia> mediaList);
        void notifyChanged(List<LocalMedia> mediaList);
        void addPictures(List<LocalMedia> mediaList);
        void changeItem(int position);
    }

    interface Presenter extends MvpPresenter<View> {
        void actionShowMake(List<LocalMedia> mediaList);
        void actionShowSelector(int request);
        void getDefaultList(List<LocalMedia> medias,List<LocalMedia> mediaList);
        void actionAddPictures(List<LocalMedia> medias,List<LocalMedia> mediaList);
        void actionChangeItem(List<LocalMedia> mediaList,int position,LocalMedia localMedia);
    }
}
