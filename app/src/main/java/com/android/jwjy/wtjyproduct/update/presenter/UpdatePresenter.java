package com.android.jwjy.wtjyproduct.update.presenter;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;

import com.android.jwjy.wtjyproduct.update.model.CheckVersionIpml;
import com.android.jwjy.wtjyproduct.update.model.DownloadApkImpl;
import com.android.jwjy.wtjyproduct.update.model.entity.NewVersionEntity;


/**
 * Created by Administrator on 2017/6/21.
 */
public class UpdatePresenter implements IUpdatePresenter {
    private static final int NEW_VERSION = 1;
    private static final int NO_VERSION = 2;
    private static final int DOWNLOAD_PROGRESS = 3;
    private static final int DOWNLOAD_SUCCESS = 4;
    private static final int DOWNLOAD_FAIL = 5;

    private CheckVersionIpml checkVersionIpml;
    private DownloadApkImpl downloadApkIpml;
    private IUpdateCallback updateCallback;
    private Handler handler = new Handler();
    public UpdatePresenter(Context context, IUpdateCallback callback) {
        updateCallback = callback;
        checkVersionIpml = new CheckVersionIpml(context);  //检查版本
        downloadApkIpml = new DownloadApkImpl(context);   //下载apk
    }

  /*  Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null && updateCallback != null) {
                switch (msg.what) {
                    case NEW_VERSION:
                        NewVersionEntity newVersionEntity = (NewVersionEntity) msg.obj;
                        updateCallback.newVersion(newVersionEntity);  //有新的版本
                        break;
                    case NO_VERSION:
                        updateCallback.noNewVersion();
                        break;
                    case DOWNLOAD_SUCCESS:
                        String downloadUrl = (String) msg.obj;
                        updateCallback.downloadSuccess(downloadUrl);
                        break;
                    case DOWNLOAD_FAIL:
                        String errorMsg = (String) msg.obj;
                        updateCallback.downloadFail(errorMsg);
                        break;
                    case DOWNLOAD_PROGRESS:
                        int progress = (int) msg.obj;
                        updateCallback.downloadProgress(progress);
                        break;
                    default:
                        break;
                }
            }
        }
    };*/


    @Override
    public void checkVersion() {
        checkVersionIpml.checkVersion(new CheckVersionIpml.CheckVersionListener() {
            @Override
            public void newVersion(final NewVersionEntity newVersionEntity) {   //url -- app 下载的路径
               /* Message message = handler.obtainMessage(NEW_VERSION);
                message.obj = newVersionEntity;
                handler.sendMessage(message);*/
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(updateCallback != null){
                            updateCallback.newVersion(newVersionEntity);
                        }
                    }
                });
            }

            @Override
            public void noNewVersion() {
                //handler.obtainMessage(NO_VERSION).sendToTarget();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(updateCallback != null){
                            updateCallback.noNewVersion();
                        }
                    }
                });
            }
        });
    }


    @Override
    public void downloadApk(String url) {
        downloadApkIpml.startCache(Uri.parse(url), new DownloadApkImpl.DownloadListener() {
            @Override
            public void onDownloading(final int percent) {
            /*    Message message = handler.obtainMessage(DOWNLOAD_PROGRESS);
                message.obj = percent;
                handler.sendMessage(message);*/
             handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(updateCallback != null){
                            updateCallback.downloadProgress(percent);
                        }
                    }
                });
            }

            @Override
            public void onSuccess(final String url) {
             /*   checkVersionIpml.setLocalCacheAppInfo();
                Message message = handler.obtainMessage(DOWNLOAD_SUCCESS);
                message.obj = url;
                handler.sendMessage(message);*/
              handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(updateCallback != null){
                            updateCallback.downloadSuccess(url);
                        }
                    }
                });
            }

            @Override
            public void onFailed(final String error) {
           /*     Message message = handler.obtainMessage(DOWNLOAD_FAIL);
                message.obj = error;
                handler.obtainMessage(DOWNLOAD_FAIL).sendToTarget();*/
             handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(updateCallback != null){
                            updateCallback.downloadFail(error);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void stopDownloadApk() {
        downloadApkIpml.stopCache();
    }


    public void destroy() {
        downloadApkIpml.destroy();
        checkVersionIpml = null;
        downloadApkIpml = null;
        updateCallback = null;
    }
}
