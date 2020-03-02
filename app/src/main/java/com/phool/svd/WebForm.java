package com.phool.svd;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.phool.svd.MainContent.Launch;
import com.phool.svd.Settings.SettingActivity;

public class WebForm extends AppCompatActivity {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    FragmentFacebook fragmentFacebook;
    FragmentInstagram fragmentInstagram;
    private AdView mAdView;
    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_facebook);

            MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.app_id));
//        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
            mAdView = (AdView) findViewById(R.id.adView_banner);
            adRequest = new AdRequest.Builder()
                    .addTestDevice("8B3B217C5524B71E3E3CBA2A407F395E")
                    .build();
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                // Implement AdListener

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mAdView.setVisibility(View.VISIBLE);
                }
            });
            fragmentFacebook = new FragmentFacebook();
            fragmentInstagram = new FragmentInstagram();
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            addTabs(viewPager);

            tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
            tabLayout.setupWithViewPager(viewPager);

            //toobar & backpress
            android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Free Social Video");
            toolbar.setSubtitle("Downloader");
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            toolbar.setSubtitleTextColor(getResources().getColor(R.color.white));
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white));
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //What to do on back clicked
                    onBackPressed();
                }
            });

        } catch (IndexOutOfBoundsException e) {
            Log.e("ExceptionError", " = " + e.getMessage());
        } catch (IllegalArgumentException e) {
            Log.e("ExceptionError", " = " + e.getMessage());
        } catch (ActivityNotFoundException e) {
            Log.e("ExceptionError", " = " + e.getMessage());
        } catch (SecurityException e) {
            Log.e("ExceptionError", " = " + e.getMessage());
        } catch (IllegalStateException e) {
            Log.e("ExceptionError", " = " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e("ExceptionError", " = " + e.getMessage());
        } catch (OutOfMemoryError e) {
            Log.e("ExceptionError", " = " + e.getMessage());
        } catch (RuntimeException e) {
            Log.e("ExceptionError", " = " + e.getMessage());
        } catch (Exception e) {
            Log.e("ExceptionError", " = " + e.getMessage());
        } finally {
            Log.e("ExceptionError", " = Finally");
        }

    }

    private void addTabs(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FragmentFacebook(), "Facebook");
        adapter.addFrag(new FragmentInstagram(), "Instagram");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
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
                //drawer.openDrawer(GravityCompat.START);
                return true;
        }        //noinspection SimplifiableIfStatement

        if (id == R.id.setting) {
            startActivity(new Intent(this, SettingActivity.class));

        } else if (id == R.id.download) {
            startActivity(new Intent(WebForm.this, Launch.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
