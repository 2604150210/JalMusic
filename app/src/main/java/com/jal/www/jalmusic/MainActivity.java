package com.jal.www.jalmusic;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private LinearLayout cur_music;
    private TextView tv_main_title;
    private ArrayList<Music> listMusic;
    private String TAG = "MainActivityLog";
    private MyReceiver myReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myReceiver = new MyReceiver(new Handler());
        IntentFilter itFilter = new IntentFilter();
        itFilter.addAction(MusicService.MAIN_UPDATE_UI);
        registerReceiver(myReceiver, itFilter);
        requestPermission();
     }
    private void initView() {
        listView = this.findViewById(R.id.listView1);
        listMusic = MusicList.getMusicData(getApplicationContext());
        Log.i(TAG, "listMusic.size()=="+listMusic.size());
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
        tv_main_title = findViewById(R.id.tv_main_title);
        if(MusicService.mlastPlayer != null){
            tv_main_title.setText(listMusic.get(MusicService.mPosition).getName());
        }
    }

    private class MyReceiver extends BroadcastReceiver {
        private final Handler handler;
        // Handler used to execute code on the UI thread
        public MyReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            // Post the UI updating code to our Handler
            handler.post(new Runnable() {
                @Override
                public void run() {
                    initView();
                }
            });
        }
    }
    @Override
    protected void onResume() {
        initView();
        if (MusicService.mlastPlayer != null){
            cur_music.setVisibility(View.VISIBLE);
            tv_main_title = findViewById(R.id.tv_main_title);
            tv_main_title.setText(listMusic.get(MusicService.mPosition).getName());
            cur_music.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    int position = MusicService.mPosition;
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0){
                    for (int i = 0; i < grantResults.length; i++) {

                        int grantResult = grantResults[i];
                        if (grantResult == PackageManager.PERMISSION_DENIED){
                            String s = permissions[i];
                            Toast.makeText(this,s+"权限被拒绝了",Toast.LENGTH_SHORT).show();
                        }else{
                            initView();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }
    private void requestPermission(){

        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionList.isEmpty()){
            ActivityCompat.requestPermissions(this,permissionList.toArray(new String[permissionList.size()]),1);
        }else {
            initView();
        }
    }
}
