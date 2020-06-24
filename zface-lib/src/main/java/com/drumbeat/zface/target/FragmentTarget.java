package com.drumbeat.zface.target;

import android.content.Context;

import androidx.fragment.app.Fragment;

/**
 * androidx.fragment.app.Fragment Wrapper.
 *
 * @author ZuoHailong
 * @date 2020/4/30
 */
public class FragmentTarget implements Target {

    private Fragment mFragment;

    public FragmentTarget(Fragment mFragment) {
        this.mFragment = mFragment;
    }

    @Override
    public Context getContext() {
        return mFragment.getActivity();
    }
}
