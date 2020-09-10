package com.android.jwjy.wtjyproduct.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;


public class FragmentListAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;
    public FragmentListAdapter(Context context, List<Fragment> fragmentList) {
        super(((FragmentActivity)context).getSupportFragmentManager());
        this.fragments = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    private int mChildCount = Integer.MIN_VALUE;
    private int lashChildCount = Integer.MIN_VALUE;

    @Override
    public void notifyDataSetChanged() {
        lashChildCount = mChildCount;
        mChildCount = getCount();
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        if(lashChildCount > 0 ){
            lashChildCount--;
            return POSITION_NONE;
        }else if(lashChildCount == Integer.MIN_VALUE){
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }


}
