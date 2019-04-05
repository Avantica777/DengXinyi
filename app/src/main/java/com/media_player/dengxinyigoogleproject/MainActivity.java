package com.media_player.dengxinyigoogleproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView m_placelistView;
    private PlaceListAdapter namelistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent m_intent = getIntent();
        String rcv_place = m_intent.getStringExtra("placeName");
        double latitude = m_intent.getDoubleExtra("latitude",40.710001);
        double longitude = m_intent.getDoubleExtra("longitude",-74.0059004);

        //Description and BitMap
        String description = m_intent.getStringExtra("description");
        Bitmap img = m_intent.getParcelableExtra("image");

        GLOBAL.m_handler.sendEmptyMessage(1);       //init Arraylist
        GLOBAL.m_handler.setMainActivity(this);

        m_placelistView = findViewById(R.id.place_list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        m_placelistView.setLayoutManager(linearLayoutManager);

//        GLOBAL.m_handler.m_nameArray.add(new PlaceListItem(18,rcv_place,latitude,longitude));
        if (GLOBAL.m_handler.getAppStarted()){
            Message msg = new Message();
            msg.what = 2;
            //Modified object to take a Bitmap and a string in its constructor
            msg.obj = new PlaceListItem(18,rcv_place,latitude,longitude, img, description);
            GLOBAL.m_handler.sendMessage(msg);
        }

//        Intent m_intent = getIntent();

        namelistAdapter = new PlaceListAdapter(GLOBAL.m_handler.getListArray());
        m_placelistView.setAdapter(namelistAdapter);
        namelistAdapter.notifyDataSetChanged();

        Button btn_create_place = findViewById(R.id.btn_add_newLink);
        btn_create_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GLOBAL.m_handler.setAppStarted();
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(i);
                MainActivity.this.finish();
            }
        });
    }
}
