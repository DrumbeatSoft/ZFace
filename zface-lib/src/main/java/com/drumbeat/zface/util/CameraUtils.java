package com.drumbeat.zface.util;

import android.hardware.Camera;

import com.drumbeat.zface.constant.CameraFacing;

import java.util.List;

/**
 * @author ZuoHailong
 * @date 2020/6/8
 */
public class CameraUtils {

    /**
     * 查询照相机支持的与所给长度最接近的尺寸
     *
     * @param facing     摄像头朝向
     * @param sideLength 边长
     * @return 尺寸 {@link Camera.Size}
     */
    public static Camera.Size getClosestSize(CameraFacing facing, int sideLength) {
        Camera.Size closestSize = null;
        List<Camera.Size> supportedPictureSizes = getSupportedPictureSizes(facing);
        List<Camera.Size> supportedPreviewSizes = getSupportedPreviewSizes(facing);
        if (supportedPictureSizes != null && supportedPictureSizes.size() > 0 && supportedPreviewSizes != null && supportedPreviewSizes.size() > 0) {
            for (Camera.Size pictureSize : supportedPictureSizes) {
                /*
                 * 保证相机支持的尺寸大于所需边长
                 * Camera.width > Camera.height
                 * */
                if (pictureSize.height > sideLength) {
                    for (Camera.Size previewSize : supportedPreviewSizes) {
                        // 选取交集尺寸
                        if (pictureSize.width == previewSize.width && pictureSize.height == previewSize.height) {
                            if (closestSize == null) {
                                closestSize = pictureSize;
                            } else {
                                //取绝对值最接近的尺寸
                                if (Math.abs(pictureSize.height - sideLength) < Math.abs(closestSize.height - sideLength)) {
                                    closestSize = pictureSize;
                                }
                            }
                        }
                    }
                }
            }
        }
        return closestSize;
    }

    /**
     * 查询照相机支持的图片尺寸的集合
     *
     * @param facing 摄像头朝向 {@link CameraFacing}
     * @return {@link Camera.Size} 的List集合
     */
    private static List<Camera.Size> getSupportedPictureSizes(CameraFacing facing) {
        Camera camera = null;
        switch (facing) {
            case CAMERA_FACING_FRONT:
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                break;
            case CAMERA_FACING_BACK:
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                break;
        }
        if (camera == null || camera.getParameters() == null) {
            return null;
        }
        return camera.getParameters().getSupportedPictureSizes();
    }

    /**
     * 查询照相机支持的预览尺寸的集合
     *
     * @param facing 摄像头朝向 {@link CameraFacing}
     * @return {@link Camera.Size} 的List集合
     */
    private static List<Camera.Size> getSupportedPreviewSizes(CameraFacing facing) {
        Camera camera = null;
        switch (facing) {
            case CAMERA_FACING_FRONT:
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                break;
            case CAMERA_FACING_BACK:
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                break;
        }
        if (camera == null || camera.getParameters() == null) {
            return null;
        }
        return camera.getParameters().getSupportedPreviewSizes();
    }
}
