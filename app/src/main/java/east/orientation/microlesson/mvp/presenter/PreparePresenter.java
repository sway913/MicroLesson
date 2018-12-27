package east.orientation.microlesson.mvp.presenter;

import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

import east.orientation.microlesson.local.Common;
import east.orientation.microlesson.mvp.presenter.base.BasePresenter;
import east.orientation.microlesson.mvp.contract.PrepareContact;
import east.orientation.microlesson.utils.RxUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observers.ResourceObserver;

/**
 * @author ljq
 * @date 2018/12/11
 * @description 编辑图片页面
 */

public class PreparePresenter extends BasePresenter<PrepareContact.View> implements PrepareContact.Presenter {

    @Override
    public void attachView(PrepareContact.View view) {
        super.attachView(view);
        registerEvent();
    }

    private void registerEvent() {

    }

    @Override
    public void actionShowMake(List<LocalMedia> mediaList) {
        ifViewAttached(view -> view.showMakeFragment(mediaList));
    }

    @Override
    public void actionShowSelector(int request) {
        ifViewAttached(view -> view.showSelector(request));
    }

    @Override
    public void actionAddPictures(List<LocalMedia> medias,List<LocalMedia> mediaList) {
        addSubscribe(Observable.create((ObservableOnSubscribe<List<LocalMedia>>) emitter -> {
            medias.addAll(mediaList);
            emitter.onNext(medias);

        }).compose(RxUtils.rxSchedulerHelper()).subscribeWith(new ResourceObserver<List<LocalMedia>>() {
            @Override
            public void onNext(List<LocalMedia> mediaList) {
                ifViewAttached(view -> view.addPictures(mediaList));
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
    public void actionChangeItem(List<LocalMedia> mediaList,int position,LocalMedia media) {
        addSubscribe(Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            mediaList.remove(position);
            mediaList.add(position,media);
            emitter.onNext(position);
        }).compose(RxUtils.rxSchedulerHelper()).subscribeWith(new ResourceObserver<Integer>() {

            @Override
            public void onNext(Integer position) {
                ifViewAttached(view -> view.changeItem(position));
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
    public void getDefaultList(List<LocalMedia> medias,List<LocalMedia> mediaList) {
        addSubscribe(Observable.create((ObservableOnSubscribe<List<LocalMedia>>) emitter -> {
            medias.addAll(mediaList);
            if (medias.size() < Common.MAX_SELECTED_COUNT) {

                for (int i = 0; i < Common.MAX_SELECTED_COUNT - mediaList.size(); i++) {
                    LocalMedia media = new LocalMedia();
                    medias.add(media);
                }
            }
            emitter.onNext(medias);
        }).compose(RxUtils.rxSchedulerHelper()).subscribeWith(new ResourceObserver<List<LocalMedia>>() {
            @Override
            public void onNext(List<LocalMedia> list) {
                ifViewAttached(view -> view.notifyChanged(list));
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
