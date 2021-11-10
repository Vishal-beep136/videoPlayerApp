package com.vishal.kaitka.videoplayer;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.vishal.kaitka.videoplayer.databinding.ActivityVideoPlayerBinding;
import com.vishal.kaitka.videoplayer.databinding.CustomPlaybackViewBinding;
import com.vishal.kaitka.videoplayer.models.MediaFiles;

import java.io.File;
import java.util.ArrayList;

public class VideoPlayerActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityVideoPlayerBinding binding;
    SimpleExoPlayer player;
    int position;
    String videoTitle;
    ArrayList<MediaFiles> mVideoFiles = new ArrayList<>();
    private ControlsMode controlsMode;

    public enum ControlsMode {
        LOCK, FULLSCREEN;
    }

    CustomPlaybackViewBinding customViews;
    ConcatenatingMediaSource concatenatingMediaSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        binding = ActivityVideoPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        customViews = CustomPlaybackViewBinding.bind(binding.getRoot());
        position = getIntent().getIntExtra("position", 0);
        videoTitle = getIntent().getStringExtra("videoTitle");
        mVideoFiles = getIntent().getExtras().getParcelableArrayList("videoArrayList");
        customViews.videoTitle.setText(videoTitle);

        customViews.exoPrev.setOnClickListener(this);
        customViews.exoNext.setOnClickListener(this);
        customViews.videoBack.setOnClickListener(this);
        customViews.lock.setOnClickListener(this);
        customViews.unlock.setOnClickListener(this);
        customViews.scaling.setOnClickListener(firstListener);


        playVideo();

    }

    private void playVideo() {
        String path = mVideoFiles.get(position).getPath();
        Uri uri = Uri.parse(path);
        player = new SimpleExoPlayer.Builder(this).build();
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                this, Util.getUserAgent(this, "app"));

        concatenatingMediaSource = new ConcatenatingMediaSource();
        for (int i = 0; i < mVideoFiles.size(); i++) {
            new File(String.valueOf(mVideoFiles.get(i)));
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(String.valueOf(uri)));

            concatenatingMediaSource.addMediaSource(mediaSource);
        }
        binding.exoplayerView.setPlayer(player);
        binding.exoplayerView.setKeepScreenOn(true);
        player.prepare(concatenatingMediaSource);
        player.seekTo(position, C.TIME_UNSET);
        playError();


    }

    private void playError() {
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                Toast.makeText(VideoPlayerActivity.this, "Video Playing Error : " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        player.setPlayWhenReady(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (player.isPlaying()) {
            player.stop();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.setPlayWhenReady(false);
        player.getPlaybackState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }

    private void setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.videoBack:
                if (player != null) {
                    player.release();
                }
                finish();
                break;
            case R.id.lock:
                controlsMode = ControlsMode.FULLSCREEN;
                customViews.rootLayout.setVisibility(View.VISIBLE);
                customViews.lock.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Unlocked!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.unlock:
                controlsMode = ControlsMode.LOCK;
                customViews.rootLayout.setVisibility(View.INVISIBLE);
                customViews.lock.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Locked!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.exo_next:
                try {
                    player.stop();
                    position++;
                    playVideo();
                } catch (Exception e) {
                    Toast.makeText(this, "No next video found!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case R.id.exo_prev:
                try {
                    player.stop();
                    position--;
                    playVideo();
                } catch (Exception e) {
                    Toast.makeText(this, "No previous video found!", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    View.OnClickListener firstListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            binding.exoplayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
            customViews.scaling.setImageResource(R.drawable.fullscreen);

            Toast.makeText(VideoPlayerActivity.this, "Full Screen", Toast.LENGTH_SHORT).show();
            customViews.scaling.setOnClickListener(secondListener);
        }
    };

    View.OnClickListener secondListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            binding.exoplayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
            customViews.scaling.setImageResource(R.drawable.zoom);

            Toast.makeText(VideoPlayerActivity.this, "Zoom Screen", Toast.LENGTH_SHORT).show();
            customViews.scaling.setOnClickListener(thirdListener);
        }
    };

    View.OnClickListener thirdListener = view -> {
        binding.exoplayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
        customViews.scaling.setImageResource(R.drawable.fit);
        Toast.makeText(VideoPlayerActivity.this, "Fit Screen", Toast.LENGTH_SHORT).show();
        customViews.scaling.setOnClickListener(firstListener);
    };


}