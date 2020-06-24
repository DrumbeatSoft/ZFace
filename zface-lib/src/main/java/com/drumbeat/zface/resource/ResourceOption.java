package com.drumbeat.zface.resource;

import com.drumbeat.zface.listener.DownloadListener;
import com.drumbeat.zface.listener.QueryListener;

/**
 * @author ZuoHailong
 * @date 2020/6/10
 */
public interface ResourceOption {
    /**
     * 查询资源文件是否需要下载更新
     *
     * @param queryListener 查询监听
     */
    void query(QueryListener queryListener);

    /**
     * 下载资源文件<br/>
     * sdk 提供下载服务及UI，下载监听只回调下载更新结果
     *
     * @param downloadListener 下载监听
     */
    void download(DownloadListener downloadListener);
}
