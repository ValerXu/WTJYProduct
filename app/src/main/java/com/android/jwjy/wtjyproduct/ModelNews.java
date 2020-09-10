package com.android.jwjy.wtjyproduct;

import android.support.v4.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//import android.icu.text.SimpleDateFormat;
import java.text.SimpleDateFormat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Created by dayuer on 19/7/2.
 * 新闻模块
 */
public class ModelNews extends Fragment implements View.OnClickListener {
    private static ControlMainActivity mControlMainActivity;
    private static String mContext = "xxxxxxxxxxxxx";
    //要显示的页面
    static private int FragmentPage;
    private View mview;
    private int height = 1344;
    private int width = 720;
    private static final String TAG = "ModelNews";
    private SmartRefreshLayout mSmart_new_layout;

    //新闻列表分页查询
    private int mNewsCurrentPage = 0;
    private int mNewsPageCount = 10;
    private int mNewsSum = 0; //新闻列表总数
    private static ModelNews myFragment;
    private String mCurrentPage = "newslist";


    public static Fragment newInstance(ControlMainActivity content, String context, int iFragmentPage) {
        mContext = context;
        mControlMainActivity = content;
        myFragment = new ModelNews();
        FragmentPage = iFragmentPage;
        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mview = inflater.inflate(FragmentPage, container, false);
        DisplayMetrics dm = mControlMainActivity.getResources().getDisplayMetrics(); //获取屏幕分辨率
        height = dm.heightPixels;
        width = dm.widthPixels;
        NewsMainShow();
        initSmartRefresh();
        return mview;
    }
    //初始化刷新模块
    private void initSmartRefresh() {
        mSmart_new_layout = mview.findViewById(R.id.Smart_new_layout);
        mSmart_new_layout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mCurrentPage.equals("newsdetails")){
                    mSmart_new_layout.finishLoadMore();
                    return;
                }
                if (mNewsSum <= mNewsCurrentPage * mNewsPageCount){
                    mSmart_new_layout.finishLoadMore();
                    LinearLayout news_end = mview.findViewById(R.id.news_end);
                    news_end.setVisibility(View.VISIBLE);
                    return;
                }
                getModelNewsMore();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (mCurrentPage.equals("newsdetails")){
                    mSmart_new_layout.finishRefresh();
                    return;
                }
                getModelNews();
            }
        });
    }

    //新闻主界面展示
    public void NewsMainShow() {
        if (mview == null) {
            return;
        }
        getModelNews();
    }

    //隐藏所有图层
    private void HideAllLayout() {
        LinearLayout news_content = mview.findViewById(R.id.news_content);
        news_content.removeAllViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default: {
                break;
            }
        }
    }


    //新闻咨讯详情
    public void getModelNewsDetils(Integer newsId) {
        if (newsId == null){
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("news_id", newsId);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryCoursePackageModelNewsDetils(body)
                .enqueue(new Callback<ModelNewsDetilsBean>() {
                    @Override
                    public void onResponse(Call<ModelNewsDetilsBean> call, Response<ModelNewsDetilsBean> response) {
                        ModelNewsDetilsBean detilsBean = response.body();
                        if (detilsBean == null){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(detilsBean.getCode(),"")){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        int code = detilsBean.getCode();
                        if (code != 200){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        ModelNewsDetilsBean.DataBean data = detilsBean.getData();
                        if (data == null){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        HideAllLayout();
                        View newsView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.news_layout2, null);
                        //新闻标题
                        TextView news2_newstitle = newsView.findViewById(R.id.news2_newstitle);
                        news2_newstitle.setText(data.getNews_title());
                        //新闻详情的内容  HTML格式
                        TextView news2_news = newsView.findViewById(R.id.news2_news);
                        new ModelHtmlUtils(mControlMainActivity, news2_news).setHtmlWithPic(data.news_content);
                        TextView news2_newsdata = newsView.findViewById(R.id.news2_newsdata);
                        Date date = null;
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        try {
                            date = df.parse(data.create_time);
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
                                SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                data.create_time = df2.format(date1).toString();
                            }
                        }
                        news2_newsdata.setText(data.create_time);
                        TextView news2_looknum = newsView.findViewById(R.id.news2_looknum);
                        news2_looknum.setText(data.visit_num + "");
                        LinearLayout news_content = mview.findViewById(R.id.news_content);
                        news_content.addView(newsView);
                        mControlMainActivity.Page_NewsDetails();
                        mCurrentPage = "newsdetails";
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelNewsDetilsBean> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }


    //新闻资讯
    public void getModelNews() {
        LoadingDialog.getInstance(mControlMainActivity).show();
        mCurrentPage = "newslist";
        LinearLayout news_end = mview.findViewById(R.id.news_end);
        news_end.setVisibility(View.INVISIBLE);
        LinearLayout news_content = mview.findViewById(R.id.news_content);
        news_content.removeAllViews();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mNewsCurrentPage = 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mNewsCurrentPage);//第几页
        paramsMap.put("pageSize", mNewsPageCount);//	每页的条目数
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryCoursePackageModelNews(body)
                .enqueue(new Callback<ModelNewsBean>() {
                    @Override
                    public void onResponse(Call<ModelNewsBean> call, Response<ModelNewsBean> response) {
                        ModelNewsBean newsBean = response.body();
                        if (newsBean != null) {
                            int code = newsBean.getCode();
                            if (!HeaderInterceptor.IsErrorCode(newsBean.getCode(),"")){
                                if (mSmart_new_layout != null) {
                                    mSmart_new_layout.finishRefresh();
                                }
                                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                return;
                            }
                            if (code != 200){
                                if (mSmart_new_layout != null) {
                                    mSmart_new_layout.finishRefresh();
                                }
                                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                return;
                            }
                            ModelNewsBean.DataBean data = newsBean.getData();
                            if (data == null){
                                if (mSmart_new_layout != null) {
                                    mSmart_new_layout.finishRefresh();
                                }
                                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                return;
                            }
                            mNewsSum = data.getTotal();
                            List<ModelNewsBean.DataBean.ListBean> list = data.getList();
                            if (list == null){
                                if (mSmart_new_layout != null) {
                                    mSmart_new_layout.finishRefresh();
                                }
                                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                return;
                            }
                            View line = null;
                            for (int i = 0; i < list.size(); i ++) {
                                ModelNewsBean.DataBean.ListBean listBean = list.get(i);
                                if (listBean == null){
                                    continue;
                                }
                                View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.news_layout1, null);
                                news_content.addView(view);
                                view.setOnClickListener(v -> {
                                    //获取详情的网络数据
                                    getModelNewsDetils(listBean.news_id);
                                });
                                //加载新闻封面
                                ImageView news1_cover1 = view.findViewById(R.id.news1_cover);
                                Glide.with(mControlMainActivity).load(listBean.getNews_cover()).listener(new RequestListener<Drawable>() {
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
                                }).error(mControlMainActivity.getResources().getDrawable(R.drawable.modelcoursecover)).into(news1_cover1);
                                //新闻名称
                                TextView news1_classname1 = view.findViewById(R.id.news1_classname);
                                news1_classname1.setText(listBean.getNews_title());
                                //新闻发布时间
                                TextView news1_data1 = view.findViewById(R.id.news1_data);
                                //时间格式转码
                                Date date = null;
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                try {
                                    date = df.parse(listBean.create_time);
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
                                        listBean.create_time = df2.format(date1);
                                    }
                                }
                                news1_data1.setText(listBean.create_time);
                                TextView news1_looknum = view.findViewById(R.id.news1_looknum);
                                news1_looknum.setText(String.valueOf(listBean.visit_num));
                                //分割线
                                line = view.findViewById(R.id.news1_line1);

                                if (listBean.tf_comment == 2) { //不推荐新闻
                                    TextView news1_state = view.findViewById(R.id.news1_state);
                                    LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) news1_state.getLayoutParams();
                                    ll.rightMargin = 0;
                                    ll.width = 0;
                                    news1_state.setLayoutParams(ll);
                                }
                            }
                            if (line != null ){
                                line.setVisibility(View.INVISIBLE);
                            }
                            if (mSmart_new_layout != null) {
                                mSmart_new_layout.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelNewsBean> call, Throwable t) {
                        Log.e(TAG, "onFailure:错误信息是 " + t.getMessage());
                        if (mSmart_new_layout != null) {
                            mSmart_new_layout.finishRefresh();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //新闻资讯
    public void getModelNewsMore() {
        LoadingDialog.getInstance(mControlMainActivity).show();
        LinearLayout news_content = mview.findViewById(R.id.news_content);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mNewsCurrentPage = mNewsCurrentPage + 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mNewsCurrentPage);//第几页
        paramsMap.put("pageSize", mNewsPageCount);//	每页的条目数
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryCoursePackageModelNews(body)
                .enqueue(new Callback<ModelNewsBean>() {
                    @Override
                    public void onResponse(Call<ModelNewsBean> call, Response<ModelNewsBean> response) {
                        ModelNewsBean newsBean = response.body();
                        if (newsBean != null) {
                            if (!HeaderInterceptor.IsErrorCode(newsBean.getCode(),"")){
                                if (mSmart_new_layout != null) {
                                    mSmart_new_layout.finishLoadMore();
                                }
                                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                return;
                            }
                            int code = newsBean.getCode();
                            if (code != 200){
                                if (mSmart_new_layout != null) {
                                    mSmart_new_layout.finishLoadMore();
                                }
                                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                return;
                            }
                            ModelNewsBean.DataBean data = newsBean.getData();
                            if (data == null){
                                if (mSmart_new_layout != null) {
                                    mSmart_new_layout.finishLoadMore();
                                }
                                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                return;
                            }
                            mNewsSum = data.getTotal();
                            List<ModelNewsBean.DataBean.ListBean> list = data.getList();
                            if (list == null){
                                if (mSmart_new_layout != null) {
                                    mSmart_new_layout.finishLoadMore();
                                }
                                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                return;
                            }
                            View line = null;
                            for (int i = 0; i < list.size(); i ++) {
                                ModelNewsBean.DataBean.ListBean listBean = list.get(i);
                                if (listBean == null){
                                    continue;
                                }
                                View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.news_layout1, null);
                                view.setOnClickListener(v -> {
                                    //获取详情的网络数据
                                    getModelNewsDetils(listBean.news_id);
                                });
                                news_content.addView(view);
                                //加载新闻封面
                                ImageView news1_cover1 = view.findViewById(R.id.news1_cover);
                                Glide.with(mControlMainActivity).load(listBean.getNews_cover()).listener(new RequestListener<Drawable>() {
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
                                }).error(mControlMainActivity.getResources().getDrawable(R.drawable.modelcoursecover)).into(news1_cover1);
                                //新闻名称
                                TextView news1_classname1 = view.findViewById(R.id.news1_classname);
                                news1_classname1.setText(listBean.getNews_title());
                                //新闻发布时间
                                TextView news1_data1 = view.findViewById(R.id.news1_data);
                                //时间格式转码
                                Date date = null;
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                try {
                                    date = df.parse(listBean.create_time);
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
                                        listBean.create_time = df2.format(date1);
                                    }
                                }
                                news1_data1.setText(listBean.create_time);
                                TextView news1_looknum = view.findViewById(R.id.news1_looknum);
                                news1_looknum.setText(String.valueOf(listBean.visit_num));
                                //分割线
                                line = view.findViewById(R.id.news1_line1);

                                if (listBean.tf_comment == 2) { //不推荐新闻
                                    TextView news1_state = view.findViewById(R.id.news1_state);
                                    LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) news1_state.getLayoutParams();
                                    ll.rightMargin = 0;
                                    ll.width = 0;
                                    news1_state.setLayoutParams(ll);
                                }
                            }
                            if (line != null ){
                                line.setVisibility(View.INVISIBLE);
                            }
                            if (mSmart_new_layout != null) {
                                mSmart_new_layout.finishLoadMore();
                            }
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelNewsBean> call, Throwable t) {
                        Log.e(TAG, "onFailure:错误信息是 " + t.getMessage());
                        if (mSmart_new_layout != null) {
                            mSmart_new_layout.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    public static class ModelNewsBean {
        /**
         * code : 200
         * data : {"total":3,"list":[{"news_cover":"插入封面2","tf_comment":1,"create_time":"2019-10-29T09:30:30.000+0800","news_title":"测试插入标题2","visit_num":10,"news_id":4},{"news_cover":"E:/upload111/logo1575509930210.png","tf_comment":1,"create_time":"2019-12-05T09:38:58.000+0800","news_title":"新闻标题0101","visit_num":0,"news_id":11},{"news_cover":"E:/upload111/u=1653307308,2646823946&fm=26&gp=01575621264686.jpg","tf_comment":1,"create_time":"2019-12-06T14:03:25.000+0800","news_title":"新闻222","visit_num":0,"news_id":16}],"pageNum":1,"pageSize":3,"size":3,"startRow":1,"endRow":3,"pages":1,"prePage":0,"nextPage":0,"isFirstPage":true,"isLastPage":true,"hasPreviousPage":false,"hasNextPage":false,"navigatePages":8,"navigatepageNums":[1],"navigateFirstPage":1,"navigateLastPage":1,"lastPage":1,"firstPage":1}
         */

        private int code;
        private DataBean data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public DataBean getData() {
            return data;
            
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * total : 3
             * list : [{"news_cover":"插入封面2","tf_comment":1,"create_time":"2019-10-29T09:30:30.000+0800","news_title":"测试插入标题2","visit_num":10,"news_id":4},{"news_cover":"E:/upload111/logo1575509930210.png","tf_comment":1,"create_time":"2019-12-05T09:38:58.000+0800","news_title":"新闻标题0101","visit_num":0,"news_id":11},{"news_cover":"E:/upload111/u=1653307308,2646823946&fm=26&gp=01575621264686.jpg","tf_comment":1,"create_time":"2019-12-06T14:03:25.000+0800","news_title":"新闻222","visit_num":0,"news_id":16}]
             * pageNum : 1
             * pageSize : 3
             * size : 3
             * startRow : 1
             * endRow : 3
             * pages : 1
             * prePage : 0
             * nextPage : 0
             * isFirstPage : true
             * isLastPage : true
             * hasPreviousPage : false
             * hasNextPage : false
             * navigatePages : 8
             * navigatepageNums : [1]
             * navigateFirstPage : 1
             * navigateLastPage : 1
             * lastPage : 1
             * firstPage : 1
             */

            private int total;
            private int pageNum;
            private int pageSize;
            private int size;
            private int startRow;
            private int endRow;
            private int pages;
            private int prePage;
            private int nextPage;
            private boolean isFirstPage;
            private boolean isLastPage;
            private boolean hasPreviousPage;
            private boolean hasNextPage;
            private int navigatePages;
            private int navigateFirstPage;
            private int navigateLastPage;
            private int lastPage;
            private int firstPage;
            private List<ListBean> list;
            private List<Integer> navigatepageNums;

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public int getPageNum() {
                return pageNum;
            }

            public void setPageNum(int pageNum) {
                this.pageNum = pageNum;
            }

            public int getPageSize() {
                return pageSize;
            }

            public void setPageSize(int pageSize) {
                this.pageSize = pageSize;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public int getStartRow() {
                return startRow;
            }

            public void setStartRow(int startRow) {
                this.startRow = startRow;
            }

            public int getEndRow() {
                return endRow;
            }

            public void setEndRow(int endRow) {
                this.endRow = endRow;
            }

            public int getPages() {
                return pages;
            }

            public void setPages(int pages) {
                this.pages = pages;
            }

            public int getPrePage() {
                return prePage;
            }

            public void setPrePage(int prePage) {
                this.prePage = prePage;
            }

            public int getNextPage() {
                return nextPage;
            }

            public void setNextPage(int nextPage) {
                this.nextPage = nextPage;
            }

            public boolean isIsFirstPage() {
                return isFirstPage;
            }

            public void setIsFirstPage(boolean isFirstPage) {
                this.isFirstPage = isFirstPage;
            }

            public boolean isIsLastPage() {
                return isLastPage;
            }

            public void setIsLastPage(boolean isLastPage) {
                this.isLastPage = isLastPage;
            }

            public boolean isHasPreviousPage() {
                return hasPreviousPage;
            }

            public void setHasPreviousPage(boolean hasPreviousPage) {
                this.hasPreviousPage = hasPreviousPage;
            }

            public boolean isHasNextPage() {
                return hasNextPage;
            }

            public void setHasNextPage(boolean hasNextPage) {
                this.hasNextPage = hasNextPage;
            }

            public int getNavigatePages() {
                return navigatePages;
            }

            public void setNavigatePages(int navigatePages) {
                this.navigatePages = navigatePages;
            }

            public int getNavigateFirstPage() {
                return navigateFirstPage;
            }

            public void setNavigateFirstPage(int navigateFirstPage) {
                this.navigateFirstPage = navigateFirstPage;
            }

            public int getNavigateLastPage() {
                return navigateLastPage;
            }

            public void setNavigateLastPage(int navigateLastPage) {
                this.navigateLastPage = navigateLastPage;
            }

            public int getLastPage() {
                return lastPage;
            }

            public void setLastPage(int lastPage) {
                this.lastPage = lastPage;
            }

            public int getFirstPage() {
                return firstPage;
            }

            public void setFirstPage(int firstPage) {
                this.firstPage = firstPage;
            }

            public List<ListBean> getList() {
                return list;
            }

            public void setList(List<ListBean> list) {
                this.list = list;
            }

            public List<Integer> getNavigatepageNums() {
                return navigatepageNums;
            }

            public void setNavigatepageNums(List<Integer> navigatepageNums) {
                this.navigatepageNums = navigatepageNums;
            }

            public static class ListBean {
                /**
                 * news_cover : 插入封面2
                 * tf_comment : 1
                 * create_time : 2019-10-29T09:30:30.000+0800
                 * news_title : 测试插入标题2
                 * visit_num : 10
                 * news_id : 4
                 */

                private String news_cover;
                private int tf_comment;
                private String create_time;
                private String news_title;
                private int visit_num;
                private int news_id;

                public String getNews_cover() {
                    return news_cover;
                }

                public void setNews_cover(String news_cover) {
                    this.news_cover = news_cover;
                }

                public int getTf_comment() {
                    return tf_comment;
                }

                public void setTf_comment(int tf_comment) {
                    this.tf_comment = tf_comment;
                }

                public String getCreate_time() {
                    return create_time;
                }

                public void setCreate_time(String create_time) {
                    this.create_time = create_time;
                }

                public String getNews_title() {
                    return news_title;
                }

                public void setNews_title(String news_title) {
                    this.news_title = news_title;
                }

                public int getVisit_num() {
                    return visit_num;
                }

                public void setVisit_num(int visit_num) {
                    this.visit_num = visit_num;
                }

                public int getNews_id() {
                    return news_id;
                }

                public void setNews_id(int news_id) {
                    this.news_id = news_id;
                }
            }
        }
    }
    //新闻详情
    public static class ModelNewsDetilsBean {
        /**
         * code : 200
         * data : {"create_time":"2019-10-29T09:30:30.000+0800","news_content":"插入内容3","news_title":"测试插入标题2","visit_num":13,"news_id":4}
         */

        private int code;
        private DataBean data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * create_time : 2019-10-29T09:30:30.000+0800
             * news_content : 插入内容3
             * news_title : 测试插入标题2
             * visit_num : 13
             * news_id : 4
             */

            private String create_time;
            private String news_content;
            private String news_title;
            private int visit_num;
            private int news_id;

            public String getCreate_time() {
                return create_time;
            }

            public void setCreate_time(String create_time) {
                this.create_time = create_time;
            }

            public String getNews_content() {
                return news_content;
            }

            public void setNews_content(String news_content) {
                this.news_content = news_content;
            }

            public String getNews_title() {
                return news_title;
            }

            public void setNews_title(String news_title) {
                this.news_title = news_title;
            }

            public int getVisit_num() {
                return visit_num;
            }

            public void setVisit_num(int visit_num) {
                this.visit_num = visit_num;
            }

            public int getNews_id() {
                return news_id;
            }

            public void setNews_id(int news_id) {
                this.news_id = news_id;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
