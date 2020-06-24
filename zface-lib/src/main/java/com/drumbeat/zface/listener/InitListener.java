package com.drumbeat.zface.listener;

import com.drumbeat.zface.constant.ErrorCode;

/**
 * @author ZuoHailong
 * @date 2020/6/15
 */
public interface InitListener {
    void onSuccess();

    /**
     * 初始化失败
     *
     * @param errorCode
     * @param errorMsg
     */
    void onFailure(ErrorCode errorCode, String errorMsg);
}
