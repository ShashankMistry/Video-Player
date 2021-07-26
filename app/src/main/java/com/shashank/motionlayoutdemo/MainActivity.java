package com.shashank.motionlayoutdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnClickListener {
    PlayerView videoView;
    RecyclerView list;
    File storage;
    TextView title;
    TextView des;
    RecyclerViewAdapter adapter;
    SimpleExoPlayer player;
    String[] allPath;
    Button play, stop;
    ArrayList<File> videos = new ArrayList<>();
    MotionLayout motionLayout;

    long pos;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        askPermission();
        videoView = findViewById(R.id.video);
        des = findViewById(R.id.des);
        title = findViewById(R.id.text);
        play = findViewById(R.id.play);
        stop = findViewById(R.id.close);
        motionLayout = findViewById(R.id.motion);
        player = new SimpleExoPlayer.Builder(this).build();
        videoView.setPlayer(player);
        motionLayout.setTransition(R.id.start, R.id.end2);
        motionLayout.transitionToEnd();

        videoView.setOnClickListener(v -> videoView.setUseController(!videoView.isControllerVisible()));



        play.setOnClickListener(v -> {
            if (player.isPlaying()) {
                player.pause();
                pos = player.getCurrentPosition();
                play.setForeground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_play));
            } else {
                player.seekTo(pos);
                player.play();
                play.setForeground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_pause));
            }
        });

        if (motionLayout.getCurrentState() == motionLayout.getStartState()) {
            play.setEnabled(false);
            stop.setEnabled(false);
        }
        motionLayout.addTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int i, int i1) {
                if (player.isPlaying()) {
                    play.setForeground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_pause));
                } else {
                    play.setForeground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_play));
                }
            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v) {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int i) {
                if (motionLayout.getCurrentState() == R.id.start) {
                    videoView.setUseController(true);
                    play.setEnabled(false);
                    stop.setEnabled(false);
                } else if (motionLayout.getCurrentState() == R.id.end) {
                    videoView.setUseController(false);
                    play.setEnabled(true);
                    stop.setEnabled(true);
                } else if (motionLayout.getCurrentState() == R.id.end2) {
                    player.stop();
                }

            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean b, float v) {
            }
        });


        stop.setOnClickListener(v -> {
            player.stop();
            motionLayout.setTransition(R.id.end, R.id.end2);
        });

    }

    private void askPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                showList();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(MainActivity.this, "Storage permission required", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    private void showList() {
        new Thread(() -> {
            allPath = StorageUtils.getStorageDirectories(MainActivity.this);

            for (String s : allPath) {
                storage = new File(s);
                Method.load_Directory_Files(storage);
            }
            runOnUiThread(() -> {
                videos.addAll(Constant.allMediaList);
                list = findViewById(R.id.list);
                list.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                list.setHasFixedSize(true);
                list.setItemViewCacheSize(20);
                list.setNestedScrollingEnabled(false);
                adapter = new RecyclerViewAdapter(MainActivity.this, MainActivity.this);
                list.setAdapter(adapter);
            });
        }).start();

    }


    @Override
    protected void onPause() {
        super.onPause();
        pos = player.getCurrentPosition();
        player.pause();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onItemClickListener(int position) {
        motionLayout.setProgress(0.0f);
        motionLayout.setTransition(R.id.end, R.id.start);
        motionLayout.transitionToEnd();
        title.setText(videos.get(position).getName());
        des.setText("Movie title: "+videos.get(position).getName()+"\n\nDescription: Lorem Ipsum is simply dummy text of the printing and typesetting industry.\n" +
                "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and +\n" +
                "scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting,\n" +
                "remaining essentially unchanged. It was popularised in the 1960s with the release of sheets containing Lorem Ipsum passages,\n" +
                "and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum Lorem Ipsum is simply dummy text of the printing and typesetting industry.\n" +
                "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and \n" +
                "scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, \n" +
                "remaining essentially unchanged. It was popularised in the 1960s with the release of sheets containing Lorem Ipsum passages,\n" +
                " and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum");
        player.setMediaItems(Constant.mediaItemList,position,0);
        player.setPlayWhenReady(true);
        player.prepare();
        videoView.showController();
    }

    @Override
    public void onBackPressed() {

        if (motionLayout.getCurrentState() == R.id.start){
            motionLayout.setTransition(R.id.start, R.id.end);
            motionLayout.transitionToEnd();
        } else {
            super.onBackPressed();
        }
    }
}