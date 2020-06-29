package com.drumbeat.zface.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.drumbeat.zface.R;
import com.drumbeat.zface.ZFace;
import com.drumbeat.zface.config.CameraConfig;
import com.drumbeat.zface.config.RecognizeConfig;
import com.drumbeat.zface.config.ZFaceConfig;
import com.drumbeat.zface.listener.RecognizeListener;
import com.drumbeat.zface.target.ActivityTarget;
import com.drumbeat.zface.util.Logger;
import com.seetatech.seetaverify.camera.CameraCallbacks;
import com.seetatech.seetaverify.camera.CameraPreview2;

import java.util.Timer;
import java.util.TimerTask;

import static com.drumbeat.zface.constant.ErrorCode.ERROR_CAMERA_UNAVAILABLE;

/**
 * @author ZuoHailong
 * @date 2020/4/30
 */
public class VideoActivity extends Activity {

    // 宽高适配
    private CameraPreview2 mCameraPreview;
    // 高度适配
    private View viewMaskTop, viewMaskBottom;
    // 宽度适配
    private ImageView ivBlueBorderTop, ivBlueBorderBottom;

    //200毫秒检测一次
    private boolean isDetecting = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        initView();
        IntentFilter intentFilter = new IntentFilter("close_detactor");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    private void initView() {
        mCameraPreview = findViewById(R.id.camera_preview);
        viewMaskTop = findViewById(R.id.viewMaskTop);
        viewMaskBottom = findViewById(R.id.viewMaskBottom);
        ivBlueBorderTop = findViewById(R.id.ivBlueBorderTop);
        ivBlueBorderBottom = findViewById(R.id.ivBlueBorderBottom);
        int cameraPreviewWidth = CameraConfig.getInstance().getCameraPreviewWidth();
        int cameraPreviewHeight = CameraConfig.getInstance().getCameraPreviewHeight();
        Logger.i("cameraPreviewWidth -- " + String.valueOf(cameraPreviewWidth));
        Logger.i("cameraPreviewHeight -- " + String.valueOf(cameraPreviewHeight));
        setViewWH(mCameraPreview, cameraPreviewHeight, cameraPreviewWidth);
        setViewWH(viewMaskTop, 0, (cameraPreviewWidth - cameraPreviewHeight) / 2);
        setViewWH(viewMaskBottom, 0, (cameraPreviewWidth - cameraPreviewHeight) / 2);
        setViewWH(ivBlueBorderTop, cameraPreviewHeight, 0);
        setViewWH(ivBlueBorderBottom, cameraPreviewHeight, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        int uiOptions = decorView.getSystemUiVisibility()
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        mCameraPreview.setCameraCallbacks(mCameraCallbacks);
        // 200毫秒检测1次
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                isDetecting = false;
            }
        }, 1000, 200);
    }

    /**
     * 设置View宽高
     *
     * @param view   view
     * @param width  宽
     * @param height 高
     */
    private void setViewWH(@NonNull View view, int width, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (width > 0) {
            layoutParams.width = width;
        }
        if (height > 0) {
            layoutParams.height = height;
        }
        view.setLayoutParams(layoutParams);
    }

    private CameraCallbacks mCameraCallbacks = new CameraCallbacks() {
        @Override
        public void onCameraUnavailable(int errorCode) {
            Logger.e("camera unavailable, reason=%d" + errorCode);
            RecognizeListener recognizeListener = RecognizeConfig.getInstance().getRecognizeListener();
            if (recognizeListener != null) {
                recognizeListener.onFailure(ERROR_CAMERA_UNAVAILABLE, "camera unavailable, reason=%d" + errorCode);
            }
        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (!isDetecting) {
                ZFace.with(VideoActivity.this)
                        .recognizer()
                        .recognize(data, RecognizeConfig.getInstance().getRecognizeListener());
                isDetecting = true;
            }
        }
    };
}
