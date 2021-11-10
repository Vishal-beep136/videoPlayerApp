package com.vishal.kaitka.videoplayer.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vishal.kaitka.videoplayer.R;
import com.vishal.kaitka.videoplayer.VideoFilesActivity;
import com.vishal.kaitka.videoplayer.databinding.FoldersItemBinding;
import com.vishal.kaitka.videoplayer.models.MediaFiles;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Vishal on 09,November,2021
 */
public class VideoFoldersAdapter extends RecyclerView.Adapter<VideoFoldersAdapter.ViewHolder> {
    private ArrayList<MediaFiles> mediaFiles;
    private ArrayList<String> folderPath;
    private Context context;

    public VideoFoldersAdapter(ArrayList<MediaFiles> mediaFiles, ArrayList<String> folderPath, Context context) {
        this.mediaFiles = mediaFiles;
        this.folderPath = folderPath;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.folders_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int indexPath = folderPath.get(position).lastIndexOf("/");
        String nameOFFolder = folderPath.get(position).substring(indexPath + 1);
        holder.binding.folderName.setText(nameOFFolder);
        holder.binding.folderPath.setText(folderPath.get(position));
        Log.d("Dj", "onBindViewHolder: FolderPath Name : " + folderPath.get(position));
        holder.binding.noOfFiles.setText(noOfFiles(folderPath.get(position)) + " Videos");

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, VideoFilesActivity.class);
            intent.putExtra("folderName", nameOFFolder);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return folderPath.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        FoldersItemBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = FoldersItemBinding.bind(itemView);
        }
    }

    int noOfFiles(String folderName) {
        File dir = new File(folderName);
        int fileCount = 0;
        if (dir.exists()) {
            fileCount = Objects.requireNonNull(dir.listFiles()).length;
        } else {
            Toast.makeText(context, "No files", Toast.LENGTH_SHORT).show();
        }
        return fileCount;
    }

}
