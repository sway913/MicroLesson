package east.orientation.microlesson.mvp.presenter;

import java.io.File;
import java.util.List;

import east.orientation.microlesson.app.MicroApp;
import east.orientation.microlesson.local.Common;
import east.orientation.microlesson.local.dao.Project;
import east.orientation.microlesson.mvp.presenter.base.BasePresenter;
import east.orientation.microlesson.mvp.contract.DraftContract;
import east.orientation.microlesson.utils.FileUtil;
import east.orientation.microlesson.utils.RxUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observers.ResourceObserver;

/**
 * @author ljq
 * @date 2018/12/11
 * @description
 */

public class DraftPresenter extends BasePresenter<DraftContract.View> implements DraftContract.Presenter {

    @Override
    public void getProject(List<Project> projects) {
        addSubscribe(Observable.create((ObservableOnSubscribe<List<Project>>) emitter -> {
            String dir = FileUtil.getCacheFolder(Common.DRAFT_DIR) + File.separator + MicroApp.sUser.account;
            File d = new File(dir);
            if (!FileUtil.isFileExists(d)) {
                d.mkdir();
            }
            List<File> files = FileUtil.listFilesInDirWithFilter(dir,"flv");
            projects.clear();
            Project project;
            for (File file:files) {
                project = new Project();
                project.setUserName("ljq_th");
                project.setTitle(file.getName());
                project.setThumbnail(file.getAbsolutePath());
                project.setFilePath(file.getAbsolutePath());
                projects.add(project);
            }
            emitter.onNext(projects);
        }).compose(RxUtils.rxSchedulerHelper()).subscribeWith(new ResourceObserver<List<Project>>() {
            @Override
            public void onNext(List<Project> list) {
                ifViewAttached(DraftContract.View::notifyChange);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        }));
    }
}
