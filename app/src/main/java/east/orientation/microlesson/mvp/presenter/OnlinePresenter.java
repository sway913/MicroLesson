package east.orientation.microlesson.mvp.presenter;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import east.orientation.microlesson.app.MicroApp;
import east.orientation.microlesson.greendao.helper.DatabaseUtil;
import east.orientation.microlesson.local.Common;
import east.orientation.microlesson.local.dao.Project;
import east.orientation.microlesson.mvp.contract.OnlineContract;
import east.orientation.microlesson.mvp.contract.ShowContract;
import east.orientation.microlesson.mvp.presenter.base.BasePresenter;
import east.orientation.microlesson.rxbus.RxBus;
import east.orientation.microlesson.rxbus.Subscribe;
import east.orientation.microlesson.rxbus.ThreadMode;
import east.orientation.microlesson.rxbus.event.PublishQueryResponse;
import east.orientation.microlesson.rxbus.event.ResponseMessage;
import east.orientation.microlesson.rxbus.event.SyncMessage;
import east.orientation.microlesson.socket.SocketManager;
import east.orientation.microlesson.utils.FileUtil;
import east.orientation.microlesson.utils.RxUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observers.ResourceObserver;

/**
 * @author ljq
 * @date 2019/1/18
 * @description
 */

public class OnlinePresenter extends BasePresenter<OnlineContract.View> implements OnlineContract.Presenter {
    private boolean haveMore;
    private List<Project> mProjects;
    @Override
    public void attachView(OnlineContract.View view) {
        super.attachView(view);
        RxBus.getDefault().register(this);
    }

    @Override
    public void refresh(List<Project> projects) {
        mProjects = projects;
        SocketManager.getInstance().publishQuery("", Common.TYPE_MICRO_LESSONS,"","","");
    }

    @Override
    public void loadMore() {

    }

    @Override
    public void down(Project project) {
        addSubscribe(Observable.create((ObservableOnSubscribe<Project>) emitter -> {
            Project p = DatabaseUtil.exist(project,MicroApp.sUser.userId);
            if (p == null) {
                RxBus.getDefault().post(new SyncMessage(Common.CMD_FILE_DOWN_TYPE,project));
                String path = FileUtil.getCacheFolder(Common.SAVE_DIR_NAME) + File.separator+ MicroApp.sUser.account + File.separator+project.getFileName();
                project.setFilePath(path);
                emitter.onNext(project);
            } else {
                emitter.onNext(p);
            }
        }).compose(RxUtils.rxSchedulerHelper()).subscribeWith(new ResourceObserver<Project>() {
            @Override
            public void onNext(Project downProject) {
                ifViewAttached(view -> view.downSuccess(downProject));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        }));
    }

    @Subscribe()
    public void OnEvent(PublishQueryResponse message) {
        String code = message.getCode();
        if (code.equals("1")) {
            haveMore = false;
            handleMessage(message.getResponse());
        } else if (code.equals("2")){
            haveMore = true;

        } else {
            ifViewAttached(view -> view.showMessage("请求失败！"));
        }
    }

    private void handleMessage(String response) {
        addSubscribe(Observable.create((ObservableOnSubscribe<List<Project>>) emitter -> {
            int indexS = response.indexOf(",", response.indexOf(",") + 1); // 第二个逗号的位置
            if (indexS < 0 || indexS == response.length() - 1) {// 判断是否有列表

            } else {
                List<Project> projects = new ArrayList<>();
                String listStr = response.substring(indexS + 1, response.length());
                String[] arrayContent = listStr.split(";");
                if (arrayContent.length > 0) {
                    Project project;
                    for (int i = 0; i < arrayContent.length; i++) {
                        String[] array = arrayContent[i].split(",");
                        if (!array[3].endsWith(".flv")) continue;
                        project = new Project();
                        project.setTime(array[0]);
                        project.setClazz(array[1]);
                        project.setFileName(array[3]);
                        project.setUserId(array[4]);
                        project.setUserName(array[5]);
                        project.setFree(array[6]);
                        project.setSize(Long.valueOf(array[7]));
                        project.setTitle(project.getFileName().substring(0,project.getFileName().indexOf(".")));
                        projects.add(project);

                    }
                    mProjects.clear();
                    mProjects.addAll(projects);
                }
            }
            emitter.onNext(mProjects);
        }).compose(RxUtils.rxSchedulerHelper()).subscribeWith(new ResourceObserver<List<Project>>() {
            @Override
            public void onNext(List<Project> list) {
                ifViewAttached(OnlineContract.View::notifyChanged);
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
    public void detachView() {
        super.detachView();
        RxBus.getDefault().unregister(this);
    }
}
