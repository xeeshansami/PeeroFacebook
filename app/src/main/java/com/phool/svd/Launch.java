package com.phool.svd;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.phool.svd.AllVideosFragment.videofolder;

public class Launch extends AppCompatActivity {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    FragmentFacebook fragmentFacebook;
    FragmentInstagram fragmentInstagram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);
        fragmentFacebook = new FragmentFacebook();
        fragmentInstagram = new FragmentInstagram();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        addTabs(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        //toobar & backpress
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("SVD");
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
            startActivity(new Intent(Launch.this, videofolder.class));
            finish();
        } else if (id == R.id.action_refresh) {
            startActivity(new Intent(Launch.this, Launch.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
