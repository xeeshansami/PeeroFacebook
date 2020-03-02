package com.phool.svd.AllVideosFragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.phool.svd.Launch;
import com.phool.svd.PreferenceManager;
import com.phool.svd.R;
import com.phool.svd.SettingActivity;
import com.phool.svd.Sliders;

import java.util.ArrayList;

/**
 * Created by duskysol on 1/5/2019.
 */

public class videofolder extends AppCompatActivity {
    private int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1234;
    String Foldername;
    PreferenceManager preferenceManager;
    private AdView mAdView;
    private AdRequest adRequest;
    ProgressBar loadingIndicator;
    ArrayList<Model_Video> al_video = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewLayoutManager;
    Adapter_VideoFolder obj_adapter;
    Button button, howtoBtn;
    private static final int REQUEST_PERMISSIONS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_all__videos);
        //toobar & backpress
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Downloaded Videos");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        button = findViewById(R.id.browse_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(videofolder.this, Launch.class));
            }
        });
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    private void init() {
        MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.banner_ad_unit_id));
        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        mAdView = (AdView) findViewById(R.id.adView_banner);
        adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        recyclerView = (RecyclerView) findViewById(R.id.videoRecycler);
        howtoBtn = findViewById(R.id.howtoBtn);
        howtoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceManager = new PreferenceManager(videofolder.this);
        /*============================*/
                if (!preferenceManager.FirstLaunch()) {
                    preferenceManager.setFirstTimeLaunch(true);
                    startActivity(new Intent(videofolder.this, Sliders.class));
                    finish();
                }
            }
        });
        recyclerViewLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        try {
            if (checkStoragePermission()) {
                fn_video();
            } else {
                requestStoragePermission();
            }
        } catch (SecurityException e) {
            System.out.print(e.getMessage());
        }
    }

    public void fn_video() {
//        Bundle bundle=getIntent().getExtras();
//        String newNameOfFolder=bundle.getString("folderlocation");
//        Foldername = "FacebookVideos";
//        if(newNameOfFolder!=null)
//        {
//            Foldername=newNameOfFolder;
//            File mBaseFolderPath = new File(Environment.getExternalStorageDirectory(), Foldername);
//            if (!mBaseFolderPath.exists() && mBaseFolderPath.isDirectory()) {
//                new File(String.valueOf(mBaseFolderPath)).mkdir();
//            }
//        }
//        else {
//            File mBaseFolderPath = new File(Environment.getExternalStorageDirectory(), Foldername);
//            if (!mBaseFolderPath.exists() && mBaseFolderPath.isDirectory()) {
//                new File(String.valueOf(mBaseFolderPath)).mkdir();
//            }
//        }
        al_video.clear();
        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name, column_id, thum;

        String absolutePathOfImage = null;
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media._ID, MediaStore.Video.Thumbnails.DATA};

        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] selectionArgs = new String[]{"%FreeSVD%"};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getApplicationContext().getContentResolver().query(uri, projection, selection, selectionArgs, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
        column_id = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
        thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Log.e("Column", absolutePathOfImage);
            Log.e("Folder", cursor.getString(column_index_folder_name));
            Log.e("column_id", cursor.getString(column_id));
            Log.e("thum", cursor.getString(thum));

            Model_Video obj_model = new Model_Video();
            obj_model.setBoolean_selected(false);
            obj_model.setStr_path(absolutePathOfImage);
            obj_model.setStr_thumb(cursor.getString(thum));
            al_video.add(obj_model);
        }
        obj_adapter = new Adapter_VideoFolder(getApplicationContext(), al_video, videofolder.this);
        recyclerView.setAdapter(obj_adapter);
        obj_adapter.notifyDataSetChanged();
        TextView textView = findViewById(R.id.novideofound);
        if (al_video.size() == 0) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    private boolean checkStoragePermission() {
        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;


    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.downloaded_menu, menu);
//        MenuItem search = menu.findItem(R.id.search);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
//        search(searchView);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
        }
        if (id == R.id.setting) {
            startActivity(new Intent(this, SettingActivity.class));
            finish();
        } else if (id == R.id.download_menu) {
            startActivity(new Intent(this, videofolder.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fn_video();
            } else {
                checkStoragePermission();
            }
        }
    }

}
