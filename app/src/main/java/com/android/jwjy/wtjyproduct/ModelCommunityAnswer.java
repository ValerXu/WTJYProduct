package com.android.jwjy.wtjyproduct;

import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
//import android.icu.text.SimpleDateFormat;
import java.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import net.sqlcipher.Cursor;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.fragment.NewImagePagerDialogFragment;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Created by dayuer on 19/7/2.
 * 社区问答
 */
public class ModelCommunityAnswer extends Fragment{
    //课程问答
        private static ControlMainActivity mControlMainActivity;
    private static String mContext="xxxxxxxxxxxxx";
    private static final String TAG = "ModelCommunityAnswer";
    //要显示的页面
    static private int FragmentPage;
    private View mview,mCommunityAnswerView ,mCommunityAnswerSelectView ,mCommunityAnswerAddView ,mCommunityAnswerChooseSignView
            ,mCommunityAnswerDetailsView;
    private int height = 1344;
    private int width = 720;
    //弹出窗口（筛选条件）
    private PopupWindow popupWindow;
    //检索条件-默认全部  mCommunityAnswerSelectTemp 为临时存储，当点击确定时，才替换到mCommunityAnswerSelect
    private String mCommunityAnswerSelect = "-1";
    private String mCommunityAnswerSelectTemp = "-1";
    //添加问答-图片选择器
    private RecyclerView mRecyclerView;
    private ArrayList<ControllerPictureBean> mPictureBeansList;
    private ControllerPictureAdapter mPictureAdapter;
    private ArrayList<String> selPhotosPath = null;//选中的图片路径集合
    //当前问题的发布状态
    private boolean mIsPublish = true;
    //发布问题的时候是否选择了图片
    private boolean mQuestionPublishImage = false;
    //发布问题的时候是否写了标题
    private boolean mQuestionPublishTitle = false;
    //发布问题的时候是否写了内容
    private boolean mQuestionPublishContent = false;
    //发布问题的内容
    private String mQuestionPublishContentS = "";
    //发布问题的标题
    private String mQuestionPublishTitleS = "";
    //添加问题标签(存储选中的标签id)。最多添加三个
    private List<String> mCommunityAnswerChooseSignList = new ArrayList<>();
    //草稿箱提示框的dialog
    private ControllerCenterDialog mMyDialog;

    //社区问答列表分页查询
    private int mCommunityAnswerCurrentPage = 0;
    private int mCommunityAnswerPageCount = 10;
    private int mCommunityAnswerSum = 0; //社区问答总数

    //社区问答详情分页查询
    private int mCommunityAnswerDetailsCurrentPage = 0;
    private int mCommunityAnswerDetailsPageCount = 10;
    private int mCommunityAnswerDetailsSum = 0; //社区问答详情总数

    private String mKey = "";//关键字搜索

    private View mPopupWindowView = null;

    //评论
    private ControllerCustomDialog mCustomDialog = null;
    private DialogInterface.OnKeyListener keylistener = (dialog, keyCode, event) -> {
        Log.i("TAG", "键盘code---" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dialog.dismiss();
            return false;
        } else if(keyCode == KeyEvent.KEYCODE_DEL){//删除键
            return false;
        }else{
            return true;
        }
    };
    //社区问答列表刷新控件
    private SmartRefreshLayout smart_model_communityanswer;
    private LinearLayout communityanswer_datails_linearlayout;
    //社区问答详情刷新控件
    private SmartRefreshLayout mSmart_model_communityanswer_detalis;
    private LinearLayout.LayoutParams ll;
    //添加问答标题输入框控件
    private EditText communityanswer_add_layout_contentetitledittext;
    //添加问答内容输入框控件
    private EditText communityanswer_add_layout_contentedittext;

    public  static Fragment newInstance(ControlMainActivity content, String context, int iFragmentPage){
        mContext = context;
        mControlMainActivity = content;
        ModelCommunityAnswer myFragment = new ModelCommunityAnswer();
        FragmentPage = iFragmentPage;
        return  myFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mview = inflater.inflate(FragmentPage,container,false);
        DisplayMetrics dm = mControlMainActivity.getResources().getDisplayMetrics(); //获取屏幕分辨率
        height = dm.heightPixels;
        width = dm.widthPixels;
        CommunityAnswerMainShow();
        return mview;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    //社区问答主界面显示
    public void CommunityAnswerMainShow() {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        LinearLayout communityanswer_layout_main = mview.findViewById(R.id.communityanswer_layout_main);
        if (mCommunityAnswerView == null){
            mCommunityAnswerView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_communityanswer, null);
            //下滑刷新处理
           //Smart_model_communityanswer
            smart_model_communityanswer = mCommunityAnswerView.findViewById(R.id.Smart_model_communityanswer);
            smart_model_communityanswer.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    //先判断是否已获取了全部数据
                    if (mCommunityAnswerSum <= mCommunityAnswerCurrentPage * mCommunityAnswerPageCount){
                        LinearLayout communityanswer_end = mCommunityAnswerView.findViewById(R.id.communityanswer_end);
                        communityanswer_end.setVisibility(View.VISIBLE);
                        return;
                    }
                    getCommunityDataMore();
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    //社区问答列表 网络请求
                    getCommunityData();
                }
            });
            //关键词搜索监听
            ImageView communityanswer_searchimage = mCommunityAnswerView.findViewById(R.id.communityanswer_searchimage);
            communityanswer_searchimage.setOnClickListener(v->{
                CommunityAnswerSelectShow();
            });
            //搜索框
            TextView communityanswer_hint = mCommunityAnswerView.findViewById(R.id.communityanswer_hint);
            communityanswer_hint.setOnClickListener(v->{
                CommunityAnswerSelectShow();
            });
            //条件查询
            ImageView communityanswer_searchcondition = mCommunityAnswerView.findViewById(R.id.communityanswer_searchcondition);
            communityanswer_searchcondition.setOnClickListener(v->{
                initPopupWindow();
            });
            //点击添加问答
            ImageView communityanswer_add = mCommunityAnswerView.findViewById(R.id.communityanswer_add);
            communityanswer_add.setOnClickListener(v->{
                CommunityAnswerAddInit(true);
            });
        }
        communityanswer_layout_main.addView(mCommunityAnswerView);
        if (!mKey.equals("")) {
            TextView communityanswer_hint = mCommunityAnswerView.findViewById(R.id.communityanswer_hint);
            communityanswer_hint.setText(mKey);
        } else {
            TextView communityanswer_hint = mCommunityAnswerView.findViewById(R.id.communityanswer_hint);
            communityanswer_hint.setText("输入关键词搜索");
        }
        LinearLayout communityanswer_linearlayout = mCommunityAnswerView.findViewById(R.id.communityanswer_linearlayout);
        communityanswer_linearlayout.removeAllViews();
        //社区问答列表 网络请求
        getCommunityData();
    }
    //添加----社区问答的列表
    public void CommunityAnswerAddInit(boolean m_isInit){
        if (mview == null) {
            return;
        }
        mControlMainActivity.Page_onCommunityAnswerAdd();
        HideAllLayout();
        LinearLayout communityanswer_layout_main = mview.findViewById(R.id.communityanswer_layout_main);
        if (mCommunityAnswerAddView == null) {
            mCommunityAnswerAddView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_communityanswer_add, null);
        }
        RecyclerView communityanswer_add_layout_image = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_image);
        communityanswer_add_layout_image.setLayoutManager(new GridLayoutManager(mControlMainActivity, 3));
        selPhotosPath = new ArrayList<>();
        //=============图片九宫格=========================//
        mPictureAdapter = null;
        //图片集合
        mPictureBeansList = new ArrayList<>();
        //设置布局管理器
        mRecyclerView = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_image);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mControlMainActivity, 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        if(mPictureAdapter == null){
            //设置适配器
            mPictureAdapter = new ControllerPictureAdapter(mControlMainActivity, mPictureBeansList);
            mRecyclerView.setAdapter(mPictureAdapter);
            //添加分割线
            //设置添加删除动画
            //调用ListView的setSelected(!ListView.isSelected())方法，这样就能及时刷新布局
            mRecyclerView.setSelected(true);
        }else{
            mPictureAdapter.notifyDataSetChanged();
        }
        //图片九宫格点击事件
        mPictureAdapter.setOnItemClickLitener(new ControllerPictureAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View v,int position) {
                //打开自定义的图片预览对话框
                List<String> photos = mPictureAdapter.getAllPhotoPaths();
                int[] screenLocation = new int[2];
                v.getLocationOnScreen(screenLocation);
                NewImagePagerDialogFragment newImagePagerDialogFragment = NewImagePagerDialogFragment.getInstance(mControlMainActivity,photos,position,screenLocation, v.getWidth(),
                        v.getHeight(),false);
                newImagePagerDialogFragment.show(mControlMainActivity.getSupportFragmentManager(),"preview img");
            }

            @Override
            public void onItemAddClick() {
                if (selPhotosPath.size() >= 9){
                    Toast.makeText(mControlMainActivity,"最多可以选择9张照片",Toast.LENGTH_SHORT).show();
                    return;
                }
                PhotoPicker.builder()
                        .setPhotoCount(mPictureAdapter.MAX)
                        .setGridColumnCount(3)
//                        .setSelected(selPhotosPath)
                        .start(mControlMainActivity, ControllerGlobals.CHOOSE_PIC_REQUEST_CODE);
            }

            @Override
            public void onItemDeleteClick(View view, int position){
                mPictureBeansList.remove(position);
                mPictureAdapter.notifyDataSetChanged();
                if (mPictureBeansList.size() == 0) {
                    mQuestionPublishImage = false;
                }
                if (mQuestionPublishImage || mQuestionPublishTitle || mQuestionPublishContent) {
                    TextView communityanswer_add_layout_next_button1 = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_next_button1);
                    communityanswer_add_layout_next_button1.setTextColor(mCommunityAnswerView.getResources().getColor(R.color.blackff333333));
                } else {
                    TextView communityanswer_add_layout_next_button1 = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_next_button1);
                    communityanswer_add_layout_next_button1.setTextColor(mCommunityAnswerView.getResources().getColor(R.color.black999999));
                }
            }
        });

        //下一步
        TextView communityanswer_add_layout_next_button1 = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_next_button1);
        communityanswer_add_layout_next_button1.setClickable(true);
        communityanswer_add_layout_next_button1.setOnClickListener(v->{
            if (mQuestionPublishImage || mQuestionPublishTitle || mQuestionPublishContent) {
                if (!mQuestionPublishTitle) {
                    //弹出提示，必须添加问题标题
                    Toast.makeText(mControlMainActivity, "您还没有输入问题标题", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!mQuestionPublishContent) {
                    //弹出提示，必须有问题内容
                    Toast.makeText(mControlMainActivity, "您还没有输入问题", Toast.LENGTH_LONG).show();
                    return;
                }
                communityanswer_add_layout_contentedittext = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_contentedittext);
                if (communityanswer_add_layout_contentedittext.getText().toString().length() < 10) {
                    //弹出提示，内容不允许少于10个字
                    Toast.makeText(mControlMainActivity, "内容不允许少于10个字", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            if (!mIsPublish){
                Toast.makeText(mControlMainActivity,"正在发布问题，请稍后！",Toast.LENGTH_LONG).show();
                return;
            }
            //点击下一步选择标签
            CommunityAnswerChooseSign();
        });
        EditText communityanswer_add_layout_contentedittext = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_contentedittext);
//        //设置标题输入框最先获取焦点 弹出输入法
//        communityanswer_add_layout_contentetitledittext.setFocusable(true);
//        communityanswer_add_layout_contentetitledittext.setFocusableInTouchMode(true);
//        communityanswer_add_layout_contentetitledittext.requestFocus();
//        communityanswer_add_layout_contentetitledittext.setSelection(communityanswer_add_layout_contentetitledittext.getText().toString().length());
        communityanswer_add_layout_contentedittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //监听是否有输入内容
                if (!s.toString().equals("")){
                    mQuestionPublishContent = true;
                    mQuestionPublishContentS = s.toString();
                } else {
                    mQuestionPublishContent = false;
                    mQuestionPublishContentS = "";
                }
                //添加问答时，图片、标题或内容有一项不为空，就将下一步按钮置为可点击状态
                if (mQuestionPublishImage || mQuestionPublishTitle || mQuestionPublishContent) {
                    TextView communityanswer_add_layout_next_button1 = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_next_button1);
                    communityanswer_add_layout_next_button1.setTextColor(mCommunityAnswerView.getResources().getColor(R.color.blackff333333));
                } else {
                    TextView communityanswer_add_layout_next_button1 = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_next_button1);
                    communityanswer_add_layout_next_button1.setTextColor(mCommunityAnswerView.getResources().getColor(R.color.black999999));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //发布信息的标题
        communityanswer_add_layout_contentetitledittext = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_contentetitledittext);
        communityanswer_add_layout_contentetitledittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //监听是否有输入内容
                if (!s.toString().equals("")){
                    mQuestionPublishTitle = true;
                    mQuestionPublishTitleS = s.toString();
                } else {
                    mQuestionPublishTitle = false;
                    mQuestionPublishTitleS = "";
                }
                //添加问答时，图片、标题或内容有一项不为空，就将下一步按钮置为可点击状态
                if (mQuestionPublishImage || mQuestionPublishTitle || mQuestionPublishContent) {
                    TextView communityanswer_add_layout_next_button1 = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_next_button1);
                    communityanswer_add_layout_next_button1.setTextColor(mCommunityAnswerView.getResources().getColor(R.color.blackff333333));
                } else {
                    TextView communityanswer_add_layout_next_button1 = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_next_button1);
                    communityanswer_add_layout_next_button1.setTextColor(mCommunityAnswerView.getResources().getColor(R.color.black999999));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        communityanswer_layout_main.addView(mCommunityAnswerAddView);
        if (m_isInit) { //如果是从社区问答主页跳转进来的 需要将所有东西初始化一下
            mQuestionPublishTitle = false;
            mQuestionPublishTitleS = "";
            mQuestionPublishContent = false;
            mQuestionPublishContentS = "";
            mQuestionPublishImage = false;
            mCommunityAnswerChooseSignList.clear();
            if (mPictureBeansList != null) {
                mPictureBeansList.clear();
            }
            if (selPhotosPath != null) {
                selPhotosPath.clear();
                mPictureBeansList.clear();
            }
            //先查询数据库中草稿箱中是否有未完成的问答
            Cursor cursor = ModelSearchRecordSQLiteOpenHelper.getReadableDatabase(mControlMainActivity).rawQuery(
                    "select * from communityanswerdraftbox ", null);
            while (cursor.moveToNext()) {
                int titleIndex = cursor.getColumnIndex("title");
                int contentIndex = cursor.getColumnIndex("content");
                int photospathIndex = cursor.getColumnIndex("photospath");
                int signIndex = cursor.getColumnIndex("sign");
                String title = cursor.getString(titleIndex);
                String content = cursor.getString(contentIndex);
                String photospath = cursor.getString(photospathIndex);
                String sign = cursor.getString(signIndex);
                if (title != null) {
                    communityanswer_add_layout_contentetitledittext.setText(title);
                    if (!title.equals("")) {
                        mQuestionPublishTitle = true;
                        mQuestionPublishTitleS = title;
                    }
                }
                if (content != null) {
                    communityanswer_add_layout_contentedittext.setText(content);
                    if (!content.equals("")) {
                        mQuestionPublishContent = true;
                        mQuestionPublishContentS = content;
                    }
                }
                if (photospath != null) {
                    String photospathS[] = photospath.split(";");
                    for (int i = 0; i < photospathS.length; i++) {
                        if (photospathS[i].equals("")) {
                            continue;
                        }
                        selPhotosPath.add(photospathS[i]);
                    }
                    for (String path : selPhotosPath) {
                        ControllerPictureBean pictureBean = new ControllerPictureBean();
                        pictureBean.setPicPath(path);
                        pictureBean.setPicName(ControllerGlobals.getFileName(path));
                        //去掉总数目的限制，这里通过增大MAX的数字来实现
                        if (mPictureBeansList.size() < mPictureAdapter.MAX) {
                            mPictureBeansList.add(pictureBean);
                        } else {
                            Toast.makeText(mControlMainActivity, "最多可以选择" + mPictureAdapter.MAX + "张图片", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    mPictureAdapter.notifyDataSetChanged();
                }
                if (sign != null) {
                    String signS[] = sign.split(";");
                    for (int i = 0; i < signS.length; i++) {
                        if (signS[i].equals("")) {
                            continue;
                        }
                        mCommunityAnswerChooseSignList.add(signS[i]);
                    }
                }
                break;
            }
            cursor.close();
        }
    }
    //社区问答添加图片
    public void CommunityAnswerPictureAdd(Intent data){
        //添加图片，发布按钮改为蓝色
        mQuestionPublishImage = true;
        if (mQuestionPublishImage || mQuestionPublishTitle || mQuestionPublishContent) {
            TextView communityanswer_add_layout_next_button1 = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_next_button1);
            communityanswer_add_layout_next_button1.setTextColor(mCommunityAnswerView.getResources().getColor(R.color.blackff333333));
        } else {
            TextView communityanswer_add_layout_next_button1 = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_next_button1);
            communityanswer_add_layout_next_button1.setTextColor(mCommunityAnswerView.getResources().getColor(R.color.black999999));
        }
        if (data != null) {
            selPhotosPath = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
        }
        if (selPhotosPath != null) {

            //下面的代码主要用于这样一个场景，就是注释了.setSelected(selPhotosPath)之后，还想要保证选择的图片不重复
					/*for(String path : selPhotosPath){
						Log.w(TAG,"path="+path);///storage/emulated/0/tempHxzk/IMG_1498034535796.jpg
						boolean existThisPic = false;
						for(int i=0;i<mPictureBeansList.size();i++){
							if(path.equals(mPictureBeansList.get(i).getPicPath())){
								//如果新选择的图片集合中存在之前选中的图片，那么跳过去
								existThisPic = true;
								break;
							}
						}
						if(! existThisPic){
							PictureBean pictureBean = new PictureBean();
							pictureBean.setPicPath(path);
							pictureBean.setPicName(getFileName(path));
							//去掉总数目的限制，这里通过增大MAX的数字来实现
							if (mPictureBeansList.size() < mPictureAdapter.MAX) {
								mPictureBeansList.add(pictureBean);
							} else {
								Toast.makeText(MainActivity.this, "最多可以选择" + mPictureAdapter.MAX + "张图片", Toast.LENGTH_SHORT).show();
								break;
							}
						}
					}*/

            //是常规操作，和上面的代码不可共存
            for (String path : selPhotosPath) {
                ControllerPictureBean pictureBean = new ControllerPictureBean();
                pictureBean.setPicPath(path);
                pictureBean.setPicName(ControllerGlobals.getFileName(path));
                //去掉总数目的限制，这里通过增大MAX的数字来实现
                if (mPictureBeansList.size() < mPictureAdapter.MAX) {
                    mPictureBeansList.add(pictureBean);
                } else {
                    Toast.makeText(mControlMainActivity, "最多可以选择" + mPictureAdapter.MAX + "张图片", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            mPictureAdapter.notifyDataSetChanged();
        }
    }
    //选择标签----选择标签
    private void CommunityAnswerChooseSign(){
        if (mview == null) {
            return;
        }
        mControlMainActivity.Page_onCommunityAnswerChooseSign();
        HideAllLayout();
        LinearLayout communityanswer_layout_main = mview.findViewById(R.id.communityanswer_layout_main);
        if (mCommunityAnswerChooseSignView == null) {
            mCommunityAnswerChooseSignView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_communityanswer_choosesign, null);
        }
        communityanswer_layout_main.addView(mCommunityAnswerChooseSignView);
        //                   发表按钮
        TextView communityanswer_choosesign_layout_commit_button1 = mCommunityAnswerChooseSignView.findViewById(R.id.communityanswer_choosesign_layout_commit_button1);
        communityanswer_choosesign_layout_commit_button1.setOnClickListener(v->{
            //点击发表问答，先判断是否有选择标签，如果没有选择标签，不做处理
            if (mCommunityAnswerChooseSignList.size() != 0){
                mControlMainActivity.setmState("发布问答");
                mIsPublish = false;
                //communityanswer_add_layout_contentetitledittext    communityanswer_add_layout_contentedittext
                String name = communityanswer_add_layout_contentetitledittext.getText().toString();
                String context = communityanswer_add_layout_contentetitledittext.getText().toString();
                //点击发布问题
                if (selPhotosPath.size() == 0 ){ //如果没有图片直接发送内容
                    mControlMainActivity.setmState("");
                    mIsPublish = true;
                    //不要图片   加载网络请求
                } else if (selPhotosPath != null) {//如果有图片先上传图片在加载网络请求
                    upLoadAnswerImage(name,context);
                }
            }
        });
        //选中的标签个数
        TextView communityanswer_choosesign_choosecount = mCommunityAnswerChooseSignView.findViewById(R.id.communityanswer_choosesign_choosecount);
         //标签赋值和点击标签的状态变化
        getCommunityQuerytagsBeanData_publish();      //社区问答标签
        communityanswer_choosesign_choosecount.setText(String.valueOf(mCommunityAnswerChooseSignList.size()));
    }

    //文件的搜索
    private void CommunityAnswerSelectShow(){
        if (mview == null) {
            return;
        }
        HideAllLayout();
        mControlMainActivity.Page_onCommunityAnswerSearch();
        LinearLayout communityanswer_layout_main = mview.findViewById(R.id.communityanswer_layout_main);
        if (mCommunityAnswerSelectView == null) {
            mCommunityAnswerSelectView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_communityanswer_select, null);
            ModelSearchView communityanswer_search_view = mCommunityAnswerSelectView.findViewById(R.id.communityanswer_search_view);
            communityanswer_search_view.init("communityanswersearchrecords");
            // 4. 设置点击搜索按键后的操作（通过回调接口）
            // 参数 = 搜索框输入的内容,,,,,,,,.
            communityanswer_search_view.setOnClickSearch(string ->{
               // System.out.println("我收到了" + string);
                Toast.makeText(mControlMainActivity, "我查询参数是"+string, Toast.LENGTH_SHORT).show();
                mKey = string ;
                mControlMainActivity.Page_CommunityAnswer();
            });
            // 5. 设置点击返回按键后的操作（通过回调接口）
            communityanswer_search_view.setOnClickBack(()->{
                mControlMainActivity.Page_CommunityAnswer();
            });
        }
        communityanswer_layout_main.addView(mCommunityAnswerSelectView);
    }
    //社区子条目详情
    public void CommunityAnswerDetailsShow(Integer questions_id) {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        mControlMainActivity.Page_onCommunityAnswerDetails();
        //详情的评论
        LinearLayout communityanswer_layout_main = mview.findViewById(R.id.communityanswer_layout_main);
        if (mCommunityAnswerDetailsView == null) {
            mCommunityAnswerDetailsView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_communityanswer_details, null);
            //下滑刷新处理
            //Smart_model_communityanswer_detalis
            mSmart_model_communityanswer_detalis = mCommunityAnswerDetailsView.findViewById(R.id.Smart_model_communityanswer_detalis);
            mSmart_model_communityanswer_detalis.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    if (mCommunityAnswerDetailsSum <= mCommunityAnswerDetailsCurrentPage * mCommunityAnswerDetailsPageCount){
                        LinearLayout communityanswer_datails_end = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_end);
                        communityanswer_datails_end.setVisibility(View.VISIBLE);
                        return;
                    }
                    getCommunityDetilsBeanDataMore(questions_id);
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    getCommunityDetilsBeanData(questions_id);
                }
            });
        }
        //评论的主评论的标题赋值
        communityanswer_layout_main.addView(mCommunityAnswerDetailsView);
        getCommunityDetilsBeanData(questions_id);
    }

    //隐藏所有图层
    private void HideAllLayout(){
        LinearLayout communityanswer_layout_main = mview.findViewById(R.id.communityanswer_layout_main);
        communityanswer_layout_main.removeAllViews();
    }

    //添加问答的返回
    public void CommunityAnswerAddReturn(){
        String title = "";
        String content = "";
        String photosPath = "";
        String sign = "";
        if (mCommunityAnswerAddView != null){ //必须放在界面跳转之前拿到数据
            EditText communityanswer_add_layout_contentetitledittext = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_contentetitledittext);
            title = communityanswer_add_layout_contentetitledittext.getText().toString();
            EditText communityanswer_add_layout_contentedittext = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_contentedittext);
            content = communityanswer_add_layout_contentedittext.getText().toString();
        }
        CommunityAnswerMainShow();
        //如果有编辑内容的话
        if (mQuestionPublishTitle || mQuestionPublishContent || mQuestionPublishImage){
            //弹出提示，已存入草稿箱
            View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.dialog_sure, null);
            mMyDialog = new ControllerCenterDialog(mControlMainActivity, 0, 0, view, R.style.DialogTheme);
            mMyDialog.setCancelable(true);
            mMyDialog.show();
            TextView tip = view.findViewById(R.id.tip);
            tip.setText("已存入草稿箱");
            TextView dialog_content = view.findViewById(R.id.dialog_content);
            dialog_content.setText("点击提问按钮，可再次编辑");
            TextView button_sure = view.findViewById(R.id.button_sure);
            button_sure.setText("知道了");
            button_sure.setTextColor(view.getResources().getColor(R.color.blue649cf0));
            button_sure.setOnClickListener(View->{
                mMyDialog.cancel();
            });
            //获取添加图片的路径  ；分割
            for (int i = 0; i < mPictureBeansList.size() ; i ++){
                if (i == mPictureBeansList.size() - 1){
                    photosPath = photosPath + mPictureBeansList.get(i).getPicPath();
                } else {
                    photosPath = photosPath + mPictureBeansList.get(i).getPicPath() + ";";
                }
            }
            //获取标签  ；分割
            for (int i = 0; i < mCommunityAnswerChooseSignList.size() ; i ++){
                if (i == mCommunityAnswerChooseSignList.size() - 1){
                    sign = sign + mCommunityAnswerChooseSignList.get(i);
                } else {
                    sign = sign + mCommunityAnswerChooseSignList.get(i) + ";";
                }
            }
            //将数据存储到本地数据库草稿箱  selPhotosPath:图片路径集合；content 内容；title ：标题  mCommunityAnswerChooseSignList：标签
            ModelSearchRecordSQLiteOpenHelper.getWritableDatabase(mControlMainActivity).
                    execSQL("delete from communityanswerdraftbox");
            ModelSearchRecordSQLiteOpenHelper.getWritableDatabase(mControlMainActivity).
                    execSQL("insert into communityanswerdraftbox(title,content,photospath,sign) values('" + title + "','" + content + "','" + photosPath + "','" + sign + "')");
        }
    }
    /**
   * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
   *
   */
    class popupDismissListener implements PopupWindow.OnDismissListener{
        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }
    }

    //初始化弹出框（选择标签筛选）
    protected void initPopupWindow() {
        if (mPopupWindowView == null) {
            mPopupWindowView = mControlMainActivity.getLayoutInflater().inflate(R.layout.model_communityanswer_selectpop, null);
        }
        int height1 = (int) (getScreenHeight() - mview.getResources().getDimension(R.dimen.dp45) - getStateBar());
        //内容，高度，宽度
        popupWindow = new PopupWindow(mPopupWindowView, (int) mview.getResources().getDimension(R.dimen.dp_280), height1, true);
        //动画效果
        popupWindow.setAnimationStyle(R.style.AnimationRightFade);
        //菜单背景色
        ColorDrawable dw = new ColorDrawable(0xffffffff);
        popupWindow.setBackgroundDrawable(dw);
        popupWindow.showAtLocation(mControlMainActivity.getLayoutInflater().inflate(R.layout.activity_main, null), Gravity.RIGHT, 0, 500);
        popupWindow.setBackgroundDrawable(null);
        //设置背景半透明
        backgroundAlpha(0.9f);
        //关闭事件
        popupWindow.setOnDismissListener(new popupDismissListener());
        mPopupWindowView.setOnTouchListener((v, event) -> {
            // 这里如果返回true的话，touch事件将被拦截
            // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            return false;
        });
        //查询标签
        getCommunityQuerytagsBeanData();
        TextView communityanswer_select_buttonsure = mPopupWindowView.findViewById(R.id.communityanswer_select_buttonsure);
        communityanswer_select_buttonsure.setOnClickListener(v->{
            mCommunityAnswerSelect = mCommunityAnswerSelectTemp;
            //请求网络数据关闭页面
            popupWindow.dismiss();
            mKey = "" ;
            getCommunityData();
        });
        //点击重置
        TextView communityanswer_select_buttonreset = mPopupWindowView.findViewById(R.id.communityanswer_select_buttonreset);
        communityanswer_select_buttonreset.setOnClickListener(v->{
            ControllerWarpLinearLayout communityanswer_select_warpLinearLayout = mPopupWindowView.findViewById(R.id.communityanswer_select_warpLinearLayout);
            //将其他置为未选中
            int childcount = communityanswer_select_warpLinearLayout.getChildCount();
            for (int i = 0; i < childcount ; i ++){
                View childView = communityanswer_select_warpLinearLayout.getChildAt(i);
                if (childView == null){
                    continue;
                }
                TextView communityanswer_selectpop_child_signname1 = childView.findViewById(R.id.communityanswer_selectpop_child_signname);
                int padding = (int) childView.getResources().getDimension(R.dimen.dp5);
                if (communityanswer_selectpop_child_signname1.getHint().toString().equals("-1")){
                    communityanswer_selectpop_child_signname1.setBackground(childView.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                    communityanswer_selectpop_child_signname1.setTextColor(childView.getResources().getColor(R.color.white));
                    communityanswer_selectpop_child_signname1.setPadding(padding,padding,padding,padding);
                } else if (communityanswer_selectpop_child_signname1.getHint().toString().equals(mCommunityAnswerSelectTemp)){ // 如果上个找到上一个选中的id，将其置为未选状态
                    communityanswer_selectpop_child_signname1.setBackground(childView.getResources().getDrawable(R.drawable.textview_style_rect));
                    communityanswer_selectpop_child_signname1.setTextColor(childView.getResources().getColor(R.color.grayff999999));
                    communityanswer_selectpop_child_signname1.setPadding(padding,padding,padding,padding);
                }
            }
            mCommunityAnswerSelectTemp = "-1";
            mCommunityAnswerSelect = "-1";
        });
    }
    /**
   * 设置添加屏幕的背景透明度
   * @param bgAlpha
   */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = mControlMainActivity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        mControlMainActivity.getWindow().setAttributes(lp);
    }

    //获取屏幕高度 不包含虚拟按键
    public static int getScreenHeight() {
        DisplayMetrics dm = mControlMainActivity.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    //获取状态栏高度
    private int getStateBar(){
        int result = 0;
        int resourceId = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = this.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    //社区问答—发布
    private void getCommunityissue(){
        if (mControlMainActivity.mStuId.equals("")){
            Toast.makeText(mControlMainActivity,"请先登录再发表问题",Toast.LENGTH_SHORT).show();
            mIsPublish = true;
            mControlMainActivity.setmState("");
            LoadingDialog.getInstance(mControlMainActivity).dismiss();
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, String> paramsMap = new HashMap<>();
        String subject_id = ";";
        for (int i = 0; i < mCommunityAnswerChooseSignList.size() ; i ++){
            subject_id = subject_id + mCommunityAnswerChooseSignList.get(i) + ";";
        }
        paramsMap.put("subject_id",subject_id);
        paramsMap.put("publisher", mControlMainActivity.mStuId);//社区问答的参数
        paramsMap.put("content",mQuestionPublishContentS);
        String questionPublishImageS = "";
        if (selPhotosPath != null) {
            for (int i = 0; i < selPhotosPath.size(); i++) {
                questionPublishImageS = questionPublishImageS + selPhotosPath.get(i) + ";";
            }
        }
        paramsMap.put("picture",questionPublishImageS);
        paramsMap.put("title",mQuestionPublishTitleS);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryMyCommunityissue(body)
                .enqueue(new Callback<ModelObservableInterface.BaseBean>() {
                    @Override
                    public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                        ModelObservableInterface.BaseBean baseBean = response.body();
                        if (baseBean == null){
                            Toast.makeText(mControlMainActivity,"发表问题失败",Toast.LENGTH_SHORT).show();
                            mIsPublish = true;
                            mControlMainActivity.setmState("");
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                            mCustomDialog.dismiss();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (baseBean.getErrorCode() != 200){
                            Toast.makeText(mControlMainActivity,"发表问题失败",Toast.LENGTH_SHORT).show();
                            mIsPublish = true;
                            mControlMainActivity.setmState("");
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        //发表，清空草稿箱中的文字，并返回到问答首页
                        ModelSearchRecordSQLiteOpenHelper.getWritableDatabase(mControlMainActivity).execSQL("delete from communityanswerdraftbox");
                        mCommunityAnswerAddView = null;
                        mControlMainActivity.Page_CommunityAnswer();
                        mIsPublish = true;
                        selPhotosPath.clear();
                        mControlMainActivity.setmState("");
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage());
                        Toast.makeText(mControlMainActivity,"发表问题失败",Toast.LENGTH_SHORT).show();
                        mIsPublish = true;
                        mControlMainActivity.setmState("");
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                });
        }

    //社区问答的列表
    private void getCommunityData(){
        LoadingDialog.getInstance(mControlMainActivity).show();
        LinearLayout communityanswer_linearlayout = mCommunityAnswerView.findViewById(R.id.communityanswer_linearlayout);
        communityanswer_linearlayout.removeAllViews();
        LinearLayout communityanswer_end = mCommunityAnswerView.findViewById(R.id.communityanswer_end);
        communityanswer_end.setVisibility(View.INVISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        mCommunityAnswerCurrentPage = 1;
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        if (mCommunityAnswerSelect != null) {
            if (!mCommunityAnswerSelect.equals("-1")) {
                paramsMap.put("subject_id", Integer.valueOf(mCommunityAnswerSelect));//学生id
            }
        }
        paramsMap.put("pageNum", mCommunityAnswerCurrentPage);//第几页
        paramsMap.put("pageSize",mCommunityAnswerPageCount);//每页几条
        paramsMap.put("course_type",1);
        String strEntity = gson.toJson(paramsMap);
        if (mKey != null) {
            if (!mKey.equals("")) {
                HashMap<String, String> paramsMap1 = new HashMap<>();
                paramsMap1.put("content", mKey);
                String strEntity1 = gson.toJson(paramsMap1);
                strEntity1 = strEntity1.replace("{", "");
                strEntity = strEntity.replace("}", "," + strEntity1);
            }
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryAllCoursePackageCommunity(body)


                .enqueue(new Callback<CommunityBean>() {
                    @Override
                    public void onResponse(Call<CommunityBean> call, Response<CommunityBean> response) {
                        if (response.body() == null){
                            if (smart_model_communityanswer != null){
                                smart_model_communityanswer.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        //获取网络数据
                        int code = response.body().code;
                        if (!HeaderInterceptor.IsErrorCode(code,response.body().msg)){
                            if (smart_model_communityanswer != null){
                                smart_model_communityanswer.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (code != 200){
                            if (smart_model_communityanswer != null){
                                smart_model_communityanswer.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        CommunityBean.CommunityDataBean communityDataBean = response.body().data;
                        if (communityDataBean == null){
                            if (smart_model_communityanswer != null){
                                smart_model_communityanswer.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mCommunityAnswerSum = communityDataBean.total;
                        if (communityDataBean.list == null){
                            if (smart_model_communityanswer != null){
                                smart_model_communityanswer.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        for (int i = 0; i < communityDataBean.list.size() ; i ++){
                            CommunityBean.ListDataBean listDataBean = communityDataBean.list.get(i);
                            if (listDataBean == null){
                                continue;
                            }
                            View model_communityanswer_child_view1 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_communityanswer_child, null);
                            communityanswer_linearlayout.addView(model_communityanswer_child_view1);
                            TextView course_question_child_name = model_communityanswer_child_view1.findViewById(R.id.course_question_child_name);
                            course_question_child_name.setText(listDataBean.nicename);
                            //浏览人数
                            TextView communityanswer_child_look = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_look);
                            communityanswer_child_look.setText("浏览人数" + listDataBean.visit_num);
                            //社区问答列表头像
                            ControllerCustomRoundAngleImageView communityanswer_child_headportrait = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_headportrait);
                            Glide.with(mControlMainActivity).load(listDataBean.head).listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    Log.d("Wain", "加载失败 errorMsg:" + (e != null ? e.getMessage() : "null"));
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    Log.d("Wain", "成功  Drawable Name:" + resource.getClass().getCanonicalName());
                                    return false;
                                }
                            }).error(mControlMainActivity.getResources().getDrawable(R.drawable.modelmy_myheaddefault)).into(communityanswer_child_headportrait);
                            //社区列表时间
                            Date date = null;
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            try {
                                date = df.parse(listDataBean.creation_time);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (date != null) {
                                SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
                                Date date1 = null;
                                try {
                                    date1 = df1.parse(date.toString());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (date1 != null) {
                                    SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
                                    listDataBean.creation_time = df2.format(date1).toString();
                                }
                            }
                            TextView communityanswer_child_time = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_time);
                            communityanswer_child_time.setText(listDataBean.creation_time);
                            //社区问答标题
                            TextView communityanswer_child_title = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_title);
                            communityanswer_child_title.setText(listDataBean.title);
                            //社区问答内容
                            TextView communityanswer_child_message = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_message);
                            new ModelHtmlUtils(mControlMainActivity, communityanswer_child_message).setHtmlWithPic(listDataBean.content);
                            //社区问答图片   communityanswer_child_imagelayout
                            GridLayout communityanswer_child_imagelayout = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_imagelayout);
                            communityanswer_child_imagelayout.removeAllViews();
                            //集合.size
                            if (listDataBean.picture != null) {
                                String pictures[] = listDataBean.picture.split(";");
                                if (pictures != null) {
                                    for (int num = 0; num < pictures.length; num ++) {
                                        if (pictures[num] == null){
                                            continue;
                                        }
                                        if (pictures[num].equals("")){
                                            continue;
                                        }
                                        View imageView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.controllercustomroundangleimageview_layout, null);
                                        ControllerCustomRoundAngleImageView CustomRoundAngleImageView = imageView.findViewById(R.id.CustomRoundAngleImageView);
                                        Glide.with(mControlMainActivity).load(pictures[num]).listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                Log.d("Warn", "加载失败 errorMsg:" + (e != null ? e.getMessage() : "null"));
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                Log.d("Warn", "成功  Drawable Name:" + resource.getClass().getCanonicalName());
                                                return false;
                                            }
                                        }).error(mControlMainActivity.getResources().getDrawable(R.drawable.modelcoursecover)).into(CustomRoundAngleImageView);
                                        communityanswer_child_imagelayout.addView(imageView);
                                    }
                                }
                            }
                            if (listDataBean.state == 0) { //普通
                                //去掉顶
                                ImageView communityanswer_child_top = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_top);
                                LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) communityanswer_child_top.getLayoutParams();
                                ll.width = 0;
                                ll.rightMargin = 0;
                                communityanswer_child_top.setLayoutParams(ll);
                                //去掉精
                                ImageView communityanswer_child_fine = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_fine);
                                ll = (LinearLayout.LayoutParams) communityanswer_child_fine.getLayoutParams();
                                ll.width = 0;
                                ll.rightMargin = 0;
                                communityanswer_child_fine.setLayoutParams(ll);
                            } else if (listDataBean.state == 1) {//加精
                                //去掉顶
                                ImageView communityanswer_child_top = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_top);
                                LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) communityanswer_child_top.getLayoutParams();
                                ll.width = 0;
                                ll.rightMargin = 0;
                                communityanswer_child_top.setLayoutParams(ll);
                            } else if (listDataBean.state == 2) {//置顶
                                //去掉精
                                ImageView communityanswer_child_fine = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_fine);
                                ll = (LinearLayout.LayoutParams) communityanswer_child_fine.getLayoutParams();
                                ll.width = 0;
                                ll.rightMargin = 0;
                                communityanswer_child_fine.setLayoutParams(ll);
                            }
                            //社区问答标签  至少一个 最多三个    communityanswer_child_sign1
                            if (listDataBean.subject_id != null) {
                                if (listDataBean.subject_id.size() == 0){
                                    LinearLayout communityanswer_child_sign = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_sign);
                                    RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) communityanswer_child_sign.getLayoutParams();
                                    rl.height = 0;
                                    rl.topMargin = 0;
                                    communityanswer_child_sign.setLayoutParams(rl);
                                } else {
                                    for (int num = 0; num < listDataBean.subject_id.size(); num++) {
                                        if (listDataBean.subject_id == null) {
                                            continue;
                                        }
                                        if (num == 0) {
                                            TextView communityanswer_child_sign1 = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_sign1);
                                            communityanswer_child_sign1.setText(listDataBean.subject_id.get(num));
                                            communityanswer_child_sign1.setVisibility(View.VISIBLE);
                                        } else if (num == 1) {
                                            TextView communityanswer_child_sign2 = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_sign2);
                                            communityanswer_child_sign2.setText(listDataBean.subject_id.get(num));
                                            communityanswer_child_sign2.setVisibility(View.VISIBLE);
                                        } else if (num == 2) {
                                            TextView communityanswer_child_sign3 = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_sign3);
                                            communityanswer_child_sign3.setText(listDataBean.subject_id.get(num));
                                            communityanswer_child_sign3.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            } else {
                                LinearLayout communityanswer_child_sign = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_sign);
                                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) communityanswer_child_sign.getLayoutParams();
                                rl.height = 0;
                                rl.topMargin = 0;
                                communityanswer_child_sign.setLayoutParams(rl);
                            }
                            if (listDataBean.huida != null){
                                //添加部分评论，此页最多显示三条
                                LinearLayout communityanswer_child_body = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_body);
                                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) communityanswer_child_body.getLayoutParams();
                                rl.topMargin = (int) model_communityanswer_child_view1.getResources().getDimension(R.dimen.dp15);
                                communityanswer_child_body.setLayoutParams(rl);
                                communityanswer_child_body.setPadding(0,0,0, (int) model_communityanswer_child_view1.getResources().getDimension(R.dimen.dp10));
                                TextView communityanswer_child_discusstext = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_discusstext);
                                communityanswer_child_discusstext.setText( listDataBean.huida_num + "");
                                //社区问答的条目评论显示
                                LinearLayout communityanswer_child_content = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_content);
                                for (int num = 0; num < listDataBean.huida.size() ; num ++){
                                    CommunityBean.DataBean dataBean = listDataBean.huida.get(num);
                                    if (dataBean == null ){
                                        continue;
                                    }
                                    if (num >= 3){//三条以上不显示
                                        //       判断当前的评论是否超过三条，如果评论超过三条显示查看全部
                                        LinearLayout communityanswer_child_lookalldiscuss = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_lookalldiscuss);
                                        ll = (LinearLayout.LayoutParams) communityanswer_child_lookalldiscuss.getLayoutParams();
                                        ll.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                        ll.topMargin = (int) model_communityanswer_child_view1.getResources().getDimension(R.dimen.dp10);
                                        communityanswer_child_lookalldiscuss.setLayoutParams(ll);

                                        //点击查看全部评论进入评论详情
                                        communityanswer_child_lookalldiscuss.setOnClickListener(v->{
                                            CommunityAnswerDetailsShow(listDataBean.questions_id);
                                        });
                                        break;
                                    }
                                    View respondView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_communityanswer_child1, null);
                                    communityanswer_child_content.addView(respondView);
                                    TextView communityanswer_child1_content = respondView.findViewById(R.id.communityanswer_child1_content);
                                    communityanswer_child1_content.setText(dataBean.content);
                                    TextView communityanswer_child1_name = respondView.findViewById(R.id.communityanswer_child1_name);
                                    TextView communityanswer_child1_name1 = respondView.findViewById(R.id.communityanswer_child1_name1);
                                    TextView communityanswer_child1_answer = respondView.findViewById(R.id.communityanswer_child1_answer);
                                    if (dataBean.q_nicename == null){
                                        communityanswer_child1_name.setText(dataBean.a_nicename);
                                        communityanswer_child1_name.setHint(dataBean.aID + "");
                                        LinearLayout.LayoutParams LL = (LinearLayout.LayoutParams) communityanswer_child1_name1.getLayoutParams();
                                        LL.width = 0;
                                        LL.leftMargin = 0;
                                        communityanswer_child1_name1.setLayoutParams(LL);
                                        LL = (LinearLayout.LayoutParams) communityanswer_child1_answer.getLayoutParams();
                                        LL.width = 0;
                                        LL.leftMargin = 0;
                                        communityanswer_child1_answer.setLayoutParams(LL);
                                    } else {
                                        communityanswer_child1_name.setText(dataBean.q_nicename);
                                        communityanswer_child1_name.setHint(dataBean.qID + "");
                                        communityanswer_child1_name1.setText(dataBean.a_nicename);
                                    }
                                    respondView.setOnClickListener(v->{
                                        //回复人的名字
                                        mCustomDialog = new ControllerCustomDialog(mControlMainActivity, R.style.customdialogstyle,"回复 " + communityanswer_child1_name.getText().toString(),false);
                                        mCustomDialog.setOnKeyListener(keylistener);
                                        mCustomDialog.show();
                                        mCustomDialog.setOnClickPublishOrImagelistener(new ControllerCustomDialog.OnClickPublishOrImage() {
                                            @Override
                                            public void publish(String content) {
                                                getCommunityDetilsreplyBeanData(String.valueOf(listDataBean.questions_id), communityanswer_child1_name.getHint().toString(),mControlMainActivity.mStuId,content,"");
                                            }

                                            @Override
                                            public void image() {

                                            }
                                        });
                                    });
                                }
                            }
                            //点击评论，对其进行回复
                            LinearLayout communityanswer_child_function_discuss = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_function_discuss);
                            communityanswer_child_function_discuss.setOnClickListener(v->{
                                mCustomDialog = new ControllerCustomDialog(mControlMainActivity, R.style.customdialogstyle,"评论",false);
                                mCustomDialog.setOnKeyListener(keylistener);
                                mCustomDialog.show();
                                mCustomDialog.setOnClickPublishOrImagelistener(new ControllerCustomDialog.OnClickPublishOrImage() {
                                    @Override
                                    public void publish(String content) {
                                        //获取回复的网络请求 for循环  判断当前的size判断当前的size是否大于3
                                        getCommunityDetilsreplyBeanData(String.valueOf(listDataBean.questions_id), String.valueOf(listDataBean.questions_id),mControlMainActivity.mStuId,content,"");
                                    }

                                    @Override
                                    public void image() {
                                        Toast.makeText(getActivity(), "我是公共的图片", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            });

                            //点击查看评论详情
                            LinearLayout communityanswer_child_content1 = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_content1);
                            communityanswer_child_content1.setOnClickListener(v->{
                                CommunityAnswerDetailsShow(listDataBean.questions_id);
                            });
                        }
                        if (smart_model_communityanswer != null){
                            smart_model_communityanswer.finishRefresh();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<CommunityBean> call, Throwable t) {
                        if (smart_model_communityanswer != null){
                            smart_model_communityanswer.finishRefresh();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                });
    }

    //社区问答的列表 - 上拉加载
    private void getCommunityDataMore(){
        LoadingDialog.getInstance(mControlMainActivity).show();
        LinearLayout communityanswer_linearlayout = mCommunityAnswerView.findViewById(R.id.communityanswer_linearlayout);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        mCommunityAnswerCurrentPage = mCommunityAnswerCurrentPage + 1;
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        if (mCommunityAnswerSelect != null) {
            if (!mCommunityAnswerSelect.equals("-1")) {
                paramsMap.put("subject_id", Integer.valueOf(mCommunityAnswerSelect));//学生id
            }
        }
        paramsMap.put("pageNum", mCommunityAnswerCurrentPage);//第几页
        paramsMap.put("pageSize",mCommunityAnswerPageCount);//每页几条
        paramsMap.put("course_type",1);
        String strEntity = gson.toJson(paramsMap);
        if (mKey != null) {
            if (!mKey.equals("")) {
                HashMap<String, String> paramsMap1 = new HashMap<>();
                paramsMap1.put("content", mKey);
                String strEntity1 = gson.toJson(paramsMap1);
                strEntity1 = strEntity1.replace("{", "");
                strEntity = strEntity.replace("}", "," + strEntity1);
            }
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryAllCoursePackageCommunity(body)
                .enqueue(new Callback<CommunityBean>() {
                    @Override
                    public void onResponse(Call<CommunityBean> call, Response<CommunityBean> response) {
                        if (response.body() == null){
                            if (smart_model_communityanswer != null){
                                smart_model_communityanswer.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        //获取网络数据
                        int code = response.body().code;
                        if (!HeaderInterceptor.IsErrorCode(code,response.body().msg)){
                            if (smart_model_communityanswer != null){
                                smart_model_communityanswer.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (code != 200){
                            if (smart_model_communityanswer != null){
                                smart_model_communityanswer.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        CommunityBean.CommunityDataBean communityDataBean = response.body().data;
                        if (communityDataBean == null){
                            if (smart_model_communityanswer != null){
                                smart_model_communityanswer.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mCommunityAnswerSum = communityDataBean.total;
                        if (communityDataBean.list == null){
                            if (smart_model_communityanswer != null){
                                smart_model_communityanswer.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        for (int i = 0; i < communityDataBean.list.size() ; i ++){
                            CommunityBean.ListDataBean listDataBean = communityDataBean.list.get(i);
                            if (listDataBean == null){
                                continue;
                            }
                            View model_communityanswer_child_view1 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_communityanswer_child, null);
                            communityanswer_linearlayout.addView(model_communityanswer_child_view1);
                            TextView course_question_child_name = model_communityanswer_child_view1.findViewById(R.id.course_question_child_name);
                            course_question_child_name.setText(listDataBean.nicename);
                            //浏览人数
                            TextView communityanswer_child_look = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_look);
                            communityanswer_child_look.setText("浏览人数" + listDataBean.visit_num);
                            //社区问答列表头像
                            ControllerCustomRoundAngleImageView communityanswer_child_headportrait = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_headportrait);
                            Glide.with(mControlMainActivity).load(listDataBean.head).listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    Log.d("Wain", "加载失败 errorMsg:" + (e != null ? e.getMessage() : "null"));
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    Log.d("Wain", "成功  Drawable Name:" + resource.getClass().getCanonicalName());
                                    return false;
                                }
                            }).error(mControlMainActivity.getResources().getDrawable(R.drawable.modelmy_myheaddefault)).into(communityanswer_child_headportrait);
                            //社区列表时间
                            Date date = null;
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            try {
                                date = df.parse(listDataBean.creation_time);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (date != null) {
                                SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
                                Date date1 = null;
                                try {
                                    date1 = df1.parse(date.toString());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (date1 != null) {
                                    SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
                                    listDataBean.creation_time = df2.format(date1).toString();
                                }
                            }
                            TextView communityanswer_child_time = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_time);
                            communityanswer_child_time.setText(listDataBean.creation_time);
                            //社区问答标题
                            TextView communityanswer_child_title = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_title);
                            communityanswer_child_title.setText(listDataBean.title);
                            //社区问答内容
                            TextView communityanswer_child_message = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_message);
                            new ModelHtmlUtils(mControlMainActivity, communityanswer_child_message).setHtmlWithPic(listDataBean.content);
                            GridLayout communityanswer_child_imagelayout = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_imagelayout);
                            communityanswer_child_imagelayout.removeAllViews();
                            //集合.size
                            if (listDataBean.picture != null) {
                                String pictures[] = listDataBean.picture.split(";");
                                if (pictures != null) {
                                    for (int num = 0; num < pictures.length; num ++) {
                                        if (pictures[num] == null){
                                            continue;
                                        }
                                        if (pictures[num].equals("")){
                                            continue;
                                        }
                                        View imageView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.controllercustomroundangleimageview_layout, null);
                                        ControllerCustomRoundAngleImageView CustomRoundAngleImageView = imageView.findViewById(R.id.CustomRoundAngleImageView);
                                        Glide.with(mControlMainActivity).load(pictures[num]).listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                Log.d("Warn", "加载失败 errorMsg:" + (e != null ? e.getMessage() : "null"));
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                Log.d("Warn", "成功  Drawable Name:" + resource.getClass().getCanonicalName());
                                                return false;
                                            }
                                        }).error(mControlMainActivity.getResources().getDrawable(R.drawable.modelcoursecover)).into(CustomRoundAngleImageView);
                                        communityanswer_child_imagelayout.addView(imageView);
                                    }
                                }
                            }
                            if (listDataBean.state == 0) { //普通
                                //去掉顶
                                ImageView communityanswer_child_top = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_top);
                                LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) communityanswer_child_top.getLayoutParams();
                                ll.width = 0;
                                ll.rightMargin = 0;
                                communityanswer_child_top.setLayoutParams(ll);
                                //去掉精
                                ImageView communityanswer_child_fine = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_fine);
                                ll = (LinearLayout.LayoutParams) communityanswer_child_fine.getLayoutParams();
                                ll.width = 0;
                                ll.rightMargin = 0;
                                communityanswer_child_fine.setLayoutParams(ll);
                            } else if (listDataBean.state == 1) {//加精
                                //去掉顶
                                ImageView communityanswer_child_top = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_top);
                                LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) communityanswer_child_top.getLayoutParams();
                                ll.width = 0;
                                ll.rightMargin = 0;
                                communityanswer_child_top.setLayoutParams(ll);
                            } else if (listDataBean.state == 2) {//置顶
                                //去掉精
                                ImageView communityanswer_child_fine = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_fine);
                                ll = (LinearLayout.LayoutParams) communityanswer_child_fine.getLayoutParams();
                                ll.width = 0;
                                ll.rightMargin = 0;
                                communityanswer_child_fine.setLayoutParams(ll);
                            }
                            //社区问答标签  至少一个 最多三个    communityanswer_child_sign1
                            if (listDataBean.subject_id != null) {
                                if (listDataBean.subject_id.size() == 0){
                                    LinearLayout communityanswer_child_sign = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_sign);
                                    RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) communityanswer_child_sign.getLayoutParams();
                                    rl.height = 0;
                                    rl.topMargin = 0;
                                    communityanswer_child_sign.setLayoutParams(rl);
                                } else {
                                    for (int num = 0; num < listDataBean.subject_id.size(); num++) {
                                        if (listDataBean.subject_id == null) {
                                            continue;
                                        }
                                        if (num == 0) {
                                            TextView communityanswer_child_sign1 = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_sign1);
                                            communityanswer_child_sign1.setText(listDataBean.subject_id.get(num));
                                            communityanswer_child_sign1.setVisibility(View.VISIBLE);
                                        } else if (num == 1) {
                                            TextView communityanswer_child_sign2 = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_sign2);
                                            communityanswer_child_sign2.setText(listDataBean.subject_id.get(num));
                                            communityanswer_child_sign2.setVisibility(View.VISIBLE);
                                        } else if (num == 2) {
                                            TextView communityanswer_child_sign3 = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_sign3);
                                            communityanswer_child_sign3.setText(listDataBean.subject_id.get(num));
                                            communityanswer_child_sign3.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            } else {
                                LinearLayout communityanswer_child_sign = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_sign);
                                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) communityanswer_child_sign.getLayoutParams();
                                rl.height = 0;
                                rl.topMargin = 0;
                                communityanswer_child_sign.setLayoutParams(rl);
                            }
                            if (listDataBean.huida != null){
                                //添加部分评论，此页最多显示三条
                                LinearLayout communityanswer_child_body = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_body);
                                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) communityanswer_child_body.getLayoutParams();
                                rl.topMargin = (int) model_communityanswer_child_view1.getResources().getDimension(R.dimen.dp15);
                                communityanswer_child_body.setLayoutParams(rl);
                                communityanswer_child_body.setPadding(0,0,0, (int) model_communityanswer_child_view1.getResources().getDimension(R.dimen.dp10));
                                TextView communityanswer_child_discusstext = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_discusstext);
                                communityanswer_child_discusstext.setText( listDataBean.huida_num + "");
                                //社区问答的条目评论显示
                                LinearLayout communityanswer_child_content = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_content);
                                for (int num = 0; num < listDataBean.huida.size() ; num ++){
                                    CommunityBean.DataBean dataBean = listDataBean.huida.get(num);
                                    if (dataBean == null ){
                                        continue;
                                    }
                                    if (num >= 3){//三条以上不显示
                                        //       判断当前的评论是否超过三条，如果评论超过三条显示查看全部
                                        LinearLayout communityanswer_child_lookalldiscuss = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_lookalldiscuss);
                                        ll = (LinearLayout.LayoutParams) communityanswer_child_lookalldiscuss.getLayoutParams();
                                        ll.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                        ll.topMargin = (int) model_communityanswer_child_view1.getResources().getDimension(R.dimen.dp10);
                                        communityanswer_child_lookalldiscuss.setLayoutParams(ll);

                                        //点击查看全部评论进入评论详情
                                        communityanswer_child_lookalldiscuss.setOnClickListener(v->{
                                            CommunityAnswerDetailsShow(listDataBean.questions_id);
                                        });
                                        break;
                                    }
                                    View respondView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_communityanswer_child1, null);
                                    communityanswer_child_content.addView(respondView);
                                    TextView communityanswer_child1_content = respondView.findViewById(R.id.communityanswer_child1_content);
                                    communityanswer_child1_content.setText(dataBean.content);
                                    TextView communityanswer_child1_name = respondView.findViewById(R.id.communityanswer_child1_name);
                                    TextView communityanswer_child1_name1 = respondView.findViewById(R.id.communityanswer_child1_name1);
                                    TextView communityanswer_child1_answer = respondView.findViewById(R.id.communityanswer_child1_answer);
                                    if (dataBean.q_nicename == null){
                                        communityanswer_child1_name.setText(dataBean.a_nicename);
                                        communityanswer_child1_name.setHint(dataBean.aID + "");
                                        LinearLayout.LayoutParams LL = (LinearLayout.LayoutParams) communityanswer_child1_name1.getLayoutParams();
                                        LL.width = 0;
                                        LL.leftMargin = 0;
                                        communityanswer_child1_name1.setLayoutParams(LL);
                                        LL = (LinearLayout.LayoutParams) communityanswer_child1_answer.getLayoutParams();
                                        LL.width = 0;
                                        LL.leftMargin = 0;
                                        communityanswer_child1_answer.setLayoutParams(LL);
                                    } else {
                                        communityanswer_child1_name.setText(dataBean.q_nicename);
                                        communityanswer_child1_name.setHint(dataBean.qID + "");
                                        communityanswer_child1_name1.setText(dataBean.a_nicename);
                                    }
                                    respondView.setOnClickListener(v->{
                                        //回复人的名字
                                        mCustomDialog = new ControllerCustomDialog(mControlMainActivity, R.style.customdialogstyle,"回复 " + communityanswer_child1_name.getText().toString(),false);
                                        mCustomDialog.setOnKeyListener(keylistener);
                                        mCustomDialog.show();
                                        mCustomDialog.setOnClickPublishOrImagelistener(new ControllerCustomDialog.OnClickPublishOrImage() {
                                            @Override
                                            public void publish(String content) {
                                                getCommunityDetilsreplyBeanData(String.valueOf(listDataBean.questions_id), communityanswer_child1_name.getHint().toString(),mControlMainActivity.mStuId,content,"");
                                            }

                                            @Override
                                            public void image() {

                                            }
                                        });
                                    });
                                }
                            }
                            //点击评论，对其进行回复
                            LinearLayout communityanswer_child_function_discuss = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_function_discuss);
                            communityanswer_child_function_discuss.setOnClickListener(v->{
                                mCustomDialog = new ControllerCustomDialog(mControlMainActivity, R.style.customdialogstyle,"评论",false);
                                mCustomDialog.setOnKeyListener(keylistener);
                                mCustomDialog.show();
                                mCustomDialog.setOnClickPublishOrImagelistener(new ControllerCustomDialog.OnClickPublishOrImage() {
                                    @Override
                                    public void publish(String content) {
                                        //获取回复的网络请求 for循环  判断当前的size判断当前的size是否大于3
                                        getCommunityDetilsreplyBeanData(String.valueOf(listDataBean.questions_id), String.valueOf(listDataBean.publisher),mControlMainActivity.mStuId,content,"");
                                    }

                                    @Override
                                    public void image() {
                                        Toast.makeText(getActivity(), "我是公共的图片", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            });

                            //点击查看评论详情
                            LinearLayout communityanswer_child_content1 = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_content1);
                            communityanswer_child_content1.setOnClickListener(v->{
                                CommunityAnswerDetailsShow(listDataBean.questions_id);
                            });
                        }
                        if (smart_model_communityanswer != null){
                            smart_model_communityanswer.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<CommunityBean> call, Throwable t) {
                        if (smart_model_communityanswer != null){
                            smart_model_communityanswer.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                });
    }

    //社区问答-----详情
    public void getCommunityDetilsBeanData(Integer questions_id){
        if (questions_id == null){
            if (mSmart_model_communityanswer_detalis != null){
                mSmart_model_communityanswer_detalis.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        LinearLayout communityanswer_datails_end = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_end);
        communityanswer_datails_end.setVisibility(View.INVISIBLE);
        //子布局的总布局带页面的自定义view线
        communityanswer_datails_linearlayout = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_linearlayout);
        communityanswer_datails_linearlayout.removeAllViews();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mCommunityAnswerDetailsCurrentPage = 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mCommunityAnswerDetailsCurrentPage);//第几页
        paramsMap.put("pageSize",mCommunityAnswerDetailsPageCount);//每页几条
        paramsMap.put("questions_id", questions_id);//问题id
        paramsMap.put("course_type",1);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.QueryCommunityQuestionsDetails(body)
                .enqueue(new Callback<CommunityDetilsBean>() {
                    @Override
                    public void onResponse(Call<CommunityDetilsBean> call, Response<CommunityDetilsBean> response) {
                        CommunityDetilsBean communityDetilsBean = response.body();
                        if (communityDetilsBean == null){
                            if (mSmart_model_communityanswer_detalis != null){
                                mSmart_model_communityanswer_detalis.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        int code = communityDetilsBean.getCode();
                        if (!HeaderInterceptor.IsErrorCode(code,communityDetilsBean.msg)){
                            if (mSmart_model_communityanswer_detalis != null){
                                mSmart_model_communityanswer_detalis.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (code != 200){
                            if (mSmart_model_communityanswer_detalis != null){
                                mSmart_model_communityanswer_detalis.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        CommunityDetilsBean.CommunityDetilsDataBean communityDetilsDataBean = communityDetilsBean.getData();
                        if (communityDetilsDataBean == null){
                            if (mSmart_model_communityanswer_detalis != null){
                                mSmart_model_communityanswer_detalis.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        //子条目的评论title
                        TextView communityanswer_datails_titletext = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_titletext);
                        communityanswer_datails_titletext.setText("社区问答评论");
                        //学员名字
                        TextView communityanswer_datails_name = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_name);
                        communityanswer_datails_name.setText(communityDetilsDataBean.getNicename());
                        //学员的头像
                        ControllerCustomRoundAngleImageView communityanswer_datails_headportrait = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_headportrait);
                        Glide.with(getActivity()).load(communityDetilsDataBean.getHead()).into(communityanswer_datails_headportrait);
                        //学员的时间
                        TextView communityanswer_datails_time = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_time);
                        Date date = null;
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        try {
                            date = df.parse(communityDetilsDataBean.getCreation_time());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (date != null) {
                            SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
                            Date date1 = null;
                            try {
                                date1 = df1.parse(date.toString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (date1 != null) {
                                SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
                                communityDetilsDataBean.setCreation_time(df2.format(date1).toString());
                            }
                        }
                        communityanswer_datails_time.setText(communityDetilsDataBean.getCreation_time());
                        //点击回复问题
                        LinearLayout communityanswer_datails_content1 = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_content1);
                        communityanswer_datails_content1.setOnClickListener(v->{
                            //学员名字
                            mCustomDialog = new ControllerCustomDialog(mControlMainActivity, R.style.customdialogstyle,"回复 " + communityanswer_datails_name.getText().toString(),false);
                            mCustomDialog.setOnKeyListener(keylistener);
                            mCustomDialog.show();
                            mCustomDialog.setOnClickPublishOrImagelistener(new ControllerCustomDialog.OnClickPublishOrImage() {
                                @Override
                                public void publish(String content) {
                                    getCommunityDetilsreplyBeanData(String.valueOf(communityDetilsDataBean.questions_id), String.valueOf(communityDetilsDataBean.publisher),mControlMainActivity.mStuId,content,"");
                                }

                                @Override
                                public void image() {

                                }
                            });
                        });
                        //学员的标题
                        TextView communityanswer_datails_title = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_title);
                        communityanswer_datails_title.setText(communityDetilsDataBean.getTitle());
                        //学员的内容
                        TextView communityanswer_datails_message = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_message);
                        new ModelHtmlUtils(mControlMainActivity, communityanswer_datails_message).setHtmlWithPic(communityDetilsDataBean.getContent());
                        if (communityDetilsDataBean.getSubject_id() != null){
                            for (int i = 0; i < communityDetilsDataBean.subject_id.size() ; i ++){
                                String string = communityDetilsDataBean.subject_id.get(i);
                                if (string == null){
                                    continue;
                                }
                                if (i >= 3){
                                    break;
                                }
                                if (i == 0){
                                    //学员的标签1    communityanswer_datails_sign1
                                    TextView communityanswer_datails_sign1 = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_sign1);
                                    communityanswer_datails_sign1.setText(string);
                                    communityanswer_datails_sign1.setVisibility(View.VISIBLE);
                                } else if (i == 1){
                                    //学员的标签2    communityanswer_datails_sign2
                                    TextView communityanswer_datails_sign2 = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_sign2);
                                    communityanswer_datails_sign2.setText(string);
                                    communityanswer_datails_sign2.setVisibility(View.VISIBLE);
                                } else if (i == 2){
                                    //学员的标签3    communityanswer_datails_sign3
                                    TextView communityanswer_datails_sign3 = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_sign3);
                                    communityanswer_datails_sign3.setText(string);
                                    communityanswer_datails_sign3.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            LinearLayout communityanswer_datails_sign = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_sign);
                            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) communityanswer_datails_sign.getLayoutParams();
                            rl.height = 0;
                            rl.topMargin = 0;
                            communityanswer_datails_sign.setLayoutParams(rl);
                        }
//                        //图片区
                        GridLayout communityanswer_datails_imagelayout = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_imagelayout);
                        communityanswer_datails_imagelayout.removeAllViews();
                        //集合.size
                        if (communityDetilsDataBean.picture != null) {
                            String pictures[] = communityDetilsDataBean.picture.split(";");
                            if (pictures != null) {
                                for (int num = 0; num < pictures.length; num ++) {
                                    if (pictures[num] == null){
                                        continue;
                                    }
                                    if (pictures[num].equals("")){
                                        continue;
                                    }
                                    View imageView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.controllercustomroundangleimageview_layout, null);
                                    ControllerCustomRoundAngleImageView CustomRoundAngleImageView = imageView.findViewById(R.id.CustomRoundAngleImageView);
                                    Glide.with(mControlMainActivity).load(pictures[num]).listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            Log.d("Warn", "加载失败 errorMsg:" + (e != null ? e.getMessage() : "null"));
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            Log.d("Warn", "成功  Drawable Name:" + resource.getClass().getCanonicalName());
                                            return false;
                                        }
                                    }).error(mControlMainActivity.getResources().getDrawable(R.drawable.modelcoursecover)).into(CustomRoundAngleImageView);
                                    communityanswer_datails_imagelayout.addView(imageView);
                                }
                            }
                        }
                        if (communityDetilsDataBean.huida != null){
                            mCommunityAnswerDetailsSum = communityDetilsDataBean.huida.total;
                            if (communityDetilsDataBean.huida.list != null) {
                                for (int i = 0; i < communityDetilsDataBean.huida.list.size(); i++) {
                                    if (communityDetilsDataBean.huida.list.get(i) == null){
                                        continue;
                                    }
                                    //for循环获取网络数据赋值
                                    View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_communityanswer_details1, null);
                                    //子条目学员的头像
                                    ControllerCustomRoundAngleImageView mcommunityanswer_datails1_headportrait = view.findViewById(R.id.communityanswer_datails1_headportrait);
                                    Glide.with(getActivity()).load(communityDetilsDataBean.huida.list.get(i).getA_head()).into(mcommunityanswer_datails1_headportrait);
                                    //学员的姓名
                                    TextView communityanswer_datails1_name = view.findViewById(R.id.communityanswer_datails1_name);
                                    communityanswer_datails1_name.setText(communityDetilsDataBean.huida.list.get(i).q_nicename);
                                    communityanswer_datails1_name.setHint(communityDetilsDataBean.huida.list.get(i).qID + "");
                                    //时间
                                    TextView communityanswer_datails1_time = view.findViewById(R.id.communityanswer_datails1_time);
                                    df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                    try {
                                        date = df.parse(communityDetilsDataBean.huida.list.get(i).creation_time);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if (date != null) {
                                        SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
                                        Date date1 = null;
                                        try {
                                            date1 = df1.parse(date.toString());
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        if (date1 != null) {
                                            SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
                                            communityDetilsDataBean.huida.list.get(i).creation_time = df2.format(date1).toString();
                                        }
                                    }
                                    communityanswer_datails1_time.setText(communityDetilsDataBean.huida.list.get(i).creation_time);
                                    //内容
                                    TextView communityanswer_datails1_message = view.findViewById(R.id.communityanswer_datails1_message);
                                    new ModelHtmlUtils(mControlMainActivity, communityanswer_datails1_message).setHtmlWithPic(communityDetilsDataBean.huida.list.get(i).content);
                                    communityanswer_datails_linearlayout.addView(view);

                                    view.setOnClickListener(v->{
                                        mCustomDialog = new ControllerCustomDialog(mControlMainActivity, R.style.customdialogstyle,"回复 " + communityanswer_datails1_name.getText().toString(),false);
                                        mCustomDialog.setOnKeyListener(keylistener);
                                        mCustomDialog.show();
                                        mCustomDialog.setOnClickPublishOrImagelistener(new ControllerCustomDialog.OnClickPublishOrImage() {
                                            @Override
                                            public void publish(String content) {
                                                getCommunityDetilsreplyBeanData(String.valueOf(communityDetilsDataBean.questions_id),communityanswer_datails1_name.getHint().toString(),mControlMainActivity.mStuId,content,"");
                                            }

                                            @Override
                                            public void image() {

                                            }
                                        });
                                    });
                                }
                            }
                        }
                        if (mSmart_model_communityanswer_detalis != null){
                            mSmart_model_communityanswer_detalis.finishRefresh();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<CommunityDetilsBean> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage() );
                        if (mSmart_model_communityanswer_detalis != null){
                            mSmart_model_communityanswer_detalis.finishRefresh();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //社区问答-----详情-下拉加载
    public void getCommunityDetilsBeanDataMore(Integer questions_id){
        if (questions_id == null){
            if (mSmart_model_communityanswer_detalis != null){
                mSmart_model_communityanswer_detalis.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        //子布局的总布局带页面的自定义view线
        communityanswer_datails_linearlayout = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_linearlayout);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mCommunityAnswerDetailsCurrentPage = mCommunityAnswerDetailsCurrentPage + 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mCommunityAnswerDetailsCurrentPage);//第几页
        paramsMap.put("pageSize",mCommunityAnswerDetailsPageCount);//每页几条
        paramsMap.put("questions_id", questions_id);//问题id
        paramsMap.put("course_type",1);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.QueryCommunityQuestionsDetails(body)
                .enqueue(new Callback<CommunityDetilsBean>() {
                    @Override
                    public void onResponse(Call<CommunityDetilsBean> call, Response<CommunityDetilsBean> response) {
                        CommunityDetilsBean communityDetilsBean = response.body();
                        if (communityDetilsBean == null){
                            if (mSmart_model_communityanswer_detalis != null){
                                mSmart_model_communityanswer_detalis.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        int code = communityDetilsBean.getCode();
                        if (!HeaderInterceptor.IsErrorCode(code,communityDetilsBean.msg)){
                            if (mSmart_model_communityanswer_detalis != null){
                                mSmart_model_communityanswer_detalis.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (code != 200){
                            if (mSmart_model_communityanswer_detalis != null){
                                mSmart_model_communityanswer_detalis.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        CommunityDetilsBean.CommunityDetilsDataBean communityDetilsDataBean = communityDetilsBean.getData();
                        if (communityDetilsDataBean == null){
                            if (mSmart_model_communityanswer_detalis != null){
                                mSmart_model_communityanswer_detalis.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        //子条目的评论title
                        TextView communityanswer_datails_titletext = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_titletext);
                        communityanswer_datails_titletext.setText("社区问答评论");
                        //学员名字
                        TextView communityanswer_datails_name = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_name);
                        communityanswer_datails_name.setText(communityDetilsDataBean.getNicename());
                        //学员的头像
                        ControllerCustomRoundAngleImageView communityanswer_datails_headportrait = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_headportrait);
                        Glide.with(getActivity()).load(communityDetilsDataBean.getHead()).into(communityanswer_datails_headportrait);
                        //学员的时间
                        TextView communityanswer_datails_time = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_time);
                        Date date = null;
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        try {
                            date = df.parse(communityDetilsDataBean.getCreation_time());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (date != null) {
                            SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
                            Date date1 = null;
                            try {
                                date1 = df1.parse(date.toString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (date1 != null) {
                                SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
                                communityDetilsDataBean.setCreation_time(df2.format(date1).toString());
                            }
                        }
                        communityanswer_datails_time.setText(communityDetilsDataBean.getCreation_time());
                        //点击回复问题
                        LinearLayout communityanswer_datails_content1 = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_content1);
                        communityanswer_datails_content1.setOnClickListener(v->{
                            //学员名字
                            mCustomDialog = new ControllerCustomDialog(mControlMainActivity, R.style.customdialogstyle,"回复 " + communityanswer_datails_name.getText().toString(),false);
                            mCustomDialog.setOnKeyListener(keylistener);
                            mCustomDialog.show();
                            mCustomDialog.setOnClickPublishOrImagelistener(new ControllerCustomDialog.OnClickPublishOrImage() {
                                @Override
                                public void publish(String content) {
                                    getCommunityDetilsreplyBeanData(String.valueOf(communityDetilsDataBean.questions_id), String.valueOf(communityDetilsDataBean.publisher),mControlMainActivity.mStuId,content,"");
                                }

                                @Override
                                public void image() {

                                }
                            });
                        });
                        //学员的标题
                        TextView communityanswer_datails_title = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_title);
                        communityanswer_datails_title.setText(communityDetilsDataBean.getTitle());
                        //学员的内容
                        TextView communityanswer_datails_message = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_message);
                        new ModelHtmlUtils(mControlMainActivity, communityanswer_datails_message).setHtmlWithPic(communityDetilsDataBean.getContent());
                        if (communityDetilsDataBean.getSubject_id() != null){
                            for (int i = 0; i < communityDetilsDataBean.subject_id.size() ; i ++){
                                String string = communityDetilsDataBean.subject_id.get(i);
                                if (string == null){
                                    continue;
                                }
                                if (i >= 3){
                                    break;
                                }
                                if (i == 0){
                                    //学员的标签1    communityanswer_datails_sign1
                                    TextView communityanswer_datails_sign1 = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_sign1);
                                    communityanswer_datails_sign1.setText(string);
                                    communityanswer_datails_sign1.setVisibility(View.VISIBLE);
                                } else if (i == 1){
                                    //学员的标签2    communityanswer_datails_sign2
                                    TextView communityanswer_datails_sign2 = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_sign2);
                                    communityanswer_datails_sign2.setText(string);
                                    communityanswer_datails_sign2.setVisibility(View.VISIBLE);
                                } else if (i == 2){
                                    //学员的标签3    communityanswer_datails_sign3
                                    TextView communityanswer_datails_sign3 = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_sign3);
                                    communityanswer_datails_sign3.setText(string);
                                    communityanswer_datails_sign3.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            LinearLayout communityanswer_datails_sign = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_sign);
                            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) communityanswer_datails_sign.getLayoutParams();
                            rl.height = 0;
                            rl.topMargin = 0;
                            communityanswer_datails_sign.setLayoutParams(rl);
                        }

//                        //图片区
                        GridLayout communityanswer_datails_imagelayout = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_imagelayout);
                        communityanswer_datails_imagelayout.removeAllViews();
                        //集合.size
                        if (communityDetilsDataBean.picture != null) {
                            String pictures[] = communityDetilsDataBean.picture.split(";");
                            if (pictures != null) {
                                for (int num = 0; num < pictures.length; num ++) {
                                    if (pictures[num] == null){
                                        continue;
                                    }
                                    if (pictures[num].equals("")){
                                        continue;
                                    }
                                    View imageView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.controllercustomroundangleimageview_layout, null);
                                    ControllerCustomRoundAngleImageView CustomRoundAngleImageView = imageView.findViewById(R.id.CustomRoundAngleImageView);
                                    Glide.with(mControlMainActivity).load(pictures[num]).listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            Log.d("Warn", "加载失败 errorMsg:" + (e != null ? e.getMessage() : "null"));
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            Log.d("Warn", "成功  Drawable Name:" + resource.getClass().getCanonicalName());
                                            return false;
                                        }
                                    }).error(mControlMainActivity.getResources().getDrawable(R.drawable.modelcoursecover)).into(CustomRoundAngleImageView);
                                    communityanswer_datails_imagelayout.addView(imageView);
                                }
                            }
                        }
                        if (communityDetilsDataBean.huida != null){
                            mCommunityAnswerDetailsSum = communityDetilsDataBean.huida.total;
                            if (communityDetilsDataBean.huida.list != null) {
                                for (int i = 0; i < communityDetilsDataBean.huida.list.size(); i++) {
                                    if (communityDetilsDataBean.huida.list.get(i) == null){
                                        continue;
                                    }
                                    //for循环获取网络数据赋值
                                    View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_communityanswer_details1, null);
                                    //子条目学员的头像
                                    ControllerCustomRoundAngleImageView mcommunityanswer_datails1_headportrait = view.findViewById(R.id.communityanswer_datails1_headportrait);
                                    Glide.with(getActivity()).load(communityDetilsDataBean.huida.list.get(i).getA_head()).into(mcommunityanswer_datails1_headportrait);
                                    //学员的姓名
                                    TextView communityanswer_datails1_name = view.findViewById(R.id.communityanswer_datails1_name);
                                    communityanswer_datails1_name.setText(communityDetilsDataBean.huida.list.get(i).q_nicename);
                                    communityanswer_datails1_name.setHint(communityDetilsDataBean.huida.list.get(i).qID + "");
                                    //时间
                                    TextView communityanswer_datails1_time = view.findViewById(R.id.communityanswer_datails1_time);
                                    df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                    try {
                                        date = df.parse(communityDetilsDataBean.huida.list.get(i).creation_time);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if (date != null) {
                                        SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
                                        Date date1 = null;
                                        try {
                                            date1 = df1.parse(date.toString());
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        if (date1 != null) {
                                            SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
                                            communityDetilsDataBean.huida.list.get(i).creation_time = df2.format(date1).toString();
                                        }
                                    }
                                    communityanswer_datails1_time.setText(communityDetilsDataBean.huida.list.get(i).creation_time);
                                    //内容
                                    TextView communityanswer_datails1_message = view.findViewById(R.id.communityanswer_datails1_message);
                                    new ModelHtmlUtils(mControlMainActivity, communityanswer_datails1_message).setHtmlWithPic(communityDetilsDataBean.huida.list.get(i).content);
                                    communityanswer_datails_linearlayout.addView(view);

                                    view.setOnClickListener(v->{
                                        mCustomDialog = new ControllerCustomDialog(mControlMainActivity, R.style.customdialogstyle,"回复 " + communityanswer_datails1_name.getText().toString(),false);
                                        mCustomDialog.setOnKeyListener(keylistener);
                                        mCustomDialog.show();
                                        mCustomDialog.setOnClickPublishOrImagelistener(new ControllerCustomDialog.OnClickPublishOrImage() {
                                            @Override
                                            public void publish(String content) {
                                                getCommunityDetilsreplyBeanData(String.valueOf(communityDetilsDataBean.questions_id),communityanswer_datails1_name.getHint().toString(),mControlMainActivity.mStuId,content,"");
                                            }

                                            @Override
                                            public void image() {

                                            }
                                        });
                                    });
                                }
                            }
                        }
                        if (mSmart_model_communityanswer_detalis != null){
                            mSmart_model_communityanswer_detalis.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<CommunityDetilsBean> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage() );
                        if (mSmart_model_communityanswer_detalis != null){
                            mSmart_model_communityanswer_detalis.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //社区问答查询标签
    public void getCommunityQuerytagsBeanData(){
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), "{}");
        //queryMyCommunityQuerytags
        queryMyCourseList.queryMyCommunityQuerytags(body)
                .enqueue(new Callback<CommunityQuerytagsBean>() {
                    @Override
                    public void onResponse(Call<CommunityQuerytagsBean> call, Response<CommunityQuerytagsBean> response) {
                        CommunityQuerytagsBean querytagsBean = response.body();
                        if (querytagsBean == null){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(querytagsBean.code,querytagsBean.msg)){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (querytagsBean.code != 200){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (querytagsBean.data == null){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        //添加搜索标签的搜索接口
                        ControllerWarpLinearLayout communityanswer_select_warpLinearLayout = mPopupWindowView.findViewById(R.id.communityanswer_select_warpLinearLayout);
                        communityanswer_select_warpLinearLayout.removeAllViews();
                        mCommunityAnswerSelectTemp = mCommunityAnswerSelect;
                        //必须有的标签-全部:默认选中全部
                        //获取网络数据 给搜索标签赋值刷新页面
                        {
                            View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.model_communityanswer_selectpop_child, null);
                            TextView communityanswer_selectpop_child_signname = view.findViewById(R.id.communityanswer_selectpop_child_signname);
                            communityanswer_selectpop_child_signname.setText("全部");
                            communityanswer_selectpop_child_signname.setHint("-1");
                            communityanswer_select_warpLinearLayout.addView(view);
                            view.setOnClickListener(v->{
                                //将其他置为未选中
                                String hint = "";
                                int childcount = communityanswer_select_warpLinearLayout.getChildCount();
                                for (int i = 0; i < childcount ; i ++){
                                    View childView = communityanswer_select_warpLinearLayout.getChildAt(i);
                                    if (childView == null){
                                        continue;
                                    }
                                    TextView communityanswer_selectpop_child_signname1 = childView.findViewById(R.id.communityanswer_selectpop_child_signname);
                                    if (childView == view){
                                        communityanswer_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                                        communityanswer_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.white));
                                        hint = communityanswer_selectpop_child_signname1.getHint().toString();
                                    } else if (communityanswer_selectpop_child_signname1.getHint().toString().equals(mCommunityAnswerSelectTemp)){ // 如果上个找到上一个选中的id，将其置为未选状态
                                        communityanswer_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect));
                                        communityanswer_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.grayff999999));
                                    }
                                }
                                //将选中项置为当前选中项id
                                mCommunityAnswerSelectTemp = hint;
                            });
                            if (mCommunityAnswerSelect.equals("-1")) {
                                communityanswer_selectpop_child_signname.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                                communityanswer_selectpop_child_signname.setTextColor(view.getResources().getColor(R.color.white));
                            }
                        }
                        for (int i = 0; i < querytagsBean.data.size(); i ++){
                            CommunityQuerytagsBean.CommunityissueDataBean communityissueDataBean = querytagsBean.data.get(i);
                            if (communityissueDataBean == null){
                                continue;
                            }
                            if (communityissueDataBean.pse_id == null){
                                continue;
                            }
                            View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.model_communityanswer_selectpop_child, null);
                            TextView communityanswer_selectpop_child_signname = view.findViewById(R.id.communityanswer_selectpop_child_signname);
                            communityanswer_selectpop_child_signname.setText(communityissueDataBean.modify_name);
                            communityanswer_select_warpLinearLayout.addView(view);
                            communityanswer_selectpop_child_signname.setHint(communityissueDataBean.pse_id + "");
                            view.setOnClickListener(v->{
                                //将其他置为未选中
                                String hint = "";
                                int childcount = communityanswer_select_warpLinearLayout.getChildCount();
                                for (int num = 0; num < childcount ; num ++){
                                    View childView = communityanswer_select_warpLinearLayout.getChildAt(num);
                                    if (childView == null){
                                        continue;
                                    }
                                    TextView communityanswer_selectpop_child_signname1 = childView.findViewById(R.id.communityanswer_selectpop_child_signname);
                                    int padding = (int) view.getResources().getDimension(R.dimen.dp5);
                                    if (childView == view){
                                        communityanswer_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                                        communityanswer_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.white));
                                        communityanswer_selectpop_child_signname1.setPadding(padding,padding,padding,padding);
                                        hint = communityanswer_selectpop_child_signname1.getHint().toString();
                                    } else if (communityanswer_selectpop_child_signname1.getHint().toString().equals(mCommunityAnswerSelectTemp)){ // 如果上个找到上一个选中的id，将其置为未选状态
                                        communityanswer_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect));
                                        communityanswer_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.grayff999999));
                                        communityanswer_selectpop_child_signname1.setPadding(padding,padding,padding,padding);
                                    }
                                }
                                //将选中项置为当前选中项id
                                mCommunityAnswerSelectTemp = hint;
                            });
                            if (mCommunityAnswerSelect.equals(communityanswer_selectpop_child_signname.getHint().toString())) {
                                communityanswer_selectpop_child_signname.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                                communityanswer_selectpop_child_signname.setTextColor(view.getResources().getColor(R.color.white));
                            }
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<CommunityQuerytagsBean> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage() );
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //社区问答发布问题查询标签
    public void getCommunityQuerytagsBeanData_publish(){
        LoadingDialog.getInstance(mControlMainActivity).show();
        //添加标签
        ControllerWarpLinearLayout communityanswer_choosesign_warpLinearLayout = mCommunityAnswerChooseSignView.findViewById(R.id.communityanswer_choosesign_warpLinearLayout);
        communityanswer_choosesign_warpLinearLayout.removeAllViews();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), "{}");
        //queryMyCommunityQuerytags
        queryMyCourseList.queryMyCommunityQuerytags(body)
                .enqueue(new Callback<CommunityQuerytagsBean>() {
                    @Override
                    public void onResponse(Call<CommunityQuerytagsBean> call, Response<CommunityQuerytagsBean> response) {
                        CommunityQuerytagsBean querytagsBean = response.body();
                        if (querytagsBean == null){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(querytagsBean.code,querytagsBean.msg)){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (querytagsBean.code != 200){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (querytagsBean.data == null){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        //添加搜索标签的搜索接口
                        TextView communityanswer_choosesign_layout_commit_button1 = mCommunityAnswerChooseSignView.findViewById(R.id.communityanswer_choosesign_layout_commit_button1);
                        for (int num = 0; num < querytagsBean.data.size(); num ++){
                            CommunityQuerytagsBean.CommunityissueDataBean communityissueDataBean = querytagsBean.data.get(num);
                            if (communityissueDataBean == null){
                                continue;
                            }
                            if (communityissueDataBean.pse_id == null){
                                continue;
                            }
                            View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.model_communityanswer_selectpop_child, null);
                            TextView communityanswer_selectpop_child_signname = view.findViewById(R.id.communityanswer_selectpop_child_signname);
                            //选择标签的网络请求
                            communityanswer_selectpop_child_signname.setText(communityissueDataBean.modify_name);
                            communityanswer_selectpop_child_signname.setHint(communityissueDataBean.pse_id + "");
                            communityanswer_choosesign_warpLinearLayout.addView(view);
                            view.setOnClickListener(v->{
                                //如果已经是选中的标签，再次点击取消选中状态
                                for (int i = 0; i < mCommunityAnswerChooseSignList.size() ; i ++){
                                    String chooseSign = mCommunityAnswerChooseSignList.get(i);
                                    if (chooseSign.equals(communityanswer_selectpop_child_signname.getHint().toString())) {
                                        communityanswer_selectpop_child_signname.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect));
                                        communityanswer_selectpop_child_signname.setTextColor(view.getResources().getColor(R.color.grayff999999));
                                        communityanswer_selectpop_child_signname.setPadding((int) view.getResources().getDimension(R.dimen.dp5),
                                                (int) view.getResources().getDimension(R.dimen.dp5),
                                                (int) view.getResources().getDimension(R.dimen.dp5),
                                                (int) view.getResources().getDimension(R.dimen.dp5));
                                        mCommunityAnswerChooseSignList.remove(i);
                                        TextView communityanswer_choosesign_choosecount = mCommunityAnswerChooseSignView.findViewById(R.id.communityanswer_choosesign_choosecount);
                                        communityanswer_choosesign_choosecount.setText(String.valueOf(mCommunityAnswerChooseSignList.size()));
                                        if (mCommunityAnswerChooseSignList.size() == 0){
                                            communityanswer_choosesign_layout_commit_button1.setTextColor(view.getResources().getColor(R.color.black999999));
                                        }
                                        return;
                                    }
                                }
                                //点击选中
                                if (mCommunityAnswerChooseSignList.size() >= 3){
                                    Toast.makeText(mControlMainActivity, "最多选择三个！", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                communityanswer_selectpop_child_signname.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                                communityanswer_selectpop_child_signname.setTextColor(view.getResources().getColor(R.color.white));
                                communityanswer_selectpop_child_signname.setPadding((int) view.getResources().getDimension(R.dimen.dp5),
                                        (int) view.getResources().getDimension(R.dimen.dp5),
                                        (int) view.getResources().getDimension(R.dimen.dp5),
                                        (int) view.getResources().getDimension(R.dimen.dp5));
                                String hint = communityanswer_selectpop_child_signname.getHint().toString();
                                mCommunityAnswerChooseSignList.add(hint);
                                //重置发表按钮颜色
                                if (mCommunityAnswerChooseSignList.size() != 0){
                                    communityanswer_choosesign_layout_commit_button1.setTextColor(view.getResources().getColor(R.color.blackff333333));
                                }
                                TextView communityanswer_choosesign_choosecount = mCommunityAnswerChooseSignView.findViewById(R.id.communityanswer_choosesign_choosecount);
                                //重置选中标签的数量
                                communityanswer_choosesign_choosecount.setText(String.valueOf(mCommunityAnswerChooseSignList.size()));
                            });
                            //没有标签默认选中第一个
                            if (mCommunityAnswerChooseSignList.size() == 0){
                                communityanswer_selectpop_child_signname.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                                communityanswer_selectpop_child_signname.setTextColor(view.getResources().getColor(R.color.white));
                                mCommunityAnswerChooseSignList.add(communityanswer_selectpop_child_signname.getHint().toString());
                                communityanswer_selectpop_child_signname.setPadding((int) view.getResources().getDimension(R.dimen.dp5),
                                        (int) view.getResources().getDimension(R.dimen.dp5),
                                        (int) view.getResources().getDimension(R.dimen.dp5),
                                        (int) view.getResources().getDimension(R.dimen.dp5));
                                TextView communityanswer_choosesign_choosecount = mCommunityAnswerChooseSignView.findViewById(R.id.communityanswer_choosesign_choosecount);
                                communityanswer_choosesign_choosecount.setText(String.valueOf(mCommunityAnswerChooseSignList.size()));
                                //重置发表按钮颜色
                                if (mCommunityAnswerChooseSignList.size() != 0){
                                    communityanswer_choosesign_layout_commit_button1.setTextColor(view.getResources().getColor(R.color.blackff333333));
                                }
                            } else {
                                for (int i = 0; i < mCommunityAnswerChooseSignList.size() ; i ++){
                                    String chooseSign = mCommunityAnswerChooseSignList.get(i);
                                    if (chooseSign.equals(communityanswer_selectpop_child_signname.getHint().toString())) {
                                        communityanswer_selectpop_child_signname.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                                        communityanswer_selectpop_child_signname.setTextColor(view.getResources().getColor(R.color.white));
                                        communityanswer_selectpop_child_signname.setPadding((int) view.getResources().getDimension(R.dimen.dp5),
                                                (int) view.getResources().getDimension(R.dimen.dp5),
                                                (int) view.getResources().getDimension(R.dimen.dp5),
                                                (int) view.getResources().getDimension(R.dimen.dp5));
                                        break;
                                    }
                                }
                            }
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<CommunityQuerytagsBean> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage() );
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //社区问答-----回复
    public void getCommunityDetilsreplyBeanData(String mid,String fid,String publisher,String content,String picture){
        if (mid == null || fid == null || publisher == null || content == null){
            Toast.makeText(mControlMainActivity, "问题回复失败!", Toast.LENGTH_SHORT).show();
            mCustomDialog.dismiss();
            return;
        }
        if (mid.equals("") || fid.equals("") || publisher.equals("") || content.equals("")){
            Toast.makeText(mControlMainActivity, "问题回复失败!", Toast.LENGTH_SHORT).show();
            mCustomDialog.dismiss();
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("mid", mid);//社区问答---回复
        paramsMap.put("fid", fid);
        paramsMap.put("publisher", publisher);
        paramsMap.put("content", content);
        paramsMap.put("picture", picture);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryCommunityDetilsreplyBean(body)
                .enqueue(new Callback<ModelObservableInterface.BaseBean>() {
                    @Override
                    public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                        ModelObservableInterface.BaseBean baseBean = response.body();
                        if (baseBean == null){
                            Toast.makeText(mControlMainActivity, "问题回复失败!", Toast.LENGTH_SHORT).show();
                            mCustomDialog.dismiss();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                            mCustomDialog.dismiss();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (baseBean.getErrorCode() != 200){
                            Toast.makeText(mControlMainActivity, "问题回复失败!", Toast.LENGTH_SHORT).show();
                            mCustomDialog.dismiss();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mControlMainActivity.Page_CommunityAnswer();
                        Toast.makeText(mControlMainActivity, "问题回复成功!", Toast.LENGTH_SHORT).show();
                        mCustomDialog.dismiss();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage() );
                        Toast.makeText(mControlMainActivity, "问题回复失败!", Toast.LENGTH_SHORT).show();
                        mCustomDialog.dismiss();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //上传社区问答中的图片
    private void upLoadAnswerImage(String title,String content) {
        if (title.equals("") || content.equals("")){
            mControlMainActivity.setmState("");
            mIsPublish = true;
            Toast.makeText(mControlMainActivity, "问题发布失败!", Toast.LENGTH_SHORT).show();
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String uu = UUID.randomUUID().toString();
                    Request request = chain.request()
                            .newBuilder()
                            .addHeader("Content-Type", "multipart/form-data; boundary=" + uu)
                            .build();
                    return chain.proceed(request);
                })
                .connectTimeout(3600, TimeUnit.SECONDS)
                .writeTimeout(3600, TimeUnit.SECONDS)
                .readTimeout(3600, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Map<String, RequestBody> params=new HashMap<>() ;
        for (int i = 0; i < selPhotosPath.size(); i ++){
            File file = new File(selPhotosPath.get(i));
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),file);
            params.put("file\"; filename=\""+ i + "#" + file.getName(), requestBody);
        }
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.upLoadImage(params);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean> () {
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                if (response == null){
                    mControlMainActivity.setmState("");
                    mIsPublish = true;
                    Toast.makeText(mControlMainActivity, "问题发布时上传图像失败!", Toast.LENGTH_SHORT).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (response.body() == null){
                    mControlMainActivity.setmState("");
                    mIsPublish = true;
                    Toast.makeText(mControlMainActivity, "问题发布时上传图像失败!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.body().getErrorCode() != 200){
                    mControlMainActivity.setmState("");
                    mIsPublish = true;
                    Toast.makeText(mControlMainActivity, "问题发布时上传图像失败!", Toast.LENGTH_SHORT).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (response.body().getData() == null){
                    mControlMainActivity.setmState("");
                    mIsPublish = true;
                    Toast.makeText(mControlMainActivity, "问题发布时上传图像失败!", Toast.LENGTH_SHORT).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (selPhotosPath.size() == response.body().getData().size()){
                    selPhotosPath.clear();
                }
                for (int i = 0; i < response.body().getData().size(); i ++){
                    String path = (String) response.body().getData().get(String.valueOf(i));
                    selPhotosPath.add(path);
                }
                mControlMainActivity.setmState("");
                mIsPublish = true;
                //发表标签的网络请求
                getCommunityissue();
            }
            //图片上传失败
            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                if (t.getMessage() != null) {
                    Log.d("Tag", t.getMessage().toString());
                }
                mControlMainActivity.setmState("");
                mIsPublish = true;
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                Toast.makeText(mControlMainActivity, "问题发布时上传图像失败!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //社区问答-查询标签
    public static class CommunityQuerytagsBean{
        private List<CommunityissueDataBean> data;
        private int code;
        private String msg;

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }

        public void setData(List<CommunityissueDataBean> data) {
            this.data = data;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public List<CommunityissueDataBean> getData() {
            return data;
        }

        class CommunityissueDataBean {
            private Integer pse_id;
            private String modify_name;

            public void setPse_id(Integer pse_id) {
                this.pse_id = pse_id;
            }

            public Integer getPse_id() {
                return pse_id;
            }

            public String getModify_name() {
                return modify_name;
            }

            public void setModify_name(String modify_name) {
                this.modify_name = modify_name;
            }
        }
    }
    //社区问答列表详情
    public static class CommunityDetilsBean{
        private CommunityDetilsDataBean data;
        private int code;
        private String msg;

        public int getCode() {
            return code;
        }

        public void setData(CommunityDetilsDataBean data) {
            this.data = data;
        }

        public CommunityDetilsDataBean getData() {
            return data;
        }

        public String getMsg() {
            return msg;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        class CommunityDetilsDataBean{
            private List<String> subject_id;
            private String creation_time;
            private Integer questions_id;
            private String nicename;
            private String title;
            private String content;
            private String picture;
            private String head;
            private Integer publisher;
            private Integer state;
            private Integer visit_num;
            private CommunityDetilsAnswerDataBean huida;

            public void setHuida(CommunityDetilsAnswerDataBean huida) {
                this.huida = huida;
            }

            public void setState(Integer state) {
                this.state = state;
            }

            public void setPublisher(Integer publisher) {
                this.publisher = publisher;
            }

            public void setPicture(String picture) {
                this.picture = picture;
            }

            public void setNicename(String nicename) {
                this.nicename = nicename;
            }

            public void setHead(String head) {
                this.head = head;
            }

            public String getPicture() {
                return picture;
            }

            public String getNicename() {
                return nicename;
            }

            public String getHead() {
                return head;
            }

            public Integer getState() {
                return state;
            }

            public Integer getPublisher() {
                return publisher;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getTitle() {
                return title;
            }

            public String getCreation_time() {
                return creation_time;
            }

            public void setCreation_time(String creation_time) {
                this.creation_time = creation_time;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getContent() {
                return content;
            }

            public void setVisit_num(Integer visit_num) {
                this.visit_num = visit_num;
            }

            public void setQuestions_id(Integer questions_id) {
                this.questions_id = questions_id;
            }

            public void setSubject_id(List<String> subject_id) {
                this.subject_id = subject_id;
            }

            public CommunityDetilsAnswerDataBean getHuida() {
                return huida;
            }

            public Integer getQuestions_id() {
                return questions_id;
            }

            public Integer getVisit_num() {
                return visit_num;
            }

            public List<String> getSubject_id() {
                return subject_id;
            }
        }
        class CommunityDetilsAnswerDataBean {
            private Integer total;
            private List<CommunityDetilsAnswerDataBeanList> list;

            public void setTotal(Integer total) {
                this.total = total;
            }

            public Integer getTotal() {
                return total;
            }

            public List<CommunityDetilsAnswerDataBeanList> getList() {
                return list;
            }

            public void setList(List<CommunityDetilsAnswerDataBeanList> list) {
                this.list = list;
            }
        }

        class CommunityDetilsAnswerDataBeanList {
            private Integer a_publisher;
            private String q_head;
            private Integer a_st;
            private String q_nicename;
            private Integer q_publisher;
            private Integer q_st;
            private Integer qID;
            private Integer aID;
            private String a_nicename;
            private String content;
            private String a_head;
            private String creation_time;

            public void setCreation_time(String creation_time) {
                this.creation_time = creation_time;
            }

            public String getCreation_time() {
                return creation_time;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public Integer getA_publisher() {
                return a_publisher;
            }

            public Integer getA_st() {
                return a_st;
            }

            public Integer getaID() {
                return aID;
            }

            public Integer getQ_publisher() {
                return q_publisher;
            }

            public Integer getQ_st() {
                return q_st;
            }

            public Integer getqID() {
                return qID;
            }

            public String getA_head() {
                return a_head;
            }

            public String getA_nicename() {
                return a_nicename;
            }

            public String getQ_head() {
                return q_head;
            }

            public String getQ_nicename() {
                return q_nicename;
            }

            public void setA_head(String a_head) {
                this.a_head = a_head;
            }

            public void setA_nicename(String a_nicename) {
                this.a_nicename = a_nicename;
            }

            public void setA_publisher(Integer a_publisher) {
                this.a_publisher = a_publisher;
            }

            public void setA_st(Integer a_st) {
                this.a_st = a_st;
            }

            public void setaID(Integer aID) {
                this.aID = aID;
            }

            public void setQ_head(String q_head) {
                this.q_head = q_head;
            }

            public void setQ_nicename(String q_nicename) {
                this.q_nicename = q_nicename;
            }

            public void setQ_publisher(Integer q_publisher) {
                this.q_publisher = q_publisher;
            }

            public void setQ_st(Integer q_st) {
                this.q_st = q_st;
            }

            public void setqID(Integer qID) {
                this.qID = qID;
            }
        }
    }


     //社区问答列表
    public static class CommunityBean{
        /**
         * code : 200
         * data : {"uid":1,"status":1,"title":"问答标题","details":"这个老师特别好，知识讲解很详细","picture":"","label":"消防安全实务"}
         */

        private int code;
        private CommunityDataBean data;
        private String msg;

         public String getMsg() {
             return msg;
         }

         public void setMsg(String msg) {
             this.msg = msg;
         }

         public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public CommunityDataBean getData() {
            return data;
        }

        public void setData(CommunityDataBean data) {
            this.data = data;
        }

        public static class CommunityDataBean {
             private Integer total;
             private List<ListDataBean> list;

            public void setList(List<ListDataBean> list) {
                this.list = list;
            }

            public Integer getTotal() {
                return total;
            }

            public void setTotal(Integer total) {
                this.total = total;
            }

            public List<ListDataBean> getList() {
                return list;
            }
        }

        public static class ListDataBean {
            private List<String> subject_id;
            private String creation_time;
            private Integer questions_id;
            private String nicename;
            private String title;
            private String content;
            private String picture;
            private String head;
            private Integer publisher;
            private Integer state;
            private Integer visit_num;
            private Integer huida_num;
            private List<DataBean> huida;

            public Integer getHuida_num() {
                return huida_num;
            }

            public void setHuida_num(Integer huida_num) {
                this.huida_num = huida_num;
            }

            public String getCreation_time() {
                return creation_time;
            }

            public void setCreation_time(String creation_time) {
                this.creation_time = creation_time;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getContent() {
                return content;
            }

            public List<String> getSubject_id() {
                return subject_id;
            }

            public Integer getVisit_num() {
                return visit_num;
            }

            public Integer getQuestions_id() {
                return questions_id;
            }

            public void setSubject_id(List<String> subject_id) {
                this.subject_id = subject_id;
            }

            public void setQuestions_id(Integer questions_id) {
                this.questions_id = questions_id;
            }

            public void setVisit_num(Integer visit_num) {
                this.visit_num = visit_num;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public Integer getPublisher() {
                return publisher;
            }

            public Integer getState() {
                return state;
            }

            public String getHead() {
                return head;
            }

            public String getNicename() {
                return nicename;
            }

            public String getPicture() {
                return picture;
            }

            public void setHead(String head) {
                this.head = head;
            }

            public void setNicename(String nicename) {
                this.nicename = nicename;
            }

            public void setPicture(String picture) {
                this.picture = picture;
            }

            public void setPublisher(Integer publisher) {
                this.publisher = publisher;
            }

            public void setState(Integer state) {
                this.state = state;
            }

            public void setHuida(List<DataBean> huida) {
                this.huida = huida;
            }

            public List<DataBean> getHuida() {
                return huida;
            }
        }
        public static class DataBean {

            private Integer a_publisher;
            private String q_head;
            private Integer a_st;
            private String q_nicename;
            private Integer q_publisher;
            private Integer q_st;
            private Integer qID;
            private Integer aID;
            private String a_nicename;
            private String content;
            private String a_head;
            private String creation_time;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public void setCreation_time(String creation_time) {
                this.creation_time = creation_time;
            }

            public String getCreation_time() {
                return creation_time;
            }

            public void setqID(Integer qID) {
                this.qID = qID;
            }

            public void setQ_st(Integer q_st) {
                this.q_st = q_st;
            }

            public void setQ_publisher(Integer q_publisher) {
                this.q_publisher = q_publisher;
            }

            public void setQ_nicename(String q_nicename) {
                this.q_nicename = q_nicename;
            }

            public void setQ_head(String q_head) {
                this.q_head = q_head;
            }

            public void setaID(Integer aID) {
                this.aID = aID;
            }

            public void setA_st(Integer a_st) {
                this.a_st = a_st;
            }

            public void setA_publisher(Integer a_publisher) {
                this.a_publisher = a_publisher;
            }

            public void setA_nicename(String a_nicename) {
                this.a_nicename = a_nicename;
            }

            public void setA_head(String a_head) {
                this.a_head = a_head;
            }

            public String getQ_nicename() {
                return q_nicename;
            }

            public String getQ_head() {
                return q_head;
            }

            public String getA_nicename() {
                return a_nicename;
            }

            public String getA_head() {
                return a_head;
            }

            public Integer getqID() {
                return qID;
            }

            public Integer getQ_st() {
                return q_st;
            }

            public Integer getQ_publisher() {
                return q_publisher;
            }

            public Integer getaID() {
                return aID;
            }

            public Integer getA_st() {
                return a_st;
            }

            public Integer getA_publisher() {
                return a_publisher;
            }
        }
    }
}
