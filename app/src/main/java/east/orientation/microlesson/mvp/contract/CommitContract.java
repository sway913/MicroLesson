package east.orientation.microlesson.mvp.contract;

import android.graphics.Bitmap;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

/**
 * @author ljq
 * @date 2018/12/27
 * @description
 */

public interface CommitContract {
    interface View extends MvpView {
        void loadThumbnail(Bitmap bitmap);
        void showMessage(String msg);
        void commitSuccess();
    }

    interface Presenter extends MvpPresenter<View> {
        void actionLoadThumbnail(String filePath);
        void actionChangeFileName(String filePath,String title,String clazz);
        void actionCommit();
    }
}
