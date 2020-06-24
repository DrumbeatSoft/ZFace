package com.drumbeat.zface.listener;

/**
 * 资源文件查询监听
 *
 * @author ZuoHailong
 * @date 2020/6/11
 */
public interface QueryListener {
    /**
     * @param needDownload 是否需要下载
     */
    void onSuccess(boolean needDownload);
}
