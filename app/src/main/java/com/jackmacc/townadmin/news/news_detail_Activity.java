package com.jackmacc.townadmin.news;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.jackmacc.townadmin.DataUrl;
import com.jackmacc.townadmin.R;
import com.jackmacc.townadmin.Tool.Utils;
import com.jackmacc.townadmin.api.ApiClient;
import com.jackmacc.townadmin.api.ApiInterface;
import com.jackmacc.townadmin.models.newsex;
import com.jackmacc.townadmin.models.returnback;
import com.jackmacc.townadmin.models.townNews;
import com.jackmacc.townadmin.newsex.newsex_adapter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jackmacc.townadmin.Tool.Utils.convertTo_text_plain_RequestBody;

public class news_detail_Activity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private String imageFilePath;

    private ImageView imageView;
    private TextView appbar_title, appbar_subtitle, date, time, title,tv_desc,tv_content;
    private TextView tv_openDialogNewsex;//扩展新闻 点击
    private boolean isHideTolbarView = false;
    private FrameLayout date_behavior;
    private LinearLayout titleAppbar;
    private AppBarLayout appBarLayout;

    private  List<newsex> newsexeList= new ArrayList<>  ();

    private Dialog mDialog;
    //弹出窗口为 新闻扩展信息提交

    RecyclerView RV_newsex;
    newsex_adapter m_newsex_adapter;



    private DataUrl dataUrl;
    private Toolbar toolbar;
    private String mUrl,news_id, mImg, mTitle, mDate, mContent, m_desc,mAuthor;


    //需要的----------------------------------------------------------

    private Bitmap bitmap;
  //  private DataUrl dataUrl;

    private String uploadProductImageSwitch="off";

    private Uri fileUri;

    //标记
    private static final int REQUEST_PICK_PHOTO=2;
    private static final int CAMERA_PIC_REQUEST=1111;
    private static final int REQUEST_PHOTO_CLIP=3;

    public static final int MEDIA_TYPE_IMAGE=1;
    public static final String IMAGE_DIRECTORY_NAME="Android File Uplaod";

    //

    ImageView newsex_image;

    //对象保证 图片可以编辑
    private File imageOld;
    private Uri photoURI_2;
   // private String imageFilePath="";

    //相机开启标记
    private boolean cameropen=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        dataUrl=new DataUrl();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");

        appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(this);
        date_behavior = findViewById(R.id.date_behavior);

        titleAppbar=findViewById(R.id.title_appbar);
        imageView=findViewById(R.id.backdrop);

        appbar_title=findViewById(R.id.title_on_appbar);

        appbar_subtitle=findViewById(R.id.subtitle_on_appbar);
        date=findViewById(R.id.date);

        title=findViewById(R.id.title);
        tv_content=findViewById(R.id.content);
        tv_desc=findViewById(R.id.desc);

        RV_newsex=findViewById(R.id.newsex_rv);



        Intent intent=getIntent();

       // mUrl    =intent.getStringExtra("url");

        //提取数据 Extra 到 String 类型
        mImg    =intent.getStringExtra("img");
        mTitle  =intent.getStringExtra("title");
        mDate   =intent.getStringExtra("date");
        news_id  =intent.getStringExtra("news_id");

        m_desc=intent.getStringExtra("desc");
        mAuthor =intent.getStringExtra("author");


        RequestOptions requestOptions=new RequestOptions();
        requestOptions.error(Utils.getRandomDrawbleColor());

        String imagepath=  dataUrl.getHostNewspath()+ File.separator+dataUrl.storeNewsImgPath+ File.separator+mImg;
        Glide.with(this).load(imagepath).apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);


      //  appbar_subtitle.setText(m_desc);

        date.setText(Utils.DateFormat(mDate));
        title.setText(mTitle);
        //tv_content.setText(mContent);
        //显示 content 数据 getTownNews_content
        content_view(news_id);

       // ___________________________________分隔_______________________________________

        tv_desc.setText(m_desc);

        String auther=null;
        if(mAuthor !=null||mAuthor !=""){
            mAuthor="\u2022 "+mAuthor;

        }else{
            auther="";

        }

        //下面显示扩展的 新闻描述 rv
      RV_newsex=findViewById(R.id.newsex_rv);

      RV_newsex.setLayoutManager(new LinearLayoutManager(news_detail_Activity.this));



      loadRVData(); //加载数据
//
        //-------------------------------------------------


        //打开 newsex 添加扩展描述弹出窗口
        tv_openDialogNewsex=findViewById(R.id.tv_openDialogNewsex);//扩展新闻 点击
        tv_openDialogNewsex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //对话框的组件
                EditText newsex_title,newsex_content;

                TextView bn_newsex_suimt,bn_newsex_cancel;
             //   ImageView newsex_image;

                Log.d("dialog: ok","newsex_dialog");
                //创建了一个对话框
                mDialog=new Dialog(news_detail_Activity.this);
                mDialog.setContentView(R.layout.popup_dialog_for_newsex);
                //★对话框布局★
                mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));





                newsex_title=mDialog.findViewById(R.id.popup_newsex_title);
                newsex_content=mDialog.findViewById(R.id.popup_newsex_content);

                //提交图片
                newsex_image=mDialog.findViewById(R.id.popup_newsex_image);
                newsex_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new MaterialDialog
                                .Builder(news_detail_Activity.this) //必须是当前页面的 .this 不能使用getAppleciantioncontenx()

                                .title("获取产品图像")
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
                                              //  newsex_image.setImageResource(R.drawable.ic_launcher_background);
                                                break;
                                        }
                                    }
                                }).show();
                    }
                });

                bn_newsex_suimt=mDialog.findViewById(R.id.tv_bn_sumit_newsex);
                bn_newsex_suimt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        newsex newsex_Item=new newsex();

                        newsex_Item.setNewsex_content(String.valueOf(newsex_content.getText()));
                        newsex_Item.setNewsex_title(String.valueOf(newsex_title.getText()));
                      //  newsex_Item.setNewsex_image_name(String.valueOf(newsex_content.getText()));
                      //文件图片混合上传
                       post_newsex_picToServer(newsex_Item,imageFilePath);
                    }
                });



                bn_newsex_cancel=mDialog.findViewById(R.id.tv_bn_cancel_newsex);
                bn_newsex_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });



                mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {


                        loadRVData();
                      //  m_newsex_adapter.notifyDataSetChanged();

                        //配合 onBindViewHolder(..payloads) 更新局部数据.
                        //更新VR 列表
                        //更新特别的一线
                      //  notifyItemChanged(viewHolder.getAdapterPosition(),"mDialog_payload");
                        //更新当前的

                    }
                });
                mDialog.show();


            }


            private List<MultipartBody.Part> filesToMultipartBodyParts(List<File> files) {
                List<MultipartBody.Part> parts = new ArrayList<>(files.size());
                for (File file : files) {
                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file); //多文件上传修改数据类型
                    MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

                    //上传文件成功第二个关键的地方，将文件（图片）请求头的content-type使用方法filesToMultipartBodyParts()对其赋值"image/png"，
                    // 并返回MultipartBody.Part集合。

                    parts.add(part);
                }
                return parts;
            }



            private void post_newsex_picToServer(newsex newsex_item, String imageFilePath) {


                Map<String , RequestBody> params=new HashMap<>();

                //Utils
                params.put("newsex_title",convertTo_text_plain_RequestBody(newsex_item.getNewsex_title()));
                params.put("newsex_content",convertTo_text_plain_RequestBody(newsex_item.getNewsex_content()));
                params.put("newsex_to_news_id",convertTo_text_plain_RequestBody(news_id)); //绑定

                String path1 ="";

                if(   uploadProductImageSwitch.equals("on")){


                    path1 =imageFilePath;

                }
                List<File> fileList = new ArrayList<>();

                fileList.add(new File(path1));


                //【2】 多文件部分
                List<MultipartBody.Part> partList = filesToMultipartBodyParts(fileList);
                String postUrl=dataUrl.getHostNewspath();

                //返回 Retrofit
                HttpRequestClient.getRetrofitHttpClient(postUrl).create(ApiInterface.class)
                        .postGoodsReturnPostEntitys_newsex(params,partList)  //接口定义部分
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<townNews>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                Toast_Looper("发生:onSubscribe");
                            }

                            @Override
                            public void onNext(@NonNull townNews goodsReturnPostEntity) {

                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Toast_Looper("发生:"+e.getMessage());
                            }

                            @Override
                            public void onComplete() {
                                    Toast_Looper("提交完成...");
                            }
                        });




            }
        });



    }

    private void loadRVData() {


        //构建一个 接口对象 "ApiInterface"
        ApiInterface apiInterface =ApiClient.getApiClient()//返回一个 工具 Retrofit
                .create(ApiInterface.class);  //将retrofit  与 "接口 "  retrofit.create()

//

        //来自  retrofit2.Call
        Call<townNews> call; //用工具做  retrofit call



        call = apiInterface.getTownNews_newsex(news_id);

        //下面处理 News call
        call.enqueue(new Callback<townNews>() {//download 获取网上的资源  retrofit 工具
            //    回调的信息
            @Override
            public void onResponse(Call<townNews> call, Response<townNews> response) {
                //发过来是 New
                if (response.isSuccessful() && response.body().getNewsexs() != null) {
                    //获取来的  Articles
                    //内部包含所有新闻词条
                    if (!newsexeList.isEmpty()) {
                        newsexeList.clear();
                    }
//
//                  //  填充 List <Article> 数据载体
                    newsexeList = response.body().getNewsexs(); //创建 Articles

//
                    m_newsex_adapter = new newsex_adapter(newsexeList, news_detail_Activity.this);
                    RV_newsex.setAdapter(m_newsex_adapter);
                    m_newsex_adapter.notifyDataSetChanged();


                } else {

                    Toast.makeText(news_detail_Activity.this, "没得到需要新闻结果返回!", Toast.LENGTH_LONG).show();

                    //可以显示错误   标记
                    String errorCode;
                    switch (response.code()) {
                        case 404:
                            errorCode = "404 页面没有发现";
                            break;
                        case 500:
                            errorCode = "500 服务器崩溃";
                            break;
                        default:
                            errorCode = "未知错误";
                            break;

                    }


                }

            }

            @Override
            public void onFailure(Call<townNews> call, Throwable t) {
                Log.d("onResponse",t.getMessage());
            }

        });
    }


    //显示 content 内容
    private void content_view(String news_id) {
        ApiInterface apiInterface =ApiClient.getApiClient()//返回一个 工具 Retrofit
                .create(ApiInterface.class);  //将retrofit  与 "接口 "  retrofit.create()

        Call<returnback> call; //用工具做  retrofit call
        call = apiInterface.getTownNews_content(news_id);

        //下面处理 News call
        call.enqueue(new Callback<returnback>() {//download 获取网上的资源  retrofit 工具
            //    回调的信息
            @Override
            public void onResponse(Call<returnback> call,   Response<returnback> response) {
                //发过来是 New
                Log.d("onResponse","ok");
                Log.d("onResponse",response.body().getNewsContent().toString());


                if (response.isSuccessful() && response.body().getNewsContent() != null) {
                    //获取来的  Articles
                    //getNews_contnet

                    mContent=response.body().getNewsContent().get(0).getNews_content();


                    tv_content.setText(mContent);
                }

            }

            @Override
            public void onFailure(Call<returnback> call, Throwable t) {
                Log.d("onResponse","fail"+t.getMessage());
            }

        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();
        return true;


    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        if (percentage == 1f && isHideTolbarView) {
            date_behavior.setVisibility(View.GONE);
            titleAppbar.setVisibility(View.VISIBLE);
            isHideTolbarView = !isHideTolbarView;


        } else if (percentage < 1f && isHideTolbarView) {
            date_behavior.setVisibility(View.VISIBLE);
            titleAppbar.setVisibility(View.GONE);
            isHideTolbarView = !isHideTolbarView;


        }
    }

    //add meunu


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news,menu);
        return true;

    }


    //删除 或者分享信息
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete_news) { //激活删除接口

            //构建一个 接口对象 "ApiInterface"
            ApiInterface apiInterface = ApiClient.getApiClient()//返回一个 工具 Retrofit
                    .create(ApiInterface.class);  //将retrofit  与 "接口 "  retrofit.create()


            //来自  retrofit2.Call
            Call<returnback> call; //用工具做  retrofit call

            call = apiInterface.getTownNews_delete(news_id);
            Intent intent = new Intent();
            call.enqueue(new Callback<returnback>() {
                @Override
                public void onResponse(Call<returnback> call, Response<returnback> response) {
                    Log.d("Logview", "ok");
                    Log.d("Logview", response.body().getType()+":"+response.body().getId());
                               //response.body();
                    intent.setClass(getApplicationContext(),news_list_Activity.class);
                    startActivity(intent);
                    finish();
                    //setResult(RESULT_OK, intent);

                }

                @Override
                public void onFailure(Call<returnback> call, Throwable t) {
                    Log.d("Logview", t.getMessage());
                }




            }
            );





        }else if(id==R.id.edit_news){//激活编辑
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(),news_Edit_Activity.class);

            intent.putExtra("news_id",news_id);
            intent.putExtra("title",mTitle );

            intent.putExtra ("mimg", mImg);

            intent.putExtra ("desc",m_desc );

           // mUrl,news_id, mImg, mTitle, mDate, mContent, m_desc,mAuthor;



            startActivity(intent);
            finish();

        }else if(id==R.id.share){
            try{
                Intent i=new Intent(Intent.ACTION_SEND);
                i.setType("text/plan");

              //  i.putExtra(Intent.EXTRA_SUBJECT,mSource);
                String body=mTitle +"\n"+ mUrl + "\n" +"来自分享信息" +"\n";

                i.putExtra(Intent.EXTRA_TEXT,body);
                startActivity(Intent.createChooser(i,"Share with :"));


            }catch (Exception e){
                Toast.makeText(this,"Hmm...sorry,\n cannot be share",Toast.LENGTH_SHORT).show();

            }
        }

        return super.onOptionsItemSelected(item);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            switch (requestCode) {
                case CAMERA_PIC_REQUEST:
                    //从拍照程序返回
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        bitmap = BitmapFactory.decodeFile(imageFilePath);

                        cameropen = true;
                        //图像尺寸 处理
                        bitmap = setReducedImageSize(); //No-9 调用call

                        //旋转图片校正
                        bitmap = rotateImage(bitmap);
                        //No-10 调用call
                        //必须保存这个文件才能去编辑

                        //  save_bitmap_to_File(bimap);

                        newsex_image.setImageBitmap(bitmap);
                        //处理的程序显示的是一个  临时的bitmap 文件
                        //这个程序已经处理了 productImageView
                        //___________________________________分隔_______________________________________
                        try {
                            //【3】一个随机文件名
                            Random random = new Random();
                            String bitmap_name = "ClipImageFile" + String.valueOf(random.nextInt(Integer.MAX_VALUE));
                            //【4】获取存储目录
                            File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                            //【5】目录下建一个文件
                            File file = new File(storageDirectory.getAbsoluteFile() + bitmap_name + ".jpg");
                            //【6】用这个文件开一个输出流
                            FileOutputStream outputStream = new FileOutputStream(file);

                            //【7】为剪切  图片将个变量填充
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            imageOld = file;
                            //【8】一个文件路径
                            imageFilePath = storageDirectory.getAbsolutePath() + bitmap_name + ".jpg";

                            //图片的地址
                            Toast_Looper(imageFilePath);


                        } catch (IOException e) {
                            e.printStackTrace();
                            cameropen = false;

                        }

                        //___________________________________分隔_______________________________________


                        uploadProductImageSwitch = "on";
                    } else {
                        cameropen = true;
                        bitmap = setReducedImageSize();
                        bitmap = get_rotateImage_bitmap(bitmap); //No-11 call
                        newsex_image.setImageBitmap(bitmap);

                        uploadProductImageSwitch = "on";

                    }

                    break;
                case REQUEST_PICK_PHOTO:
                    if(data!=null){
                        Uri selectedImage=data.getData();
                        if(selectedImage!=null){
                            try{
                                //【1】从选择的图片获取  bitmap 映像
                                bitmap=getBitmapFormUri(news_detail_Activity.this,selectedImage);
                                //No-12

                                //【2】显示图片
                                newsex_image.setImageBitmap(bitmap); //显示图片

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
            }


        }
    }


    //通过uri 获取图片并进行压缩
    private Bitmap getBitmapFormUri(news_detail_Activity newproduct, Uri selectedImageUri) throws IOException {

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

    //No-9 设置尺寸ImageView
    private Bitmap setReducedImageSize() {

        int targetImageViewWidth=newsex_image.getWidth();
        int targetImageViewHeight=newsex_image.getHeight();

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
        intent.putExtra("aspectX", 4);//宽高比
        intent.putExtra("aspectY", 3);
        intent.putExtra("outputX", 400);
        intent.putExtra("outputY", 300);
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
                    Toast.makeText(news_detail_Activity.this, "照片创建失败!",
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
