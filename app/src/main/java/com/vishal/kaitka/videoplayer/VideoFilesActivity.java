package com.vishal.kaitka.videoplayer;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vishal.kaitka.videoplayer.adapters.VideoFilesAdapter;
import com.vishal.kaitka.videoplayer.databinding.ActivityVideoFilesBinding;
import com.vishal.kaitka.videoplayer.models.MediaFiles;

import java.util.ArrayList;
import java.util.Objects;

public class VideoFilesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String MY_PREF = "my pref";
    ActivityVideoFilesBinding binding;

    private ArrayList<MediaFiles> videoFilesArrayList = new ArrayList<>();
    VideoFilesAdapter videoFilesAdapter;
    String folderName = "";
    String sortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoFilesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        folderName = getIntent().getStringExtra("folderName");
        Objects.requireNonNull(getSupportActionBar()).setTitle(folderName);

        binding.swipeRefresh.setOnRefreshListener(() -> {
            showVideoFiles();
            binding.swipeRefresh.setRefreshing(false);
        });


        showVideoFiles();

    }

    @SuppressLint("NotifyDataSetChanged")
    private void showVideoFiles() {
        videoFilesArrayList = fetchMedia();
        videoFilesAdapter = new VideoFilesAdapter(videoFilesArrayList, this);
        binding.videosRv.setAdapter(videoFilesAdapter);
        binding.videosRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        videoFilesAdapter.notifyDataSetChanged();
    }

    @SuppressLint("Range")
    private ArrayList<MediaFiles> fetchMedia() {
        SharedPreferences preferences = getSharedPreferences(MY_PREF, MODE_PRIVATE);
        String sort_value = preferences.getString("sort", "");


        ArrayList<MediaFiles> videoFiles = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        switch (sort_value) {
            case "sortName":
                sortOrder = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
                break;
            case "sortSize":
                sortOrder = MediaStore.MediaColumns.SIZE + " DESC";
                break;
            case "sortDate":
                sortOrder = MediaStore.MediaColumns.DATE_ADDED + " DESC";
                break;
            default:
                sortOrder = MediaStore.Video.Media.DURATION + " DESC";
                break;
        }

        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] selectionArg = new String[]{"%" + folderName + "%"};
        Cursor cursor = getContentResolver().query(uri, null, selection, selectionArg, sortOrder);
        if (cursor != null && cursor.moveToNext()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                String size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                String dateAdded = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
                MediaFiles mediaFiles = new MediaFiles(id, title, displayName, size, duration, path, dateAdded);
                videoFiles.add(mediaFiles);

            } while (cursor.moveToNext());
        }
        return videoFiles;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.video_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.searchVideo);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences preferences = getSharedPreferences(MY_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int id = item.getItemId();
        switch (id) {
            case R.id.refreshFiles:
                finish();
                startActivity(getIntent());
                break;
            case R.id.shortBy:
                AlertDialog.Builder filterDialog = new AlertDialog.Builder(this);
                filterDialog.setTitle("Short Title");
                filterDialog.setPositiveButton("OK", (dialogInterface, i) -> {
                    editor.apply();
                    finish();
                    startActivity(getIntent());
                    dialogInterface.dismiss();
                });

                String[] items = {"Name (A to Z)", "Size (Big to Small)", "Date (New to Old)"
                        , "Duration (Long to Short)"};

                filterDialog.setSingleChoiceItems(items, -1, (dialogInterface, i) -> {
                    switch (i) {
                        case 0:
                            editor.putString("sort", "sortName");
                            break;
                        case 1:
                            editor.putString("sort", "sortSize");
                            break;
                        case 2:
                            editor.putString("sort", "sortDate");
                            break;
                        case 3:
                            editor.putString("sort", "sortLength");
                            break;
                    }
                }).create().show();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        String inputs = s.toLowerCase();
        ArrayList<MediaFiles> mediaFiles = new ArrayList<>();
        for (MediaFiles media : videoFilesArrayList) {
            if (media.getTitle().toLowerCase().contains(inputs)) {
                mediaFiles.add(media);
            }
        }
        videoFilesAdapter.updateVideoFiles(mediaFiles);
        return true;
    }


}