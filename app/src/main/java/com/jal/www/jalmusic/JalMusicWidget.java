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

/**
 * Implementation of App Widget functionality.
 */
public class JalMusicWidget extends AppWidgetProvider {
    private boolean mStop = true;
    private String TAG = "JalMusicWidgetLog";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        pushUpdate(context,appWidgetManager);
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

        }
        super.onReceive(context, intent);
    }
    private void pushUpdate(Context context,AppWidgetManager appWidgetManager) {
        RemoteViews remoteView = new RemoteViews(context.getPackageName(),R.layout.jal_music_widget);
        remoteView.setOnClickPendingIntent(R.id.play_pause,getPendingIntent(context, R.id.play_pause));
        remoteView.setOnClickPendingIntent(R.id.prev_song, getPendingIntent(context, R.id.prev_song));
        remoteView.setOnClickPendingIntent(R.id.next_song, getPendingIntent(context, R.id.next_song));
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

