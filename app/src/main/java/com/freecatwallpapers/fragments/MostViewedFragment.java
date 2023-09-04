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
import com.freecatwallpapers.databinding.FragmentMostViewedBinding;
import com.freecatwallpapers.models.Wallpaper;
import com.freecatwallpapers.utils.SharedPrefsUtils.MostViewedSharedPrefs;

import java.util.ArrayList;


public class MostViewedFragment extends Fragment {
    FragmentMostViewedBinding binding;
    MainGridAdapter adapter;
    MostViewedSharedPrefs mostViewedSharedPrefs;

    public MostViewedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMostViewedBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mostViewedSharedPrefs = MostViewedSharedPrefs.getInstance(getContext());
        adapter = new MainGridAdapter(mostViewedSharedPrefs.getAllMostViewed());
        binding.mostViewedRv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.mostViewedRv.setAdapter(adapter);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter("most-viewed-added"));
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Wallpaper wallpaper = (Wallpaper) intent.getSerializableExtra("wallpaper");
            adapter.addWallpaper(wallpaper);
        }
    };

}