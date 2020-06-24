package com.drumbeat.zface.resource;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.drumbeat.zface.R;
import com.drumbeat.zface.constant.ErrorCode;
import com.drumbeat.zface.listener.DownloadListener;
import com.drumbeat.zface.ui.BasePopupWindow;
import com.drumbeat.zface.util.WeakHandler;
import com.yanzhenjie.kalle.Kalle;
import com.yanzhenjie.kalle.download.Callback;
import com.yanzhenjie.kalle.download.Download;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 资源下载弹窗
 *
 * @author ZuoHailong
 * @date 2020/6/12
 */
class ResourcePopupWindow extends BasePopupWindow {

    private View contentView;
    private ProgressBar progressBar;
    private TextView tvSpeed;
    private TextView tvPercent;

    private DownloadListener downloadListener;
    private String resLengthS = "0 MB";
    /**
     * 标记是否要终止下载动作<br/>
     * 当出现下载异常时，终止下载动作
     */
    private boolean isDownloading = false;
    private CountDownTimer countDownTimer;
    private List<String> resUrlList = new ArrayList<>();

    ResourcePopupWindow(Context context) {
        super(context, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.context = context;
        setTouchable(false);
        setOutsideTouchable(false);
        setAnimationStyle(R.style.mypopwindow_anim_style);
        contentView = LayoutInflater.from(context).inflate(R.layout.popup_resource, null);
        setContentView(contentView);
        initView();
    }

    private int index;

    /**
     * 弹出资源下载框，并立即开始下载资源文件
     *
     * @param resUrlList       待下载资源url集合。入参前保证resUrlList中有数据，在这里不再重复判定。
     * @param downloadListener 下载监听
     */
    void showAndDownload(List<String> resUrlList, DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
        this.resUrlList.clear();
        this.resUrlList.addAll(resUrlList);
        showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
        index = 0;
        // 一秒一次
        countDownTimer = new CountDownTimer(60 * 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (!isDownloading) {
                    isDownloading = true;
                    download(resUrlList.get(index));
                }
            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
    }

    private void initView() {
        progressBar = contentView.findViewById(R.id.progressBar);
        tvSpeed = contentView.findViewById(R.id.tvSpeed);
        tvPercent = contentView.findViewById(R.id.tvPercent);
    }

    private void download(String resUrl) {
        String resName = null;
        String[] split = resUrl.split("/");
        if (split.length > 0) {
            resName = split[split.length - 1];
        }
        Kalle.Download.get(resUrl)
                .directory(ResourceUtil.getResFileExternalStorageDir())
                .fileName(resName)
                .onProgress(new Download.ProgressBar() {
                    @Override
                    public void onProgress(int progress, long byteCount, long speed) {
                        // KB/s
                        float speedF = speed / 1024F;
                        String speedS = new BigDecimal(speedF).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + " KB/s";
                        if (speedF > 1024) {
                            // MB/s
                            speedF = speedF / 1024F;
                            speedS = new BigDecimal(speedF).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + " MB/s";
                        }
                        Bundle bundle = new Bundle();
                        bundle.putInt("progress", progress);
                        bundle.putString("speed", "本次下载 " + resLengthS + "，速度 " + speedS);
                        Message msg = new Message();
                        msg.setData(bundle);
                        msg.what = CODE_UPDATE_PROGRESS;
                        mHandler.sendMessage(msg);
                    }
                })
                .perform(new Callback() {
                    @Override
                    public void onStart() {
                        // 待下载文件大小
                        ResourceUtil.getResLength(resUrl, resLength -> {
                            // KB/s
                            float resLengthF = resLength / 1024F;
                            resLengthS = new BigDecimal(resLengthF).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + " KB";
                            if (resLengthF > 1024) {
                                // MB/s
                                resLengthF = resLengthF / 1024F;
                                resLengthS = new BigDecimal(resLengthF).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + " MB";
                            }

                            Bundle bundle = new Bundle();
                            bundle.putInt("progress", 0);
                            bundle.putString("speed", "本次下载 " + resLengthS + "，速度 0 MB/s");
                            Message msg = new Message();
                            msg.setData(bundle);
                            msg.what = CODE_UPDATE_PROGRESS;
                            mHandler.sendMessage(msg);
                        });
                    }

                    @Override
                    public void onFinish(String path) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("progress", 100);
                        bundle.putString("speed", "本次下载 " + resLengthS + "，速度 0 MB/s");
                        Message msg = new Message();
                        msg.setData(bundle);
                        msg.what = CODE_UPDATE_PROGRESS;
                        mHandler.sendMessage(msg);
                        // 下载完成
                        if (++index >= resUrlList.size()) {
                            mHandler.sendEmptyMessage(CODE_DOWNLOAD_SUCCESS);
                        }
                        isDownloading = false;
                    }

                    @Override
                    public void onException(Exception e) {
                        mHandler.sendEmptyMessage(CODE_DOWNLOAD_FAIL);
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onEnd() {

                    }
                });
    }

    private final static int CODE_UPDATE_PROGRESS = 101;
    private final static int CODE_DOWNLOAD_SUCCESS = 103;
    private final static int CODE_DOWNLOAD_FAIL = 104;

    private WeakHandler mHandler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == CODE_UPDATE_PROGRESS) {
                int progress = msg.getData().getInt("progress");
                String speed = msg.getData().getString("speed");
                progressBar.setProgress(progress);
                tvPercent.setText(progress + "%");
                tvSpeed.setText(speed);
            } else if (msg.what == CODE_DOWNLOAD_SUCCESS) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                dismiss();
                downloadListener.onSuccess();
            } else if (msg.what == CODE_DOWNLOAD_FAIL) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                dismiss();
                downloadListener.onFailure(ErrorCode.ERROR_DOWNLOAD, null);
            }
            return true;
        }
    });

}
