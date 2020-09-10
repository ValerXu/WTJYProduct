package com.android.jwjy.wtjyproduct.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.adapter.PlaybackChatAdapter;
import com.android.jwjy.wtjyproduct.consts.EventType;
import com.android.jwjy.wtjyproduct.entity.Event;
import com.android.jwjy.wtjyproduct.util.EventBusUtil;
import com.android.jwjy.wtjyproduct.util.ExpressionUtil;
import com.android.jwjy.wtjyproduct.util.StringUtils;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.data.PlaybackDataManage;
import com.talkfun.sdk.event.AutoScrollListener;
import com.talkfun.sdk.event.HtDispatchPlaybackMsgListener;
import com.talkfun.sdk.module.ChatEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PlaybackChatFragment extends PlaybackBaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        HtDispatchPlaybackMsgListener, AutoScrollListener {
    @BindView(R.id.chat_lv)
    ListView chatLv;
    @BindView(R.id.play_back_input)
    ViewGroup inputLayout;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipeRefreshLayout;

    private String tag;
    private PlaybackChatAdapter chatAdapter;
    private List<ChatEntity> chatMessageEntityList = new ArrayList<>();
    public PlaybackChatFragment() {

    }

    public static PlaybackChatFragment create(String tag) {
        PlaybackChatFragment cf = new PlaybackChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("chat", tag);
        cf.setArguments(bundle);
        return cf;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().getString("chat") != null) {
            tag = getArguments().getString("chat");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View layout = inflater.inflate(R.layout.playback_chat_fragment_layout, container, false);
        ButterKnife.bind(this, layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.blue, android.R.color.holo_red_light, android.R.color.holo_green_light,
                android.R.color.holo_orange_light);

        chatAdapter = new PlaybackChatAdapter(getActivity());
        chatAdapter.setItems(chatMessageEntityList);
        chatLv.setAdapter(chatAdapter);
        chatLv.setOnScrollListener(scrollListener);
        chatLv.setOnTouchListener(touchListener);

        inputLayout.setVisibility(View.GONE);
        PlaybackDataManage.getInstance().setChatListener(this);
        setChatList(PlaybackDataManage.getInstance().getChatList());
        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PlaybackDataManage.getInstance().setChatListener(null);
    }



    private void setChatList(List<ChatEntity> list) {
        chatMessageEntityList.clear();
        if (list != null)
            chatMessageEntityList.addAll(list);
        if (chatAdapter != null)
            chatAdapter.setItems(chatMessageEntityList);
    }

    @Override
    public void onRefresh() {
        mIsLoading = true;
        PlaybackDataManage.getInstance().loadDownMoreData(PlaybackDataManage.DataType.CHAT);
    }

    @Override
    public void getPlaybackMsgSuccess(int position) {
        if (isShow && chatAdapter != null) {
            setChatList(PlaybackDataManage.getInstance().getChatList());
            if (position < chatMessageEntityList.size()) {
                chatLv.setSelection(position);
            } else {
                chatLv.setSelection(chatMessageEntityList.size() - 1);
            }
        }
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        mIsLoading = false;
    }

    @Override
    public void getPlaybackMsgFail(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        mIsLoading = false;
    }

    public boolean isEqualCollection(List list1,List list2){
        if(list1 == null && list2 == null)
            return true;
        else if((list1 == null && list2 != null) || (list1 != null && list2 == null))
            return false;
        return list1.size() == list2.size() && list1.containsAll(list2);
    }
    int lastDisplayIndex = -1;
    @Override
    public void scrollToItem(final int pos) {
        if (isShow && chatAdapter != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    List<ChatEntity> list = PlaybackDataManage.getInstance().getChatList();
                    boolean dataEqual = isEqualCollection(chatMessageEntityList,list);
                    if(!dataEqual){
                        setChatList(list);
                    }

                    if (pos < chatMessageEntityList.size()) {
                        chatLv.setSelection(pos);
                    } else {
                        chatLv.setSelection(chatMessageEntityList.size() - 1);
                    }


                    if(chatMessageEntityList.size() <= 0)
                        return;
                    int index = pos;
                    if (index >= chatMessageEntityList.size()) {
                        index = chatMessageEntityList.size() - 1;
                    }

                    chatLv.setSelection(index);
                    if(dataEqual && index == lastDisplayIndex)
                        return;
                    ChatEntity chatEntity = null;
                    SpannableString spannableString = null;
                    int currentTime = 0;

                    int playTime = (int) HtSdk.getInstance().getVideoCurrentTime();

                    for(int i = index; i >= 0; i--){
                        chatEntity = chatMessageEntityList.get(i);
                        int tmpTime = StringUtils.getInt(chatEntity.getTime(),0);
                        if(i == index){
                            currentTime = tmpTime;
                            if(currentTime > playTime)
                                break;
                        }else if(currentTime != tmpTime)
                            break;
                        spannableString = ExpressionUtil.removeExpression(getActivity(), chatEntity.getMsg());
                        EventBusUtil.postEvent(new Event(EventType.ADDDANMAKU, spannableString));
                    }


                    lastDisplayIndex = index;


                }
            });
        }
    }

    @Override
    public void updateAdapter() {
        if (chatAdapter != null)
            chatAdapter.notifyDataSetChanged();
    }

    /**
     * 清空聊天消息
     */
    public void clearPlaybackChatMessage() {
        if (chatAdapter == null) return;
        chatAdapter.clearItems();
    }

    @Override
    void getMoreData() {
        if (chatLv.getLastVisiblePosition() + 1 == chatMessageEntityList.size()) {
            mIsLoading = true;
            PlaybackDataManage.getInstance().loadUpMordData(PlaybackDataManage.DataType.CHAT);
        }
    }

    @Override
    public void startAutoScroll() {
        PlaybackDataManage.getInstance().startAutoScroll(this, PlaybackDataManage.DataType.CHAT);
    }

    @Override
    void resetAdapterData() {
        setChatList(PlaybackDataManage.getInstance().getChatList());
    }

}
