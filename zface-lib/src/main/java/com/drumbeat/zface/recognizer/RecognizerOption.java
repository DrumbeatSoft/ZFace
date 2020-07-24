package com.drumbeat.zface.recognizer;

import androidx.annotation.NonNull;

import com.drumbeat.zface.listener.CompareListener;
import com.drumbeat.zface.listener.InitListener;
import com.drumbeat.zface.listener.RecognizeListener;

/**
 * Face recognizer.
 *
 * @author ZuoHailong
 * @date 2020/6/10
 */
public interface RecognizerOption {
    /**
     * 初始化人脸识别器
     */
    void init(InitListener initListener);

    /**
     * 人脸检测</br>
     * 启动相机预览页，检测出最大人脸，并通过回调返回人脸特征数据
     *
     * @param recognizeListener 检测监听
     */
    void recognize(@NonNull RecognizeListener recognizeListener);

    /**
     * 人脸检测</br>
     * 用于相机预览页传入预览图像数据进行检测
     *
     * @param data 图像数据
     */
    void recognize(@NonNull byte[] data);

    /**
     * 人脸特征比对，并通过回调返回比对相似度
     *
     * @param feature1        人脸特征数据
     * @param feature2        人脸特征数据
     * @param compareListener 人脸比对监听
     */
    void compare(@NonNull float[] feature1, @NonNull float[] feature2, @NonNull CompareListener compareListener);

    /**
     * 关闭人脸识别页面，关闭人脸识别器
     */
//    void close();
}
