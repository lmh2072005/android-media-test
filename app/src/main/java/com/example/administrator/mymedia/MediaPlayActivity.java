package com.example.administrator.mymedia;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class MediaPlayActivity extends AppCompatActivity {
    private ImageButton btnPre, btnPlay, btnNext;
    private String itemId,itemTitle,itemSinger;
    private ArrayList mediaList;
    private int curPosition;
    private TextView songTitle;
    private ImageButton btnBack;
    private Intent intent2;
    private boolean isPlaying = false;
    private HashMap itemData;

    private SeekBar seekBarVoice;
    private int maxSound;
    private int curSound;
    private AudioManager audioManager;
    private TextView seekBarVoiceText;

    private SeekBar seekBarMedia;
    private int maxMediaProgress;
    private int curMediaProgress;
    private TextView seekBarMediaCurText;
    private TextView seekBarMediaMaxText;
    SeekBarReceiver receiver;
    boolean seekbarTrackTouch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_play);
        btnBack = (ImageButton) findViewById(R.id.btn_back);
        songTitle = (TextView) findViewById(R.id.media_song_title);
        System.out.println("activity created");
        //控制声音
        /*seekBarVoice = (SeekBar) findViewById(R.id.seekBarVoice);
        seekBarVoiceText = (TextView) findViewById(R.id.seekBarVoiceText);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE); //获取系统音量服务
        maxSound = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); //获取系统音量最大值
        curSound = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC); //获取当前音量值
        seekBarVoice.setMax(maxSound); //音量控制设置最大值为系统音量最大值
        seekBarVoice.setProgress(curSound); //设置当前值
        seekBarVoiceText.setText((int)((float)curSound / maxSound * 100) + "%");
        seekBarVoice.setOnSeekBarChangeListener(new seekBarVoiceListener());*/

        //控制播放进度
        seekBarMedia = (SeekBar) findViewById(R.id.seekBarMedia);
        seekBarMediaCurText = (TextView) findViewById(R.id.seekBarMediaCurText);
        seekBarMediaMaxText = (TextView) findViewById(R.id.seekBarMediaMaxText);
        seekBarMedia.setOnSeekBarChangeListener(new SeekBarListener());



        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        itemId = bundle.getString("itemId");
        itemTitle = bundle.getString("itemTitle");
        itemSinger = bundle.getString("itemSinger");
        curPosition = bundle.getInt("position");
        mediaList = bundle.getStringArrayList("mediaList");
        itemData = (HashMap) mediaList.get(curPosition);

        setPlayingSongTitle(curPosition);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnPre = (ImageButton) findViewById(R.id.btn_play_pre);
        btnPlay = (ImageButton) findViewById(R.id.btn_play_play);
        btnNext = (ImageButton) findViewById(R.id.btn_play_next);
        intent2 = new Intent(this, MediaService.class);
        playMedia("init");
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    playMedia("stop");
                }else{
                    playMedia("play");
                }

            }
        });

        btnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(--curPosition < 0){
                    curPosition = mediaList.size() - 1 ;
                }
                setPlayingSongTitle(curPosition);
                isPlaying = false;
                setPlayBtnBg();
                intent2.putExtra("playInfo", "pre");
                startService(intent2);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(++curPosition >= mediaList.size()){
                    curPosition = 0;
                }
                setPlayingSongTitle(curPosition);
                isPlaying = false;
                setPlayBtnBg();
                intent2.putExtra("playInfo", "next");
                startService(intent2);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("activity started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new SeekBarReceiver();
        this.registerReceiver(receiver, new IntentFilter("com.myMediaBroadCast.seekbar"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private void setPlayingSongTitle(int index){
        itemData = (HashMap) mediaList.get(index);
        songTitle.setText(itemData.get("itemTitle").toString());
    }
    //playInfo : play/stop/init
    private void playMedia(String playInfo){
        setPlayBtnBg();
        intent2.putExtra("playInfo", playInfo);
        intent2.putExtra("initPosition", curPosition);
        startService(intent2);
       // seekBarMediaMaxText.setText(MediaService.myMediaPlayer.getDuration());
    }
    private void setPlayBtnBg(){
        if(isPlaying){
            btnPlay.setImageResource(R.drawable.btn_play);
            isPlaying = false;
        }else{
            isPlaying = true;
            btnPlay.setImageResource(R.drawable.btn_stop);
        }
    }

    //音量控制
    /*class seekBarVoiceListener implements SeekBar.OnSeekBarChangeListener{
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                int seekPosition = seekBar.getProgress();
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seekPosition, 0);
                seekBarVoiceText.setText((int)((float)seekPosition / maxSound * 100) + "%");
                Toast.makeText(MediaPlayActivity.this, "position:"+seekPosition+"; maxsound:"+maxSound, Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }*/


    //返回操作
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    //拖动播放进度
    class SeekBarListener implements SeekBar.OnSeekBarChangeListener {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }
        public void onStartTrackingTouch(SeekBar seekBar) {
            seekbarTrackTouch = true;
        }
        public void onStopTrackingTouch(SeekBar seekBar) {
            seekbarTrackTouch = false;
            int currentPosition = seekBar.getProgress();  //当前刻度
            Intent intent = new Intent("com.myMediaBroadCast.seekbarTo");
            intent.putExtra("currentPosition", currentPosition);
            sendBroadcast(intent);
        }
    }

    class SeekBarReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int curIndex = intent.getIntExtra("curIndex", 0); //当前播放歌曲索引
            int currentPosition = intent.getIntExtra("currentPosition", 0);  //当前播放毫秒
            int duration = intent.getIntExtra("duration", 0); //总毫秒
            System.out.println("activity收到一个广播：currentPosition=" + currentPosition + " duration=" + duration);
            SimpleDateFormat dataFormat = new SimpleDateFormat("mm:ss");  //毫秒转换成分秒
            seekBarMediaMaxText.setText(dataFormat.format(duration));  //设置歌曲最大播放时间
            if(!seekbarTrackTouch){
                //将播放进度条分成100份， 每份为:per(毫秒) = duration(持续总毫秒)/100,
                // 所以当前进度(刻度)为： graduation = currentPosition/per = currentPosition*100/duration;
                //当前播放时间：currentPosition
                seekBarMedia.setProgress(currentPosition * 100 / duration);  //设置时时进度条变化
                seekBarMedia.invalidate();

                seekBarMediaCurText.setText(dataFormat.format(currentPosition)); //设置时间变化
                curPosition = curIndex; //当前索引号
                setPlayingSongTitle(curIndex); //设置自带切换歌曲时标题变化
            }
        }
    }
}
