package com.drumbeat.zface.config;

/**
 * @author ZuoHailong
 * @date 2020/6/9
 */
public class CameraConfig {

    /**
     * 页面呈现的预览正方形的边长，单位dp
     */
    public static final int PREVIEW_SIDE_LENGTH = 180;

    private int cameraPreviewWidth;
    private int cameraPreviewHeight;

    private CameraConfig() {
    }

    public static CameraConfig getInstance() {
        return InstanceHelper.instance;
    }

    private static class InstanceHelper {
        private static final CameraConfig instance = new CameraConfig();
    }

    public int getCameraPreviewWidth() {
        return cameraPreviewWidth;
    }

    public CameraConfig setCameraPreviewWidth(int cameraPreviewWidth) {
        this.cameraPreviewWidth = cameraPreviewWidth;
        return this;
    }

    public int getCameraPreviewHeight() {
        return cameraPreviewHeight;
    }

    public CameraConfig setCameraPreviewHeight(int cameraPreviewHeight) {
        this.cameraPreviewHeight = cameraPreviewHeight;
        return this;
    }
}
