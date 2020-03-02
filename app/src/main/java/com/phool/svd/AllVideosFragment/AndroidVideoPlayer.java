package com.phool.svd.AllVideosFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.phool.svd.R;

/**
 * Created by duskysol on 1/5/2019.
 */

public class AndroidVideoPlayer extends AppCompatActivity {
    private static final String VIDEO_SAMPLE = "tacoma_narrows";
    private VideoView mVideoView;
    MediaController mediaController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.android_video_player);
        mVideoView = findViewById(R.id.videoview);
        mediaController = new MediaController(this);
        initializePlayer();
    }

    private void initializePlayer() {
        Bundle bundle = getIntent().getExtras();
        String videoUrli = bundle.getString("videoUri");
        String filename = videoUrli.substring(videoUrli.lastIndexOf('/') + 1);
        //toobar & backpress
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(filename);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                onBackPressed();
            }
        });
        mVideoView.setVideoURI(Uri.parse(videoUrli));
        mVideoView.setMediaController(mediaController);
        mediaController.setEnabled(true);
        mVideoView.start();
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, videofolder.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        releasePlayer();
    }

    private void releasePlayer() {
        mVideoView.stopPlayback();

    }

}
