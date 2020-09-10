package com.android.jwjy.wtjyproduct.update.model;

import android.app.DownloadManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Message;

import java.io.File;


public class DownloadObserver extends ContentObserver {
    private DownloadManager dm;
    private long downloadId;
    private DownloadApkImpl.DownloadHandler handler;
    private boolean isDone = false;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public DownloadObserver(DownloadManager dm, long downloadId, DownloadApkImpl.DownloadHandler handler) {
        super(handler);
        this.dm = dm;
        this.downloadId = downloadId;
        this.handler = handler;
        isDone = false;
    }


    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = dm.query(query);
        if (c != null && c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
            Message msg = new Message();
            switch (status) {
                case DownloadManager.STATUS_RUNNING:
                    msg.what = 1;
                    double totalSizeBytes = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    double downloadSizeBytes = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    msg.arg1 = (int) ((downloadSizeBytes / totalSizeBytes) * 100);
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    if (!isDone) {
                        String successFilePath = Uri.parse(c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI))).getPath();
                        msg.what = 2;
                        msg.obj = successFilePath;
                        isDone = true;
                    }
                    break;
                case DownloadManager.STATUS_FAILED:
                    String filePath = c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                    msg.what = 3;
                    msg.obj = "onFailed";
                    break;
                default:
                    break;

            }
            handler.sendMessage(msg);
            c.close();
            c = null;
        }
    }

/*
    public int[] getBytesAndStatus(long downloadId) {
        int[] bytesAndStatus = new int[] { -1, -1, 0 };
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = null;
        try {
            c = downloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                bytesAndStatus[0] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                bytesAndStatus[1] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                bytesAndStatus[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return bytesAndStatus;
    }*/
}
