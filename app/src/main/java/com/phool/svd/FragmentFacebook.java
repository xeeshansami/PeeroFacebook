package com.phool.svd;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.phool.svd.MainContent.Launch;

import java.io.File;

import static android.content.Context.VIBRATOR_SERVICE;
import static com.google.android.gms.internal.zzahn.runOnUiThread;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FragmentFacebook#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentFacebook extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    int REQUEST_ID_MULTIPLE_PERMISSIONS = 123;
    String fbdata, fburl;
    WebView mWebView;
    private InterstitialAd interstitial;
    private boolean history_on_close_key, vibrate_when_finish, clear_cache, vibrate_on_download_error,
            show_finsih_noti, show_progress_noti;
    File mBaseFolderPath;
    RelativeLayout facebook_layout;
    int bytes_total;
    private DownloadedVideo downloaded_video_fragment;
    String Foldername;
    private int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1234;
    private DownloadManager manager = null;
    private static long lastDownload = -1L;
    private SharedPreferences mSharedPrefs;
    private Button fab;
    ProgressBar progress;
    private AdView mAdView;
    private AdRequest adRequest, ar;
    private InterstitialAd mInterstitial;
    ProgressBar loadingIndicator;
    android.support.v4.app.FragmentTransaction ftst;
    android.support.v4.app.FragmentManager fmst;
    boolean doubleBackToExitPressedOnce = false;
    FrameLayout frameLayout;
    String fbVideoData, fbVidoId;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView missedcalled_recyler;
    View view;

    public FragmentFacebook() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentFacebook.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentFacebook newInstance(String param1, String param2) {
        FragmentFacebook fragment = new FragmentFacebook();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_facebook, container, false);

            frameLayout = view.findViewById(R.id.rootview);

            frameLayout.setVisibility(View.GONE);
            downloaded_video_fragment = new DownloadedVideo();
            progress = (ProgressBar) view.findViewById(R.id.progress);
            facebook_layout = view.findViewById(R.id.facebook_layout);
            fbdata = null;
            fburl = null;


//        MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.banner_ad_unit_id));
////        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
////        mAdView = (AdView) findViewById(R.id.adView_banner);
////
////        adRequest = new AdRequest.Builder()
////////                .addTestDevice("DF3AC08CDD630177F1719EF8950F138D")
////                .build();
////        mAdView.loadAd(adRequest);
            Foldername = "FreeSVD";
            mBaseFolderPath = new File(Environment.getExternalStorageDirectory(), Foldername);
            //mBaseFolderPath = Environment.DIRECTORY_MOVIES;
            if (!mBaseFolderPath.exists() && mBaseFolderPath.isDirectory()) {

                Toast.makeText(getContext(), "Folder Created", Toast.LENGTH_SHORT).show();
                new File(String.valueOf(mBaseFolderPath)).mkdir();
            }
            fab = (Button) view.findViewById(R.id.main_fab);
            fab.setVisibility(View.GONE);
            progress.getProgressDrawable().setColorFilter(
                    Color.WHITE, PorterDuff.Mode.SRC_IN);
            mWebView = (WebView) view.findViewById(R.id.webView);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mWebView.getSettings().setMediaPlaybackRequiresUserGesture(true);
            }
//        webSettings.setBuiltInZoomControls(true);
//        webSettings.setSupportZoom(true);
//        webSettings.setUseWideViewPort(true);
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            mWebView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
            mWebView.getSettings().setAppCachePath("/data/data/" + getActivity().getPackageName() + "/cache");
            mWebView.getSettings().setAppCacheEnabled(true);
            //        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            mWebView.getSettings().setDomStorageEnabled(true);

            if (isOnline()) {
                mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            } else {
                Log.i("", "404 no Network Found");
            }
            Facebook("");
            mWebView.setVisibility(View.VISIBLE);


            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fbdata != null || fburl != null) {
                        if (checkStoragePermission()) {
                            try {
                                String mFilePath = "file://" + mBaseFolderPath + "/" + fburl + ".mp4";
                                Uri downloadUri = Uri.parse(fbdata);
                                DownloadManager.Request req = new DownloadManager.Request(downloadUri);
                                //req.setDestinationInExternalPublicDir(mBaseFolderPath, mFilePath);
                                req.setDestinationUri(Uri.parse(mFilePath));
                                req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                DownloadManager dm = (DownloadManager) getActivity().getSystemService(getActivity().getApplicationContext().DOWNLOAD_SERVICE);
                                dm.enqueue(req);
                                Toast.makeText(getActivity(), "Download Started", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), "Download Failed: " + e.toString(), Toast.LENGTH_LONG).show();
                            }

                        } else {
                            requestStoragePermission();
                        }
                    }
                }
            });

            mWebView.canGoBack();
            mWebView.setOnKeyListener(new View.OnKeyListener() {

                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK
                            && event.getAction() == MotionEvent.ACTION_UP
                            && mWebView.canGoBack()) {
                        mWebView.goBack();
                        return true;
                    }
                    return false;
                }
            });
//            view.setFocusableInTouchMode(true);
//            view.requestFocus();
//            view.setOnKeyListener(new View.OnKeyListener() {
//                @Override
//                public boolean onKey(View v, int keyCode, KeyEvent event) {
//                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                        switch (keyCode) {
//                            case KeyEvent.KEYCODE_BACK:
//                                if (mWebView.isFocused() && mWebView.canGoBack()) {
//                                    mWebView.goBack();
//                                    return true;
////                                } else {
////                                    getActivity().finish();
//                                }
//                        }
//
//                    }
//                    return false;
//                }
//            });
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

        return view;
    }

    private boolean checkStoragePermission() {
        return ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;


    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

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

    public void Facebook(final String videoURL) {
        try {
            fab.setVisibility(View.GONE);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
            mWebView.getSettings().setBuiltInZoomControls(true);
            mWebView.getSettings().setDisplayZoomControls(true);
            mWebView.getSettings().setUseWideViewPort(true);
            mWebView.getSettings().setLoadWithOverviewMode(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
            }
            mWebView.addJavascriptInterface(this, "FBDownloader");

            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    mWebView.loadUrl("javascript:(function() { "
                            + "var el = document.querySelectorAll('div[data-sigil]');"
                            + "for(var i=0;i<el.length; i++)"
                            + "{"
                            + "var sigil = el[i].dataset.sigil;"
                            + "if(sigil.indexOf('inlineVideo') > -1){"
                            + "delete el[i].dataset.sigil;"
                            + "var jsonData = JSON.parse(el[i].dataset.store);"
                            + "el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\");');"
                            + "}" + "}" + "})()");
                }

                @Override
                public void onLoadResource(WebView view, String url) {
                    mWebView.loadUrl("javascript:(function prepareVideo() { "
                            + "var el = document.querySelectorAll('div[data-sigil]');"
                            + "for(var i=0;i<el.length; i++)"
                            + "{"
                            + "var sigil = el[i].dataset.sigil;"
                            + "if(sigil.indexOf('inlineVideo') > -1){"
                            + "delete el[i].dataset.sigil;"
                            + "console.log(i);"
                            + "var jsonData = JSON.parse(el[i].dataset.store);"
                            + "el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\",\"'+jsonData['videoID']+'\");');"
                            + "}" + "}" + "})()");
                    mWebView.loadUrl("javascript:( window.onload=prepareVideo;"
                            + ")()");
                }
            });

            mWebView.setVisibility(View.VISIBLE);
//        mAdView.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);
            if (videoURL != null && videoURL.contains("facebook")) {
                mWebView.loadUrl(videoURL);
            } else {
                mWebView.loadUrl("https://www.facebook.com");
            }
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

    @JavascriptInterface
    public void processVideo(final String vidData, final String vidID) {
        fbVideoData = null;
        fbVidoId = null;
        fbVideoData = vidData;
        fbVidoId = vidID;
        Log.i("facebookVideo", vidData);
        Log.i("facebookVideo", vidID);
        if (vidData != null && vidID != null) {
            facebookDownloadButton();
            showIntersitial();
        } else {
            Toast.makeText(getActivity(), "Please run or resume the video", Toast.LENGTH_SHORT).show();
        }
    }

//    @JavascriptInterface
//    public void PlayVideo(final String vidData, final String vidID) {
//        fbVideoData = null;
//        fbVidoId = null;
//        fbVideoData = vidData;
//        fbVidoId = vidID;
//        Log.i("facebookVideo", vidData);
//        Log.i("facebookVideo", vidID);
//        if (vidData != null || vidID != null) {
//            facebookPlayButton(vidID);
//        }
//    }

    private void facebookPlayButton(String videoURL) {
        Intent intent = new Intent(getActivity(), WebVideo.class);
        intent.putExtra("videoUrl", videoURL);
        startActivity(intent);
    }

    public void showIntersitial() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MobileAds.initialize(getActivity().getApplication(), getResources().getString(R.string.app_id));
                    AdRequest adRequest = new AdRequest.Builder()
                            .addTestDevice("8B3B217C5524B71E3E3CBA2A407F395E")
                            .build();
                    // Prepare the Interstitial Ad
                    interstitial = new InterstitialAd(getActivity());
                    // Insert the Ad Unit ID
                    interstitial.setAdUnitId(getString(R.string.intersitial_as_unit_id));
                    interstitial.loadAd(adRequest);


                    // Prepare an Interstitial Ad Listener
                    interstitial.setAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {


                            // Code to be executed when an ad finishes loading.
                            if (interstitial.isLoaded()) {
                                interstitial.show();
                            } else {
//                                facebookDownloadButton();
                            }
                        }

                        @Override
                        public void onAdFailedToLoad(int errorCode) {
                            // Code to be executed when an ad request fails.
//                            facebookDownloadButton();
                            Log.e("errorinAds", " = " + errorCode + "");
                        }

                        @Override
                        public void onAdOpened() {
                            // Code to be executed when the ad is displayed.
                        }

                        @Override
                        public void onAdClicked() {
                            // Code to be executed when the user clicks on an ad.
                        }

                        @Override
                        public void onAdLeftApplication() {
                            // Code to be executed when the user has left the app.
                        }

                        @Override
                        public void onAdClosed() {
                            // Code to be executed when the interstitial ad is closed.
//                            facebookDownloadButton();
                        }
                    });
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

    //when click on facebook video play button
    public void facebookDownloadButton() {
        try {
            final AlertDialog alertDialog;
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setCancelable(true);

            // ...Irrelevant code for customizing the buttons and title
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.download_popup, null);
            dialogBuilder.setView(dialogView);
            alertDialog = dialogBuilder.create();
            alertDialog.setCanceledOnTouchOutside(true);
            Display display = ((WindowManager) getActivity().getSystemService(getActivity().getApplication().WINDOW_SERVICE)).getDefaultDisplay();
            int width = display.getWidth();
            int height = display.getHeight();
            Log.v("width", width + "");
            alertDialog.getWindow().setLayout((6 * width) / 6, (4 * height) / 11);

            Button cancel = dialogView.findViewById(R.id.fb_cancel_btn);
            Button watchBtn = dialogView.findViewById(R.id.fb_watch_btn);
            Button downloadBtn = dialogView.findViewById(R.id.fb_download_btn);

            watchBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    facebookPlayButton(fbVideoData);

                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    fbVideoData = null;
                    fbVidoId = null;
                }
            });
            downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        alertDialog.dismiss();
                        if (checkStoragePermission()) {
                            fbDowbloaderFunc(fbVideoData, fbVidoId);
                        } else {
                            requestStoragePermission();
                        }
                    } catch (SecurityException e) {
                    }


                }
            });
            alertDialog.show();
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

    public void fbDowbloaderFunc(String fbDownloadUrl, String fbDownloadFilename) {
        downloader(fbDownloadUrl, fbDownloadFilename);
    }

    public void downloader(String mUrlForVideo1, String filename2) {
        if (checkStoragePermission()) {
            String filename;
            if (mUrlForVideo1.contains("insta")) {
                filename = mUrlForVideo1.substring(mUrlForVideo1.lastIndexOf('/') + 1);
            } else if (mUrlForVideo1.contains("fb") || mUrlForVideo1.contains("facebook")) {
                filename = filename2;
            } else {
                filename = mUrlForVideo1.substring(mUrlForVideo1.lastIndexOf('/') + 1);
            }
            try {
                Log.i("downloadUrl", mUrlForVideo1);
                manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse((mUrlForVideo1)));
                Snackbar.make(facebook_layout, "Downloading Start", 2000)
                        .setActionTextColor(Color.RED)
                        .show();
                request.setDescription("In progress");
                request.setTitle(filename);
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Foldername, filename + ".mp4");
                lastDownload = manager.enqueue(request);
                mSharedPrefs = getActivity().getSharedPreferences("prefix", Context.MODE_PRIVATE);
                mSharedPrefs = getActivity().getSharedPreferences("prefix", Context.MODE_PRIVATE);
                SharedPreferences.Editor mEditor = mSharedPrefs.edit();
                mEditor.putLong("lastdownload", (lastDownload));
                mEditor.apply();
                mEditor.commit();

                Cursor cursor = manager.query(new DownloadManager.Query().setFilterById(lastDownload));

//                insert_query = " Insert into duration (name,duration,total) values('" + filename + "','" + lastDownload + "','" + bytes_total + "')";
//                Log.e("Insert query", insert_query);
//                db.insert_update(insert_query);
                if (cursor == null) {
                    Snackbar.make(facebook_layout, "Download not found", 2000)
                            .setActionTextColor(Color.RED)
                            .show();
                } else {
                    cursor.moveToFirst();
                    cursor.moveToFirst();

                    int bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    Log.d(getClass().getName(), "STATUS_PROGRESS : " + (bytesDownloaded / bytesTotal) * 100);
                    //Toast.makeText(this, ""+downint, Toast.LENGTH_SHORT).show();
                    // do your thing...

                    Log.d(getClass().getName(), "COLUMN_ID: " +
                            cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID)));
                    Log.d(getClass().getName(), "COLUMN_BYTES_DOWNLOADED_SO_FAR: " +
                            cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)));
                    Log.d(getClass().getName(), "COLUMN_LAST_MODIFIED_TIMESTAMP: " +
                            cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP)));
                    Log.d(getClass().getName(), "COLUMN_LOCAL_URI: " +
                            cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
                    Log.d(getClass().getName(), "COLUMN_STATUS: " +
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)));
                    Log.d(getClass().getName(), "COLUMN_REASON: " +
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON)));

                    Log.d(getClass().getName(), "STATUS_MESSAGE : " + statusMessage(cursor));
                }
            } catch (Exception ff) {
                System.out.println("Error = " + ff);
//                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();
            }
//            Log.i("mUrlForVideo", "From Server" + mUrlForVideo);
        } else {
            requestStoragePermission();
        }
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            Cursor c = manager.query(new DownloadManager.Query().setFilterById(lastDownload));


            if (c == null) {
                Toast.makeText(getActivity().getApplicationContext(), "Download not found!", Toast.LENGTH_LONG).show();
            } else {
                if (c.moveToFirst()) {
                    int bytes_downloaded = c.getInt(c
                            .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    bytes_total = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    Log.d(getClass().getName(), "STATUS_PROGRESS : " + (bytes_downloaded / bytes_total) * 1000);
                    Log.d(getClass().getName(), "COLUMN_ID: " +
                            c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID)));
                    Log.d(getClass().getName(), "COLUMN_BYTES_DOWNLOADED_SO_FAR: " +
                            c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)));
                    Log.d(getClass().getName(), "COLUMN_LAST_MODIFIED_TIMESTAMP: " +
                            c.getLong(c.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP)));
                    Log.d(getClass().getName(), "COLUMN_LOCAL_URI: " +
                            c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
                    Log.d(getClass().getName(), "COLUMN_STATUS: " +
                            c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS)));
                    Log.d(getClass().getName(), "COLUMN_REASON: " +
                            c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON)));

                    Log.d(getClass().getName(), "STATUS_MESSAGE : " + statusMessage(c));
                    Toast.makeText(getActivity().getApplicationContext(), statusMessage(c), Toast.LENGTH_LONG).show();
                    if (statusMessage(c).equals("Download complete!")) {
                        if (vibrate_when_finish) {
//                            Toast.makeText(getApplicationContext(), "Compelete If"+ vibrate_when_finish, Toast.LENGTH_LONG).show();
                            shakeItBaby();
                        } else {
//                            Toast.makeText(getApplicationContext(), "Compelete Else"+ vibrate_when_finish, Toast.LENGTH_LONG).show();

                        }

                    } else if (statusMessage(c).equals("Download failed!")) {
                        if (vibrate_on_download_error) {
//                            Toast.makeText(getApplicationContext(), "Error If"+ vibrate_on_download_error, Toast.LENGTH_LONG).show();
                            shakeItBaby();
                        } else {
//                            Toast.makeText(getApplicationContext(), "Error Else"+ vibrate_on_download_error, Toast.LENGTH_LONG).show();

                        }

                    }
                }

                c.close();
            }
//            Cursor c = mgr.query(new DownloadManager.Query().setFilterById(lastDownload));
//            if (c.moveToFirst()) {
//                int x = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
//                switch (x) {
//                    case DownloadManager.STATUS_PAUSED:
//
//                    case DownloadManager.STATUS_PENDING:
//
//                    case DownloadManager.STATUS_RUNNING:
//
//                        break;
//                    case DownloadManager.STATUS_SUCCESSFUL:
//
//                        break;
//                    case DownloadManager.STATUS_FAILED:
//                        //TODO: retry download
//                        break;
//                }
//            }
        }
    };

    private void shakeItBaby() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE)).vibrate(150);
        }
    }

    private String statusMessage(Cursor c) {
        String msg = "???";

        switch (c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            case DownloadManager.STATUS_FAILED:
                msg = "Download failed!";
                break;

            case DownloadManager.STATUS_PAUSED:
                msg = "Download paused!";
                break;

            case DownloadManager.STATUS_PENDING:
                msg = "Download pending!";
                break;

            case DownloadManager.STATUS_RUNNING:
                msg = "Download in progress!";
                break;

            case DownloadManager.STATUS_SUCCESSFUL:
                msg = "Download complete!";
                break;

            default:
                msg = "Download is nowhere in sight";
                break;
        }

        return (msg);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                checkStoragePermission();
            }
        }
    }



/*    @Override
    public void onBackPressed() {
        if (mWebView.getVisibility() == View.VISIBLE) {
            // dont pass back button action
            if (mWebView.canGoBack() && mWebView.isFocused()) {
                mWebView.goBack();
            } else {
                mWebView.loadUrl("");
                mWebView.setVisibility(View.GONE);
                startActivity(new Intent(getActivity(), Launch.class));
                getActivity().finish();
            }
            return;
        } else if (mWebView.getVisibility() == View.GONE) {
//            getFragmentManager().popBackStack();
            if (doubleBackToExitPressedOnce) {
                // pass back button action
                getActivity().finishAffinity();
                return;
            }
        } else if (doubleBackToExitPressedOnce) {
            getActivity().finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(getActivity(), "Press one more time to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 1000);

    }*/

}
