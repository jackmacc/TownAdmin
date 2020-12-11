package com.jackmacc.townadmin.swipernews;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.jackmacc.townadmin.models.returnback;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class swiper_news_detail_Activity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private ImageView imageView;
    private TextView appbar_title, appbar_subtitle, date, time, title,tv_desc,tv_content;
    private boolean isHideTolbarView = false;
    private FrameLayout date_behavior;
    private LinearLayout titleAppbar;
    private AppBarLayout appBarLayout;


    private DataUrl dataUrl;
    private Toolbar toolbar;
    private String mUrl,news_id, mImg, mTitle, mDate, mContent, m_desc,mAuthor;

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



    }

    //显示 content 内容
    private void content_view(String news_id) {
        ApiInterface apiInterface =ApiClient.getApiClient()//返回一个 工具 Retrofit
                .create(ApiInterface.class);  //将retrofit  与 "接口 "  retrofit.create()

        Call<returnback> call; //用工具做  retrofit call
        call = apiInterface.getTownNews_content_swiper(news_id);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete_news) { //激活删除接口

            //构建一个 接口对象 "ApiInterface"
            ApiInterface apiInterface = ApiClient.getApiClient()//返回一个 工具 Retrofit
                    .create(ApiInterface.class);  //将retrofit  与 "接口 "  retrofit.create()


            //来自  retrofit2.Call
            Call<returnback> call; //用工具做  retrofit call

            call = apiInterface.getTownNews_delete_swiper(news_id);
            Intent intent = new Intent();
            call.enqueue(new Callback<returnback>() {
                @Override
                public void onResponse(Call<returnback> call, Response<returnback> response) {
                    Log.d("Logview", "ok");
                    Log.d("Logview", response.body().getType()+":"+response.body().getId());
                               //response.body();
                    intent.setClass(getApplicationContext(), swiper_news_list_Activity.class);
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
            intent.setClass(getApplicationContext(), swiper_news_Edit_Activity.class);

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
}
