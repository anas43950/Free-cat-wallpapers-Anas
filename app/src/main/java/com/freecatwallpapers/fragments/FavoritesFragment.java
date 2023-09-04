package com.freecatwallpapers.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freecatwallpapers.adapters.MainGridAdapter;
import com.freecatwallpapers.databinding.FragmentFavoritesBinding;
import com.freecatwallpapers.models.Wallpaper;
import com.freecatwallpapers.utils.SharedPrefsUtils.FavoritesSharedPrefs;

public class FavoritesFragment extends Fragment {
    FavoritesSharedPrefs favoritesSharedPrefs;
    MainGridAdapter adapter;
    FragmentFavoritesBinding binding;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFavoritesBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        favoritesSharedPrefs = FavoritesSharedPrefs.getInstance(getContext());
        adapter = new MainGridAdapter(favoritesSharedPrefs.getAllFavorites());
        binding.favoritesRv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.favoritesRv.setAdapter(adapter);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter("favorite-changed"));
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Wallpaper wallpaper = (Wallpaper) intent.getSerializableExtra("wallpaper");
            boolean isFavorite = intent.getBooleanExtra("isFavorite", false);
            if (isFavorite) {
                adapter.addWallpaper(wallpaper);
            } else {
                adapter.removeWallpaper(wallpaper);
            }
        }
    };


}