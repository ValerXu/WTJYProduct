package com.android.jwjy.wtjyproduct.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

public class StringUtils {

    public static void tip(Context context , int contentId){
        Toast.makeText(context , getString(context ,contentId ) , Toast.LENGTH_SHORT).show();
    }

    public static void tip(Context context , String content){
        Toast.makeText(context , content , Toast.LENGTH_SHORT).show();
    }

    private static String getString(Context context , int id){
        return context.getResources().getString(id);
    }

    /**
     *  格式化时间
     *  yyyy-MM-dd HH:mm:ss
     * @param date 需要格式化的时间
     * @return  时间的字符串形式
     */
/*    public static String DataToString(Date date){
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//24小时制
        return sdformat.format(date);
    }*/


    public static int getInt(String str, int defaultInt) {
        if(TextUtils.isEmpty(str))
            return defaultInt;
        if (str.contains("."))
            str = str.substring(0, str.indexOf("."));
        try{
            return Integer.parseInt(str);
        }catch (Exception e){
            return defaultInt;
        }

    }

    public static long getLong(String str, long defaultLong) {
        if(TextUtils.isEmpty(str))
            return defaultLong;
        if (str.contains("."))
            str = str.substring(0, str.indexOf("."));
        try{
            return Long.parseLong(str);
        }catch (Exception e){
            return defaultLong;
        }

    }



}
