package com.example.musictool;

import static java.io.File.createTempFile;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.widget.TextView;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.FormBody;

import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchEditText = findViewById(R.id.searchEditText);
        Button searchButton = findViewById(R.id.searchButton);
        FloatingActionButton lrcButton = findViewById(R.id.lrcButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTerm = searchEditText.getText().toString();
                new MusicSearchTask().execute(searchTerm);
            }
        });

        lrcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MusicPlayerActivity.class);

                startActivity(intent);
            }
        });
    }


    private class MusicSearchTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String searchTerm = params[0];
            String responseText = "";

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("input", searchTerm)
                    .add("filter", "name")
                    .add("type", "netease")
                    .add("page", "1")
                    .build();

            Request request = new Request.Builder()
                    .url("https://music.haom.ren/")
                    .addHeader("X-Requested-With", "XMLHttpRequest")
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    responseText = response.body().string();
                } else {
                    responseText = "请求失败";
                }
            } catch (IOException e) {
                e.printStackTrace();
                responseText = "请求异常";
            }

            return responseText;
        }


        protected void onPostExecute(String result) {
            try {
                // 解析JSON响应
                JSONObject jsonResponse = new JSONObject(result);
                JSONArray dataArray = jsonResponse.getJSONArray("data");
                // 获取LinearLayout容器
                // 获取音乐盒子容器的引用
                LinearLayout musicBoxContainer = findViewById(R.id.musicBoxContainer);

                // 清除旧的音乐盒子内容
                musicBoxContainer.removeAllViews();

                // 遍历data数组，为每个数据项创建一个music_box组件
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject dataItem = dataArray.getJSONObject(i);
                    // 从dataItem中获取title、author和pic字段的值
                    String title = dataItem.optString("title");
                    String author = dataItem.optString("author");
                    String picUrl = dataItem.optString("pic");
                    String musicUrl = dataItem.optString("url");// 获取音乐URL
                    String lrc = dataItem.optString("lrc");

                    // 使用LayoutInflater动态加载music_box布局文件
                    View musicBoxView = getLayoutInflater().inflate(R.layout.music_box, null);

                    // 找到music_box布局中的各个视图元素并设置内容
                    TextView songName = musicBoxView.findViewById(R.id.songName);
                    TextView artistName = musicBoxView.findViewById(R.id.artistName);
                    ImageView musicImageView = musicBoxView.findViewById(R.id.albumImage);

                    Button playButton = musicBoxView.findViewById(R.id.playButton); // 找到播放按钮
                    Button downloadButton = musicBoxView.findViewById(R.id.downloadButton);// 找到下载按钮

                    downloadButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MusicDownloader musicDownloader = new MusicDownloader();
                            musicDownloader.downloadMusic(MainActivity.this, musicUrl, title, author);
                        }
                    });

                    songName.setText(title);
                    artistName.setText("歌手：" + author);

                    // 使用Glide加载音乐封面图像并设置到musicImageView中
                    Glide.with(MainActivity.this).load(picUrl).into(musicImageView);

                    // 为播放按钮设置点击事件监听器
                    playButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            SharedPreferences sharedPreferences = getSharedPreferences("MusicData", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("lrc", lrc);
                            editor.putString("musicUrl", musicUrl);
                            editor.apply();

                            Intent serviceIntent = new Intent(MainActivity.this, MusicService.class);
                            serviceIntent.setAction("com.example.musictool.PLAY");
                            startService(serviceIntent);
                        }
                    });
                    musicBoxContainer.addView(musicBoxView);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}




