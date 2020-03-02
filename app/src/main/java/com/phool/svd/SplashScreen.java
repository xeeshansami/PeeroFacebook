package com.phool.svd;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


public class SplashScreen extends AppCompatActivity {
    private int MY_PERMISSIONS_REQUEST_READANDWRITE_EXTERNAL_STORAGE = 1230;
    //Splash time 1sec
    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_splash);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (checkStoragePermission() && checkWritePermission()) {
                    doFunc();
                } else {
                    requestStoragePermission();
                }
            } else {
                if (isOnline()) {
                    doFunc();
                } else {
                    doFunc();
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


    public void doFunc() {
        //Intent from ParentActivity to Slides Activity
        final Intent intent = new Intent(getApplicationContext(), Sliders.class);
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(SPLASH_TIME_OUT);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } finally {
                    startActivity(intent);
                    finish();
                }
            }
        };
        thread.start();
    }
/*    public void doFunc() {
        *//* Create an Intent that will start the Menu-Activity. *//*
        Intent mainIntent = new Intent(SplashScreen.this, Sliders.class);
        SplashScreen.this.startActivity(mainIntent);
        SplashScreen.this.finish();
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READANDWRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isOnline()) {
                    doFunc();
                } else {
                    checkStoragePermission();
                    checkWritePermission();
                }
            }
        }
    }

    private boolean checkStoragePermission() {
        return ActivityCompat.checkSelfPermission(SplashScreen.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkWritePermission() {
        return ActivityCompat.checkSelfPermission(SplashScreen.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {

        ActivityCompat.requestPermissions(SplashScreen.this,
                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READANDWRITE_EXTERNAL_STORAGE);


    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected() && wifiNetwork.isAvailable()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected() && mobileNetwork.isAvailable()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected() && activeNetwork.isAvailable()) {
            return true;
        }

        return false;
    }
}