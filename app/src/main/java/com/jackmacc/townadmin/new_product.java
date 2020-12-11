package com.jackmacc.townadmin;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.afollestad.materialdialogs.MaterialDialog;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.jackmacc.townadmin.Tool.WindowTool.hiddenActionBar;


/*
need-1.没有拍照前,旧图片需要 imageOld  数据才能点击编辑  ?
need-2. 取消按钮 返回? ok
need-3. 需要addswitch开关 ,需要Extra  product_id 数据 ok
need-4 新建产品后,刷新列表RV


 */

public class new_product extends AppCompatActivity {
    //验证类 声明 mSessionManager;


    public final int FOR_EDIT_RESULT_BACK=7777;


    private CircleImageView productImageView;
    //圆形图像裁剪

    private Button btnCancel;
    private TextView tv_choose,tv_product_list,tv_manager_page;
    private Button btn_upload_webdata;

    private EditText product_name,product_price,product_desc;


    private Bitmap bitmap;
    private DataUrl dataUrl;

    private String uploadProductImageSwitch="off";

    private Uri fileUri;



    //标记
    private static final int REQUEST_PICK_PHOTO=2;
    private static final int CAMERA_PIC_REQUEST=1111;
    private static final int REQUEST_PHOTO_CLIP=3;

    public static final int MEDIA_TYPE_IMAGE=1;
    public static final String IMAGE_DIRECTORY_NAME="Android File Uplaod";

    //对象保证 图片可以编辑
    private File imageOld;
    private Uri photoURI_2;
    private String imageFilePath="";

    //相机开启标记
    private boolean cameropen=false;

    //主机地址
    private String getWeb_Host;

    //上传图片处理对话框
    private ProgressDialog progressDialog;

    private   String product_id="";
    private   String edit_pos="";
    private   String database="";
    private   String newEditSwitch="";
    //更新后成功标记
    public String product_id_update_ok_switch="off";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //隐藏 bar   来自工具类
        hiddenActionBar(new_product.this);


        //获取 newEditSwitch 切换开关
        Intent intent=getIntent();
         newEditSwitch=intent.getStringExtra("newEditSwitch");

         if(newEditSwitch.equals("edit")) {
             product_id = intent.getStringExtra("product_id");
             edit_pos = intent.getStringExtra("edit_pos");
             database = intent.getStringExtra("database");
         }


        //数据范围环节  参数获取
        dataUrl=new DataUrl();
        //可以指定登录 "http://域名(调试ip):端口"

        //xml 改<de.hdodenhof.circleimageview.CircleImageView />
        //改名 profile->product
        productImageView=findViewById(R.id.civ_img_choose);
        product_name=findViewById(R.id.civ_et_name);
        product_desc=findViewById(R.id.civ_et_dec);
        product_price=findViewById(R.id.civ_et_mobile);


        //去产品列表 链接
        tv_product_list=findViewById(R.id.tv_product_list);
        tv_product_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(getApplicationContext(),product_list_Activity.class);
                startActivity(intent);
            }
        });


        //去主页 链接
        tv_manager_page=findViewById(R.id.tv_manager_page);
        tv_manager_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(getApplicationContext(),home_page.class);
                startActivity(intent);
            }
        });


        //获取地址
        getWeb_Host=dataUrl.getHostpath();

        //___________________________________分隔_______________________________________
        //修改照片进行旋转剪切
        //点击主图片进行剪切编辑
        productImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cameropen){
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                        //正确打开,否则打不开 Uri 需要授权
                        Uri photoURI= FileProvider.getUriForFile(
                                new_product.this,
                                //授权 class 要创建
                                "com.jackmacc.townadmin.MyFileProvider",
                                imageOld
                        );
                        photoURI_2=photoURI;

                        //No-1 调用剪切程序
                        photoClip(photoURI,REQUEST_PHOTO_CLIP, new_product.this);
                    }else{
                        //从文件加载, 否则读取问题
                        photoClip(Uri.fromFile(imageOld),REQUEST_PHOTO_CLIP, new_product.this);
                        //need-1 imageOld 文件需要 处理
                    }
                }
            }
        });

        //处理相机权限 操作文件的权限 多个权限打包获取
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            productImageView.setEnabled(false); //如果权限不足不能编辑图片
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },0
            );
        }
        else //开启图片编辑
        {
            productImageView.setEnabled(true);
        }

        //处理 cancel 按钮跳转
        btnCancel=findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //返回  need-2

                //更新数据成功返回
              if (product_id_update_ok_switch.equals("on")) {
                   Intent intent = new Intent();

                   //现在时间：2020年11月13日10时41分15秒 实现更新返回
                //database 的值在 上传 call 中确定
                if(database.equals("update") || database.equals("read")) {
                    intent.putExtra("product_id", product_id);
                    intent.putExtra("edit_pos", edit_pos);
                    database="update";
                    intent.putExtra("database",database);
                }
                if(database.equals("insert")) {
                    intent.putExtra("database",database);
//                    intent.setClass(getApplicationContext(),product_list_Activity.class);
//                    startActivity(intent);
//                    finish();

                    //可以用 setResult 来刷新.
                }
                 setResult(RESULT_OK, intent);

              }
                finish();


            }
        });


        //上传产品图像 更新产品数据
        //更新数据成功后,改变 按钮显示
        btn_upload_webdata=findViewById(R.id.btn_upload_webdata);
        btn_upload_webdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //一个对话框显示 上传异步等待.... 等待框
                progressDialog=new ProgressDialog(new_product.this);
                progressDialog.setTitle("上传图片");
                progressDialog.setMessage("请稍等...");
                progressDialog.show();

                //发送图片到服务器
                StringRequest stringRequest=new StringRequest(
                        Request.Method.POST,
                        dataUrl.getHostpath() +File.separator+ dataUrl.writeProduct_data,//上传的目标 web (php) 处理地址
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //上传 成功返回了
                                //关闭等待框
                                 Toast_Looper(response);

                                product_id_update_ok_switch="on";
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    database = jsonObject.getString("database");

                                    if (database.equals("update")) {
                                        btnCancel.setText("刷新回退");
                                    }

                                    if (database.equals("insert")){
                                        btnCancel.setText("刷新产品列表");
                                    }
                                } catch (JSONException e) {

                                    e.printStackTrace();
                                }

                             //   btnCancel.setText("刷新...");
                                   progressDialog.dismiss();
                               // Toast_Looper("成功提交数据");
                             // Toast_Looper(response);

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //上传 失败返回了
                        progressDialog.dismiss(); //关闭等待框
                        Toast.makeText(getApplicationContext(),"error:"+error.toString(),Toast.LENGTH_LONG).show();

                    }
                }

                ){
                    //发送 Post 数据 到服务器 准备
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        //return super.getParams();
                        Map<String,String> params=new HashMap<>();
                        //把图片 转换成  string
                        //No-2 调用 图像转化程序

                     //开启编辑图片
                        if(   uploadProductImageSwitch.equals("on")){


                            String imageDataString=imageToString(bitmap);
                            //给参数 赋值
                            params.put("image",imageDataString);

                            params.put("uploadProductImageSwitch","on");

                        }
                        //无图片
                        if(   uploadProductImageSwitch.equals("off")){
                            params.put("uploadProductImageSwitch","off");
                        }


                        //新建 和 编辑 一样的参数
                        params.put("price",product_price.getText().toString());
                        params.put("name",product_name.getText().toString());
                        params.put("desc",product_desc.getText().toString());


                        //获取 newEditSwitch 切换开关
                        if (newEditSwitch.equals("edit")) {
                            //加载图片
                            params.put("id",product_id);
                            params.put("addswitch","update");
                        }
                        if (newEditSwitch.equals("new")) {
                            //加载图片
                            params.put("addswitch","insert");
                        }
                       // params.put("id", 加入 id 参数 以及其他参数
                        return params;
                    }

                    //准备 Headers

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        //return super.getHeaders();
                        //如果登录后,可以从 cookie 中获取 session id 通过Header 传出去
                        HashMap localHashMap=new HashMap();
                        //加入 mSessionManager.SESSION_ID 到 "Cookie"
                        return localHashMap;
                    }
                };

                //将准备好的 StringRequest 添加到请求序列调用
                RequestQueue requestQueue= Volley.newRequestQueue(new_product.this);
                requestQueue.add(stringRequest);
            }
        });


        //拍摄 相册,选取功能
        tv_choose=findViewById(R.id.tv_choose);
        productImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog
                        .Builder(new_product.this)
                        .title("获取照片")
                        //此资源来自于  values/arrays.xml 定义
                        .items(R.array.uploadImages)
                        .itemsIds(R.array.itemIds)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                switch (position){
                                    case 0:
                                        //从相册选择 照片
                                        Intent galleryIntent=new Intent(
                                                Intent.ACTION_PICK,
                                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //从它返回

                                        startActivityForResult(galleryIntent,REQUEST_PICK_PHOTO);//结果处理标记
                                        break;
                                    case 1:
                                        //No-3 激活相机 去拍照
                                        captureImage();
                                        break;
                                    case 3:
                                        //删除当前图片后显示 默认图片
                                        productImageView.setImageResource(R.drawable.ic_launcher_background);
                                        break;
                                }
                            }
                        }).show();
            }
        });


        //编辑切换开关
        if (newEditSwitch.equals("edit")) {
            //加载图片
            getWebUserProductImg(product_id); //是编辑状态 ,读取数据填充
        }

       if (newEditSwitch.equals("new")) {

        }

            //-----------------------------------------
            //从 Web 获取 产品图片 的原始数据



    }//onCreate 结束
   // ___________________________________分隔_______________________________________

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==0){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                productImageView.setEnabled(true);
            }
        }
    }

    //接收处理器

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            switch (requestCode){
                case CAMERA_PIC_REQUEST:
                    //从拍照程序返回
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                        bitmap= BitmapFactory.decodeFile(imageFilePath);

                        cameropen=true;
                        //图像尺寸 处理
                        bitmap=setReducedImageSize(); //No-9 调用call

                        //旋转图片校正
                        bitmap=rotateImage(bitmap);
                        //No-10 调用call
                        //必须保存这个文件才能去编辑
                        
                      //  save_bitmap_to_File(bimap);
                        bitmap= compressImage(bitmap); //对上传的图片进行压缩.
                        productImageView.setImageBitmap( bitmap);
                        //处理的程序显示的是一个  临时的bitmap 文件
                        //这个程序已经处理了 productImageView
                        //___________________________________分隔_______________________________________
                        try{
                        //【3】一个随机文件名
                        Random random=new Random();
                        String bitmap_name="ClipImageFile"+String.valueOf(random.nextInt(Integer.MAX_VALUE));
                        //【4】获取存储目录
                        File storageDirectory=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        //【5】目录下建一个文件
                        File file=new File(storageDirectory.getAbsoluteFile()+bitmap_name+".jpg");
                        //【6】用这个文件开一个输出流
                        FileOutputStream outputStream=new FileOutputStream(file);

                        //【7】为剪切  图片将个变量填充
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100, outputStream);
                        imageOld=file;
                        //【8】一个文件路径
                        imageFilePath=storageDirectory.getAbsolutePath()+bitmap_name+".jpg";

                        //图片的地址
                        Toast_Looper(imageFilePath);




                        }catch (IOException e)
                        {
                            e.printStackTrace();
                            cameropen=false;

                        }

                        //___________________________________分隔_______________________________________



                        uploadProductImageSwitch="on";
                    }else{
                        cameropen=true;
                        bitmap=setReducedImageSize();
                        bitmap=get_rotateImage_bitmap(bitmap); //No-11 call
                        productImageView.setImageBitmap(bitmap);

                        uploadProductImageSwitch="on";

                    }

                    break;
                case REQUEST_PICK_PHOTO:
                    if(data!=null){
                        Uri selectedImage=data.getData();
                        if(selectedImage!=null){
                            try{
                                //【1】从选择的图片获取  bitmap 映像
                                bitmap=getBitmapFormUri(new_product.this,selectedImage);
                                //No-12

                                //【2】显示图片
                                productImageView.setImageBitmap(bitmap); //显示图片

                                cameropen=true;

                                //【3】一个随机文件名
                                Random random=new Random();
                                String bitmap_name="ClipImageFile"+String.valueOf(random.nextInt(Integer.MAX_VALUE));
                                //【4】获取存储目录
                                File storageDirectory=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                                //【5】目录下建一个文件
                                File file=new File(storageDirectory.getAbsoluteFile()+bitmap_name+".jpg");
                                //【6】用这个文件开一个输出流
                                FileOutputStream outputStream=new FileOutputStream(file);

                                //【7】为剪切  图片将个变量填充
                                bitmap.compress(Bitmap.CompressFormat.JPEG,100, outputStream);
                                imageOld=file;
                                //【8】一个文件路径
                                imageFilePath=storageDirectory.getAbsolutePath()+bitmap_name+".jpg";

                                //图片的地址
                                Toast_Looper(imageFilePath);
                                uploadProductImageSwitch="on";


                            }catch (IOException e)
                            {
                                e.printStackTrace();
                                cameropen=false;

                            }
                        }

                    }
                    break;

                    //处理图片 剪切后
                case REQUEST_PHOTO_CLIP:

                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                        Bitmap image_tmp=null;
                        try{
                            image_tmp=BitmapFactory.decodeStream(getContentResolver().openInputStream(photoURI_2));

                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }

                        //为旋转
                        bitmap=image_tmp;
                        productImageView.setImageURI(photoURI_2);
                        cameropen=true;
                        uploadProductImageSwitch="on";
                    }else{
                        try{
                            Bitmap image_tmp=BitmapFactory.decodeFile(imageFilePath);
                            bitmap=image_tmp;
                            cameropen=true;

                            productImageView.setImageBitmap(image_tmp);

                            uploadProductImageSwitch="on";

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    break;


            }
        }
    }

    //No-12
    //通过uri 获取图片并进行压缩
    private Bitmap getBitmapFormUri(new_product newproduct, Uri selectedImageUri) throws IOException {

        InputStream inputStream= newproduct.getContentResolver().openInputStream(selectedImageUri);
        BitmapFactory.Options onlyBoundsOptions=new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds=true;
        onlyBoundsOptions.inDither=true;
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeStream(inputStream,null,onlyBoundsOptions);
        inputStream.close();
        int originalWidth=onlyBoundsOptions.outWidth;
        int originalHeight=onlyBoundsOptions.outHeight;
        if((originalWidth==-1)||(originalHeight==-1)){
            return null;
        }
        //图片分辨率
        float hh=800f;
        float ww=480f;
        //缩放比
        int be=1; //不缩放
        if(originalWidth>originalHeight && originalWidth>ww){
            //宽度大,根据宽度固定大小缩放
            be=(int)(originalWidth/ww);

        }else if(originalWidth<originalHeight && originalWidth>hh){
            //高度,固定大小缩放
            be=(int)(originalHeight/hh);

        }
        if(be<=0)
            be=1;
        //比例压缩
        BitmapFactory.Options bitmapOptions=new BitmapFactory.Options();
        bitmapOptions.inSampleSize=be; //设置缩放比
        bitmapOptions.inDither=true;
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;
        inputStream= newproduct.getContentResolver().openInputStream(selectedImageUri);
        Bitmap bitmap=BitmapFactory.decodeStream(inputStream,null,bitmapOptions);
        inputStream.close();

        return compressImage(bitmap); //进行质量压缩
        //No-13

    }

    //No-13
    //图像小于欧100k ,大于继续压缩,10%逐渐压缩
    private Bitmap compressImage(Bitmap compressimage) {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        compressimage.compress(Bitmap.CompressFormat.JPEG,100,baos);
        //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options=100;

        while (baos.toByteArray().length/1024>100){//循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//清空 baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            compressimage.compress(Bitmap.CompressFormat.JPEG,options,baos);
            //这里压缩options%，把压缩后的数据存放到baos中
            options -=10;//每次都减少10

        }
        ByteArrayInputStream isBm=new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap=BitmapFactory.decodeStream(isBm,null,null);
        return bitmap;


    }
    //No-11
    private Bitmap get_rotateImage_bitmap(Bitmap bitmap) {
        ExifInterface exifInterface=null;

        try {
                exifInterface=new ExifInterface(imageFilePath);
        } catch (IOException e) {
                e.printStackTrace();
        }
        int orientation=exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix=new Matrix();

        switch (orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            default:

        }
        Bitmap rotatedBitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return rotatedBitmap;

    }

    //No-10 旋转
    private Bitmap rotateImage(Bitmap bitmap) {
        ExifInterface exifInterface=null;
        try{
            exifInterface=new ExifInterface(imageFilePath);

        }catch (IOException e){
            e.printStackTrace();
        }

        int orientation=exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Matrix matrix=new Matrix();

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            default:
        }


        Bitmap rotatedBitmap=Bitmap.createBitmap(bitmap,0,0,
                bitmap.getWidth(),bitmap.getHeight(),matrix,true);

        return rotatedBitmap;
    }


    //___________________________________分隔_______________________________________

    //No-9 设置尺寸
    private Bitmap setReducedImageSize() {
        int targetImageViewWidth=productImageView.getWidth();
        int targetImageViewHeight=productImageView.getHeight();

        BitmapFactory.Options bmOptions=new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds=true;

        BitmapFactory.decodeFile(imageFilePath,bmOptions);
        int cameraImageWidth=bmOptions.outWidth;
        int cameraImageHeight=bmOptions.outHeight;

        int scaleFactor=Math.min(cameraImageWidth/targetImageViewWidth,cameraImageHeight/targetImageViewHeight);
        bmOptions.inSampleSize=scaleFactor;
        bmOptions.inJustDecodeBounds=false;

        return BitmapFactory.decodeFile(imageFilePath,bmOptions);


    }



    //No-7 获取产品图片(信息)
    private void getWebUserProductImg(String product_id) {
        StringRequest stringRequest=new StringRequest(
                Request.Method.POST,
                dataUrl.getHostpath() +File.separator+ dataUrl.readProduct_data,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                             database ="read";
                            //读取 array , php 服务器文件,发送,返回 success
                            if(success.equals("1")){
                                JSONArray jsonArray=jsonObject.getJSONArray("read");
                                //读取网络上图片的地址
                                String StrProduct_image_name="" ;
                                String StrProduct_price="" ;
                                String StrProduct_name="" ;
                                String StrProduct_desc="";
                                for(int i=0;i<jsonArray.length();i++) {
                                    //从 array 中取出
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    //填充本页的 控件  产品图片,产品名称,产品价格
                                    StrProduct_image_name = object.getString("image").trim();
                                     StrProduct_price = object.getString("price").trim();
                                    StrProduct_name = object.getString("name").trim();
                                     StrProduct_desc = object.getString("desc").trim();
                                    //String StrProduct_name=object.getString("name").trim();
                                    //String StrProduct_name=object.getString("name").trim();
                                }

                                product_name.setText(StrProduct_name);
                                product_price.setText(StrProduct_price);
                                product_desc.setText(StrProduct_desc);

                                if(StrProduct_image_name!="null"){
                                            String product_photo_webfile_path=
                                                dataUrl.getHostpath()+File.separator+dataUrl.storeProductImgPath+File.separator+StrProduct_image_name;

                                        Glide.with(new_product.this).load(product_photo_webfile_path)
                                                .thumbnail().centerInside()  //圆形图像.对齐形式
                                                .error(R.drawable.ic_launcher_background)
                                                .into(productImageView);

                                }else{
                                    Glide.with(new_product.this).load(R.drawable.ic_launcher_background).thumbnail().into(productImageView);
                                }
                                    cameropen=false;

                            }
                            if(success.equals("0")){
                                String reportMsg=jsonObject.getString("message");
                                Toast_Looper("错误返回:"+reportMsg); //No-8
                            }

                        } catch (JSONException e) {
                            Toast_Looper("JSON 数据异常 "+e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                     @Override
                    public void onErrorResponse(VolleyError error) {
                         Toast_Looper("Volley 请求错误"+error.toString());


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

    //No-8
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




    //___________________________________分隔_______________________________________
    //下面是功能函数部分
    //No-1 =====调用剪切程序======================

    public void photoClip(Uri uri, int requestCode, AppCompatActivity a) {
        Intent intent = new Intent();//调用系统自带的图片裁剪
        intent.setAction("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");//设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("aspectX", 3);//宽高比
        intent.putExtra("aspectY", 4);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 400);
        intent.putExtra("return-data", false);//return置为false，获取截图保存的uri
        //数据大不能直接以 intent 的"data" 返回  设置 为 false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (intent.resolveActivity(getPackageManager()) != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI_2);
            }else{
                return;
            }
        }else{
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageOld));
        }
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        a.startActivityForResult(intent, requestCode); //剪切参数 回调.

    }
    //No-2 调用 图像转化程序
    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        byte[] imageBytes=outputStream.toByteArray();
        //android.util.Base64
        String encodedImageDataString= Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);
        return encodedImageDataString;
    }

    //No-3 照相机捕捉 调用
    private void captureImage(){
        if (Build.VERSION.SDK_INT>21) {
            Intent callCameraApplicaitonIntent = new Intent();
            callCameraApplicaitonIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//intent 去拍照 程序
            callCameraApplicaitonIntent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
            //使用拍照
            File photoFile;

            //拍照文件的处理
            if (callCameraApplicaitonIntent.resolveActivity(getPackageManager()) != null) {
                try {
                    //【1】拍照成功返回文件
                    photoFile = createImageFile(); //No-4 返回文件
                } catch (IOException e) {
                    Toast.makeText(new_product.this, "照片创建失败!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Uri outputUri;
                //判断代码是 N 更高的版本
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //使用FileProvider 声明在 AndroidManifest 中.以及  xml 目录下
                    //【2】将文件换成 SDK N 安全合格的  Uri
                    //现在时间：2020年11月14日21时17分27秒
                    outputUri = FileProvider.getUriForFile(
                            this,
                            "com.jackmacc.townadmin.MyFileProvider",
                            photoFile
                    );
                    photoURI_2 = outputUri;//保留这个 Uri
                    //在callCameraApplicationIntent
                    //拍照 intent 中 附带 Extra 信息
                    callCameraApplicaitonIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
                    //这个参数是程序返回输出的文件路径
                    
                } else {//sdk 版本低
                    outputUri = Uri.fromFile(photoFile);
                    fileUri = outputUri;
                    //保留这个uri
                    callCameraApplicaitonIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
                }

                startActivityForResult(callCameraApplicaitonIntent, CAMERA_PIC_REQUEST);
                //发送拍照 标记给 ,给处理 call
            }
            //Build.VERSION.SDK_INT 低于 21
        }else{
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //No-5 调用
                fileUri=getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);

                //处理好后交给  结果处理call
                startActivityForResult(intent,CAMERA_PIC_REQUEST);
         }
     }




    //No-4 返回文件
    private File createImageFile()throws IOException {
        //创建一个,"不冲突的文件名"
        //创建一个唯一的文件名
        String timeStamp=new SimpleDateFormat("yyyMMdd_HHmmSS", Locale.getDefault()).format(new Date());
        String imageFileName="img_"+timeStamp+"_product_";
        //指定环境
        //创建文件的确切路径
        File storageDirectory=getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        //创建一个存储目录,如果不存在
        if(!storageDirectory.exists())
                storageDirectory.mkdir();

        //使用前缀,后缀和目录创建文件
        File imageFile=File.createTempFile(imageFileName,".jpg",storageDirectory);

        //文件名 注意
        imageFilePath=imageFile.getAbsolutePath();
        return imageFile;

    }

    //No-5 调用 创建文件uri 从 图像/视频
    private Uri getOutputMediaFileUri(int mediaTypeImage) {
        //No-6
        return Uri.fromFile(getOutputMediaFile(mediaTypeImage));
    }

    //No-6 返回图像 和视频
    private static File getOutputMediaFile(int type) {

        //SD 卡位置
        File mediaStorageDir=new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);


        //创建存储目录 如果存在
        if(!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdir()){
                Log.d("错误!", "失败创建 "  + IMAGE_DIRECTORY_NAME + " 目录");
                return null;

            }
        }

        //创建 媒体文件名
        String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmSS",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if(type==MEDIA_TYPE_IMAGE){
            mediaFile=new File(mediaStorageDir.getPath()+File.separator
            +"img_"+timeStamp+"_product_"+".jpg");
        }else{
            return null;

        }
        return mediaFile;


    }


}
