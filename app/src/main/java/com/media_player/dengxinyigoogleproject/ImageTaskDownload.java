package com.media_player.dengxinyigoogleproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Image Task download, using this thread to download image link specified by users
 */
public class ImageTaskDownload extends AsyncTask<String, Void, Bitmap> {
    @Override
    protected Bitmap doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream stream = urlConnection.getErrorStream();

            Bitmap img = BitmapFactory.decodeStream(stream);
            return img;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
