package com.freecatwallpapers.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.freecatwallpapers.models.Wallpaper;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {

    public static void loadImage(Context context, ImageView imageView, Wallpaper wallpaper, ImageView placeholderIv) {
        File imageFile = new File(context.getCacheDir() + "/wallpaper_images_cache/" + wallpaper.getName() + ".png");
        if (imageFile.exists()) {
            Glide.with(context).load(Uri.fromFile(imageFile)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    placeholderIv.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    placeholderIv.setVisibility(View.GONE);
                    return false;
                }
            }).into(imageView);
        } else {
            loadWithGlide(context, imageView, wallpaper.getUrl(), placeholderIv, imageFile);
        }
    }

    private static void loadWithGlide(Context context, ImageView imageView, String url, ImageView placeholderIv, File file) {
        Glide.with(context).load(url)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        placeholderIv.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        placeholderIv.setVisibility(View.GONE);
                        saveCurrentWallpaper(file, resource);
                        return false;
                    }
                })
                .into(imageView);
    }

    private static void saveCurrentWallpaper(File dest, Drawable drawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap currentWallpaperBitmap = bitmapDrawable.getBitmap();
        try {
            dest.getParentFile().mkdirs();
            dest.createNewFile();
            FileOutputStream out = new FileOutputStream(dest);
            currentWallpaperBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Uri getWallpaperUri(Context context, Wallpaper wallpaper) {
        File imageFile = new File(context.getCacheDir() + "/wallpaper_images_cache/" + wallpaper.getName() + ".png");
        if (imageFile.exists()) {
            return FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", imageFile);
        }
        return null;
    }

    public static Bitmap getWallpaperBitmap(Context context, Wallpaper wallpaper) {
        File imageFile = new File(context.getCacheDir() + "/wallpaper_images_cache/" + wallpaper.getName() + ".png");
        return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
    }

    public static void saveWallpaper(File dest, Context context, String wallpaperName) {
        File cacheImage = new File(context.getCacheDir() + "/wallpaper_images_cache/" + wallpaperName + ".png");
        try {
            FileUtils.copyFile(cacheImage, dest);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(dest)));
            Toast.makeText(context, "Wallpaper saved at " + dest.getPath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
