package com.android.jwjy.wtjyproduct.update.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class FileUtils {
    /**
     * 复制文件
     * @param fromFile
     * @param toFile
     */
    public static void copyFile(File fromFile, File toFile) {

        if (!fromFile.exists())
            return;
        if (!fromFile.isFile())
            return;
        if (!fromFile.canRead())
            return;

        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }
        if (toFile.exists()) {
            toFile.delete();
        }
        FileInputStream fosfrom = null;
        FileOutputStream fosto = null;
        try {
            fosfrom = new FileInputStream(fromFile);
            fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c); //将内容写到新文件当中
            }


        } catch (Exception ex) {
            Log.e("readfile", ex.getMessage());
        }finally {
            if(fosfrom != null) {
                try {
                    fosfrom.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fosto != null){
                try {
                    fosto.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
