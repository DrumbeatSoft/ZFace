package com.drumbeat.zface.listener;

import com.drumbeat.zface.constant.ErrorCode;

/**
 * 人脸识别监听
 *
 * @author ZuoHailong
 * @date 2020/5/29
 */
public interface RecognizeListener {
    /**
     * 人脸检测结果回调，检测中每秒回调一次
     *
     * @param featureData 人脸特征数据
     */
    void onSuccess(float[] featureData,byte[] faceData);

    void onFailure(ErrorCode errorCode, String errorMsg);
}
