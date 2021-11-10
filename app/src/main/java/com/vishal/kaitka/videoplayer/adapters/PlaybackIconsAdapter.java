package com.vishal.kaitka.videoplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vishal.kaitka.videoplayer.R;
import com.vishal.kaitka.videoplayer.models.IconModel;

import java.util.ArrayList;

/**
 * Created by Vishal on 10,November,2021
 */
public class PlaybackIconsAdapter extends RecyclerView.Adapter<PlaybackIconsAdapter.ViewHolder> {
    private ArrayList<IconModel> iconModelList;
    private Context context;

    public PlaybackIconsAdapter(ArrayList<IconModel> iconModelList, Context context) {
        this.iconModelList = iconModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.icons_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.icon.setImageResource(iconModelList.get(position).getImageView());
        holder.iconName.setText(iconModelList.get(position).getIconTitle());
    }

    @Override
    public int getItemCount() {
        return iconModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView iconName;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.playbackIcon);
            iconName = itemView.findViewById(R.id.iconTitle);
        }
    }
}
