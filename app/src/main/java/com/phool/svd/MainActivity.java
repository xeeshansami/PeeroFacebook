package com.phool.svd;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {
    private AdView mAdView;
    private AdRequest adRequest;
    private InterstitialAd mInterstitial;
    ProgressBar loadingIndicator;
    FrameLayout frameLayout;
    LinearLayout linearLayout;
    TextView textView;

    private DownloadedVideo downloaded_video_fragment;

    android.support.v4.app.FragmentTransaction ftst;
    android.support.v4.app.FragmentManager fmst;
    Dialog dialog,dialog2,dialog3;

//    int images[]={
//            R.drawable.share,
//            R.drawable.share,
//            R.drawable.share,
//            R.drawable.bk1,
//            R.drawable.bk2,
//            R.drawable.bk3,
//    };

    private AlertDialog alertDialog, alertDialogs;
    Button facebook,share,rate,quit;
    ViewFlipper viewFlipper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        facebook=findViewById(R.id.facebook);
        linearLayout=findViewById(R.id.linear);
        share=findViewById(R.id.share);
        rate=findViewById(R.id.rate);
        quit=findViewById(R.id.quit);
        frameLayout=findViewById(R.id.rootview);
//        viewFlipper = (ViewFlipper) findViewById(R.id.flipper);

        textView=findViewById(R.id.txt2);
        downloaded_video_fragment= new DownloadedVideo();
        dialog = new Dialog(MainActivity.this);
        // dialog.setTitle("               Please Wait");
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(getLayoutInflater().inflate(R.layout.custom, null));


        MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.banner_ad_unit_id));
        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        mAdView = (AdView) findViewById(R.id.adView_banner);

        adRequest = new AdRequest.Builder()
////                .addTestDevice("DF3AC08CDD630177F1719EF8950F138D")
                .build();
        mAdView.loadAd(adRequest);

        frameLayout.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
//        viewFlipper.setVisibility(View.VISIBLE);


        // loop for creating ImageView's
//        for (int i = 0; i < images.length; i++) {
//            // create the object of ImageView
//            ImageView imageView = new ImageView(this);
//            imageView.setImageResource(images[i]); // set image in ImageView
//            viewFlipper.addView(imageView); // add the created ImageView in ViewFlipper
//        }
        // Declare in and out animations and load them using AnimationUtils class
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        // set the animation type's to ViewFlipper
//        viewFlipper.setInAnimation(in);
//        viewFlipper.setOutAnimation(out);
        // set interval time for flipping between views
//        viewFlipper.setFlipInterval(3000);
        // set auto start for flipping between views
//        viewFlipper.setAutoStart(true);


        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,Launch.class);
                startActivity(i);
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = createShareForecastIntent();
                startActivity(shareIntent);
            }
        });
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="
                                + getPackageName())));




            }
        });
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });



    }






    public void ads() {
        if (isOnline()) {
            if (!MainActivity.this.isFinishing()){
                dialog.show();
            }

            mInterstitial = null;


            Display display = ((WindowManager) getSystemService(getApplication().WINDOW_SERVICE)).getDefaultDisplay();
            int width = display.getWidth();
            int height = display.getHeight();
            Log.v("width", width + "");
            dialog.getWindow().setLayout((6 * width) / 6, (4 * height) / 25);

            AdRequest adRequestInter = new AdRequest.Builder().build();
            mInterstitial = new InterstitialAd(this);
            mInterstitial.setAdUnitId(getResources().getString(R.string.intersitial_as_unit_id));

            mInterstitial.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    if (mInterstitial != null) {
                        if (mInterstitial.isLoaded()) {

                            loadingIndicator.setVisibility(View.GONE);
                            dialog.dismiss();
                            //loadingIndicator.draw();
                            {
                                {
                                    mInterstitial.show();
                                }
                            }
                        }
                    }
                }
            });
            mInterstitial.loadAd(adRequestInter);
        }
    }





    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }

        return false;
    }



    private Intent createShareForecastIntent() {
        String textThatYouWantToShare =
                "https://play.google.com/store/apps/details?id=" + getPackageName();
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setChooserTitle("Share via")
                .setType("text/plain")
                .setText(textThatYouWantToShare)
                .getIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;

    }
//    private void setFlipperImage(int res) {
//        Log.i("Set Filpper Called", res+"");
//        ImageView image = new ImageView(getApplicationContext());
//        image.setBackgroundResource(res);
//        viewFlipper.addView(image);
//    }


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

        frameLayout.setVisibility(View.VISIBLE);
        switch (item.getItemId()) {
            case android.R.id.home:
                //drawer.openDrawer(GravityCompat.START);
                return true;
        }        //noinspection SimplifiableIfStatement

        if (id == R.id.setting) {
            startActivity(new Intent(this, SettingActivity.class));

        }
        else if (id == R.id.download) {
            frameLayout.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
//            viewFlipper.setVisibility(View.GONE);
            //frameLayout.setVisibility(View.VISIBLE);
            fmst = getSupportFragmentManager();
            ftst = fmst.beginTransaction();
            ftst.replace(R.id.rootview, downloaded_video_fragment, "MAIN_FRAGMENT");
            ftst.commit();

        }


        return super.onOptionsItemSelected(item);
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        if(getSupportFragmentManager().findFragmentById(R.id.rootview) != null) {
            getSupportFragmentManager()
                    .beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.rootview)).commit();
        }
        frameLayout.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
//        viewFlipper.setVisibility(View.VISIBLE);
        // network_stream();
        //Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();



        if (doubleBackToExitPressedOnce) {
            //network_stream();
             super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);

    }

}
