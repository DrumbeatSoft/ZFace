package com.drumbeat.zface.constant;

/**
 * 错误码
 *
 * @author ZuoHailong
 * @date 2020/6/10
 */
public enum ErrorCode {
    /**
     * 相机不可用
     */
    ERROR_CAMERA_UNAVAILABLE,
    /**
     * 假人脸
     */
    ERROR_SPOOF,
    /**
     * 图像模糊
     */
    ERROR_FUZZY,
    /**
     * 无人脸
     */
    ERROR_NO_FACE,
    /**
     * 无待下载资源
     */
    ERROR_NO_NEED_DOWNLOAD_RESOURCE,
    /**
     * 下载出错
     */
    ERROR_DOWNLOAD,
    /**
     * 检测器未初始化
     */
    ERROR_NO_INIT,
    /**
     * 初始化失败
     */
    ERROR_INIT_FAIL,
    /**
     * 资源文件缺失
     */
    ERROR_NO_RESOURCE,
    /**
     * 资源文件加载失败
     */
    ERROR_LOAD_RESOURCE_FAIL
}
