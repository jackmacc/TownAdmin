package com.jackmacc.townadmin.maninfo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jackmacc.townadmin.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyVolleyTool extends Activity {


    /**
     * 请求队列,RequestQueue内部的设计就是非常合适高并发的，因此我们不必为每一次HTTP请求都创建一个RequestQueue对象
     * 基本上在每一个需要和网络交互的Activity中创建一个RequestQueue对象就足够了。
     */
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_man);

        requestQueue = Volley.newRequestQueue(this);//创建网络请求队列(执行网络连接的)

        //downloadStringToGet("http://www.baidu.com");//这里不加http://会报空指针异常

        //downloadStringToPost("http://www.qubaobei.com/ios/cf/dish_list.php");

        //getJsonObjectData("http://www.qubaobei.com/ios/cf/dish_list.php?stage_id=1&page=1&limit=20");

        getJsonArrayData("http://app.bilibili.com/promo/ipad3/267/banner.ipad3.json");
    }

    /**
     * 通过http协议的GET请求,从网络获取字符串形式的数据,步骤:
     * 1. 创建一个RequestQueue对象。
     * 2. 创建一个StringRequest对象。
     * 3. 将StringRequest对象添加到RequestQueue里面。
     */
    public void downloadStringToGet(String url){
        //参数说明:1.目标服务器的URL地址，2.服务器响应成功的回调，3.服务器响应失败的回调。
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //主线程中运行的
                Log.i("tag", "请求成功,数据:" + s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //主线程运行的
                Log.i("tag", "请求失败" + volleyError.getMessage(), volleyError);//抛出异常(不会终止程序)
            }
        });
        //将这个StringRequest对象添加到RequestQueue里面就可以执行网络连接了
        requestQueue.add(stringRequest);
    }

    /**
     * 通过http协议的POST请求,从网络获取字符串形式的数据
     * StringRequest的构造函数需要传入三个参数，第一个参数就是目标服务器的URL地址，
     * 第二个参数是服务器响应成功的回调，第三个参数是服务器响应失败的回调
     */
    public void downloadStringToPost(String url){
        //在上面的基础上增加了参数1指定请求方式,并重写了getParams()方法
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.i("tag", "请求成功,数据:" + s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i("tag", "请求失败" + volleyError.getMessage(), volleyError);//抛出异常(不会终止程序)
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //重写此方法来指定Post的数据
                Map<String, String> map = new HashMap<String, String>();
                map.put("stage_id", "1");
                map.put("page", "1");
                map.put("limit", "20");
                return map;
            }
        };
        //将这个StringRequest对象添加到RequestQueue里面就可以执行网络连接了
        requestQueue.add(stringRequest);
    }


    /**
     * json数据请求,JsonObject数据
     */
    public void getJsonObjectData(String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.i("tag", "请求成功,数据:" + jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i("tag", "请求失败" + volleyError.getMessage(), volleyError);//抛出异常(不会终止程序)
            }
        });
        requestQueue.add(jsonObjectRequest);//开始执行
    }

    /**
     * json数据请求,JsonArray数据
     */
    public void getJsonArrayData(String url){
        JsonArrayRequest JsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                Log.i("tag", "请求成功,数据:" + jsonArray);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i("tag", "请求失败" + volleyError.getMessage(), volleyError);//抛出异常(不会终止程序)
            }
        });
        requestQueue.add(JsonArrayRequest);//开始执行
    }
}

//上面介绍的获取字符串资源
