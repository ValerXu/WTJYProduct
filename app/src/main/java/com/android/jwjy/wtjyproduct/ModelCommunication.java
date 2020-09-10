package com.android.jwjy.wtjyproduct;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import static android.content.ContentValues.TAG;
/**
 * Created by dayuer on 19/7/2.
 * 网络通信模块
 */
public class ModelCommunication {
    private static final int TIME_OUT = 100 * 1000; // 超时时间

    /**
     * http 网络数据传输 post
     * @param myUrl 链接地址
     * @param content 传输内容
     */
    public static void myPostHttpConnection(final Context mainContext, final CommunicationInterface context, final String myUrl, final String content, final String param1, final String param2){
        if (myUrl.equals("") || context == null){
            context.onSendOver(myUrl,content,"",false,param1,param2);
            return;
        }
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    byte[] end_data = content.getBytes();
                    URL url = new URL(params[0]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");//设置meta参数
                    conn.setRequestProperty("Charset","utf-8");
                    OutputStream out = new DataOutputStream(conn.getOutputStream());
                    Log.d("TAG", "myPostHttpConnection上传完毕数据" + content);
                    out.write(end_data);
                    out.flush();
                    out.close();
                    // 定义BufferedReader输入流来读取URL的响应
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            conn.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        context.onSendOver(myUrl,content,line,true,param1,param2);
                    }
                } catch (Exception e) {
                    if (mainContext != null) {
                        ((InterfaceToast) mainContext).ToastMake("发送POST请求出现异常." + e.toString(), Toast.LENGTH_LONG);
                    }
                    Log.d("TAG", "myPostHttpConnection发送POST请求出现异常。" + e);
                    context.onSendOver(myUrl,content,"",false,param1,param2);
                }
                return null;
            }
            @Override
            protected void onPostExecute(String result) {
                Log.d("TAG", "myPostHttpConnection :" + result);
            }
        }.execute(myUrl);
    }

    /**
     * http 网络数据传输 get
     * @param myUrl 链接地址
     * @param content 传输内容
     */
    public static void myGetHttpConnection(final Context mainContext, final CommunicationInterface context, final String myUrl, final String content, final String param1, final String param2) {

        //在子线程中操作网络请求
        new Thread(() ->{
                //urlConnection请求服务器，验证
                try {
                    //1：url对象
                    URL url = new URL(myUrl);
                    //2;url.openconnection
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //3
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10 * 1000);
                    //4
                    int code = conn.getResponseCode();
                    if (code == 200){
                        InputStream inputStream = conn.getInputStream();
                        String returnMessage = convertStreamToString(inputStream);
                        context.onSendOver(myUrl,returnMessage,returnMessage,true,param1,param2);
                        Log.d("TAG", "myGetHttpConnection返回数据。 " + returnMessage);
                    } else {
                        Log.d("TAG", "myGetHttpConnection返回数据失败。 ");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ((InterfaceToast)mainContext).ToastMake("myGetHttpConnection: 网络请求失败。" + e.toString(), Toast.LENGTH_LONG);
                    Log.d("TAG", "myGetHttpConnection网络请求失败。 " + e.toString());
                }
            }).start();
    }

    /**
     * android上传文件到服务器
     *
     * @param file
     *            需要上传的文件
     * @param RequestURL
     *            请求的rul
     * @return 返回响应的内容
     *
     *  [urlRequest setHTTPMethod:@"POST"];
    [urlRequest setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    [urlRequest setValue:@"multipart/form-data; boundary=heima" forHTTPHeaderField:@"Content-Type"];


    NSMutableData * body = [NSMutableData data];
    NSData * data = [NSData dataWithContentsOfFile:createPath];


    //    2> 文件参数
    [body appendData:HMEncode(@"--heima\r\n")];
    [body appendData:HMEncode(@"Content-Disposition: form-data; name=\"file\"; filename=\"test123.png\"\r\n")];
    [body appendData:HMEncode(@"Content-Type: application/zip\r\n")];
//vvvvvvv
    [body appendData:HMEncode(@"\r\n")];
    [body appendData:data];
    [body appendData:HMEncode(@"\r\n")];

    //    3> 结束标记 ：参数结束的标记
    [body appendData:HMEncode(@"--heima--\r\n")];
    [urlRequest setHTTPBody:body];
     */
    public static boolean uploadFile(File file, String RequestURL, String ContentType, String packageHead, String packageTail) {
        String BOUNDARY = "huozhongedu"; // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", "utf-8"); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
                    + BOUNDARY);
            if (!packageHead.equals("")){
                conn.setRequestProperty("wx-info", packageHead);
            }
            if (file != null) {
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                DataOutputStream dos = new DataOutputStream(
                        conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */
                sb.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                        + file.getName() + "\"");
//                if (!packageHead.equals("")){
//                    sb.append("; " + packageHead);
//                }
                sb.append(LINE_END);
//                sb.append("Content-Type: application/zip" + LINE_END);
                sb.append("Content-Type: " + ContentType + LINE_END);
//                sb.append("Content-Type: audio/mp3" + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                Log.e(TAG, "request content   " + sb.toString());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024 * 1024];
                int len ;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                    Log.e(TAG, "request content   " + Arrays.toString(bytes));
                }
                is.close();
                dos.write(LINE_END.getBytes());
                Log.e(TAG, "request content   " + Arrays.toString(LINE_END.getBytes()));
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
                        .getBytes();
                dos.write(end_data);
                Log.e(TAG, "request content   " + PREFIX + BOUNDARY + PREFIX + LINE_END);
                dos.flush();
                dos.close();
                /**
                 * 获取响应码 200=成功 当响应成功，获取响应的流
                 */
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.e(TAG, "response message:" + line);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "response message1:" + e.toString());
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "response message:" + e.toString());
            return false;
        }
        return true;
    }
    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "/n");   //这里的“/n”一定要加上，原因见http://blog.itpub.net/28932681/viewspace-2286126/
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    public interface CommunicationInterface {
        void onSendOver(String myUrl, String content, String returnMessage, boolean isSuccess, String param1, String param2);
    }
}
