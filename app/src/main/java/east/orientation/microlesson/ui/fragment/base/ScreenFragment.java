package east.orientation.microlesson.ui.fragment.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.View;
import android.view.WindowManager;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.laifeng.sopcastsdk.configuration.AudioConfiguration;
import com.laifeng.sopcastsdk.configuration.VideoConfiguration;
import com.laifeng.sopcastsdk.constant.SopCastConstant;
import com.laifeng.sopcastsdk.controller.StreamController;
import com.laifeng.sopcastsdk.controller.StreamListener;
import com.laifeng.sopcastsdk.controller.audio.NormalAudioController;
import com.laifeng.sopcastsdk.controller.video.ScreenVideoController;
import com.laifeng.sopcastsdk.stream.packer.Packer;
import com.laifeng.sopcastsdk.stream.sender.Sender;
import com.laifeng.sopcastsdk.utils.SopCastLog;
import com.laifeng.sopcastsdk.utils.SopCastUtils;

/**
 * @author ljq
 * @date 2018/12/27
 * @description
 */

public abstract class ScreenFragment<V extends MvpView,P extends MvpPresenter<V>> extends BaseFragment<V,P> implements StreamListener{
    private static final String TAG = SopCastConstant.TAG;
    private static final int RECORD_REQUEST_CODE = 101;
    protected StreamController mStreamController;
    protected MediaProjectionManager mMediaProjectionManage;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void requestRecording() {
        if(!SopCastUtils.isOverLOLLIPOP()) {
            SopCastLog.d(TAG, "Device don't support screen recording.");
            return;
        }
        mMediaProjectionManage = (MediaProjectionManager) getContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent captureIntent = mMediaProjectionManage.createScreenCaptureIntent();
        startActivityForResult(captureIntent, RECORD_REQUEST_CODE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setStickyStyle();

        _mActivity.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
            if ((visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {// system bar可见
                if (!isSupportVisible()) setStickyStyle();
            }
        });
    }

    private void setStickyStyle() {
        int flag = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        _mActivity.getWindow().getDecorView().setSystemUiVisibility(flag);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECORD_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                NormalAudioController audioController = new NormalAudioController();
                ScreenVideoController videoController = new ScreenVideoController(mMediaProjectionManage, resultCode, data);
                mStreamController = new StreamController(videoController, audioController,this);
                requestRecordSuccess();
            } else {
                requestRecordFail();
            }
        }
    }

    protected void requestRecordSuccess() {

    }

    protected void requestRecordFail() {

    }

    public void setVideoConfiguration(VideoConfiguration videoConfiguration) {
        if(mStreamController != null) {
            mStreamController.setVideoConfiguration(videoConfiguration);
        }
    }

    public void setAudioConfiguration(AudioConfiguration audioConfiguration) {
        if(mStreamController != null) {
            mStreamController.setAudioConfiguration(audioConfiguration);
        }
    }

    protected void startRecording() {
        if(mStreamController != null) {
            mStreamController.start();
        }
    }

    protected void stopRecording() {
        if(mStreamController != null) {
            mStreamController.stop();
        }
    }

    protected void pauseRecording() {
        if(mStreamController != null) {
            mStreamController.pause();
        }
    }


    protected void resumeRecording() {
        if(mStreamController != null) {
            mStreamController.resume();
        }
    }

    protected void muteRecording(boolean mute) {
        if(mStreamController != null) {
            mStreamController.mute(mute);
        }
    }

    protected boolean setRecordBps(int bps) {
        if(mStreamController != null) {
            return mStreamController.setVideoBps(bps);
        } else {
            return false;
        }
    }

    protected void setRecordPacker(Packer packer) {
        if(mStreamController != null) {
            mStreamController.setPacker(packer);
        }
    }

    protected void setRecordSender(Sender sender) {
        if(mStreamController != null) {
            mStreamController.setSender(sender);
        }
    }

    @Override
    public void start() {
        _mActivity.runOnUiThread(this::streamStart);
    }

    @Override
    public void pause() {
        _mActivity.runOnUiThread(this::streamPause);
    }

    @Override
    public void resume() {
        _mActivity.runOnUiThread(this::streamResume);
    }

    @Override
    public void mute() {
        _mActivity.runOnUiThread(this::streamMute);
    }

    @Override
    public void stop() {
        _mActivity.runOnUiThread(this::streamStop);
    }

    @Override
    public void duration(long duration) {
        String s_duration = DateUtils.formatElapsedTime(duration / 1000);
        _mActivity.runOnUiThread(()->streamDuration(s_duration));
    }

    protected void streamStart() {

    }

    protected void streamPause() {

    }

    protected void streamResume() {

    }

    protected void streamMute() {

    }

    protected void streamStop() {

    }

    protected void streamDuration(String duration) {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            release();
        } else {
            setStickyStyle();
        }
    }

    @Override
    public boolean onBackPressedSupport() {
        release();
        stopRecording();
        return super.onBackPressedSupport();
    }

    private void release() {
        _mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        _mActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        _mActivity.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(null);
    }

    @Override
    public void onDestroy() {
        release();
        stopRecording();
        super.onDestroy();
    }
}
