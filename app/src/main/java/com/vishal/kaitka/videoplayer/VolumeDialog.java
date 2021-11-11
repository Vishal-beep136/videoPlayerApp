package com.vishal.kaitka.videoplayer;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

/**
 * Created by Vishal on 10,November,2021
 */
public class VolumeDialog extends AppCompatDialogFragment {
    private ImageView cross;
    private TextView volumeNo;
    private SeekBar seekBar;
    AudioManager audioManager;

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.vol_dialog_item, null);
        builder.setView(view);
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        cross = view.findViewById(R.id.close);
        volumeNo = view.findViewById(R.id.vol_number);
        seekBar = view.findViewById(R.id.volSeek);

        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        seekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
                int mediaVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                double volPer = Math.ceil((((double) mediaVolume / (double) maxVol) * (double) 100));
                volumeNo.setText("" + volPer);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        cross.setOnClickListener(view1 -> dismiss());
        return builder.create();
    }

}
