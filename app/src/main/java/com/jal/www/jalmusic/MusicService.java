package com.jal.www.jalmusic;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

import static android.content.Intent.getIntent;

public class MusicService extends Service {
    private String path = "";
    private String TAG = "MusicService";
    private MediaPlayer player;
    private static MediaPlayer lastPlayer;
    private static Music lastMusic;
    private Music music;
    @Override
    public IBinder onBind(Intent intent) {

        //当执行完了onCreate后，就会执行onBind把操作歌曲的方法返回
        return new MyBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        music = (Music) bundle.getSerializable("music");
        path = music.getUrl();
        System.out.println(path);
        if (lastPlayer == null || lastMusic == null){
            player = new MediaPlayer();//这里只执行一次，用于准备播放器
            lastPlayer = player;
            lastMusic = music;
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                System.out.println(path);
                player.setDataSource(path);
                //准备资源
                player.prepare();
                player.start();
            } catch (IOException e) {
                Log.i(TAG,"ERROR");
                e.printStackTrace();
            }
            Log.i(TAG, "准备播放音乐");
        }else if(lastMusic.equals(music)){
            player = lastPlayer;
        }else{
            lastPlayer.stop();
            lastPlayer.release();
            player = new MediaPlayer();//这里只执行一次，用于准备播放器
            lastPlayer = player;
            lastMusic = music;
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                System.out.println(path);
                player.setDataSource(path);
                //准备资源
                player.prepare();
                player.start();
            } catch (IOException e) {
                Log.i(TAG,"ERROR");
                e.printStackTrace();
            }
            Log.i(TAG, "准备播放音乐");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    //该方法包含关于歌曲的操作
    public class MyBinder extends Binder {

        //判断是否处于播放状态
        public boolean isPlaying(){
            return player.isPlaying();
        }

        //播放或暂停歌曲
        public void play() {
            if (player.isPlaying()) {
                player.pause();
                Log.i(TAG, "播放停止");
            } else {
                player.start();
                Log.i(TAG, "播放开始");
            }
        }

        //返回歌曲的长度，单位为毫秒
        public int getDuration(){
            Log.i(TAG, "歌曲长度"+player.getDuration());
            return player.getDuration();
        }

        //返回歌曲目前的进度，单位为毫秒
        public int getCurrenPostion(){
            return player.getCurrentPosition();
        }

        //设置歌曲播放的进度，单位为毫秒
        public void seekTo(int mesc){
            player.seekTo(mesc);
        }
    }
}