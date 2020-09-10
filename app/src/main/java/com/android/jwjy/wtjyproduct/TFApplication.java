package com.android.jwjy.wtjyproduct;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.android.jwjy.wtjyproduct.consts.MainConsts;
import com.android.jwjy.wtjyproduct.util.ActivityStacks;
import com.google.gson.Gson;
import com.talkfun.sdk.log.TalkFunLogger;
import com.talkfun.sdk.offline.PlaybackDownloader;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import cn.jpush.android.api.JPushInterface;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dayuer on 19/7/2.
 * 应用
 */
public class TFApplication extends Application {
    private String mIpadress = "http://wangxiao.jianweijiaoyu.com/";
    @Override
    public void onCreate() {
        super.onCreate();
        //记录崩溃信息
        final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {


            //获取崩溃时的UNIX时间戳
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("version:" + getVerCode(this));
            stringBuilder.append(":\n");
            long timeMillis = System.currentTimeMillis();
            //将时间戳转换成人类能看懂的格式，建立一个String拼接器
            stringBuilder.append(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(timeMillis)));
            stringBuilder.append(":\n");
            //获取错误信息
            stringBuilder.append(throwable.getMessage());
            stringBuilder.append("\n");
            //获取堆栈信息
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            stringBuilder.append(sw.toString());

            //这就是完整的错误信息了，你可以拿来上传服务器，或者做成本地文件保存等等等等
            String errorLog = stringBuilder.toString();
            UploadUncaughtException(errorLog);
            //最后如何处理这个崩溃，这里使用默认的处理方式让APP停止运行
            defaultHandler.uncaughtException(thread, throwable);
        });
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        String rid = JPushInterface.getRegistrationID(this);
//        Log.e("rid","rid   " + rid);
//        String sha1 = getCertificateSHA1Fingerprint(this);
//        Log.e("sha1","sha1  " + sha1);
//        Integer aa = null;
//        float bb = Float.valueOf(aa);
//        //初始化点播下载
//        initPlaybackDownLoader();
        TalkFunLogger.setLogLevel(TalkFunLogger.LogLevel.ALL);
        CrashReport.initCrashReport(getApplicationContext(), MainConsts.BUGLY_ID, true);
    }

    //上传崩溃日志
    private void UploadUncaughtException(String errorString) {
        if (errorString == null){
            return;
        }
        if (errorString.equals("")){
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();
        HashMap<String,String> paramsMap1 = new HashMap<>();
        paramsMap1.put("errorString", errorString);
        String strEntity = gson.toJson(paramsMap1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.UploadUncaughtException(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {

            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                if (response.body() == null){
                    return;
                }
                ModelObservableInterface.BaseBean baseBean = response.body();
                if (baseBean == null){
                    return;
                }
                if (baseBean.getErrorCode() != 200){
                    return;
                }
                if (baseBean.getData() == null){
                    return;
                }
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
//                Log.e("aaaaaaaaaaaaaa",t.getMessage().toString());
                return;
            }
        });
    }

    public void initPlaybackDownLoader() {
        PlaybackDownloader.getInstance().init(this);
        PlaybackDownloader.getInstance().setDownLoadThreadSize(3);
    }
//
//    //这个是获取SHA1的方法
//    public static String getCertificateSHA1Fingerprint(Context context) {
//        //获取包管理器
//        PackageManager pm = context.getPackageManager();
//        //获取当前要获取SHA1值的包名，也可以用其他的包名，但需要注意，
//        //在用其他包名的前提是，此方法传递的参数Context应该是对应包的上下文。
//        String packageName = context.getPackageName();
//        //返回包括在包中的签名信息
//        int flags = PackageManager.GET_SIGNATURES;
//        PackageInfo packageInfo = null;
//        try {
//            //获得包的所有内容信息类
//            packageInfo = pm.getPackageInfo(packageName, flags);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        //签名信息
//        Signature[] signatures = packageInfo.signatures;
//        byte[] cert = signatures[0].toByteArray();
//        //将签名转换为字节数组流
//        InputStream input = new ByteArrayInputStream(cert);
//        //证书工厂类，这个类实现了出厂合格证算法的功能
//        CertificateFactory cf = null;
//        try {
//            cf = CertificateFactory.getInstance("X509");
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        }
//        //X509证书，X.509是一种非常通用的证书格式
//        X509Certificate c = null;
//        try {
//            c = (X509Certificate) cf.generateCertificate(input);
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        }
//        String hexString = null;
//        try {
//            //加密算法的类，这里的参数可以使MD4,MD5等加密算法
//            MessageDigest md = MessageDigest.getInstance("SHA1");
//            //获得公钥
//            byte[] publicKey = md.digest(c.getEncoded());
//            //字节到十六进制的格式转换
//            hexString = byte2HexFormatted(publicKey);
//        } catch (NoSuchAlgorithmException e1) {
//            e1.printStackTrace();
//        } catch (CertificateEncodingException e) {
//            e.printStackTrace();
//        }
//        return hexString;
//    }
//    //这里是将获取到得编码进行16进制转换
//    private static String byte2HexFormatted(byte[] arr) {
//        StringBuilder str = new StringBuilder(arr.length * 2);
//        for (int i = 0; i < arr.length; i++) {
//            String h = Integer.toHexString(arr[i]);
//            int l = h.length();
//            if (l == 1)
//                h = "0" + h;
//            if (l > 2)
//                h = h.substring(l - 2, l);
//            str.append(h.toUpperCase());
//            if (i < (arr.length - 1))
//                str.append(':');
//        }
//        return str.toString();
//    }
    @Override
    public void onTerminate() {
        super.onTerminate();
        System.exit(0);
    }

    public static void exit() {
        /**终止应用程序对象时调用，不保证一定被调用 ,退出移除所有的下载任务*/
        ActivityStacks.getInstance().finishAllActivity();
        //释放离线下载资源
        PlaybackDownloader.getInstance().destroy();
        TalkFunLogger.release();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
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
