package com.drumbeat.zface.target;

import android.content.Context;

/**
 * Context Wrapper.
 *
 * @author ZuoHailong
 * @date 2020/4/30
 */
public class ContextTarget implements Target {

    private Context mContext;

    public ContextTarget(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public Context getContext() {
        return mContext;
    }
}
