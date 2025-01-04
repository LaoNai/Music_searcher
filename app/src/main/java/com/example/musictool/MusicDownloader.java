package com.example.musictool;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

public class MusicDownloader extends MainActivity{
    private long downloadId;

    public MusicDownloader() {
        // 注册下载完成的广播接收器
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
    }

    public void downloadMusic(Context context, String musicUrl, String songName, String singer) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        // 创建下载请求
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(musicUrl));

        // 设置文件保存路径和文件名
        String fileName = songName + "_" + singer + ".mp3";
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        // 可选：设置下载标题和描述
        request.setTitle(songName + " - " + singer);
        request.setDescription("Downloading...");

        // 可选：设置通知可见性
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // 开始下载并获取下载任务的 ID
        downloadId = downloadManager.enqueue(request);
    }

    // 广播接收器，用于监听下载完成事件
    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long completedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (completedDownloadId == downloadId) {
                // 下载完成，可以执行一些操作
                // 例如，显示一个通知或者提示用户下载已完成
            }
        }
    };
}
