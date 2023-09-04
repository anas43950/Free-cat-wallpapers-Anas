package com.freecatwallpapers.activities;

import android.Manifest.permission;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;
import androidx.work.WorkManager;

import com.freecatwallpapers.adapters.WallpapersViewPagerAdapter;
import com.freecatwallpapers.databinding.ActivityWallpaperViewerBinding;
import com.freecatwallpapers.databinding.DialogSetWallpaperBinding;
import com.freecatwallpapers.models.Wallpaper;
import com.freecatwallpapers.utils.ImageUtils;
import com.freecatwallpapers.utils.PermissionUtils;
import com.freecatwallpapers.utils.SharedPrefsUtils;
import com.freecatwallpapers.utils.SharedPrefsUtils.Constants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class WallpaperViewerActivity extends AppCompatActivity {
    ActivityWallpaperViewerBinding binding;
    int position;
    WallpapersViewPagerAdapter adapter;
    private final int STORAGE_PERMISSION = 102;
    ArrayList<Wallpaper> wallpapers;
    int width, height;
    private WallpaperManager wallpaperManager;
    private AlertDialog setWallpaperDialog;
    private boolean settingBoth = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWallpaperViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        wallpaperManager = WallpaperManager.getInstance(WallpaperViewerActivity.this);
        setListeners();

        position = getIntent().getIntExtra("position", 0);
        wallpapers = (ArrayList<Wallpaper>) getIntent().getSerializableExtra("wallpapers");

        adapter = new WallpapersViewPagerAdapter(wallpapers);
        binding.wallpaperViewPager.setAdapter(adapter);
        binding.wallpaperViewPager.setCurrentItem(position, false);

    }

    private void setListeners() {
        binding.backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.wallpaperViewPager.registerOnPageChangeCallback(new OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                WallpaperViewerActivity.this.position = position;
            }
        });
        binding.applyBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetWallpaperDialog();
            }
        });
        binding.saveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && !PermissionUtils.hasPermission(WallpaperViewerActivity.this, permission.WRITE_EXTERNAL_STORAGE)) {
                    PermissionUtils.requestPermissions(WallpaperViewerActivity.this, new String[]{permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
                    return;
                }
                File savedWallpaperFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/"
                        + wallpapers.get(position).getName() + ".png");
                if (savedWallpaperFile.exists()) {
                    Toast.makeText(WallpaperViewerActivity.this, "Wallpaper is already saved", Toast.LENGTH_SHORT).show();
                } else {
                    ImageUtils.saveWallpaper(savedWallpaperFile, WallpaperViewerActivity.this, wallpapers.get(position).getName());
                }
            }
        });

        binding.shareBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = ImageUtils.getWallpaperUri(WallpaperViewerActivity.this, wallpapers.get(position));
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setType("image/png");
                startActivity(intent);
            }
        });
    }

    private void showSetWallpaperDialog() {


        DialogSetWallpaperBinding dialogBinding = DialogSetWallpaperBinding.inflate(getLayoutInflater());
        setWallpaperDialog = new AlertDialog.Builder(this).setView(dialogBinding.getRoot()).create();

        Bitmap wallpaperBitmap = ImageUtils.getWallpaperBitmap(this, wallpapers.get(position));

        dialogBinding.setHomeWallpaper.setOnClickListener(view -> {
            setHomeScreen(wallpaperBitmap);
            dismissDialog(setWallpaperDialog);
        });

        dialogBinding.setLockWallpaper.setOnClickListener(view -> {
            setLockScreen(wallpaperBitmap);
            dismissDialog(setWallpaperDialog);
        });

        dialogBinding.setBoth.setOnClickListener(v -> {
            setHomeScreen(wallpaperBitmap);
            setLockScreen(wallpaperBitmap);
            dismissDialog(setWallpaperDialog);

        });
        setWallpaperDialog.show();
        setWallpaperDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void setHomeScreen(Bitmap wallpaperBitmap) {
        width = SharedPrefsUtils.getIntData(this, Constants.DISPLAY_WIDTH);
        height = SharedPrefsUtils.getIntData(this, Constants.DISPLAY_HEIGHT);
        try {
            wallpaperManager.setWallpaperOffsetSteps(1, 1);
            wallpaperManager.suggestDesiredDimensions(width, height);
            wallpaperManager.setBitmap(getScaledBitmap(wallpaperBitmap));
            stopAutoWallpaperChanger();
        } catch (IOException e) {
            Log.d("Logged", "showSetWallpaperDialog: error : ");
            e.printStackTrace();
        }
    }

    private void setLockScreen(Bitmap wallpaperBitmap) {
        try {
            wallpaperManager.setBitmap(wallpaperBitmap, null, true, WallpaperManager.FLAG_LOCK);
        } catch (IOException e) {
            Log.d("Logged", "showSetWallpaperDialog: error : ");
            e.printStackTrace();
        }
    }

    private void dismissDialog(AlertDialog setWallpaperDialog) {
        setWallpaperDialog.dismiss();
        Toast.makeText(this, "Wallpaper Set Successfully", Toast.LENGTH_SHORT).show();
    }

    private Bitmap getScaledBitmap(Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    private void stopAutoWallpaperChanger() {
        WorkManager.getInstance(this)
                .cancelUniqueWork(AutoWallpaperChangerActivity.AUTO_WALLPAPER_CHANGE_WORK);
        SharedPrefsUtils.saveData(this, Constants.CHANGE_WALLPAPER_AUTOMATICALLY, false);
    }
}