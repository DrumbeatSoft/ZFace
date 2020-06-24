package com.drumbeat.zface.permission;


import androidx.annotation.NonNull;

import com.drumbeat.zface.listener.Action;

import java.util.List;

/**
 * @author ZuoHailong
 * @date 2020/6/23
 */
public interface PermissionOption {

    /**
     * One or more permissions.
     */
    PermissionOption permission(@NonNull @PermissionDef String... permissions);

    /**
     * One or more permissions group.
     *
     * @param groups use constants in {@link Permission.Group}.
     */
    PermissionOption permission(@NonNull String[]... groups);

    /**
     * Action to be taken when all permissions are granted.
     */
    PermissionOption onGranted(@NonNull Action<List<String>> granted);

    /**
     * Action to be taken when all permissions are denied.
     */
    PermissionOption onDenied(@NonNull Action<List<String>> denied);

    /**
     * Request permission.
     */
    void start();
}
