package east.orientation.microlesson.mvp.presenter;

import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.text.TextUtils;

import com.laifeng.sopcastsdk.configuration.AudioConfiguration;
import com.laifeng.sopcastsdk.configuration.VideoConfiguration;
import com.laifeng.sopcastsdk.controller.StreamController;
import com.laifeng.sopcastsdk.stream.packer.flv.FlvPacker;
import com.laifeng.sopcastsdk.stream.sender.local.LocalSender;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.List;

import east.orientation.microlesson.app.MicroApp;
import east.orientation.microlesson.local.Common;
import east.orientation.microlesson.mvp.presenter.base.BasePresenter;
import east.orientation.microlesson.mvp.contract.MakeContract;
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

public class MakePresenter extends BasePresenter<MakeContract.View> implements MakeContract.Presenter {

    @Override
    public void getDefaultPicList(List<LocalMedia> medias, List<LocalMedia> mediaList) {
        addSubscribe(Observable.create((ObservableOnSubscribe<List<LocalMedia>>) emitter -> {
            for (int i = 0; i < mediaList.size(); i++) {
                if (!TextUtils.isEmpty(mediaList.get(i).getPath()) || !TextUtils.isEmpty(mediaList.get(i).getCompressPath())) {
                    medias.add(mediaList.get(i));
                }
            }
            emitter.onNext(medias);
        }).compose(RxUtils.rxSchedulerHelper()).subscribeWith(new ResourceObserver<List<LocalMedia>>() {
            @Override
            public void onNext(List<LocalMedia> list) {
                ifViewAttached(view -> view.notifyPicChange(list));
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
    public void startRecorder(StreamController streamController) {
        addSubscribe(Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            try {
                String path;
                String dir = FileUtil.getCacheFolder(Common.SAVE_DIR_NAME) + File.separator+ MicroApp.sUser.account;
                File dirFile = new File(dir);
                if (!dirFile.exists()) {
                    if (dirFile.mkdir()) {
                    }
                }
                path = dirFile.getAbsolutePath()+ File.separator + "Temp.flv";
                //初始化flv打包器
                FlvPacker packer = new FlvPacker();
                packer.initAudioParams(AudioConfiguration.DEFAULT_FREQUENCY, 16, false);

                VideoConfiguration config = new VideoConfiguration.Builder().setSize(600, 960).build();

                streamController.setVideoConfiguration(config);
                streamController.setPacker(packer);
                streamController.setSender(new LocalSender(path));
                emitter.onNext(true);
            } catch (Exception e) {
                emitter.onNext(false);
            }
        }).compose(RxUtils.rxSchedulerHelper()).subscribeWith(new ResourceObserver<Boolean>() {
            @Override
            public void onNext(Boolean ok) {
                if (ok) streamController.start();
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
    public void stopRecorder(StreamController streamController) {
        streamController.stop();
    }

    @Override
    public void actionJumpCommit() {
        String dir = FileUtil.getCacheFolder(Common.SAVE_DIR_NAME) + File.separator + MicroApp.sUser.account + File.separator + "Temp.flv";
        ifViewAttached(view -> view.jumpToCommit(dir));
    }

}
