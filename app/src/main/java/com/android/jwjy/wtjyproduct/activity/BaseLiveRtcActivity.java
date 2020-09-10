package com.android.jwjy.wtjyproduct.activity;

import android.content.DialogInterface;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.view.View;

import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigButton;
import com.mylhyl.circledialog.callback.ConfigText;
import com.mylhyl.circledialog.params.ButtonParams;
import com.mylhyl.circledialog.params.TextParams;
import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.base.BaseDatabindingActivity;
import com.android.jwjy.wtjyproduct.interfaces.IMultiMediaViewManager;
import com.android.jwjy.wtjyproduct.util.DimensionUtils;
import com.android.jwjy.wtjyproduct.viewmodel.BaseLiveRtcViewModel;

/**
 * Created by ccy on 2019/5/16/18:26
 */
public abstract class BaseLiveRtcActivity<B extends ViewDataBinding, VM extends BaseLiveRtcViewModel> extends BaseDatabindingActivity<B, VM> implements IMultiMediaViewManager {
    protected int duration;
    protected boolean isMp4;
    protected boolean isDraw;
    protected long noNetWorkTime;
    protected int lastVisibility;
    protected boolean isMuitiMediaFullScreen;
    /**
     * 是否应用音视频
     */
    protected boolean isApplicateMuitiMedia;
    protected boolean lastRotate;

    /**
     * 返回弹框
     */
    protected void popupBackDialog() {
        int weith = isIPad() ? getDimension(R.dimen.dp_150) : getDimension(R.dimen.dp_210);
        new CircleDialog.Builder().setCanceledOnTouchOutside(false).setWidth(weith).setText("确定要退出直播间吗?").configText(new ConfigText() {
            @Override
            public void onConfig(TextParams params) {
                params.textColor = Color.parseColor("#1D334E");
                params.height = isIPad() ? getDimension(R.dimen.dp_65) : getDimension(R.dimen.dp_95);
            }
        }).setPositive("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }).configPositive(new ConfigButton() {
            @Override
            public void onConfig(ButtonParams params) {
                params.height = dialogBtnHeight();
                params.textColor = getResources().getColor(R.color.red);
            }
        }).setNegative("取消", null).configNegative(new ConfigButton() {
            @Override
            public void onConfig(ButtonParams params) {
                params.height = dialogBtnHeight();
                params.textColor = Color.parseColor("#263548");
            }
        }).show(this.getSupportFragmentManager());
    }

    protected int dialogBtnHeight() {
        return isIPad() ? this.getDimension(R.dimen.dp_25)
                : this.getDimension(R.dimen.dp_35);
    }

    protected boolean isIPad() {
        return DimensionUtils.isPad(this);
    }

    protected int getDimension(int id) {
        return (int) this.getResources().getDimension(id);
    }

    protected String getResouceString(int id) {
        return this.getResources().getString(id);
    }

    //用户被强制退出
    public void memberForceout() {
        String reason = getResouceString(R.string.member_forceout);
        memberOut(reason);
    }

    //用户被踢
    public void memberKick() {
        String reason = getResouceString(R.string.member_kick);
        memberOut(reason);
    }

    //用户退出
    private void memberOut(String reason) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(reason);
        builder.setTitle(R.string.tips);

        builder.setPositiveButton((R.string.goback), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                BaseLiveRtcActivity.this.finish();
            }
        });
        android.app.AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }


    public interface OnPositiveOrNegativeListener {
        void onPositive();

        void onNegative();
    }

    protected void reset() {
        noNetWorkTime = 0;
        lastRotate = false;
        isMp4 = false;
        duration = 0;
        isDraw = false;
        isMuitiMediaFullScreen = false;
        isApplicateMuitiMedia = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        baseViewModel.onPause();
    }

    @Override
    public void onBackPressed() {
        popupBackDialog();
    }
}
