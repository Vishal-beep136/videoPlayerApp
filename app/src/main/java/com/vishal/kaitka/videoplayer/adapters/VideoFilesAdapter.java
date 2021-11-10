package com.vishal.kaitka.videoplayer.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.vishal.kaitka.videoplayer.R;
import com.vishal.kaitka.videoplayer.VideoPlayerActivity;
import com.vishal.kaitka.videoplayer.databinding.VideoItemBinding;
import com.vishal.kaitka.videoplayer.models.MediaFiles;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Vishal on 09,November,2021
 */
public class VideoFilesAdapter extends RecyclerView.Adapter<VideoFilesAdapter.ViewHolder> {

    private ArrayList<MediaFiles> videoList;
    private Context context;
    BottomSheetDialog bottomSheetDialog;

    public VideoFilesAdapter(ArrayList<MediaFiles> videoList, Context context) {
        this.videoList = videoList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false);
        return new ViewHolder(view);
    }

    //todo: change suppressLint(RecyclerView) int position
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.videoName.setText(videoList.get(position).getDisplayName());
        String size = videoList.get(position).getSize();
        holder.binding.videoSize.setText(android.text.format.Formatter.formatFileSize(context, Long.parseLong(size)));

        double milliSeconds = Double.parseDouble(videoList.get(position).getDuration());
        holder.binding.videoDuration.setText(timeConversation((long) milliSeconds));

        Glide.with(context)
                .load(new File(videoList.get(position).getPath()))
                .into(holder.binding.thumbnail);

        holder.binding.videoMenuMore.setOnClickListener(view -> {
            bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetTheme);
            View bsView = LayoutInflater.from(context).inflate(R.layout.video_bs_layout, view.findViewById(R.id.bottomSheet));
            bsView.findViewById(R.id.bsPlay).setOnClickListener(view1 -> {
                holder.itemView.performClick();
                bottomSheetDialog.dismiss();
            });

            bsView.findViewById(R.id.bsRename).setOnClickListener(view12 -> {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Rename To");
                EditText editText = new EditText(context);
                String path = videoList.get(position).getPath();
                File file = new File(path);
                String videoName = file.getName();
                videoName = videoName.substring(0, videoName.lastIndexOf("."));
                editText.setText(videoName);
                alertDialog.setView(editText);
                editText.requestFocus();

                alertDialog.setPositiveButton("OK", (dialogInterface, i) -> {
                    if (!editText.getText().toString().isEmpty()) {
                        String onlyPath = file.getParentFile().getAbsolutePath();
                        String ext = file.getAbsolutePath();
                        ext = ext.substring(ext.lastIndexOf("."));
                        String newPath = onlyPath + "/" + editText.getText().toString() + ext;
                        Log.d("DJ", "onBindViewHolder: RENAME newPath = " + newPath);
                        File newFile = new File(newPath);
                        if (!newFile.exists()) {
                            try {
                                boolean rename = file.renameTo(newFile);
                                if (rename) {
                                    ContentResolver resolver = context.getContentResolver();
                                    resolver.delete(MediaStore.Files.getContentUri("external"), MediaStore.MediaColumns.DATA + "=?",
                                            new String[]{file.getAbsolutePath()});
                                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                    intent.setData(Uri.fromFile(newFile));
                                    context.sendBroadcast(intent);
                                    VideoFilesAdapter.this.notifyDataSetChanged();
                                    Toast.makeText(context, "Video Renamed Successfully", Toast.LENGTH_SHORT).show();
                                    SystemClock.sleep(200);
                                    ((Activity) context).recreate();
                                } else {
                                    Toast.makeText(context, "Unable to rename!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(context, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    } else {
                        Toast.makeText(context, "this file already exist!", Toast.LENGTH_SHORT).show();
                        editText.setError("already exist!");
                        editText.requestFocus();
                    }
                }).setNegativeButton("cancel", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
                bottomSheetDialog.dismiss();
            });

            bsView.findViewById(R.id.bsShare).setOnClickListener(view13 -> {
                Uri uri = Uri.parse(videoList.get(position).getPath());
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("video/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                context.startActivity(Intent.createChooser(shareIntent, "Share Video Via"));
                bottomSheetDialog.dismiss();


            });
            bsView.findViewById(R.id.bsDelete).setOnClickListener(view14 -> {
                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(context);
                deleteDialog.setTitle("Delete")
                        .setMessage("Are You Really Want to Delete this video?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            Uri contentUri = ContentUris.
                                    withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                            Long.parseLong(videoList.get(position).getId()));
                            File file = new File(videoList.get(position).getPath());
                            boolean delete = file.delete();
                            if (delete) {
                                context.getContentResolver().delete(contentUri, null, null);
                                videoList.remove(position);
                                VideoFilesAdapter.this.notifyItemRemoved(position);
                                VideoFilesAdapter.this.notifyItemRangeChanged(position, videoList.size());
                                Toast.makeText(context, "Video Deleted Successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Unable To Delete This Video!", Toast.LENGTH_SHORT).show();
                            }

                        }).setNegativeButton("No", (dialogInterface, i) -> {

                }).create().show();
                bottomSheetDialog.dismiss();
            });

            bsView.findViewById(R.id.bsProperties).setOnClickListener(view15 -> {
                AlertDialog.Builder propertiesDialog = new AlertDialog.Builder(context);
                propertiesDialog.setTitle("Properties");
                String one = "File: " + videoList.get(position).getDisplayName();
                String path = videoList.get(position).getPath();
                int indexOfPath = path.lastIndexOf("/");
                String two = "Path: " + path.substring(0, indexOfPath);
                String three = "Size: " + android.text.format.Formatter.formatFileSize(context, Long.parseLong(videoList.get(position).getSize()));
                String fourth = "Duration: " + timeConversation((long) milliSeconds);
                String nameWithFormat = videoList.get(position).getDisplayName();
                int index = nameWithFormat.lastIndexOf(".");
                String format = nameWithFormat.substring(index + 1);
                String five = "Format: " + format;

                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(videoList.get(position).getPath());
                String height = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                String width = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                String six = "Resolution: " + width + "x" + height;


                propertiesDialog.setMessage(one + "\n\n" + two + "\n\n" + three + "\n\n" + fourth + "\n\n" + five + "\n\n" + six);
                propertiesDialog.setPositiveButton("Close", (dialog, i) -> dialog.dismiss());
                propertiesDialog.create().show();
                bottomSheetDialog.dismiss();
            });

            bottomSheetDialog.setContentView(bsView);
            bottomSheetDialog.show();

        });

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("videoTitle", videoList.get(position).getDisplayName());
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("videoArrayList", videoList);
            intent.putExtras(bundle);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        VideoItemBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = VideoItemBinding.bind(itemView);
        }
    }

    @SuppressLint("DefaultLocale")
    public String timeConversation(long value) {
        String videoTime;
        int duration = (int) value;
        int hrs = (duration / 3600000);
        int mns = (duration / 60000) % 60000;
        int sec = duration % 60000 / 1000;
        if (hrs > 0)
            videoTime = String.format("%02d:%02d:%02d", hrs, mns, sec);
        else
            videoTime = String.format("%02d:%02d", mns, sec);

        return videoTime;
    }

    public void updateVideoFiles(ArrayList<MediaFiles> files) {
        videoList = new ArrayList<>();
        videoList.addAll(files);
        notifyDataSetChanged();
    }

}
