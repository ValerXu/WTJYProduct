package com.android.jwjy.wtjyproduct;

import android.annotation.SuppressLint;
import android.content.Context;
//import android.icu.text.SimpleDateFormat;
import java.text.SimpleDateFormat;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Created by dayuer on 19/7/2.
 * 订单支付功能
 */
public class ModelOrderDetails implements View.OnClickListener {
    private View mOrderBuyView,mOrderBankCardView,mOrderResultView,mOrderCouponChooseView;
    private ControlMainActivity mControlMainActivity = null;
    //当前选中的支付方式
    private String mCurrentPayType = "bankcard";
    //当前选中的选择优惠券标签(默认为可用优惠券)
    private String mCurrentmOrderCouponChooseTab = "use";
    private int mCurrentmOrderCouponChooseIndex = 1;
    private ControllerCustomDialog mCustomDialog = null;
    private ControllerCenterDialog mMyCouponDialog = null;
    private String mPage = "";
    private View modelOrderDetailsView = null;
    private int height = 1344;
    private int width = 720;
    private static final String TAG = "ModelOrderDetails";
    private ModelOrderDetailsInterface mModelOrderDetailsInterface = null;
    //选择的优惠券Id
//    private String mCouponId = "";
    private CouponBean.CouponDataListBean mCouponDataListBean;
    //支付宝支付回调
    private static final int ALISDK_PAY_FLAG = 1;
    private CourseInfo mCourseInfo = null;
    private CoursePacketInfo mCoursePacketInfo = null;
    private ModelMy.MyOrderlistBean.DataBean.ListBean mMyOrderListBean = null;
    //生成的订单编号和创建订单时间
    private String mOrderNum = "";
    private long mOrderTimeL = 0;
    private long mOrderInvalidTime = 30*60;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ALISDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    ModelAliPayResult payResult = new ModelAliPayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        HideAllLayout();
                        LinearLayout modeldetails_main = modelOrderDetailsView.findViewById(R.id.modeldetails_main);
                        View resultView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelorderpay_payresult1, null);
                        modeldetails_main.addView(resultView);
                        TextView orderpay_payresult = resultView.findViewById(R.id.orderpay_payresult);
                        orderpay_payresult.setText("订单成功");
                        ImageView orderpay_payresult_icon = resultView.findViewById(R.id.orderpay_payresult_icon);
                        orderpay_payresult_icon.setBackground(resultView.getResources().getDrawable(R.drawable.img_orderresult_success));
                        mControlMainActivity.onClickOrderResult();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        HideAllLayout();
                        LinearLayout modeldetails_main = modelOrderDetailsView.findViewById(R.id.modeldetails_main);
                        View resultView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelorderpay_payresult1, null);
                        modeldetails_main.addView(resultView);
                        TextView orderpay_payresult = resultView.findViewById(R.id.orderpay_payresult);
                        orderpay_payresult.setText("订单失败");
                        mControlMainActivity.onClickOrderResult();
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };
    private SmartRefreshLayout mSmart_modelorderpay_couponchoose;

    public View ModelOrderDetails(ModelOrderDetailsInterface modelOrderDetailsInterface, Context context, CourseInfo courseInfo, CoursePacketInfo coursePacketInfo,ModelMy.MyOrderlistBean.DataBean.ListBean mMyOrderListBean){
        mCourseInfo = courseInfo;
        mCoursePacketInfo = coursePacketInfo;
        this.mMyOrderListBean = mMyOrderListBean;
        mControlMainActivity = (ControlMainActivity) context;
        mModelOrderDetailsInterface = modelOrderDetailsInterface;
        DisplayMetrics dm = context.getResources().getDisplayMetrics(); //获取屏幕分辨率
        height = dm.heightPixels;
        width = dm.widthPixels;
        modelOrderDetailsView = LayoutInflater.from(context).inflate(R.layout.modelorderdetails, null);
        if (mMyOrderListBean != null){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date startTime = null;
            try {
                startTime = df.parse(mMyOrderListBean.getOrder_time());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date endTime = new Date(System.currentTimeMillis());
            long diff = startTime.getTime() - endTime.getTime();
            mOrderTimeL = diff / 1000;
            mOrderNum = mMyOrderListBean.getOrder_num();
        }
        mCouponDataListBean = null;
        CourseDetailsBuyInit();
        if (mMyOrderListBean == null){
            OrderBuy(); //获取订单编号
        }
        return modelOrderDetailsView;
    }

    public void HideAllLayout(){
        LinearLayout modeldetails_main = modelOrderDetailsView.findViewById(R.id.modeldetails_main);
        modeldetails_main.removeAllViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.orderpay_paytype_bankcard:{ //支付方式选择银行卡
                ImageView orderpay_paytype_bankcardicon = mOrderBuyView.findViewById(R.id.orderpay_paytype_bankcardicon);
                ImageView orderpay_paytype_alipayicon = mOrderBuyView.findViewById(R.id.orderpay_paytype_alipayicon);
                ImageView orderpay_paytype_wechaticon = mOrderBuyView.findViewById(R.id.orderpay_paytype_wechaticon);
                orderpay_paytype_bankcardicon.setBackground(mOrderBuyView.getResources().getDrawable(R.drawable.radiobutton_bluecircle));
                orderpay_paytype_alipayicon.setBackground(mOrderBuyView.getResources().getDrawable(R.drawable.radiobutton_graycircle));
                orderpay_paytype_wechaticon.setBackground(mOrderBuyView.getResources().getDrawable(R.drawable.radiobutton_graycircle));
                mCurrentPayType = "bankcard";
                break;
            }
            case R.id.orderpay_paytype_alipay:{ //支付方式选择支付宝
                ImageView orderpay_paytype_bankcardicon = mOrderBuyView.findViewById(R.id.orderpay_paytype_bankcardicon);
                ImageView orderpay_paytype_alipayicon = mOrderBuyView.findViewById(R.id.orderpay_paytype_alipayicon);
                ImageView orderpay_paytype_wechaticon = mOrderBuyView.findViewById(R.id.orderpay_paytype_wechaticon);
                orderpay_paytype_bankcardicon.setBackground(mOrderBuyView.getResources().getDrawable(R.drawable.radiobutton_graycircle));
                orderpay_paytype_alipayicon.setBackground(mOrderBuyView.getResources().getDrawable(R.drawable.radiobutton_bluecircle));
                orderpay_paytype_wechaticon.setBackground(mOrderBuyView.getResources().getDrawable(R.drawable.radiobutton_graycircle));
                mCurrentPayType = "alipay";
                break;
            }
            case R.id.orderpay_paytype_wechat:{ //支付方式选择微信
                ImageView orderpay_paytype_bankcardicon = mOrderBuyView.findViewById(R.id.orderpay_paytype_bankcardicon);
                ImageView orderpay_paytype_alipayicon = mOrderBuyView.findViewById(R.id.orderpay_paytype_alipayicon);
                ImageView orderpay_paytype_wechaticon = mOrderBuyView.findViewById(R.id.orderpay_paytype_wechaticon);
                orderpay_paytype_bankcardicon.setBackground(mOrderBuyView.getResources().getDrawable(R.drawable.radiobutton_graycircle));
                orderpay_paytype_alipayicon.setBackground(mOrderBuyView.getResources().getDrawable(R.drawable.radiobutton_graycircle));
                orderpay_paytype_wechaticon.setBackground(mOrderBuyView.getResources().getDrawable(R.drawable.radiobutton_bluecircle));
                mCurrentPayType = "wechat";
                break;
            }
            case R.id.orderpay_immediatepayment: {//点击订单界面的立即支付按钮
                if (mCurrentPayType.equals("bankcard")){ //银行卡支付
                    mControlMainActivity.Page_OrderDetailsBankCard();
                    CourseDetailsBankCardInit();
                    return;
                } else if (mCurrentPayType.equals("alipay")){ //支付宝支付
                    OrderRepay("支付宝APP");
                } else if (mCurrentPayType.equals("wechat")){ //微信支付
                    OrderRepay("微信APP");
                }
                break;
            }
            case R.id.orderpay_preferentialnumber_layout:{ //点击选择优惠券
                mControlMainActivity.Page_OrderDetailsChooseCoupon();
                CourseDetailsOrderCouponChooseInit();
                break;
            }
            case R.id.orderpay_couponchoose_tab_use:{ //订单-选择优惠券-可用优惠券
                if (!mCurrentmOrderCouponChooseTab.equals("use")) {
                    ImageView orderpay_couponchoose_cursor1 = mOrderCouponChooseView.findViewById(R.id.orderpay_couponchoose_cursor1);
                    Animation animation = new TranslateAnimation(( mCurrentmOrderCouponChooseIndex - 1)  * width / 2,0 , 0, 0);
                    animation.setFillAfter(true);// True:图片停在动画结束位置
                    animation.setDuration(200);
                    orderpay_couponchoose_cursor1.startAnimation(animation);
                    TextView orderpay_couponchoose_tab_use = mOrderCouponChooseView.findViewById(R.id.orderpay_couponchoose_tab_use);
                    TextView orderpay_couponchoose_tab_unused = mOrderCouponChooseView.findViewById(R.id.orderpay_couponchoose_tab_unused);
                    orderpay_couponchoose_tab_use.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mOrderCouponChooseView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                    orderpay_couponchoose_tab_unused.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mOrderCouponChooseView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                }
                mCurrentmOrderCouponChooseIndex = 1;
                mCurrentmOrderCouponChooseTab = "use";
                LinearLayout orderpay_couponchoose_main_content = mOrderCouponChooseView.findViewById(R.id.orderpay_couponchoose_main_content);
                orderpay_couponchoose_main_content.removeAllViews();
                if (mCourseInfo != null) {
                    QueryDiscountFromOneStuCourse(true, orderpay_couponchoose_main_content);
                } else if (mCoursePacketInfo != null){
                    QueryDiscountFromOneStuCoursePacket(true, orderpay_couponchoose_main_content);
                } else if (mMyOrderListBean != null){
                    QueryDiscountFromOneStuRepay(true, orderpay_couponchoose_main_content);
                }
                break;
            }
            case R.id.orderpay_couponchoose_tab_unused:{ //订单-选择优惠券-不可用优惠券
                if (!mCurrentmOrderCouponChooseTab.equals("unuse")) {
                    ImageView orderpay_couponchoose_cursor1 = mOrderCouponChooseView.findViewById(R.id.orderpay_couponchoose_cursor1);
                    Animation animation = new TranslateAnimation(( mCurrentmOrderCouponChooseIndex - 1)  * width / 2,width / 2 , 0, 0);
                    animation.setFillAfter(true);// True:图片停在动画结束位置
                    animation.setDuration(200);
                    orderpay_couponchoose_cursor1.startAnimation(animation);
                    TextView orderpay_couponchoose_tab_use = mOrderCouponChooseView.findViewById(R.id.orderpay_couponchoose_tab_use);
                    TextView orderpay_couponchoose_tab_unused = mOrderCouponChooseView.findViewById(R.id.orderpay_couponchoose_tab_unused);
                    orderpay_couponchoose_tab_use.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mOrderCouponChooseView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    orderpay_couponchoose_tab_unused.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mOrderCouponChooseView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                }
                mCurrentmOrderCouponChooseIndex = 2;
                mCurrentmOrderCouponChooseTab = "unuse";
                LinearLayout orderpay_couponchoose_main_content = mOrderCouponChooseView.findViewById(R.id.orderpay_couponchoose_main_content);
                orderpay_couponchoose_main_content.removeAllViews();
                if (mCourseInfo != null) {
                    QueryDiscountFromOneStuCourse(false, orderpay_couponchoose_main_content);
                } else if (mCoursePacketInfo != null){
                    QueryDiscountFromOneStuCoursePacket(false, orderpay_couponchoose_main_content);
                } else if (mMyOrderListBean != null){
                    QueryDiscountFromOneStuRepay(false, orderpay_couponchoose_main_content);
                }
                break;
            }
            case R.id.orderpay_couponchoose_main_return_button1:{
                mCouponDataListBean = null;
                CourseDetailsBuyInit();
                mControlMainActivity.Page_OrderDetailsChooseCouponReturn();
                break;
            }
            case R.id.orderpay_couponchoose_main_exchange:{
                //点击兑换弹出兑换对话框
                View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.dialog_sure_cancel1, null);
                mMyCouponDialog = new ControllerCenterDialog(mControlMainActivity, 0, 0, view, R.style.DialogTheme);
                mMyCouponDialog.setCancelable(true);
                mMyCouponDialog.show();
                TextView button_cancel = view.findViewById(R.id.button_cancel);
                button_cancel.setOnClickListener(View->{
                    mMyCouponDialog.cancel();
                });
                TextView button_sure = view.findViewById(R.id.button_sure);
                button_sure.setOnClickListener(View->{
                    //开始兑换优惠码
                    EditText dialog_content = view.findViewById(R.id.dialog_content);
                    if (dialog_content.getText().toString().equals("")){
                        Toast.makeText(mControlMainActivity,"兑换码不允许为空",Toast.LENGTH_LONG).show();
                        return;
                    }
                    CheckBeforeExchangingCoupons(dialog_content.getText().toString());
                });
                break;
            }
            case R.id.orderpay_bankcard_return_button1:{ //银行卡-返回
                CourseDetailsBuyInit();
                mControlMainActivity.Page_OrderDetailsBankCardReturn();
                break;
            }
            default:
                break;
        }
    }
    //购买详情界面初始化
    private void CourseDetailsBuyInit(){
        mPage = "OrderDetailsBuy";
        HideAllLayout();
        LinearLayout modeldetails_main = modelOrderDetailsView.findViewById(R.id.modeldetails_main);
//        if (mOrderBuyView == null) {
        mOrderBuyView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelorderpay_main, null);
        LinearLayout orderpay_paytype_bankcard = mOrderBuyView.findViewById(R.id.orderpay_paytype_bankcard);
        LinearLayout orderpay_paytype_alipay = mOrderBuyView.findViewById(R.id.orderpay_paytype_alipay);
        LinearLayout orderpay_paytype_wechat = mOrderBuyView.findViewById(R.id.orderpay_paytype_wechat);
        TextView orderpay_immediatepayment = mOrderBuyView.findViewById(R.id.orderpay_immediatepayment);
        LinearLayout orderpay_preferentialnumber_layout = mOrderBuyView.findViewById(R.id.orderpay_preferentialnumber_layout);
        orderpay_preferentialnumber_layout.setOnClickListener(this);
        orderpay_immediatepayment.setOnClickListener(this);
        orderpay_paytype_bankcard.setOnClickListener(this);
        orderpay_paytype_alipay.setOnClickListener(this);
        orderpay_paytype_wechat.setOnClickListener(this);
//            ImageView course_question_add_layout_return_button1 = mCourseBuyView.findViewById(R.id.course_question_add_layout_return_button1);
//            course_question_add_layout_return_button1.setOnClickListener(this);
//        }
        //默认为银行卡支付
        ImageView orderpay_paytype_bankcardicon = mOrderBuyView.findViewById(R.id.orderpay_paytype_bankcardicon);
        ImageView orderpay_paytype_alipayicon = mOrderBuyView.findViewById(R.id.orderpay_paytype_alipayicon);
        ImageView orderpay_paytype_wechaticon = mOrderBuyView.findViewById(R.id.orderpay_paytype_wechaticon);
        orderpay_paytype_bankcardicon.setBackground(mOrderBuyView.getResources().getDrawable(R.drawable.radiobutton_bluecircle));
        orderpay_paytype_alipayicon.setBackground(mOrderBuyView.getResources().getDrawable(R.drawable.radiobutton_graycircle));
        orderpay_paytype_wechaticon.setBackground(mOrderBuyView.getResources().getDrawable(R.drawable.radiobutton_graycircle));
        //订单编号
        TextView orderpay_ordernumber = mOrderBuyView.findViewById(R.id.orderpay_ordernumber);
        orderpay_ordernumber.setText(mOrderNum);
        mCurrentPayType = "bankcard";
        float discountprice = 0; //优惠金额
        TextView orderpay_preferentialnumber = mOrderBuyView.findViewById(R.id.orderpay_preferentialnumber);
        if (mCouponDataListBean != null){
            orderpay_preferentialnumber.setText("已选择1张");
            //计算优惠金额
            if (mCouponDataListBean.preferential_way.equals("满减")){
                String dc_denominationS[] = mCouponDataListBean.dc_denomination.split(",");//满？？，减？？
                if (dc_denominationS.length == 2){
                    discountprice = Float.valueOf(dc_denominationS[1]);
                }
            } else if (mCouponDataListBean.preferential_way.equals("折扣")){
                if (mCoursePacketInfo != null){
                    if (mCoursePacketInfo.mCoursePacketPrice != null && mCouponDataListBean.dc_denomination != null){
                        discountprice = Float.valueOf(mCouponDataListBean.dc_denomination) * Float.valueOf(mCoursePacketInfo.mCoursePacketPrice);
                    }
                } else if (mCourseInfo != null) {
                    if (mCouponDataListBean.dc_denomination != null && mCourseInfo.mCoursePrice != null) {
                        discountprice = Float.valueOf(mCouponDataListBean.dc_denomination) * Float.valueOf(mCourseInfo.mCoursePrice);
                    }
                }else if (mMyOrderListBean != null){
                    if (mCouponDataListBean.dc_denomination != null ) {
                        discountprice = (float) (Float.valueOf(mCouponDataListBean.dc_denomination) * mMyOrderListBean.getProduct_price());
                    }
                }
            } else if (mCouponDataListBean.preferential_way.equals("抵现")){
                discountprice = Float.valueOf(mCouponDataListBean.dc_denomination);
            }
            TextView orderpay_coursediscountprice = mOrderBuyView.findViewById(R.id.orderpay_coursediscountprice);
            orderpay_coursediscountprice.setText(String.valueOf(discountprice));
        } else {
            orderpay_preferentialnumber.setText("查看");
        }
        //课程订单还是课程包订单
        if (mCoursePacketInfo != null){
            TextView orderpay_productname = mOrderBuyView.findViewById(R.id.orderpay_productname);
            orderpay_productname.setText(mCoursePacketInfo.mCoursePacketName);
            TextView orderpay_productprice = mOrderBuyView.findViewById(R.id.orderpay_productprice);
            orderpay_productprice.setText(mCoursePacketInfo.mCoursePacketPrice);
            TextView orderpay_courseprice = mOrderBuyView.findViewById(R.id.orderpay_courseprice);
            orderpay_courseprice.setText(mCoursePacketInfo.mCoursePacketPrice);
            TextView orderpay_pay = mOrderBuyView.findViewById(R.id.orderpay_pay);
            if (mCoursePacketInfo.mCoursePacketPrice != null) {
                orderpay_pay.setText(String.valueOf(Float.valueOf(mCoursePacketInfo.mCoursePacketPrice) - discountprice));
            }
        } else if (mCourseInfo != null) {
            TextView orderpay_productname = mOrderBuyView.findViewById(R.id.orderpay_productname);
            orderpay_productname.setText(mCourseInfo.mCourseName);
            TextView orderpay_productprice = mOrderBuyView.findViewById(R.id.orderpay_productprice);
            orderpay_productprice.setText(mCourseInfo.mCoursePrice);
            TextView orderpay_courseprice = mOrderBuyView.findViewById(R.id.orderpay_courseprice);
            orderpay_courseprice.setText(mCourseInfo.mCoursePrice);
            TextView orderpay_pay = mOrderBuyView.findViewById(R.id.orderpay_pay);
            if (mCourseInfo.mCoursePrice != null) {
                if (mCourseInfo.mCoursePrice.equals("免费")){
                    mCourseInfo.mCoursePrice="0";
                }
                orderpay_pay.setText(String.valueOf(Float.valueOf(mCourseInfo.mCoursePrice) - discountprice));
            }
        } else if (mMyOrderListBean != null) {
            TextView orderpay_productname = mOrderBuyView.findViewById(R.id.orderpay_productname);
            orderpay_productname.setText(mMyOrderListBean.getProduct_name());
            TextView orderpay_productprice = mOrderBuyView.findViewById(R.id.orderpay_productprice);
            orderpay_productprice.setText(mMyOrderListBean.getProduct_price() + "");
            TextView orderpay_courseprice = mOrderBuyView.findViewById(R.id.orderpay_courseprice);
            orderpay_courseprice.setText(mMyOrderListBean.getProduct_price() + "");
            TextView orderpay_pay = mOrderBuyView.findViewById(R.id.orderpay_pay);
            orderpay_pay.setText(String.valueOf(mMyOrderListBean.getProduct_price() - discountprice));
        }
        modeldetails_main.addView(mOrderBuyView);
    }
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mOrderTimeL --;
            String formatLongToTimeStr = formatLongToTimeStr(mOrderTimeL);
            String[] split = formatLongToTimeStr.split("：");
            TextView orderpay_invalid2_hour = mOrderBuyView.findViewById(R.id.orderpay_invalid2_hour);
            TextView orderpay_invalid2_minute = mOrderBuyView.findViewById(R.id.orderpay_invalid2_minute);
            TextView orderpay_invalid2_second = mOrderBuyView.findViewById(R.id.orderpay_invalid2_second);
            for (int i = 0; i < split.length; i++) {
                if(i == 0){
                    orderpay_invalid2_hour.setText(split[0]+"小时");
                }
                if(i == 1){
                    orderpay_invalid2_minute.setText(split[1]+"分钟");
                }
                if(i == 2){
                    orderpay_invalid2_second.setText(split[2]+"秒");
                }

            }
            if(mOrderTimeL > 0){
                handler.postDelayed(this, 1000);
            } else {
                //取消订单  返回首页
                HideAllLayout();
                LinearLayout modeldetails_main = modelOrderDetailsView.findViewById(R.id.modeldetails_main);
                View resultView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelorderpay_payresult1, null);
                modeldetails_main.addView(resultView);
                TextView orderpay_payresult = resultView.findViewById(R.id.orderpay_payresult);
                orderpay_payresult.setText("订单超时");
                mControlMainActivity.onClickOrderResult();
            }
        }
    };

    public String formatLongToTimeStr(Long l) {
        int hour = 0;
        int minute = 0;
        int second = 0;
        second = l.intValue() ;
        if (second > 60) {
            minute = second / 60;   //取整
            second = second % 60;   //取余
        }

        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        String strtime = hour+"："+minute+"："+second;
        return strtime;

    }

    private void OrderDetailsInit(String orderNum,String discountAmount,String orderInvalidTime){
        TextView orderpay_ordernumber = mOrderBuyView.findViewById(R.id.orderpay_ordernumber);
        orderpay_ordernumber.setText(orderNum);
        TextView orderpay_coursediscountprice = mOrderBuyView.findViewById(R.id.orderpay_coursediscountprice);
        orderpay_coursediscountprice.setText(discountAmount);
    }

    //订单支付-银行卡支付界面
    private void CourseDetailsBankCardInit(){
        mPage = "CourseDetailsBankCard";
        HideAllLayout();
        LinearLayout modeldetails_main = modelOrderDetailsView.findViewById(R.id.modeldetails_main);
//        if (mOrderBankCardView == null) {
        mOrderBankCardView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelorderpay_bankcard, null);
        ImageView orderpay_bankcard_return_button1 = mOrderBankCardView.findViewById(R.id.orderpay_bankcard_return_button1);
        orderpay_bankcard_return_button1.setOnClickListener(this);
//        }
        modeldetails_main.addView(mOrderBankCardView);
    }
    //支付结果界面
    private void CourseDetailsOrderResultInit(){
        mPage = "CourseDetailsOrderResult";
        HideAllLayout();
        LinearLayout modeldetails_main = modelOrderDetailsView.findViewById(R.id.modeldetails_main);
//        if (mOrderResultView == null) {
        mOrderResultView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelorderpay_payresult, null);
//        }
        modeldetails_main.addView(mOrderResultView);
    }
    //支付-选择优惠券界面
    private void CourseDetailsOrderCouponChooseInit() {
        mPage = "OrderCouponChoose";
        HideAllLayout();
        LinearLayout modeldetails_main = modelOrderDetailsView.findViewById(R.id.modeldetails_main);
//        if (mOrderCouponChooseView == null) {
        mOrderCouponChooseView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelorderpay_couponchoose, null);
        TextView orderpay_couponchoose_tab_use = mOrderCouponChooseView.findViewById(R.id.orderpay_couponchoose_tab_use);
        TextView orderpay_couponchoose_tab_unused = mOrderCouponChooseView.findViewById(R.id.orderpay_couponchoose_tab_unused);
        ImageView orderpay_couponchoose_main_return_button1 = mOrderCouponChooseView.findViewById(R.id.orderpay_couponchoose_main_return_button1);
        TextView orderpay_couponchoose_main_exchange = mOrderCouponChooseView.findViewById(R.id.orderpay_couponchoose_main_exchange);
        orderpay_couponchoose_main_exchange.setOnClickListener(this);
        orderpay_couponchoose_main_return_button1.setOnClickListener(this);
        orderpay_couponchoose_tab_unused.setOnClickListener(this);
        orderpay_couponchoose_tab_use.setOnClickListener(this);
        //下拉刷新     订单的刷新控件
        mSmart_modelorderpay_couponchoose = mOrderCouponChooseView.findViewById(R.id.Smart_modelorderpay_couponchoose);
        mSmart_modelorderpay_couponchoose.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

                mSmart_modelorderpay_couponchoose.finishLoadMore();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {

                mSmart_modelorderpay_couponchoose.finishRefresh();
            }
        });
//        }
        ImageView orderpay_couponchoose_cursor1 = mOrderCouponChooseView.findViewById(R.id.orderpay_couponchoose_cursor1);
        int x = width / 4 - mOrderCouponChooseView.getResources().getDimensionPixelSize(R.dimen.dp18) / 2;
        orderpay_couponchoose_cursor1.setX(x);
        //默认选中的为课程
        mCurrentmOrderCouponChooseIndex = 1;
        mCurrentmOrderCouponChooseTab = "use";
//        TextView orderpay_couponchoose_tab_use = mOrderCouponChooseView.findViewById(R.id.orderpay_couponchoose_tab_use);
//        TextView orderpay_couponchoose_tab_unused = mOrderCouponChooseView.findViewById(R.id.orderpay_couponchoose_tab_unused);
        orderpay_couponchoose_tab_use.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mOrderCouponChooseView.getResources().getDimensionPixelSize(R.dimen.textsize18));
        orderpay_couponchoose_tab_unused.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mOrderCouponChooseView.getResources().getDimensionPixelSize(R.dimen.textsize16));
        LinearLayout orderpay_couponchoose_main_content = mOrderCouponChooseView.findViewById(R.id.orderpay_couponchoose_main_content);
        orderpay_couponchoose_main_content.removeAllViews();
        modeldetails_main.addView(mOrderCouponChooseView);
        if (mCourseInfo != null) {
            QueryDiscountFromOneStuCourse(true, orderpay_couponchoose_main_content);
        } else if (mCoursePacketInfo != null){
            QueryDiscountFromOneStuCoursePacket(true, orderpay_couponchoose_main_content);
        } else if (mMyOrderListBean != null){
            QueryDiscountFromOneStuRepay(true, orderpay_couponchoose_main_content);
        }
    }

    public void onClickOrderDetailsReturn(){
        mModelOrderDetailsInterface.onRecive();
    }

    public void onClickOrderDetailsChooseCouponReturn(){
        mCouponDataListBean = null;
        CourseDetailsBuyInit();
    }

    public void onClickOrderDetailsBankCardReturn(){
        CourseDetailsBuyInit();
    }

//    public void WxChatAPP() {
//        OrderRepay();
////        final IWXAPI wxapi = WXAPIFactory.createWXAPI(mControlMainActivity, WeiXinConstants.APP_ID,false);
////        PayReq req = new PayReq();
////        req.appId = WeiXinConstants.APP_ID;
////        req.partnerId = "1562130261";
////        req.prepayId = "wx27180155091495e5f7677f121369940900";
////        req.packageValue = "Sign=WXPay";
////        req.nonceStr = "Bz6sPpRlEXkGR5Wi";
////        long time = new Date().getTime();
////        req.timeStamp = String.valueOf(time / 1000);
//////        req.extData = "app data"; // optional
////        Map<String, String> data = new HashMap<>();
////        data.put("appid",  req.appId);
////        data.put("partnerid",req.partnerId);
////        data.put("prepayid", req.prepayId);
////        data.put("noncestr", req.nonceStr);
////        data.put("timestamp", req.timeStamp);
////        data.put("package", req.packageValue);
////        try {
////            req.sign = generateSignature(data,"huozhongeduhuozhongedu2019052216");
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////        boolean result = wxapi.sendReq(req);
//    }

    public static String getMd5Value(String sSecret) {
        try {
            MessageDigest bmd5 = MessageDigest.getInstance("MD5");
            bmd5.update(sSecret.getBytes());
            int i;
            StringBuffer buf = new StringBuffer();
            byte[] b = bmd5.digest();// 加密
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String generateSignature(final Map<String, String> data, String key) throws Exception {
        //System.out.println(data);
        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k.equals("sign")) {
                continue;
            }
            if (data.get(k).trim().length() > 0) // 参数值为空，则不参与签名
                sb.append(k).append("=").append(data.get(k).trim()).append("&");
        }
        sb.append("key=").append(key);
//        String appSign = getMessageDigest(sb.toString()).toUpperCase();
//        return appSign;
        return getMd5Value(sb.toString()).toUpperCase();
    }

    //订单编号
    private void OrderBuy(){
        if (mCourseInfo == null && mCoursePacketInfo == null){
            return;
        }
        if (mControlMainActivity.mStuId.equals("")){
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
        HashMap<String, String> paramsMap = new HashMap<>();
        int productId = 0;
        if (mCourseInfo != null){
            paramsMap.put("tf_course", "课程");
            productId = Integer.parseInt(mCourseInfo.mCourseId);
        } else if (mCoursePacketInfo != null){
            paramsMap.put("tf_course", "课程包");
            productId = Integer.parseInt(mCoursePacketInfo.mCoursePacketId);
        }
        paramsMap.put("order_resource", "安卓APP");
        String strEntity = gson.toJson(paramsMap);
        HashMap<String, Integer> paramsMap1 = new HashMap<>();
        //course_package_id参数id    文件的参数id
        paramsMap1.put("CPC_id", productId);
        paramsMap1.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.querygenerateOrderNumber(body)
                .enqueue(new Callback<BuyCode>() {
                    @Override
                    public void onResponse(Call<BuyCode> call, Response<BuyCode> response) {
                        BuyCode body1 = response.body();
                        int code = body1.getCode();
                        if (!HeaderInterceptor.IsErrorCode(code,"")){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (code != 200) {
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (body1.getData() == null) {
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mOrderNum = body1.getData();
                        mOrderTimeL = mOrderInvalidTime;
                        handler.postDelayed(runnable, 0);
                        CourseDetailsBuyInit();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<BuyCode> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage()+"" );
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //订单-支付
    private void OrderRepay(String type){
        if (mControlMainActivity.mStuId.equals("")){
            HideAllLayout();
            LinearLayout modeldetails_main = modelOrderDetailsView.findViewById(R.id.modeldetails_main);
            View resultView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelorderpay_payresult1, null);
            modeldetails_main.addView(resultView);
            TextView orderpay_payresult = resultView.findViewById(R.id.orderpay_payresult);
            orderpay_payresult.setText("订单失败");
            mControlMainActivity.onClickOrderResult();
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        String strEntity = "{\n" +
                "    \"outTradeNo\":\"" + mOrderNum + "\"," +
                "    \"stu_id\":" + mControlMainActivity.mStuId + "," ;
        if (mCouponDataListBean != null) {
            strEntity = strEntity + "\"small_discount_id\":" + mCouponDataListBean.small_discount_id + ",";
        } else {
            strEntity = strEntity + "\"small_discount_id\":null,";
        }
        strEntity = strEntity + "\"type\":\"" + type + "\"}";
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        if (type.equals("支付宝APP")){
            modelObservableInterface.orderRepayAli(body)
                    .enqueue(new Callback<ModelObservableInterface.BaseBean1>() {
                        @Override
                        public void onResponse(Call<ModelObservableInterface.BaseBean1> call, Response<ModelObservableInterface.BaseBean1> response) {
                            if (response == null){
                                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                Toast.makeText(mControlMainActivity,"支付失败",Toast.LENGTH_SHORT).show();
                                HideAllLayout();
                                LinearLayout modeldetails_main = modelOrderDetailsView.findViewById(R.id.modeldetails_main);
                                View resultView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelorderpay_payresult1, null);
                                modeldetails_main.addView(resultView);
                                TextView orderpay_payresult = resultView.findViewById(R.id.orderpay_payresult);
                                orderpay_payresult.setText("订单失败");
                                mControlMainActivity.onClickOrderResult();
                                return;
                            }
                            ModelObservableInterface.BaseBean1 body1 = response.body();
                            if (body1 == null){
                                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                Toast.makeText(mControlMainActivity,"支付失败",Toast.LENGTH_SHORT).show();
                                HideAllLayout();
                                LinearLayout modeldetails_main = modelOrderDetailsView.findViewById(R.id.modeldetails_main);
                                View resultView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelorderpay_payresult1, null);
                                modeldetails_main.addView(resultView);
                                TextView orderpay_payresult = resultView.findViewById(R.id.orderpay_payresult);
                                orderpay_payresult.setText("订单失败");
                                mControlMainActivity.onClickOrderResult();
                                return;
                            }
                            if (!HeaderInterceptor.IsErrorCode(response.code(),response.message())){
                                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                HideAllLayout();
                                LinearLayout modeldetails_main = modelOrderDetailsView.findViewById(R.id.modeldetails_main);
                                View resultView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelorderpay_payresult1, null);
                                modeldetails_main.addView(resultView);
                                TextView orderpay_payresult = resultView.findViewById(R.id.orderpay_payresult);
                                orderpay_payresult.setText("订单失败");
                                mControlMainActivity.onClickOrderResult();
                                return;
                            }
                            int code = body1.getErrorCode();
                            if (code != 200) {
                                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                Toast.makeText(mControlMainActivity,body1.getErrorMsg(),Toast.LENGTH_SHORT).show();
                                HideAllLayout();
                                LinearLayout modeldetails_main = modelOrderDetailsView.findViewById(R.id.modeldetails_main);
                                View resultView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelorderpay_payresult1, null);
                                modeldetails_main.addView(resultView);
                                TextView orderpay_payresult = resultView.findViewById(R.id.orderpay_payresult);
                                orderpay_payresult.setText("订单失败");
                                mControlMainActivity.onClickOrderResult();
                                return;
                            }
                            String data = body1.getData();
                            if (data == null){
                                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                HideAllLayout();
                                LinearLayout modeldetails_main = modelOrderDetailsView.findViewById(R.id.modeldetails_main);
                                View resultView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelorderpay_payresult1, null);
                                modeldetails_main.addView(resultView);
                                TextView orderpay_payresult = resultView.findViewById(R.id.orderpay_payresult);
                                orderpay_payresult.setText("订单失败");
                                mControlMainActivity.onClickOrderResult();
                                return;
                            }
                            final Runnable payRunnable = () -> {
                                PayTask alipay = new PayTask(mControlMainActivity);
                                //支付信息由服务器生成orderInfo  以下只是测试数据
//                                String orderInfo = "alipay_sdk=alipay-sdk-java-3.1.0&app_id=2019120469668090&biz_content=%7B%22out_trade_no%22%3A%223%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22subject%22%3A%22%E6%A0%87%E9%A2%98%22%2C%22timeout_express%22%3A%2230h%22%2C%22total_amount%22%3A%220.01%22%7D&charset=utf-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2F111.229.55.52%3A8080%2Fhzedu%2Forder%2Fzhifubaoyibu&sign=MIcxkv7KuEPRv%2BNHg88iX54zT8olfRWPUUFAHc0eha4wVY1rSnsdiLAMotqhwCTfGQ8ywNrEzZPImMhrRJzANShC4JPGNtV2faQYEb10NugIS7llPEJ8CofzCbGZnJ1lF833jL0DchaUtqGEWRii4tQ77%2F3w1lAZEvJcaR7MGGXZTO%2Fy4BiSR43o6y9wVAw68%2B5nEeYF0uGvpKn%2FYuHg9v5FuriM%2FfkHe4Mz%2BNI%2B52geLlAPrb4cbHgFI7YG34MMa1RV%2BcZ3NMnwA6QWelmFdrL4RjK%2Fha7UR5snW7um1km1UE4UttJf4XyHgmmQKywO2Tzgh4kgesj8FU6C4%2BzHTg%3D%3D&sign_type=RSA2&timestamp=2019-12-31+17%3A57%3A29&version=1.0";
                                Map<String, String> result = alipay.payV2(data, true);
                                Log.i("msp", result.toString());

                                Message msg = new Message();
                                msg.what = ALISDK_PAY_FLAG;
                                msg.obj = result;
                                mHandler.sendMessage(msg);
                            };
                            // 必须异步调用
                            Thread payThread = new Thread(payRunnable);
                            payThread.start();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        }

                        @Override
                        public void onFailure(Call<ModelObservableInterface.BaseBean1> call, Throwable t) {
                            Log.e(TAG, "onFailure: "+t.getMessage()+"" );
                            Toast.makeText(mControlMainActivity,"支付失败",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            HideAllLayout();
                            LinearLayout modeldetails_main = modelOrderDetailsView.findViewById(R.id.modeldetails_main);
                            View resultView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelorderpay_payresult1, null);
                            modeldetails_main.addView(resultView);
                            TextView orderpay_payresult = resultView.findViewById(R.id.orderpay_payresult);
                            orderpay_payresult.setText("订单失败");
                            mControlMainActivity.onClickOrderResult();
                        }
                    });
        } else if (type.equals("微信APP")){
            modelObservableInterface.orderRepay(body)
                    .enqueue(new Callback<ModelObservableInterface.BaseBean>() {
                        @Override
                        public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                            if (response == null){
                                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                Toast.makeText(mControlMainActivity,"支付失败",Toast.LENGTH_SHORT).show();
                                HideAllLayout();
                                LinearLayout modeldetails_main = modelOrderDetailsView.findViewById(R.id.modeldetails_main);
                                View resultView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelorderpay_payresult1, null);
                                modeldetails_main.addView(resultView);
                                TextView orderpay_payresult = resultView.findViewById(R.id.orderpay_payresult);
                                orderpay_payresult.setText("订单失败");
                                mControlMainActivity.onClickOrderResult();
                                return;
                            }
                            if (!HeaderInterceptor.IsErrorCode(response.code(),response.message())){
                                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                HideAllLayout();
                                LinearLayout modeldetails_main = modelOrderDetailsView.findViewById(R.id.modeldetails_main);
                                View resultView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelorderpay_payresult1, null);
                                modeldetails_main.addView(resultView);
                                TextView orderpay_payresult = resultView.findViewById(R.id.orderpay_payresult);
                                orderpay_payresult.setText("订单失败");
                                mControlMainActivity.onClickOrderResult();
                                return;
                            }
                            ModelObservableInterface.BaseBean body1 = response.body();
                            if (body1 == null){
                                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                Toast.makeText(mControlMainActivity,"支付失败",Toast.LENGTH_SHORT).show();
                                HideAllLayout();
                                LinearLayout modeldetails_main = modelOrderDetailsView.findViewById(R.id.modeldetails_main);
                                View resultView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelorderpay_payresult1, null);
                                modeldetails_main.addView(resultView);
                                TextView orderpay_payresult = resultView.findViewById(R.id.orderpay_payresult);
                                orderpay_payresult.setText("订单失败");
                                mControlMainActivity.onClickOrderResult();
                                return;
                            }
                            int code = body1.getErrorCode();
                            if (code != 200) {
                                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                Toast.makeText(mControlMainActivity,body1.getErrorMsg(),Toast.LENGTH_SHORT).show();
                                HideAllLayout();
                                LinearLayout modeldetails_main = modelOrderDetailsView.findViewById(R.id.modeldetails_main);
                                View resultView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelorderpay_payresult1, null);
                                modeldetails_main.addView(resultView);
                                TextView orderpay_payresult = resultView.findViewById(R.id.orderpay_payresult);
                                orderpay_payresult.setText("订单失败");
                                mControlMainActivity.onClickOrderResult();
                                return;
                            }
                            Map<String,Object> data = body1.getData();
                            if (data == null){
                                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                HideAllLayout();
                                LinearLayout modeldetails_main = modelOrderDetailsView.findViewById(R.id.modeldetails_main);
                                View resultView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelorderpay_payresult1, null);
                                modeldetails_main.addView(resultView);
                                TextView orderpay_payresult = resultView.findViewById(R.id.orderpay_payresult);
                                orderpay_payresult.setText("订单失败");
                                mControlMainActivity.onClickOrderResult();
                                return;
                            }
                            final IWXAPI wxapi = WXAPIFactory.createWXAPI(mControlMainActivity, (String) data.get("appid"),false);
                            PayReq req = new PayReq();
                            req.appId = (String) data.get("appid");
                            req.partnerId = (String) data.get("mch_id");
                            req.prepayId = (String) data.get("prepay_id");
                            req.packageValue = "Sign=WXPay";
                            req.nonceStr = (String) data.get("nonce_str");
                            long time = new Date().getTime();
                            req.timeStamp = String.valueOf(time / 1000);
                            //        req.extData = "app data"; // optional
//                            Map<String, String> data1 = new HashMap<>();
//                            data1.put("appid",  req.appId);
//                            data1.put("partnerid",req.partnerId);
//                            data1.put("prepayid", req.prepayId);
//                            data1.put("noncestr", req.nonceStr);
//                            data1.put("timestamp", req.timeStamp);
//                            data1.put("package", req.packageValue);
//                            try {
//                                req.sign = generateSignature(data1,"huozhongeduhuozhongedu2019052216");
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
                            req.sign = (String) data.get("sign");
                            boolean result = wxapi.sendReq(req);
                            if (!result){
                                Toast.makeText(mControlMainActivity,"支付失败",Toast.LENGTH_SHORT).show();
                            } else {
                                mControlMainActivity.onClickOrderBuyReturn();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        }

                        @Override
                        public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                            Log.e(TAG, "onFailure: "+t.getMessage()+"" );
                            Toast.makeText(mControlMainActivity,"支付失败",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            HideAllLayout();
                            LinearLayout modeldetails_main = modelOrderDetailsView.findViewById(R.id.modeldetails_main);
                            View resultView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelorderpay_payresult1, null);
                            modeldetails_main.addView(resultView);
                            TextView orderpay_payresult = resultView.findViewById(R.id.orderpay_payresult);
                            orderpay_payresult.setText("订单失败");
                            mControlMainActivity.onClickOrderResult();
                        }
                    });
        }
    }

    public static  class BuyCode {

        /**
         * code : 200
         * data : {"order_num":"1576216789889"}
         */

        private int code;
        private String data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    private void QueryDiscountFromOneStuCourse(boolean isEnable,LinearLayout orderpay_couponchoose_main_content) {
        if (mCourseInfo.mCourseId.equals("") ||mControlMainActivity.mStuId.equals("")){
            if (isEnable){
                Toast.makeText(mControlMainActivity,"查询可用优惠券列表失败",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mControlMainActivity,"查询不可用优惠券列表失败",Toast.LENGTH_LONG).show();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();
        HashMap<String,Integer> paramsMap1 = new HashMap<>();
        if (isEnable){
            paramsMap1.put("type", 1);
        } else {
            paramsMap1.put("type", 0);
        }
        paramsMap1.put("course_id", Integer.valueOf(mCourseInfo.mCourseId));
        paramsMap1.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));
        String strEntity = gson.toJson(paramsMap1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<CouponBean> call = modelObservableInterface.queryDiscountFromOneStuCourse(body);
        call.enqueue(new Callback<CouponBean>() {
            @Override
            public void onResponse(Call<CouponBean> call, Response<CouponBean> response) {
                int code = response.code();
                if (code != 200){
                    Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                    if (isEnable){
                        Toast.makeText(mControlMainActivity,"查询可用优惠券列表失败",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mControlMainActivity,"查询不可用优惠券列表失败",Toast.LENGTH_LONG).show();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                CouponBean couponBean = response.body();
                if (couponBean == null){
                    Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                    if (isEnable){
                        Toast.makeText(mControlMainActivity,"查询可用优惠券列表失败",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mControlMainActivity,"查询不可用优惠券列表失败",Toast.LENGTH_LONG).show();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(couponBean.getErrorCode(),couponBean.getErrorMsg())){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                CouponBean.CouponDataBean couponDataBean = couponBean.getData();
                if (couponDataBean == null){
                    Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                    if (isEnable){
                        Toast.makeText(mControlMainActivity,"查询可用优惠券列表失败",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mControlMainActivity,"查询不可用优惠券列表失败",Toast.LENGTH_LONG).show();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (couponDataBean.couponsList == null){
                    Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                    if (isEnable){
                        Toast.makeText(mControlMainActivity,"查询可用优惠券列表失败",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mControlMainActivity,"查询不可用优惠券列表失败",Toast.LENGTH_LONG).show();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                TextView orderpay_couponchoose_tab_use = mOrderCouponChooseView.findViewById(R.id.orderpay_couponchoose_tab_use);
                orderpay_couponchoose_tab_use.setText("可用（" + couponDataBean.enNum + "）");
                TextView orderpay_couponchoose_tab_unused = mOrderCouponChooseView.findViewById(R.id.orderpay_couponchoose_tab_unused);
                orderpay_couponchoose_tab_unused.setText("不可用（" + couponDataBean.unNum + "）");
                for (CouponBean.CouponDataListBean couponDataListBean:couponDataBean.couponsList){
                    View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelorderpay_couponchoose1, null);
                    TextView orderpay_couponchoose1_termofvaliditydata = view.findViewById(R.id.orderpay_couponchoose1_termofvaliditydata);
                    orderpay_couponchoose1_termofvaliditydata.setText(couponDataListBean.service_life_end_time);
                    TextView orderpay_couponchoose1_couponfullreduction = view.findViewById(R.id.orderpay_couponchoose1_couponfullreduction);
                    TextView orderpay_couponchoose1_areaofapplication = view.findViewById(R.id.orderpay_couponchoose1_areaofapplication);
                    orderpay_couponchoose1_areaofapplication.setText(couponDataListBean.preferential_scope);
                    TextView modelmy_mycoupon1_couponprice = view.findViewById(R.id.orderpay_couponchoose1_couponprice);
                    TextView orderpay_couponchoose1_couponrequire = view.findViewById(R.id.orderpay_couponchoose1_couponrequire);
                    if (couponDataListBean.preferential_way.equals("满减")){
                        orderpay_couponchoose1_couponfullreduction.setText("满减劵");
                        String dc_denominationS[] = couponDataListBean.dc_denomination.split(",");//满？？，减？？
                        if (dc_denominationS.length == 2){
                            modelmy_mycoupon1_couponprice.setText(dc_denominationS[1]);
                            orderpay_couponchoose1_couponrequire.setText("满" + dc_denominationS[0] + "元可用");
                        }
                    } else if (couponDataListBean.preferential_way.equals("折扣")){
                        orderpay_couponchoose1_couponfullreduction.setText("折扣劵");
                        modelmy_mycoupon1_couponprice.setText(couponDataListBean.dc_denomination + "折");
                        LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) orderpay_couponchoose1_couponrequire.getLayoutParams();
                        ll.height = 0;
                        orderpay_couponchoose1_couponrequire.setLayoutParams(ll);
                        TextView orderpay_couponchoose1_couponpriceicon = view.findViewById(R.id.orderpay_couponchoose1_couponpriceicon);
                        ll = (LinearLayout.LayoutParams) orderpay_couponchoose1_couponpriceicon.getLayoutParams();
                        ll.width = 0;
                        orderpay_couponchoose1_couponpriceicon.setLayoutParams(ll);
                    } else if (couponDataListBean.preferential_way.equals("抵现")){
                        orderpay_couponchoose1_couponfullreduction.setText("抵现劵");
                        modelmy_mycoupon1_couponprice.setText(couponDataListBean.dc_denomination);
                        LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) orderpay_couponchoose1_couponrequire.getLayoutParams();
                        ll.height = 0;
                        orderpay_couponchoose1_couponrequire.setLayoutParams(ll);
                    }
                    if (isEnable){
                        ImageView orderpay_couponchoose1_immediateuse = view.findViewById(R.id.orderpay_couponchoose1_immediateuse);
                        orderpay_couponchoose1_immediateuse.setOnClickListener(V->{
                            mCouponDataListBean = couponDataListBean; //选中的优惠券
                            CourseDetailsBuyInit();
                            mControlMainActivity.Page_OrderDetailsChooseCouponReturn();
                        });
                        view.setOnClickListener(v->{
                            if (mCouponDataListBean == null){
                                return;
                            }
                            if (mCouponDataListBean.small_discount_id.equals(couponDataListBean.small_discount_id)){
                                mCouponDataListBean = null; //点击原来选中的优惠券，将其变为未选中
                                RelativeLayout orderpay_couponchoose1_background = view.findViewById(R.id.orderpay_couponchoose1_background);
                                orderpay_couponchoose1_background.setBackground(view.getResources().getDrawable(R.drawable.mycoupon_cardbackground));
                            }
                        });
                        RelativeLayout orderpay_couponchoose1_background = view.findViewById(R.id.orderpay_couponchoose1_background);
                        orderpay_couponchoose1_background.setBackground(view.getResources().getDrawable(R.drawable.mycoupon_cardbackground));
                        if (mCouponDataListBean != null) {
                            if (mCouponDataListBean.small_discount_id.equals(couponDataListBean.small_discount_id)) {
                                orderpay_couponchoose1_background.setBackground(view.getResources().getDrawable(R.drawable.mycoupon_cardbackground_select));
                            }
                        }
                    } else {
                        //将文字颜色置为灰色
                        TextView orderpay_couponchoose1_couponpriceicon = view.findViewById(R.id.orderpay_couponchoose1_couponpriceicon);
                        TextView orderpay_couponchoose1_couponprice = view.findViewById(R.id.orderpay_couponchoose1_couponprice);
                        orderpay_couponchoose1_couponpriceicon.setTextColor(view.getResources().getColor(R.color.black999999));
                        orderpay_couponchoose1_couponprice.setTextColor(view.getResources().getColor(R.color.black999999));
                        orderpay_couponchoose1_couponrequire.setTextColor(view.getResources().getColor(R.color.black999999));
                        ImageView orderpay_couponchoose1_immediateuse = view.findViewById(R.id.orderpay_couponchoose1_immediateuse);
                        orderpay_couponchoose1_immediateuse.setBackground(view.getResources().getDrawable(R.drawable.mycoupon_button_immediateuse_gray));
                    }
                    orderpay_couponchoose_main_content.addView(view);
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<CouponBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                if (isEnable){
                    Toast.makeText(mControlMainActivity,"查询可用优惠券列表失败",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mControlMainActivity,"查询不可用优惠券列表失败",Toast.LENGTH_LONG).show();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    private void QueryDiscountFromOneStuCoursePacket(boolean isEnable,LinearLayout orderpay_couponchoose_main_content) {
        if (mCoursePacketInfo.mCoursePacketId.equals("") ||mControlMainActivity.mStuId.equals("")){
            if (isEnable){
                Toast.makeText(mControlMainActivity,"查询可用优惠券列表失败",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mControlMainActivity,"查询不可用优惠券列表失败",Toast.LENGTH_LONG).show();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();
        HashMap<String,Integer> paramsMap1 = new HashMap<>();
        if (isEnable){
            paramsMap1.put("type", 1);
        } else {
            paramsMap1.put("type", 0);
        }
        paramsMap1.put("course_package_id", Integer.valueOf(mCoursePacketInfo.mCoursePacketId));
        paramsMap1.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));
        String strEntity = gson.toJson(paramsMap1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<CouponBean> call = modelObservableInterface.queryDiscountFromOneStuCoursePacket(body);
        call.enqueue(new Callback<CouponBean>() {
            @Override
            public void onResponse(Call<CouponBean> call, Response<CouponBean> response) {
                int code = response.code();
                if (code != 200){
                    Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                    if (isEnable){
                        Toast.makeText(mControlMainActivity,"查询可用优惠券列表失败",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mControlMainActivity,"查询不可用优惠券列表失败",Toast.LENGTH_LONG).show();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                CouponBean couponBean = response.body();
                if (couponBean == null){
                    Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                    if (isEnable){
                        Toast.makeText(mControlMainActivity,"查询可用优惠券列表失败",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mControlMainActivity,"查询不可用优惠券列表失败",Toast.LENGTH_LONG).show();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(couponBean.getErrorCode(),couponBean.getErrorMsg())){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                CouponBean.CouponDataBean couponDataBean = couponBean.getData();
                if (couponDataBean == null){
                    Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                    if (isEnable){
                        Toast.makeText(mControlMainActivity,"查询可用优惠券列表失败",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mControlMainActivity,"查询不可用优惠券列表失败",Toast.LENGTH_LONG).show();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (couponDataBean.couponsList == null){
                    Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                    if (isEnable){
                        Toast.makeText(mControlMainActivity,"查询可用优惠券列表失败",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mControlMainActivity,"查询不可用优惠券列表失败",Toast.LENGTH_LONG).show();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                TextView orderpay_couponchoose_tab_use = mOrderCouponChooseView.findViewById(R.id.orderpay_couponchoose_tab_use);
                orderpay_couponchoose_tab_use.setText("可用（" + couponDataBean.enNum + "）");
                TextView orderpay_couponchoose_tab_unused = mOrderCouponChooseView.findViewById(R.id.orderpay_couponchoose_tab_unused);
                orderpay_couponchoose_tab_unused.setText("不可用（" + couponDataBean.unNum + "）");
                for (CouponBean.CouponDataListBean couponDataListBean:couponDataBean.couponsList){
                    View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelorderpay_couponchoose1, null);
                    TextView orderpay_couponchoose1_termofvaliditydata = view.findViewById(R.id.orderpay_couponchoose1_termofvaliditydata);
                    orderpay_couponchoose1_termofvaliditydata.setText(couponDataListBean.service_life_end_time);
                    TextView orderpay_couponchoose1_couponfullreduction = view.findViewById(R.id.orderpay_couponchoose1_couponfullreduction);
                    TextView orderpay_couponchoose1_areaofapplication = view.findViewById(R.id.orderpay_couponchoose1_areaofapplication);
                    orderpay_couponchoose1_areaofapplication.setText(couponDataListBean.preferential_scope);
                    TextView modelmy_mycoupon1_couponprice = view.findViewById(R.id.orderpay_couponchoose1_couponprice);
                    TextView orderpay_couponchoose1_couponrequire = view.findViewById(R.id.orderpay_couponchoose1_couponrequire);
                    if (couponDataListBean.preferential_way.equals("满减")){
                        orderpay_couponchoose1_couponfullreduction.setText("满减劵");
                        String dc_denominationS[] = couponDataListBean.dc_denomination.split(",");//满？？，减？？
                        if (dc_denominationS.length == 2){
                            modelmy_mycoupon1_couponprice.setText(dc_denominationS[1]);
                            orderpay_couponchoose1_couponrequire.setText("满" + dc_denominationS[0] + "元可用");
                        }
                    } else if (couponDataListBean.preferential_way.equals("折扣")){
                        orderpay_couponchoose1_couponfullreduction.setText("折扣劵");
                        modelmy_mycoupon1_couponprice.setText(couponDataListBean.dc_denomination + "折");
                        LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) orderpay_couponchoose1_couponrequire.getLayoutParams();
                        ll.height = 0;
                        orderpay_couponchoose1_couponrequire.setLayoutParams(ll);
                        TextView orderpay_couponchoose1_couponpriceicon = view.findViewById(R.id.orderpay_couponchoose1_couponpriceicon);
                        ll = (LinearLayout.LayoutParams) orderpay_couponchoose1_couponpriceicon.getLayoutParams();
                        ll.width = 0;
                        orderpay_couponchoose1_couponpriceicon.setLayoutParams(ll);
                    } else if (couponDataListBean.preferential_way.equals("抵现")){
                        orderpay_couponchoose1_couponfullreduction.setText("抵现劵");
                        modelmy_mycoupon1_couponprice.setText(couponDataListBean.dc_denomination);
                        LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) orderpay_couponchoose1_couponrequire.getLayoutParams();
                        ll.height = 0;
                        orderpay_couponchoose1_couponrequire.setLayoutParams(ll);
                    }
                    if (isEnable){
                        ImageView orderpay_couponchoose1_immediateuse = view.findViewById(R.id.orderpay_couponchoose1_immediateuse);
                        orderpay_couponchoose1_immediateuse.setOnClickListener(V->{
                            mCouponDataListBean = couponDataListBean; //选中的优惠券
                            CourseDetailsBuyInit();
                            mControlMainActivity.Page_OrderDetailsChooseCouponReturn();
                        });
                        view.setOnClickListener(v->{
                            if (mCouponDataListBean != null) {
                                if (mCouponDataListBean.small_discount_id.equals(couponDataListBean.small_discount_id)) {
                                    mCouponDataListBean = null; //点击原来选中的优惠券，将其变为未选中
                                    RelativeLayout orderpay_couponchoose1_background = view.findViewById(R.id.orderpay_couponchoose1_background);
                                    orderpay_couponchoose1_background.setBackground(view.getResources().getDrawable(R.drawable.mycoupon_cardbackground));
                                }
                            }
                        });
                        RelativeLayout orderpay_couponchoose1_background = view.findViewById(R.id.orderpay_couponchoose1_background);
                        orderpay_couponchoose1_background.setBackground(view.getResources().getDrawable(R.drawable.mycoupon_cardbackground));
                        if (mCouponDataListBean != null) {
                            if (mCouponDataListBean.small_discount_id.equals(couponDataListBean.small_discount_id)) {
                                orderpay_couponchoose1_background.setBackground(view.getResources().getDrawable(R.drawable.mycoupon_cardbackground_select));
                            }
                        }
                    } else {
                        //将文字颜色置为灰色
                        TextView orderpay_couponchoose1_couponpriceicon = view.findViewById(R.id.orderpay_couponchoose1_couponpriceicon);
                        TextView orderpay_couponchoose1_couponprice = view.findViewById(R.id.orderpay_couponchoose1_couponprice);
                        orderpay_couponchoose1_couponpriceicon.setTextColor(view.getResources().getColor(R.color.black999999));
                        orderpay_couponchoose1_couponprice.setTextColor(view.getResources().getColor(R.color.black999999));
                        orderpay_couponchoose1_couponrequire.setTextColor(view.getResources().getColor(R.color.black999999));
                        ImageView orderpay_couponchoose1_immediateuse = view.findViewById(R.id.orderpay_couponchoose1_immediateuse);
                        orderpay_couponchoose1_immediateuse.setBackground(view.getResources().getDrawable(R.drawable.mycoupon_button_immediateuse_gray));
                    }
                    orderpay_couponchoose_main_content.addView(view);
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<CouponBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                if (isEnable){
                    Toast.makeText(mControlMainActivity,"查询可用优惠券列表失败",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mControlMainActivity,"查询不可用优惠券列表失败",Toast.LENGTH_LONG).show();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    private void QueryDiscountFromOneStuRepay(boolean isEnable,LinearLayout orderpay_couponchoose_main_content) {
        if (mMyOrderListBean.getCPC_id() == null || mControlMainActivity.mStuId.equals("")){
            if (isEnable){
                Toast.makeText(mControlMainActivity,"查询可用优惠券列表失败",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mControlMainActivity,"查询不可用优惠券列表失败",Toast.LENGTH_LONG).show();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();
        HashMap<String,Integer> paramsMap1 = new HashMap<>();
        if (isEnable){
            paramsMap1.put("type", 1);
        } else {
            paramsMap1.put("type", 0);
        }
        if (mMyOrderListBean.getProduct_type().equals("课程包")){
            paramsMap1.put("course_package_id", mMyOrderListBean.getCPC_id());
        } else if (mMyOrderListBean.getProduct_type().equals("课程")) {
            paramsMap1.put("course_id", mMyOrderListBean.getCPC_id());
        }
        paramsMap1.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));
        String strEntity = gson.toJson(paramsMap1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        if (mMyOrderListBean.getProduct_type().equals("课程包")){
            Call<CouponBean> call = modelObservableInterface.queryDiscountFromOneStuCoursePacket(body);
            call.enqueue(new Callback<CouponBean>() {
                @Override
                public void onResponse(Call<CouponBean> call, Response<CouponBean> response) {
                    int code = response.code();
                    if (code != 200){
                        Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                        if (isEnable){
                            Toast.makeText(mControlMainActivity,"查询可用优惠券列表失败",Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mControlMainActivity,"查询不可用优惠券列表失败",Toast.LENGTH_LONG).show();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                    CouponBean couponBean = response.body();
                    if (couponBean == null){
                        Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                        if (isEnable){
                            Toast.makeText(mControlMainActivity,"查询可用优惠券列表失败",Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mControlMainActivity,"查询不可用优惠券列表失败",Toast.LENGTH_LONG).show();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                    if (!HeaderInterceptor.IsErrorCode(couponBean.getErrorCode(),couponBean.getErrorMsg())){
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                    CouponBean.CouponDataBean couponDataBean = couponBean.getData();
                    if (couponDataBean == null){
                        Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                        if (isEnable){
                            Toast.makeText(mControlMainActivity,"查询可用优惠券列表失败",Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mControlMainActivity,"查询不可用优惠券列表失败",Toast.LENGTH_LONG).show();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                    if (couponDataBean.couponsList == null){
                        Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                        if (isEnable){
                            Toast.makeText(mControlMainActivity,"查询可用优惠券列表失败",Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mControlMainActivity,"查询不可用优惠券列表失败",Toast.LENGTH_LONG).show();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                    TextView orderpay_couponchoose_tab_use = mOrderCouponChooseView.findViewById(R.id.orderpay_couponchoose_tab_use);
                    orderpay_couponchoose_tab_use.setText("可用（" + couponDataBean.enNum + "）");
                    TextView orderpay_couponchoose_tab_unused = mOrderCouponChooseView.findViewById(R.id.orderpay_couponchoose_tab_unused);
                    orderpay_couponchoose_tab_unused.setText("不可用（" + couponDataBean.unNum + "）");
                    for (CouponBean.CouponDataListBean couponDataListBean:couponDataBean.couponsList){
                        View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelorderpay_couponchoose1, null);
                        TextView orderpay_couponchoose1_termofvaliditydata = view.findViewById(R.id.orderpay_couponchoose1_termofvaliditydata);
                        orderpay_couponchoose1_termofvaliditydata.setText(couponDataListBean.service_life_end_time);
                        TextView orderpay_couponchoose1_couponfullreduction = view.findViewById(R.id.orderpay_couponchoose1_couponfullreduction);
                        TextView orderpay_couponchoose1_areaofapplication = view.findViewById(R.id.orderpay_couponchoose1_areaofapplication);
                        orderpay_couponchoose1_areaofapplication.setText(couponDataListBean.preferential_scope);
                        TextView modelmy_mycoupon1_couponprice = view.findViewById(R.id.orderpay_couponchoose1_couponprice);
                        TextView orderpay_couponchoose1_couponrequire = view.findViewById(R.id.orderpay_couponchoose1_couponrequire);
                        if (couponDataListBean.preferential_way.equals("满减")){
                            orderpay_couponchoose1_couponfullreduction.setText("满减劵");
                            String dc_denominationS[] = couponDataListBean.dc_denomination.split(",");//满？？，减？？
                            if (dc_denominationS.length == 2){
                                modelmy_mycoupon1_couponprice.setText(dc_denominationS[1]);
                                orderpay_couponchoose1_couponrequire.setText("满" + dc_denominationS[0] + "元可用");
                            }
                        } else if (couponDataListBean.preferential_way.equals("折扣")){
                            orderpay_couponchoose1_couponfullreduction.setText("折扣劵");
                            modelmy_mycoupon1_couponprice.setText(couponDataListBean.dc_denomination + "折");
                            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) orderpay_couponchoose1_couponrequire.getLayoutParams();
                            ll.height = 0;
                            orderpay_couponchoose1_couponrequire.setLayoutParams(ll);
                            TextView orderpay_couponchoose1_couponpriceicon = view.findViewById(R.id.orderpay_couponchoose1_couponpriceicon);
                            ll = (LinearLayout.LayoutParams) orderpay_couponchoose1_couponpriceicon.getLayoutParams();
                            ll.width = 0;
                            orderpay_couponchoose1_couponpriceicon.setLayoutParams(ll);
                        } else if (couponDataListBean.preferential_way.equals("抵现")){
                            orderpay_couponchoose1_couponfullreduction.setText("抵现劵");
                            modelmy_mycoupon1_couponprice.setText(couponDataListBean.dc_denomination);
                            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) orderpay_couponchoose1_couponrequire.getLayoutParams();
                            ll.height = 0;
                            orderpay_couponchoose1_couponrequire.setLayoutParams(ll);
                        }
                        if (isEnable){
                            ImageView orderpay_couponchoose1_immediateuse = view.findViewById(R.id.orderpay_couponchoose1_immediateuse);
                            orderpay_couponchoose1_immediateuse.setOnClickListener(V->{
                                mCouponDataListBean = couponDataListBean; //选中的优惠券
                                CourseDetailsBuyInit();
                                mControlMainActivity.Page_OrderDetailsChooseCouponReturn();
                            });
                        } else {
                            //将文字颜色置为灰色
                            TextView orderpay_couponchoose1_couponpriceicon = view.findViewById(R.id.orderpay_couponchoose1_couponpriceicon);
                            TextView orderpay_couponchoose1_couponprice = view.findViewById(R.id.orderpay_couponchoose1_couponprice);
                            orderpay_couponchoose1_couponpriceicon.setTextColor(view.getResources().getColor(R.color.black999999));
                            orderpay_couponchoose1_couponprice.setTextColor(view.getResources().getColor(R.color.black999999));
                            orderpay_couponchoose1_couponrequire.setTextColor(view.getResources().getColor(R.color.black999999));
                            ImageView orderpay_couponchoose1_immediateuse = view.findViewById(R.id.orderpay_couponchoose1_immediateuse);
                            orderpay_couponchoose1_immediateuse.setBackground(view.getResources().getDrawable(R.drawable.mycoupon_button_immediateuse_gray));
                        }
                        orderpay_couponchoose_main_content.addView(view);
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                }

                @Override
                public void onFailure(Call<CouponBean> call, Throwable t) {
                    Log.e("TAG", "onError: " + t.getMessage()+"" );
                    if (isEnable){
                        Toast.makeText(mControlMainActivity,"查询可用优惠券列表失败",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mControlMainActivity,"查询不可用优惠券列表失败",Toast.LENGTH_LONG).show();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                }
            });
        } else if (mMyOrderListBean.getProduct_type().equals("课程")) {
            Call<CouponBean> call = modelObservableInterface.queryDiscountFromOneStuCourse(body);
            call.enqueue(new Callback<CouponBean>() {
                @Override
                public void onResponse(Call<CouponBean> call, Response<CouponBean> response) {
                    int code = response.code();
                    if (code != 200){
                        Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                        if (isEnable){
                            Toast.makeText(mControlMainActivity,"查询可用优惠券列表失败",Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mControlMainActivity,"查询不可用优惠券列表失败",Toast.LENGTH_LONG).show();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                    CouponBean couponBean = response.body();
                    if (couponBean == null){
                        Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                        if (isEnable){
                            Toast.makeText(mControlMainActivity,"查询可用优惠券列表失败",Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mControlMainActivity,"查询不可用优惠券列表失败",Toast.LENGTH_LONG).show();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                    CouponBean.CouponDataBean couponDataBean = couponBean.getData();
                    if (couponDataBean == null){
                        Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                        if (isEnable){
                            Toast.makeText(mControlMainActivity,"查询可用优惠券列表失败",Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mControlMainActivity,"查询不可用优惠券列表失败",Toast.LENGTH_LONG).show();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                    if (couponDataBean.couponsList == null){
                        Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                        if (isEnable){
                            Toast.makeText(mControlMainActivity,"查询可用优惠券列表失败",Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mControlMainActivity,"查询不可用优惠券列表失败",Toast.LENGTH_LONG).show();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                    TextView orderpay_couponchoose_tab_use = mOrderCouponChooseView.findViewById(R.id.orderpay_couponchoose_tab_use);
                    orderpay_couponchoose_tab_use.setText("可用（" + couponDataBean.enNum + "）");
                    TextView orderpay_couponchoose_tab_unused = mOrderCouponChooseView.findViewById(R.id.orderpay_couponchoose_tab_unused);
                    orderpay_couponchoose_tab_unused.setText("不可用（" + couponDataBean.unNum + "）");
                    for (CouponBean.CouponDataListBean couponDataListBean:couponDataBean.couponsList){
                        View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelorderpay_couponchoose1, null);
                        TextView orderpay_couponchoose1_termofvaliditydata = view.findViewById(R.id.orderpay_couponchoose1_termofvaliditydata);
                        orderpay_couponchoose1_termofvaliditydata.setText(couponDataListBean.service_life_end_time);
                        TextView orderpay_couponchoose1_couponfullreduction = view.findViewById(R.id.orderpay_couponchoose1_couponfullreduction);
                        TextView orderpay_couponchoose1_areaofapplication = view.findViewById(R.id.orderpay_couponchoose1_areaofapplication);
                        orderpay_couponchoose1_areaofapplication.setText(couponDataListBean.preferential_scope);
                        TextView modelmy_mycoupon1_couponprice = view.findViewById(R.id.orderpay_couponchoose1_couponprice);
                        TextView orderpay_couponchoose1_couponrequire = view.findViewById(R.id.orderpay_couponchoose1_couponrequire);
                        if (couponDataListBean.preferential_way.equals("满减")){
                            orderpay_couponchoose1_couponfullreduction.setText("满减劵");
                            String dc_denominationS[] = couponDataListBean.dc_denomination.split(",");//满？？，减？？
                            if (dc_denominationS.length == 2){
                                modelmy_mycoupon1_couponprice.setText(dc_denominationS[1]);
                                orderpay_couponchoose1_couponrequire.setText("满" + dc_denominationS[0] + "元可用");
                            }
                        } else if (couponDataListBean.preferential_way.equals("折扣")){
                            orderpay_couponchoose1_couponfullreduction.setText("折扣劵");
                            modelmy_mycoupon1_couponprice.setText(couponDataListBean.dc_denomination + "折");
                            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) orderpay_couponchoose1_couponrequire.getLayoutParams();
                            ll.height = 0;
                            orderpay_couponchoose1_couponrequire.setLayoutParams(ll);
                            TextView orderpay_couponchoose1_couponpriceicon = view.findViewById(R.id.orderpay_couponchoose1_couponpriceicon);
                            ll = (LinearLayout.LayoutParams) orderpay_couponchoose1_couponpriceicon.getLayoutParams();
                            ll.width = 0;
                            orderpay_couponchoose1_couponpriceicon.setLayoutParams(ll);
                        } else if (couponDataListBean.preferential_way.equals("抵现")){
                            orderpay_couponchoose1_couponfullreduction.setText("抵现劵");
                            modelmy_mycoupon1_couponprice.setText(couponDataListBean.dc_denomination);
                            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) orderpay_couponchoose1_couponrequire.getLayoutParams();
                            ll.height = 0;
                            orderpay_couponchoose1_couponrequire.setLayoutParams(ll);
                        }
                        if (isEnable){
                            ImageView orderpay_couponchoose1_immediateuse = view.findViewById(R.id.orderpay_couponchoose1_immediateuse);
                            orderpay_couponchoose1_immediateuse.setOnClickListener(V->{
                                mCouponDataListBean = couponDataListBean; //选中的优惠券
                                CourseDetailsBuyInit();
                                mControlMainActivity.Page_OrderDetailsChooseCouponReturn();
                            });
                            view.setOnClickListener(v->{
                                if (mCouponDataListBean == null){
                                    return;
                                }
                                if (mCouponDataListBean.small_discount_id.equals(couponDataListBean.small_discount_id)){
                                    mCouponDataListBean = null; //点击原来选中的优惠券，将其变为未选中
                                    RelativeLayout orderpay_couponchoose1_background = view.findViewById(R.id.orderpay_couponchoose1_background);
                                    orderpay_couponchoose1_background.setBackground(view.getResources().getDrawable(R.drawable.mycoupon_cardbackground));
                                }
                            });
                            RelativeLayout orderpay_couponchoose1_background = view.findViewById(R.id.orderpay_couponchoose1_background);
                            orderpay_couponchoose1_background.setBackground(view.getResources().getDrawable(R.drawable.mycoupon_cardbackground));
                            if (mCouponDataListBean != null) {
                                if (mCouponDataListBean.small_discount_id.equals(couponDataListBean.small_discount_id)) {
                                    orderpay_couponchoose1_background.setBackground(view.getResources().getDrawable(R.drawable.mycoupon_cardbackground_select));
                                }
                            }
                        } else {
                            //将文字颜色置为灰色
                            TextView orderpay_couponchoose1_couponpriceicon = view.findViewById(R.id.orderpay_couponchoose1_couponpriceicon);
                            TextView orderpay_couponchoose1_couponprice = view.findViewById(R.id.orderpay_couponchoose1_couponprice);
                            orderpay_couponchoose1_couponpriceicon.setTextColor(view.getResources().getColor(R.color.black999999));
                            orderpay_couponchoose1_couponprice.setTextColor(view.getResources().getColor(R.color.black999999));
                            orderpay_couponchoose1_couponrequire.setTextColor(view.getResources().getColor(R.color.black999999));
                            ImageView orderpay_couponchoose1_immediateuse = view.findViewById(R.id.orderpay_couponchoose1_immediateuse);
                            orderpay_couponchoose1_immediateuse.setBackground(view.getResources().getDrawable(R.drawable.mycoupon_button_immediateuse_gray));
                        }
                        orderpay_couponchoose_main_content.addView(view);
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                }

                @Override
                public void onFailure(Call<CouponBean> call, Throwable t) {
                    Log.e("TAG", "onError: " + t.getMessage()+"" );
                    if (isEnable){
                        Toast.makeText(mControlMainActivity,"查询可用优惠券列表失败",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mControlMainActivity,"查询不可用优惠券列表失败",Toast.LENGTH_LONG).show();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                }
            });
        }

    }

    private void CheckBeforeExchangingCoupons(String coupon) {
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();
        HashMap<String,Integer> paramsMap = new HashMap<>();
        paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));
        String strEntity = gson.toJson(paramsMap);
        HashMap<String,String> paramsMap1 = new HashMap<>();
        paramsMap1.put("discount_code_value", coupon);
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.checkBeforeExchangingCoupons(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                int code = response.code();
                if (code != 200){
                    Log.e("TAG", "CheckBeforeExchangingCoupons  onErrorCode: " + code);
                    Toast.makeText(mControlMainActivity,"该优惠码不可用",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (response.body() == null){
                    Log.e("TAG", "CheckBeforeExchangingCoupons  onErrorCode: " + code);
                    Toast.makeText(mControlMainActivity,"该优惠码不可用",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(response.body().getErrorCode(),response.body().getErrorMsg())){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                String message = response.body().getErrorMsg();
                if (message == null){
                    Log.e("TAG", "CheckBeforeExchangingCoupons  onErrorCode: " + code);
                    Toast.makeText(mControlMainActivity,"该优惠码不可用",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (message.equals("ok")) {
                    redeemCoupons(coupon);
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                } else {
                    Log.e("TAG", "CheckBeforeExchangingCoupons  onErrorCode: " + code);
                    Toast.makeText(mControlMainActivity,"该优惠码不可用",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mControlMainActivity,"兑换优惠券失败",Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    private void redeemCoupons(String coupon) {
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();
        HashMap<String,Integer> paramsMap = new HashMap<>();
        paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));
        String strEntity = gson.toJson(paramsMap);
        HashMap<String,String> paramsMap1 = new HashMap<>();
        paramsMap1.put("discount_code_value", coupon);
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.redeemCoupons(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                int code = response.code();
                if (code != 200){
                    Log.e("TAG", "redeemCoupons  onErrorCode: " + code);
                    Toast.makeText(mControlMainActivity,"该优惠码已失效",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (response.body() == null){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(response.body().getErrorCode(),response.body().getErrorMsg())){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                Toast.makeText(mControlMainActivity,"优惠码兑换成功",Toast.LENGTH_LONG).show();
                if (mMyCouponDialog != null) {
                    mMyCouponDialog.cancel();
                }
                //重新刷一下优惠码界面
                CourseDetailsOrderCouponChooseInit();
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mControlMainActivity,"兑换优惠券失败",Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    public static class CouponBean {
        private CouponDataBean data;
        private int code;
        private String msg;

        public CouponDataBean getData() {
            return data;
        }

        public void setData(CouponDataBean data) {
            this.data = data;
        }

        public int getErrorCode() {
            return code;
        }

        public void setErrorCode(int code) {
            this.code = code;
        }

        public String getErrorMsg() {
            return msg;
        }

        public void setErrorMsg(String msg) {
            this.msg = msg;
        }

        public static class CouponDataBean {
            private String enNum;  //可用数量
            private String unNum;   //不可用数量
            private List<CouponDataListBean> couponsList;

            public void setCouponsList(List<CouponDataListBean> couponsList) {
                this.couponsList = couponsList;
            }

            public List<CouponDataListBean> getCouponsList() {
                return couponsList;
            }

            public String getEnNum() {
                return enNum;
            }

            public String getUnNum() {
                return unNum;
            }

            public void setEnNum(String enNum) {
                this.enNum = enNum;
            }

            public void setUnNum(String unNum) {
                this.unNum = unNum;
            }
        }
        public static class CouponDataListBean {
            private String num;
            private String subject_id;   //科目id（优惠范围为指定范围时）
            private String dc_denomination;   //优惠面额
            private String course_id;           //课程id（优惠范围为指定课程）
            private String service_life_start_time;         //使用的开始时间
            private String service_life_end_time;                //使用的结束时间
            private String preferential_way;                //优惠方式
            private String stu_id;                          //学生id
            private String discount_code_value;             //优惠码码值
            private String product_type;                    //优惠类型
            private String course_package_id;               //课程包id（优惠范围为指定课程包时）
            private String project_id;                      //项目id（优惠范围为指定范围时）
            private String small_discount_id;               //这张优惠券的id
            private String preferential_scope;              //优惠范围

            public String getCourse_id() {
                return course_id;
            }

            public String getCourse_package_id() {
                return course_package_id;
            }

            public String getDc_denomination() {
                return dc_denomination;
            }

            public String getDiscount_code_value() {
                return discount_code_value;
            }

            public String getPreferential_scope() {
                return preferential_scope;
            }

            public String getPreferential_way() {
                return preferential_way;
            }

            public String getProduct_type() {
                return product_type;
            }

            public String getProject_id() {
                return project_id;
            }

            public String getService_life_end_time() {
                return service_life_end_time;
            }

            public String getService_life_start_time() {
                return service_life_start_time;
            }

            public String getSmall_discount_id() {
                return small_discount_id;
            }

            public String getStu_id() {
                return stu_id;
            }

            public String getSubject_id() {
                return subject_id;
            }

            public void setCourse_id(String course_id) {
                this.course_id = course_id;
            }

            public void setCourse_package_id(String course_package_id) {
                this.course_package_id = course_package_id;
            }

            public void setDc_denomination(String dc_denomination) {
                this.dc_denomination = dc_denomination;
            }

            public void setDiscount_code_value(String discount_code_value) {
                this.discount_code_value = discount_code_value;
            }

            public void setPreferential_scope(String preferential_scope) {
                this.preferential_scope = preferential_scope;
            }

            public void setPreferential_way(String preferential_way) {
                this.preferential_way = preferential_way;
            }

            public void setProduct_type(String product_type) {
                this.product_type = product_type;
            }

            public void setProject_id(String project_id) {
                this.project_id = project_id;
            }

            public void setService_life_end_time(String service_life_end_time) {
                this.service_life_end_time = service_life_end_time;
            }

            public void setService_life_start_time(String service_life_start_time) {
                this.service_life_start_time = service_life_start_time;
            }

            public void setSmall_discount_id(String small_discount_id) {
                this.small_discount_id = small_discount_id;
            }

            public void setStu_id(String stu_id) {
                this.stu_id = stu_id;
            }

            public void setSubject_id(String subject_id) {
                this.subject_id = subject_id;
            }
        }
    }
}
