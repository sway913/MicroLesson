package east.orientation.microlesson.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import east.orientation.microlesson.R;
import east.orientation.microlesson.mvp.contract.MainContract;
import east.orientation.microlesson.mvp.presenter.MainPresenter;
import east.orientation.microlesson.ui.fragment.base.BaseFragment;
import east.orientation.microlesson.utils.RxToast;

/**
 * @author ljq
 * @date 2018/12/12
 * @description
 */

public class MainFragment extends BaseFragment<MainContract.View,MainContract.Presenter> implements MainContract.View {
    @BindView(R.id.tv_show_project)
    TextView mTvShow;
    @BindView(R.id.tv_create_project)
    TextView mTvCreate;

    private ShowFragment mShowFragment;
    private CreateFragment mCreateFragment;

    public static MainFragment newInstance() {
        
        Bundle args = new Bundle();
        
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    protected void initView() {
        super.initView();
        initFragments();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initEventAndData() {

    }

    private void initFragments() {
        mShowFragment = ShowFragment.newInstance();
        mCreateFragment = CreateFragment.newInstance();
        loadMultipleRootFragment(R.id.layout_content,0,mShowFragment,mCreateFragment);
    }

    @Override
    public void show() {
        showHideFragment(mShowFragment);
        RxToast.showToast("show");
    }

    @Override
    public void create() {
        showHideFragment(mCreateFragment);
        RxToast.showToast("create");
    }

    @OnClick({R.id.tv_show_project,R.id.tv_create_project})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_show_project:
                presenter.actionShow();
                break;
            case R.id.tv_create_project:
                presenter.actionCreate();
                break;
        }
    }

    public void startBrotherFragment(BaseFragment fragment) {
        start(fragment);
    }

    @NonNull
    @Override
    public MainContract.Presenter createPresenter() {
        return new MainPresenter();
    }
}
