package east.orientation.microlesson.ui.fragment.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.laifeng.sopcastsdk.configuration.AudioConfiguration;
import com.laifeng.sopcastsdk.configuration.VideoConfiguration;
import com.laifeng.sopcastsdk.constant.SopCastConstant;
import com.laifeng.sopcastsdk.controller.StreamController;
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

public abstract class ScreenFragment<V extends MvpView,P extends MvpPresenter<V>> extends BaseFragment<V,P> {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECORD_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                NormalAudioController audioController = new NormalAudioController();
                ScreenVideoController videoController = new ScreenVideoController(mMediaProjectionManage, resultCode, data);
                mStreamController = new StreamController(videoController, audioController);
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
    public void onDestroy() {
        stopRecording();
        super.onDestroy();
    }
}
