package com.freecatwallpapers.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.freecatwallpapers.R;
import com.freecatwallpapers.activities.WallpaperViewerActivity;
import com.freecatwallpapers.models.Wallpaper;
import com.freecatwallpapers.utils.ImageUtils;
import com.freecatwallpapers.utils.SharedPrefsUtils.MostViewedSharedPrefs;

import java.util.ArrayList;

public class MainGridAdapter extends RecyclerView.Adapter<MainGridAdapter.ViewHolder> {
    Context context;
    ArrayList<Wallpaper> wallpapers;

    @NonNull
    @Override
    public MainGridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.main_rv_item, parent, false);
        return new ViewHolder(view);
    }

    public MainGridAdapter(ArrayList<Wallpaper> wallpapers) {
        this.wallpapers = wallpapers;
    }

    @Override
    public void onBindViewHolder(@NonNull MainGridAdapter.ViewHolder holder, int position) {
        ImageUtils.loadImage(context, holder.imageView, wallpapers.get(position), holder.placeHolderIv);
        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MostViewedSharedPrefs.getInstance(context).addViewed(wallpapers.get(position));
                context.startActivity(new Intent(context, WallpaperViewerActivity.class).putExtra("wallpapers", wallpapers).putExtra("position", position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return wallpapers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, placeHolderIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.main_rv_item_iv);
            placeHolderIv = itemView.findViewById(R.id.placeholder_iv);
        }
    }

    public void addWallpaper(Wallpaper wallpaper) {
        wallpapers.add(wallpaper);
        notifyDataSetChanged();
    }

    public void removeWallpaper(Wallpaper wallpaper) {
        wallpapers.removeIf(s -> (s.getName().equals(wallpaper.getName())));
        notifyDataSetChanged();
    }

    public void setWallpapers(ArrayList<Wallpaper> wallpapers) {
        this.wallpapers = wallpapers;
        notifyDataSetChanged();
    }

    public ArrayList<Wallpaper> getWallpapers() {
        return wallpapers;
    }
}