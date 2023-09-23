package com.example.musictool;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import android.widget.TextView;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.FormBody;
import android.media.MediaPlayer;
import java.io.IOException;

import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private EditText searchEditText;
    private Button searchButton;
    private TextView responseTextView;

    private MediaPlayer mp;
//    private void showDeveloperInfoDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("关于")
//                .setMessage("这个应用程序由开发者开发。\n联系开发者：\n" )
//                .setPositiveButton("关闭", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss(); // 关闭对话框
//                    }
//                });
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }
    //showDeveloperInfoDialog();展示开发者信息

    private void downMusic(String musicUrl, String songName,String singer) {
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        // 创建下载请求
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(musicUrl));

        // 设置文件保存路径和文件名
        String fileName = songName +"_"+singer+ ".mp3"; // 使用歌曲名作为文件名
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        // 可选：设置下载标题和描述
        request.setTitle(songName+"_"+singer + ".mp3");
        request.setDescription("下载ing...");

        // 可选：设置通知可见性
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // 开始下载并获取下载任务的 ID
        long downloadId = downloadManager.enqueue(request);

        // 可选：如果你想监听下载完成事件，可以注册广播接收器
        // 这里我们不再需要在方法内注册，因为在 onCreate 中已经注册了
    }



    // 在播放按钮的点击事件处理程序中调用 playMusic 方法
    private void playMusic(String musicUrl) {
        try {

            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.reset();
            mp.setDataSource(musicUrl); // 设置音乐文件的数据源
            mp.prepare(); // 准备音乐播放
            mp.start(); // 开始播放音乐
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mp = new MediaPlayer();

        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        responseTextView = findViewById(R.id.responseTextView);


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTerm = searchEditText.getText().toString();
                new MusicSearchTask().execute(searchTerm);
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


        @Override
        protected void onPostExecute(String result) {
            try {
                // 解析JSON响应
                JSONObject jsonResponse = new JSONObject(result);
                JSONArray dataArray = jsonResponse.getJSONArray("data");

                // 获取LinearLayout容器
                // 获取音乐盒子容器的引用
                LinearLayout musicBoxContainer = findViewById(R.id.musicBoxContainer);

// 清除旧的音乐盒子内容
                // 清除旧的音乐盒子内容
                musicBoxContainer.removeAllViews();

// 遍历data数组，为每个数据项创建一个music_box组件
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject dataItem = dataArray.getJSONObject(i);

                    // 从dataItem中获取title、author和pic字段的值
                    String title = dataItem.optString("title");
                    String author = dataItem.optString("author");
                    String picUrl = dataItem.optString("pic");
                    String musicUrl = dataItem.optString("url"); // 获取音乐URL

                    // 使用LayoutInflater动态加载music_box布局文件
                    View musicBoxView = getLayoutInflater().inflate(R.layout.music_box, null);

                    // 找到music_box布局中的各个视图元素并设置内容
                    TextView songName = musicBoxView.findViewById(R.id.songName);
                    TextView artistName = musicBoxView.findViewById(R.id.artistName);
                    ImageView musicImageView = musicBoxView.findViewById(R.id.albumImage);
                    Button playButton = musicBoxView.findViewById(R.id.playButton); // 找到播放按钮
                    Button downloadButton = musicBoxView.findViewById(R.id.downloadButton); // 找到下载按钮
                    downloadButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            downMusic (musicUrl, title,author);
                            // 在这里处理下载按钮的点击事件
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
                            // 在这里处理播放按钮的点击事件
                            // 播放音乐
                            playMusic(musicUrl);
                        }
                    });

                    // 将music_box组件添加到容器中
                    musicBoxContainer.addView(musicBoxView);
                }


            } catch (JSONException e) {
                e.printStackTrace();
                responseTextView.setText("JSON解析错误");
            }
        }

    }


    }



