package com.example.administrator.mymedia;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;

public class MediaService extends Service implements Runnable {
    public MediaPlayer myMediaPlayer;
    public static boolean isrunning = true;
    private boolean isPlaying = false;
    //当前播放的索引号
    private int curIndex = 0;
    //音乐地址
    private String[] myMusicList = {
            "http://other.web.rh01.sycdn.kuwo.cn/resource/n1/27/80/3472654610.mp3", //地址一定要加http
            "http://other.web.ra01.sycdn.kuwo.cn/resource/n3/41/65/3376056316.mp3",
            "http://other.web.rh01.sycdn.kuwo.cn/resource/n3/31/99/4014411466.mp3",
            "http://other.web.rh01.sycdn.kuwo.cn/resource/n2/38/79/3609675313.mp3",
            "http://other.web.rh01.sycdn.kuwo.cn/resource/n3/98/92/3639908249.mp3",
            "http://other.web.rh01.sycdn.kuwo.cn/resource/n3/50/53/390674508.mp3",
            "http://other.web.rh01.sycdn.kuwo.cn/resource/n2/54/65/4109259218.mp3",
            "http://other.web.rh01.sycdn.kuwo.cn/resource/n1/3/28/2585152413.mp3"
    };
    private boolean startMusicInited = false;  //播放器是否已经初始化过了

    private SeekbarReceiver receiver ;

    public MediaService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //播放音乐
    private void playMusic(String path){
        if(myMediaPlayer != null){
            myMediaPlayer.stop();
            myMediaPlayer.release();
            myMediaPlayer = null;
        }
        myMediaPlayer = new MediaPlayer();
        try {
            startMusicInited = true;
            myMediaPlayer.setDataSource(this, Uri.parse(path));
            myMediaPlayer.prepare();
            myMediaPlayer.start();
            myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    nextMusic();
                }
            });
            new Thread(this).start();
            isPlaying = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //下一首
    private void nextMusic(){
        isPlaying = false;
        if (++curIndex >= myMusicList.length){
            curIndex = 0;
        }
        playMusic(myMusicList[curIndex]);
    }

    //上一首
    private void preMusic(){
        isPlaying = false;
        if (--curIndex <0){
            curIndex = myMusicList.length - 1;
        }
        playMusic(myMusicList[curIndex]);
    }

    //播放
    private void playFn(){
        isPlaying = false;
        if(startMusicInited){
            myMediaPlayer.start();
            isPlaying = true;
        }else{
            playMusic(myMusicList[curIndex]);
        }
    }
    //暂停
    private void stopFn(){
        isPlaying = false;
        if(myMediaPlayer.isPlaying()){
            myMediaPlayer.pause();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (myMediaPlayer != null) {
            myMediaPlayer.reset();
            myMediaPlayer.release();
            myMediaPlayer = null;
        }
        myMediaPlayer = new MediaPlayer();

        //接收广播
        receiver = new SeekbarReceiver();
        registerReceiver(receiver, new IntentFilter("com.myMediaBroadCast.seekbarTo"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String playInfo = intent.getExtras().getString("playInfo");
        if(playInfo.equals("init")){
            startMusicInited = false;
            curIndex = intent.getExtras().getInt("initPosition");
            playFn();
        }else if(playInfo.equals("play")){   //java 里面字符串比较要用equals ! ! !
            playFn();
        }else if (playInfo.equals("stop")){
            stopFn();
        }else if(playInfo.equals("pre")){
            preMusic();
        }else if(playInfo.equals("next")){
            nextMusic();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        if (myMediaPlayer != null) {
            myMediaPlayer.stop();
            myMediaPlayer.release();
            myMediaPlayer = null;
            isrunning = false;
            isPlaying = false;
        }
    }


    @Override
    public void run() {
        while(isrunning){
            try{
                Thread.sleep(100);
            }catch (Exception e) {

            }
            if(myMediaPlayer!=null && isPlaying){  //一定是isPlaying才发广播, 新开的线程
                int currentPosition = myMediaPlayer.getCurrentPosition(); //当前播放毫秒
                int duration = myMediaPlayer.getDuration(); //播放的总毫秒
                Intent intent = new Intent("com.myMediaBroadCast.seekbar");
                intent.putExtra("currentPosition", currentPosition);
                intent.putExtra("duration", duration);
                intent.putExtra("curIndex", curIndex);
                sendBroadcast(intent);
            }
        }
    }

    //接收前台拖放的进度，并调整歌曲的播放进度
    class SeekbarReceiver extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent) {
            int currentPosition = intent.getIntExtra("currentPosition", 0); //当前刻度
            //将播放进度条分成100份， 每份为:per(毫秒) = duration(持续总毫秒)/100,
            //当前播放进度：per*currentPosition = duration(持续总毫秒)*currentPosition/100
            myMediaPlayer.seekTo(myMediaPlayer.getDuration()*currentPosition/100);
            myMediaPlayer.start();
        }
    }
}
