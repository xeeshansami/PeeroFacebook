package com.phool.svd;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import static com.makeramen.roundedimageview.RoundedImageView.TAG;

/**
 * Created by azhar on 3/8/2017.
 */

public class SettingFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    public CheckBoxPreference history_clear, clear_cache;
    Preference clear_cookies;
    private AlertDialog alertDialog;
    Preference privacy, quitPreferences, cachepref,file;
    private SharedPreferences preferences;
    CheckBoxPreference vibrate_when_finish, vibrate_on_download_error, show_finsih_noti, show_progress_noti, thirdparty;
    private SQLiteDatabase mDb;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.prefere);
//        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
//        PreferenceScreen prefScre = getPreferenceScreen();
//        int count = prefScre.getPreferenceCount();
//        for(int i = 0; i<count;i++){
//            Preference preference = prefScre.getPreference(i);
//            if(!(preference instanceof CheckBoxPreference)){
//                String value = sharedPreferences.getString(preference.getKey(),"");
//                setPreferenceSummary(preference,value);
//            }
//        }
        privacy = (Preference) findPreference(getResources().getString(R.string.privacy));
        file = (Preference) findPreference(getResources().getString(R.string.file));
        file.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
//                Bundle bundle=getActivity().getIntent().getExtras();
//                String foldername =bundle.getString("folderlocation");
                network_stream("FacebookVideos");
                return true;
            }
        });
        privacy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Uri uri = Uri.parse("https://downloaderforsocial.blogspot.com/2019/02/phool-dev-built-free-social-media-video.html");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;
            }
        });
//       listPreference  = (ListPreference) findPreference(getResources().getString(R.string.home_page_key));
//        if(listPreference.getValue()==null) {
//            // to ensure we don't get a null value
//            // set first value by default
//            listPreference.setValueIndex(0);
//        }
//        listPreference.setSummary(listPreference.getValue().toString());
//        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
//
//                // Set the value as the new value
//                listPreference.setValue(newValue.toString());
//
//                preference.setSummary(newValue.toString());
//                return true;
//            }
//        });
        BookmarkDBHelper dbHelper = new BookmarkDBHelper(getActivity());
        mDb = dbHelper.getWritableDatabase();


        quitPreferences = (Preference) findPreference(getResources().getString(R.string.clearAllHistorykey));


//        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
//        PreferenceScreen prefScre = getPreferenceScreen();
//        int count = prefScre.getPreferenceCount();
//        for(int i = 0; i<count;i++){
//            Preference preference = prefScre.getPreference(i);
//            if(!(preference instanceof CheckBoxPreference)){
//                String value = sharedPreferences.getString(preference.getKey(),"");
//                setPreferenceSummary(preference,value);
//            }
//        }
//        String locale = this.getResources().getConfiguration().locale.getDisplayName();
////        String LAnglocale = locale.getDefault().getDisplayName();
//        String lan = Locale.getDefault().getDisplayLanguage();
//        String langCode = Locale.getDefault().getLanguage(); //to get usual language code
//
//        System.out.println(lan + "\n" + langCode + "\n" + locale);
//        homePeference.setSummary(locale);

//        getPreferenceManager().findPreference(getResources().getString(R.string.reset_key))
//                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//
//                    public boolean onPreferenceClick(final Preference preference) {
//                        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
//                        alertDialog.setMessage("Are you sure you want to reset all settings to default?");
//                        alertDialog.setCancelable(true);
//                        alertDialog.setButton("Yes", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
//                                SharedPreferences.Editor editor = preferences.edit();
//                                editor.clear();
//                                editor.apply();
//                                boolean test = preferences.getBoolean(getResources().getString(R.string.history_on_close_key), true);
//                                System.out.println("RESET" + test);
//
//                            }
//                        });
//                        alertDialog.setButton2("No", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        });
//                        alertDialog.show();
//                        return false;
//                    }
//                });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        Preference pref = findPreference(key);
//        if (pref != null) {
//            if(!(pref instanceof CheckBoxPreference)){
//                String value = sharedPreferences.getString(pref.getKey(),"");
//                setPreferenceSummary(pref,value);
//            }
//        }
//        String locale = this.getResources().getConfiguration().locale.getDisplayName();
////        String LAnglocale = locale.getDefault().getDisplayName();
//        String lan = Locale.getDefault().getDisplayLanguage();
//        String langCode = Locale.getDefault().getLanguage(); //to get usual language code
//
//        System.out.println(lan + "\n" + langCode + "\n" + locale);
//        if (key.equals(getResources().getString(R.string.language_key))) {
//            languagePeference.setSummary(locale);
//        }
        if (key.equals(getResources().getString(R.string.history_on_close_key))) {
            boolean test = sharedPreferences.getBoolean(getResources().getString(R.string.history_on_close_key), true);
            //Do whatever you want here. This is an example.
            if (test) {

                Toast.makeText(getContext(), "Changes Applied", Toast.LENGTH_LONG).show();
//                testPref.setSummary("Enabled");
                System.out.println(test);
            } else {

                Toast.makeText(getContext(), "Changes Applied", Toast.LENGTH_LONG).show();
//                testPref.setSummary("Disabled");
                System.out.println(test);
            }

            if (key.equals(getResources().getString(R.string.clear_cache_key))) {
                boolean clear_cache = sharedPreferences.getBoolean(getResources().getString(R.string.clear_cache_key), true);
                //Do whatever you want here. This is an example.
                if (clear_cache) {

                    Toast.makeText(getContext(), "Changes Applied", Toast.LENGTH_LONG).show();
//                testPref.setSummary("Enabled");
                    System.out.println(clear_cache);
                } else {

                    Toast.makeText(getContext(), "Changes Applied", Toast.LENGTH_LONG).show();
//                testPref.setSummary("Disabled");
                    System.out.println(clear_cache);
                }


            }

        }
    }

    //    private void setPreferenceSummary(Preference preference, String value) {
//
//        if(preference instanceof ListPreference){
//            ListPreference listPreference = (ListPreference)preference;
//            int preIndex = listPreference.findIndexOfValue(value);
//            if(preIndex >= 0){
//                listPreference.setSummary(listPreference.getEntries()[preIndex]);
//            }
//        }
//    }
    private void deleteAllHistroy() {
        mDb.delete(HistoryContract.Histroylist.TABLE_NAME, null, null);
        mDb.execSQL("delete from " + HistoryContract.Histroylist.TABLE_NAME);
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean clear_cache = preferences.getBoolean(getResources().getString(R.string.clear_cache_key), true);
        if (clear_cache) {
//                testPref.setSummary("Enabled");
            System.out.println(clear_cache);
        } else {
//                testPref.setSummary("Disabled");
            System.out.println(clear_cache);
        }
        boolean test = preferences.getBoolean(getResources().getString(R.string.history_on_close_key), true);

        if (test) {
//            testPref.setSummary("Enabled");
        } else {
//            testPref.setSummary("Disabled");
        }
    }

    @SuppressWarnings("deprecation")
    public static void clearCookies(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d(TAG, "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            Log.d(TAG, "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    void network_stream(String folderlocation) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setCancelable(false);

// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.folder_location, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();


        Button apply = (Button) dialogView.findViewById(R.id.folder_apply);
        EditText text = dialogView.findViewById(R.id.edittext_folder);
        Button cancel = (Button) dialogView.findViewById(R.id.cancel);
        text.setText(folderlocation);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });


        alertDialog.show();

    }
}
