package com.drumbeat.zface.recognizer;

import android.content.Intent;
import android.hardware.Camera;
import android.os.HandlerThread;
import android.os.Process;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.drumbeat.zface.ZFace;
import com.drumbeat.zface.config.CameraConfig;
import com.drumbeat.zface.config.RecognizeConfig;
import com.drumbeat.zface.config.ZFaceConfig;
import com.drumbeat.zface.constant.CameraFacing;
import com.drumbeat.zface.constant.ErrorCode;
import com.drumbeat.zface.listener.Action;
import com.drumbeat.zface.listener.CompareListener;
import com.drumbeat.zface.listener.InitListener;
import com.drumbeat.zface.listener.RecognizeListener;
import com.drumbeat.zface.permission.Permission;
import com.drumbeat.zface.resource.ResourceUtil;
import com.drumbeat.zface.target.Target;
import com.drumbeat.zface.ui.VideoActivity;
import com.drumbeat.zface.util.CameraUtils;
import com.drumbeat.zface.util.FileUtils;
import com.drumbeat.zface.util.Logger;
import com.drumbeat.zface.util.SizeUtils;
import com.seeta.sdk.FaceAntiSpoofing;
import com.seeta.sdk.FaceDetector;
import com.seeta.sdk.FaceLandmarker;
import com.seeta.sdk.FaceRecognizer;
import com.seeta.sdk.SeetaDevice;
import com.seeta.sdk.SeetaImageData;
import com.seeta.sdk.SeetaModelSetting;
import com.seeta.sdk.SeetaPointF;
import com.seeta.sdk.SeetaRect;
import com.seeta.sdk.TrackingInfo;
import com.seetatech.silentliving.CachedStatusAndImage;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.drumbeat.zface.resource.ResourceUtil.SO_FILENAME_libSeetaAuthorize;
import static com.drumbeat.zface.resource.ResourceUtil.SO_FILENAME_libSeetaFaceAntiSpoofingX600;
import static com.drumbeat.zface.resource.ResourceUtil.SO_FILENAME_libSeetaFaceAntiSpoofingX600_java;
import static com.drumbeat.zface.resource.ResourceUtil.SO_FILENAME_libSeetaFaceDetector600;
import static com.drumbeat.zface.resource.ResourceUtil.SO_FILENAME_libSeetaFaceDetector600_java;
import static com.drumbeat.zface.resource.ResourceUtil.SO_FILENAME_libSeetaFaceLandmarker600;
import static com.drumbeat.zface.resource.ResourceUtil.SO_FILENAME_libSeetaFaceLandmarker600_java;
import static com.drumbeat.zface.resource.ResourceUtil.SO_FILENAME_libSeetaFaceRecognizer600;
import static com.drumbeat.zface.resource.ResourceUtil.SO_FILENAME_libSeetaFaceRecognizer600_java;
import static com.drumbeat.zface.resource.ResourceUtil.SO_FILENAME_libTenniS;
import static com.drumbeat.zface.resource.ResourceUtil.SO_FILENAME_libopencv_java3;

/**
 * @author ZuoHailong
 * @date 2020/6/10
 */
public class Recognizer implements RecognizerOption {

    private static FaceDetector faceDetector = null;
    private static FaceLandmarker faceLandmarker = null;
    private static FaceRecognizer faceRecognizer = null;
    private static FaceAntiSpoofing faceAntiSpoofing = null;

    private Mat matNv21;

    private HandlerThread mFaceTrackThread;
    private HandlerThread mFasThread;

    {
        mFaceTrackThread = new HandlerThread("FaceTrackThread", Process.THREAD_PRIORITY_MORE_FAVORABLE);
        mFasThread = new HandlerThread("FasThread", Process.THREAD_PRIORITY_MORE_FAVORABLE);
        mFaceTrackThread.start();
        mFasThread.start();
    }


    private SeetaImageData imageData;
    //初始状态
    private FaceAntiSpoofing.Status state = FaceAntiSpoofing.Status.DETECTING;

    private Target target;

    public static Recognizer getInstance() {
        return InstanceHelper.instance;
    }

    private static class InstanceHelper {
        private static Recognizer instance = new Recognizer();
    }

    private Recognizer() {
    }

    public Recognizer setTarget(Target target) {
        this.target = target;
        return this;
    }

    @Override
    public void init(InitListener initListener) {
        try {

            /*
             * 拷贝 so 文件到 jniLibs 目录下
             */

            String soExternalStoragePath_libSeetaAuthorize = ResourceUtil.getResFileExternalStoragePath(SO_FILENAME_libSeetaAuthorize);
            String soExternalStoragePath_libSeetaFaceAntiSpoofingX600 = ResourceUtil.getResFileExternalStoragePath(SO_FILENAME_libSeetaFaceAntiSpoofingX600);
            String soExternalStoragePath_libSeetaFaceAntiSpoofingX600_java = ResourceUtil.getResFileExternalStoragePath(SO_FILENAME_libSeetaFaceAntiSpoofingX600_java);
            String soExternalStoragePath_libSeetaFaceDetector600 = ResourceUtil.getResFileExternalStoragePath(SO_FILENAME_libSeetaFaceDetector600);
            String soExternalStoragePath_libSeetaFaceDetector600_java = ResourceUtil.getResFileExternalStoragePath(SO_FILENAME_libSeetaFaceDetector600_java);
            String soExternalStoragePath_libSeetaFaceLandmarker600 = ResourceUtil.getResFileExternalStoragePath(SO_FILENAME_libSeetaFaceLandmarker600);
            String soExternalStoragePath_libSeetaFaceLandmarker600_java = ResourceUtil.getResFileExternalStoragePath(SO_FILENAME_libSeetaFaceLandmarker600_java);
            String soExternalStoragePath_libSeetaFaceRecognizer600 = ResourceUtil.getResFileExternalStoragePath(SO_FILENAME_libSeetaFaceRecognizer600);
            String soExternalStoragePath_libSeetaFaceRecognizer600_java = ResourceUtil.getResFileExternalStoragePath(SO_FILENAME_libSeetaFaceRecognizer600_java);
            String soExternalStoragePath_libTenniS = ResourceUtil.getResFileExternalStoragePath(SO_FILENAME_libTenniS);
            String soExternalStoragePath_libopencv_java3 = ResourceUtil.getResFileExternalStoragePath(SO_FILENAME_libopencv_java3);

            // 检查so文件是否已经下载
            if (TextUtils.isEmpty(soExternalStoragePath_libSeetaAuthorize) ||
                    TextUtils.isEmpty(soExternalStoragePath_libSeetaFaceAntiSpoofingX600) ||
                    TextUtils.isEmpty(soExternalStoragePath_libSeetaFaceAntiSpoofingX600_java) ||
                    TextUtils.isEmpty(soExternalStoragePath_libSeetaFaceDetector600) ||
                    TextUtils.isEmpty(soExternalStoragePath_libSeetaFaceDetector600_java) ||
                    TextUtils.isEmpty(soExternalStoragePath_libSeetaFaceLandmarker600) ||
                    TextUtils.isEmpty(soExternalStoragePath_libSeetaFaceLandmarker600_java) ||
                    TextUtils.isEmpty(soExternalStoragePath_libSeetaFaceRecognizer600) ||
                    TextUtils.isEmpty(soExternalStoragePath_libSeetaFaceRecognizer600_java) ||
                    TextUtils.isEmpty(soExternalStoragePath_libTenniS) ||
                    TextUtils.isEmpty(soExternalStoragePath_libopencv_java3)) {
                initListener.onFailure(ErrorCode.ERROR_NO_RESOURCE, null);
                return;
            }

            String soJniLibPath_libSeetaAuthorize = ResourceUtil.getNativePath(target.getContext(), SO_FILENAME_libSeetaAuthorize);
            String soJniLibPath_libSeetaFaceAntiSpoofingX600 = ResourceUtil.getNativePath(target.getContext(), SO_FILENAME_libSeetaFaceAntiSpoofingX600);
            String soJniLibPath_libSeetaFaceAntiSpoofingX600_java = ResourceUtil.getNativePath(target.getContext(), SO_FILENAME_libSeetaFaceAntiSpoofingX600_java);
            String soJniLibPath_libSeetaFaceDetector600 = ResourceUtil.getNativePath(target.getContext(), SO_FILENAME_libSeetaFaceDetector600);
            String soJniLibPath_libSeetaFaceDetector600_java = ResourceUtil.getNativePath(target.getContext(), SO_FILENAME_libSeetaFaceDetector600_java);
            String soJniLibPath_libSeetaFaceLandmarker600 = ResourceUtil.getNativePath(target.getContext(), SO_FILENAME_libSeetaFaceLandmarker600);
            String soJniLibPath_libSeetaFaceLandmarker600_java = ResourceUtil.getNativePath(target.getContext(), SO_FILENAME_libSeetaFaceLandmarker600_java);
            String soJniLibPath_libSeetaFaceRecognizer600 = ResourceUtil.getNativePath(target.getContext(), SO_FILENAME_libSeetaFaceRecognizer600);
            String soJniLibPath_libSeetaFaceRecognizer600_java = ResourceUtil.getNativePath(target.getContext(), SO_FILENAME_libSeetaFaceRecognizer600_java);
            String soJniLibPath_libTenniS = ResourceUtil.getNativePath(target.getContext(), SO_FILENAME_libTenniS);
            String soJniLibPath_libopencv_java3 = ResourceUtil.getNativePath(target.getContext(), SO_FILENAME_libopencv_java3);

            // 拷贝 so 文件到 jniLibs 目录下
            if (copySoToJniLibsPath(soExternalStoragePath_libSeetaAuthorize, soJniLibPath_libSeetaAuthorize) &&
                    copySoToJniLibsPath(soExternalStoragePath_libSeetaFaceAntiSpoofingX600, soJniLibPath_libSeetaFaceAntiSpoofingX600) &&
                    copySoToJniLibsPath(soExternalStoragePath_libSeetaFaceAntiSpoofingX600_java, soJniLibPath_libSeetaFaceAntiSpoofingX600_java) &&
                    copySoToJniLibsPath(soExternalStoragePath_libSeetaFaceDetector600, soJniLibPath_libSeetaFaceDetector600) &&
                    copySoToJniLibsPath(soExternalStoragePath_libSeetaFaceDetector600_java, soJniLibPath_libSeetaFaceDetector600_java) &&
                    copySoToJniLibsPath(soExternalStoragePath_libSeetaFaceLandmarker600, soJniLibPath_libSeetaFaceLandmarker600) &&
                    copySoToJniLibsPath(soExternalStoragePath_libSeetaFaceLandmarker600_java, soJniLibPath_libSeetaFaceLandmarker600_java) &&
                    copySoToJniLibsPath(soExternalStoragePath_libSeetaFaceRecognizer600, soJniLibPath_libSeetaFaceRecognizer600) &&
                    copySoToJniLibsPath(soExternalStoragePath_libSeetaFaceRecognizer600_java, soJniLibPath_libSeetaFaceRecognizer600_java) &&
                    copySoToJniLibsPath(soExternalStoragePath_libTenniS, soJniLibPath_libTenniS) &&
                    copySoToJniLibsPath(soExternalStoragePath_libopencv_java3, soJniLibPath_libopencv_java3)) {

                // 全部拷贝成功，加载so文件
                System.load(soJniLibPath_libTenniS);
                System.load(soJniLibPath_libSeetaAuthorize);
                System.load(soJniLibPath_libSeetaFaceAntiSpoofingX600);
                System.load(soJniLibPath_libSeetaFaceDetector600);
                System.load(soJniLibPath_libSeetaFaceLandmarker600);
                System.load(soJniLibPath_libSeetaFaceRecognizer600);
                System.load(soJniLibPath_libSeetaFaceAntiSpoofingX600_java);
                System.load(soJniLibPath_libSeetaFaceDetector600_java);
                System.load(soJniLibPath_libSeetaFaceLandmarker600_java);
                System.load(soJniLibPath_libSeetaFaceRecognizer600_java);
                System.load(soJniLibPath_libopencv_java3);

                /*
                 * 实例化各检测器
                 */
                // 使用机器学习模型文件，构建人脸检测器
                faceDetector = new FaceDetector(
                        new SeetaModelSetting(0,
                                new String[]{ResourceUtil.getResFileExternalStoragePath(ResourceUtil.MODEL_FACE_DATECTOR)},
                                SeetaDevice.SEETA_DEVICE_AUTO));
                // 使用机器学习模型文件，构建人脸关键点定位器
                faceLandmarker = new FaceLandmarker(
                        new SeetaModelSetting(0,
                                new String[]{ResourceUtil.getResFileExternalStoragePath(ResourceUtil.MODEL_FACE_LANDMARKER_PTS5)},
                                SeetaDevice.SEETA_DEVICE_AUTO));

                // 使用机器学习模型文件，构建人脸识别器
                faceRecognizer = new FaceRecognizer(
                        new SeetaModelSetting(0,
                                new String[]{ResourceUtil.getResFileExternalStoragePath(ResourceUtil.MODEL_FACE_RECOGNIZER)},
                                SeetaDevice.SEETA_DEVICE_AUTO));

                // 使用机器学习模型文件，构建活体识别器
                faceAntiSpoofing = new FaceAntiSpoofing(
                        new SeetaModelSetting(0,
                                new String[]{ResourceUtil.getResFileExternalStoragePath(ResourceUtil.MODEL_FAS_FIRST),
                                        ResourceUtil.getResFileExternalStoragePath(ResourceUtil.MODEL_FAS_SECOND)},
                                SeetaDevice.SEETA_DEVICE_AUTO));

                float clarity = 0.30f;
                float fasThresh = 0.80f;
                faceAntiSpoofing.SetThreshold(clarity, fasThresh);
                // 设置允许检测的最小人脸像素值
                faceDetector.set(FaceDetector.Property.PROPERTY_MIN_FACE_SIZE, 80);
                initListener.onSuccess();
                Logger.i("Recognizer initialized successfully.");
            } else {
                // 有so文件未拷贝成功
                initListener.onFailure(ErrorCode.ERROR_LOAD_RESOURCE_FAIL, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("Recognition initialization failed.");
            initListener.onFailure(ErrorCode.ERROR_INIT_FAIL, null);
        }
    }

    @Override
    public void recognize(@NonNull RecognizeListener recognizeListener) {
        if (faceDetector == null || faceLandmarker == null || faceRecognizer == null || faceAntiSpoofing == null) {
            recognizeListener.onFailure(ErrorCode.ERROR_NO_INIT, null);
            return;
        }
        RecognizeConfig.getInstance().setRecognizeListener(recognizeListener);
        ZFace.with(target.getContext())
                .permission()
                .permission(Permission.CAMERA)
                .onGranted(permissions -> {
                    // 查询最合适的相机预览尺寸
                    Camera.Size closestSize = CameraUtils.getClosestSize(CameraFacing.CAMERA_FACING_FRONT, SizeUtils.dp2px(target.getContext(), CameraConfig.PREVIEW_SIDE_LENGTH));
                    if (closestSize != null) {
                        CameraConfig.getInstance().setCameraPreviewWidth(closestSize.width);
                        CameraConfig.getInstance().setCameraPreviewHeight(closestSize.height);

                        matNv21 = new Mat(CameraConfig.getInstance().getCameraPreviewHeight() + CameraConfig.getInstance().getCameraPreviewHeight() / 2,
                                CameraConfig.getInstance().getCameraPreviewWidth(), CvType.CV_8UC1);
                        imageData = new SeetaImageData(CameraConfig.getInstance().getCameraPreviewHeight(),
                                CameraConfig.getInstance().getCameraPreviewWidth(), 3);
                    }
                    // 启动预览检测页
                    target.getContext().startActivity(new Intent(target.getContext(), VideoActivity.class));
                })
                .start();
    }

    @Override
    public void recognize(@NonNull byte[] data, @NonNull RecognizeListener recognizeListener) {
        if (faceDetector == null || faceLandmarker == null || faceRecognizer == null || faceAntiSpoofing == null) {
            recognizeListener.onFailure(ErrorCode.ERROR_NO_INIT, null);
            return;
        }
        RecognizeConfig.getInstance().setRecognizeListener(recognizeListener);
        detect(data);
    }

    @Override
    public void compare(@NonNull float[] feature1, @NonNull float[] feature2, @NonNull CompareListener compareListener) {
        if (faceRecognizer == null) {
            compareListener.onFailure(ErrorCode.ERROR_NO_INIT, null);
            return;
        }
        compareListener.onSuccess(faceRecognizer.CalculateSimilarity(feature1, feature2));
    }

    @Override
    public void close() {
        mFaceTrackThread.quitSafely();
        mFasThread.quitSafely();
        LocalBroadcastManager.getInstance(target.getContext()).sendBroadcast(new Intent("close_detactor"));
    }

    /**
     * 人脸检测
     *
     * @param data 视频帧数据
     */
    public void detect(byte[] data) {
        TrackingInfo trackingInfo = new TrackingInfo();

        matNv21.put(0, 0, data);
        trackingInfo.matBgr = new Mat(CameraConfig.getInstance().getCameraPreviewHeight(), CameraConfig.getInstance().getCameraPreviewWidth(), CvType.CV_8UC3);
        trackingInfo.matGray = new Mat();

        Imgproc.cvtColor(matNv21, trackingInfo.matBgr, Imgproc.COLOR_YUV2BGR_NV21);

        Core.transpose(trackingInfo.matBgr, trackingInfo.matBgr);
        Core.flip(trackingInfo.matBgr, trackingInfo.matBgr, 0);
        Core.flip(trackingInfo.matBgr, trackingInfo.matBgr, 1);

        Imgproc.cvtColor(trackingInfo.matBgr, trackingInfo.matGray, Imgproc.COLOR_BGR2GRAY);

        trackingFace(trackingInfo);
    }

    /**
     * 追踪人脸
     *
     * @param trackingInfo
     */
    private void trackingFace(TrackingInfo trackingInfo) {

        trackingInfo.matBgr.get(0, 0, imageData.data);
        SeetaRect[] faces = faceDetector.Detect(imageData);

        trackingInfo.faceInfo.x = 0;
        trackingInfo.faceInfo.y = 0;
        trackingInfo.faceInfo.width = 0;
        trackingInfo.faceInfo.height = 0;

        if (faces.length != 0) {
            // 找到最大人脸，对最大人脸进行检测
            int maxIndex = 0;
            double maxWidth = 0;
            for (int i = 0; i < faces.length; ++i) {
                if (faces[i].width > maxWidth) {
                    maxIndex = i;
                    maxWidth = faces[i].width;
                }
            }

            trackingInfo.faceInfo = faces[maxIndex];

            trackingInfo.faceRect.x = faces[maxIndex].x;
            trackingInfo.faceRect.y = faces[maxIndex].y;
            trackingInfo.faceRect.width = faces[maxIndex].width;
            trackingInfo.faceRect.height = faces[maxIndex].height;

            livenessDetect(trackingInfo);
        }
    }

    //超过该帧数后才进行活体识别，过渡摄像头曝光过程
    static int frameNumThreshold = 5;

    /**
     * 活体检测
     *
     * @param trackingInfo
     */
    private void livenessDetect(TrackingInfo trackingInfo) {
        trackingInfo.matGray = new Mat();
        trackingInfo.matBgr.get(0, 0, imageData.data);
        //存在人脸
        if (trackingInfo.faceInfo.width != 0) {
            //摄像头过渡
            if (CachedStatusAndImage.currentFrameNum < frameNumThreshold) {
                ++CachedStatusAndImage.currentFrameNum;
            } else {
                //特征点检测
                SeetaPointF[] points = new SeetaPointF[5];
                faceLandmarker.mark(imageData, trackingInfo.faceInfo, points);
                SeetaRect faceInfo = trackingInfo.faceInfo;

                state = faceAntiSpoofing.Predict(imageData, faceInfo, points);

                RecognizeListener recognizeListener = RecognizeConfig.getInstance().getRecognizeListener();
                if (recognizeListener != null) {
                    switch (state) {
                        case SPOOF:
                            recognizeListener.onFailure(ErrorCode.ERROR_SPOOF, null);
                            break;
                        case FUZZY:
                            recognizeListener.onFailure(ErrorCode.ERROR_FUZZY, null);
                            break;
                        case REAL:
                            extractFaceFeature(trackingInfo);
                            break;
                        default:
                            recognizeListener.onFailure(ErrorCode.ERROR_NO_FACE, null);
                            break;
                    }

                }
            }
        }
    }

    /**
     * 提取人脸特征数据
     *
     * @param trackingInfo
     */
    private void extractFaceFeature(TrackingInfo trackingInfo) {
        trackingInfo.matGray = new Mat();
        trackingInfo.matBgr.get(0, 0, imageData.data);

        float[] feats = new float[faceRecognizer.GetExtractFeatureSize()];

        // 存在人脸
        if (trackingInfo.faceInfo.width != 0) {
            //特征点检测
            SeetaPointF[] points = new SeetaPointF[5];
            faceLandmarker.mark(imageData, trackingInfo.faceInfo, points);
            if (faceLandmarker == null) {
                throw new UnsupportedOperationException("请初始化ZFace：ZFace.init()");
            }

            //特征提取
            faceRecognizer.Extract(imageData, points, feats);
            if (faceRecognizer == null) {
                throw new UnsupportedOperationException("请初始化ZFace：ZFace.init()");
            }

            RecognizeListener recognizeListener = RecognizeConfig.getInstance().getRecognizeListener();
            if (feats.length > 0 && recognizeListener != null) {
                recognizeListener.onSuccess(feats);
            }
        }
    }

    /**
     * 将so文件拷贝到目标文件夹
     */
    private boolean copySoToJniLibsPath(String srcPath, String destPath) {
        File fileDest = new File(destPath);
        if (fileDest.exists() && fileDest.length() == new File(srcPath).length()) {
            return true;
        }
        try {
            fileDest.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 拷贝
        return FileUtils.copyFile(srcPath, destPath, () -> true);
    }

}
