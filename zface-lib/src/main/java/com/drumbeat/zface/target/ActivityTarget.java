package com.drumbeat.zface.target;

import android.app.Activity;
import android.content.Context;

/**
 * Activity Wrapper.
 *
 * @author ZuoHailong
 * @date 2020/4/30
 */
public class ActivityTarget implements Target {

    private Activity mActivity;

    public ActivityTarget(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public Context getContext() {
        return mActivity;
    }
}
