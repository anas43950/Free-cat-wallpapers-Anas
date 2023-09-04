package com.freecatwallpapers;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.freecatwallpapers.utils.SharedPrefsUtils;
import com.freecatwallpapers.utils.SharedPrefsUtils.Constants;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

public class AutoWallpaperChangerWorker extends Worker {
    Context context;
    String currentPath;

    public AutoWallpaperChangerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        context = getApplicationContext();
        File[] wallpaperFiles = getAllWallpapersFiles();
        int randomNum = ThreadLocalRandom.current().nextInt(0, wallpaperFiles.length);
        if (wallpaperFiles[randomNum].getAbsolutePath().equals(currentPath)) { // In case new wallpaper path is equal to the one already set
            randomNum++;
            if (randomNum >= wallpaperFiles.length) { // In case new random number is greater than wallpapers list size
                randomNum -= 2;
            }
        }
        currentPath = wallpaperFiles[randomNum].getAbsolutePath();
        Bitmap wallpaperBitmap = BitmapFactory.decodeFile(wallpaperFiles[randomNum].getAbsolutePath());
        setWallpaper(wallpaperBitmap);
        return Result.success();
    }

    private File[] getAllWallpapersFiles() {
        File folder;
        if (SharedPrefsUtils.getBooleanData(context, Constants.USE_DOWNLOADED_WALLPAPERS)) {
            folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/Wallpapers/");
        } else {
            folder = new File(context.getCacheDir() + "/wallpaper_images_cache/");
        }
        return folder.listFiles();
    }

    private void setWallpaper(Bitmap wallpaperBitmap) {
        int width = SharedPrefsUtils.getIntData(context, Constants.DISPLAY_WIDTH);
        int height = SharedPrefsUtils.getIntData(context, Constants.DISPLAY_HEIGHT);
        try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
            wallpaperManager.setWallpaperOffsetSteps(1, 1);
            wallpaperManager.suggestDesiredDimensions(width, height);
            wallpaperManager.setBitmap(Bitmap.createScaledBitmap(wallpaperBitmap, width, height, true), null, false, WallpaperManager.FLAG_SYSTEM);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
