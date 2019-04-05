package com.media_player.dengxinyigoogleproject;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.util.ArrayList;

public class mHandler extends Handler {
    public ArrayList<PlaceListItem> m_nameArray = null;
    private MapsActivity mapsActivity = null;
    private MainActivity mainActivity = null;
    private boolean is_started_app = false;

    public void setAppStarted(){
        is_started_app = true;
    }

    public boolean getAppStarted(){
        return is_started_app;
    }

    public ArrayList<PlaceListItem> getListArray(){
        if (m_nameArray == null) {
            m_nameArray = new ArrayList<PlaceListItem>();
            m_nameArray.clear();
        }
        return m_nameArray;
    }

    public void setMapsActivity(MapsActivity mapsActivity){
        this.mapsActivity = mapsActivity;
    }

    public void setMainActivity(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }
    @Override
    public void handleMessage(Message msg) {
        if (msg.what == 1){
            if (m_nameArray == null){
                m_nameArray = new ArrayList<PlaceListItem>();
                m_nameArray.clear();
            }
        }
        else if(msg.what == 2){
            PlaceListItem item = (PlaceListItem) msg.obj;
            m_nameArray.add(item);
        }else if(msg.what == 3){
            Toast.makeText(mapsActivity,Double.toString(((PlaceListItem)msg.obj).getLan()),Toast.LENGTH_LONG).show();
            Intent i = new Intent(mainActivity,MapsActivity.class);
            i.putExtra("placeName",((PlaceListItem)msg.obj).getPlaceName());
            i.putExtra("latitude",((PlaceListItem)msg.obj).getLan());
            i.putExtra("longitude",((PlaceListItem)msg.obj).getLan2());
            mainActivity.startActivity(i);
            mainActivity.finish();
        }
    }
}
