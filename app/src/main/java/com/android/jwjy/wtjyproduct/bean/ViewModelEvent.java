package com.android.jwjy.wtjyproduct.bean;

/**
 * Created by ccy on 2019/5/9/10:23
 */
public class ViewModelEvent {
    private int type;
    private Object object;

    public ViewModelEvent(int type){
        this.type =type;
    }
    public ViewModelEvent(int type, Object o){
        this.type =type;
        this.object =o;
    }

    public int getType() {
        return type;
    }

    public Object getObject() {
        return object;
    }
}
