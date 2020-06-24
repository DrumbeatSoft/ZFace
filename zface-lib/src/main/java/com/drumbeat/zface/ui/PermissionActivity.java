package com.drumbeat.zface.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.drumbeat.zface.permission.Messenger;

/**
 * @author ZuoHailong
 * @date 2020/6/23
 */
public class PermissionActivity extends Activity {

    private static final int REQUEST_PERMISSION = 200;
    private static final String KEY_PERMISSIONS = "KEY_PERMISSIONS";

    private String[] permissions;

    /**
     * Request for permissions.
     */
    public static void requestPermission(Context context, String[] permissions) {
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.putExtra(KEY_PERMISSIONS, permissions);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) return;
        Intent intent = getIntent();
        permissions = intent.getStringArrayExtra(KEY_PERMISSIONS);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Messenger.send(this);
        finish();
    }
}
