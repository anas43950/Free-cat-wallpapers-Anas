package com.freecatwallpapers.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.freecatwallpapers.R;
import com.freecatwallpapers.activities.AutoWallpaperChangerActivity;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.ViewHolder> {
    ArrayList<String> wallpaperList = new ArrayList<>();
    Context context;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout, parent, false);
        return new ViewHolder(view);
    }

    public SliderAdapter(ArrayList<String> wallpaperList, Context context) {
        this.wallpaperList = wallpaperList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Glide.with(context).load("file:///android_asset/slider-images/" + wallpaperList.get(position)).into(viewHolder.imageView);
        viewHolder.imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, AutoWallpaperChangerActivity.class));
            }
        });
    }

    @Override
    public int getCount() {
        return wallpaperList.size();
    }

    static class ViewHolder extends SliderViewAdapter.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.slider_iv);
        }
    }
}
