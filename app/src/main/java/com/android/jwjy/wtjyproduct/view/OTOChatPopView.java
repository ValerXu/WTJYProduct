package com.android.jwjy.wtjyproduct.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.adapter.ChatEmptyViewAdapter;
import com.android.jwjy.wtjyproduct.adapter.OTOChatAdapter;
import com.android.jwjy.wtjyproduct.util.DimensionUtils;
import com.talkfun.sdk.module.ChatEntity;
import com.talkfun.widget.PopView;
import com.talkfun.widget.anni.HorizontalGravity;
import com.talkfun.widget.anni.VerticalGravity;

import java.lang.ref.WeakReference;

/**
 * Created by ccy on 2019/5/8/17:30
 */
public class OTOChatPopView {
    private PopView mPopView;
    private RecyclerView chatRV;
    private ChatEmptyViewAdapter mLiveRtcChatAdapter;
    private WeakReference<Context> contextWeakReference;

    public OTOChatPopView(Context context) {
        contextWeakReference = new WeakReference<>(context);
        View rootView = View.inflate(getContext(), R.layout.activity_live_one_to_one__right_chat_rv, null);
        int width = (int) getContext().getResources().getDimension(R.dimen.dp_270);
        int height = DimensionUtils.getScreenHeight(getContext()) - (int) context.getResources().getDimension(R.dimen.dp_40);
        mPopView = new PopView(getContext()).setContentView(rootView).setWidth(width).setHeight(height).setFocusable(true).
                setFocusAndOutsideEnable(true).setAnimationStyle(R.style.pop_enter_exit_anim).createPopup();

        chatRV = rootView.findViewById(R.id.chat_layout);
        chatRV.setLayoutManager(new LinearLayoutManager(getContext()));
        setAdapter();
    }

    private Context getContext() {
        return contextWeakReference.get();
    }

    private void setAdapter() {
        mLiveRtcChatAdapter = new ChatEmptyViewAdapter(getContext(), new OTOChatAdapter());
        chatRV.setAdapter(mLiveRtcChatAdapter);
        RecyclerView.ItemDecoration itemDecoration = new RecycleViewDivider.Build().orientation(LinearLayout.VERTICAL).
                context(getContext()).dividerColor(Color.parseColor(
                "#1A919DAE")).dividerHeight(getDimension(R.dimen.dp_1)).rightOffset(getDimension(R.dimen.dp_5)).leftOffset(getDimension(R.dimen.dp_5)).create();
        chatRV.addItemDecoration(itemDecoration);
    }

    public void receiveChatEntity(ChatEntity value) {
        mLiveRtcChatAdapter.appendList(value);
        chatRV.scrollToPosition(mLiveRtcChatAdapter.getItemCount() - 1);

    }

    public void show(View view) {
        if (mPopView == null) {
            return;
        }
        if (mPopView.isShowing()) {
            mPopView.dismiss();
            return;
        }
        mPopView.showAtAnchorView(view, VerticalGravity.ABOVE, HorizontalGravity.ALIGN_RIGHT);
    }

    public boolean isShowing() {
        return mPopView == null ? false : mPopView.isShowing();
    }

    private int getDimension(int id) {
        return (int) getContext().getResources().getDimension(id);
    }
}
