package com.jackmacc.townadmin.Tool;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class Utils {

    public static RequestBody convertTo_text_plain_RequestBody(String param){
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), param);
        //因为GsonConverterFactory.create()转换器的缘故，
        // 会将参数请求头的content-type值默认赋值application/json，
        // 如果没有进行这步转换操作，就可以在OKHttp3的日志拦截器中查看到这样的赋值，
        // 这样导致服务器不能正确识别参数，导致上传失败，所以这里需要对参数请求头的content-type设置一个正确的值：text/plain。
        return requestBody;
    }

    public static ColorDrawable[] vibrantLightColorList =
            {
                    new ColorDrawable(Color.parseColor("#ffeead")),
                    new ColorDrawable(Color.parseColor("#93cfb3")),
                    new ColorDrawable(Color.parseColor("#fd7a7a")),
                    new ColorDrawable(Color.parseColor("#faca5f")),
                    new ColorDrawable(Color.parseColor("#1ba798")),
                    new ColorDrawable(Color.parseColor("#6aa9ae")),
                    new ColorDrawable(Color.parseColor("#ffbf27")),
                    new ColorDrawable(Color.parseColor("#d93947"))
            };

    public static ColorDrawable getRandomDrawbleColor() {
        int idx = new Random().nextInt(vibrantLightColorList.length);
        return vibrantLightColorList[idx];
    }

    public static String DateToTimeFormat(String oldstringDate){
        PrettyTime p = new PrettyTime(new Locale(getCountry()));
        String isTime = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
                    Locale.ENGLISH);
            Date date = sdf.parse(oldstringDate);
            isTime = p.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return isTime;
    }

    //获取时间戳
    public static String getPublishTime(long publishTimeL) {
        String publishTime;
        long timeMillis = System.currentTimeMillis() - publishTimeL;
        long second = timeMillis / 1000;//秒
        if (second < 60) {
            publishTime = second + "秒";
        } else {
            long minute = second / 60;
            if (minute < 60) {
                publishTime = minute + "分钟";
            } else {
                long hour = minute / 60;
                if (hour < 24) {
                    publishTime = hour + "小时";
                } else {
                    long day = hour / 24;
                    publishTime = day + "天前";
                }
            }
        }
        return publishTime;
    }

    public static String DateFormat(String oldstringDate){
        String newDate;
      //  SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMM yyyy", new Locale(getCountry()));
        //新建日期格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-d HH:MM", new Locale(getCountry()));
        try {
           // Date date = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS").parse(oldstringDate);
            //老日期格式.
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'").parse(oldstringDate);

            newDate = dateFormat.format(date);//格式化成新日期格式
        } catch (ParseException e) {
            e.printStackTrace();
            newDate = "oldstringDate";
        }

        return newDate;
    }

    public static String getCountry(){
        Locale locale = Locale.getDefault();
        String country = String.valueOf(locale.getCountry());
        return country.toLowerCase();
    }

    public static String getLanguage(){
        Locale locale = Locale.getDefault();
        String country = String.valueOf(locale.getLanguage());
        return country.toLowerCase();
    }


    public static String toUtf8(String str) {
        String result = null;
        try {
            result = new String(str.getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
}