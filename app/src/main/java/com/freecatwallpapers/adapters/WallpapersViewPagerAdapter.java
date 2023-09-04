package com.freecatwallpapers.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.freecatwallpapers.databinding.WallpaperViewPagerItemBinding;
import com.freecatwallpapers.models.Wallpaper;
import com.freecatwallpapers.utils.ImageUtils;
import com.freecatwallpapers.utils.SharedPrefsUtils.FavoritesSharedPrefs;

import java.util.ArrayList;

public class WallpapersViewPagerAdapter extends RecyclerView.Adapter<WallpapersViewPagerAdapter.ViewHolder> {
    Context context;
    ArrayList<Wallpaper> wallpapers;

    @NonNull
    @Override
    public WallpapersViewPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        WallpaperViewPagerItemBinding binding = WallpaperViewPagerItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WallpapersViewPagerAdapter.ViewHolder holder, int position) {
        FavoritesSharedPrefs favoritesSharedPrefs = FavoritesSharedPrefs.getInstance(context);
        Wallpaper wallpaper = wallpapers.get(position);
        ImageUtils.loadImage(context, holder.binding.wallpaperFullScreenIv, wallpaper, holder.binding.placeholderIv);
        holder.binding.addToFavBtn.setChecked(favoritesSharedPrefs.isFavorite(wallpaper.getName()));

        holder.binding.addToFavBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        favoritesSharedPrefs.addToFavorites(wallpaper);
                        Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        favoritesSharedPrefs.removeFromFav(wallpaper);
                        Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    }
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("favorite-changed").putExtra("wallpaper", wallpaper).putExtra("isFavorite", isChecked));
                }
            }
        });
    }


    public WallpapersViewPagerAdapter(ArrayList<Wallpaper> wallpapers) {
        this.wallpapers = wallpapers;
    }

    @Override
    public int getItemCount() {
        return wallpapers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        WallpaperViewPagerItemBinding binding;

        public ViewHolder(@NonNull WallpaperViewPagerItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}