package com.phool.svd;

import android.*;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;


public class WebVideo extends AppCompatActivity {
    private VideoEnabledWebView webView;
    private VideoEnabledWebChromeClient webChromeClient;
    String FacebookUrl;
    String fbdata, fburl;
    WebView mWebView;
    private boolean history_on_close_key, vibrate_when_finish, clear_cache, vibrate_on_download_error,
            show_finsih_noti, show_progress_noti;
    File mBaseFolderPath;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        setContentView(R.layout.web_video);
        //toobar & backpress
        Bundle extras = getIntent().getExtras();
        FacebookUrl = extras.getString("videoUrl");
        String filename = FacebookUrl.substring(FacebookUrl.lastIndexOf('/') + 1);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar_video);
        toolbar.setTitle(filename);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
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

        Foldername = "FacebookVideos";
        mBaseFolderPath = new File(Environment.getExternalStorageDirectory(), Foldername);
        //mBaseFolderPath = Environment.DIRECTORY_MOVIES;
        if (!mBaseFolderPath.exists() && mBaseFolderPath.isDirectory()) {

            Toast.makeText(this, "Folder Created", Toast.LENGTH_SHORT).show();
            new File(String.valueOf(mBaseFolderPath)).mkdir();
        }
        // Save the web view
        webView = findViewById(R.id.webView);

        // Initialize the VideoEnabledWebChromeClient and set event handlers
        View nonVideoLayout = findViewById(R.id.nonVideoLayout); // Your own view, read class comments
        ViewGroup videoLayout = (ViewGroup) findViewById(R.id.videoLayout); // Your own view, read class comments
        //noinspection all
        View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null); // Your own view, read class comments
        webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView) // See all available constructors...

        {
            // Subscribe to standard events, such as onProgressChanged()...
            @Override
            public void onProgressChanged(WebView view, int progress) {
                // Your code...
            }
        };

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                downloader(url);
            }
        });
        webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback() {
            @Override
            public void toggledFullscreen(boolean fullscreen) {
                // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                if (fullscreen) {
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14) {
                        //noinspection all
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    }
                } else {
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14) {
                        //noinspection all
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                }

            }
        });
        webView.setWebChromeClient(webChromeClient);
        // Call private class InsideWebViewClient
        webView.setWebViewClient(new InsideWebViewClient());
        // Navigate anywhere you want, but consider that this classes have only been tested on YouTube's mobile site
        webView.loadUrl(FacebookUrl);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class InsideWebViewClient extends WebViewClient {
        @Override

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(FacebookUrl);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        try{
        SplashScreen splashScreen = new SplashScreen();
        // Notify the VideoEnabledWebChromeClient, and handle it ourselves if it doesn't handle it
        if (!webChromeClient.onBackPressed()) {
            if (webView.canGoBack()) {
                webView.goBack();
                webChromeClient.onBackPressed();
//                splashScreen.showIntersitial();
            } else {
                // Standard back button implementation (for example this could close the app)
                super.onBackPressed();
            }
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
    private boolean checkStoragePermission() {
        return ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;


    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

    }


    public void downloader(String mUrlForVideo1) {
        if (checkStoragePermission()) {
            String filename;
            filename = mUrlForVideo1.substring(mUrlForVideo1.lastIndexOf('/') + 1);
            try {
                Log.i("downloadUrl", mUrlForVideo1);
                manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse((mUrlForVideo1)));
                Snackbar.make(findViewById(android.R.id.content), "Downloading Start", 2000)
                        .setActionTextColor(Color.RED)
                        .show();
                request.setDescription("In progress");
                request.setTitle(filename);
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Foldername, filename + ".mp4");
                lastDownload = manager.enqueue(request);
                mSharedPrefs = getSharedPreferences("prefix", Context.MODE_PRIVATE);
                mSharedPrefs = getSharedPreferences("prefix", Context.MODE_PRIVATE);
                SharedPreferences.Editor mEditor = mSharedPrefs.edit();
                mEditor.putLong("lastdownload", (lastDownload));
                mEditor.apply();
                mEditor.commit();

                Cursor cursor = manager.query(new DownloadManager.Query().setFilterById(lastDownload));

//                insert_query = " Insert into duration (name,duration,total) values('" + filename + "','" + lastDownload + "','" + bytes_total + "')";
//                Log.e("Insert query", insert_query);
//                db.insert_update(insert_query);
                if (cursor == null) {
                    Snackbar.make(findViewById(android.R.id.content), "Download not found", 2000)
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
                Toast.makeText(getApplicationContext(), "Download not found!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getApplicationContext(), statusMessage(c), Toast.LENGTH_LONG).show();
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
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(150);
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


}