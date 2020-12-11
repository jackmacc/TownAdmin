package com.jackmacc.townadmin.swipernews;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.jackmacc.townadmin.DataUrl;
import com.jackmacc.townadmin.R;
import com.jackmacc.townadmin.Tool.Utils;
import com.jackmacc.townadmin.models.townArticle;

import java.io.File;
import java.util.List;

public class swiper_news_Adapter extends RecyclerView.Adapter<swiper_news_Adapter.MyViewHolder> {

    private List<townArticle> articles;
    private Context context;
    private OnItemClickListener onItemClickListener;//对象1
    DataUrl dataUrl;

    //适配器用魔术指针处理  引用数据
    public swiper_news_Adapter(List<townArticle> articles, Context context) {
        this.articles = articles;//魔术指针
        this.context = context;
        dataUrl=new DataUrl();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate//获取布局的视图
                (R.layout.news_adapter_item_swiper,parent,false);

        //这个地方使用魔术指针 处理 view 和 onItemClicList 处理item 被点击.
        //使得adapter 可以通过接口处理 holder 的内部 onClick 事件
        //③
        return new MyViewHolder(view,onItemClickListener); //创建并设置监听 数据 holder
        //对象1
    }
    //___________________________________分隔_______________________________________
    //设置一个接口 这个接口 也被自身创建了一个内部对象
    //★ setOnItemClickListener  <=  OnItemClickListener    from
    //①
    public interface OnItemClickListener{
        //为得到点击pos 而建立的接口方法

        void from_onClick_get_pos_to_process(View view, int position);

    }
    //②  这个函数来  引进这个接口 实体
    //接口函数  数据来自接口函数
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){//外来的一个接口对象来填充.
        //使用了.onItemClickListener 对象
        this.onItemClickListener=onItemClickListener;
    }
    //___________________________________分隔_______________________________________


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holders, int position) {
        //绑定holder 数据
        final MyViewHolder holder=holders;
        //获取 数据List 单项数据
        townArticle model_Item=articles.get(position);

       //数据请求设置
        RequestOptions requestOptions=new RequestOptions();
        requestOptions.placeholder(Utils.getRandomDrawbleColor());
        requestOptions.error(Utils.getRandomDrawbleColor());

        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();
      //  requestOptions.timeout(3000);

      String imagepath=  dataUrl.getHostNewspath()+ File.separator+dataUrl.storeNewsImgPath+ File.separator+model_Item.getNews_images_path();
        //图片加载
        Glide.with(context)
                .load(imagepath)
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target,
                                                boolean isFirstResource) {
                        //显示处理进度加载效果...
                       holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageView);

        //文本数据 装入
       holder.title.setText(model_Item.getNews_title());
       holder.desc.setText(model_Item.getNews_desc());
    //    holder.source.setText(model_Item.getSource().getName());
        holder.release_date.setText(model_Item.getNews_date());
       // holder.published_ad.setText(Utils.DateFormat(model_Item.getPublishedAt()));
      // holder.author.setText(model_Item.getAuthor());




    }

    @Override
    public int getItemCount() {
        return articles.size();
    }






    //内部定义数据包
    //一路一个接口可以实现调用 Adapter 的内部
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title,desc,release_date;
        ImageView imageView;
        ProgressBar progressBar;

        OnItemClickListener onItemClickListener;


        //构造函数在  适配器的 onCreateViewHolder 中实现
        public MyViewHolder(View itemView,OnItemClickListener onItemClickListener){
            super(itemView);


            itemView.setOnClickListener(this); //获取点击监听对象


            title=itemView.findViewById(R.id.title);
            desc=itemView.findViewById(R.id.desc);
            //发布时间
            release_date=itemView.findViewById(R.id.release_date);
            imageView=itemView.findViewById(R.id.img);


            //处理进度球
            progressBar=itemView.findViewById(R.id.progress_load_photo);

            //魔术指针 引用了这一接口对象
            this.onItemClickListener=onItemClickListener;
        }

        //重载了 View.OnClickListener.
        @Override
        public void onClick(View v) {//使得 onClick 事件调用了 接口函数.
            //单元点击
            //内部的点击  获取了数据的定位,  给了 接口的函数.
            //外部只要,实现 接口对象就可以链接响应  点击的数据.

            onItemClickListener.from_onClick_get_pos_to_process(v,getAdapterPosition());
            //调用 了.RecyclerView 的 getAdapterPosition

            //有了这个接口,这里就不用重复去重新获取 db 数据.

        }
    }
}
