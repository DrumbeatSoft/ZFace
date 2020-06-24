package com.drumbeat.zface.resource;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.drumbeat.zface.ZFace;
import com.drumbeat.zface.util.FileUtils;
import com.drumbeat.zface.util.PathUtils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 资源文件工具类
 *
 * @author ZuoHailong
 * @date 2020/6/11
 */
public class ResourceUtil {

    public final static String MODEL_FACE_DATECTOR = "face_detector.csta";
    public final static String MODEL_FACE_LANDMARKER_PTS5 = "face_landmarker_pts5.csta";
    public final static String MODEL_FACE_RECOGNIZER = "face_recognizer.csta";
    public final static String MODEL_FAS_FIRST = "fas_first.csta";
    public final static String MODEL_FAS_SECOND = "fas_second.csta";

    private final static String SO_libSeetaAuthorize = "SeetaAuthorize";
    private final static String SO_libSeetaFaceAntiSpoofingX600 = "SeetaFaceAntiSpoofingX600";
    private final static String SO_libSeetaFaceAntiSpoofingX600_java = "SeetaFaceAntiSpoofingX600_java";
    private final static String SO_libSeetaFaceDetector600 = "SeetaFaceDetector600";
    private final static String SO_libSeetaFaceDetector600_java = "SeetaFaceDetector600_java";
    private final static String SO_libSeetaFaceLandmarker600 = "SeetaFaceLandmarker600";
    private final static String SO_libSeetaFaceLandmarker600_java = "SeetaFaceLandmarker600_java";
    private final static String SO_libSeetaFaceRecognizer600 = "SeetaFaceRecognizer600";
    private final static String SO_libSeetaFaceRecognizer600_java = "SeetaFaceRecognizer600_java";
    private final static String SO_libTenniS = "TenniS";
    private final static String SO_libopencv_java3 = "opencv_java3";

    public final static String SO_FILENAME_libSeetaAuthorize = "lib" + SO_libSeetaAuthorize + ".so";
    public final static String SO_FILENAME_libSeetaFaceAntiSpoofingX600 = "lib" + SO_libSeetaFaceAntiSpoofingX600 + ".so";
    public final static String SO_FILENAME_libSeetaFaceAntiSpoofingX600_java = "lib" + SO_libSeetaFaceAntiSpoofingX600_java + ".so";
    public final static String SO_FILENAME_libSeetaFaceDetector600 = "lib" + SO_libSeetaFaceDetector600 + ".so";
    public final static String SO_FILENAME_libSeetaFaceDetector600_java = "lib" + SO_libSeetaFaceDetector600_java + ".so";
    public final static String SO_FILENAME_libSeetaFaceLandmarker600 = "lib" + SO_libSeetaFaceLandmarker600 + ".so";
    public final static String SO_FILENAME_libSeetaFaceLandmarker600_java = "lib" + SO_libSeetaFaceLandmarker600_java + ".so";
    public final static String SO_FILENAME_libSeetaFaceRecognizer600 = "lib" + SO_libSeetaFaceRecognizer600 + ".so";
    public final static String SO_FILENAME_libSeetaFaceRecognizer600_java = "lib" + SO_libSeetaFaceRecognizer600_java + ".so";
    public final static String SO_FILENAME_libTenniS = "lib" + SO_libTenniS + ".so";
    public final static String SO_FILENAME_libopencv_java3 = "lib" + SO_libopencv_java3 + ".so";

    /**
     * 模型文件在文件服务器所处位置的前置路径
     */
//    private final static String RESOURCE_MODEL_DOWNLOAD_BASE_URL = "https://drumbeat-update-app.oss-cn-hangzhou.aliyuncs.com/face/model/";

    /**
     * so文件在文件服务器所处位置的前置路径
     */
//    private final static String RESOURCE_SO_DOWNLOAD_BASE_URL = "https://drumbeat-update-app.oss-cn-hangzhou.aliyuncs.com/face/so/";

    /**
     * 获取 so 文件在应用下 jniLibs 的存储路径
     *
     * @param soName so文件名，作为常量在 {@link ResourceUtil} 中声明
     * @return so文件在应用下 jniLibs 的存储路径
     */
    public static String getNativePath(Context context, String soName) {
        return getNativeDir(context) + File.separator + soName;
    }

    /**
     * 获取应用下 jniLibs 的路径
     *
     * @return 资源文件存储的文件夹路径
     */
    private static String getNativeDir(Context context) {
        return context.getDir("jniLibs", Activity.MODE_PRIVATE).getAbsolutePath();
    }

    /**
     * 获取资源文件在手机中的存储路径
     *
     * @param resName 资源文件名，作为常量在 {@link ResourceUtil} 中声明
     * @return 资源文件路径
     */
    public static String getResFileExternalStoragePath(String resName) {
        String filePath = getResFileExternalStorageDir() + File.separator + resName;
        if (!FileUtils.isFileExists(filePath)) {
            return null;
        }
        return filePath;
    }

    /**
     * 获取资源文件在手机中的存储的文件夹路径
     *
     * @return 资源文件存储的文件夹路径
     */
    static String getResFileExternalStorageDir() {
        String resFileDir = PathUtils.getExternalStoragePath() + "/Android/com/drumbeat";
        boolean existsDir = FileUtils.createOrExistsDir(resFileDir);
        if (existsDir) {
            return resFileDir;
        }
        return null;
    }


    /**
     * 获取远端资源文件长度<br/>
     * 注意不要在主线程调用此方法
     *
     * @param resUrl 资源文件链接
     */
    static void getResLength(String resUrl, GetResLengthListener listener) {
        final long[] resLength = new long[1];
        Thread thread = new Thread(() -> {
            try {
                URL url = new URL(resUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                resLength[0] = httpURLConnection.getContentLength();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        listener.onGetResLength(resLength[0]);

    }

    /**
     * 获取远程资源文件大小的回调
     */
    public interface GetResLengthListener {
        void onGetResLength(long resLength);
    }

    /**
     * 获取需下载资源文件的url集合的回调
     */
    public interface GetNeedDownloadResUrlListListener {
        void onGetNeedDownloadResUrlList(List<String> needDownloadResUrlList);
    }

    /**
     * 获取需要下载的资源的url集合
     */
    static void getNeedDownloadResUrlList(GetNeedDownloadResUrlListListener needDownloadResUrlListListener) {
        List<String> needDownloadResUrlList = new ArrayList<>();
        Thread thread = new Thread(() -> {
            String RESOURCE_MODEL_DOWNLOAD_BASE_URL = ZFace.getConfig().getResource_model_download_base_url();
            String RESOURCE_SO_DOWNLOAD_BASE_URL = ZFace.getConfig().getResource_so_download_base_url();
            if (TextUtils.isEmpty(RESOURCE_MODEL_DOWNLOAD_BASE_URL)) {
                throw new UnsupportedOperationException("resource_model_download_base_url is missing.");
            }
            if (TextUtils.isEmpty(RESOURCE_SO_DOWNLOAD_BASE_URL)) {
                throw new UnsupportedOperationException("resource_so_download_base_url is missing.");
            }
            RESOURCE_MODEL_DOWNLOAD_BASE_URL = RESOURCE_MODEL_DOWNLOAD_BASE_URL.endsWith(File.separator) ?
                    RESOURCE_MODEL_DOWNLOAD_BASE_URL :
                    RESOURCE_MODEL_DOWNLOAD_BASE_URL + File.separator;
            RESOURCE_SO_DOWNLOAD_BASE_URL = RESOURCE_SO_DOWNLOAD_BASE_URL.endsWith(File.separator) ?
                    RESOURCE_SO_DOWNLOAD_BASE_URL :
                    RESOURCE_SO_DOWNLOAD_BASE_URL + File.separator;
            /*
             * model
             */
            if (isNeedDownload(RESOURCE_MODEL_DOWNLOAD_BASE_URL, MODEL_FACE_DATECTOR)) {
                needDownloadResUrlList.add(RESOURCE_MODEL_DOWNLOAD_BASE_URL + MODEL_FACE_DATECTOR);
            }
            if (isNeedDownload(RESOURCE_MODEL_DOWNLOAD_BASE_URL, MODEL_FACE_LANDMARKER_PTS5)) {
                needDownloadResUrlList.add(RESOURCE_MODEL_DOWNLOAD_BASE_URL + MODEL_FACE_LANDMARKER_PTS5);
            }
            if (isNeedDownload(RESOURCE_MODEL_DOWNLOAD_BASE_URL, MODEL_FACE_RECOGNIZER)) {
                needDownloadResUrlList.add(RESOURCE_MODEL_DOWNLOAD_BASE_URL + MODEL_FACE_RECOGNIZER);
            }
            if (isNeedDownload(RESOURCE_MODEL_DOWNLOAD_BASE_URL, MODEL_FAS_FIRST)) {
                needDownloadResUrlList.add(RESOURCE_MODEL_DOWNLOAD_BASE_URL + MODEL_FAS_FIRST);
            }
            if (isNeedDownload(RESOURCE_MODEL_DOWNLOAD_BASE_URL, MODEL_FAS_SECOND)) {
                needDownloadResUrlList.add(RESOURCE_MODEL_DOWNLOAD_BASE_URL + MODEL_FAS_SECOND);
            }
            /*
             * so
             */
            if (isNeedDownload(RESOURCE_SO_DOWNLOAD_BASE_URL, SO_FILENAME_libSeetaAuthorize)) {
                needDownloadResUrlList.add(RESOURCE_SO_DOWNLOAD_BASE_URL + SO_FILENAME_libSeetaAuthorize);
            }
            if (isNeedDownload(RESOURCE_SO_DOWNLOAD_BASE_URL, SO_FILENAME_libSeetaFaceAntiSpoofingX600)) {
                needDownloadResUrlList.add(RESOURCE_SO_DOWNLOAD_BASE_URL + SO_FILENAME_libSeetaFaceAntiSpoofingX600);
            }
            if (isNeedDownload(RESOURCE_SO_DOWNLOAD_BASE_URL, SO_FILENAME_libSeetaFaceAntiSpoofingX600_java)) {
                needDownloadResUrlList.add(RESOURCE_SO_DOWNLOAD_BASE_URL + SO_FILENAME_libSeetaFaceAntiSpoofingX600_java);
            }
            if (isNeedDownload(RESOURCE_SO_DOWNLOAD_BASE_URL, SO_FILENAME_libSeetaFaceDetector600)) {
                needDownloadResUrlList.add(RESOURCE_SO_DOWNLOAD_BASE_URL + SO_FILENAME_libSeetaFaceDetector600);
            }
            if (isNeedDownload(RESOURCE_SO_DOWNLOAD_BASE_URL, SO_FILENAME_libSeetaFaceDetector600_java)) {
                needDownloadResUrlList.add(RESOURCE_SO_DOWNLOAD_BASE_URL + SO_FILENAME_libSeetaFaceDetector600_java);
            }
            if (isNeedDownload(RESOURCE_SO_DOWNLOAD_BASE_URL, SO_FILENAME_libSeetaFaceLandmarker600)) {
                needDownloadResUrlList.add(RESOURCE_SO_DOWNLOAD_BASE_URL + SO_FILENAME_libSeetaFaceLandmarker600);
            }
            if (isNeedDownload(RESOURCE_SO_DOWNLOAD_BASE_URL, SO_FILENAME_libSeetaFaceLandmarker600_java)) {
                needDownloadResUrlList.add(RESOURCE_SO_DOWNLOAD_BASE_URL + SO_FILENAME_libSeetaFaceLandmarker600_java);
            }
            if (isNeedDownload(RESOURCE_SO_DOWNLOAD_BASE_URL, SO_FILENAME_libSeetaFaceRecognizer600)) {
                needDownloadResUrlList.add(RESOURCE_SO_DOWNLOAD_BASE_URL + SO_FILENAME_libSeetaFaceRecognizer600);
            }
            if (isNeedDownload(RESOURCE_SO_DOWNLOAD_BASE_URL, SO_FILENAME_libSeetaFaceRecognizer600_java)) {
                needDownloadResUrlList.add(RESOURCE_SO_DOWNLOAD_BASE_URL + SO_FILENAME_libSeetaFaceRecognizer600_java);
            }
            if (isNeedDownload(RESOURCE_SO_DOWNLOAD_BASE_URL, SO_FILENAME_libTenniS)) {
                needDownloadResUrlList.add(RESOURCE_SO_DOWNLOAD_BASE_URL + SO_FILENAME_libTenniS);
            }
            if (isNeedDownload(RESOURCE_SO_DOWNLOAD_BASE_URL, SO_FILENAME_libopencv_java3)) {
                needDownloadResUrlList.add(RESOURCE_SO_DOWNLOAD_BASE_URL + SO_FILENAME_libopencv_java3);
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        needDownloadResUrlListListener.onGetNeedDownloadResUrlList(needDownloadResUrlList);
    }

    /**
     * 是否需要下载<br/>
     * 注意不要在主线程调用此方法
     *
     * @param resName 资源文件名
     * @return boolean
     */
    private static boolean isNeedDownload(String baseUrl, String resName) {
        String resFilePath = getResFileExternalStoragePath(resName);
        if (TextUtils.isEmpty(resFilePath)) {
            return true;
        }
        File file = new File(resFilePath);
        if (!file.exists()) {
            return true;
        }
        try {
            URL url = new URL(baseUrl + resName);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            int contentLength = httpURLConnection.getContentLength();
            long length = file.length();
            return contentLength != length;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;

    }

}
