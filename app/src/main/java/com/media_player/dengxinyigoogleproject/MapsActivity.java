package com.media_player.dengxinyigoogleproject;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private MapView mapView;
    private EditText et_search;
    private ImageButton btn_search;

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private boolean mPermissionLocationGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private GoogleApiClient client;
    private String placeID = null;
    private Context m_context = null;
    private double latitude = 0.0;
    private double longitude = 0.0;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        m_context = getApplicationContext();

        mapView = findViewById(R.id.map);
        Bundle mapViewBundle = null;
        if(savedInstanceState != null){
            mapViewBundle = savedInstanceState.getBundle("saved");
        }
        mapView.onCreate(mapViewBundle);
        if (!isServiceOk())
            return;

        GLOBAL.m_handler.setMapsActivity(this);
        client = new GoogleApiClient.Builder(m_context)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this,this).build();

        Intent tmp_int = getIntent();
        latitude = tmp_int.getDoubleExtra("latitude",40.7143528);
        longitude = tmp_int.getDoubleExtra("longitude",-74.0059731);
        getLocationPermission();
        et_search = (EditText)findViewById(R.id.input_search);
        btn_search = (ImageButton) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new GetCordinates().execute(et_search.getText().toString().replace(" ","+"));   //same with getSearchResults() function
                getSearchResults();
            }
        });
    }

    public boolean isServiceOk(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(m_context);

        if (available == ConnectionResult.SUCCESS)
        {
            Log.d("Maps Activity","Service is ok");
            return true;
        }else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d("Maps Activity","Service is not ok");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapsActivity.this, available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(MapsActivity.this,"you cannot make a map request!",Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),permissions[0])==PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),permissions[1])==PackageManager.PERMISSION_GRANTED)
            {
                mPermissionLocationGranted = true;
                initMap();
            }else
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }else
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
    }
    private void init_search(){
        Log.e("Maps Activity","Init Search Called!");
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH||actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER)
                {
//                    Log.e("eeeeeeeeeee","key pressed ");
                    getSearchResults();
                }
                return false;
            }
        });
    }

    private void initMap(){

        mapView.getMapAsync(this);
    }

    LatLng cur_latlan = new LatLng(0,0);
    String cur_place = "No Result";
    String cur_address = "where?";

    private void MoveCamera(LatLng laglng, float zome, String address, String placeNAME){
        // Add a marker in Sydney and move the camera
        final String place = address;
        cur_place = placeNAME;
        cur_address = address;
        cur_latlan = laglng;
        mMap.addMarker(new MarkerOptions().position(cur_latlan).title("Marker in NYC"));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                PendingResult<PlacePhotoMetadataResult> placeResult = Places.GeoDataApi.getPlacePhotos(client,place);
                placeResult.setResultCallback(mPlaceResultCallback);

                return false;
            }
        });
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cur_latlan,zome));

    }

    private ResultCallback<PlacePhotoMetadataResult> mPlaceResultCallback = new ResultCallback<PlacePhotoMetadataResult>(){

        @Override
        public void onResult(@NonNull PlacePhotoMetadataResult placePhotoMetadataResult) {
            if (placePhotoMetadataResult.getStatus().isSuccess()){
                Log.e("Place Photo Available",placePhotoMetadataResult.toString());
                try {
                    PlacePhotoMetadata photoMetaRes = placePhotoMetadataResult.getPhotoMetadata().get(0);
                    Log.e("Photo Num >0", photoMetaRes.toString());
                    Log.e("let me see",photoMetaRes.getAttributions().toString());
                    PendingResult<PlacePhotoResult> photoRes =  photoMetaRes.getScaledPhoto(client,100,100);
                    photoRes.setResultCallback(new ResultCallback<PlacePhotoResult>() {
                        @Override
                        public void onResult(@NonNull PlacePhotoResult placePhotoResult) {
                            Toast.makeText(MapsActivity.this,placePhotoResult.toString(),Toast.LENGTH_LONG).show();
                            Intent i = new Intent(MapsActivity.this,MainActivity.class);
                            i.putExtra("placeName",cur_place);
                            i.putExtra("latitude",cur_latlan.latitude);
                            i.putExtra("longitude",cur_latlan.longitude);
                            MapsActivity.this.startActivity(i);
                            MapsActivity.this.finish();
                        }
                    });
                }catch(NullPointerException e){
                    Toast.makeText(MapsActivity.this,"No photo Result!!!",Toast.LENGTH_LONG).show();
                    Intent i = new Intent(MapsActivity.this,MainActivity.class);
                    i.putExtra("placeName",cur_place);
                    i.putExtra("latitude",cur_latlan.latitude);
                    i.putExtra("longitude",cur_latlan.longitude);
                    MapsActivity.this.startActivity(i);
                    MapsActivity.this.finish();
                }catch (IllegalStateException e){
                    Toast.makeText(MapsActivity.this,"No photo Result!!!",Toast.LENGTH_LONG).show();
                    Intent i = new Intent(MapsActivity.this,MainActivity.class);
                    i.putExtra("placeName",cur_place);
                    i.putExtra("latitude",cur_latlan.latitude);
                    i.putExtra("longitude",cur_latlan.longitude);
                    MapsActivity.this.startActivity(i);
                    MapsActivity.this.finish();
                }
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionLocationGranted = false;
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length>0){
                    for (int i = 0; i < grantResults.length; i ++){
                        if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
                            mPermissionLocationGranted = false;
                    }
                }
                mPermissionLocationGranted = true;
                initMap();
        }
    }

    private void getSearchResults(){
        String searchString = et_search.getText().toString();
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        if(Geocoder.isPresent())
            Toast.makeText(MapsActivity.this,"Geocoder present!!!",Toast.LENGTH_LONG).show();
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString,1);
        }catch (IOException e){
            Log.d("Maps Activity",e.getMessage());
        }
        if (list.size()>0){
            Address address = list.get(0);

//            Toast.makeText(MapsActivity.this,address.toString(),Toast.LENGTH_LONG).show();
            MoveCamera(new LatLng(address.getLatitude(),address.getLongitude()),12, address.toString(), address.getAddressLine(0));

        }else{
            MoveCamera(new LatLng(40.700008, -74.0000001),12,et_search.getEditableText().toString(),et_search.getEditableText().toString());
//            Toast.makeText(MapsActivity.this,"No result!!!",Toast.LENGTH_LONG).show();
        }
    }

    private class GetCordinates extends AsyncTask<String,Void,String>{

        ProgressDialog mpdg = new ProgressDialog(MapsActivity.this);
        @Override
        protected String doInBackground(String... strings) {
            String response = "";
            String address = strings[0];
            HttpDataHandler http = new HttpDataHandler();
            String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s",address);
            response = http.getHttpData(url);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            mpdg.cancel();
            Log.e("Response from Google",s);
//            try{
//                JSONObject jsonObject = new JSONObject(s);
                Log.e("Response from Google",s);
//            }catch (JSONException e){e.printStackTrace();}
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mpdg.setMessage("Please Wait while Searching...");
            mpdg.setCanceledOnTouchOutside(false);
            mpdg.show();
        }
    }
    //we need to override

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle  = outState.getBundle("saved");
        if(mapViewBundle==null){
            //no saved state
            mapViewBundle = new Bundle();
            outState.putBundle("saved", mapViewBundle);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(MapsActivity.this,"Google Map Ready to Use!!!",Toast.LENGTH_LONG).show();
        mMap = googleMap;

        LatLng dengxinyi = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(dengxinyi).title("Marker in NYC"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dengxinyi,12));
        init_search();
    }

    @Override
    public void finish() {
        super.finish();
        final Intent i = new Intent(MapsActivity.this,MainActivity.class);
        i.putExtra("placeName",cur_place);
        i.putExtra("latitude",cur_latlan.latitude);
        i.putExtra("longitude",cur_latlan.longitude);
        this.getImageDescript(i);
        MapsActivity.this.startActivity(i);
    }

    /**
     * get the user input regarding the image as well as the description, which is then added to the intent
     * which will be passed back to the main activity
     * @param i
     */
    private void getImageDescript(final Intent i) {
        //The dialog that pops up asking the user to input image url and description
        Dialog newDialog = new Dialog(this);
        newDialog.setContentView(R.layout.dialog);
        newDialog.setCancelable(false);

        Button ok = newDialog.findViewById(R.id.fetch_image);

        final String img_url = ((EditText) newDialog.findViewById(R.id.map_url)).getText().toString();
        String description = ((EditText) newDialog.findViewById(R.id.map_descrip)).getText().toString();
        i.putExtra("description", description);
        final ImageTaskDownload task = new ImageTaskDownload();
        newDialog.show();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Bitmap img = task.execute(img_url).get();
                    i.putExtra("image", img);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(m_context, "No Image found", Toast.LENGTH_LONG).show();
                }

            }
        });

    }
}
