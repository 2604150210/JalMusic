package com.jal.www.jalmusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private MyConnection conn;
    private String TAG = "DetailsActivity";
    private Button btn_pre;
    private Button btn_play;
    private Button btn_next;
    private ImageView btn_return;
    private SeekBar seekBar;
    private MusicButton imageView;
    private TextView tv_title,tv_cur_time,tv_total_time;
    private MusicService.MyBinder musicControl;
    private static final int UPDATE_UI = 0;
    private Animation animation;
    //使用handler定时更新进度条
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_UI:
                    updateUI();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = new Intent(this, MusicService.class);
        Bundle bundle = getIntent().getExtras();
        intent.putExtras(bundle);
        conn = new MyConnection();
        startService(intent);
        bindService(intent, conn, BIND_AUTO_CREATE);
        bindViews();
        //Mixed mode binding service
    }

    private void bindViews() {
        btn_pre = findViewById(R.id.btn_pre);
        btn_play = findViewById(R.id.btn_play);
        btn_next = findViewById(R.id.btn_next);
        btn_return = findViewById(R.id.btn_return);
        seekBar =  findViewById(R.id.sb);
        tv_title = findViewById(R.id.tv_title);
        tv_cur_time =findViewById(R.id.tv_cur_time);
        tv_total_time = findViewById(R.id.tv_total_time);
        imageView = findViewById(R.id.imageview);
        btn_pre.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        imageView.setOnClickListener(this);
        btn_return.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Progress bar change
                if (fromUser) {
                    musicControl.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Start touching the progress bar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Stop touching the progress bar
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:
            case R.id.imageview:
                play(v);
                break;
            case R.id.btn_next:
                next(v);
                break;
            case R.id.btn_pre:
                pre(v);
                break;
            case R.id.btn_return:
                finish();
                break;
        }
    }

    private class MyConnection implements ServiceConnection {

        //This method will be entered after the service is started.
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "::MyConnection::onServiceConnected");
            //Get MyBinder in service
            musicControl = (MusicService.MyBinder) service;
            //Update button text
            updatePlayText();
            updateUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "::MyConnection::onServiceDisconnected");

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Start the update UI bar after entering the interface
        if (musicControl != null) {
            handler.sendEmptyMessage(UPDATE_UI);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Unbind from the service after exiting
        unbindService(conn);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Stop the progress of the update progress bar
        handler.removeCallbacksAndMessages(null);
    }

    //Update progress bar
    private void updateProgress() {
        int currenPostion = musicControl.getCurrenPostion();
        seekBar.setProgress(currenPostion);
    }


    //Update button text
    public void updatePlayText() {
        if (musicControl.isPlaying()) {
            imageView.play();

            btn_play.setText("暂停");
        } else {
            imageView.pause();

            btn_play.setText("播放");
        }
    }

    //Call the play() method in MyBinder
    public void play(View view) {
        musicControl.play();
        updatePlayText();
    }

    //next music
    public void next(View view) {
        musicControl.next(1);
        updatePlayText();
    }

    //previous music
    public void pre(View view) {
        musicControl.next(-1);
        updatePlayText();
    }

    public void updateUI(){

        //Set the maximum value of the progress bar
        int cur_time = musicControl.getCurrenPostion(), total_time = musicControl.getDuration();
        seekBar.setMax(total_time);
        //Set the progress of the progress bar
        seekBar.setProgress(cur_time);

        String str = musicControl.getName();
        tv_title.setText(str);
        tv_cur_time.setText(timeToString(cur_time));
        tv_total_time.setText(timeToString(total_time));

        updateProgress();

        //Update the UI bar every 500 milliseconds using Handler
        handler.sendEmptyMessageDelayed(UPDATE_UI, 500);
    }

    private String timeToString(int time) {
        time /= 1000;
        return String.format("%02d:%02d",time/60,time%60);
    }
}
