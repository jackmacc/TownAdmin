package com.jackmacc.townadmin;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jackmacc.townadmin.models.townnewstype;

import java.io.File;
import java.util.List;

public class homervtopItemAdapter extends RecyclerView.Adapter<homervtopItemAdapter.rvtopHoloder> {

    private List<townnewstype> mItemList;
    private Context mContext;
    private OnItemClickListenertype onItemClickListener_type;//对象1

    public interface OnItemClickListenertype{
        //为得到点击pos 而建立的接口方法

        void from_onClick_get_pos_to_process(View view, int position);

    }

    public homervtopItemAdapter(List<townnewstype> mItemList, Context mContext) {
        this.mItemList = mItemList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public rvtopHoloder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //cell 布局
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_rvtop_item_big,parent,false);

        return new rvtopHoloder (view,onItemClickListener_type);

    }



    //②  这个函数来  引进这个接口 实体
    //接口函数  数据来自接口函数
    public void setOnItemClickListener(OnItemClickListenertype onItemClickListener){//外来的一个接口对象来填充.
        //使用了.onItemClickListener 对象
        this.onItemClickListener_type=onItemClickListener;
    }
    //___________________________________分隔_______________________________________

    @Override
    public void onBindViewHolder(@NonNull rvtopHoloder holder, int position) {

        townnewstype itemText=mItemList.get(position);
            holder.textView.setText(itemText.getNewstype_name());

            DataUrl dataUrl=new DataUrl();
            String Newstype_image_name=itemText.getNewstype_image();

                Newstype_image_name=dataUrl.getHostNewspath()+ File.separator+dataUrl.storeNewsImgPath+File.separator+Newstype_image_name;

                Log.d("image",Newstype_image_name);
        Glide.with(mContext).load(Newstype_image_name)
                .thumbnail()
                .error(R.drawable.ic_launcher_background)
                .into(holder.typeimageView);

            int margin=doToPx(24);

            int left    =doToPx(8);
            int top     =doToPx(3);
            int right   =doToPx(8);
            int bottom  =doToPx(3);

            int spanCount=2;

        boolean isFirst2Items=position< spanCount;
        boolean isLast2Items=position> getItemCount()-spanCount;

        if(isFirst2Items){
            top=doToPx(12);

        }

        if(isLast2Items){
            bottom=doToPx(12);
        }

        boolean isLeftSide=(position+1) % spanCount !=0;

        boolean isRightSide=!isLeftSide;

        if(isLeftSide){
            right=doToPx(6);

        }

        if (isRightSide) {

            left=doToPx(6);
        }




      //  FrameLayout.LayoutParams laytoutParams=(FrameLayout.LayoutParams) holder.cardView.getLayoutParams();
      //  laytoutParams.setMargins(left,top,right,bottom);
       // holder.cardView.setLayoutParams(laytoutParams);

    }

    private int doToPx(int dp){
        float px=dp* mContext.getResources().getDisplayMetrics().density;
        return (int) px;

    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }



  public  class rvtopHoloder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        private CardView cardView;
        private TextView textView;
        ImageView typeimageView;
        OnItemClickListenertype onItemClickListener_Holder;


      public  rvtopHoloder(View itemView, OnItemClickListenertype onItemClickListener){
            super(itemView);

            itemView.setOnClickListener(this); //关键地方.

            this.onItemClickListener_Holder=onItemClickListener;

           // cardView=itemView.findViewById(R.id.card_view);
            textView=itemView.findViewById(R.id.townnewstype_name);
            typeimageView=itemView.findViewById(R.id.townnewstype_image);
          Log.d("onclick","holoder!");
        }

        @Override
        public void onClick(View v) {


            Log.d("onclick","触发点击!");
            onItemClickListener_Holder.from_onClick_get_pos_to_process(v,getAdapterPosition());
        }

    }
}
