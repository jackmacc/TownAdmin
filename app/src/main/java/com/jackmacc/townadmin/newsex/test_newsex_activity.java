package com.jackmacc.townadmin.newsex;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jackmacc.townadmin.R;
import com.jackmacc.townadmin.api.ApiClient;
import com.jackmacc.townadmin.api.ApiInterface;
import com.jackmacc.townadmin.models.newsex;
import com.jackmacc.townadmin.models.townNews;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class test_newsex_activity extends AppCompatActivity {
    RecyclerView RV_newsex;
    newsex_adapter m_newsex_adapter;
    private List<newsex> newsexeList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_newsex_activity);


       newsexeList= new ArrayList<>();


        //下面显示扩展的 新闻描述 rv
        RV_newsex=findViewById(R.id.newsex_rv_test);

        RV_newsex.setLayoutManager(new LinearLayoutManager(test_newsex_activity.this));

        //构建一个 接口对象 "ApiInterface"
        ApiInterface apiInterface = ApiClient.getApiClient()//返回一个 工具 Retrofit
                .create(ApiInterface.class);  //将retrofit  与 "接口 "  retrofit.create()

//

        //来自  retrofit2.Call
        Call<townNews> call; //用工具做  retrofit call



        call = apiInterface.getTownNews_newsex("36");

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
                    m_newsex_adapter = new newsex_adapter(newsexeList, test_newsex_activity.this);
                    RV_newsex.setAdapter(m_newsex_adapter);
                    m_newsex_adapter.notifyDataSetChanged();


                } else {

                    Toast.makeText(test_newsex_activity.this, "没得到需要新闻结果返回!", Toast.LENGTH_LONG).show();

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
}