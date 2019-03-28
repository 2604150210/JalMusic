package com.jal.www.jalmusic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service {
    private String path = "";
    private String TAG = "MusicServiceLog";
    private MediaPlayer player;
    static MediaPlayer lastPlayer;
    static Music lastMusic;
    private Music music;
    private ArrayList<Music>listMusic;
    private Context mContext;
    private int position;
    @Override
    public IBinder onBind(Intent intent) {

        //当执行完了onCreate后，就会执行onBind把操作歌曲的方法返回
        return new MyBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        listMusic =  bundle.getParcelableArrayList("listMusic");
        position = bundle.getInt("position");
        Log.i(TAG,"position:"+position);
        music = listMusic.get(position);
        Log.i(TAG, music.toString());
        if (lastPlayer == null || lastMusic == null || !lastMusic.equals(music)){
            prepare();
        }else{
            player = lastPlayer;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    void prepare(){
        path = music.getUrl();
        Log.i(TAG,"path:"+path);
        player = new MediaPlayer();//这里只执行一次，用于准备播放器
        if (lastPlayer!=null){
            lastPlayer.stop();
            lastPlayer.release();
        }
        lastPlayer = player;
        lastMusic = music;
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            Log.i(TAG,path);
            player.setDataSource(path); //准备资源
            player.prepare();
            player.start();
            Log.i(TAG, "准备播放音乐");
        } catch (IOException e) {
            Log.i(TAG,"ERROR");
            e.printStackTrace();
        }
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                position+=1;
                position = (position + listMusic.size())%listMusic.size();
                music = listMusic.get(position);
                Toast.makeText(mContext, "自动为您切换下一首:"+music.getName(), Toast.LENGTH_SHORT).show();
                prepare();
            }
        });
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

        //播放下一曲
        public void next(int type){
            position+=type;
            position = (position + listMusic.size())%listMusic.size();
            music = listMusic.get(position);
            prepare();
        }

        //返回歌曲的长度，单位为毫秒
        public int getDuration(){
//            Log.i(TAG, "歌曲长度"+player.getDuration());
            return player.getDuration();
        }

        //返回歌曲的标题
        public String getName(){
            return music.getName();
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