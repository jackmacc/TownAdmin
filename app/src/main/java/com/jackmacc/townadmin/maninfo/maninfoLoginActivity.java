package com.jackmacc.townadmin.maninfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jackmacc.townadmin.DataUrl;
import com.jackmacc.townadmin.R;
import com.jackmacc.townadmin.home_page;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/*
登录页面
1.登录口令验证
2.session id 锁定
3.弹出重复登录确认

 */

public class maninfoLoginActivity extends Activity {
    Button   btn_login;
    EditText username, password;
    ImageView iconLog;
    ProgressBar mProgressBar; //处理栏
    TextView no_acc; //没有注册

    //底部浏览 图标监听
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Intent intent=new Intent();
            switch (item.getItemId()) {

                case R.id.EnjoyCraftRoot:
                    intent.setClass(getApplicationContext(),home_page.class);
                    startActivity(intent);

                    return true;
            }
            return false;
        }
    };

    //创建一次弹出窗口
    //static Boolean checkoutOnlyoneTime=false;
    Boolean check_Boolean;

    //网址操作路径
    DataUrl dataUrl;

    //volley 框架
    StringRequest mStringRequest;
    RequestQueue mRequestQueue;
    HashMap<String, String> user;
    //sp 管理器
    SessionManager mSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maninfo_user_login_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //sr
        mSessionManager=new SessionManager(this);
        check_Boolean= mSessionManager.isLoggin();  //如果已经登录被退回到登录....

         user = mSessionManager.getUserDetail();
        //路径
        dataUrl=new DataUrl();


        //没有注册可以注册标题
        no_acc=findViewById(R.id.no_acc);
        no_acc.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //注册用户窗口
                Intent intent = new Intent();
                intent.setClass(maninfoLoginActivity.this, guildtoRegActivity.class);
                startActivity(intent);

            }
        });

        //底部浏览图标加载监听
        BottomNavigationView bottom_nav = (BottomNavigationView) findViewById(R.id.Loginpage_bottomNavigation);
        bottom_nav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        username = findViewById(R.id.man_username);
        password = findViewById(R.id.man_password);

        //右侧图标 密码显示
        password.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片

                Drawable drawable = password.getCompoundDrawables()[2];
                //如果右边没有图片，不再处理
                if (drawable == null)
                    return false;
                //如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (event.getX() > password.getWidth()
                        - password.getPaddingRight()
                        - drawable.getIntrinsicWidth()){
                    password.setInputType(128);
                }
                return false;
            }
        });


        mProgressBar = findViewById(R.id.man_progressBar);
        mProgressBar.setVisibility(View.GONE);

        //declaring Server ip,username,database name and password
        btn_login = (Button) findViewById(R.id.btn_login);
        //登录按钮
        btn_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_name=username.getText().toString();
                String pass_word=password.getText().toString();


                mProgressBar.setVisibility(View.VISIBLE);
                btn_login.setVisibility(View.GONE);
                //输入检查
                if(user_name.trim().equals("")||pass_word.trim().equals("")){
                    Toast.makeText(getApplicationContext(), "请输入用户名(密码)", Toast.LENGTH_SHORT).show();

                    btn_login.setVisibility(View.VISIBLE);
                }else new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        getLoginData(user_name, pass_word);

                    }
                }, 3000);
            }
        });

        if (mRequestQueue == null) {
            //创建一个请求队列（使用Volley框架）
            mRequestQueue = Volley.newRequestQueue(maninfoLoginActivity.this);
        }

        //如果已经登录弹出窗口
        if(check_Boolean){
            //抛出一个等待create() 完成的线程句柄
          //  popupHandler.sendEmptyMessageDelayed(0, 1000);
            //给 onResume() 处理 更好

        }


    }

    //为了弹出窗口 创建的线程
    private Handler popupHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    btn_login.setVisibility(View.GONE);
                    showPopupWindow(getApplicationContext(), maninfoLoginActivity.this.username);
                    break;
            }
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
     //   Toast.makeText(getApplicationContext(), "恢复页面", Toast.LENGTH_SHORT).show();

        check_Boolean= mSessionManager.isLoggin();
        if(check_Boolean){
           // checkoutOnlyoneTime=false;

            popupHandler.sendEmptyMessageDelayed(0, 500);

        }

    }

    //退出登录 checkUserDetail
    private void checkUserDetail(String userid,String same){

        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        //URL_checkoutText 是退出 session_id
        //现在时间：2020年12月7日19时02分13秒 测试 node.js 接口
        //原来是 php 接口 getHostpath() 主机地址

        StringRequest stringRequest=new StringRequest(Request.Method.POST, dataUrl.getHostNewspath()+ File.separator+dataUrl.js_checkoutPath,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {//得到远程服务器的 回答

                     //   response= toUtf8(response);

                        try {
                            JSONObject jsonObject=new JSONObject(response); //转换成 json 对象
                            String success =jsonObject.getString("success");
                             Toast_Looper("退出消息:"+response);

                            btn_login.setVisibility(View.VISIBLE);

                            if(success.equals("3")){
                                String reportMsg = jsonObject.getString("message");
                                   Toast_Looper("正确返回:"+reportMsg);
                            }

                            if(success.equals("4")){
                                String reportMsg = jsonObject.getString("message");
                                  Toast_Looper("错误返回:"+reportMsg);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            //  Toast.makeText(maninfo_main_Activity.this,
                            //       "Error Readding Detail"+e.toString(),Toast.LENGTH_SHORT).show();
                            Toast_Looper("检查返回异常 Json 为"+e.toString());
                        }
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        // Toast.makeText(maninfo_main_Activity.this,
                        //     "Error Readding Detail"+error.toString(),Toast.LENGTH_SHORT).show();

                    }


                })
        {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params=new HashMap<>();
                params.put("id",userid);
                params.put("sname",same);
                params.put("checkout","out");
                return params;

            }
            // charset=%s", "utf-8");
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //请求头部添加 cookie  将本地 的 session id 传过去
                //2019年10月9日 添加 session id 头


                HashMap localHashMap=new HashMap();
             //   HashMap<String, String> user = mSessionManager.getUserDetail();
                 Toast_Looper("<<<<<sessid >>>>>"+user.get(mSessionManager.SESSION_ID));
                localHashMap.put("Cookie",user.get(mSessionManager.SESSION_ID));


                return localHashMap;

            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    //处理登录数据
    public void getLoginData(String user_name ,String pass_word ) {

        //登录php页面
        //此处是登录服务器的地址.
        //测试 node.js  接口
        //现在时间：2020年12月7日19时04分10秒
        String finalurl=dataUrl.getHostNewspath()+File.separator+dataUrl.js_loginPath;
                //url1+stringip+contextURLpath;

      //  finalurl="http://192.168.10.196:8071/town_login";
        /**
         * 使用Volley框架真正去请求服务器
         * Method.POST：请求方式为post
         * builder.toString()：请求的链接ef
         * Listener<String>：监听
         */

        //MyStringRequest
       // Toast_Looper("开始发送!");
        mStringRequest = new StringRequest(
                StringRequest.Method.POST, finalurl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                     //  Toast_Looper("ok  return  response /n");
                   //   Toast_Looper(response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success"); //约定

                            if(success.equals("2")) {
                                String reportMsg = jsonObject.getString("message");
                                Toast_Looper(reportMsg+ "不要重复登录");
                                mProgressBar.setVisibility(View.GONE);
                                btn_login.setVisibility(View.VISIBLE);
                            }

                            if(success.equals("0")) {
                                String reportMsg = jsonObject.getString("message");
                                Toast_Looper(reportMsg+ "登录失败了");
                                mProgressBar.setVisibility(View.GONE);
                                btn_login.setVisibility(View.VISIBLE);
                            }

                            if (success.equals("1")) {
                                JSONObject jsonObjectlogin = jsonObject.getJSONObject("login"); //约定
                              //  JSONArray jsonArray = jsonObject.getJSONArray("login"); //约定
                                String reportMsg = jsonObject.getString("message");
                                Toast_Looper(reportMsg);

//                                for (int i = 0; i < jsonArray.length(); i++) {
//                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String sname  = jsonObjectlogin.getString("sname").trim();
                                   String email = jsonObjectlogin.getString("email").trim();
                                    String id = jsonObjectlogin.getString("id").trim();

                                    //SR 管理器 记录数据
                                    mSessionManager.createSessiond(sname, email, id); //只是在登录的时候创建一次.


                                    Intent intent = new Intent();
                                    intent.setClass(maninfoLoginActivity.this, home_page.class);
                                   startActivity(intent);

                                    //回退页面后处理
                                    mProgressBar.setVisibility(View.GONE);
                                    btn_login.setVisibility(View.VISIBLE);
                                   // LoginActivity.this.finish();
//                                }
                            }
                        } catch (JSONException e) {
                            mProgressBar.setVisibility(View.GONE);
                           Toast_Looper("错误" + e.toString());
                            btn_login.setVisibility(View.VISIBLE);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener()

                    {

                        //请求出错的监听
                        @Override
                        public void onErrorResponse (VolleyError error){
                            mProgressBar.setVisibility(View.GONE);
                            btn_login.setVisibility(View.VISIBLE);


                            Toast_Looper("NetworkError" + error.toString());

                        }
                    }


       ) {

            //post请求 发送给服务器的参数参数信息
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                      Map<String,String> params=new HashMap<String,String>();

                      params.put("sname",user_name);
                      params.put("pass",pass_word);
                      return params;
            }

            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

            //加上本地保存的cookie 的信息给服务器
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //请求头部添加 cookie  将本地 的 session id 传过去
                     //2019年10月9日 添加 session id 头


                HashMap localHashMap=new HashMap();
                //HashMap<String, String> user = mSessionManager.getUserDetail();
              //  Toast_Looper("获取 Sesson id:"+user.get(mSessionManager.SESSION_ID));
                localHashMap.put("Cookie",user.get(mSessionManager.SESSION_ID));
            //    localHashMap.put("enctype","application/x-www-form-urlencoded");
                return localHashMap;

            }


            //读取和处理 response 中的 Headers 字段
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    //但是cookie在服务端的存储也是有时间限制的,所以以后的每次登陆都要在登陆成功后把最
                    // 新的cookie信息保存一遍,这样能保证每次用的cookie都是有效的.
                    //保存cookie
                    Map<String, String> responseHeaders = response.headers;//获取所有头字段

                    Log.d("cookie",responseHeaders.toString());

                    if(!check_Boolean) {//两次重写同一个 cookie 会导致错误
                        //一次获取session 后,再次就不发送了.
                        String rawCookies = "empty";
                        rawCookies = responseHeaders.get("Set-Cookie");//获取cookie头字段
                        //更新本地的 session

                        //读取Hearder 中的 cookie
                        if (!TextUtils.isEmpty(rawCookies)) {


                            //  Toast_Looper(data);
                            String sessionid = rawCookies.substring(0, rawCookies.indexOf(";"));
                            // Toast_Looper("获取sessionid!"+sessionid);
                            mSessionManager.addSessionId(sessionid);
                            //现在时间：2020年12月8日09时03分16秒 node.js 调试cookie ok
                            //添加到保存的客户端本地
                        } else {
                            rawCookies = responseHeaders.get("Cookie");
                            if (!TextUtils.isEmpty(rawCookies)) {

                                String sessionid = rawCookies.substring(0, rawCookies.indexOf(";"));
                                //Toast_Looper("获取sessionid!"+sessionid);
                                mSessionManager.addSessionId(sessionid);

                                //connect.sid=xxxxxx
                                //就是 session id
                            }

                            //如果字段是空的.
                            if (TextUtils.isEmpty(rawCookies))
                                Toast_Looper("获取Cookies失败!");
                        }
                    }


                    String dataString = new String(response.data, "UTF-8"); //获取服务器返回的数据

                    return Response.success(dataString, HttpHeaderParser.parseCacheHeaders(response));
                } catch (Exception e) {
                    return Response.error(new ParseError(e));
                }
            }

        };//提交数据  end



        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        mRequestQueue.add(mStringRequest);
    }

    //==================================
    //不能直接调用 Toast
    //==================================
    public void Toast_Looper (String toastString ) {

        final   String toastStringMsg=toastString;
        new Thread() {
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                try {
                    Toast.makeText(getApplicationContext(), toastStringMsg, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Looper.loop();
            }
        }.start();
    }
    //==================================








//模态对话框 登录后不用再次 使用本页面
    public void showPopupWindow(Context context, View parent){
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View vPopupWindow=inflater.inflate(R.layout.popupwindow, null, false);
        final PopupWindow pw= new PopupWindow(vPopupWindow,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,true);

        //OK按钮及其处理事件
        //Cancel按钮及其处理事件
        TextView btnCancel=(TextView)vPopupWindow.findViewById(R.id.dialog_cancel);
        btnCancel.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {



                pw.dismiss();



                Intent intent = new Intent();
                intent.setClass(maninfoLoginActivity.this, home_page.class);
                startActivity(intent);

            }
        });



        TextView btnOK=(TextView)vPopupWindow.findViewById(R.id.dialog_ok);

        btnOK.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                mSessionManager.EditorClear();
                //  checkoutsessionNum=false;


              //  HashMap<String, String> user = mSessionManager.getUserDetail();


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        checkUserDetail(user.get(mSessionManager.ID), user.get(mSessionManager.NAME));

                      //  Intent intent = new Intent();
                      //  intent.setClass(LoginActivity.this, LoginActivity.class);
                      //  startActivity(intent);

                    }
                }, 3000);

                pw.dismiss();

            }
        });
        //显示popupWindow对话框
        pw.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

}
