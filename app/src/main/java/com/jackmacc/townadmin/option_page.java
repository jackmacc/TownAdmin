package com.jackmacc.townadmin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jackmacc.townadmin.maninfo.SessionManager;
import com.jackmacc.townadmin.maninfo.maninfoLoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class option_page extends AppCompatActivity {

    DataUrl dataUrl;

    SessionManager mSessionManager; //cookie 管理模块
    HashMap<String, String> user;


    String newEditSwitch="edit";
    EditText ip1,ip2,ip3,ip4;
    TextView setip;
    Button  bn_login_man,bn_logout_man,bn_product_info_edit;

    public final int FOR_EDIT_RESULT_BACK=7777;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_page);


        mSessionManager = new SessionManager(this); //获取SR
        mSessionManager.checkLogin();  //未登录被退回到登录....
        //读取 session sp中 信息打包
        user = mSessionManager.getUserDetail();

        //路径
        dataUrl=new DataUrl();



        ip1=findViewById(R.id.ip1);
        ip2=findViewById(R.id.ip2);
        ip3=findViewById(R.id.ip3);
        ip4=findViewById(R.id.ip4);

        setip=findViewById(R.id.tv_setIp);

        setip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newHost=ip1.getText()+"."+ip2.getText()+"."+ip3.getText()+"."+ip4.getText();
             dataUrl.setHostpathAll(newHost);


           if  ( dataUrl.getHostIP().equals(newHost))
                Toast_Looper("更新成功");

          else
                Toast_Looper("更新失败");

            }
        });

        bn_login_man=findViewById(R.id.bn_login_man);
        bn_login_man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(option_page.this, maninfoLoginActivity.class);
                startActivity(intent);

                //finish();

            }
        });
        bn_logout_man=findViewById(R.id.bn_logout_man);
        bn_logout_man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                checkoutSession();

                //finish();

            }
        });

        //编辑个人信息
        bn_product_info_edit=findViewById(R.id.bn_product_info_edit);
        bn_product_info_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent=new Intent(getApplicationContext(),product_info_edit.class);

            intent.putExtra("product_id",user.get(mSessionManager.ID));
            intent.putExtra("newEditSwitch",newEditSwitch);

            startActivity(intent);
             //   option_page.this.startActivityForResult(intent,FOR_EDIT_RESULT_BACK);





                //finish();

            }
        });


        if( mSessionManager.isLoggin()) {


            //加载数据库信息 2019年10月13日 加载用户信息页面
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                  //  getUserDetail(user.get(mSessionManager.ID));
                    //由数据库读取用户信息.
                }
            }, 3000);
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==FOR_EDIT_RESULT_BACK){
            if(data.getStringExtra("database").equals("update")) {
                String product_id = data.getStringExtra("product_id");//ok
             //   edit_pos = data.getStringExtra("edit_pos");
                //取得数据后要对对话框进行更新.
                //___________________________________分隔_______________________________________
                //根据 id 读取
             //   getWebUserProduct_data(product_id);

                Toast_Looper("编辑返回");
            }

        }

    }

    //=============================
    //checkout 弹出窗口
    //===============================

    public void checkoutSession(){
        new AlertDialog.Builder(this).setTitle("提示").setMessage("退出当前账户身份回到主界面...")

                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which){

                     //   HashMap<String, String> user = mSessionManager.getUserDetail();
                        Toast_Looper("<<<<<sessid >>>>>"+user.get(mSessionManager.SESSION_ID));
                        mSessionManager.EditorClear();  //清楚本地session 数据
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                checkUserDetail( user.get(mSessionManager.ID),user.get(mSessionManager.NAME));
                                //   Toast_Looper( user.get(mSessionManager.ID)+user.get(mSessionManager.NAME));
                            }
                        }, 3000);

                        //  mSessionManager.logout();
                        dialog.dismiss();

                        //直接返回到主界面


                        //   maninfo_main_Activity.this.finish();




                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                }).show();
    }


    //checkUserDetail 退出书服务器上的 session 数据
    private void checkUserDetail(String userid,String sname){

        //   final ProgressDialog progressDialog=new ProgressDialog(this);
        //  progressDialog.setMessage("Loading...");
        //  progressDialog.show();

        //URL_checkoutText 是退出 session_id
        StringRequest stringRequest=new StringRequest(Request.Method.POST, dataUrl.getHostNewspath()+ File.separator+dataUrl.js_checkoutPath,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {//得到远程服务器的 回答
                       // response= toUtf8(response);
                        try {
                            JSONObject jsonObject=new JSONObject(response); //转换成 json 对象
                            String success =jsonObject.getString("success");
                            Toast_Looper("退出消息:"+response);



                            if(success.equals("3")){
                                String reportMsg = jsonObject.getString("message");
                                Toast_Looper("正确返回:"+reportMsg);
                                mSessionManager.EditorClear();

                                Intent intent = new Intent();
                                intent.setClass( option_page.this, home_page.class);
                                startActivity(intent);
                            }

                            if(success.equals("4")){
                                String reportMsg = jsonObject.getString("message");
                                Toast_Looper("错误返回:"+reportMsg);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            //   progressDialog.dismiss();
                            Toast_Looper("检查返回异常 Json 为"+e.toString());

                        }
                        //  progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  progressDialog.dismiss();
                        Toast_Looper("checkUser Error"+error.toString());

                    }
                })
        {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params=new HashMap<>();
                params.put("id",userid);
                params.put("sname",sname);
                params.put("checkout","out");
                //   Toast_Looper("id:"+userid+"sname"+sname);
                return params;

            }




            public String getBodyContentType() {
                return "application/x-www-form-urlencoded"; //+ this.getParamsEncoding();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //请求头部添加 cookie  将本地 的 session id 传过去
                //2019年10月9日 添加 session id 头


                HashMap localHashMap=new HashMap();
              //  HashMap<String, String> user = mSessionManager.getUserDetail();
                //  Toast_Looper("<<<<<sessid >>>>>"+user.get(mSessionManager.SESSION_ID));
                localHashMap.put("Cookie",user.get(mSessionManager.SESSION_ID));

                //加上这个 session id 就可以读取 session 数据.否则没有数据可读判定
                //判定当前退出-没有登录
                //现在时间：2020年12月8日13时13分30秒
                return localHashMap;

            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

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