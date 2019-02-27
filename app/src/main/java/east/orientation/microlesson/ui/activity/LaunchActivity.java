package east.orientation.microlesson.ui.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import east.orientation.microlesson.R;
import east.orientation.microlesson.app.ActivityCollector;
import east.orientation.microlesson.mvp.contract.LaunchContract;
import east.orientation.microlesson.mvp.presenter.LaunchPresenter;
import east.orientation.microlesson.service.SyncService;
import east.orientation.microlesson.ui.activity.base.BaseActivity;
import east.orientation.microlesson.ui.fragment.MainFragment;
import east.orientation.microlesson.utils.RxToast;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

/**
 * @author ljq
 * @date 2018/11/27
 */
@RuntimePermissions
public class LaunchActivity extends BaseActivity<LaunchContract.View,LaunchContract.Presenter> implements LaunchContract.View {

    private MainFragment mMainFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // runtime permission
        LaunchActivityPermissionsDispatcher.getRuntimePermissionWithPermissionCheck(this);
    }

    @Override
    protected void onViewCreated() {
        initFragments();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_launch;
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
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
        } else if (RxToast.doubleClickExit()) {
            //ActivityCompat.finishAfterTransition(this);
            ActivityCollector.getInstance().exitApp();
        }

    }

    @NonNull
    @Override
    public LaunchContract.Presenter createPresenter() {
        return new LaunchPresenter();
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO})
    void getRuntimePermission() {
        // 开启同步服务
        startService(new Intent(this, SyncService.class));
    }

    @OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void show() {
        new AlertDialog.Builder(this).setTitle("Tips")
                .setMessage("不授予权限则无法正常使用，是否授予权限？")
                .setPositiveButton("确认", (dialog, which) -> {
                    LaunchActivityPermissionsDispatcher.getRuntimePermissionWithPermissionCheck(this);
                })
                .setNegativeButton("取消",(dialog, which) -> dialog.dismiss())
                .show();
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LaunchActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
