package com.freecatwallpapers.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.freecatwallpapers.models.Wallpaper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class SharedPrefsUtils {
    public static class Constants {
        public static final String CHANGE_WALLPAPER_AUTOMATICALLY = "change_wallpaper_automatically";
        public static final String USE_DOWNLOADED_WALLPAPERS = "use_downloaded_wallpapers_only";
        public static final String AUTO_WALL_PAPER_CHANGE_DURATION = "wallpaper_change_duration";
        public static final String DISPLAY_HEIGHT = "display_height";
        public static final String DISPLAY_WIDTH = "display_width";
    }

    public static class MostViewedSharedPrefs {
        private static MostViewedSharedPrefs sInstance;
        private static SharedPreferences prefs;
        private static Editor editor;
        private static Context context;

        public static MostViewedSharedPrefs getInstance(Context context2) {
            if (sInstance == null) {
                prefs = context2.getSharedPreferences("most-viewed", Context.MODE_PRIVATE);
                editor = prefs.edit();
                sInstance = new MostViewedSharedPrefs();
            }
            context = context2;
            return sInstance;
        }

        public void addViewed(Wallpaper wallpaper) {
            String wallpaperJson = new Gson().toJson(wallpaper);
            int count = prefs.getInt(wallpaperJson, 0);
            if (count + 1 == 3) {
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("most-viewed-added").putExtra("wallpaper", wallpaper));
            }
            editor.putInt(wallpaperJson, count + 1).commit();
        }

        public ArrayList<Wallpaper> getAllMostViewed() {
            Map<String, ?> allFavPrefs = prefs.getAll();
            ArrayList<Wallpaper> mostViewed = new ArrayList<>();
            for (Map.Entry<String, ?> entry : allFavPrefs.entrySet()) {
                if ((int) entry.getValue() >= 3) {
                    Wallpaper wallpaper = new Gson().fromJson(entry.getKey(), Wallpaper.class);
                    mostViewed.add(wallpaper);
                }
            }
            return mostViewed;
        }

    }

    public static class FavoritesSharedPrefs {
        private static FavoritesSharedPrefs sInstance;
        private static SharedPreferences prefs;
        private static Editor editor;

        public static FavoritesSharedPrefs getInstance(Context context) {
            if (sInstance == null) {
                prefs = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
                editor = prefs.edit();
                sInstance = new FavoritesSharedPrefs();
            }
            return sInstance;
        }

        public void addToFavorites(Wallpaper wallpaper) {
            editor.putString(wallpaper.getName(), wallpaper.getUrl()).commit();
        }

        public ArrayList<Wallpaper> getAllFavorites() {

            Map<String, ?> allFavPrefs = prefs.getAll();
            ArrayList<Wallpaper> favorites = new ArrayList<>();
            for (Map.Entry<String, ?> entry : allFavPrefs.entrySet()) {
                String name = entry.getKey();
                String url = (String) entry.getValue();
                Wallpaper wallpaper = new Wallpaper(name, url);
                favorites.add(wallpaper);
            }
            return favorites;
        }

        public void removeFromFav(Wallpaper wallpaper) {
            editor.remove(wallpaper.getName()).commit();
        }

        public boolean isFavorite(String wallpaperName) {
            String value = prefs.getString(wallpaperName, null);
            if (value == null) return false;
            return true;
        }
    }

    private static final String PREF_APP = "general";

    private SharedPrefsUtils() {
        throw new UnsupportedOperationException(
                "Should not create instance of Util class. Please use as static..");
    }

    /**
     * Gets boolean data.
     *
     * @param context the context
     * @param key     the key
     * @return the boolean data
     */
    static public boolean getBooleanData(Context context, String key) {

        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getBoolean(key, false);
    }

    /**
     * Gets int data.
     *
     * @param context the context
     * @param key     the key
     * @return the int data
     */
    static public int getIntData(Context context, String key) {
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getInt(key, 0);
    }

    static public long getLongData(Context context, String key) {
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getLong(key, 900000);
    }

    /**
     * Gets string data.
     *
     * @param context the context
     * @param key     the key
     * @return the string data
     */
    // Get Data
    static public String getStringData(Context context, String key) {
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getString(key, null);
    }

    /**
     * Save data.
     *
     * @param context the context
     * @param key     the key
     * @param val     the val
     */
    // Save Data
    static public void saveData(Context context, String key, String val) {
        context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).edit().putString(key, val).apply();
    }

    /**
     * Save data.
     *
     * @param context the context
     * @param key     the key
     * @param val     the val
     */
    static public void saveData(Context context, String key, int val) {
        context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE)
                .edit()
                .putInt(key, val).apply();
    }

    /**
     * Save data.
     *
     * @param context the context
     * @param key     the key
     * @param val     the val
     */
    static public void saveData(Context context, String key, boolean val) {
        context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(key, val)
                .apply();
    }

    static public void saveData(Context context, String key, long val) {
        context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE)
                .edit()
                .putLong(key, val)
                .apply();
    }


    static public SharedPreferences.Editor getSharedPrefEditor(Context context, String pref) {
        return context.getSharedPreferences(pref, Context.MODE_PRIVATE).edit();
    }

    static public void saveData(Editor editor) {
        editor.apply();
    }
}
