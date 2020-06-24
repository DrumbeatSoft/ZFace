package com.drumbeat.zface.resource;

import com.drumbeat.zface.ZFace;
import com.drumbeat.zface.constant.ErrorCode;
import com.drumbeat.zface.listener.Action;
import com.drumbeat.zface.listener.DownloadListener;
import com.drumbeat.zface.listener.QueryListener;
import com.drumbeat.zface.permission.Permission;
import com.drumbeat.zface.target.Target;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZuoHailong
 * @date 2020/6/11
 */
public class Resource implements ResourceOption {

    private Target target;
    private List<String> resUrlList = new ArrayList<>();

    public static Resource getInstance() {
        return InstanceHelper.instance;
    }

    @Override
    public void query(QueryListener queryListener) {
        ZFace.with(target.getContext())
                .permission()
                .permission(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(permissions -> ResourceUtil.getNeedDownloadResUrlList(needDownloadResUrlList -> {
                    resUrlList.clear();
                    resUrlList.addAll(needDownloadResUrlList);
                    if (resUrlList.size() > 0) {
                        queryListener.onSuccess(true);
                    } else {
                        queryListener.onSuccess(false);
                    }
                }))
                .start();
    }

    @Override
    public void download(DownloadListener downloadListener) {
        if (resUrlList.size() == 0) {
            ResourceUtil.getNeedDownloadResUrlList(needDownloadResUrlList -> {
                resUrlList.clear();
                resUrlList.addAll(needDownloadResUrlList);
            });
        }
        if (resUrlList.size() == 0) {
            downloadListener.onFailure(ErrorCode.ERROR_NO_NEED_DOWNLOAD_RESOURCE, null);
        }
        // 弹出更新进度框，并开始下载
        ResourcePopupWindow resourcePopupWindow = new ResourcePopupWindow(target.getContext());
        resourcePopupWindow.showAndDownload(resUrlList, downloadListener);

    }

    private static class InstanceHelper {
        private static Resource instance = new Resource();
    }

    private Resource() {
    }

    public Resource setTarget(Target target) {
        this.target = target;
        return this;
    }

}
