package east.orientation.microlesson.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import east.orientation.microlesson.R;
import east.orientation.microlesson.local.Common;
import east.orientation.microlesson.local.adapter.MakePictureAdapter;
import east.orientation.microlesson.local.adapter.MakeVoiceAdapter;
import east.orientation.microlesson.local.adapter.SpaceItemDecoration;
import east.orientation.microlesson.local.model.LocalVoice;
import east.orientation.microlesson.mvp.contract.MakeContract;
import east.orientation.microlesson.mvp.presenter.MakePresenter;
import east.orientation.microlesson.ui.fragment.base.BaseFragment;
import east.orientation.microlesson.ui.fragment.base.ScreenFragment;
import east.orientation.microlesson.utils.RxToast;

/**
 * @author ljq
 * @date 2018/12/11
 * @description 制作界面  为每张图片添加语音
 */

public class MakeFragment extends ScreenFragment<MakeContract.View,MakeContract.Presenter> implements MakeContract.View {

    @BindView(R.id.rv_pics)
    RecyclerView mRvPics;
    @BindView(R.id.iv_start)
    ImageView mIvStart;
    @BindView(R.id.tv_duration)
    TextView mTvDuration;

    private List<LocalMedia> mMedias = new ArrayList<>();
    private CommonAdapter<LocalMedia> mPicAdapter;
    private int mPicPosition;
    private boolean isStart;

    public static MakeFragment newInstance(List<LocalMedia> mediaList) {

        Bundle args = new Bundle();
        args.putParcelableArrayList(Common.KEY_MEDIAS, (ArrayList<? extends Parcelable>) mediaList);
        MakeFragment fragment = new MakeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_make;
    }

    @Override
    protected void initView() {
        super.initView();
        initPicRecycler();
    }

    private void initPicRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        mRvPics.setLayoutManager(layoutManager);
        mPicAdapter = new MakePictureAdapter(getContext(),R.layout.layout_pic_make_item,mMedias);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(mRvPics);
        mRvPics.addItemDecoration(new SpaceItemDecoration(0,30));
        mRvPics.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 页数变化
                changePage(pagerSnapHelper,layoutManager);
            }
        });
        mRvPics.setNestedScrollingEnabled(false);
        mRvPics.setAdapter(mPicAdapter);
    }

    private void changePage(PagerSnapHelper pagerSnapHelper,LinearLayoutManager layoutManager) {
        mPicPosition = pagerSnapHelper.findTargetSnapPosition(layoutManager,0,0);
    }

    @Override
    protected void initEventAndData() {
        if (getArguments() != null) {
            presenter.getDefaultPicList(mMedias,getArguments().getParcelableArrayList(Common.KEY_MEDIAS));
        }
    }

    @OnClick(R.id.iv_start)
    void click(View view) {
        switch (view.getId()) {
            case R.id.iv_start:
                if (isStart)
                    presenter.stopRecorder(mStreamController);
                else
                    requestRecording();
                break;
        }
    }

    @Override
    protected void requestRecordSuccess() {
        super.requestRecordSuccess();
        presenter.startRecorder(mStreamController, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                        .getAbsolutePath()+"/"+ System.currentTimeMillis() + "_micro.flv");
        isStart = true;
    }

    @Override
    protected void requestRecordFail() {
        super.requestRecordFail();
        RxToast.showToast("获取录屏权限失败");
    }

    @Override
    public void notifyPicChange(List<LocalMedia> mediaList) {
        mPicAdapter.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MakeContract.Presenter createPresenter() {
        return new MakePresenter();
    }
}
