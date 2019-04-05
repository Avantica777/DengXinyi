package com.media_player.dengxinyigoogleproject;

import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HttpDataHandler {
    public HttpDataHandler(){

    }

    public String getHttpData(String requestUrl){
        URL url;
        String response = "";
        try{
            url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(1996);
            conn.setConnectTimeout(1996);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            conn.setRequestProperty("x-api-key","AIzaSyDhdT7cIHg9Yrla19NduUS2a3-1Uhjjmbg");
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK)
            {
                Log.e("Handler ::","response is http ok");
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while((line = br.readLine()) !=null ){
                    response+=line;
                }
            }else
                response = "";

        }catch (ProtocolException e){
            Log.e("what's wrong?P:",e.toString());
        }catch (MalformedURLException e){
            Log.e("what's wrong?M:",e.toString());
        }catch (IOException e){
            Log.e("what's wrong?I:",e.toString());
        }
        return response;
    }
}
