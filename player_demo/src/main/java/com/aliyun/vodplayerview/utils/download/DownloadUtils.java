package com.aliyun.vodplayerview.utils.download;

import android.content.Context;

public class DownloadUtils {

    public static boolean isStorageAlarm(Context context, AliyunDownloadMediaInfo downloadMediaInfo, AliyunDownloadManagerInterface callback) {
        long availablekb = 0L;
        if (StorageUtil.isExternalMemoryPath(getSaveDir(context,callback))) {
            availablekb = StorageUtil.getAvailableExternalMemorySize();
        } else {
            availablekb = StorageUtil.getAvailableInternalMemorySize();
        }

        if (availablekb <= 0L) {
            return false;
        } else {
            long itemLeftKb = (long)(100 - downloadMediaInfo.getProgress()) * downloadMediaInfo.getSize() / 102400L;
            return availablekb - itemLeftKb > StorageUtil.MINIST_STORAGE_SIZE;
        }
    }

    public static boolean isStorageAlarm(Context context, AliyunDownloadManagerInterface callback) {
        long availablekb = 0L;
        if (StorageUtil.isExternalMemoryPath(getSaveDir(context,callback))) {
            availablekb = StorageUtil.getAvailableExternalMemorySize();
        } else {
            availablekb = StorageUtil.getAvailableInternalMemorySize();
        }

        return availablekb > 0L && availablekb < StorageUtil.MIN_STORAGE_SIZE;
    }

    public static String getSaveDir(Context context, AliyunDownloadManagerInterface callback) {
        return AliyunDownloadManager.getInstance(context,callback).getDownloadDir();
    }
}
