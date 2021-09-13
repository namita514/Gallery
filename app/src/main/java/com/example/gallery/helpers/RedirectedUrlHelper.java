package com.example.gallery.helpers;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RedirectedUrlHelper extends AsyncTask<String ,Void, String> {
private  OnCompleteListener listener1;


   public RedirectedUrlHelper fetchRedirectedUrl(OnCompleteListener listener){
       listener1=listener;
       return this;
   }

    @Override
    protected String doInBackground(String... strings) {
       return getRedirectedUrl(strings[0]);
    }
    public String getRedirectedUrl(String url){
        URL uTemp = null;
        String redUrl;
        HttpURLConnection connection = null;

        try{
            uTemp = new URL(url);
        } catch (MalformedURLException exp){
            exp.printStackTrace();
        }

        try{
            connection = (HttpURLConnection) uTemp.openConnection();
        } catch (IOException e){
            e.printStackTrace();
        }

        try{
            connection.getResponseCode();
        } catch (IOException e){
            e.printStackTrace();
        }

        redUrl = connection.getURL().toString();
        connection.disconnect();

        return redUrl;

    }

    @Override
    protected void onPostExecute(String s) {
       listener1.onFetched(s);


    }
     public interface OnCompleteListener{
       void onFetched(String redirectedUrl);
    }
}
