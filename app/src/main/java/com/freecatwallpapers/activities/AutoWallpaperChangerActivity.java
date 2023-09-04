package com.freecatwallpapers.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.freecatwallpapers.AutoWallpaperChangerWorker;
import com.freecatwallpapers.R;
import com.freecatwallpapers.databinding.ActivityAutoWallpaperChangerBinding;
import com.freecatwallpapers.utils.SharedPrefsUtils;
import com.freecatwallpapers.utils.SharedPrefsUtils.Constants;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class AutoWallpaperChangerActivity extends AppCompatActivity {
    ActivityAutoWallpaperChangerBinding binding;
    ArrayList<String> durations;
    LinkedHashMap<Long, String> durationsHashMap = new LinkedHashMap<>();
    public static final String AUTO_WALLPAPER_CHANGE_WORK = "auto_wallpaper_changer_work";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAutoWallpaperChangerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        durationsHashMap.put(15 * 60 * 1000L, "15 Minutes");
        durationsHashMap.put(30 * 60 * 1000L, "30 Minutes");
        durationsHashMap.put(1 * 60 * 60 * 1000L, "1 Hour");
        durationsHashMap.put(3 * 60 * 60 * 1000L, "3 Hours");
        durationsHashMap.put(24 * 60 * 60 * 1000L, "24 Hours");

        setUpActionBar();
        initializeViews();
        setListeners();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initializeViews() {
        setUpSpinner();
        binding.enableWallpaperChangerSwitch.setChecked(SharedPrefsUtils.getBooleanData(this, Constants.CHANGE_WALLPAPER_AUTOMATICALLY));
        binding.useDownloadedWallpapersSwitch.setChecked(SharedPrefsUtils.getBooleanData(this, Constants.USE_DOWNLOADED_WALLPAPERS));
    }

    private void setUpActionBar() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue, null)));
        getSupportActionBar().setTitle("Auto Wallpaper Changer");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setListeners() {
        binding.enableWallpaperChangerSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    instantiateWorkManger();
                } else {
                    WorkManager.getInstance(AutoWallpaperChangerActivity.this)
                            .cancelUniqueWork(AUTO_WALLPAPER_CHANGE_WORK);
                }

                SharedPrefsUtils.saveData(AutoWallpaperChangerActivity.this, Constants.CHANGE_WALLPAPER_AUTOMATICALLY, isChecked);
            }
        });

        binding.durationSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (Entry<Long, String> entry : durationsHashMap.entrySet()) {
                    if (entry.getValue().equals(durations.get(position))) {
                        long duration = entry.getKey();
                        SharedPrefsUtils.saveData(AutoWallpaperChangerActivity.this, Constants.AUTO_WALL_PAPER_CHANGE_DURATION, duration);

                        // We will restart the work manager only if it is enabled other wise we will just save the duration
                        if (SharedPrefsUtils.getBooleanData(AutoWallpaperChangerActivity.this, Constants.CHANGE_WALLPAPER_AUTOMATICALLY)) {
                            instantiateWorkManger();
                        }
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.useDownloadedWallpapersSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPrefsUtils.saveData(AutoWallpaperChangerActivity.this, Constants.USE_DOWNLOADED_WALLPAPERS, isChecked);

                // We will restart the work manager only if it is enabled other wise we will just save the boolean
                if (SharedPrefsUtils.getBooleanData(AutoWallpaperChangerActivity.this, Constants.CHANGE_WALLPAPER_AUTOMATICALLY)) {
                    instantiateWorkManger();
                }
            }
        });
    }

    private void setUpSpinner() {
        durations = new ArrayList<>(durationsHashMap.values());
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, durations);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.durationSpinner.setAdapter(spinnerAdapter);
        binding.durationSpinner.setSelection(durations.indexOf(durationsHashMap.get(SharedPrefsUtils.getLongData(this, Constants.AUTO_WALL_PAPER_CHANGE_DURATION))), false);
    }

    private void instantiateWorkManger() {
        PeriodicWorkRequest request = new PeriodicWorkRequest
                .Builder(AutoWallpaperChangerWorker.class, SharedPrefsUtils.getLongData(this, Constants.AUTO_WALL_PAPER_CHANGE_DURATION), TimeUnit.MILLISECONDS)
                .build();
        WorkManager.getInstance(AutoWallpaperChangerActivity.this)
                .enqueueUniquePeriodicWork(AUTO_WALLPAPER_CHANGE_WORK, ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, request);
    }
}