package com.jackmacc.townadmin;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.jackmacc.townadmin.Tool.showMessage.Toast_Looper;

public  class product_data_RecycleViewAdpater extends RecyclerView.Adapter<product_data_RecycleViewAdpater.MyViewHolder> {
    //标记
    private String edit_pos="";
    private String product_id="";
    private String newEditSwitch="";
    //返回标记
    public final int FOR_EDIT_RESULT_BACK=7777;
    public final int FOR_DELETE_RESULT_BACK=7779;


    Context mContext;
    List<Product_data> m_Product_data_list;
    Dialog mDialog;

    DataUrl dataUrl=new DataUrl();


    //刷新数据 使用魔术指针
    public void addItems(List<Product_data> X_Product_data_list){

        this.m_Product_data_list=X_Product_data_list;

        notifyDataSetChanged();
    }

    //删除一个记录
    public void  removeData(int del_pos){
        this.m_Product_data_list.remove(del_pos);
        notifyItemRemoved(del_pos);
        notifyDataSetChanged();
    }

    public product_data_RecycleViewAdpater(Context mContext, List<Product_data> m_Product_data_list) {
        this.mContext = mContext;
        this.m_Product_data_list = m_Product_data_list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mContext).inflate(R.layout.product_item_cell,parent,false);
        //★列表单元布局★
        final MyViewHolder viewHolder=new MyViewHolder(view);

        //创建了一个对话框
        mDialog=new Dialog(mContext);
        mDialog.setContentView(R.layout.product_dialog_popup);
        //★对话框布局★
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //设置对话中按钮 控件数据
        //这个mConstraintLayout就是列表中 单元点击设置 激活的点击
        viewHolder.mConstraintLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {  //【1】 显示单元的点击


                edit_pos=String.valueOf(viewHolder.getAdapterPosition()); //类型必须转化   更新位置
                newEditSwitch="edit"; //发起编辑 newEditSwitch  是 edit
                product_id=m_Product_data_list.get(viewHolder.getAdapterPosition()).getId(); //产品id


                TextView product_dialog_name=mDialog.findViewById(R.id.product_dialog_name);
                TextView product_dialog_price=mDialog.findViewById(R.id.product_dialog_price);
                Button dialog_product_btn_call_edit=mDialog.findViewById(R.id.dialog_product_btn_call_edit);

                //产品名称
                product_dialog_name.setText(m_Product_data_list.get(viewHolder.getAdapterPosition()).getName());
                //产品价格
                product_dialog_price.setText(m_Product_data_list.get(viewHolder.getAdapterPosition()).getPrice());

                //现在时间：2020年11月13日10时30分53秒
                //将RV 内部的定义的一个对话框 ,发起一个  startActivityForResult 得到 new_Product.的返回,
                Intent intent =new Intent(mContext,product_list_Activity.class);

                //___________________________________分隔_______________________________________
                // 对话框【编辑按钮】
                //产品修改数据按钮
               // ___________________________________分隔_______________________________________

                dialog_product_btn_call_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {//【2】对话框按钮的点击

                        intent.setClass(mContext,new_product.class);
                        //转发标记
                        intent.putExtra("newEditSwitch",newEditSwitch);
                        intent.putExtra("product_id",product_id);
                        intent.putExtra("edit_pos",edit_pos);

                        //  mContext.startActivity(intent);
                        //发起ForResult
                        ((Activity)mContext).startActivityForResult(intent,FOR_EDIT_RESULT_BACK);

                        mDialog.dismiss();
                    }
                });

                //对话框删除 按钮
                Button dialog_product_btn_delete=mDialog.findViewById(R.id.dialog_product_btn_delete);
                //删除数据还没有开始!
                dialog_product_btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent.setClass(mContext,product_list_Activity.class);

                        //转发标记

//                        intent.putExtra("newEditSwitch","delete");
//                        intent.putExtra("product_id",product_id);
//                        intent.putExtra("edit_pos",edit_pos);

                        delete_id_form_webDB(Integer.parseInt(product_id));

                        removeData(Integer.valueOf(edit_pos));
                        //  mContext.startActivity(intent);
                        //发起ForResult
                      //  ((Activity)mContext).startActivityForResult(intent,FOR_DELETE_RESULT_BACK);


                        mDialog.dismiss();
                    }
                });

                //准备图片
                String product_image_filename;
                product_image_filename=m_Product_data_list.get(viewHolder.getAdapterPosition()).getPhoto();
                //路径
                String product_image_web_file_path;
                product_image_web_file_path = dataUrl.getHostpath()+File.separator+dataUrl.storeProductImgPath+File.separator+product_image_filename;

                //布局控件
                CircleImageView product_dialog_image=mDialog.findViewById(R.id.product_dialog_image);

                if(product_image_filename!=null)

                    Glide.with(mDialog.getContext()).load(product_image_web_file_path)
                            .thumbnail()
                            .error(R.drawable.ic_launcher_background)
                            .into(product_dialog_image);


                   // Glide.with(mDialog.getContext()).load(product_image_web_file_path).thumbnail().into(product_dialog_image);
                else
                    Glide.with(mDialog.getContext()).load(R.drawable.ic_launcher_background).thumbnail().into(product_dialog_image);


                //对话框退出的时候侦测动作 处理:
                mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        //配合 onBindViewHolder(..payloads) 更新局部数据.
                   //更新VR 列表
                       // 更新那个修改的位置的数据
                        notifyItemChanged(viewHolder.getAdapterPosition(),"mDialog_payload");
                        //更新当前的

                    }
                });

                mDialog.show();

            }
        });

        return viewHolder;
    }


    //No-8 删除产品图片(信息)
    private void delete_id_form_webDB(int position) {
        StringRequest stringRequest=new StringRequest(
                Request.Method.POST,
                dataUrl.getHostpath() +File.separator+ dataUrl.deleteProduct_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            //读取 array , php 服务器文件,发送,返回 success
                            if(success.equals("1")){
                                String reportMsg=jsonObject.getString("message");
                                Toast_Looper("数据ID 已经删除:"+reportMsg,mContext); //No-8


                            }
                            if(success.equals("0")){
                                String reportMsg=jsonObject.getString("message");
                                Toast_Looper("错误返回:"+reportMsg,mContext); //No-8
                            }

                        } catch (JSONException e) {
                            Toast_Looper("JSON 数据异常 "+e.toString(),mContext);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast_Looper("Volley 请求错误"+error.toString(),mContext);


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

        RequestQueue requestQueue= Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);


    }



    //局部更新数据
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position, @NonNull List<Object> payloads) {
      //  super.onBindViewHolder(holder, position, payloads);

        if(payloads.isEmpty()){
            onBindViewHolder(myViewHolder,position);

        }else{ //获取playloads 中的数据


            String payLoad=(String)payloads.get(0);
            //下面获取新的数据可以更新 RV

           String update_id= m_Product_data_list.get(position).getId();

           //从数据 来
         getWebProduct_data(update_id,myViewHolder, m_Product_data_list,position);

        }

    }

    //No-7 获取产品图片(信息)
    private void getWebProduct_data(String product_id,MyViewHolder myViewHolder ,List<Product_data> m_Product_data_list,int position) {
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

                                    Product_data productData=new  Product_data(StrProduct_name,StrProduct_image_name,StrProduct_price,product_id);

                                     //列表也要更新
                                    m_Product_data_list.set(position,productData);

                                    //产品名称
                                    myViewHolder.tv_name.setText(StrProduct_name);

                                    //产品价格
                                   myViewHolder.tv_price.setText(StrProduct_price);

                                    //准备图片
                                    //布局控件


                                    String product_image_file_name=StrProduct_image_name;

                                    if(product_image_file_name!="null"){
                                        String product_image_web_file_path=dataUrl.getHostpath()+ File.separator+dataUrl.storeProductImgPath+ File.separator+product_image_file_name;


                                        Glide.with(mContext).load(product_image_web_file_path)
                                                .thumbnail()
                                                .error(R.drawable.ic_launcher_background)
                                                .into(myViewHolder.img);

                                    }else{
                                        Glide.with(mContext).load(R.drawable.ic_launcher_background).thumbnail().into(myViewHolder.img);

                                    }




                                }
                            }
                            if(success.equals("0")){
                                String reportMsg=jsonObject.getString("message");
                                Toast_Looper("错误返回:"+reportMsg,mContext); //No-8
                            }

                        } catch (JSONException e) {
                            Toast_Looper("JSON 数据异常 "+e.toString(),mContext);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast_Looper("Volley 请求错误"+error.toString(),mContext);


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

        RequestQueue requestQueue= Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {

      //  notifyItemChanged(position);

      //  notifyItemRangeChanged(1,10);

        myViewHolder.tv_name.setText(m_Product_data_list.get(position).getName());
        myViewHolder.tv_price.setText(m_Product_data_list.get(position).getPrice());

        String product_image_file_name=m_Product_data_list.get(position).getPhoto();

        if(product_image_file_name!="null"){
            String product_image_web_file_path=dataUrl.getHostpath()+ File.separator+dataUrl.storeProductImgPath+ File.separator+product_image_file_name;


            Glide.with(mContext).load(product_image_web_file_path)
                    .thumbnail()
                    .error(R.drawable.ic_launcher_background)
                    .into(myViewHolder.img);

        }else{
            Glide.with(mContext).load(R.drawable.ic_launcher_background).thumbnail().into(myViewHolder.img);

        }

    }

    @Override
    public int getItemCount() {
        return m_Product_data_list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        private ConstraintLayout mConstraintLayout;

        private TextView tv_name;
        private TextView tv_price;
        private CircleImageView img;



        public MyViewHolder(@NonNull View itemView) { //提供  V 对象 是单元对象,
            super(itemView);

            mConstraintLayout=(ConstraintLayout)itemView.findViewById(R.id.product_list_cl);

            tv_name=(TextView)itemView.findViewById(R.id.product_name);
            tv_price=(TextView)itemView.findViewById(R.id.product_price);
            img=(CircleImageView)itemView.findViewById(R.id.product_image);


        }
    }
}
