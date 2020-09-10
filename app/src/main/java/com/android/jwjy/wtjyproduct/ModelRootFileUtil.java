package com.android.jwjy.wtjyproduct;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
/**
 * Created by dayuer on 19/7/2.
 * 文件管理工具
 */
public class ModelRootFileUtil {
    public static String mRecordVideoFileDownloadDir = "com.huozhongedu/download/videodownload";
    /**
     * 检测sdcard是否可用
     *
     * @return true为可用，否则为不可用
     */
    public static boolean isSDCardAvailable() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    public static boolean isCheckSDCardWarning() {
        return !isSDCardAvailable();
    }

    public static boolean createDir(String path) {
        if (isCheckSDCardWarning()) {
            return false;
        }

        if (TextUtils.isEmpty(path)) {
            return false;
        }

        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return true;
    }

    //创建文件
    public static File createFile(String path, String filename) {
        if (!createDir(path)) {
            return null;
        }

        if (TextUtils.isEmpty(filename)) {
            return null;
        }

        File file = null;
        file = new File(path, filename);
        if (file.exists()) {
            return file;
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            return null;
        }

        return file;
    }
    //创建一个文件夹，用来存放下载的文件
    public static File getRootFile(String dir){
        File sd = Environment.getExternalStorageDirectory();
        File rootFile = new File(sd,dir);
        if (!rootFile.exists()){
            rootFile.mkdirs();
        }
        return rootFile;
    }
    public static boolean isFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath))
            return false;

        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    /**
     * 删除单个文件
     * @param   filePath    被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 删除文件夹以及目录下的文件
     * @param   filePath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }

    /**
     *  根据路径删除指定的目录或文件，无论存在与否
     *@param filePath  要删除的目录或文件
     *@return 删除成功返回 true，否则返回 false。
     */
    public boolean DeleteFolder(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                // 为文件时调用删除文件方法
                return deleteFile(filePath);
            } else {
                // 为目录时调用删除目录方法
                return deleteDirectory(filePath);
            }
        }
    }
}
