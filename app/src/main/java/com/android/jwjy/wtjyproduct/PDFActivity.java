package com.android.jwjy.wtjyproduct;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * 加载pdf格式
 */
public class PDFActivity extends AppCompatActivity {
    private PDFView pdfView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_layout);
        pdfView = findViewById(R.id.pdfview);
        Intent intent = getIntent();
        String nameString = intent.getStringExtra("url"); // "https://download.brother.com/welcome/docp000648/cv_pt3600_schn_sig_lad962001.pdf"
        pdfView.loadOnlinePDF(nameString);

    }
}
