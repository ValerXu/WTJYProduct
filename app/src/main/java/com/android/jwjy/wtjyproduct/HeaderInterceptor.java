package com.android.jwjy.wtjyproduct;

import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.android.jwjy.wtjyproduct.util.ToastUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
/**
 * Created by dayuer on 19/7/2.
 * 通信，包头中拼入token studentid cookie
 */
public class HeaderInterceptor implements Interceptor {
    //保存cookie
    public static String cookie = null;

    public static String stuId = null;
    public static String permissioncode = null;

    public static ControlMainActivity context = null;
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        if(cookie != null) {
            builder.addHeader("Cookie", cookie);
            if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) {
                builder.addHeader("Connection", "close");
            }
        } else{
            Log.e("Cookie","Cookie not found");
        }
        if (stuId != null){
            builder.addHeader("Stuid", stuId);
            if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) {
                builder.addHeader("Connection", "close");
            }
        } else{
            Log.e("stuId","stuId not found");
        }
        if (permissioncode != null){
            builder.addHeader("permissioncode", permissioncode);
            if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) {
                builder.addHeader("Connection", "closbuilder.addHeader(\"permissioncode\", permissioncode);e");
            }
        } else{
            Log.e("permissioncode","permissioncode not found");
        }
        return chain.proceed(builder.build());
    }

    static boolean IsErrorCode(int code,String errorMessage){
        if (context == null){
            return true;
        }
        if (code == 401 && stuId == null){
            context.onClickLogout();
            ToastUtil.show(context, "请登录账号", Toast.LENGTH_SHORT);
            return false;
        } else if (code == 401){
            context.onClickLogout();
            ToastUtil.show(context, "由于账号异端登录，您已被迫下线。请重新登录。", Toast.LENGTH_SHORT);
            return false;
        }
        return true;
    }
}