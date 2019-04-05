package com.media_player.dengxinyigoogleproject;

import android.graphics.Color;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class PlaceListAdapter  extends RecyclerView.Adapter<PlaceViewHolder> {
    private ArrayList<PlaceListItem> m_list = null;

    public PlaceListAdapter(ArrayList<PlaceListItem> m_list){
        this.m_list = m_list;
    }
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.place_list_item, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PlaceViewHolder nameviewHolder, int position) {
//        if (position == 0){
//            nameviewHolder.tv_place_name.setText("Place Name");
//            nameviewHolder.tv_place_cor1.setText("Latitude");
//            nameviewHolder.tv_place_cor2.setText("Longitude");
//            nameviewHolder.tv_place_name.setTextColor(Color.rgb(0,0,100));
//            nameviewHolder.tv_place_cor1.setTextColor(Color.rgb(0,0,100));
//            nameviewHolder.tv_place_cor2.setTextColor(Color.rgb(0,0,100));
//            nameviewHolder.tv_place_name.setTextSize(20);
//            nameviewHolder.tv_place_cor1.setTextSize(20);
//            nameviewHolder.tv_place_cor2.setTextSize(20);
//        }else{
            PlaceListItem namelistItem = m_list.get(position);
            nameviewHolder.tv_place_name.setText(namelistItem.getPlaceName());
            nameviewHolder.tv_place_cor1.setText(Double.toString(namelistItem.getLan()));
            nameviewHolder.tv_place_cor2.setText(Double.toString(namelistItem.getLan2()));
            nameviewHolder.tv_description.setText(namelistItem.getDescription());
            nameviewHolder.iv_photo.setImageBitmap(namelistItem.getImg());
            final PlaceListItem pli = namelistItem;
            nameviewHolder.tv_place_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg = new Message();
                    msg.what = 3;
                    msg.obj = pli;
                    GLOBAL.m_handler.sendMessage(msg);
                }
            });
//        }
    }

    @Override
    public int getItemCount() {
        if(m_list==null)
        {
            m_list = new ArrayList<PlaceListItem>();
        }
        return m_list.size();
    }
}
