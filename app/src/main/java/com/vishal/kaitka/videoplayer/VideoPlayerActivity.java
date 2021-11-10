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
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.vishal.kaitka.videoplayer.adapters.PlaybackIconsAdapter;
import com.vishal.kaitka.videoplayer.databinding.ActivityVideoPlayerBinding;
import com.vishal.kaitka.videoplayer.databinding.CustomPlaybackViewBinding;
import com.vishal.kaitka.videoplayer.models.IconModel;
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

    //horizontal RecyclerView vars
    private ArrayList<IconModel> iconModelArrayList = new ArrayList<>();
    PlaybackIconsAdapter playbackIconsAdapter;
    boolean expand = false;
    boolean dark = false;
    boolean mute = false;
    //horizontal RecyclerView vars


    @SuppressLint({"NotifyDataSetChanged", "Range"})
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

        iconModelArrayList.add(new IconModel(R.drawable.ic_right, ""));
        iconModelArrayList.add(new IconModel(R.drawable.ic_night_mode, "Night"));
        iconModelArrayList.add(new IconModel(R.drawable.ic_volume_off, "Mute"));
        iconModelArrayList.add(new IconModel(R.drawable.ic_rotate, "Rotate"));
        playbackIconsAdapter = new PlaybackIconsAdapter(iconModelArrayList, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, true);
        customViews.recyclerViewIcon.setLayoutManager(layoutManager);
        customViews.recyclerViewIcon.setAdapter(playbackIconsAdapter);
        playbackIconsAdapter.notifyDataSetChanged();

        playbackIconsAdapter.setOnItemClickListener(position -> {
            if (position == 0) {
                Toast.makeText(VideoPlayerActivity.this, "First", Toast.LENGTH_SHORT).show();
                if (expand) {
                    iconModelArrayList.clear();
                    iconModelArrayList.add(new IconModel(R.drawable.ic_right, ""));
                    iconModelArrayList.add(new IconModel(R.drawable.ic_night_mode, "Night"));
                    iconModelArrayList.add(new IconModel(R.drawable.ic_volume_off, "Mute"));
                    iconModelArrayList.add(new IconModel(R.drawable.ic_rotate, "Rotate"));
                    expand = false;
                } else {
                    if (iconModelArrayList.size() == 4) {
                        iconModelArrayList.add(new IconModel(R.drawable.ic_volume_up, "Volume"));
                        iconModelArrayList.add(new IconModel(R.drawable.ic_brigthness, "Brightness"));
                        iconModelArrayList.add(new IconModel(R.drawable.ic_equalizer, "Equalizer"));
                        iconModelArrayList.add(new IconModel(R.drawable.ic_speed, "Speed"));
                        iconModelArrayList.add(new IconModel(R.drawable.ic_subtitle, "Subtitle"));
                    }
                    iconModelArrayList.set(position, new IconModel(R.drawable.ic_left, ""));
                    expand = true;
                }
                playbackIconsAdapter.notifyDataSetChanged();
            }
            if (position == 1) {
                Toast.makeText(VideoPlayerActivity.this, "Second", Toast.LENGTH_SHORT).show();
                if (dark) {
                    binding.nightMode.setVisibility(View.GONE);
                    iconModelArrayList.set(position, new IconModel(R.drawable.ic_night_mode, "Night"));
                    dark = false;
                } else {
                    binding.nightMode.setVisibility(View.VISIBLE);
                    iconModelArrayList.set(position, new IconModel(R.drawable.ic_night_mode, "Day"));
                    dark = true;
                }
                playbackIconsAdapter.notifyDataSetChanged();
            }
            if (position == 2) {
                Toast.makeText(VideoPlayerActivity.this, "Third", Toast.LENGTH_SHORT).show();
                if (mute) {
                    player.setVolume(100);
                    iconModelArrayList.set(position, new IconModel(R.drawable.ic_volume_up, "Mute"));
                    mute = false;
                } else {
                    player.setVolume(0);
                    iconModelArrayList.set(position, new IconModel(R.drawable.ic_volume_off, "UnMute"));
                    mute = true;
                }
                playbackIconsAdapter.notifyDataSetChanged();
            }
            if (position == 3) {
                Toast.makeText(VideoPlayerActivity.this, "fourth", Toast.LENGTH_SHORT).show();
            }
            if (position == 4) {
                Toast.makeText(VideoPlayerActivity.this, "fifth", Toast.LENGTH_SHORT).show();
            }
        });

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