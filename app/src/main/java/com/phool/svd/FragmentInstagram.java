package com.phool.svd;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.phool.svd.MainContent.Launch;

import java.io.File;

import static android.content.Context.VIBRATOR_SERVICE;
import static com.google.android.gms.internal.zzahn.runOnUiThread;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FragmentInstagram#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentInstagram extends Fragment {
    private InterstitialAd interstitial;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String Instadata, Instaurl;
    WebView mWebView;
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
    private FloatingActionButton fab;
    ProgressBar progress;
    private AdView mAdView;
    private AdRequest adRequest, ar;
    private InterstitialAd mInterstitial;
    ProgressBar loadingIndicator;
    View view;
    String urlInsta;
    android.support.v4.app.FragmentTransaction ftst;
    android.support.v4.app.FragmentManager fmst;
    boolean doubleBackToExitPressedOnce = false;
    FrameLayout frameLayout;
    String InstaVideoData, InstaVidoId;
    int REQUEST_ID_MULTIPLE_PERMISSIONS = 1234;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static FragmentInstagram inst;

    public FragmentInstagram() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentInstagram.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentInstagram newInstance(String param1, String param2) {
        FragmentInstagram fragment = new FragmentInstagram();
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

    public static FragmentInstagram instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @SuppressLint("WrongViewCast")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_instagram, container, false);
            frameLayout = view.findViewById(R.id.rootview);

            frameLayout.setVisibility(View.GONE);
            downloaded_video_fragment = new DownloadedVideo();
            progress = (ProgressBar) view.findViewById(R.id.progress);
            facebook_layout = view.findViewById(R.id.facebook_layout);
            Instadata = null;
            Instaurl = null;
            Foldername = "FreeSVD";
            mBaseFolderPath = new File(Environment.getExternalStorageDirectory(), Foldername);
            //mBaseFolderPath = Environment.DIRECTORY_MOVIES;
            if (!mBaseFolderPath.exists() && mBaseFolderPath.isDirectory()) {

                Toast.makeText(getContext(), "Folder Created", Toast.LENGTH_SHORT).show();
                new File(String.valueOf(mBaseFolderPath)).mkdir();
            }
            fab = view.findViewById(R.id.main_fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkStoragePermission()) {

                        InstaDownloadButton();
                        showIntersitial();
                    } else {
                        requestStoragePermission();
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
            progress.getProgressDrawable().setColorFilter(
                    Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
            mWebView = (WebView) view.findViewById(R.id.webView);
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        webSettings.setBuiltInZoomControls(true);
//        webSettings.setSupportZoom(true);
//        webSettings.setUseWideViewPort(true);
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            mWebView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
            mWebView.getSettings().setAppCachePath("/data/data/" + getActivity().getPackageName() + "/cache");
            mWebView.getSettings().setAppCacheEnabled(true);
            //        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webSettings.setDomStorageEnabled(true);

            if (isOnline()) {
                mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            } else {
                Log.i("", "404 no Network Found");
            }
            Instagram("www.instagram.com");
            mWebView.setVisibility(View.VISIBLE);
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

    public void Instagram(final String videoURL) {
        try {
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
            mWebView.getSettings().setBuiltInZoomControls(true);
            mWebView.getSettings().setDisplayZoomControls(true);
            mWebView.getSettings().setUseWideViewPort(true);
            mWebView.getSettings().setLoadWithOverviewMode(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mWebView.getSettings().setMediaPlaybackRequiresUserGesture(true);
            }

            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {

                }

                @Override
                public void onLoadResource(WebView view, String url) {
                    System.out.println("FinalUrlOn = " + url);
                    String str4 = "https://www.dailymotion.com/player/metadata/video";
                    String liveUrl = "https://cdn.liveleak.com/80281E/";
                    String twimgUrl = "https://video.twimg.com/ext_tw_video/";
                    String vimeoUrl = "https://player.vimeo.com/video/";
                    String instaUrl = "insta";

                    if (url.contains("flv") ||
                            url.contains("3gp") ||
                            url.contains("mov") ||
                            url.contains("mpeg") ||
                            url.contains("wmv") ||
                            url.contains("avi") ||
                            url.contains("dv") ||
                            url.contains("mp4") ||
                            url.toLowerCase().contains(str4.toLowerCase()) ||
                            url.toLowerCase().contains(instaUrl.toLowerCase()) ||
                            url.toLowerCase().contains(liveUrl.toLowerCase()) ||
                            url.toLowerCase().contains(twimgUrl.toLowerCase()) ||
                            url.toLowerCase().contains(vimeoUrl.toLowerCase())) {
                        String filename = url.substring(url.lastIndexOf('/') + 1);

                        Log.i("onLoadResource()", "url = " + url + "\n" + filename);

                        if (url.toLowerCase().contains("twimg") &&
                                (url.contains("m3u8") ||
                                        url.contains("flv") ||
                                        url.contains("3gp") ||
                                        url.contains("mov") ||
                                        url.contains("mpeg") ||
                                        url.contains("wmv") ||
                                        url.contains("avi") ||
                                        url.contains("dv") ||
                                        url.contains("mp4"))) {
//                        PublicUrl = url;
                            fab.setVisibility(View.VISIBLE);
//                        Log.i("FinalUrltwimgUrl", PublicUrl);
                        }
                        if (url.toLowerCase().contains("live") &&
                                (url.contains("mp4") ||
                                        url.contains("flv") ||
                                        url.contains("3gp") ||
                                        url.contains("mov") ||
                                        url.contains("mpeg") ||
                                        url.contains("wmv") ||
                                        url.contains("avi") ||
                                        url.contains("dv") ||
                                        url.contains("mp4"))) {
//                        urlLive = url;
//                        Log.i("FinalUrlliveUrl", urlLive);
                        }
                        if (url.toLowerCase().contains("insta") && url.toLowerCase().contains("vp") &&
                                url.contains(".mp4") ||
                                url.contains(".flv") ||
                                url.contains(".3gp") ||
                                url.contains(".mov") ||
                                url.contains(".mpeg") ||
                                url.contains(".wmv") ||
                                url.contains(".avi") ||
                                url.contains(".dv") ||
                                url.contains(".mp4")) {
                            urlInsta = url;
                            fab.setVisibility(View.VISIBLE);
                            Log.i("FinalUrlinstaUrl", urlInsta);
                        }
                        if (url.toLowerCase().contains(str4.toLowerCase())) {
//                        urldailymotion = url;
//                        executeAsyncTask();
//                        System.out.print("dailymotion url = " + urldailymotion);
                        }
                        if (url.toLowerCase().contains("vimeo") && (url.toLowerCase().contains("mp4"))) {
//                        urlVimeo = url;
//                        Log.i("FinalUrlvimeoUrl", urlVimeo);
                        }
                        super.onLoadResource(view, url);
                    } else {
                    }

                }

            });

            mWebView.setVisibility(View.VISIBLE);
//        mAdView.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);
            if (videoURL != null && videoURL.contains("insta")) {
                mWebView.loadUrl("https://www.instagram.com");
            } else {
                mWebView.loadUrl("https://www.instagram.com");
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
//                                InstaDownloadButton();
                            }
                        }

                        @Override
                        public void onAdFailedToLoad(int errorCode) {
                            // Code to be executed when an ad request fails.
//                            InstaDownloadButton();
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
//                            InstaDownloadButton();
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
    public void InstaDownloadButton() {
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
                    try {
                        alertDialog.dismiss();
                        showIntersitial();
                        if (urlInsta.toLowerCase().contains("insta") && urlInsta.toLowerCase().contains("vp") &&
                                urlInsta.contains(".mp4") ||
                                urlInsta.contains(".flv") ||
                                urlInsta.contains(".3gp") ||
                                urlInsta.contains(".mov") ||
                                urlInsta.contains(".mpeg") ||
                                urlInsta.contains(".wmv") ||
                                urlInsta.contains(".avi") ||
                                urlInsta.contains(".dv") ||
                                urlInsta.contains(".mp4")) {
                            facebookPlayButton(urlInsta);
                        } else {
                            Toast.makeText(getActivity(), "Please run or resume the video", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "Please run or resume the video", Toast.LENGTH_SHORT).show();
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
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    InstaVideoData = null;
                    InstaVidoId = null;
                }
            });
            downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        alertDialog.dismiss();
                        if (checkStoragePermission()) {
                            try {
                                showIntersitial();
                                if (urlInsta.toLowerCase().contains("insta") && urlInsta.toLowerCase().contains("vp") &&
                                        urlInsta.contains(".mp4") ||
                                        urlInsta.contains(".flv") ||
                                        urlInsta.contains(".3gp") ||
                                        urlInsta.contains(".mov") ||
                                        urlInsta.contains(".mpeg") ||
                                        urlInsta.contains(".wmv") ||
                                        urlInsta.contains(".avi") ||
                                        urlInsta.contains(".dv") ||
                                        urlInsta.contains(".mp4")) {
                                    fbDowbloaderFunc(urlInsta, urlInsta);
                                } else {
                                    Toast.makeText(getActivity(), "Please run or resume the video", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getActivity(), "Please run or resume the video", Toast.LENGTH_SHORT).show();
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
        try {
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

}
