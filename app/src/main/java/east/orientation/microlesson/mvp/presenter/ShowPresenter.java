package east.orientation.microlesson.mvp.presenter;

import android.content.Context;

import java.io.File;
import java.util.List;

import east.orientation.microlesson.app.MicroApp;
import east.orientation.microlesson.greendao.helper.DatabaseUtil;
import east.orientation.microlesson.local.Common;
import east.orientation.microlesson.local.dao.Project;
import east.orientation.microlesson.local.model.User;
import east.orientation.microlesson.mvp.presenter.base.BasePresenter;
import east.orientation.microlesson.mvp.contract.ShowContract;
import east.orientation.microlesson.rxbus.RxBus;
import east.orientation.microlesson.rxbus.Subscribe;
import east.orientation.microlesson.rxbus.event.UpdateFilesMessage;
import east.orientation.microlesson.rxbus.event.UpdateUserMessage;
import east.orientation.microlesson.socket.SocketManager;
import east.orientation.microlesson.utils.FileUtil;
import east.orientation.microlesson.utils.RxUtils;
import east.orientation.microlesson.utils.SharePreferenceUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observers.ResourceObserver;

/**
 * @author ljq
 * @date 2018/11/27
 */

public class ShowPresenter extends BasePresenter<ShowContract.View> implements ShowContract.Presenter {


    @Override
    public void attachView(ShowContract.View view) {
        super.attachView(view);
        RxBus.getDefault().register(this);
    }

    @Override
    public void getProject(List<Project> projects) {
        addSubscribe(Observable.create((ObservableOnSubscribe<List<Project>>) emitter -> {
//            String dir = FileUtil.getCacheFolder(Common.SAVE_DIR_NAME)+File.separator+MicroApp.sUser.account;
//            File d = new File(dir);
//            if (!FileUtil.isFileExists(d)) {
//                d.mkdir();
//            }
//            List<File> files = FileUtil.listFilesInDirWithFilter(dir,"flv");
//            projects.clear();
//            Project project;
//            for (File file:files) {
//                project = new Project();
//                project.setUserName("ljq_th");
//                project.setTitle(file.getName());
//                project.setThumbnail(file.getAbsolutePath());
//                project.setFilePath(file.getAbsolutePath());
//                projects.add(project);
//            }
            List<Project> datas = DatabaseUtil.getProjects(MicroApp.sUser.userId);
            projects.clear();
            projects.addAll(datas);
            emitter.onNext(projects);
        }).compose(RxUtils.rxSchedulerHelper()).subscribeWith(new ResourceObserver<List<Project>>() {
            @Override
            public void onNext(List<Project> list) {
                ifViewAttached(ShowContract.View::notifyChange);
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
    public void actionGetUser(Context context) {
        addSubscribe(Observable.create((ObservableOnSubscribe<String>) emitter -> {
            User user = (User) SharePreferenceUtil.get(context,Common.KEY_USER,new User());
            if (user != null) emitter.onNext(user.name);
            else emitter.onNext(new User().name);
        }).compose(RxUtils.rxSchedulerHelper()).subscribeWith(new ResourceObserver<String>() {
            @Override
            public void onNext(String name) {
                ifViewAttached(view -> view.updateUser(name));
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
    public void deleteProject(Project project,int position) {
        addSubscribe(Observable.create((ObservableOnSubscribe<String>) emitter -> {
            // 删除文件
            if (FileUtil.delete(project.getFilePath())) {
//                if (project.getUserId().equals(MicroApp.sUser.userId)) {
//                    // 如果是本人上传项目则删除服务器文件
//                    SocketManager.getInstance().filedel(Common.TYPE_MICRO_LESSONS,project.getTitle());
//                }
                // 删除数据库记录
                DatabaseUtil.delete(project.getId());
                emitter.onNext("删除成功！");
            } else {
                emitter.onNext("删除失败！");
            }

        }).compose(RxUtils.rxSchedulerHelper()).subscribeWith(new ResourceObserver<String>() {
            @Override
            public void onNext(String msg) {
                ifViewAttached(view -> {
                    view.showDeleteMsg(msg);
                    view.removeItem(position);
                });
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
    void OnEvent(UpdateUserMessage message) {
        if (message != null) {
            ifViewAttached(view -> view.updateUser(message.getName()));
        }
    }

    @Subscribe
    void update(UpdateFilesMessage message) {
        if (message != null) {
            ifViewAttached(ShowContract.View::updateList);
        }
    }

    @Override
    public void detachView() {
        super.detachView();
        RxBus.getDefault().unregister(this);
    }
}
