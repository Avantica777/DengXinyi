package com.media_player.dengxinyigoogleproject;

import android.graphics.Bitmap;

public class PlaceListItem {
    private int no;
    private String place_name;
    private double lan;
    private double lan2;
    private String description;
    private Bitmap img;

    public PlaceListItem(){
        no = 0;
        place_name = "US";
        lan = 40.7143528;
        lan2 = -74.0059731;
        img = null;
        description = "";
    }

    public PlaceListItem(int no, String place_name, double lan, double lan2, Bitmap img, String description){
        this.no = no;
        this.place_name = place_name;
        this.lan = lan;
        this.lan2 = lan2;
        this.img = img;
        this.description = description;
    }

    public int getIndex(){
        return this.no;
    }
    public void setIndex(int no){
        this.no = no;
    }
    public String getPlaceName(){
        return this.place_name;
    }
    public void setPlaceName(String placename){
        this.place_name = placename;
    }
    public double getLan(){ return this.lan; }
    public void setLan(double lan){ this.lan = lan; }
    public double getLan2(){ return this.lan2; }
    public void setLan2() { this.lan2 = lan2; }

    public Bitmap getImg() {
        return img;
    }

    public String getDescription() {
        return description;
    }
}
