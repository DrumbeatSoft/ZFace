package com.seeta.sdk;

/**
 * 设备类型枚举类
 */
public enum SeetaDevice {
    /**
     * 自动
     */
    SEETA_DEVICE_AUTO(0),
    /**
     * CPU
     */
    SEETA_DEVICE_CPU(1),
    /**
     * GPU
     */
    SEETA_DEVICE_GPU(2);

    private int value;

    private SeetaDevice(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

