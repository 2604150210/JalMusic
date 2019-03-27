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

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private MyConnection conn;
    private Button btn_pre;
    private Button btn_play;
    private Button btn_next;
    private SeekBar seekBar;
    private MusicService.MyBinder musicControl;
    private static final int UPDATE_PROGRESS = 0;
    private AudioManager audioManager;
    //使用handler定时更新进度条
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_PROGRESS:
                    updateProgress();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        audioManager =(AudioManager)getSystemService(Context.AUDIO_SERVICE);

        Intent intent3 = new Intent(this, MusicService.class);
        Bundle bundle = getIntent().getExtras();
        intent3.putExtras(bundle);
        conn = new MyConnection();//使用混合的方法开启服务，
        startService(intent3);
        bindService(intent3, conn, BIND_AUTO_CREATE);
        ImageView imageView = (ImageView) findViewById(R.id.imageview);//动画
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.img_animation);
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
            updatePlayText();
            //设置进度条的最大值
            seekBar.setMax(musicControl.getDuration());
            //设置进度条的进度
            seekBar.setProgress(musicControl.getCurrenPostion());
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
            handler.sendEmptyMessage(UPDATE_PROGRESS);
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
        //使用Handler每500毫秒更新一次进度条
        handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 500);
    }


    //更新按钮的文字
    public void updatePlayText() {
        if (musicControl.isPlaying()) {
            btn_play.setText("暂停");
            handler.sendEmptyMessage(UPDATE_PROGRESS);
        } else {
            btn_play.setText("播放");
        }
    }

    //调用MyBinder中的play()方法
    public void play(View view) {
        musicControl.play();
        updatePlayText();
    }
}
