package com.media_player.dengxinyigoogleproject;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PlaceViewHolder extends RecyclerView.ViewHolder {
    TextView tv_place_name;
    TextView tv_place_cor1;
    TextView tv_place_cor2;
    ImageView iv_photo;
    TextView tv_description;
    public PlaceViewHolder(View itemView){
        super(itemView);
        tv_place_name = (TextView) itemView.findViewById(R.id.tv_item_place);
        tv_place_cor1 = (TextView) itemView.findViewById(R.id.tv_item_cor1);
        tv_place_cor2 = (TextView) itemView.findViewById(R.id.tv_item_cor2);
        iv_photo = (ImageView) itemView.findViewById(R.id.iv_photo);
        tv_description = (TextView) itemView.findViewById(R.id.tv_show_description);

    }
}
