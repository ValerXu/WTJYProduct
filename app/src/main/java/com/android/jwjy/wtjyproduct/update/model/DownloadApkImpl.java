package com.android.jwjy.wtjyproduct.update.model;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.android.jwjy.wtjyproduct.util.AppUtil;

import java.lang.ref.WeakReference;


public class DownloadApkImpl {
    private long downloadId = -1;
    private DownloadManager dm;
    private DownloadObserver downloadObserver;
    private final DownloadHandler downloadHandler = new DownloadHandler();
    private WeakReference<Context> contextRef;
    public DownloadApkImpl(Context context) {
        contextRef = new WeakReference<>(context);
    }

    /**
     * 开始缓存 app
     */
    public void startCache(Uri uri, DownloadListener listener) {

        String appName = uri.getLastPathSegment();
        Context context = contextRef.get();
        if(context == null)
            return;
        if(TextUtils.isEmpty(appName)){

            appName = AppUtil.getAppName(context);
        }

        DownloadManager.Request req = new DownloadManager.Request(uri);
        //Wi-Fi环境下下载
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        //不显示
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

        //创建目录
        //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir() ;

        //req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, appName);
        req.setDestinationInExternalFilesDir(context,Environment.DIRECTORY_DOWNLOADS, appName);
        req.setMimeType("application/vnd.android.package-archive");

        downloadHandler.setDownloadListener(listener);
        dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadId = dm.enqueue(req);
        downloadObserver = new DownloadObserver(dm, downloadId, downloadHandler);
        registerContentObserver(context);
    }

    /**
     * 注册ContentObserver
     */
    public void registerContentObserver(Context context) {
        if (downloadObserver != null) {
            context.getContentResolver().registerContentObserver(  Uri.parse("content://downloads/my_downloads"),
                    true,
                    downloadObserver);
        }
    }

    /**
     * 注销ContentObserver
     */
    public void unregisterContentObserver(Context context) {
        if (downloadObserver != null && context != null) {
            context.getContentResolver().unregisterContentObserver(downloadObserver);
        }
    }

    public void destroy(){
        downloadHandler.setDownloadListener(null);
        unregisterContentObserver(contextRef.get());
    }

    /**
     * 停止缓存 app
     */
    public void stopCache() {
        Context context = contextRef.get();
        if(context == null)
            return;
        unregisterContentObserver(context);
        if (dm == null)
            dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadId > 0)
            dm.remove(downloadId);
    }



    public interface DownloadListener {
        void onDownloading(int percent);

        void onSuccess(String url);

        void onFailed(String error);
    }

    static class DownloadHandler extends Handler {
        private WeakReference<DownloadListener> listenerRef;
        public static final int MSG_DOWNLOADING = 1;
        public static final int MSG_DOWNLOAD_SUCCESS = 2;
        public static final int MSG_DOWNLOAD_FAILED = 3;

        public void setDownloadListener(DownloadListener listener){
            listenerRef = new WeakReference<>(listener);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DownloadListener listener = listenerRef != null ? listenerRef.get() : null;
            switch (msg.what) {
                case 1:
                    if (listener != null)
                        listener.onDownloading(msg.arg1);
                    break;
                case 2:
                    if (listener != null) {
                        listener.onSuccess((String) msg.obj);
                    }
                    break;
                case 3:
                    if (listener != null)
                        listener.onFailed((String) msg.obj);
                    break;
                default:
                    break;
            }
        }
    }
}
