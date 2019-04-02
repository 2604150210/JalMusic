package com.jal.www.jalmusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import java.util.ArrayList;

public class NotificationReceiver extends BroadcastReceiver {

    private ArrayList<Music> listMusic;

    @Override
    public void onReceive(Context context, Intent intent) {
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
            RemoteViews remoteView = new RemoteViews(context.getPackageName(),R.layout.notification);
            remoteView.setTextViewText(R.id.notification_title, listMusic.get(songid).getName());
            String s;
            switch (play_pause) {
                case MusicService.VAL_UPDATE_UI_PLAY:
                    s = context.getResources().getString(R.string.pause);
                    remoteView.setTextViewText(R.id.play_pause, s);
                    break;
                case MusicService.VAL_UPDATE_UI_PAUSE:
                    s = context.getResources().getString(R.string.play);
                    remoteView.setTextViewText(R.id.play_pause, s);
                    break;
                default:
                    break;
            }

        }
    }
    private void pushAction(Context context, int ACTION) {
        Intent actionIntent = new Intent(MusicService.ACTION);
        actionIntent.putExtra(MusicService.KEY_USR_ACTION, ACTION);
        context.sendBroadcast(actionIntent);
    }

}
