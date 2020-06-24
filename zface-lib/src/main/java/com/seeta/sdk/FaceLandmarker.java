package com.seeta.sdk;

/**
 * 人脸关键点定位<br/>
 * 注意：关键点有时也被称为特征点，但这个与人脸识别中的特征概念没有任何相关性。不存在关键点越多，人脸识别经度越高的结论。
 */
public class FaceLandmarker {

    public long impl = 0;

    private native void construct(SeetaModelSetting seeting);

    public FaceLandmarker(SeetaModelSetting setting) {
        this.construct(setting);
    }

    public native void dispose();

    protected void finalize() throws Throwable {
        super.finalize();
        this.dispose();
    }

    public native int number();

    public native void mark(SeetaImageData imageData, SeetaRect seetaRect, SeetaPointF[] pointFS);

    public native void mark(SeetaImageData imageData, SeetaRect seetaRect, SeetaPointF[] pointFS, int[] masks);
}
