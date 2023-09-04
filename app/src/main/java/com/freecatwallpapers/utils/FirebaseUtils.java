package com.freecatwallpapers.utils;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.freecatwallpapers.models.Wallpaper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

public class FirebaseUtils {
    private static FirebaseStorage mStorage;
    private static FirebaseUtils sInstance;
    private static StorageReference mStorageRef;
    private static FetchWallpapersCallbacks listener;

    private FirebaseUtils() {
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
    }

    public static FirebaseUtils getInstance(FetchWallpapersCallbacks listener) {
        if (sInstance == null) {
            sInstance = new FirebaseUtils();
        }
        FirebaseUtils.listener = listener;
        return sInstance;
    }

    public void loadWallpapers() {

        /*mRef.child("urls").child("uncompressed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Wallpaper> wallpapers = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String url = (String) dataSnapshot.getValue();
                    String name = (String) dataSnapshot.getKey();
                    Wallpaper wallpaper = new Wallpaper(name, url);
                    wallpapers.add(wallpaper);
                }
                Collections.shuffle(wallpapers);
                if (activity instanceof SplashActivity) {
                    activity.startActivity(new Intent(activity, MainActivity.class).putExtra("wallpapers", wallpapers));
                    activity.finish();
                } else {
                    adapter.setWallpapers(wallpapers);
                    MainActivity.closeProgressDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.toException().printStackTrace();
            }
        });*/
        mStorageRef.child("uncompressed").listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if (task.isSuccessful()) {
                    listener.onSizeFetched(task.getResult().getItems().size());
                    for (StorageReference ref : task.getResult().getItems()) {

                        ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    String name = ref.getName();
                                    String url = String.valueOf(task.getResult());
                                    Wallpaper wallpaper = new Wallpaper(name, url);
                                    listener.onWallpaperFetched(wallpaper);
                                } else {
                                    Log.d("hahaha", "onComplete: exception : " + task.getException().getMessage());
                                }
                            }
                        });
                    }

                } else {
                    Log.d("hahaha", "onComplete: exception : " + task.getException().getMessage());
                }
            }
        });
    }

    public interface FetchWallpapersCallbacks {
        void onWallpaperFetched(Wallpaper wallpaper);

        void onSizeFetched(int size);
    }

}
