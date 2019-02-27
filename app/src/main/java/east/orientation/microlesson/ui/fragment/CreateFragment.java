package east.orientation.microlesson.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import east.orientation.microlesson.R;
import east.orientation.microlesson.mvp.contract.CreateContract;
import east.orientation.microlesson.mvp.presenter.CreatePresenter;
import east.orientation.microlesson.ui.fragment.base.BaseFragment;

/**
 * @author ljq
 * @date 2018/11/27
 */

public class CreateFragment extends BaseFragment<CreateContract.View,CreateContract.Presenter> implements CreateContract.View {

    @BindView(R.id.tv_select_pic)
    TextView mTvSelectPic;
    @BindView(R.id.tv_draft_box)
    TextView mTvDraftBox;

    public static CreateFragment newInstance() {

        Bundle args = new Bundle();

        CreateFragment fragment = new CreateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_create;
    }

    @Override
    protected void initEventAndData() {

    }

    @OnClick({R.id.tv_select_pic,R.id.tv_draft_box})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_select_pic:
                presenter.actionGetPics();
                break;
            case R.id.tv_draft_box:
                presenter.actionShowDraft();
                break;
        }
    }

    @Override
    public void showSelector() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .maxSelectNum(9)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .isCamera(false)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .compress(true)// 是否压缩
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    @Override
    public void showDraft() {
        DraftBoxFragment draftBoxFragment = findChildFragment(DraftBoxFragment.class);
        if (draftBoxFragment == null) draftBoxFragment = DraftBoxFragment.newInstance();
        ((MainFragment) getParentFragment()).start(draftBoxFragment);
    }

    @Override
    public void showPrepare(List<LocalMedia> mediaList) {
        PrepareFragment prepareFragment = findChildFragment(PrepareFragment.class);
        if (prepareFragment == null) prepareFragment = PrepareFragment.newInstance(mediaList);
        ((MainFragment) getParentFragment()).start(prepareFragment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                    //for (LocalMedia media : selectList) {
                    //    Log.i("图片-----》", media.getPath());
                    //}
                    presenter.actionShowPrepare(selectList);

                    break;
            }
        }
    }

    @NonNull
    @Override
    public CreateContract.Presenter createPresenter() {
        return new CreatePresenter();
    }
}
