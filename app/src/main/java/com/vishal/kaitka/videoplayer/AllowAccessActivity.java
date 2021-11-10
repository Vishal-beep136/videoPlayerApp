package com.vishal.kaitka.videoplayer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.vishal.kaitka.videoplayer.databinding.ActivityAllowAccessBinding;

public class AllowAccessActivity extends AppCompatActivity {

    public static final int REQUEST_PERMISSION_SETTING = 2;
    ActivityAllowAccessBinding binding;
    String[] mPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    public static final int STORAGE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllowAccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences preferences = getSharedPreferences("AllowAccess", MODE_PRIVATE);

        String value = preferences.getString("Allow", "");
        if (value.equals("OK")) {
            startActivity(new Intent(AllowAccessActivity.this, MainActivity.class));
            finish();
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Allow", "OK");
        editor.apply();

        binding.allowBtn.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {

                startActivity(new Intent(AllowAccessActivity.this, MainActivity.class));
                finish();

            } else {
                ActivityCompat.requestPermissions(AllowAccessActivity.this, mPermissions, STORAGE_PERMISSION);
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION) {
            for (int i = 0; i < permissions.length; i++) {
                String per = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    boolean showRationale = shouldShowRequestPermissionRationale(per);
                    if (!showRationale) {
                        //user clicked on never ask again
                        AlertDialog.Builder builder = new AlertDialog.Builder(AllowAccessActivity.this);
                        builder.setTitle("App Permission")
                                .setMessage("For Playing Videos on this app you must have give permission to access your videos which is stored in your device." +
                                        "Note : We Do Not See Your Any Files"
                                        + "\n\n" + "Please Follow these below steps to Give Permissions" + "\n\n" + "Open Settings from below button" +
                                        "\n" + "Click on Permissions" + "\n" +
                                        "Allow Access For Storage").setPositiveButton("Open Setting", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                            }
                        }).setNegativeButton("Cancel", null).create().show();


                    } else {
                        ActivityCompat.requestPermissions(AllowAccessActivity.this, mPermissions, STORAGE_PERMISSION);
                    }
                } else {
                    startActivity(new Intent(AllowAccessActivity.this, MainActivity.class));
                    finish();
                }
            }
        }
    } // onRequestPermissionResult

    @Override
    protected void onResume() {
        super.onResume();
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(AllowAccessActivity.this, MainActivity.class));
            finish();
        }
    }
}