package com.jal.www.jalmusic;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

public class MusicButton extends AppCompatImageView {
    private ObjectAnimator objectAnimator;

    public static final int STATE_PLAYING =1;
    public static final int STATE_PAUSE =2;
    public static final int STATE_STOP =3;
    public static int state;

    public MusicButton(Context context) {
        super(context);
        init();
    }

    public MusicButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MusicButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        state = STATE_STOP;
        //Add a rotation animation, the rotation center defaults to the midpoint of the control
        objectAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f);
        objectAnimator.setDuration(3000);//Set animation time
        objectAnimator.setInterpolator(new LinearInterpolator());//Animation time linear gradient
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
    }

    public void playMusic(){
        if(state == STATE_STOP){
            objectAnimator.start();
            state = STATE_PLAYING;
        }else if(state == STATE_PAUSE){
            objectAnimator.resume();
            state = STATE_PLAYING;
        }else if(state == STATE_PLAYING){
            objectAnimator.pause();
            state = STATE_PAUSE;
        }
    }

    public void play(){
        if(objectAnimator.isPaused()){
            System.out.println("isPaused");
            objectAnimator.resume();
        }else {
            System.out.println("else");
            objectAnimator.start();
        }
    }
    public void pause(){
        objectAnimator.pause();
    }

    public void stopMusic(){
        objectAnimator.end();
        state = STATE_STOP;
    }
}