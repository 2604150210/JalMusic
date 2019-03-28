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

        //When onCreate() is executed, onBind() will be executed to return the method of operating the music.
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
        player = new MediaPlayer();//This is only done once, used to prepare the player.
        if (lastPlayer!=null){
            lastPlayer.stop();
            lastPlayer.release();
        }
        lastPlayer = player;
        lastMusic = music;
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            Log.i(TAG,path);
            player.setDataSource(path); //Prepare resources
            player.prepare();
            player.start();
            Log.i(TAG, "Ready to play music");
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

    //This method contains operations on music
    public class MyBinder extends Binder {

        public boolean isPlaying(){
            return player.isPlaying();
        }

        public void play() {
            if (player.isPlaying()) {
                player.pause();
                Log.i(TAG, "Play stop");
            } else {
                player.start();
                Log.i(TAG, "Play start");
            }
        }

        //Play the next music
        public void next(int type){
            position+=type;
            position = (position + listMusic.size())%listMusic.size();
            music = listMusic.get(position);
            prepare();
        }

        //Returns the length of the music in milliseconds
        public int getDuration(){
            return player.getDuration();
        }

        //Return the name of the music
        public String getName(){
            return music.getName();
        }

        //Returns the current progress of the music in milliseconds
        public int getCurrenPostion(){
            return player.getCurrentPosition();
        }

        //Set the progress of music playback in milliseconds
        public void seekTo(int mesc){
            player.seekTo(mesc);
        }
    }
}