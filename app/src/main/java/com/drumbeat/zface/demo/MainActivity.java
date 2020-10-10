package com.drumbeat.zface.demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.drumbeat.zface.ZFace;
import com.drumbeat.zface.config.CameraConfig;
import com.drumbeat.zface.config.ZFaceConfig;
import com.drumbeat.zface.constant.ErrorCode;
import com.drumbeat.zface.listener.CompareListener;
import com.drumbeat.zface.listener.DownloadListener;
import com.drumbeat.zface.listener.InitListener;
import com.drumbeat.zface.listener.RecognizeListener;
import com.drumbeat.zface.util.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private float[] featureDataRegister;
    private ProgressBar progressBar;
    private ImageView iv_header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        iv_header = findViewById(R.id.iv_header);
        ZFace.setConfig(ZFaceConfig.newBuilder()
                .setResource_model_download_base_url("https://drumbeat-update-app.oss-cn-hangzhou.aliyuncs.com/face/model") // model文件baseurl
                .setResource_so_download_base_url("https://drumbeat-update-app.oss-cn-hangzhou.aliyuncs.com/face/so") // so文件baseurl
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
                    public void onSuccess(float[] featureData, byte[] faceData) {
                        if (featureData != null && featureData.length > 0) {
                            featureDataRegister = new float[featureData.length];
                            System.arraycopy(featureData, 0, featureDataRegister, 0, featureData.length);
                            showToastShort("人脸识别完成，已取得人脸特征数据");
                            Logger.i("人脸识别完成，已取得人脸特征数据");
                            iv_header.setImageBitmap(convertBmp(rotateBitmap(270, bytes2Bitmap(faceData))));

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
                    public void onSuccess(float[] featureData, byte[] imgData) {
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

    /**
     * Bytes to bitmap.
     *
     * @param bytes The bytes.
     * @return bitmap
     */
    public static Bitmap bytes2Bitmap(final byte[] data) {
        Bitmap bitmap = null;
        YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, CameraConfig.getInstance().getCameraPreviewWidth(), CameraConfig.getInstance().getCameraPreviewHeight(), null); //20、20分别是图的宽度与高度
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, CameraConfig.getInstance().getCameraPreviewWidth(), CameraConfig.getInstance().getCameraPreviewHeight()), 80, baos);//80--JPG图片的质量[0-100],100最高
        byte[] jdata = baos.toByteArray();
        bitmap = BitmapFactory.decodeByteArray(jdata, 0, jdata.length);

        return bitmap;
    }

    //旋转图片
    public static Bitmap rotateBitmap(int angle, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap rotation = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, true);
        return rotation;
    }


    public Bitmap convertBmp(Bitmap bmp) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1); // 镜像水平翻转
        Bitmap convertBmp = Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);

        return convertBmp;
    }
}
