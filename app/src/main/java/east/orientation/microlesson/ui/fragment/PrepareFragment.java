package east.orientation.microlesson.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import east.orientation.microlesson.R;
import east.orientation.microlesson.local.Common;
import east.orientation.microlesson.local.adapter.ShowPictureAdapter;
import east.orientation.microlesson.local.adapter.SortItemTouchHelperCallback;
import east.orientation.microlesson.mvp.contract.PrepareContact;
import east.orientation.microlesson.mvp.presenter.PreparePresenter;
import east.orientation.microlesson.ui.fragment.base.BaseFragment;
import east.orientation.microlesson.utils.RxToast;

/**
 * @author ljq
 * @date 2018/12/11
 */

public class PrepareFragment extends BaseFragment<PrepareContact.View,PrepareContact.Presenter> implements PrepareContact.View {

    @BindView(R.id.rv_pictures)
    RecyclerView mRvPictures;
    @BindView(R.id.btn_start_make)
    Button mBtnMake;

    private List<LocalMedia> mMedias = new ArrayList<>();
    private ShowPictureAdapter mAdapter;
    private int mClickItemPos;// 当前点击的item

    public static PrepareFragment newInstance(List<LocalMedia> medias) {
        
        Bundle args = new Bundle();
        args.putParcelableArrayList(Common.KEY_MEDIAS, (ArrayList<? extends Parcelable>) medias);
        PrepareFragment fragment = new PrepareFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_prepare;
    }

    @Override
    protected void initView() {
        super.initView();
        initRecycler();
    }

    private void initRecycler() {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),3);
        mRvPictures.setLayoutManager(layoutManager);
        mAdapter = new ShowPictureAdapter(getContext(),R.layout.layout_pic_selected_item,mMedias);
        mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                mClickItemPos = position;
                presenter.actionShowSelector(Common.REQUEST_CHANGE_PICTURE);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        View footView = LayoutInflater.from(getContext()).inflate(R.layout.default_add_pic, mRvPictures, false);
        mAdapter.addFootView(footView);
        SortItemTouchHelperCallback<LocalMedia> sortItemTouchHelperCallback = new SortItemTouchHelperCallback<>(getContext(),mAdapter,mMedias);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(sortItemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(mRvPictures);
        mRvPictures.setAdapter(mAdapter);
        footView.setOnClickListener(v -> presenter.actionShowSelector(Common.REQUEST_ADD_PICTURES));
    }

    @Override
    protected void initEventAndData() {
        if (getArguments() != null) {
            presenter.getDefaultList(mMedias,getArguments().getParcelableArrayList(Common.KEY_MEDIAS));
        }
    }

    @OnClick({R.id.btn_start_make})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_make:
                presenter.actionShowMake(mMedias);
                break;
        }
    }

    @Override
    public void showSelector(int request) {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .maxSelectNum((request == Common.REQUEST_ADD_PICTURES) ? 9-mMedias.size() : 1)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .isCamera(false)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .compress(true)// 是否压缩
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .forResult(request);//结果回调onActivityResult code
    }

    @Override
    public void showMakeFragment(List<LocalMedia> mediaList) {
        MakeFragment makeFragment = findChildFragment(MakeFragment.class);
        if (makeFragment == null) makeFragment = MakeFragment.newInstance(mediaList);
        start(makeFragment);
    }

    @Override
    public void notifyChanged(List<LocalMedia> mediaList) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void changeItem(int position) {
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void addPictures(List<LocalMedia> mediaList) {
        mAdapter.notifyItemRangeChanged(mMedias.size(),mediaList.size());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Common.REQUEST_ADD_PICTURES:
                    // 图片选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    presenter.actionAddPictures(mMedias,selectList);
                    break;
                case Common.REQUEST_CHANGE_PICTURE:
                    // 图片选择结果回调
                    List<LocalMedia> localMedias = PictureSelector.obtainMultipleResult(data);
                    for (int i = 0; i < localMedias.size(); i++) {
                        presenter.actionChangeItem(mMedias,mClickItemPos,localMedias.get(i));
                    }
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @NonNull
    @Override
    public PrepareContact.Presenter createPresenter() {
        return new PreparePresenter();
    }
}
