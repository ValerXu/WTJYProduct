package com.android.jwjy.wtjyproduct.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.android.jwjy.wtjyproduct.BuildConfig;
import com.android.jwjy.wtjyproduct.update.model.entity.NewVersionEntity;
import com.android.jwjy.wtjyproduct.update.presenter.IUpdateCallback;
import com.android.jwjy.wtjyproduct.update.presenter.UpdatePresenter;

import java.io.File;


public class UpdateManager implements IUpdateCallback<NewVersionEntity> {

    private static final String TAG = UpdateManager.class.getSimpleName();

    private UpdatePresenter updatePresenter;

    private IOnUpdateListener onUpdateListenre;
    private Activity mContext;
    private ProgressDialog progressDialog;

    public UpdateManager(Activity context, IOnUpdateListener listener) {
        mContext = context;
        updatePresenter = new UpdatePresenter(context, this);
        this.onUpdateListenre = listener;

    }


    public void checkVersion() {
        updatePresenter.checkVersion();
    }

    public void stopDownload() {
        updatePresenter.stopDownloadApk();
        if (onUpdateListenre != null)
            onUpdateListenre.updateEnd();
    }

    public void destroy() {
        updatePresenter.destroy();
        updatePresenter = null;
        onUpdateListenre = null;
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        progressDialog = null;
    }


    //------------------------------------------回调---------------------------------------------

    /**
     * 没有新版本
     */
    @Override
    public void noNewVersion() {
        if (onUpdateListenre != null)
            onUpdateListenre.updateEnd();
    }


    /**
     * 有新版本，提示更新
     *
     * @param newVersionEntity
     */
    @Override
    public void newVersion(NewVersionEntity newVersionEntity) {
        updateNotice(newVersionEntity);
    }


    /**
     * 下载进度
     *
     * @param percent
     */
    @Override
    public void downloadProgress(int percent) {
        updateDownloadDialog(percent);
    }

    //下载成功
    @Override
    public void downloadSuccess(String url) {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        installApp(url);   //安装
    }

    //下载失败
    @Override
    public void downloadFail(String msg) {
        Log.e(TAG, msg);
        if (onUpdateListenre != null)
            onUpdateListenre.updateEnd();
    }


    //安装应用 install application

    private void installApp(String url) {

       if(mContext == null)
           return;

        Uri uri;
        File apkFile = new File(url);

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            uri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID  + ".fileProvider", apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }else {

            uri = Uri.fromFile(apkFile);
        }

        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        mContext.startActivity(intent);

    }


    //--------------------------------------dialog--------------------------------------------

    /**
     * 提示更新
     */
    private void updateNotice(final NewVersionEntity newVersionEntity) {
        AlertDialog loadFailDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("更新提示");
        String content = newVersionEntity.getContent();
        if (!TextUtils.isEmpty(content))
            builder.setMessage(content);
        else
            builder.setMessage("有新版本更新");
        builder.setPositiveButton(("立即更新"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            /*    File file = new File(cachePath);
                //本地是否缓存
                if (file.exists() && AppInfoUtil.getLocalCacheAppVersionCode(activity) == newVersionEntity.getVersionCode()) {  //如果apk存在，直接跳转安装
                    installApp();
                    return;
                } else*/
                updatePresenter.downloadApk(newVersionEntity.getUrl());   //如果不存在则先下载再安装
            }
        });
        builder.setNegativeButton("下次更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onUpdateListenre != null)
                    onUpdateListenre.updateEnd();
            }
        });
        loadFailDialog = builder.create();
        loadFailDialog.setCancelable(false);
        loadFailDialog.show();
    }


    /**
     * 更新下载进度弹创
     *
     * @param progress
     */
    private void updateDownloadDialog(int progress) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("下载");
            progressDialog.setMax(100);
            progressDialog.setCancelable(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    progressDialog.dismiss();
                    stopDownload();
                }
            });
        } else {
            progressDialog.setProgress(progress);
            //progressDialog.setMessage("已下载：" + progress + "%");
        }
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
}
