package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {
    private String BASE_URL = "https://www.googleapis.com/youtube/v3/search?key=AIzaSyAcqsfnhOFIyIgX1auWR-SzjcTXOoc3MDE&part=snippet,id&order=viewCount&maxResults=1&q=";
    private String text = "despacito";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {

                String videolink = requestData(BASE_URL + text);
                JSONObject videojson = null;
                String video = null;
                JSONArray videoarray = null;
                JSONObject videoarray1 = null;
                try {
                    videojson = new JSONObject(videolink);
                    videoarray = videojson.getJSONArray("items");
                    videoarray1 = videoarray.getJSONObject(0).getJSONObject("id");
                    video = videoarray1.getString("videoId");
                    Log.d("FUCKYOU",videoarray1.getString("videoId"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                youTubePlayer.loadVideo(video, 0);

            }
        });
    }

    private String requestData(String urlstring) {

        try {
            final String[] response = new String[1];
            final CountDownLatch latch = new CountDownLatch(1);
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Log.d("START", "Starting GET");
                        URL url = new URL(urlstring);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(5000);
                        connection.setReadTimeout(5000);
                        connection.connect();
                        Log.d("INFO", urlstring);
                        Log.d("INFO", Integer.toString(connection.getResponseCode()));
                        Log.d("INFO", connection.getResponseMessage());
                        BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String content = "", line;
                        while ((line = rd.readLine()) != null) {
                            content += line + "\n";
                        }
                        response[0] = content;
                        Log.d("SUCCESS", response[0]);
                        latch.countDown();
                    } catch (Exception ex) {
                        Log.d("ERROR", "Error Processing Get Request...");
                        for (int i = 0; i < ex.getStackTrace().length; i++) {
                            Log.d("ERROR", ex.getStackTrace()[i].toString());
                        }
                        latch.countDown();
                    }
                }

            }).start();
            latch.await();
            return response[0];
        } catch (Exception ex) {
            return "";
        }


    }


}