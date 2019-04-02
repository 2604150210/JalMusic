package com.jal.www.jalmusic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
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
    private RemoteViews remoteView;
    private Notification notification;
    public static String ACTION = "to_service";
    public static String KEY_USR_ACTION = "key_usr_action";
    public static final int ACTION_PRE = 0, ACTION_PLAY_PAUSE = 1, ACTION_NEXT = 2;
    public static String MAIN_UPDATE_UI = "main_activity_update_ui";  //Action
    public static String KEY_MAIN_ACTIVITY_UI_BTN = "main_activity_ui_btn_key"; //putExtra中传送当前播放状态的key
    public static String KEY_MAIN_ACTIVITY_UI_TEXT = "main_activity_ui_text_key"; //putextra中传送TextView的key
    public static final int  VAL_UPDATE_UI_PLAY = 1,VAL_UPDATE_UI_PAUSE =2;//当前歌曲的播放状态


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
        initNotificationBar();
        Bundle bundle = intent.getExtras();
        position = bundle.getInt("position");
        if (mlastPlayer == null || mPosition != position){
            prepare();
        }else{
            player = mlastPlayer;
        }
        return super.onStartCommand(intent, flags, startId);
    }
    private void postState(Context context, int state,int songid) {
        Intent actionIntent = new Intent(MusicService.MAIN_UPDATE_UI);
        actionIntent.putExtra(MusicService.KEY_MAIN_ACTIVITY_UI_BTN,state);
        actionIntent.putExtra(MusicService.KEY_MAIN_ACTIVITY_UI_TEXT, songid);
        updateNotification();
        context.sendBroadcast(actionIntent);
    }
    private void initNotificationBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // 通知渠道的id 这个地方只要一直即可
            String id = "111111";
            // 用户可以看到的通知渠道的名字.
            CharSequence name = "notification channel";
            // 用户可以看到的通知渠道的描述
            String description = "notification description";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // 配置通知渠道的属性
            mChannel.setDescription(description);
            mChannel.setLightColor(Color.RED);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            //最后在notificationmanager中创建该通知渠道
            mNotificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder  mBuilder = new NotificationCompat.Builder(this, "111111");
        mBuilder.setContentIntent(PendingIntent.getActivities(this,0,new Intent[]{new Intent(this,DetailsActivity.class)},0))
                .setWhen(System.currentTimeMillis())
                .setOngoing(false)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.zjalmusic).
                setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.jalmusic));
        notification = mBuilder.build();

        remoteView = new RemoteViews(getPackageName(),R.layout.notification);
        remoteView.setOnClickPendingIntent(R.id.play_pause,getPendingIntent(this, R.id.play_pause));
        remoteView.setOnClickPendingIntent(R.id.prev_song, getPendingIntent(this, R.id.prev_song));
        remoteView.setOnClickPendingIntent(R.id.next_song, getPendingIntent(this, R.id.next_song));
        updateNotification();



    }

    private void updateNotification() {
        Log.i(TAG, "updateNotification");
        remoteView.setTextViewText(R.id.notification_title, listMusic.get(MusicService.mPosition).getName());
        if (MusicService.mlastPlayer != null && MusicService.mlastPlayer.isPlaying()) {
            String s = getResources().getString(R.string.pause);
            remoteView.setTextViewText(R.id.play_pause, s);
        }else {
            String s = getResources().getString(R.string.play);
            remoteView.setTextViewText(R.id.play_pause, s);
        }
        notification.contentView = remoteView;
        NotificationManager manager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        manager.notify(1,notification);
    }

    private PendingIntent getPendingIntent(Context context, int buttonId) {
        Intent intent = new Intent();
        intent.setClass(context, JalMusicWidget.class);
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        intent.setData(Uri.parse(""+buttonId));
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
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
        postState(getApplicationContext(), VAL_UPDATE_UI_PLAY,position);
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
            postState(getApplicationContext(), VAL_UPDATE_UI_PAUSE,position);
            Log.i(TAG, "Play stop");
        } else {
            player.start();
            postState(getApplicationContext(), VAL_UPDATE_UI_PLAY,position);
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