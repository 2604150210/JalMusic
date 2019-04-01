package com.jal.www.jalmusic;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
    static MediaPlayer mlastPlayer;
    static int mPosition;
    private int position;
    private String path = "";
    private String TAG = "MusicServiceLog";
    private MediaPlayer player;
    private Music music;
    private ArrayList<Music>listMusic;
    private Context context;

    public static String ACTION = "to_service";
    public static String KEY_USR_ACTION = "key_usr_action";
    public static final int ACTION_PRE = 0, ACTION_PLAY_PAUSE = 1, ACTION_NEXT = 2;
    @Override
    public IBinder onBind(Intent intent) {

        //When onCreate() is executed, onBind() will be executed to return the method of operating the music.
        return new MyBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        listMusic = MusicList.getMusicData(context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        position = bundle.getInt("position");
        if (mlastPlayer == null || mPosition != position){
            prepare();
        }else{
            player = mlastPlayer;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    void prepare(){
        music = listMusic.get(position);
        path = music.getUrl();
        Log.i(TAG,"path:"+path);
        player = new MediaPlayer();//This is only done once, used to prepare the player.
        if (mlastPlayer !=null){
            mlastPlayer.stop();
            mlastPlayer.release();
        }
        mlastPlayer = player;
        mPosition = position;
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
                position +=1;
                position = (position + listMusic.size())%listMusic.size();
                music = listMusic.get(position);
                Toast.makeText(context, "自动为您切换下一首:"+music.getName(), Toast.LENGTH_SHORT).show();
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
            mPosition +=type;
            mPosition = (mPosition + listMusic.size())%listMusic.size();
            music = listMusic.get(mPosition);
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

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action  = intent.getAction();
            if (ACTION.equals(action)) {
                int widget_action = intent.getIntExtra(KEY_USR_ACTION, -1);

                switch (widget_action) {
                    case ACTION_PRE:
                        next(-1);
                        Log.d(TAG,"action_prev");
                        break;
                    case ACTION_PLAY_PAUSE:
                        play();
                        break;
                    case ACTION_NEXT:
                        next(1);
                        Log.d(TAG,"action_next");
                        break;
                    default:
                        break;
                }
            }
        }
    };

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
        position +=type;
        position = (position + listMusic.size())%listMusic.size();
        music = listMusic.get(position);
        prepare();
    }


}