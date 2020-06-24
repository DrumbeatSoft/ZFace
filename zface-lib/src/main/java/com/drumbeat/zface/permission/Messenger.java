package com.drumbeat.zface.permission;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * @author ZuoHailong
 * @date 2020/6/24
 */
public class Messenger extends BroadcastReceiver {

    private Context mContext;
    private Callback mCallback;

    public Messenger(Context mContext, Callback mCallback) {
        this.mContext = mContext;
        this.mCallback = mCallback;
    }

    public void register() {
        IntentFilter filter = new IntentFilter(compileAction(mContext));
        mContext.registerReceiver(this, filter);
    }

    public void unRegister() {
        mContext.unregisterReceiver(this);
    }

    public static void send(Context context) {
        Intent broadcast = new Intent(compileAction(context));
        context.sendBroadcast(broadcast);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mCallback.onCallback();
    }

    private static String compileAction(Context context) {
        return context.getPackageName() + ".zface.permission";
    }

    public interface Callback {
        void onCallback();
    }

}
