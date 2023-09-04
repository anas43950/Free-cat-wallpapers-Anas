package com.freecatwallpapers.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.freecatwallpapers.R;
import com.freecatwallpapers.adapters.SliderAdapter;
import com.freecatwallpapers.databinding.ActivityMainBinding;
import com.freecatwallpapers.fragments.FavoritesFragment;
import com.freecatwallpapers.fragments.MainFragment;
import com.freecatwallpapers.fragments.MostViewedFragment;
import com.freecatwallpapers.models.Wallpaper;
import com.freecatwallpapers.utils.SharedPrefsUtils;
import com.freecatwallpapers.utils.SharedPrefsUtils.Constants;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.tabs.TabLayout.Tab;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy;
import com.smarteist.autoimageslider.SliderAnimations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    ActionBarDrawerToggle actionBarDrawerToggle;
    static AlertDialog progressDialog;
    ActivityMainBinding binding;
    ArrayList<Wallpaper> wallpapers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        wallpapers = (ArrayList<Wallpaper>) getIntent().getSerializableExtra("wallpapers");
        setSupportActionBar(binding.toolbar);
        setTitle("");
        setupSidebar();
        setUpMainViewPager();
        setUpSlider();
        saveScreenWidthAndHeight();
//        uploadDataOnFirebase(); //from assets to firebase
    }


    private void setupSidebar() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, binding.mainDrawer, R.string.nav_open, R.string.nav_close);
        binding.mainDrawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.off_white, null));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.sidebarNav.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_exit) {
                    new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme)
                            .setCancelable(false)
                            .setMessage("Do you want to exit?")
                            .setNegativeButton("No", new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .setPositiveButton("Yes", new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).show();
                    return true;
                }
                if (itemId == R.id.auto_wallpaper_changer_menu) {
                    startActivity(new Intent(MainActivity.this, AutoWallpaperChangerActivity.class));
                    return true;
                }
                if (itemId == R.id.menu_rating) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.direct_play_store_link))));
                    return true;
                }
                if (itemId == R.id.menu_share) {
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    String shareBody = "Download this app for cool and funny cat wallpapers\n" + getString(R.string.app_link);
                    intent.setType("text/plain");
                    intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(intent, "Share Using..."));
                    return true;
                }
                if (itemId == R.id.menu_feedback) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:" + getString(R.string.feedback_email))); // only email apps should handle this
                    startActivity(intent);
                    return true;
                }
                if (itemId == R.id.menu_about) {
                    startActivity(new Intent(MainActivity.this, AboutActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // This all block is to upload data from assets folder to firebase
    /*public void getBitmapFromAsset(String wallpaperName) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference mRef = storage.getReference();
        InputStream is;
        try {
            is = getAssets().open("wallpapers/" + wallpaperName);
            mRef.child("uncompressed").child(wallpaperName).putStream(is).addOnCompleteListener(new OnCompleteListener<TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Task<Uri> getUrlTask = task.getResult().getMetadata().getReference().getDownloadUrl();
                        getUrlTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String url = task.getResult().toString();
                                uploadUrlOnFirebase(url, wallpaperName);
                            }
                        });
                    } else {
                        task.getException().printStackTrace();
                    }
                    try {
                        is.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            });
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }


    public void uploadDataOnFirebase() {
        ArrayList<String> wallpaperNameList = listWallpapersNames();
        Log.d("Logged", "uploadDataOnFirebase: size : " + wallpaperNameList.size());
        for (String wallpaperName : wallpaperNameList) {
            getBitmapFromAsset(wallpaperName);
        }

    }

    public void uploadUrlOnFirebase(String url, String wallpaperName) {
        FirebaseDatabase mDb = FirebaseDatabase.getInstance();
        DatabaseReference mRef = mDb.getReference();
        mRef.child("urls").child("uncompressed").child(wallpaperName.substring(0, wallpaperName.indexOf('.'))).setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("Logged", "onComplete: completed : ");
                } else {
                    task.getException().printStackTrace();
                }
            }
        });
    }

    private ArrayList<String> listWallpapersNames() {
        ArrayList<String> sliderWallpaperList = new ArrayList<>();
        String[] list;
        try {
            list = getAssets().list("wallpapers");
            if (list.length > 0) {
                Collections.addAll(sliderWallpaperList, list);
            }
        } catch (IOException e) {
            return null;
        }
        return sliderWallpaperList;
    }*/

    private ArrayList<String> listSliderWallpapersNames() {
        ArrayList<String> sliderWallpaperList = new ArrayList<>();
        String[] list;
        try {
            list = getAssets().list("slider-images");
            if (list.length > 0) {
                Collections.addAll(sliderWallpaperList, list);
            }
        } catch (IOException e) {
            return null;
        }
        return sliderWallpaperList;
    }

    private void setUpSlider() {
        if (SharedPrefsUtils.getBooleanData(this, Constants.CHANGE_WALLPAPER_AUTOMATICALLY)) {
            binding.sliderRv.setVisibility(View.GONE);
            return;
        }
        SliderAdapter sliderAdapter = new SliderAdapter(listSliderWallpapersNames(), this);
        binding.autoWallpaperChangerSlider.setSliderAdapter(sliderAdapter);
        binding.autoWallpaperChangerSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        binding.autoWallpaperChangerSlider.startAutoCycle();
    }

    private void setUpMainViewPager() {
        binding.mainViewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                Fragment fragment = new Fragment();
                if (position == 0) {
                    fragment = new FavoritesFragment();
                } else if (position == 1) {
                    fragment = new MainFragment();
                    Bundle b = new Bundle();
                    b.putSerializable("wallpapers", wallpapers);
                    fragment.setArguments(b);
                } else if (position == 2) {
                    fragment = new MostViewedFragment();
                }
                return fragment;
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        });
        new TabLayoutMediator(binding.mainTabLayout, binding.mainViewPager, new TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull Tab tab, int position) {
                if (position == 0) {
                    tab.setIcon(R.drawable.ic_baseline_favorite_24);
                } else if (position == 1) {
                    tab.setIcon(R.drawable.ic_all);
                } else {
                    tab.setIcon(R.drawable.ic_most_viewed);
                }
            }
        }).attach();
        binding.mainViewPager.setCurrentItem(1, false);


    }

    public static void showProgressDialog(Context context, String message) {
        progressDialog = new AlertDialog.Builder(context)
                .setView(R.layout.circular_progress_bar)
                .setCancelable(false)
                .create();
        progressDialog.show();
        TextView messageView = progressDialog.findViewById(R.id.progress_tv);
        if (message.equals("")) {
            messageView.setVisibility(View.GONE);
        } else {
            messageView.setText(message);
        }
    }

    public static void closeProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    private void saveScreenWidthAndHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        SharedPrefsUtils.saveData(this, Constants.DISPLAY_WIDTH, width);
        SharedPrefsUtils.saveData(this, Constants.DISPLAY_HEIGHT, height);
    }
}