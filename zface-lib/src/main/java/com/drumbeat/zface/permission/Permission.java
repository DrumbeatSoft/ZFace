package com.drumbeat.zface.permission;

import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;

import com.drumbeat.zface.listener.Action;
import com.drumbeat.zface.target.Target;
import com.drumbeat.zface.ui.PermissionActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ZuoHailong
 * @date 2020/6/23
 */
public class Permission implements PermissionOption, Messenger.Callback {

    public static final String READ_CALENDAR = "android.permission.READ_CALENDAR";
    public static final String WRITE_CALENDAR = "android.permission.WRITE_CALENDAR";

    public static final String CAMERA = "android.permission.CAMERA";

    public static final String READ_CONTACTS = "android.permission.READ_CONTACTS";
    public static final String WRITE_CONTACTS = "android.permission.WRITE_CONTACTS";
    public static final String GET_ACCOUNTS = "android.permission.GET_ACCOUNTS";

    public static final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";
    public static final String ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION";
    public static final String ACCESS_BACKGROUND_LOCATION = "android.permission.ACCESS_BACKGROUND_LOCATION";

    public static final String RECORD_AUDIO = "android.permission.RECORD_AUDIO";

    public static final String READ_PHONE_STATE = "android.permission.READ_PHONE_STATE";
    public static final String CALL_PHONE = "android.permission.CALL_PHONE";
    public static final String USE_SIP = "android.permission.USE_SIP";
    public static final String READ_PHONE_NUMBERS = "android.permission.READ_PHONE_NUMBERS";
    public static final String ANSWER_PHONE_CALLS = "android.permission.ANSWER_PHONE_CALLS";
    public static final String ADD_VOICEMAIL = "com.android.voicemail.permission.ADD_VOICEMAIL";

    public static final String READ_CALL_LOG = "android.permission.READ_CALL_LOG";
    public static final String WRITE_CALL_LOG = "android.permission.WRITE_CALL_LOG";
    public static final String PROCESS_OUTGOING_CALLS = "android.permission.PROCESS_OUTGOING_CALLS";

    public static final String BODY_SENSORS = "android.permission.BODY_SENSORS";
    public static final String ACTIVITY_RECOGNITION = "android.permission.ACTIVITY_RECOGNITION";

    public static final String SEND_SMS = "android.permission.SEND_SMS";
    public static final String RECEIVE_SMS = "android.permission.RECEIVE_SMS";
    public static final String READ_SMS = "android.permission.READ_SMS";
    public static final String RECEIVE_WAP_PUSH = "android.permission.RECEIVE_WAP_PUSH";
    public static final String RECEIVE_MMS = "android.permission.RECEIVE_MMS";

    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";

    public static final class Group {

        public static final String[] CALENDAR = new String[]{Permission.READ_CALENDAR, Permission.WRITE_CALENDAR};

        public static final String[] CAMERA = new String[]{Permission.CAMERA};

        public static final String[] CONTACTS = new String[]{Permission.READ_CONTACTS, Permission.WRITE_CONTACTS, Permission.GET_ACCOUNTS};

        public static final String[] LOCATION = new String[]{Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION,
                Permission.ACCESS_BACKGROUND_LOCATION};

        public static final String[] MICROPHONE = new String[]{Permission.RECORD_AUDIO};

        public static final String[] PHONE = new String[]{Permission.READ_PHONE_STATE, Permission.CALL_PHONE, Permission.USE_SIP,
                Permission.READ_PHONE_NUMBERS, Permission.ANSWER_PHONE_CALLS, Permission.ADD_VOICEMAIL};

        public static final String[] CALL_LOG = new String[]{Permission.READ_CALL_LOG, Permission.WRITE_CALL_LOG,
                Permission.PROCESS_OUTGOING_CALLS};

        public static final String[] SENSORS = new String[]{Permission.BODY_SENSORS};

        public static final String[] ACTIVITY_RECOGNITION = new String[]{Permission.ACTIVITY_RECOGNITION};

        public static final String[] SMS = new String[]{Permission.SEND_SMS, Permission.RECEIVE_SMS, Permission.READ_SMS,
                Permission.RECEIVE_WAP_PUSH, Permission.RECEIVE_MMS};

        public static final String[] STORAGE = new String[]{Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE};
    }

    private Target target;
    private List<String> permissionList = new ArrayList<>();
    private Action<List<String>> mGranted;
    private Action<List<String>> mDenied;
    private Messenger mMessenger;

    public static Permission getInstance() {
        return Permission.InstanceHelper.instance;
    }

    @Override
    public PermissionOption permission(@NonNull String... permissions) {
        permissionList.clear();
        if (permissions.length == 0) {
            return this;
        }
        permissionList.addAll(Arrays.asList(permissions));
        return this;
    }

    @Override
    public PermissionOption permission(@NonNull String[]... groups) {
        permissionList.clear();
        if (groups.length == 0) {
            return this;
        }
        for (String[] group : groups) {
            if (group == null || group.length == 0) {
                continue;
            }
            permissionList.addAll(Arrays.asList(group));
        }
        return this;
    }

    @Override
    public PermissionOption onGranted(@NonNull Action<List<String>> granted) {
        this.mGranted = granted;
        return this;
    }

    @Override
    public PermissionOption onDenied(@NonNull Action<List<String>> denied) {
        this.mDenied = denied;
        return this;
    }

    @Override
    public void start() {
        mMessenger = new Messenger(target.getContext(), this);
        mMessenger.register();
        PermissionActivity.requestPermission(target.getContext(), permissionList.toArray(new String[0]));
    }

    @Override
    public void onCallback() {
        // 检查权限获取情况
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (mGranted != null) mGranted.onAction(null);
        } else {
            PackageManager packageManager = target.getContext().getPackageManager();
            List<String> deniedPermissionList = new ArrayList<>();
            for (String permission : permissionList) {
                if (PackageManager.PERMISSION_DENIED == packageManager.checkPermission(permission, target.getContext().getPackageName())) {
                    deniedPermissionList.add(permission);
                }
            }
            if (deniedPermissionList.size() > 0) {
                if (mDenied != null) mDenied.onAction(deniedPermissionList);
            } else {
                if (mGranted != null) mGranted.onAction(null);
            }
        }
        mMessenger.unRegister();
    }

    private static class InstanceHelper {
        private static Permission instance = new Permission();
    }

    private Permission() {
    }

    public Permission setTarget(Target target) {
        this.target = target;
        return this;
    }
}
