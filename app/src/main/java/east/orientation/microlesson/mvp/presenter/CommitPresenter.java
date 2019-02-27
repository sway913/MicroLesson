package east.orientation.microlesson.mvp.presenter;

import android.graphics.Bitmap;
import android.support.annotation.IntDef;
import android.text.TextUtils;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import east.orientation.microlesson.app.MicroApp;
import east.orientation.microlesson.greendao.helper.DatabaseUtil;
import east.orientation.microlesson.local.Common;
import east.orientation.microlesson.local.dao.Project;
import east.orientation.microlesson.mvp.contract.CommitContract;
import east.orientation.microlesson.mvp.presenter.base.BasePresenter;
import east.orientation.microlesson.rxbus.RxBus;
import east.orientation.microlesson.rxbus.event.SyncMessage;
import east.orientation.microlesson.rxbus.event.UpdateFilesMessage;
import east.orientation.microlesson.utils.FileUtil;
import east.orientation.microlesson.utils.RxUtils;
import east.orientation.microlesson.utils.VideoUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observers.ResourceObserver;

/**
 * @author ljq
 * @date 2018/12/27
 * @description
 */

public class CommitPresenter extends BasePresenter<CommitContract.View> implements CommitContract.Presenter {


    @Override
    public void actionLoadThumbnail(String filePath) {
        addSubscribe(Observable.create((ObservableOnSubscribe<Bitmap>) emitter -> {
            Bitmap bitmap = VideoUtil.getVideoThumbnail(filePath,1000*1000);
            if (bitmap != null) emitter.onNext(bitmap);
        }).compose(RxUtils.rxSchedulerHelper()).subscribeWith(new ResourceObserver<Bitmap>() {
            @Override
            public void onNext(Bitmap bitmap) {
                ifViewAttached(view -> view.loadThumbnail(bitmap));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        }));
    }

    @Override
    public void actionChangeFileName(String filePath,String title,String clazz) {
        addSubscribe(Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            if (TextUtils.isEmpty(title)) {
                emitter.onNext(ERROR_1);
            } else if (!FileUtil.renameFile(filePath,title+".flv")){
                emitter.onNext(ERROR_2);
            } else {
                emitter.onNext(SUCCESS);
                if (!TextUtils.isEmpty(MicroApp.sUser.userId)) {
                    Project project = new Project();
                    project.setOwnerId(MicroApp.sUser.userId);
                    project.setUserId(MicroApp.sUser.userId);
                    project.setUserName(MicroApp.sUser.name);
                    project.setTitle(title);
                    project.setFileName(title + ".flv");
                    project.setClazz(clazz);
                    project.setFree("0");
                    project.setThumbnail(new File(filePath).getParent() + File.separator + title+".flv");
                    project.setFilePath(new File(filePath).getParent() + File.separator + title+".flv");
                    project.setSize(new File(project.getFilePath()).length());
                    // 插入数据库
                    DatabaseUtil.insertProject(project);
                    // 通知上传
                    RxBus.getDefault().post(new SyncMessage(Common.CMD_FILE_UP, project));
                    // 更新本地列表
                    RxBus.getDefault().post(new UpdateFilesMessage());
                }
            }
        }).compose(RxUtils.rxSchedulerHelper()).subscribeWith(new ResourceObserver<Integer>() {
            @Override
            public void onNext(@Error Integer code) {
                switch (code) {
                    case SUCCESS:
                        actionCommit();
                        break;
                    case ERROR_1:
                        ifViewAttached(view -> view.showMessage("请输入标题！"));
                        break;
                    case ERROR_2:
                        ifViewAttached(view -> view.showMessage("修改标题失败！"));
                        break;
                    case ERROR_3:
                        ifViewAttached(view -> view.showMessage("请输入分类！"));
                        break;
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        }));
    }

    @Override
    public void actionCommit() {

        ifViewAttached(CommitContract.View::commitSuccess);
    }

    private static final int SUCCESS = 0;
    private static final int ERROR_1 = 1;
    private static final int ERROR_2 = 2;
    private static final int ERROR_3 = 3;

    @IntDef({SUCCESS,ERROR_1,ERROR_2,ERROR_3})

    @Retention(RetentionPolicy.SOURCE)

    public @interface Error{

    }

}
