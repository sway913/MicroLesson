package east.orientation.microlesson.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.OnClick;
import east.orientation.microlesson.R;
import east.orientation.microlesson.local.Common;
import east.orientation.microlesson.mvp.contract.CommitContract;
import east.orientation.microlesson.mvp.presenter.CommitPresenter;
import east.orientation.microlesson.ui.fragment.base.BaseFragment;
import east.orientation.microlesson.utils.RxToast;

/**
 * @author ljq
 * @date 2018/12/27
 * @description
 */

public class CommitFragment extends BaseFragment<CommitContract.View,CommitContract.Presenter> implements CommitContract.View {
    @BindView(R.id.iv_thumbnail)
    ImageView mIvThumbnail;
    @BindView(R.id.et_title)
    EditText mEtTitle;
    @BindView(R.id.et_class)
    EditText mEtClass;
    @BindView(R.id.btn_commit)
    Button mBtnCommit;
    private String mFilePath;

    public static CommitFragment newInstance(String filePath) {

        Bundle args = new Bundle();
        args.putString(Common.KEY_FILE_PATH,filePath);
        CommitFragment fragment = new CommitFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_commit;
    }

    @Override
    protected void initView() {
        super.initView();
        mEtTitle.requestFocus();
    }

    @Override
    protected void initEventAndData() {
        if (getArguments() != null) {
            mFilePath = getArguments().getString(Common.KEY_FILE_PATH);
            presenter.actionLoadThumbnail(mFilePath);
        }
    }

    @Override
    public void loadThumbnail(Bitmap bitmap) {
        Glide.with(this).load(bitmap).into(mIvThumbnail);
    }

    @Override
    public void showMessage(String msg) {
        RxToast.showToast(msg);
    }

    @Override
    public void commitSuccess() {
        popTo(MainFragment.class,false);
    }

    @OnClick({R.id.btn_commit})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_commit:
                presenter.actionChangeFileName(mFilePath,mEtTitle.getText().toString().trim(),mEtClass.getText().toString().trim());
                break;
        }
    }

    @NonNull
    @Override
    public CommitContract.Presenter createPresenter() {
        return new CommitPresenter();
    }
}
