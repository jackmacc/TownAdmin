package com.jackmacc.townadmin.newsex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.jackmacc.townadmin.DataUrl;
import com.jackmacc.townadmin.R;
import com.jackmacc.townadmin.models.newsex;

import java.io.File;
import java.util.List;

public class newsex_adapter extends RecyclerView.Adapter<newsex_adapter.newsexHolder> {


    private List<newsex> newsexeList;

    private Context m_Context;

    DataUrl dataUrl;

    public newsex_adapter(List<newsex> newsexeList, Context m_Context) {
        this.newsexeList = newsexeList;
        this.m_Context = m_Context;
        dataUrl=new DataUrl();

    }

    @NonNull
    @Override
    public newsexHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(m_Context).inflate(R.layout.newsex_item_adapter,parent,false);

        return  new newsexHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull newsexHolder holder, int position) {

        final newsexHolder newsex_Holder=holder;
        newsex newsex_Item=newsexeList.get(position);

        String imagepath=  dataUrl.getHostNewspath()+
                File.separator+dataUrl.storeNewsImgPath+
                File.separator+newsex_Item.getNewsex_image_name();


        Glide.with(m_Context)
                .load(imagepath)

                .transition(DrawableTransitionOptions.withCrossFade())
                .into(newsex_Holder.newsex_image);

        newsex_Holder.newsex_et_title.setText(newsex_Item.getNewsex_title());
        newsex_Holder.newsex_et_content.setText(newsex_Item.getNewsex_content());




    }

    @Override
    public int getItemCount() {
        return newsexeList.size();
    }

    public class newsexHolder extends RecyclerView.ViewHolder {

        ImageView newsex_image;
        TextView newsex_et_title,newsex_et_content;

        public newsexHolder(@NonNull View itemView) {
            super(itemView);
            newsex_et_content=itemView.findViewById(R.id.newsex_content);
            newsex_et_title=itemView.findViewById(R.id.newsex_title);
            newsex_image=itemView.findViewById(R.id.newsex_imageView);



        }
    }
}
