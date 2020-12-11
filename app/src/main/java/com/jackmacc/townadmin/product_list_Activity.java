package com.jackmacc.townadmin;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.jackmacc.townadmin.Tool.WindowTool.hiddenActionBar;
import static com.jackmacc.townadmin.Tool.showMessage.Toast_Looper;

public class product_list_Activity extends AppCompatActivity {

    //下拉刷新
    private static final int REFRESH_COMPLETE = 8777;
    private SwipeRefreshLayout refreshLayout;

    private RecyclerView mProduct_list_RV;
    product_data_RecycleViewAdpater recycleViewAdpater;
    private DataUrl dataUrl;
    private TextView product_name,product_price,product_dec;
    private CircleImageView product_image;

    List<Product_data> product_data_List;

    public final int FOR_EDIT_RESULT_BACK=7777;
    public final int FOR_NEW_RESULT_BACK=7776;
    public final int FOR_DELETE_RESULT_BACK=7779;

    Dialog Product_mDialog;





    //底部
    //布局中添加 BottomNavigationView
    //布局中,配置 菜单文件 menu  xml

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Intent intent=new Intent();
                switch (item.getItemId()){

                    case R.id.new_product:
                        intent.setClass(getApplicationContext(),new_product.class);
                        //addswitch =new
                        intent.putExtra("newEditSwitch","new");

                        //  mContext.startActivity(intent);
                        //发起ForResult
                        startActivityForResult(intent,FOR_NEW_RESULT_BACK);


                        return true;

                    case R.id.home_page:
                        intent.setClass(getApplicationContext(),home_page.class);
                        startActivity(intent);
                        return true;

                    case R.id.option_page:
                        intent.setClass(getApplicationContext(),option_page.class);
                        startActivity(intent);
                        return true;
                }

                return false;

            }



    };

    String edit_pos;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);

         if (resultCode == RESULT_OK) { //正确返回,
            //现在时间：2020年11月13日11时10分17秒
            //更新 对话框.
             //注意取值从 data 中
            // Toast_Looper("产品ID:" + product_id+"位置:" +edit_pos, this);

            if (requestCode==FOR_EDIT_RESULT_BACK){
               if(data.getStringExtra("database").equals("update")) {
                    String product_id = data.getStringExtra("product_id");//ok
                    edit_pos = data.getStringExtra("edit_pos");
                    //取得数据后要对对话框进行更新.
                    //___________________________________分隔_______________________________________
                    //根据 id 读取
                    getWebUserProduct_data(product_id);
               }

            }
            if(requestCode==FOR_NEW_RESULT_BACK){
               if(data.getStringExtra("database").equals("insert")) {
               REFRESH_readProduct_data_fromWebDB(this);

            }

             if(requestCode==FOR_DELETE_RESULT_BACK){
                //删除返回

             }
            }


              //  Toast_Looper("来自:" +FOR_EDIT_RESULT_BACK,this);
         }

    }



    //___________________________________分隔_______________________________________
    //读取product 数据列表
    private void REFRESH_readProduct_data_fromWebDB(Context X_context) {
        //可以对列表,进行分类 ,读取控制

        StringRequest stringRequest=new StringRequest(Request.Method.POST,dataUrl.getHostpath() + File.separator+ dataUrl.ReadProduct_List,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //得到远程服务器的 回答
                        List<Product_data> temp_list=new ArrayList<>();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            //读取一个array 获取 php 接口数据

                            if (success.equals("1")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("read");
                                 ;
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String product_name     = object.getString("name").trim();
                                    String product_price    = object.getString("price").trim();
                                    String product_desc     = object.getString("desc").trim();
                                    String product_image    = object.getString("image").trim();
                                    String product_id       = object.getString("id").trim();

                                    temp_list.add(new Product_data(product_name, product_image,product_price, product_id));
                                    //将数据捆绑到数据对象 ,添加到 RV 列表
                                    //need-2 需要对一次读取的数据做限制
                                }
                                Toast_Looper("刷新...", getApplicationContext());

                                //现在时间：2020年11月14日19时18分00秒
                                //说明:如果加入 lins.add 是原来的就会将追加数据
                                //在 Adapter 必须 用this.list=temp_list 否则不能更新这是"魔术指针" this
                                recycleViewAdpater.addItems(temp_list);

                            }

                            if(success.equals("0")){
                                String reportMsg=jsonObject.getString("message");
                                Toast_Looper("错误描述:"+reportMsg,getApplication());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast_Looper("JSON 异常:"+e.toString(),getApplicationContext());

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast_Looper("Volley 错误回报:"+error.toString(),getApplicationContext());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //  return super.getParams();
                String manage_pwd="888888";
                String manage_name="admin";
                Map<String,String>params=new HashMap<>();
                //管理员 id name


                params.put("m_pwd",manage_pwd);
                params.put("m_name",manage_name);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap localHashMap=new HashMap();
                // HashMap<String, String> user = mSessionManager.getUserDetail();
                //   Toast_Looper("<<<<<sessid >>>>>"+user.get(mSessionManager.SESSION_ID));
                localHashMap.put("Cookie","1111");
                return localHashMap;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }



    //___________________________________分隔_______________________________________




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        hiddenActionBar(product_list_Activity.this);
        dataUrl=new DataUrl();




        product_data_List=new ArrayList<>();

        mProduct_list_RV = findViewById(R.id.product_list_rv);
        mProduct_list_RV.setLayoutManager(new LinearLayoutManager(this));
        readProduct_data_fromWenDB(this); // 使用了 product_data_List
        //使用了 recycleViewAdpater   mProduct_list_RV



        BottomNavigationView bottom_nav = (BottomNavigationView) findViewById(R.id.maninfoPage_bottomNavigation);
        bottom_nav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    //读取product 数据列表
    private void readProduct_data_fromWenDB(Context X_context) {
        //可以对列表,进行分类 ,读取控制

        StringRequest stringRequest=new StringRequest(Request.Method.POST,dataUrl.getHostpath() + File.separator+ dataUrl.ReadProduct_List,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //得到远程服务器的 回答


                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            //读取一个array 获取 php 接口数据

                            if (success.equals("1")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("read");
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String product_name     = object.getString("name").trim();
                                    String product_price    = object.getString("price").trim();
                                    String product_desc     = object.getString("desc").trim();
                                    String product_image    = object.getString("image").trim();
                                    String product_id       = object.getString("id").trim();

                                    product_data_List.add(new Product_data(product_name, product_image,product_price, product_id));
                                    //将数据捆绑到数据对象 ,添加到 RV 列表
                                    //need-2 需要对一次读取的数据做限制
                                }

                                recycleViewAdpater=  new product_data_RecycleViewAdpater(X_context,product_data_List);
                                mProduct_list_RV.setAdapter(recycleViewAdpater);
                            }

                            if(success.equals("0")){
                                String reportMsg=jsonObject.getString("message");
                                Toast_Looper("错误描述:"+reportMsg,getApplication());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast_Looper("JSON 异常:"+e.toString(),getApplicationContext());

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast_Looper("Volley 错误回报:"+error.toString(),getApplicationContext());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
              //  return super.getParams();
                String manage_pwd="888888";
                String manage_name="admin";
                Map<String,String>params=new HashMap<>();
                //管理员 id name


                params.put("m_pwd",manage_pwd);
                params.put("m_name",manage_name);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap localHashMap=new HashMap();
                // HashMap<String, String> user = mSessionManager.getUserDetail();
                //   Toast_Looper("<<<<<sessid >>>>>"+user.get(mSessionManager.SESSION_ID));
                localHashMap.put("Cookie","1111");
                return localHashMap;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    //No-7 获取产品图片(信息)
    private void getWebUserProduct_data(String product_id) {
        StringRequest stringRequest=new StringRequest(
                Request.Method.POST,
                dataUrl.getHostpath() +File.separator+ dataUrl.readProduct_data,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            //读取 array , php 服务器文件,发送,返回 success
                            if(success.equals("1")){
                                JSONArray jsonArray=jsonObject.getJSONArray("read");
                                //读取网络上图片的地址
                                for(int i=0;i<jsonArray.length();i++){
                                    //从 array 中取出
                                    JSONObject object=jsonArray.getJSONObject(i);

                                    //填充本页的 控件  产品图片,产品名称,产品价格
                                    String StrProduct_image_name=object.getString("image").trim();
                                    String StrProduct_price=object.getString("price").trim();
                                    String StrProduct_name=object.getString("name").trim();

                                    //根据 product_ id 打开一个对话框
                                    Product_mDialog=new Dialog(product_list_Activity.this);
                                    Product_mDialog.setContentView(R.layout.product_dialog_popup);
                                    //★对话框布局★
                                    Product_mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    //产品名称
                                    TextView product_dialog_name=Product_mDialog.findViewById(R.id.product_dialog_name);
                                    product_dialog_name.setText(StrProduct_name);
                                    //产品价格
                                    TextView product_dialog_price=Product_mDialog.findViewById(R.id.product_dialog_price);
                                    product_dialog_price.setText(StrProduct_price);
                                    //准备图片
                                    //布局控件
                                    CircleImageView product_dialog_image=Product_mDialog.findViewById(R.id.product_dialog_image);
                                    if(StrProduct_image_name!="null") {
                                        //路径
                                        String product_image_web_file_path;
                                        product_image_web_file_path = dataUrl.getHostpath() + File.separator + dataUrl.storeProductImgPath + File.separator + StrProduct_image_name;

                                        Glide.with(Product_mDialog.getContext()).load(product_image_web_file_path)
                                                .thumbnail()
                                                .error(R.drawable.ic_launcher_background)
                                                .into(product_dialog_image);
                                    }else{
                                        Glide.with(Product_mDialog.getContext()).load(R.drawable.ic_launcher_background).thumbnail().into(product_dialog_image);
                                    }


                                    Product_mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {

                                            //配合 onBindViewHolder(..payloads) 更新局部数据.
                                            //更新VR 列表
                                          //  Product_mDialog(viewHolder.getAdapterPosition(),"mDialog_payload");
                                            //更新当前的


                                        }
                                    });
                                    Product_mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {

                                            //配合 onBindViewHolder(..payloads) 更新局部数据.
                                            //更新VR 列表
                                            recycleViewAdpater.notifyItemChanged(Integer.valueOf(edit_pos),"mDialog_payload");
                                            //外部更新  适配器 内部.独立的
                                            //更新当前的


                                        }
                                    });

                                    Product_mDialog.show();


                                }
                            }
                            if(success.equals("0")){
                                String reportMsg=jsonObject.getString("message");
                                Toast_Looper("错误返回:"+reportMsg,getApplicationContext()); //No-8
                            }

                        } catch (JSONException e) {
                            Toast_Looper("JSON 数据异常 "+e.toString(),getApplicationContext());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast_Looper("Volley 请求错误"+error.toString(),getApplicationContext());


                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // 准备发送到服务器 PHP 接口的参数请求
                Map<String,String> params=new HashMap<>();
                //提交要编辑的 产品 id 号
                params.put("id",product_id);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //请求 数据 添加 cookie userid管理id
                //只有管理员才能获取 产品数据提交,编辑
                return super.getHeaders();
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }


}