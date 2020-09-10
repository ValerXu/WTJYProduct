package com.android.jwjy.wtjyproduct.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.jwjy.wtjyproduct.R;

public class ScoreItemViewHolder {

    private TextView tvScoreLabel;
    private RatingBar ratingBar;
    private int rate = 5;
    private View view;
    private String scoreKey;
    public ScoreItemViewHolder(Context context, ViewGroup parent) {
        view = createView(context,parent);
        tvScoreLabel = view.findViewById(R.id.tv_label);
        ratingBar = view.findViewById(R.id.rating_bar);

    }

    private View createView(Context context, ViewGroup parent){
        View view  = View.inflate(context, R.layout.item_score,parent);
        return view;
    }

    public View getView(){
        return view;
    }

    public ScoreItemViewHolder setScoreLabel(String name){
        tvScoreLabel.setText(name+"：");
        return this;
    }

    public ScoreItemViewHolder setScoreLabelWidth(int width){
        ViewGroup.LayoutParams lp = tvScoreLabel.getLayoutParams();
        if(lp == null){
            lp = new ViewGroup.LayoutParams(width,ViewGroup.LayoutParams.WRAP_CONTENT);
        }else{
            lp.width = width;
        }
        tvScoreLabel.setLayoutParams(lp);
        return this;
    }

    public ScoreItemViewHolder setScoreRate(int rate){
       this.rate = rate;
       return this;
    }

    public ScoreItemViewHolder setScoreKey(String key){
        scoreKey = key;
        return this;
    }

    public String getScoreKey(){
        return scoreKey;
    }

    public int getScore(){

        return (int) (ratingBar.getRating() * rate / ratingBar.getNumStars());
    }
}
