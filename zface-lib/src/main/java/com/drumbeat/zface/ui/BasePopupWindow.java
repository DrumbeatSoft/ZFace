package com.drumbeat.zface.ui;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.IBinder;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * 设置半透明蒙层，并屏蔽点击返回键 popup.dismiss 的行为
 *
 * @author ZuoHailong
 * @date 2020/6/10
 */
public abstract class BasePopupWindow extends PopupWindow {
    protected Context context;
    private WindowManager windowManager;
    private View maskView;

    public BasePopupWindow(Context context, int width, int height) {
        super(width, height);
        this.context = context;
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable());
    }

    @Override
    public void showAsDropDown(View anchor) {
        this.showAsDropDown(anchor, 0, 0);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        this.showAsDropDown(anchor, xoff, yoff, Gravity.TOP | Gravity.START);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        addMask(anchor.getWindowToken());
        super.showAsDropDown(anchor, xoff, yoff, gravity);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        addMask(parent.getWindowToken());
        super.showAtLocation(parent, gravity, x, y);
    }

    @Override
    public void dismiss() {
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        if (stackTrace.length >= 2 && "dispatchKeyEvent".equals(stackTrace[1].getMethodName())) {
            //按了返回键，可以在此处设定返回键按键回调，通知给调用方
        } else {
            //点击外部或者点击关闭调用了dismiss,直接让弹窗消失
            removeMask();
            super.dismiss();
        }
    }

    private void addMask(IBinder token) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams wl = new WindowManager.LayoutParams();
        wl.width = WindowManager.LayoutParams.MATCH_PARENT;
        wl.height = WindowManager.LayoutParams.MATCH_PARENT;
        wl.format = PixelFormat.TRANSLUCENT;//不设置这个弹出框的透明遮罩显示为黑色
        wl.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;//该Type描述的是形成的窗口的层级关系
        wl.token = token;//获取当前Activity中的View中的token,来依附Activity
        maskView = new View(context);
        maskView.setBackgroundColor(0x4d000000);
        maskView.setFitsSystemWindows(false);
        /**
         * 通过WindowManager的addView方法创建View，产生出来的View根据WindowManager.LayoutParams属性不同，效果也就不同了。
         * 比如创建系统顶级窗口，实现悬浮窗口效果！
         */
        windowManager.addView(maskView, wl);
    }

    private void removeMask() {
        if (null != maskView) {
            windowManager.removeViewImmediate(maskView);
            maskView = null;
        }
    }
}
