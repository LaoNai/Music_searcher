package com.example.musictool;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = getSharedPreferences("MusicData", Context.MODE_PRIVATE);
        String musicUrl = sharedPreferences.getString("musicUrl", "");
        System.out.println(musicUrl);
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(musicUrl);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals("com.example.musictool.PLAY")) {
                    playMusic();
                } else if (action.equals("com.example.musictool.PAUSE")) {
                    pauseMusic();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }



    // 播放音乐
    private void playMusic() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    // 暂停音乐
    private void pauseMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}







//    public void onCreate() {
//        super.onCreate();
//        // 从SharedPreferences中获取存储的musicUrl
//        SharedPreferences sharedPreferences = getSharedPreferences("MusicData", Context.MODE_PRIVATE);
//        String musicUrl = sharedPreferences.getString("musicUrl", "");
//
//        mediaPlayer = new MediaPlayer();
//        try {
//            mediaPlayer.setDataSource(musicUrl);
//            mediaPlayer.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mediaPlayer.start();
//    }

