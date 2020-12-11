package com.jackmacc.townadmin;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jackmacc.townadmin.models.townArticle;

import java.io.File;
import java.util.List;

class homervswiperItemAdapter extends RecyclerView.Adapter<homervswiperItemAdapter.rvswiperItemHolder> {

    private List<townArticle> mItemList;
    private Context mContext;
    private OnSwiperItemClickListener onSwiperItemClickListener ;//


    public interface OnSwiperItemClickListener{
        void from_swiper_onClick_get_pos_to_press(View view,int position);


    }

    public homervswiperItemAdapter(List<townArticle> mItemList, Context mContext) {
        this.mItemList = mItemList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public rvswiperItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.swiper_h_scroll_item,parent,false);



        return new rvswiperItemHolder(view,onSwiperItemClickListener);
    }


    //②  这个函数来  引进这个接口 实体
    //接口函数  数据来自接口函数
    public void setOnItemClickListener(homervswiperItemAdapter.OnSwiperItemClickListener onItemClickListener){//外来的一个接口对象来填充.
        //使用了.onItemClickListener 对象
        this.onSwiperItemClickListener=onItemClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull rvswiperItemHolder holder, int position) {
        townArticle item_townArticle=mItemList.get(position);

        DataUrl dataUrl=new DataUrl();
        String newsArticle_image_name=item_townArticle.getNews_images_path();

        newsArticle_image_name=dataUrl.getHostNewspath()+ File.separator+dataUrl.storeNewsImgPath+File.separator+newsArticle_image_name;

        Log.d("swiper",newsArticle_image_name);

        Glide.with(mContext).load(newsArticle_image_name)
                .thumbnail()
                .error(R.drawable.ic_launcher_background)
                .into(holder.swiperimage);



    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public class rvswiperItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView swiperimage;
        OnSwiperItemClickListener onSwiperItemClickListener_Holder;

        public rvswiperItemHolder(@NonNull View itemView,OnSwiperItemClickListener onSwiperItemClickListener) {
            super(itemView);

            itemView.setOnClickListener(this); //定位监听到item
            this.onSwiperItemClickListener_Holder=onSwiperItemClickListener;



            swiperimage=itemView.findViewById(R.id.swiper_h_scroll_image);




        }


        @Override
        public void onClick(View v) {

            Log.d("swiper","触发点击!");
            onSwiperItemClickListener_Holder.from_swiper_onClick_get_pos_to_press(v,getAdapterPosition());
        }
    }

}
