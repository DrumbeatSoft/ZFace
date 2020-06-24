package com.drumbeat.zface;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.drumbeat.zface.config.ZFaceConfig;
import com.drumbeat.zface.option.Option;
import com.drumbeat.zface.target.ActivityTarget;
import com.drumbeat.zface.target.ContextTarget;
import com.drumbeat.zface.target.FragmentTarget;

/**
 * @author ZuoHailong
 * @date 2020/4/30
 */
public class ZFace {

    private static ZFaceConfig fConfig;

    public static void setConfig(ZFaceConfig config) {
        if (fConfig == null) {
            synchronized (ZFace.class) {
                if (fConfig == null) {
                    fConfig = config == null ? ZFaceConfig.newBuilder().build() : config;
                }
            }
        }
    }

    public static ZFaceConfig getConfig() {
        // 保证sConfig不是null
        setConfig(null);
        return fConfig;
    }

    /**
     * In the Activity.
     *
     * @param activity {@link Activity}.
     * @return {@link Option}.
     */
    public static
    @NonNull
    Option with(@NonNull Activity activity) {
        return new Boot(new ActivityTarget(activity));
    }

    /**
     * In the Fragment.
     *
     * @param fragment {@link Fragment}.
     * @return {@link Option}.
     */
    public static
    @NonNull
    Option with(@NonNull Fragment fragment) {
        return new Boot(new FragmentTarget(fragment));
    }

    /**
     * Anywhere..
     *
     * @param context {@link Context}.
     * @return {@link Option}.
     */
    public static
    @NonNull
    Option with(@NonNull Context context) {
        return new Boot(new ContextTarget(context));
    }

    private ZFace() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

}
