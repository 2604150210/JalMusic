package com.jal.www.jalmusic;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class JalMusicWidget extends AppWidgetProvider {
    private boolean mStop = true;
    private String TAG = "JalMusicWidgetLog";
    private ArrayList<Music> listMusic;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        if (listMusic == null)
            listMusic = MusicList.getMusicData(context);
        pushUpdate(context, AppWidgetManager.getInstance(context), listMusic.get(0).getName(),true);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
    private void pushAction(Context context, int ACTION) {
        Intent actionIntent = new Intent(MusicService.ACTION);
        actionIntent.putExtra(MusicService.KEY_USR_ACTION, ACTION);
        context.sendBroadcast(actionIntent);
    }
    public void onReceive(Context context, Intent intent) {
        if (listMusic == null)
             listMusic = MusicList.getMusicData(context);
        String action = intent.getAction();
        if (intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)) {
            Uri data = intent.getData();
            int buttonId = Integer.parseInt(data.getSchemeSpecificPart());
            switch (buttonId) {
                case R.id.play_pause:
                    pushAction(context,MusicService.ACTION_PLAY_PAUSE);
                    if(MusicService.mlastPlayer == null){
                        Intent startIntent = new Intent(context,MusicService.class);
                        Bundle bundle1 = new Bundle();
                        bundle1.putInt("position",0);
                        startIntent.putExtras(bundle1);
                        context.startService(startIntent);
                    }
                    break;
                case R.id.prev_song:
                    pushAction(context, MusicService.ACTION_PRE);
                    break;
                case R.id.next_song:
                    pushAction(context, MusicService.ACTION_NEXT);
                    break;
            }

        }else if (MusicService.MAIN_UPDATE_UI.equals(action)){
            int play_pause =  intent.getIntExtra(MusicService.KEY_MAIN_ACTIVITY_UI_BTN, -1);
            int songid = intent.getIntExtra(MusicService.KEY_MAIN_ACTIVITY_UI_TEXT, -1);
            switch (play_pause) {
                case MusicService.VAL_UPDATE_UI_PLAY:
                    pushUpdate(context, AppWidgetManager.getInstance(context), listMusic.get(songid).getName(),true);
                    break;
                case MusicService.VAL_UPDATE_UI_PAUSE:
                    pushUpdate(context, AppWidgetManager.getInstance(context), listMusic.get(songid).getName(),false);
                    break;
                default:
                    break;
            }

        }
        super.onReceive(context, intent);
    }
    private void pushUpdate(Context context,AppWidgetManager appWidgetManager,String songName,Boolean play_pause) {
        RemoteViews remoteView = new RemoteViews(context.getPackageName(),R.layout.jal_music_widget);
        remoteView.setOnClickPendingIntent(R.id.play_pause,getPendingIntent(context, R.id.play_pause));
        remoteView.setOnClickPendingIntent(R.id.prev_song, getPendingIntent(context, R.id.prev_song));
        remoteView.setOnClickPendingIntent(R.id.next_song, getPendingIntent(context, R.id.next_song));
        //设置内容
        if (!songName.equals("")) {
            remoteView.setTextViewText(R.id.widget_title, songName);
        }
        //设定按钮图片
        if (play_pause) {
            String s = context.getResources().getString(R.string.pause);
            remoteView.setTextViewText(R.id.play_pause, s);
//            remoteView.setImageViewResource(R.id.play_pause, R.drawable.car_musiccard_pause);
        }else {
            String s = context.getResources().getString(R.string.play);
            remoteView.setTextViewText(R.id.play_pause, s);
//            remoteView.setImageViewResource(R.id.play_pause, R.drawable.car_musiccard_play);
        }
        ComponentName componentName = new ComponentName(context,JalMusicWidget.class);
        appWidgetManager.updateAppWidget(componentName, remoteView);
    }
    private PendingIntent getPendingIntent(Context context, int buttonId) {
        Intent intent = new Intent();
        intent.setClass(context, JalMusicWidget.class);
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        intent.setData(Uri.parse(""+buttonId));
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        return pi;
    }
}

