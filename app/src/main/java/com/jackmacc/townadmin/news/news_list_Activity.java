package com.jackmacc.townadmin.news;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jackmacc.townadmin.R;
import com.jackmacc.townadmin.Tool.Utils;
import com.jackmacc.townadmin.api.ApiClient;
import com.jackmacc.townadmin.api.ApiInterface;
import com.jackmacc.townadmin.home_page;
import com.jackmacc.townadmin.models.townArticle;
import com.jackmacc.townadmin.models.townNews;
import com.jackmacc.townadmin.option_page;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class news_list_Activity extends AppCompatActivity   implements SwipeRefreshLayout.OnRefreshListener {


    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<townArticle> articles = new ArrayList<>  ();

    private news_Adapter newsAdapter;
    private String TAG =    news_list_Activity.class.getSimpleName();

    private TextView topHeadline;
    private SwipeRefreshLayout swipeRefreshLayout;
    public final int FOR_NEW_NEWS_RESULT_BACK=8776;

//底部
    //布局中添加 BottomNavigationView
    //布局中,配置 菜单文件 menu  xml

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Intent intent=new Intent();
            switch (item.getItemId()){

                case R.id.new_product_add:
                    intent.setClass(getApplicationContext(), news_AddEdit_Activity.class);
                    //addswitch =new
                    intent.putExtra("newEditSwitch","new");

                    intent.putExtra("newsType_id",getIntent().getStringExtra("newsType_id"));
                    intent.putExtra("newsType_name",getIntent().getStringExtra("newsType_name"));

                    Log.d("newsType_id", getIntent().getStringExtra("newsType_id"));
                    //  mContext.startActivity(intent);
                    //发起ForResult
                    startActivity(intent);
                    finish();

                    return true;


                case R.id.home_page:
                    intent.setClass(getApplicationContext(), home_page.class);
                    startActivity(intent);
                    return true;

                case R.id.option_page:
                    intent.setClass(getApplicationContext(), option_page.class);
                    startActivity(intent);
                    return true;
            }

            return false;

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        //下拉重载
        swipeRefreshLayout = findViewById  (R.id.swip_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources  (R.color.colorAccent);

        //使用 NestedScrollView 控件
        recyclerView = findViewById   (R.id.recyclerView);
        //窗口管理器
        layoutManager = new LinearLayoutManager  (news_list_Activity.this);
      //  recyclerView.setLayoutManager(new GridLayoutManager(news_list_Activity.this,2));
      recyclerView.setLayoutManager(layoutManager);

        //动画
        recyclerView.setItemAnimator(new   DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false); //滚动开启- off


        topHeadline = findViewById(R.id.topheadelines); //新闻条

        BottomNavigationView bottom_nav = (BottomNavigationView) findViewById(R.id.maninfoPage_bottomNavigation);
        bottom_nav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //调用自定义 void
        //这个函数是流程处理
        Log.d("newsType_id", getIntent().getStringExtra("newsType_id"));

      String   newstype_id= getIntent().getStringExtra("newsType_id");
      //取值可能是空的. null

      if (newstype_id ==null)
          onLoadingSwipeRefresh("");
      else if(newstype_id.equals(""))
          onLoadingSwipeRefresh("");
      else


          onLoadingSwipeRefresh(getIntent().getStringExtra("newsType_id"));//缺省是 ""

    }

    //发起线程   执行 加载 json  执行 LoadJson
    private void onLoadingSwipeRefresh(final String    keyword) {
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        LoadJson(keyword);

                    }
                }
        );
    }

    //加载 json 文本流
    public void LoadJson(final String keyword) {

           topHeadline.setVisibility(View.INVISIBLE);
        //   errorLayout.setVisibility(View.GONE);

        swipeRefreshLayout.setRefreshing(true);/**/

        //构建一个 接口对象 "ApiInterface"
        ApiInterface apiInterface =ApiClient.getApiClient()//返回一个 工具 Retrofit
                .create(ApiInterface.class);  //将retrofit  与 "接口 "  retrofit.create()


        String country = Utils.getCountry(); //得到你的国家属性(系统的)
        String language = Utils.getLanguage(); //得到系统的语言

        //来自  retrofit2.Call
        Call<townNews> call; //用工具做  retrofit call


        if (keyword.length() > 0) { //搜索的 关键字

            call = apiInterface.getTownNews(keyword);
        //apiInterface.getNewsSearch(keyword,country, "publishedAt", API_KEY);
                        //数值大于0 进行搜索 自定义数据是  News
                        //参数-1:keyword 搜索关键字
                        //参数-2:国家
                        //参数-3:排序 词语
                        //参数-4:数据源 交易 API_KEY

        } else {
            call = apiInterface.getTownNews("");
            //无搜索查询就缺省国家 查询

        }



        //下面处理 News call
        call.enqueue(new Callback<townNews>() {//download 获取网上的资源  retrofit 工具
            //    回调的信息
            @Override
            public void onResponse(Call<townNews> call,   Response<townNews> response) {
                //发过来是 New
                if (response.isSuccessful() &&  response.body().getArticles() != null) {
                            //获取来的  Articles
                            //内部包含所有新闻词条
                    if (!articles.isEmpty()) {
                        articles.clear();
                    }

                    //填充 List <Article> 数据载体
                    articles = response.body().getArticles(); //创建 Articles

                    //【第一步】 将数据给适配器显示列表
                    //新闻适配器
                    newsAdapter = new news_Adapter(articles,  news_list_Activity.this);
                    recyclerView.setAdapter  (newsAdapter);//将信息载入 对象.列表 新闻列表
                    newsAdapter.notifyDataSetChanged(); //载入后立即更新

                    //【第二步】 通过接口方法将数据人设置  单元点击事件的数据传递,
                    //为 适配器配置  newsAdapter  setOnItemClickListener 监听器
                    initListener(); //点击后去打开新闻 详细 页面

                    topHeadline.setVisibility  (View.INVISIBLE);
                 //   topHeadline.setVisibility  (View.VISIBLE);
                    swipeRefreshLayout.setRefreshing  (false);

                } else {//加载无返回 处理
                    topHeadline.setVisibility  (View.INVISIBLE);

                    swipeRefreshLayout.setRefreshing  (false);
                    //
                    //
                    Toast.makeText(news_list_Activity.this, "没得到需要新闻结果返回!", Toast.LENGTH_LONG).show();

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
                topHeadline.setVisibility   (View.INVISIBLE);
                swipeRefreshLayout.setRefreshing  (false);
                //失败的回调处理
                //网络问题,

            }
        });
    }

    //
    private void initListener() {

        //在这个地方实现了. 接口对象  而这个接口对象,进而可以去调用 内部中的.MyViewHolder
        //setOnItemClickLister 调用了news_Adapter 的接口OnItemClickListener() 的onItemClick方法

        //由这个接口 对象打包数据给 MyViewHolder的 监听点击函数,
        newsAdapter.setOnItemClickListener(new news_Adapter.OnItemClickListener() {
            @Override
            //这个是接口函数  onItemClick 自定义  ,实现
            //来自于接口的重载方法. 引用指向 适配器内部绑定对象,
            public void from_onClick_get_pos_to_process(View view, int  position) {//两个参数,来自 holder 对象的点击事件
                //这里定义接口函数实现,在 holder 哪里调用这个接口,实现事件处理函数能处理的数据.
                Intent intent = new Intent   (news_list_Activity.this, news_detail_Activity.class);

                //由这个点击事件获取的信息来 定位信息 未知. 从而实现回调
                townArticle article = articles.get (position);


               intent.putExtra("title", article.getNews_title());
               intent.putExtra("news_id", article.getNews_id());
               intent.putExtra ("img", article.getNews_images_path());
               intent.putExtra  ("date", article.getNews_date());
               intent.putExtra ("desc", article.getNews_desc());
//                intent.putExtra  ("author", article.getAuthor());

                startActivity(intent); //打开一个新闻的描述页面


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_news_list, menu);
        SearchManager searchManager = (SearchManager)   getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView)    menu.findItem(R.id.action_search).getActionView();

        MenuItem searchMenuItem = menu.findItem    (R.id.action_search);
        searchView.setSearchableInfo   (searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("search latest news...");
        //
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {
                    onLoadingSwipeRefresh(query);
                } else {
                    Toast.makeText(news_list_Activity.this,
                            "Type more than two  letters   !",
                            Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String  newText) {
                return false;
            }
        });

        searchMenuItem.getIcon().setVisible(false,  false);

        return true;

    }

    @Override
    public void onRefresh() {
        //页面刷新重新加载
        // 加载 json 文本流


        String   newstype_id= getIntent().getStringExtra("newsType_id");
        //取值可能是空的. null

        if (newstype_id ==null)
            onLoadingSwipeRefresh("");
        else if(newstype_id.equals(""))
            onLoadingSwipeRefresh("");
        else
            LoadJson(newstype_id);


    }




}
