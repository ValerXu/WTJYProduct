package com.android.jwjy.wtjyproduct.manager;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.adapter.OTMVoteOptionAdapter;
import com.android.jwjy.wtjyproduct.imageload.GlideImageLoader;
import com.android.jwjy.wtjyproduct.util.ToastUtil;
import com.android.jwjy.wtjyproduct.view.MultipleStatusLayout;
import com.android.jwjy.wtjyproduct.view.NoScrollListView;
import com.android.jwjy.wtjyproduct.view.PhotoDialog;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.event.Callback;
import com.talkfun.sdk.module.VoteDetailEntity;
import com.talkfun.sdk.module.VoteEntity;
import com.talkfun.sdk.module.VotePubEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 投票详情页
 * Created by ccy on 2019/5/22/19:45
 */
public class OTMVoteDetailsViewManager implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Context context;
    private ConstraintLayout parentView;
    private LinearLayout detailsView;
    private TextView nickNameTV;
    private TextView voteStatusTV;
    private TextView forwardTimeTV;
    private TextView labelTV;
    private TextView titleTV;
    private ImageView imageIV;
    private TextView checkTypeTV;
    private NoScrollListView listView;
    private TextView yourAnswerTV;
    private TextView answerTV;
    private Button seneVoteBtn;
    private ProgressBar loadingPB;
    private OTMVoteOptionAdapter mOTMVoteOptionAdapter;

    private char[] choice = new char[]{
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    /**
     * 是否是单选
     */
    private boolean isSingleChoice;
    private String voteId;
    private OnSendVoteListener mOnSendVoteListener;
    private MultipleStatusLayout multiStatusLayout;
    private String imageUrl;
    private View imageRL;
    private OnBackListener onBackListener;

    public OTMVoteDetailsViewManager(Context context) {
        this.context = context;

    }

    /**
     * 显示投票开始详情
     *
     * @param contentView
     * @param data
     */
    public void showVoteStartDetail(ConstraintLayout contentView, VoteEntity data) {
        if (contentView == null || data == null) {
            return;
        }
        initView(contentView);
        getVoteDetails(data.getVid(), data.getVoted() == 1);
    }

    /**
     * 显示投票结束详情
     *
     * @param contentView
     * @param data
     */
    public void showVoteStopDetail(ConstraintLayout contentView, VotePubEntity data) {
        if (contentView == null || data == null) {
            return;
        }
        initView(contentView);
        getVoteDetails(data.getVid(), false);
    }

    private void initView(ConstraintLayout contentView) {
        addView(contentView);
        findViewById();
    }

    /**
     * 将详情页的View加入到Dialog中
     *
     * @param contentView
     */
    private void addView(ConstraintLayout contentView) {
        this.parentView = contentView;
        detailsView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.dialog_fragment_otm_vote_details, null);
        if (contentView.indexOfChild(detailsView) != -1) {
            contentView.removeView(detailsView);
        }
        contentView.addView(detailsView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void findViewById() {
        if (detailsView == null) {
            return;
        }
        multiStatusLayout = detailsView.findViewById(R.id.msl__multi_status_layout); //昵称
        nickNameTV = detailsView.findViewById(R.id.tv_nickname); //昵称
        voteStatusTV = detailsView.findViewById(R.id.tv_vote_status); //投票状态
        forwardTimeTV = detailsView.findViewById(R.id.tv_forward_time);//时间
        labelTV = detailsView.findViewById(R.id.tv_label);//标签
        titleTV = detailsView.findViewById(R.id.tv_title);//主题
        imageRL = detailsView.findViewById(R.id.rl_image_url);//图片
        imageIV = detailsView.findViewById(R.id.iv_image_url);//图片
        checkTypeTV = detailsView.findViewById(R.id.tv_check_type);//单选 多选
        listView = detailsView.findViewById(R.id.lv_option);//答案选项
        seneVoteBtn = detailsView.findViewById(R.id.btn_send_vote);//发送按钮
        yourAnswerTV = detailsView.findViewById(R.id.tv_you_answer); //答案
        answerTV = detailsView.findViewById(R.id.tv_answer);
        loadingPB = detailsView.findViewById(R.id.pb_loading);//加载圈
        seneVoteBtn.setOnClickListener(this);
        detailsView.findViewById(R.id.iv_back).setOnClickListener(this);
        imageIV.setOnClickListener(this);
        listView.setDividerHeight(0);
    }


    /**
     * 获取数据详情
     *
     * @param voted 是否已投票
     * @param vid
     */
    public void getVoteDetails(String vid, final boolean voted) {
        voteId = vid;
        multiStatusLayout.showLoading();
//        loadingPB.setVisibility(View.VISIBLE);
        HtSdk.getInstance().getVoteDetail(vid, new Callback<VoteDetailEntity>() {
            @Override
            public void success(VoteDetailEntity result) {
                multiStatusLayout.showContent();
//                loadingPB.setVisibility(View.GONE);
                if (result == null) {
                    return;
                }
                setData(result, voted);
            }

            @Override
            public void failed(String failed) {
                multiStatusLayout.showEmpty();
                loadingPB.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 刷新详情页
     *
     * @param vid
     */
    public void refreshView(String vid) {
        if (detailsView == null) {
            return;
        }
        getVoteDetails(vid, true);
    }

    /**
     * @param result
     * @param isVoted 是否已投过票
     */
    private void setData(VoteDetailEntity result, boolean isVoted) {
        int isShow = result.getIsShow();
        VoteDetailEntity.InfoBean infoBean = result.getInfo();
        if (infoBean == null) {
            return;
        }
        boolean isVoteStop = !(TextUtils.isEmpty(infoBean.getEndTime()) || TextUtils.equals("0", infoBean.getEndTime()));
        yourAnswerTV.setVisibility(isVoteStop ? View.VISIBLE : View.GONE);
        answerTV.setVisibility(isVoteStop ? View.VISIBLE : View.GONE);
        nickNameTV.setText(infoBean.getNickname());
        voteStatusTV.setText(isVoteStop ? "已结束" : isVoted ? "已投票" : "投票中");
        voteStatusTV.setTextColor(isVoteStop ? Color.parseColor("#6D829E") : isVoted ? Color.parseColor("#6D829E") : Color.parseColor("#FF9F2D"));
        forwardTimeTV.setText(infoBean.getStartTime() + " " + "发起投票");
        if (TextUtils.isEmpty(infoBean.getImageUrl())) {//主题是文字就没有标签，主题是图片就有标签
            titleTV.setText(infoBean.getTitle());
            titleTV.setVisibility(View.VISIBLE);
            labelTV.setVisibility(View.GONE);
            imageRL.setVisibility(View.GONE);
        } else {
            imageRL.setVisibility(View.VISIBLE);
            titleTV.setVisibility(View.GONE);
            labelTV.setText("标签:\t" + infoBean.getLabel());
            labelTV.setVisibility(View.VISIBLE);
            imageUrl = infoBean.getImageUrl();
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();
            GlideImageLoader.create(imageIV).load(imageUrl, requestOptions);
        }


        isSingleChoice = TextUtils.equals("1", infoBean.getOptional());
        mOTMVoteOptionAdapter = new OTMVoteOptionAdapter(context);
        if (isVoteStop || isVoted) {//已投票或者投票结束
            seneVoteBtn.setVisibility(View.GONE);
            mOTMVoteOptionAdapter.disableAllItemChoser();
//            listView.setItemsCanFocus(false);
        } else {//投票未结束 &&未投票
            seneVoteBtn.setVisibility(View.VISIBLE);
//            listView.setItemsCanFocus(true);
            mOTMVoteOptionAdapter.enableItemChoser();
            listView.setChoiceMode(isSingleChoice ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_MULTIPLE);
            listView.setOnItemClickListener(this);
        }
        mOTMVoteOptionAdapter.setIsShowAnswer(isVoteStop && isShow == 1);
        String checkType = isSingleChoice ? "单选" : "多选";
        checkTypeTV.setText(String.format("%s(%s):", "选票选项", checkType));
        mOTMVoteOptionAdapter.setCheckboxStyle(isSingleChoice);
        listView.setAdapter(mOTMVoteOptionAdapter);
        mOTMVoteOptionAdapter.addItems(result.getStatsList());
        setVotedOption(result, isVoteStop, isVoted);
        if (isVoteStop) {//当前用户答案
            yourAnswerTV.setText("你的选项:\t" + filterYourAnswer(result));
        }
        setAnswer(result, isShow);

    }

    /**
     * 答案设置
     *
     * @param voteDetailEntity
     * @param isShow           是否公布答案 1：公布 0：未公布
     */
    private void setAnswer(VoteDetailEntity voteDetailEntity, int isShow) {
        if (isShow == 0) {
            return;
        }
        if (isAnswerRight(voteDetailEntity)) {
            answerTV.setTextColor(Color.parseColor("#4D9AFF"));
            answerTV.setText("回答正确");
            return;
        }
        VoteDetailEntity.InfoBean infoBean = voteDetailEntity.getInfo();
        if (/*isShow == 1 && */infoBean != null && !TextUtils.isEmpty(infoBean.getAnswer())) {//正确答案 索引从0开始
            String answer = "";
            for (String s : infoBean.getAnswer().split(",")) {
                answer += choice[Integer.parseInt(s)];
            }
            answerTV.setTextColor(Color.parseColor("#FF2C2C"));
            answerTV.setText("正确答案:\t" + answer);
        }
    }

    /**
     * 当前用户是否回答正确
     */
    private boolean isAnswerRight(VoteDetailEntity voteDetailEntity) {
        boolean isRight = false;
        if (voteDetailEntity == null || voteDetailEntity.getRightUser() == null || voteDetailEntity.getRightUser().isEmpty()) {
            return isRight;
        }
        List<VoteDetailEntity.RightUserBean> rightUserBeanList = voteDetailEntity.getRightUser();
        for (VoteDetailEntity.RightUserBean rightUserBean : rightUserBeanList) {
            if (TextUtils.equals(rightUserBean.getUid(), getUid())) {
                isRight = true;
                break;
            }
        }
        return isRight;
    }

    /**
     * 设置已选择的选项
     *
     * @param result
     * @param isVoteStop
     * @param isVoted
     */
    private void setVotedOption(VoteDetailEntity result, boolean isVoteStop, boolean isVoted) {
        if (isVoteStop || isVoted) {
            for (Integer integer : filterYourAnswerPosition(result)) {//设置用户选中哪几项
                if (isSingleChoice) { //单选
                    mOTMVoteOptionAdapter.setSelectSingleItem(integer);
                } else {//多选
                    mOTMVoteOptionAdapter.setSelectMultiItem(integer, 1);
                }

            }
            mOTMVoteOptionAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 刷选出自己的答案
     *
     * @param result
     * @return
     */
    private String filterYourAnswer(VoteDetailEntity result) {
        List<VoteDetailEntity.UserBean> userBeanList = result.getUser();
        String yourAnswer = "";
        if (userBeanList == null || userBeanList.isEmpty()) {
            return yourAnswer;
        }
        for (VoteDetailEntity.UserBean userBean : userBeanList) {
            if (TextUtils.equals(userBean.getUid(), getUid())) {
                for (String s : userBean.getOption()) {
                    yourAnswer += choice[Integer.parseInt(s) - 1];
                }
                break;
            }
        }
        return yourAnswer;
    }

    /**
     * 获取UID
     *
     * @return
     */
    private String getUid() {
        if (HtSdk.getInstance().getRoomInfo() == null && HtSdk.getInstance().getRoomInfo().getUser() == null) {
            return "";
        }
        return HtSdk.getInstance().getRoomInfo().getUser().getUid();
    }

    /**
     * 过滤当前用户答案选择的位置
     *
     * @param result
     * @return
     */
    private List<Integer> filterYourAnswerPosition(VoteDetailEntity result) {
        List<VoteDetailEntity.UserBean> userBeanList = result.getUser();
        List<Integer> filterYourAnswerPositionList = new ArrayList<>();
        if (userBeanList == null || userBeanList.isEmpty()) {
            return filterYourAnswerPositionList;
        }
        for (VoteDetailEntity.UserBean userBean : userBeanList) {
            if (HtSdk.getInstance().getRoomInfo() == null && HtSdk.getInstance().getRoomInfo().getUser() == null) {
                break;
            }
            if (TextUtils.equals(userBean.getUid(), HtSdk.getInstance().getRoomInfo().getUser().getUid())) {
                for (String s : userBean.getOption()) {
                    filterYourAnswerPositionList.add(Integer.parseInt(s) - 1);
                }
                break;
            }
        }
        return filterYourAnswerPositionList;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                back();
                break;
            case R.id.btn_send_vote:
                sendVote();
                break;
            case R.id.iv_image_url:
//                EventBusUtil.postEvent(new Event(EventType.PHOTO_VIEW,imageUrl));
                PhotoDialog photoPopView = new PhotoDialog(context);
                photoPopView.setImageUrl(imageUrl);
                photoPopView.show();
                break;
        }
    }

    /**
     * 返回
     */
    public void back() {
        if (parentView == null || detailsView == null || parentView.indexOfChild(detailsView) == -1) {
            return;
        }
        parentView.removeView(detailsView);
        if (onBackListener != null) {
            onBackListener.onback();
        }
    }

    public void setOnBackListener(OnBackListener onBackListener) {
        this.onBackListener = onBackListener;
    }

    public interface OnBackListener {
        void onback();
    }

    /**
     * 发送投票
     */
    private void sendVote() {
        List<String> optlist = new ArrayList<>();
        if (isSingleChoice) {
            if (mOTMVoteOptionAdapter.getSelectSingleItem()!=-1) {
                optlist.add(mOTMVoteOptionAdapter.getSelectSingleItem() + 1 + "");
            }

        } else {
            int[] selectItemArr = mOTMVoteOptionAdapter.getSelectMultiItemArr();
            if (selectItemArr == null) {
                return;
            }
            for (int i = 0; i < selectItemArr.length; i++) {
                if (selectItemArr[i] == 1) {
                    optlist.add(i + 1 + "");
                }
            }
        }
        if (optlist.isEmpty()) {
            ToastUtil.show(context, "当前未选中答案！！");
            return;
        }
        loadingPB.setVisibility(View.VISIBLE);
        HtSdk.getInstance().sendVote(voteId, optlist.toString(), new Callback() {
            @Override
            public void success(Object result) {
                if (mOnSendVoteListener != null) {
                    mOnSendVoteListener.succuess(voteId);
                }
                mOTMVoteOptionAdapter.disableAllItemChoser();
                loadingPB.setVisibility(View.GONE);
                ToastUtil.show(context, "投票成功");
                seneVoteBtn.setVisibility(View.GONE);
                voteStatusTV.setText("已投票");
                voteStatusTV.setTextColor(Color.parseColor("#6D829E"));
            }

            @Override
            public void failed(String failed) {
                loadingPB.setVisibility(View.GONE);
                ToastUtil.show(context, "投票失败");
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (isSingleChoice) {
            boolean isChoice = mOTMVoteOptionAdapter.getSelectSingleItem() == position;
            mOTMVoteOptionAdapter.setSelectSingleItem(isChoice ? -1 : position);
//            setSendVoteBtnStatus(mOTMVoteOptionAdapter.getSelectSingleItem() > -1);
        } else {
            boolean isCheck = mOTMVoteOptionAdapter.isSelectedMultiItem(position);
            mOTMVoteOptionAdapter.setSelectMultiItem(position, isCheck ? 0 : 1);
//            seneVoteBtn.setSelected(mOTMVoteOptionAdapter.isExistMultiItemValue());
        }
        mOTMVoteOptionAdapter.notifyDataSetChanged();
    }

    /**
     * 设置发送按钮的状态
     *
     * @param isSelect
     */
    public void setSendVoteBtnStatus(boolean isSelect) {
        seneVoteBtn.setSelected(isSelect);
    }

    public void setOnSendVoteListener(OnSendVoteListener mOnSendVoteListener) {
        this.mOnSendVoteListener = mOnSendVoteListener;
    }

    public interface OnSendVoteListener {
        void succuess(String voteId);
    }
}
