package com.android.jwjy.wtjyproduct.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.jwjy.wtjyproduct.R;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private final String TAG = "WXPayEntryActivity";

    private IWXAPI api;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modelorderpay_payresult);
        api = WXAPIFactory.createWXAPI(this, WeiXinConstants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }
    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.i(TAG,"errCode = " + resp.errCode);
        //最好依赖于商户后台的查询结果
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            TextView orderpay_payresult = findViewById(R.id.orderpay_payresult);
            ImageView orderpay_payresult_icon = findViewById(R.id.orderpay_payresult_icon);
//            //如果返回-1，很大可能是因为应用签名的问题。用官方的工具生成
//            //签名工具下载：https://open.weixin.qq.com/zh_CN/htmledition/res/dev/download/sdk/Gen_Signature_Android.apk
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("提示");
//            builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
//            builder.show();
            if(resp.errCode==0){
                orderpay_payresult.setText("支付成功");
                orderpay_payresult_icon.setBackground(getResources().getDrawable(R.drawable.img_orderresult_success));
            }
            if(resp.errCode==-1){
//                setPayResult("支付失败");
                orderpay_payresult.setText("支付失败");
                orderpay_payresult_icon.setBackground(getResources().getDrawable(R.drawable.img_orderresult_fail));
            }
            if(resp.errCode==-2){
//                setPayResult("取消支付");
                orderpay_payresult.setText("取消支付");
                orderpay_payresult_icon.setBackground(getResources().getDrawable(R.drawable.img_orderresult_fail));
            }
        }
    }

    public void onClickMyOrderReturn(View view) {
        finish();
    }
}