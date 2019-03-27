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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DateFormat;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private MyConnection conn;
    private Button btn_pre;
    private Button btn_play;
    private Button btn_next;
    private SeekBar seekBar;
    private ImageView imageView;
    private TextView tv_title,tv_cur_time,tv_total_time;
    private MusicService.MyBinder musicControl;
    private static final int UPDATE_UI = 0;
    private AudioManager audioManager;
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
        audioManager =(AudioManager)getSystemService(Context.AUDIO_SERVICE);

        Intent intent = new Intent(this, MusicService.class);
        Bundle bundle = getIntent().getExtras();
        intent.putExtras(bundle);
        conn = new MyConnection();//使用混合的方法开启服务，
        startService(intent);
        bindService(intent, conn, BIND_AUTO_CREATE);
        imageView = (ImageView) findViewById(R.id.imageview);//动画
        animation = AnimationUtils.loadAnimation(this, R.anim.img_animation);
        LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
        animation.setInterpolator(lin);
        imageView.startAnimation(animation);

        bindViews();
    }

    private void bindViews() {
        btn_pre = (Button) findViewById(R.id.btn_pre);
        btn_play = (Button) findViewById(R.id.btn_play);
        btn_next = (Button) findViewById(R.id.btn_next);
        seekBar = (SeekBar) findViewById(R.id.sb);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_cur_time = (TextView)findViewById(R.id.tv_cur_time);
        tv_total_time = (TextView)findViewById(R.id.tv_total_time);
        btn_pre.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_next.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //进度条改变
                if (fromUser) {
                    musicControl.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //开始触摸进度条
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //停止触摸进度条
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:
                play(v);
                break;
            case R.id.btn_next:
                next(v);
                break;
            case R.id.btn_pre:
                pre(v);
                break;
        }
    }
    private String TAG = "DetailsActivity";

    private class MyConnection implements ServiceConnection {

        //服务启动完成后会进入到这个方法
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "::MyConnection::onServiceConnected");
            //获得service中的MyBinder
            musicControl = (MusicService.MyBinder) service;
            //更新按钮的文字
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
        //进入到界面后开始更新进度条
        if (musicControl != null) {
            handler.sendEmptyMessage(UPDATE_UI);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出应用后与service解除绑定
        unbindService(conn);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //停止更新进度条的进度
        handler.removeCallbacksAndMessages(null);
    }

    //更新进度条
    private void updateProgress() {
        int currenPostion = musicControl.getCurrenPostion();
        seekBar.setProgress(currenPostion);
    }


    //更新按钮的文字
    public void updatePlayText() {
        if (musicControl.isPlaying()) {
            imageView.startAnimation(animation);
            btn_play.setText("暂停");
        } else {
            imageView.clearAnimation();
            btn_play.setText("播放");
        }
    }

    //调用MyBinder中的play()方法
    public void play(View view) {
        musicControl.play();
        updatePlayText();
    }

    //调用MyBinder中的next()方法
    public void next(View view) {
        musicControl.next(1);
        updatePlayText();
    }

    //调用MyBinder中的next()方法
    public void pre(View view) {
        musicControl.next(-1);
        updatePlayText();
    }

    public void updateUI(){

        //设置进度条的最大值
        int cur_time = musicControl.getCurrenPostion(), total_time = musicControl.getDuration();
        seekBar.setMax(total_time);
        //设置进度条的进度
        seekBar.setProgress(cur_time);

        tv_title.setText(musicControl.getTitle());

        tv_cur_time.setText(timeToString(cur_time));
        tv_total_time.setText(timeToString(total_time));

        updateProgress();

        //使用Handler每500毫秒更新一次进度条
        handler.sendEmptyMessageDelayed(UPDATE_UI, 500);
    }

    private String timeToString(int time) {
        time /= 1000;
        return String.format("%02d:%02d",time/60,time%60);
    }
}
