package com.android.jwjy.wtjyproduct.helper;

import android.app.Activity;
import android.content.Context;
import android.support.constraint.Guideline;
import android.view.View;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnLayoutInflatedListener;
import com.app.hubert.guide.model.GuidePage;
import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.util.SharedPreferencesUtil;

/**
 * Created by ccy on 2019/5/9/15:54
 */
public class GuideHelper {
    private static final String PERMISSION_KEY = "GUIDE";
    private Context context;
    private int videoWidth;

    public GuideHelper(Context context, int videoWidth) {
        this.context = context;
        this.videoWidth = videoWidth;
    }

    public void show() {
        boolean permission = SharedPreferencesUtil.getBoolean(context, PERMISSION_KEY);
        if (!permission) {
            return;
        }
        GuidePage guidePageOne = new GuidePage();
        guidePageOne.setLayoutRes(R.layout.activity_watch_guide_one);
        guidePageOne.setEverywhereCancelable(false);
        guidePageOne.setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(View view, final Controller controller) {
                Guideline guideline = view.findViewById(R.id.guideline2);
                guideline.setGuidelineEnd(videoWidth);
                view.findViewById(R.id.next_iv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        controller.showPage(1);
                    }
                });
            }
        });
        GuidePage guidePageTwo = new GuidePage();
        guidePageTwo.setLayoutRes(R.layout.activity_watch_guide_two);
        guidePageTwo.setEverywhereCancelable(false);
        guidePageTwo.setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(View view, final Controller controller) {
                view.findViewById(R.id.kown_iv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferencesUtil.saveBoolean(context, PERMISSION_KEY, false);
                        controller.remove();
                    }
                });
            }
        });
        new NewbieGuide().with((Activity) context).setLabel("1").addGuidePage(guidePageOne).addGuidePage(guidePageTwo).alwaysShow(true).show();
    }
}
