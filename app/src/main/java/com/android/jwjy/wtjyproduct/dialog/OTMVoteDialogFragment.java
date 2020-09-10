package com.android.jwjy.wtjyproduct.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.adapter.OTMVoteListAdapter;
import com.android.jwjy.wtjyproduct.base.BaseDatabindingAdapter;
import com.android.jwjy.wtjyproduct.manager.OTMVoteDetailsViewManager;
import com.android.jwjy.wtjyproduct.util.DimensionUtils;
import com.talkfun.sdk.module.VoteDelEntity;
import com.talkfun.sdk.module.VoteEntity;
import com.talkfun.sdk.module.VotePubEntity;

import java.util.List;

/**
 * 1V16 投票
 * Created by ccy on 2019/5/21/18:31
 */
public class OTMVoteDialogFragment extends BaseDialogFragment implements BaseDatabindingAdapter.OnChildClickListener, OTMVoteDetailsViewManager.OnSendVoteListener, OTMVoteDetailsViewManager.OnBackListener {
    private OTMVoteListAdapter otmVoteListAdapter;
    private ConstraintLayout contentView;
    private RecyclerView voteRV;
    private OTMVoteDetailsViewManager mOTMVoteDetailsViewManager;
    private String voteId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = (ConstraintLayout) inflater.inflate(R.layout.dialog_fragment_otm_vote, container, false);
        voteRV = contentView.findViewById(R.id.rv_vote);
        getDialog().setCanceledOnTouchOutside(true);
        return contentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setWindowAttribute();
        setAdapter();
        init();
    }

    private void init() {
        mOTMVoteDetailsViewManager = new OTMVoteDetailsViewManager(getContext());
        mOTMVoteDetailsViewManager.setOnSendVoteListener(this);
        mOTMVoteDetailsViewManager.setOnBackListener(this);
    }

    private void setWindowAttribute() {
        setCancelable(true);
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setWindowAnimations(R.style.pop_enter_exit_anim);
        int dimenId = DimensionUtils.isPad(getContext()) ? R.dimen.dp_175 : R.dimen.dp_260;
        int width = (int) getActivity().getResources().getDimension(dimenId);
        int height = DimensionUtils.isPad(getContext()) ? (int) (DimensionUtils.getScreenHeight(getContext()) - getActivity().getResources().getDimension((R.dimen.dp_106)))
                : ViewGroup.LayoutParams.MATCH_PARENT;
        window.setLayout(width, height);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        if (voteRV!=null) {
            voteRV.setVisibility(View.VISIBLE);
        }
    }

    private void setAdapter() {
        lazyInitAdapter();
        voteRV.setAdapter(otmVoteListAdapter);
        voteRV.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void lazyInitAdapter() {
        if (otmVoteListAdapter == null) {
            otmVoteListAdapter = new OTMVoteListAdapter();
            otmVoteListAdapter.setOnChildClickListener(this);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.OTMVoteDialogFragment);
    }

    /**
     * 添加投票列表
     *
     * @param voteList
     */
    public void addVoteList(List<Object> voteList) {
        lazyInitAdapter();
        otmVoteListAdapter.setDataList(voteList);
    }

    public void notifyItemChanged(int position) {
        otmVoteListAdapter.notifyItemChanged(position);
    }

    public void notifyDataSetChanged() {
        otmVoteListAdapter.notifyDataSetChanged();
    }

    public static OTMVoteDialogFragment create() {
        OTMVoteDialogFragment mOTMVoteDialogFragment = new OTMVoteDialogFragment();
        return mOTMVoteDialogFragment;
    }

    @Override
    public void onClick(View view, Object data, int position) {
        if (mOTMVoteDetailsViewManager == null) {
            return;
        }
        voteRV.setVisibility(View.GONE);
        if (data instanceof VoteEntity) {
            VoteEntity voteEntity = (VoteEntity) data;
            voteId = voteEntity.getVid();
            mOTMVoteDetailsViewManager.showVoteStartDetail(contentView, voteEntity);
        } else if (data instanceof VotePubEntity) {
            mOTMVoteDetailsViewManager.showVoteStopDetail(contentView, (VotePubEntity) data);
        }

    }

    @Override
    public void succuess(String voteId) {
        int position = refreshVoted(voteId);
        if (position >= 0) {
            notifyItemChanged(position);
        }
    }

    /**
     * @param
     * @return
     */
    private int refreshVoted(String voteId) {
        int index = -1;
        if (otmVoteListAdapter == null) {
            return index;
        }
        List<Object> list = otmVoteListAdapter.getDataList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof VoteEntity) {
                VoteEntity voteEntity = (VoteEntity) list.get(i);
                if (TextUtils.equals(voteId, voteEntity.getVid())) {
                    voteEntity.setVoted(1);
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    /**
     * 当有投票结束
     * <p>
     * 并且处于详情页
     * 且voteID 是一致 的情况 刷新详情页
     *
     * @param votePubEntity
     */
    public void voteStop(VotePubEntity votePubEntity) {
        if (contentView == null) {
            return;
        }
        if (contentView.getChildCount() > 1 && TextUtils.equals(voteId, votePubEntity.getVid())) {
            mOTMVoteDetailsViewManager.refreshView(votePubEntity.getVid());
        }
    }

    public boolean isShow() {
        if (getDialog() == null) {
            return false;
        }
        return getDialog().isShowing();
    }

    public void voteDel(VoteDelEntity voteDelEntity) {
        if (contentView == null || mOTMVoteDetailsViewManager == null) {
            return;
        }
        if (contentView.getChildCount() > 1 && TextUtils.equals(voteId, voteDelEntity.getVid())) {
            mOTMVoteDetailsViewManager.back();
        }
    }

    @Override
    public void onback() {
        if (voteRV != null) {
            voteRV.setVisibility(View.VISIBLE);
        }
    }
}
