package com.drumbeat.zface.listener;

/**
 * @author ZuoHailong
 * @date 2020/6/24
 */
public interface Action<T> {

    /**
     * An action.
     *
     * @param permissions the permissions.
     */
    void onAction(T permissions);
}
