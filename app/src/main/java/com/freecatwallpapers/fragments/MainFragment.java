package com.freecatwallpapers.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.freecatwallpapers.R;
import com.freecatwallpapers.activities.MainActivity;
import com.freecatwallpapers.adapters.MainGridAdapter;
import com.freecatwallpapers.databinding.FragmentMainBinding;
import com.freecatwallpapers.models.Wallpaper;
import com.freecatwallpapers.utils.FirebaseUtils;
import com.freecatwallpapers.utils.FirebaseUtils.FetchWallpapersCallbacks;

import java.util.ArrayList;
import java.util.Collections;

public class MainFragment extends Fragment implements FetchWallpapersCallbacks {
    FragmentMainBinding binding;
    MainGridAdapter adapter;
    ArrayList<Wallpaper> wallpapers;
    int i = 1;
    int size = 0;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(getLayoutInflater());
        wallpapers = (ArrayList<Wallpaper>) getArguments().getSerializable("wallpapers");
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_fragment_menu, menu);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        adapter = new MainGridAdapter(wallpapers);
        binding.mainRv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.mainRv.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            MainActivity.showProgressDialog(getContext(), "Loading...");
            wallpapers.removeAll(wallpapers);
            FirebaseUtils.getInstance(this).loadWallpapers();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWallpaperFetched(Wallpaper wallpaper) {
        wallpapers.add(wallpaper);
        if (i == size) {
            Collections.shuffle(wallpapers);
            adapter.setWallpapers(wallpapers);
            MainActivity.closeProgressDialog();
        }
        i++;
    }

    @Override
    public void onSizeFetched(int size) {
        this.size = size;
        if (size == 0) {
            MainActivity.closeProgressDialog();
        }
    }
}
