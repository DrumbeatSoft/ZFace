package com.drumbeat.zface.listener;

import com.drumbeat.zface.constant.ErrorCode;

/**
 * 人脸特征比对监听
 *
 * @author ZuoHailong
 * @date 2020/6/2
 */
public interface CompareListener {
    /**
     * 人脸特征对比
     *
     * @param faceSimilar 比对相似度
     */
    void onSuccess(float faceSimilar);

    void onFailure(ErrorCode errorCode, String errorMsg);
}
