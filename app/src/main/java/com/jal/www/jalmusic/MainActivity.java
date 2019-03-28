package com.jal.www.jalmusic;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private LinearLayout cur_music;
    private Context mContext;
    private TextView tv_main_title;
    private ArrayList<Music> listMusic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        listView = (ListView) this.findViewById(R.id.listView1);
        listMusic = MusicList.getMusicData(getApplicationContext());
        MusicAdapter adapter = new MusicAdapter(this, listMusic);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("listMusic",listMusic);
                bundle.putInt("position", position);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(MainActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });
        cur_music = findViewById(R.id.cur_music);
        if (MusicService.lastPlayer != null && MusicService.lastMusic != null){
            cur_music.setVisibility(View.VISIBLE);
            tv_main_title = findViewById(R.id.tv_main_title);
            tv_main_title.setText(MusicService.lastMusic.getName());
            cur_music.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("listMusic",listMusic);
                    int position = listMusic.indexOf(listMusic);
                    bundle.putInt("position", position);
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(MainActivity.this, DetailsActivity.class);
                    startActivity(intent);
                }
            });
        }else{
            cur_music.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        if (MusicService.lastPlayer != null && MusicService.lastMusic != null){
            cur_music.setVisibility(View.VISIBLE);
            tv_main_title = findViewById(R.id.tv_main_title);
            tv_main_title.setText(MusicService.lastMusic.getName());
            cur_music.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("listMusic",listMusic);
                    int position = listMusic.indexOf(MusicService.lastMusic);
                    System.out.println("MainActivity:position="+position);
                    bundle.putInt("position", position);
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(MainActivity.this, DetailsActivity.class);
                    startActivity(intent);
                }
            });
        }else{
            cur_music.setVisibility(View.GONE);
        }
        super.onResume();
    }
}
