package com.jackmacc.townadmin.maninfo;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jackmacc.townadmin.DataUrl;
import com.jackmacc.townadmin.R;
import com.jackmacc.townadmin.home_page;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


//2019年10月21日
//调试无问题

//现在时间：2020年12月10日06时33分51秒
//将移植此页面为 towo 数据库

public class RegisterManActivity extends AppCompatActivity implements Validator.ValidationListener {


   // @NotEmpty(messageResId = R.string.Fileld_errorMessage)//类型
   // @Order(2)
    EditText kidname;


    @NotEmpty(messageResId = R.string.Fileld_errorMessage)//类型
    //@Order(1)//第几个
    EditText       sname;


  //  @NotEmpty(messageResId = R.string.Fileld_errorMessage)//类型
  //  @Order(3)
    EditText        age;


 //    @NotEmpty(messageResId = R.string.Fileld_errorMessage)//类型
   // @Order(4)
    EditText       mobile;


    //   @NotEmpty(messageResId = R.string.Fileld_errorMessage)//类型
    //@Email(message="Email 格式不对!")
  //  @Order(5)
    EditText         email;

    //ALPHA_NUMERIC_SYMBOLS
    //ALPHA_NUMERIC_MIXED_CASE_SYMBOLS

    @Password(min = 6,message="密码长度不低于 6 位")
    @NotEmpty(messageResId = R.string.Fileld_errorMessage)//类型
   // @Order(6)
    EditText       pass;


    @ConfirmPassword (message="重新输入一边密码进行核对!")
    @NotEmpty(messageResId = R.string.Fileld_errorMessage)//类型
   // @Order(7)
    EditText       CF_pass;



    SessionManager mSessionManager;
    StringRequest mStringRequest;
    RequestQueue mRequestQueue;
    HashMap<String, String> user;
    ProgressBar mProgressBar;
    Button reg_bn;

    //数据路径
    DataUrl dataUrl;

    Validator validator;


    //表单检查后执行
    @Override
    public void onValidationSucceeded() {

            //  final String kidname1   = kidname.getText().toString().trim();
        final String sname_to     = sname.getText().toString().trim();
         //   final String age1       = age.getText().toString().trim();
        //   final String mobile1    = mobile.getText().toString().trim();
        //  final String email1     = email.getText().toString().trim();
        final String pass_to      = pass.getText().toString().trim();
        final String CF_pass_to = CF_pass.getText().toString().trim();


        //避免重复点击 处理完成前 不显示 注册按钮
        mProgressBar.setVisibility(View.VISIBLE);
        reg_bn.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
                @Override
           public void run() {
                    //send TOserver
                    //验证完成后执行
                    insertUser( sname_to,pass_to);
            }
        }, 3000);


    }

    //表单效验错误
    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        mProgressBar.setVisibility(View.GONE);
        reg_bn.setVisibility(View.VISIBLE);
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_maninfo_lite);
        //activity_register_maninfo_lite
        //activity_register_man
        Objects.requireNonNull(getSupportActionBar()).hide();
     //   getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //需要构建
        dataUrl=new DataUrl();
        mSessionManager = new SessionManager(this);
        user    = mSessionManager.getUserDetail();
      //  kidname =findViewById(R.id.kidname);
        sname   =findViewById(R.id.sname);
     //   age     = findViewById(R.id.age);
     //   email   =findViewById(R.id.email);
      //  mobile  =findViewById(R.id.mobile);
        pass    =findViewById(R.id.password);
        CF_pass=findViewById(R.id.password2);
//表单效验
        validator = new Validator(this);
        validator.setValidationListener(this);
        reg_bn =findViewById(R.id.reg);
        mProgressBar = findViewById(R.id.manReg_progressBar);
        mProgressBar.setVisibility(View.GONE);
        mRequestQueue= Volley.newRequestQueue(RegisterManActivity.this);
        //取出数据
        Intent getIntent = getIntent();
        String name_get = getIntent.getStringExtra("name");
        String pass_get=getIntent.getStringExtra("pass");

        sname.setText(name_get);
        pass.setText(pass_get);

        //右侧图标 密码显示
        pass.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片

                Drawable drawable = pass.getCompoundDrawables()[2];
                //如果右边没有图片，不再处理
                if (drawable == null)
                    return false;
                //如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (event.getX() > pass.getWidth()
                        - pass.getPaddingRight()
                        - drawable.getIntrinsicWidth()){
                    pass.setInputType(128);
                }
                return false;
            }
        });





        //提交注册按钮
        reg_bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();


        //需要堆已经登录的用户进行限制注册
        if (mSessionManager.isLoggin()) {
            Intent intent = new Intent();
            intent.setClass( getApplicationContext(), home_page.class);
            startActivity(intent);

        }
    }

    //插入注册的用户数据
    private void insertUser(//final String kidname1,
                            final String sname1,
                           // final String age1,
                           // final String mobile1,
                          //  final String email1,
                            final String pass1
                            ) {



             //   dataUrl.getHostpath()+dataUrl.regPath;

        mStringRequest =new StringRequest
                ( StringRequest.Method.POST, dataUrl.getHostNewspath()+ File.separator+dataUrl.js_regPath_lite,

                        new Response.Listener<String>() {


                            @Override
                            public void onResponse(String response) {


                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String success = jsonObject.getString("success"); //约定
                                    if (success.equals("1")){
                                        String reportMsg = jsonObject.getString("message");
                                        Toast_Looper(reportMsg + "成功注册");
                                        Intent intent = new Intent();
                                        intent.setClass(RegisterManActivity.this, maninfoLoginActivity.class);
                                        startActivity(intent);
                                        mProgressBar.setVisibility(View.GONE);
                                        reg_bn.setVisibility(View.VISIBLE);


                                    }
                                    if(success.equals("0")){
                                        mProgressBar.setVisibility(View.GONE);
                                        reg_bn.setVisibility(View.VISIBLE);
                                        String reportMsg = jsonObject.getString("message");
                                        Toast_Looper("发生错误:"+reportMsg);
                                        mProgressBar.setVisibility(View.GONE);
                                        reg_bn.setVisibility(View.VISIBLE);
                                    }

                                    if(success.equals("5")){
                                        mProgressBar.setVisibility(View.GONE);
                                        reg_bn.setVisibility(View.VISIBLE);
                                        String reportMsg = jsonObject.getString("message");
                                        Toast_Looper("发生错误:"+reportMsg);
                                        sname.setError("用户名已经被使用");
                                        mProgressBar.setVisibility(View.GONE);
                                        reg_bn.setVisibility(View.VISIBLE);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {


                                    VolleyError_show(error);


                            }
                        }

                )

        {
            protected Map<String,String> getParams()throws AuthFailureError {
                Map<String,String> params=new HashMap<String,String>();
              //  params.put("kidname",kidname1);
                params.put("sname",sname1);
              //  params.put("age",age1);
              //  params.put("mobile",mobile1);
             //   params.put("email",email1);
                params.put("pass",pass1);

                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                // TODO Auto-generated method stub
                String str = null;
                str = new String(response.data, StandardCharsets.UTF_8);
                // str=response.data.toString();
                return Response.success(str, HttpHeaderParser.parseCacheHeaders(response));
            }


            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=utf-8"; //+ this.getParamsEncoding();
            }


        };//mStringRequest




        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQueue.add(mStringRequest);





    }

    public void  VolleyError_show(VolleyError error){

        if (error instanceof NetworkError){

            showMesage("NetworkError"+error.toString());
            Log.e("Register Activity","NetWork Error: "+error.toString());

        }else if(error instanceof ServerError){
            showMesage("serverError:"+error.toString());
            Log.e("Register Activity","ServerError: "+error.toString());

        }else if(error instanceof AuthFailureError){
            showMesage("AuthFailureError:"+error.toString());
            Log.e("Register Activity","AuthFailureError: "+error.toString());
        }else if(error instanceof ParseError){
            showMesage("ParseError:"+error.toString());
            Log.e("Register Activity","ParseError: "+error.toString());

        }else if(error instanceof NoConnectionError){
            showMesage("NoConnectionError:"+error.toString());
            Log.e("Register Activity","NoConnectionError: "+error.toString());

        }else if(error instanceof TimeoutError){
            showMesage("TimeoutError:"+error.toString());
            Log.e("Register Activity","TimeoutError: "+error.toString());

        }

    }

    public void showMesage(String msg){

        //PrintWriter

        Toast.makeText(RegisterManActivity.this,msg,Toast.LENGTH_LONG).show();
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




}
