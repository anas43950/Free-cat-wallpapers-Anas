package com.freecatwallpapers.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.freecatwallpapers.BuildConfig;
import com.freecatwallpapers.R;
import com.freecatwallpapers.models.Wallpaper;
import com.freecatwallpapers.utils.FirebaseUtils;
import com.freecatwallpapers.utils.FirebaseUtils.FetchWallpapersCallbacks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;

import java.util.ArrayList;
import java.util.Collections;

public class SplashActivity extends AppCompatActivity implements FetchWallpapersCallbacks {

    int size = 0;
    int i = 1;
    ArrayList<Wallpaper> wallpapers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (BuildConfig.DEBUG) {
            FirebaseApp.initializeApp(this);
            FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
            firebaseAppCheck.installAppCheckProviderFactory(
                    DebugAppCheckProviderFactory.getInstance());
        } else {
            FirebaseApp.initializeApp(this);
            FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
            firebaseAppCheck.installAppCheckProviderFactory(
                    PlayIntegrityAppCheckProviderFactory.getInstance());
        }
        FirebaseUtils.getInstance(this).loadWallpapers();
    }

    @Override
    public void onWallpaperFetched(Wallpaper wallpaper) {
        wallpapers.add(wallpaper);
        if (i == size) {
            Collections.shuffle(wallpapers);
            startMainActivity();
        }
        i++;
    }

    @Override
    public void onSizeFetched(int size) {
        this.size = size;
        if (size == 0) {
            startMainActivity();
        }
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class).putExtra("wallpapers", wallpapers));
        finish();
    }
}