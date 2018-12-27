package east.orientation.microlesson.ui.activity;


import android.support.annotation.NonNull;

import east.orientation.microlesson.R;
import east.orientation.microlesson.mvp.contract.LaunchContract;
import east.orientation.microlesson.mvp.presenter.LaunchPresenter;
import east.orientation.microlesson.ui.activity.base.BaseActivity;
import east.orientation.microlesson.ui.fragment.MainFragment;

/**
 * @author ljq
 * @date 2018/11/27
 */

public class MainActivity extends BaseActivity<LaunchContract.View,LaunchContract.Presenter> implements LaunchContract.View {

    private MainFragment mMainFragment;

    @Override
    protected void onViewCreated() {
        initFragments();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initToolbar() {

    }

    @Override
    protected void initEventAndData() {

    }

    private void initFragments() {
        mMainFragment = MainFragment.newInstance();
        loadRootFragment(R.id.layout_content,mMainFragment);
        presenter.actionShowMain();
    }

    @Override
    public void showMain() {
        showHideFragment(mMainFragment);
    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
    }

    @NonNull
    @Override
    public LaunchContract.Presenter createPresenter() {
        return new LaunchPresenter();
    }
}
