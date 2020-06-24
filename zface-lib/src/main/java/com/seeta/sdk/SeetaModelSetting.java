package com.seeta.sdk;

/**
 * 机器学习模型配置
 */
public class SeetaModelSetting {

    /**
     * 设备类型，CPU/GPU/AUTO等
     */
    public SeetaDevice device;
    /**
     * when device is GPU, id means GPU id
     */
    public int id;
    /**
     * 机器学习模型文件<br/>
     * 将被用于人脸识别、人脸检测、人脸关键点定位等。
     */
    public String[] model;

    public SeetaModelSetting(int id, String[] models, SeetaDevice dev) {
        this.id = id;
        this.device = dev;
        this.model = new String[models.length];
        for (int i = 0; i < models.length; i++) {
            this.model[i] = new String(models[i]);
        }
    }


}
