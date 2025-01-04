package top.papierkran.musictool;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

import com.example.musictool.R;


public class MusicPlayerActivity extends AppCompatActivity {

    private boolean isPlaying;
    private String lrcData;

    private String newMusicUrl;

    public MusicPlayerActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        // 从SharedPreferences中获取存储的lrc和musicUrl
        SharedPreferences sharedPreferences = getSharedPreferences("MusicData", Context.MODE_PRIVATE);
        String lrcData = sharedPreferences.getString("lrc", "");
        String musicUrl = sharedPreferences.getString("musicUrl", "");

        // 创建用于启动 MusicService 的 Intent
        Intent serviceIntent = new Intent(this, MusicService.class);

        if (!musicUrl.isEmpty()) {
            // 设置音乐文件URL
            serviceIntent.putExtra("musicUrl", musicUrl);
//            serviceIntent.setAction("com.example.musictool.PLAY");

            startService(serviceIntent);
        }




        // 获取包含歌词的布局
        LinearLayout lrcContainer = findViewById(R.id.lyricsView); // 确保你的布局中有一个名为lyricsView的LinearLayout

        // 分割歌词数据成单独的歌词行
        String[] lrcLines = lrcData.split("\n");

        // 遍历歌词行并为它们创建 TextView 控件
        for (String lrcLine : lrcLines) {
            TextView lrcTextView = new TextView(this);
            lrcTextView.setText(lrcLine);
            lrcTextView.setTextColor(Color.BLACK); // 设置文本颜色为黑色
            lrcTextView.setTextSize(19); // 设置文本大小为19sp
            lrcTextView.setGravity(Gravity.CENTER);

            // 添加 TextView 到布局
            lrcContainer.addView(lrcTextView);
        }


        Button playButton = findViewById(R.id.playButton); // 请确保在布局中有一个名为 playButton 的按钮
        // 点击播放按钮
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying) {
                    // 发送暂停命令
                    serviceIntent.setAction("com.example.musictool.PAUSE");
                    startService(serviceIntent);
                    isPlaying = false;
                    playButton.setText("Play");
                } else {
                    // 发送播放命令
                    serviceIntent.setAction("com.example.musictool.PLAY");
                    startService(serviceIntent);
                    isPlaying = true;
                    playButton.setText("Pause");
                }
            }

        });
    }
}

//package com.example.musictool;
//
//        import android.app.Notification;
//        import android.app.NotificationChannel;
//        import android.app.NotificationManager;
//        import android.app.Service;
//        import android.content.ComponentName;
//        import android.content.Context;
//        import android.content.ServiceConnection;
//        import android.content.SharedPreferences;
//        import android.os.IBinder;
//        import android.text.SpannableStringBuilder;
//        import android.content.Intent;
//        import android.graphics.Color;
//        import android.media.MediaPlayer;
//        import android.os.Build;
//        import android.os.Bundle;
//        import android.os.Handler;
//        import android.text.Spannable;
//        import android.text.SpannableString;
//        import android.text.style.ForegroundColorSpan;
//        import android.text.style.RelativeSizeSpan;
//        import android.view.View;
//        import android.widget.Button;
//        import android.widget.SeekBar;
//        import android.widget.TextView;
//        import androidx.appcompat.app.AppCompatActivity;
//        import androidx.constraintlayout.widget.ConstraintHelper;
//        import androidx.core.app.NotificationCompat;
//
//        import java.io.IOException;
//        import java.security.Provider;
//
//public class MusicPlayerActivity extends MainActivity {
//
//    private MediaPlayer mediaPlayer;
//    private Button playButton;
//    private SeekBar seekBar;
//    private TextView currentTimeView;
//    private TextView totalTimeView;
//
//    private boolean isPlaying;
//    private int totalDuration;
//    private Handler handler;
//    private String lrc;
//    private TextView lyricsView;
//
//    private boolean isBound = false;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_music_player);
//
//        // 从Intent中获取音乐文件路径
//        Intent intent = getIntent();
//        String title = intent.getStringExtra("title");
//        String author = intent.getStringExtra("author");
//        lrc = intent.getStringExtra("lrc");
//
//
//        // 从SharedPreferences中获取存储的lrc和musicUrl
//        SharedPreferences sharedPreferences = getSharedPreferences("MusicData", Context.MODE_PRIVATE);
//        String musicUrl = sharedPreferences.getString("musicUrl", "");
//        String lrc = sharedPreferences.getString("lrc", "");
//
//        // 使用musicUrl和lrc进行后续操作
//
//
//        playButton = findViewById(R.id.playButton);
//        seekBar = findViewById(R.id.seekBar);
//        currentTimeView = findViewById(R.id.currentTime);
//        totalTimeView = findViewById(R.id.totalTime);
//        lyricsView = findViewById(R.id.lyricsView);
//        lyricsView.setText(lrc); // 设置歌词内容
//
//
//        // 初始化 MediaPlayer 并设置数据源
//        mediaPlayer = new MediaPlayer();
//        try {
//            mediaPlayer.setDataSource(musicUrl);
//            mediaPlayer.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mediaPlayer.start();
//
//        totalDuration = mediaPlayer.getDuration();
//        seekBar.setMax(totalDuration);
//
//        handler = new Handler();
//        updateSeekBar();
//
//        playButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (isPlaying) {
//                    mediaPlayer.start();
//                    isPlaying = false;
//                    playButton.setText("Pause");
//                    updateSeekBar();
//                } else {
//                    mediaPlayer.pause();
//                    isPlaying = true;
//                    playButton.setText("Play");
//                }
//
//            }
//        });
//    }
//
//
//    private void updateLyricsHighlight() {
//        String[] strings = content.split("\n");
//
//        lyric = new Lyric();
//
//        TreeMap<Integer, Line> lyrics = new TreeMap<>();
//        Map<String, Object> tags = new HashMap<>();
//
//        String lineInfo=null;
//        int lineNumber = 0;
//        for (int i = 0; i < strings.length; i++) {
//            try {
//                lineInfo=strings[i];
//                Line line = parserLine(tags, lineInfo);
//                if (line != null) {
//                    lyrics.put(lineNumber, line);
//                    lineNumber++;
//                }
//            } catch (Exception var9) {
//                var9.printStackTrace();
//            }
//        }
//
//        lyric.setLyrics(lyrics);
//        lyric.setTags(tags);
//
///**
// * 解析每一行歌词
// */
//        private Line parserLine(Map<String, Object> tags, String lineInfo) {
//            if (lineInfo.startsWith("[0")) {
//                //歌词开始了
//                Line line = new Line();
//
//                int leftIndex = 1;
//                int rightIndex = lineInfo.length();
//                String[] lineComments = lineInfo.substring(leftIndex, rightIndex).split("]", -1);
//
//                //开始时间
//                String startTimeStr = lineComments[0];
//                int startTime = TimeUtil.parseInteger(startTimeStr);
//                line.setStartTime(startTime);
//
//                //歌词
//                String lineLyricsStr = lineComments[1];
//                line.setLineLyrics(lineLyricsStr);
//
//                return line;
//            }
//
//            return null;
//        }
//        private void drawLyricText(Canvas canvas) {
//            //在当前位置绘制正在演唱的歌词
//            Line line = lyricsLines.get(lineNumber);
//
//            //当前歌词的宽高
//            float textWidth = getTextWidth(backgroundTextPaint, line.getLineLyrics());
//            float textHeight = getTextHeight(backgroundTextPaint);
//
//            float centerY = (getMeasuredHeight() - textHeight) / 2 + lineNumber * getLineHeight(backgroundTextPaint) - offsetY;
//
//            float x = (getMeasuredWidth() - textWidth) / 2;
//            float y = centerY;
//
//            //当前歌词高亮
//            if (lyric.isAccurate()) {
//                //TODO 精确到字，歌词，下一节讲解
//            } else {
//                //精确到行
//                canvas.drawText(line.getLineLyrics(), x, y, foregroundTextPaint);
//            }
//
//
//            //绘制前面的歌词
//            for (int i = lineNumber - 1; i > 0; i--) {
//                //从当前行的上一行开始绘制
//                line = lyricsLines.get(i);
//
//                //当前歌词的宽高
//                textWidth = getTextWidth(backgroundTextPaint, line.getLineLyrics());
//                textHeight = getTextHeight(backgroundTextPaint);
//
//
//                x = (getMeasuredWidth() - textWidth) / 2;
//                y = centerY - (lineNumber - i) * getLineHeight(backgroundTextPaint);
//
//                if (y < getLineHeight(backgroundTextPaint)) {
//                    //超出了View顶部，不再绘制
//                    break;
//                }
//
//                canvas.drawText(line.getLineLyrics(), x, y, backgroundTextPaint);
//            }
//
//            //绘制后面的歌词
//            for (int i = lineNumber + 1; i < lyricsLines.size(); i++) {
//                //从当前行的下一行开始绘制
//                line = lyricsLines.get(i);
//
//                //当前歌词的宽高
//                textWidth = getTextWidth(backgroundTextPaint, line.getLineLyrics());
//                textHeight = getTextHeight(backgroundTextPaint);
//
//
//                x = (getMeasuredWidth() - textWidth) / 2;
//                y = centerY + (i - lineNumber) * getLineHeight(backgroundTextPaint);
//
//                if (y + getLineHeight(backgroundTextPaint) > getHeight()) {
//                    //超出了View底部，不再绘制
//                    break;
//                }
//
//                canvas.drawText(line.getLineLyrics(), x, y, backgroundTextPaint);
//            }
//
//
//            // 更新 SeekBar 和当前时间
//            private void updateSeekBar() {
//                seekBar.setProgress(mediaPlayer.getCurrentPosition());
//                int currentDuration = mediaPlayer.getCurrentPosition();
//                currentTimeView.setText(millisecondsToTimer(currentDuration));
//                // 更新歌词高亮
//                updateLyricsHighlight(); // 调用更新歌词高亮的方法
//
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        updateSeekBar();
//                    }
//                }, 1000);
//            }
//            if (valueAnimator != null && valueAnimator.isRunning()) {
//                valueAnimator.cancel();
//            }
//            valueAnimator = ValueAnimator.ofFloat(offsetY, distanceY);
//            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                    offsetY = (float) valueAnimator.getAnimatedValue();
//                    invalidate();
//                }
//            });
//
//            valueAnimator.setDuration(200);
//            valueAnimator.setInterpolator(new DecelerateInterpolator());
//            valueAnimator.start();
//
//
//            // 将毫秒转换为时间格式
//            public String millisecondsToTimer(long milliseconds) {
//                String timerString = "";
//                String secondsString;
//                int hours = (int) (milliseconds / (1000 * 60 * 60));
//                int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
//                int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
//                if (hours > 0) {
//                    timerString = hours + ":";
//                }
//                if (seconds < 10) {
//                    secondsString = "0" + seconds;
//                } else {
//                    secondsString = "" + seconds;
//                }
//                timerString = timerString + minutes + ":" + secondsString;
//                return timerString;
//            }
//
//            private long lrcTimestampToMillis(String timestamp) {
//                try {
//                    // 检查时间戳是否以 "[" 开头并以 "]" 结尾
//                    if (timestamp.startsWith("[") && timestamp.endsWith("]")) {
//                        // 去除 "[" 和 "]"，获取时间戳内容
//                        String timestampContent = timestamp.substring(1, timestamp.length() - 1);
//
//                        // 解析时间戳，将其转换为毫秒
//                        String[] parts = timestampContent.split(":");
//                        if (parts.length == 2) {
//                            String[] minutesAndSeconds = parts[0].split("\\.");
//                            int minutes = Integer.parseInt(minutesAndSeconds[0]);
//                            int seconds = Integer.parseInt(minutesAndSeconds[1]);
//                            int milliseconds = Integer.parseInt(parts[1]);
//
//                            return ((long) minutes * 60 * 1000) + (seconds * 1000L) + milliseconds;
//                        }
//                    }
//                } catch (NumberFormatException e) {
//                    e.printStackTrace();
//                }
//                return 0; // 默认返回0表示无效时间戳
//            }
//
//            @Override
//            protected void onDestroy() {
//                super.onDestroy();
//                // 停止播放和释放 MediaPlayer
//                if (mediaPlayer != null) {
//                    mediaPlayer.stop();
//                    mediaPlayer.release();
//                    mediaPlayer = null;
//                }
//                if (isBound) {
//                    isBound = false;
//                }
//            }
//
//        }
