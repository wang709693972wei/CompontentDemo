package com.basemodule.ww_test.net.download;

/**
 * Created by XingjieZheng
 * on 2017/5/16.
 */

public interface IDownloadCallback {

    void onProgress(DownloadInfo info, long progress, long total, boolean done);

    void onSuccess(DownloadInfo info, String result);

    void onFailed(DownloadInfo info, String errorMsg);

}
