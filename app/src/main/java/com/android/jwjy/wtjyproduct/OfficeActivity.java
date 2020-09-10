package com.android.jwjy.wtjyproduct;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 加载webview格式
 */

public class OfficeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office);
        Intent intent = getIntent();
        String nameString = intent.getStringExtra("url");
        WebView webView = findViewById(R.id.web_view);
        //使用微软打开，在线预览
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        /**
         * 加载完成后才关闭加载框
         */
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    //todo    可以在这里关闭弹窗
                }
            }
        });
//        String url = "https://wangxiao3.oss-cn-shanghai.aliyuncs.com/b4cac181-237d-481c-8e3b-e26a85e5076f1590830663982.pptx";
//        url = "https://wangxiao3.oss-cn-shanghai.aliyuncs.com/test/1.xlsx";
//        url = "https://wangxiao3.oss-cn-shanghai.aliyuncs.com/test/2.docx";
//        url = "https://wangxiao3.oss-cn-shanghai.aliyuncs.com/test/3.doc";
//        url = "https://wangxiao3.oss-cn-shanghai.aliyuncs.com/test/4.xls";
        webView.loadUrl("https://view.officeapps.live.com/op/view.aspx?src=" + nameString );//使用微软在线浏览方式
    }
}
