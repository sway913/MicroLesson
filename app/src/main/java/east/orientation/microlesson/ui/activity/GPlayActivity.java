package east.orientation.microlesson.ui.activity;

import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import east.orientation.microlesson.R;
import east.orientation.microlesson.local.Common;
import east.orientation.microlesson.local.dao.Project;
import east.orientation.microlesson.mvp.contract.PlayContract;
import east.orientation.microlesson.mvp.presenter.PlayPresenter;
import east.orientation.microlesson.ui.activity.base.BaseActivity;

/**
 * @author ljq
 * @date 2019/2/26
 * @description
 */

public class GPlayActivity extends BaseActivity<PlayContract.View,PlayContract.Presenter> implements PlayContract.View  {
    private StandardGSYVideoPlayer videoPlayer;

    private OrientationUtils orientationUtils;

    @Override
    protected void onViewCreated() {
        Project project = getIntent().getParcelableExtra(Common.KEY_PROJECTS);
        if (project != null) init(project);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gplay;
    }

    @Override
    protected void initToolbar() {

    }

    @Override
    protected void initEventAndData() {

    }

    private void init(Project project) {
        videoPlayer =  findViewById(R.id.video_player);
        videoPlayer.setUp(project.getFilePath(), true, project.getTitle());
        //增加title
        videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        //设置返回键
        videoPlayer.getBackButton().setVisibility(View.VISIBLE);
        //设置旋转
        orientationUtils = new OrientationUtils(this, videoPlayer);
        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        videoPlayer.getFullscreenButton().setOnClickListener(v -> orientationUtils.resolveByClick());
        //是否可以滑动调整
        videoPlayer.setIsTouchWiget(true);
        //设置返回按键功能
        videoPlayer.getBackButton().setOnClickListener(v -> onBackPressed());

        new Handler().postDelayed(()->videoPlayer.startPlayLogic(),800);
    }


    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayer.onVideoResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    @Override
    public void onBackPressedSupport() {
        //先返回正常状态
        if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            videoPlayer.getFullscreenButton().performClick();
            return;
        }
        //释放所有
        videoPlayer.setVideoAllCallBack(null);
        super.onBackPressedSupport();
    }

    @NonNull
    @Override
    public PlayContract.Presenter createPresenter() {
        return new PlayPresenter();
    }
}
