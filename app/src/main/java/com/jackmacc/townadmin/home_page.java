package com.jackmacc.townadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jackmacc.townadmin.Tool.AutoScrollLayoutManager;
import com.jackmacc.townadmin.Tool.AutoScrollRecyclerView;
import com.jackmacc.townadmin.api.ApiClient;
import com.jackmacc.townadmin.api.ApiInterface;
import com.jackmacc.townadmin.models.townArticle;
import com.jackmacc.townadmin.models.townNews;
import com.jackmacc.townadmin.models.townnewstype;
import com.jackmacc.townadmin.news.news_list_Activity;
import com.jackmacc.townadmin.swipernews.swiper_news_detail_Activity;
import com.jackmacc.townadmin.swipernews.swiper_news_list_Activity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class home_page extends AppCompatActivity  {


    List<townnewstype>  townnewstypeList;
     List<townArticle> townArticleList;




    LinearLayoutManager swiperlinearLayoutManager;

 // RecyclerView rvswiper;
    AutoScrollRecyclerView rvswiper;
    RecyclerView rvtop;



    homervtopItemAdapter rvtopAdapter;
    homervswiperItemAdapter rvswiperAdapter;


    @Override
    protected void onResume() {
        super.onResume();
    //    mHandler.postDelayed(scrollRunable,10);
       // townArticleList.clear();
     //  load_rvswiper();

    }



    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);


        Load_rvtop();

        load_rvswiper();



        BottomNavigationView bottom_nav = (BottomNavigationView) findViewById(R.id.maninfoPage_bottomNavigation);
        bottom_nav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }




    private void load_rvswiper() {

        townArticleList=new ArrayList<>();


       //读取数据接口 分类数据
        ApiInterface apiInterface2 = ApiClient.getApiClient()//返回一个 工具 Retrofit
                .create(ApiInterface.class);  //将retrofit  与 "接口 "  retrofit.create()
        Call<townNews> call2; //用工具做  retrofit call
        call2 = apiInterface2.getTownNews_swiper("scroll");

        //下面处理 News call
        call2.enqueue(new Callback<townNews>() {//download 获取网上的资源  retrofit 工具
            //回调的信息
            @Override
            public void onResponse(Call<townNews> call, Response<townNews> response) {
                //发过来是 New
                if (response.isSuccessful() && response.body().getArticles() != null) {
                    //获取来的  Articles
                    townArticleList=response.body().getArticles();

                    rvswiper=findViewById(R.id.Servernews_swiper_rv);
                    rvswiperAdapter=new homervswiperItemAdapter(townArticleList,home_page.this);
                    swiperlinearLayoutManager=
                 //  new LinearLayoutManager(home_page.this);
                            new AutoScrollLayoutManager(home_page.this);
                    swiperlinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    rvswiper.setLayoutManager(swiperlinearLayoutManager );
                    rvswiper.setAdapter(rvswiperAdapter);

                    //触发自动滚动
                    rvswiper.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            rvswiper.smoothScrollToPosition(rvswiperAdapter.getItemCount());
                        }
                    });



                    //连续滚动
                    rvswiper.setOnScrollListener
                            (new RecyclerView.OnScrollListener() {
                                 @Override
                                 public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                     super.onScrollStateChanged(recyclerView, newState);
                                     if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                         // 如果自动滑动到最后一个位置，则此处状态为SCROLL_STATE_IDLE
                                         AutoScrollLayoutManager lm = (AutoScrollLayoutManager) recyclerView.getLayoutManager();
                                         int position = lm.findLastCompletelyVisibleItemPosition();
                                         int count = lm.getItemCount();
                                         if (position == count - 1) {
                                             lm.scrollToPosition(0);
                                             rvswiper.smoothScrollToPosition(rvswiperAdapter.getItemCount());
                                         }
                                     }
                                 }
                             }
                            );
                    initListener_swipter();


                }

            }

            @Override
            public void onFailure(Call<townNews> call, Throwable t) {
                Toast.makeText(home_page.this, "没得到需要新闻结果返回!", Toast.LENGTH_LONG).show();
            }
        });
        //数据读取


    }

    private void Load_rvtop() {

        townnewstypeList=new ArrayList<>();




        //读取数据接口 分类数据
        ApiInterface apiInterface = ApiClient.getApiClient()//返回一个 工具 Retrofit
                .create(ApiInterface.class);  //将retrofit  与 "接口 "  retrofit.create()
        Call<townNews> call; //用工具做  retrofit call

        call = apiInterface.getTownNewstype();
        //下面处理 News call
        call.enqueue(new Callback<townNews>() {//download 获取网上的资源  retrofit 工具
            //    回调的信息
            @Override
            public void onResponse(Call<townNews> call, Response<townNews> response) {
                //发过来是 New
                if (response.isSuccessful() && response.body().gettownnewstype() != null) {
                    //获取来的  Articles
                    townnewstypeList=response.body().gettownnewstype();
                    rvtop=findViewById(R.id.product_shopcard_rv);
                    rvtopAdapter=new homervtopItemAdapter(townnewstypeList,home_page.this);
                    rvtop.setAdapter(rvtopAdapter);


                    //rvtop.setLayoutManager( new LinearLayoutManager(home_page.this));



                    rvtop.setLayoutManager(new GridLayoutManager(home_page.this,2){

                        @Override
                        public boolean canScrollVertically() {
                            return false;
                        }
                    });
                    rvtopAdapter.notifyDataSetChanged();


                    initListener();

                }

            }

            @Override
            public void onFailure(Call<townNews> call, Throwable t) {
                Toast.makeText(home_page.this, "没得到需要新闻结果返回!", Toast.LENGTH_LONG).show();
            }
        });
        //数据读取
    }


    //


    private void initListener_swipter() {

        rvswiperAdapter.setOnItemClickListener(new homervswiperItemAdapter.OnSwiperItemClickListener() {
            @Override
            public void from_swiper_onClick_get_pos_to_press(View view, int position) {

                Intent intent = new Intent(home_page.this, swiper_news_detail_Activity.class);

                townArticle m_townArticle = townArticleList.get(position);


                //提取数据 Extra 到 String 类型
//                mImg    =intent.getStringExtra("img");
//                mTitle  =intent.getStringExtra("title");
//                mDate   =intent.getStringExtra("date");
//                news_id  =intent.getStringExtra("news_id");
//                m_desc=intent.getStringExtra("desc");

                intent.putExtra("title", m_townArticle.getNews_title());
                intent.putExtra("img", m_townArticle.getNews_images_path());
                intent.putExtra("news_id", String.valueOf(m_townArticle.getNews_id()));
                intent.putExtra("desc", m_townArticle.getNews_desc());
                intent.putExtra("date", m_townArticle.getNews_date());

                startActivity(intent);


            }
        });
    }

    //rvtop item 点击处理
    private void initListener(){

        rvtopAdapter.setOnItemClickListener(new homervtopItemAdapter.OnItemClickListenertype() {
            @Override
            public void from_onClick_get_pos_to_process(View view, int position) {

                Intent intent=new Intent(home_page.this, news_list_Activity.class);

                townnewstype townnewstype=townnewstypeList.get(position);

                intent.putExtra("newsType_name", townnewstype.getNewstype_name());
                intent.putExtra("newsType_image", townnewstype.getNewstype_image());
                intent.putExtra("newsType_id",String.valueOf( townnewstype.getNewstype_id()));
                intent.putExtra("newsType_desc", townnewstype.getNewstype_desc());

                startActivity(intent);




            }
        });
    }


    //底部菜单事件处理
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Intent intent=new Intent();
            switch (item.getItemId()){

                case R.id.menu_productlist:
                    intent.setClass(getApplicationContext(),product_list_Activity.class);
                    //addswitch =new
                    intent.putExtra("newEditSwitch","new");
                    startActivity(intent);
                    return true;

                case R.id.menu_newslist:
                    intent.setClass(getApplicationContext(), swiper_news_list_Activity.class);
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


}