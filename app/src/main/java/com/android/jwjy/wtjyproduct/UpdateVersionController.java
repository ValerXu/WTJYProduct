package com.android.jwjy.wtjyproduct;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * Created by dayuer on 19/7/2.
 * 更新版本号
 */
public class UpdateVersionController {
    private Context context;
    private float versionCode;//当前版本号
    private Dialog dialog; //提示用户更新的dialog
    private ProgressDialog pd;  //下载进度条

    public static UpdateVersionController getInstance(Context context) {
        return new UpdateVersionController(context);
    }

    public UpdateVersionController(Context context) {
        this.context = context;
    }
    /*
     * 记得运行该方法
     */
    public void forceCheckUpdateInfo(Integer tf_super,String version,String url){
        if (version.equals("")){
            return;
        }
        //获取版本号，这个版本号为未更新的版本号
        versionCode = getVerCode(context);
        //更新app版本号比对，info新版本号和当前的版本号versionCode做对比，如果新版本号大于本版本就运行更新方法showUpdataDialog()
        if ( Float.valueOf(version) > versionCode) {
            showUpdataDialog(tf_super,url);
        }
    }

    /**
     * 弹出对话框提示用户更新
     */
    protected void showUpdataDialog(Integer tf_super,String url) {
        dialog = new Dialog(context, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        if (tf_super != null && tf_super == 1){ // 强制更新
            dialog.setContentView(R.layout.dialog_sure);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            ((TextView) dialog.findViewById(R.id.tip)).setText("更新应用");
            ((TextView) dialog.findViewById(R.id.dialog_content)).setText("为保证您能正常使用，您必须更新此应用");
            // 确认更新
            dialog.findViewById(R.id.button_sure).setOnClickListener(v -> {
                dialog.dismiss();
                downLoadApk(url);
            });
        } else {
            dialog.setContentView(R.layout.dialog_notip_sure_cancel);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            ((TextView) dialog.findViewById(R.id.dialog_content)).setText("是否进行更新?");
            TextView button_cancel = dialog.findViewById(R.id.button_cancel);
            //        cancelBtn.setVisibility("0".equals(info3) ? View.GONE : View.VISIBLE);
            // 取消更新
            button_cancel.setOnClickListener(v -> dialog.dismiss());
            // 确认更新
            dialog.findViewById(R.id.button_sure).setOnClickListener(v -> {
                dialog.dismiss();
                downLoadApk(url);
            });
        }
        dialog.show();
    }

    /**
     * 步骤三：下载文件
     */
    private void downLoadApk(String url) {
        // 进度条对话框
        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("下载中...");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        // 监听返回键--防止下载的时候点击返回
        pd.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                Toast.makeText(context, "正在下载请稍后", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        });
        // Sdcard不可用
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, "SD卡不可用", Toast.LENGTH_SHORT).show();

        } else {
            pd.show();
            //下载的子线程
            new Thread() {
                @Override
                public void run() {
                    try {
                        // 在子线程中下载APK文件url = "http://192.168.30.122/test/HZJY_1.0_2019-12-26.apk"
                        File file = getFileFromServer(url, pd);
                        sleep(1000);
                        // 安装APK文件
                        OpenFileUtil.openFileByPath(context,file.toString());
                        pd.dismiss(); // 结束掉进度条对话框
                    } catch (Exception e) {
//                        Toast.makeText(context, "文件下载失败了", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        Log.e("","errorwwwww" + e.toString());
                        e.printStackTrace();
                    }
                }

            }.start();
        }
    }

    /**
     * 从服务器下载apk
     */
    public File getFileFromServer(String path, ProgressDialog pd) throws Exception {
        // 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            URL url = new URL(path);
            HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //获取到文件的大小
            int max = conn.getContentLength();
            pd.setMax(max / 1024);
            InputStream is = conn.getInputStream();
//            long time= System.currentTimeMillis();//当前时间的毫秒数
            String pathS[] = conn.getURL().getFile().split("/");
            String filePath = "";
            if (pathS != null){
                if (pathS.length > 1){
                    filePath = pathS[pathS.length - 1];
                }
            }
            File file = new File(Environment.getExternalStorageDirectory(), filePath);
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len ;
            int total = 0;
            while((len = bis.read(buffer)) != -1){
                fos.write(buffer, 0, len);
                total += len;
                //获取当前下载量
                pd.setProgress(total / 1024);
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        } else {
            return null;
        }
    }
    /**
     * 获取版本号
     */
    public static float getVerCode(Context context) {
        String verCode = "-1";
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = context.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);

            verCode = packInfo.versionName;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Float.valueOf(verCode);
    }
}

