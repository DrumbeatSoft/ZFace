package com.drumbeat.zface.listener;

import com.drumbeat.zface.constant.ErrorCode;

/**
 * 资源文件下载监听
 *
 * @author ZuoHailong
 * @date 2020/6/11
 */
public interface DownloadListener {
    /**
     * 资源文件下载完成
     */
    void onSuccess();

    void onFailure(ErrorCode errorCode, String errorMsg);
}
