package com.seeta.sdk;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZuoHailong
 * @date 2020/6/2
 */
public class TrackingInfo {
    public Mat matBgr;
    public Mat matGray;
    public SeetaRect faceInfo = new SeetaRect();
    /**
     * 要检测的人脸所处的区域（可对人脸进行画框）
     */
    public Rect faceRect = new Rect();
}
