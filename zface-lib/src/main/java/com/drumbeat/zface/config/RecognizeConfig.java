package com.drumbeat.zface.config;

import com.drumbeat.zface.listener.RecognizeListener;

/**
 * @author ZuoHailong
 * @date 2020/6/24
 */
public class RecognizeConfig {

    private RecognizeListener recognizeListener;

    public RecognizeListener getRecognizeListener() {
        return recognizeListener;
    }

    public RecognizeConfig setRecognizeListener(RecognizeListener recognizeListener) {
        this.recognizeListener = recognizeListener;
        return this;
    }

    public static RecognizeConfig getInstance() {
        return RecognizeConfig.InstanceHelper.instance;
    }

    private RecognizeConfig() {
    }

    private static class InstanceHelper {
        private final static RecognizeConfig instance = new RecognizeConfig();
    }
}
