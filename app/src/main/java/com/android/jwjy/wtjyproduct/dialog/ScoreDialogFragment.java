package com.android.jwjy.wtjyproduct.dialog;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.view.ScoreItemViewHolder;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.event.Callback;
import com.talkfun.sdk.module.ScoreItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class ScoreDialogFragment extends BaseDialog implements Callback<Void> {

    @BindView(R.id.et_msg)
    EditText etMsg;
    @BindView(R.id.ll_score)
    LinearLayout llScore;

    private static final String ARG_SCORE_ITEM_MAP_KEY = "scoreItemMap";

    List<ScoreItemViewHolder> scoreItemViewHolderList = new ArrayList<>();

    public static ScoreDialogFragment create(HashMap<String, ScoreItem> scoreItemMap) {
        ScoreDialogFragment dialog = new ScoreDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_SCORE_ITEM_MAP_KEY,scoreItemMap);
        dialog.setArguments(bundle);
        return dialog;
    }


    @Override
    protected int getContentLayout() {
        return R.layout.dialog_score;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    /**
     * 初始化控件
     */
    protected void initView() {
        super.initView();
        icon.setImageDrawable(getResources().getDrawable(R.mipmap.score_icon));
        title.setVisibility(View.VISIBLE);
        title.setText("评分");

        scoreItemViewHolderList.clear();

        Map<String, ScoreItem> scoreItemMap = (Map<String, ScoreItem>) getArguments().getSerializable(ARG_SCORE_ITEM_MAP_KEY);
        int scoreNameTextWidth = measureTextWidth(scoreItemMap);

        for (Map.Entry<String,ScoreItem> entry : scoreItemMap.entrySet()){
            ScoreItemViewHolder itemViewHolder = new ScoreItemViewHolder(getContext(),null);
            ScoreItem item = entry.getValue();
            itemViewHolder.setScoreLabel(item.getName())
                    .setScoreRate(item.getRate())
                    .setScoreKey(entry.getKey())
                    .setScoreLabelWidth(scoreNameTextWidth);
            scoreItemViewHolderList.add(itemViewHolder);
            llScore.addView(itemViewHolder.getView());
        }


    }

    private int measureTextWidth(Map<String, ScoreItem> scoreItemMap){
        String maxName = null;
        int maxLength = 0;
        for (Map.Entry<String,ScoreItem> entry : scoreItemMap.entrySet()){
            ScoreItem item = entry.getValue();
            if(item.getName().length() > maxLength){
                maxName = item.getName();
                maxLength = item.getName().length();
            }
        }

        Paint paint = new Paint();
       // paint.setTextSize(size);
        maxName = maxName + "：";
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,14,getResources().getDisplayMetrics()));
       float strWidth = paint.measureText(maxName) + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,getResources().getDisplayMetrics());
        return Math.round(strWidth)+ 1;
    }

    @OnClick({R.id.btn_submit})
    void onClickHandle(View v){
        switch (v.getId()){
            case R.id.btn_submit:
                sendScore();
                break;
        }
    }

    private void sendScore(){

        String msg = etMsg.getText().toString();

        Map<String,Object> map = new HashMap<>();

        for (ScoreItemViewHolder itemHolder: scoreItemViewHolderList) {
            map.put(itemHolder.getScoreKey(),itemHolder.getScore());
        }
        map.put("msg",msg);

        HtSdk.getInstance().sendScore(map,this);

    }


    /*@Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        if(rating < 1.0f){
            ratingBar.setRating(1.0f);
        }
    }*/

    @Override
    public void success(Void result) {
        Context context = getContext();
        if(context != null){
            Toast.makeText(context, "发送评分成功", Toast.LENGTH_SHORT).show();
        }
        this.dismissAllowingStateLoss();
    }

    @Override
    public void failed(String failed) {
        Context context = getContext();
        if(context == null)
            return;
        Toast.makeText(context, failed, Toast.LENGTH_SHORT).show();
    }
}
