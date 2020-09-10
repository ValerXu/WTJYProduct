package com.android.jwjy.wtjyproduct;

import com.aliyun.utils.VcPlayerLog;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by pengshuang on 31/08/2017.
 */
public class VidStsUtil {


    private static final String TAG = VidStsUtil.class.getSimpleName();

    public static void getVidSts(String stsUrl,String videoId, final OnStsResultListener onStsResultListener) {

        if (videoId == null) {
            VcPlayerLog.e(TAG, "e = videoId == null");
            onStsResultListener.onFail();
            return ;
        }
        if (videoId.equals("")) {
            VcPlayerLog.e(TAG, "e = videoId == null");
            onStsResultListener.onFail();
            return ;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(stsUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();
        HashMap<String, String> paramsMap1 = new HashMap<>();
        paramsMap1.put("video_id", videoId);
        String strEntity = gson.toJson(paramsMap1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.getAliCourseAccessVideo(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {

            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                if (response.body() == null) {
                    onStsResultListener.onFail();
                    VcPlayerLog.e(TAG, "e = 此课程暂无播放资源");
                    return;
                }
                ModelObservableInterface.BaseBean baseBean = response.body();
                if (baseBean == null) {
                    onStsResultListener.onFail();
                    VcPlayerLog.e(TAG, "e = 此课程暂无播放资源");
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(), baseBean.getErrorMsg())) {
                    onStsResultListener.onFail();
                    VcPlayerLog.e(TAG, "e = 此课程暂无播放资源");
                    return;
                }
                if (baseBean.getErrorCode() != 200) {
                    onStsResultListener.onFail();
                    VcPlayerLog.e(TAG, "e = 此课程暂无播放资源");
                    return;
                }
                if (baseBean.getData() == null) {
                    onStsResultListener.onFail();
                    VcPlayerLog.e(TAG, "e = 此课程暂无播放资源");
                    return ;
                }
                Map<String, Object> map = baseBean.getData();
                String SecurityToken = (String) map.get("SecurityToken");
                String AccessKeyId = (String) map.get("AccessKeyId");
                String AccessKeySecret = (String) map.get("AccessKeySecret");
//                String resourse_name = (String) map.get("resourse_name");
                if (SecurityToken == null || AccessKeyId == null || AccessKeySecret == null) {
                    onStsResultListener.onFail();
                    VcPlayerLog.e(TAG, "e = 此课程暂无播放资源");
                    return ;
                }
                onStsResultListener.onSuccess(videoId, AccessKeyId,AccessKeySecret, SecurityToken);
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                onStsResultListener.onFail();
                VcPlayerLog.e(TAG, "e = 此课程暂无播放资源");
                return ;
            }
        });
        return ;
    }

    public interface OnStsResultListener {
        void onSuccess(String vid, String akid, String akSecret, String token);

        void onFail();
    }

//    public static void getVidSts(final String url,final String vid, final OnStsResultListener onStsResultListener) {
//        AsyncTask<Void, Void, VidSts> asyncTask = new AsyncTask<Void, Void, VidSts>() {
//
//            @Override
//            protected VidSts doInBackground(Void... params) {
//                return getVidSts(url,vid);
//            }
//
//            @Override
//            protected void onPostExecute(VidSts s) {
//                if (s == null) {
//                    onStsResultListener.onFail();
//                } else {
//                    onStsResultListener.onSuccess(s.getVid(), s.getAccessKeyId(), s.getAccessKeySecret(), s.getSecurityToken());
//                }
//            }
//        };
//        asyncTask.execute();
//
//        return;
//    }


}
