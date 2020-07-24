package com.drumbeat.zface.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.drumbeat.zface.ZFace;
import com.drumbeat.zface.config.ZFaceConfig;
import com.drumbeat.zface.constant.ErrorCode;
import com.drumbeat.zface.listener.CompareListener;
import com.drumbeat.zface.listener.DownloadListener;
import com.drumbeat.zface.listener.InitListener;
import com.drumbeat.zface.listener.RecognizeListener;
import com.drumbeat.zface.util.Logger;

public class MainActivity extends AppCompatActivity {

    private float[] featureDataRegister;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        ZFace.setConfig(ZFaceConfig.newBuilder()
                .setResource_model_download_base_url("") // model文件baseurl
                .setResource_so_download_base_url("") // so文件baseurl
                .build());
    }

    public void queryResource(View view) {
        showLoading();
        ZFace.with(this).resource().query(needDownload -> {
            if (needDownload) {
                showToastShort("需要下载");
                hideLoading();
                ZFace.with(this).resource().download(new DownloadListener() {
                    @Override
                    public void onSuccess() {
                        showToastShort("资源文件下载成功");
                    }

                    @Override
                    public void onFailure(ErrorCode errorCode, String errorMsg) {
                        showToastShort("资源文件下载失败，错误码：" + errorCode);
                    }
                });
            } else {
                hideLoading();
                showToastShort("不用下载");
            }
        });
    }

    public void init(View view) {
        showLoading();
        ZFace.with(this).recognizer().init(new InitListener() {
            @Override
            public void onSuccess() {
                hideLoading();
                showToastShort("初始化成功");
            }

            @Override
            public void onFailure(ErrorCode errorCode, String errorMsg) {
                hideLoading();
                showToastShort("初始化失败，错误码：" + errorCode);
            }
        });
    }

    public void register(View view) {
        ZFace.with(this)
                .recognizer()
                .recognize(new RecognizeListener() {
                    @Override
                    public void onSuccess(float[] featureData) {
                        if (featureData != null && featureData.length > 0) {
                            featureDataRegister = new float[featureData.length];
                            System.arraycopy(featureData, 0, featureDataRegister, 0, featureData.length);
                            showToastShort("人脸识别完成，已取得人脸特征数据");
                            Logger.i("人脸识别完成，已取得人脸特征数据");
                        }
                    }

                    @Override
                    public void onFailure(ErrorCode errorCode, String errorMsg) {
                        showToastShort("人脸识别失败，错误码：" + errorCode);
                    }
                });
    }

    public void compare(View view) {
        ZFace.with(this)
                .recognizer()
                .recognize(new RecognizeListener() {
                    @Override
                    public void onSuccess(float[] featureData) {
                        if (featureData != null && featureData.length > 0) {
                            ZFace.with(MainActivity.this).recognizer().compare(featureDataRegister, featureData, new CompareListener() {
                                @Override
                                public void onSuccess(float faceSimilar) {
                                    if (faceSimilar > 0.7) {
                                        showToastShort("比对成功");
                                        Logger.i("比对成功");
                                    } else {
                                        showToastShort("比对失败，相似度：" + faceSimilar);
                                    }
                                }

                                @Override
                                public void onFailure(ErrorCode errorCode, String errorMsg) {
                                    showToastShort("人脸比对失败，错误码：" + errorCode);
                                }
                            });
                        } else {
                            showToastShort("未检测到人脸特征数据");
                        }
                    }

                    @Override
                    public void onFailure(ErrorCode errorCode, String errorMsg) {
                        showToastShort("人脸识别失败，错误码：" + errorCode);
                    }
                });
    }

    private void showToastShort(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

}
