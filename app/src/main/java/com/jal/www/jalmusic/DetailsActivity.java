package com.jal.www.jalmusic;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class DetailsActivity extends AppCompatActivity  implements View.OnClickListener{
    private Context mContext = null;
    private Music music = null;
    private Button btn_pre;
    private Button btn_play;
    private Button btn_stop;
    private Button btn_next;
    private static MediaPlayer mediaPlayer = null;
    private static boolean isRelease = true;   //判断是否MediaPlayer是否释放的标志

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ImageView imageView = (ImageView) findViewById(R.id.imageview);
        //动画
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.img_animation);
        LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
        animation.setInterpolator(lin);
        imageView.startAnimation(animation);

        Bundle bundle = getIntent().getExtras();
        music = (Music) bundle.getSerializable("music");
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(music.getUrl());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bindViews();
    }

    private void bindViews() {
        btn_pre = (Button) findViewById(R.id.btn_pre);
        btn_play = (Button) findViewById(R.id.btn_play);
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_pre.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_play:
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }
                mediaPlayer.start();
                btn_play.setEnabled(false);
                btn_stop.setEnabled(true);
                break;
            case R.id.btn_stop:
                mediaPlayer.pause();
                btn_play.setEnabled(true);
                btn_stop.setEnabled(false);
                break;
            case R.id.btn_pre:
                Toast.makeText(mContext, "开发中~", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_next:
                Toast.makeText(mContext, "开发中~", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
