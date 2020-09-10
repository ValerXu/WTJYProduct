package com.android.jwjy.wtjyproduct;

import android.support.v4.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jwjy.wtjyproduct.consts.PlayType;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dayuer on 19/7/2.
 * 课程表
 */
public class ModelClassCheduleCard extends Fragment implements
        CalendarView.OnDateSelectedListener,
        CalendarView.OnYearChangeListener,
        View.OnClickListener {
    private static int FragmentPage;
    private static String mContext;
    //    private List<WeekDay> mWeekDayList ;
    private View mView = null,mClasschedulecardView = null;
    private static ControlMainActivity mControlMainActivity;
    private ModelGroupRecyclerView recyclerView;
    private CalendarView calendarView ;

    //要显示的页面
//    private int FragmentPage;
    public  static  Fragment newInstance(ControlMainActivity content, String context, int iFragmentPage){
        mContext = context;
        mControlMainActivity = content;
        ModelClassCheduleCard myFragment = new ModelClassCheduleCard();
        FragmentPage = iFragmentPage;
        return  myFragment;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(FragmentPage,container,false);
        ClassCheduleCardMainInit(mContext);
        return mView;
    }

    //隐藏所有图层
    private void HideAllLayout(){
        LinearLayout classchedulecard_content = mView.findViewById(R.id.classchedulecard_content);
        classchedulecard_content.removeAllViews();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void ClassCheduleCardMainInit(String context){
        if (mView == null){
            return;
        }
        HideAllLayout();
//        if (mClasschedulecardView == null) {
            mClasschedulecardView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.fragment_classchedulecard1, null);
            calendarView = mClasschedulecardView.findViewById(R.id.calendarView);
//        calendarView.setThemeColor(Color.YELLOW,Color.YELLOW);
            calendarView.setOnDateSelectedListener(this);
            calendarView.setOnYearChangeListener(this);
//        }
        if (context.equals("首页")){
            LinearLayout classchedulecard_title_layout = mView.findViewById(R.id.classchedulecard_title_layout);
            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) classchedulecard_title_layout.getLayoutParams();
            rl.height = 0;
            classchedulecard_title_layout.setLayoutParams(rl);
        } else {
            LinearLayout classchedulecard_title_layout = mView.findViewById(R.id.classchedulecard_title_layout);
            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) classchedulecard_title_layout.getLayoutParams();
            rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            classchedulecard_title_layout.setLayoutParams(rl);
        }
        //获取当天日期和时间
        Date dateOld = new Date(System.currentTimeMillis()); // 根据long类型的毫秒数生命一个date类型的时间
        final java.util.Calendar c = java.util.Calendar.getInstance();
        String format1 = new SimpleDateFormat("yyyy-MM-dd").format(dateOld);
        int mWay = c.get(java.util.Calendar.DAY_OF_WEEK);
        String classchedulecard_week = "";
        if (mWay == 1){
            classchedulecard_week = "星期日";
        } else if (mWay == 2){
            classchedulecard_week = "星期一";
        } else if (mWay == 3){
            classchedulecard_week = "星期二";
        } else if (mWay == 4){
            classchedulecard_week = "星期三";
        } else if (mWay == 5){
            classchedulecard_week = "星期四";
        } else if (mWay == 6){
            classchedulecard_week = "星期五";
        } else if (mWay == 7){
            classchedulecard_week = "星期六";
        }
        SimpleDateFormat format = new SimpleDateFormat("a");
        TextView classchedulecard_weekNum = mView.findViewById(R.id.classchedulecard_weekNum);
        classchedulecard_weekNum.setText(classchedulecard_week);
        TextView classchedulecard_dataNum = mView.findViewById(R.id.classchedulecard_dataNum);
        classchedulecard_dataNum.setText(format1);
        TextView classchedulecard_time = mView.findViewById(R.id.classchedulecard_time);
        classchedulecard_time.setText(format.format(dateOld));
        LinearLayout classchedulecard_content = mView.findViewById(R.id.classchedulecard_content);
        classchedulecard_content.addView(mClasschedulecardView);
        recyclerView = mClasschedulecardView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mControlMainActivity));
        ModelGroupItemDecoration modelGroupItemDecoration = new ModelGroupItemDecoration<String,ClassBean>();
        modelGroupItemDecoration.setTextColor(getResources().getColor(R.color.black999999));
        modelGroupItemDecoration.setTextSize(getResources().getDimensionPixelSize(R.dimen.textsize12));
        recyclerView.addItemDecoration(modelGroupItemDecoration);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onYearChange(int year) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDateSelected(Calendar calendar, boolean isClick) {
        if (isClick){ //点击某一个日期
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); //设置时间格式
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
            java.util.Calendar cal = java.util.Calendar.getInstance();
            String data = calendar.toString();
            try {
                Date date = sdf.parse(data);
                cal.setTime(date);
                //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
                int dayWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);//获得传入日期是一个星期的第几天
                if (1 == dayWeek) {
                    cal.add(java.util.Calendar.DAY_OF_MONTH, -1);
                }
                cal.setFirstDayOfWeek(java.util.Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
                int day = cal.get(java.util.Calendar.DAY_OF_WEEK);//获得传入日期是一个星期的第几天
                cal.add(java.util.Calendar.DATE, cal.getFirstDayOfWeek() - day);//根据日历的规则，给传入日期减去星期几与一个星期第一天的差值
                LinkedHashMap<String, List<ClassBean>> map  = new LinkedHashMap<>();
                String Monday = sdf1.format(cal.getTime());
                System.out.println("所在周星期一的日期：" + Monday);
                map.put(Monday,null);
                cal.add(java.util.Calendar.DATE, 1);
                String two = sdf1.format(cal.getTime());
                map.put(two,null);
                cal.add(java.util.Calendar.DATE, 1);
                String three = sdf1.format(cal.getTime());
                map.put(three,null);
                cal.add(java.util.Calendar.DATE, 1);
                String four = sdf1.format(cal.getTime());
                map.put(four,null);
                cal.add(java.util.Calendar.DATE, 1);
                String five = sdf1.format(cal.getTime());
                map.put(five,null);
                cal.add(java.util.Calendar.DATE, 1);
                String six = sdf1.format(cal.getTime());
                map.put(six,null);
                cal.add(java.util.Calendar.DATE, 1);
                String Sunday = sdf1.format(cal.getTime());
                System.out.println("所在周星期日的日期：" + Sunday);
                map.put(Sunday,null);
                getQueryAllSchoolTimeTableFromOneStu(map,Monday + " 00:00:00",Sunday + " 24:00:00");
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(mControlMainActivity,"课程表信息获取失败",Toast.LENGTH_SHORT).show();
            }

        }
    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        return calendar;
    }

    private void getQueryAllSchoolTimeTableFromOneStu(LinkedHashMap<String, List<ClassBean>> map,String begin_time,String end_time){
        if (mControlMainActivity.mStuId.equals("")){
            Toast.makeText(mControlMainActivity,"您还没有安排课程",Toast.LENGTH_SHORT).show();
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        String strEntity = "{\"stu_id\":" + mControlMainActivity.mStuId + "," +
                "\"begin_time\":\"" + begin_time + "\"," +
                "\"end_time\":\"" + end_time + "\"}";
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.QueryAllSchoolTimeTableFromOneStu(body)
                .enqueue(new Callback<SchoolTimeTableBean>() {

                    @Override
                    public void onResponse(Call<SchoolTimeTableBean> call, Response<SchoolTimeTableBean> response) {
                        if (response == null){
                            Toast.makeText(mControlMainActivity,"查询课程表失败",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (response.body() == null){
                            Toast.makeText(mControlMainActivity,"查询课程表失败",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        SchoolTimeTableBean schoolTimeTableBean = response.body();
                        if (!HeaderInterceptor.IsErrorCode(schoolTimeTableBean.getCode(),"")){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (schoolTimeTableBean.code != 200){
                            Toast.makeText(mControlMainActivity,"查询课程表失败",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        List<SchoolTimeTableBean.SchoolTimeTableDataBean> schoolTimeTableDataBeans = schoolTimeTableBean.data;
                        if (schoolTimeTableDataBeans == null){
                            Toast.makeText(mControlMainActivity,"查询课程表失败",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        for (SchoolTimeTableBean.SchoolTimeTableDataBean schoolTimeTableDataBean:schoolTimeTableDataBeans) {
                            if (schoolTimeTableDataBean == null){
                                continue;
                            }
                            if (schoolTimeTableDataBean.begin_class_date == null){
                                continue;
                            }
                            String beginDate = new SimpleDateFormat("yyyy-MM-dd").format(schoolTimeTableDataBean.begin_class_date);
                            for (String key: map.keySet()) {
                                if (key == null){
                                    continue;
                                }
                                if (key.equals(beginDate)){
                                    List<ClassBean> classBeans = map.get(key);
                                    if (classBeans == null){
                                        classBeans = new ArrayList<>();
                                    }
                                    ClassBean classBean = new ClassBean();
                                    classBean.setClassTeacher(schoolTimeTableDataBean.trueName);
                                    classBean.setClassName(schoolTimeTableDataBean.ct_name);
                                    classBean.setClassTeacherImg(schoolTimeTableDataBean.head);
                                    classBean.setBegin_class_date(schoolTimeTableDataBean.begin_class_date);
                                    classBean.setEnd_time_datess(schoolTimeTableDataBean.end_time_datess);
                                    classBean.setCourse_times_id(schoolTimeTableDataBean.course_times_id);
                                    classBean.setId(schoolTimeTableDataBean.course_id);
                                    classBean.setStatus(schoolTimeTableDataBean.status);
                                    classBeans.add(classBean);
                                    map.put(key,classBeans);
                                    break;
                                }
                            }
                        }
                        LinkedHashMap<String, List<ClassBean>> map1 = new LinkedHashMap<>();
                        List<String> titles = new ArrayList<>();
                        List<Calendar> CalendarList = new ArrayList<>();
                        Date dateOld = new Date(System.currentTimeMillis()); // 根据long类型的毫秒数生命一个date类型的时间
                        String format1 = new SimpleDateFormat("yyyy-MM-dd").format(dateOld);
                        for (String key: map.keySet()) {
                            if (key == null) {
                                continue;
                            }
                            String date = "";
                            String day = "0";
                            String month = "1";
                            String year = "1949";
                            try {
                                Date data = stringToDate(key,"yyyy-mm-dd");
                                date = new SimpleDateFormat("yyyy年mm月dd日").format(data);
                                day = new SimpleDateFormat("dd").format(data);
                                month = new SimpleDateFormat("mm").format(data);
                                year = new SimpleDateFormat("yyyy").format(data);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            int count = 0;
                            if (map.get(key) != null){
                                count = map.get(key).size();
                                //为有课程的日子加上特殊颜色
                                CalendarList.add(getSchemeCalendar(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), getResources().getColor(R.color.holo_red_dark), "假"));
                                map1.put(date + ";今日课次：" + count,map.get(key));
                                titles.add(date + ";今日课次：" + count);
                            } else {
                                List<ClassBean> classBeans = new ArrayList<>();
                                ClassBean classBean = new ClassBean();
                                classBean.setClassTeacher("");
                                classBean.setClassName("null");
                                classBean.setClassTeacherImg("");
                                classBeans.add(classBean);
                                map1.put(date + ";今日课次：" + count,classBeans);
                                titles.add(date + ";今日课次：" + count);
                            }
                            if (format1.equals(key)){
                                TextView classchedulecard_classnum = mView.findViewById(R.id.classchedulecard_classnum);
                                classchedulecard_classnum.setText(count + "");
                            }
                        }
                        //此方法在巨大的数据量上不影响遍历性能，推荐使用
                        calendarView.setSchemeDate(CalendarList);
                        ModelClassAdapter modelClassAdapter =  new ModelClassAdapter(mControlMainActivity,map1,titles);
                        modelClassAdapter.setOnItemClickListener((position, itemId,item) -> {
                            if (item == null){
                                return;
                            }
                            if (item.getClassName().equals("null")){
                                return;
                            }
                            //点击每个课程信息的回调
                            if (item.getStatus().equals("进行中")) {
                                mControlMainActivity.LoginLiveOrPlayback(item.getCourse_times_id(), 2, PlayType.LIVE);
                            } else if (item.getStatus().equals("已结束")) {
                                mControlMainActivity.LoginLiveOrPlayback(item.getCourse_times_id(), 2, PlayType.PLAYBACK);
                            } else {
                                mControlMainActivity.LoginLiveOrPlayback(item.getCourse_times_id(), 2, PlayType.LIVE);
                            }
                        });
                        recyclerView.setAdapter( modelClassAdapter);
                        recyclerView.notifyDataSetChanged();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<SchoolTimeTableBean> call, Throwable t) {
                        Toast.makeText(mControlMainActivity,"查询课程表失败",Toast.LENGTH_SHORT).show();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                });
    }
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = formatter.parse(strTime);
        return date;
    }
    class SchoolTimeTableBean {
        private Integer code;
        private List<SchoolTimeTableDataBean> data;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public List<SchoolTimeTableDataBean> getData() {
            return data;
        }

        public void setData(List<SchoolTimeTableDataBean> data) {
            this.data = data;
        }

        class SchoolTimeTableDataBean {
            private String ct_name;
            private String trueName;
            private String head;
            private Integer course_times_id;
            private Integer course_id;
            private Integer js_user_id;
            private Date end_time_datess;
            private Date begin_class_date;
            private String status;

            public String getCt_name() {
                return ct_name;
            }

            public void setCt_name(String ct_name) {
                this.ct_name = ct_name;
            }

            public String getTrueName() {
                return trueName;
            }

            public void setTrueName(String trueName) {
                this.trueName = trueName;
            }

            public String getHead() {
                return head;
            }

            public void setHead(String head) {
                this.head = head;
            }

            public Integer getCourse_times_id() {
                return course_times_id;
            }

            public void setCourse_times_id(Integer course_times_id) {
                this.course_times_id = course_times_id;
            }

            public Integer getCourse_id() {
                return course_id;
            }

            public void setCourse_id(Integer course_id) {
                this.course_id = course_id;
            }

            public Integer getJs_user_id() {
                return js_user_id;
            }

            public void setJs_user_id(Integer js_user_id) {
                this.js_user_id = js_user_id;
            }

            public Date getEnd_time_datess() {
                return end_time_datess;
            }

            public void setEnd_time_datess(Date end_time_datess) {
                this.end_time_datess = end_time_datess;
            }

            public Date getBegin_class_date() {
                return begin_class_date;
            }

            public void setBegin_class_date(Date begin_class_date) {
                this.begin_class_date = begin_class_date;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }
        }
    }
    }