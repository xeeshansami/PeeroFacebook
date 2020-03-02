package com.phool.svd;


import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileFilter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadedVideo extends Fragment {
    private Cursor videoCursorActivity;
    private String filename, title, album;
    private ArrayList<VideoSongs> videoActivitySongsList = new ArrayList<VideoSongs>();
    DownloadedVideoAdapter videoSongsAdapter;
    private RecyclerView recyclerView;
    private int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1230;
    ProgressDialog progressDialog;
    ProgressDialog prodialog;
    File file[] = null;

    public DownloadedVideo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_downloaded_video, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(false);

        videoSongsAdapter = new DownloadedVideoAdapter(getActivity(), videoActivitySongsList);
        recyclerView = (RecyclerView) view.findViewById(R.id.videoRecycler);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(videoSongsAdapter);

        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {

            if (checkStoragePermission()) {
                new GetAudioListAsynkTask(getActivity()).execute();


            } else {
                requestStoragePermission();
            }

        } else {
            new GetAudioListAsynkTask(getActivity()).execute();

        }
        return view;
    }


    private void getDownloadedFiels() {
        String path = Environment.getExternalStorageDirectory().toString() + "/FacebookVideos";
        Log.i("Path of Files", "Downloaded" + path);
        File f = new File(path);
        file = f.listFiles(new AudioFilter());

        if (file != null) {

            for (int i = 0; i < file.length; i++) {
                if (file[i].getName().endsWith(".mp4")) {
                    Log.d("Files", "FileName:" + file[i].getName());
                    getFiles(file[i].getName());
                }


            }

        } else {
            Log.i("Downloaded videos", "No downloads files here");
        }
    }

    public class AudioFilter implements FileFilter {

        // only want to see the following audio file types
        private String[] extension = {".3gp", "3g2",
                ".mp4", ".m4p", ".m4v", ".mxf", ".m2v", ".wav",
                ".flv", ".webm", ".flac", ".f4v", "f4p", "f4a", "f4b", ".ogv", ".gif"
                , ".mng", ".au", ".svi", ".snd", ".mid", ".midi", ".kar"
                , ".mga", ".avi", ".rm", ".rmvb", ".asf",
                ".aif", ".aiff", ".aifc", ".oga",
                ".spx", ".mkv", ".m4a", ".amv", ".nsv"
                , "wmv", ".avi", ".mov", ".qt", ".yuv", ".vob"
        };

        @Override
        public boolean accept(File pathname) {

            // if we are looking at a directory/file that's not hidden we want to see it so return TRUE
            if ((pathname.isDirectory() || pathname.isFile()) && !pathname.isHidden()) {
                return true;
            }

            // loops through and determines the extension of all files in the directory
            // returns TRUE to only show the audio files defined in the String[] extension array
            for (String ext : extension) {
                if (pathname.getName().toLowerCase().endsWith(ext) || pathname.getName().toUpperCase().endsWith(ext)) {
                    return true;
                }
            }

            return false;
        }
    }

    private class GetAudioListAsynkTask extends AsyncTask<Void, Void, Boolean> {
        private Context context;

        @Override
        protected void onPreExecute() {
            progressDialog.setCancelable(false);
            prodialog = progressDialog.show(getActivity(), "", "Loading....", false);
        }

        public GetAudioListAsynkTask(Context context) {

            this.context = context;
        }


        @Override
        protected Boolean doInBackground(Void... voids) {

            try {

                getDownloadedFiels();
                prodialog.dismiss();


                return true;
            } catch (Exception e) {
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean result) {

//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }

            prodialog.dismiss();

            recyclerView.setAdapter(videoSongsAdapter);

            videoSongsAdapter.notifyDataSetChanged();
        }
    }

    private void getFiles(String songsName) {


        String selection = MediaStore.Video.VideoColumns.DATA + " like?";
        String[] projection = {MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.VideoColumns.DISPLAY_NAME,
                MediaStore.Video.VideoColumns.ARTIST,
                MediaStore.Video.VideoColumns.RESOLUTION,
                MediaStore.Video.VideoColumns.ALBUM,
                MediaStore.Video.VideoColumns.DESCRIPTION,
                MediaStore.Video.VideoColumns.TITLE,
                MediaStore.Video.VideoColumns.DURATION,
                MediaStore.Video.VideoColumns.SIZE};

        String[] selectionArgs = {"%" + songsName + "%"};
//        String[] selectionArgs=new String[]{"%Swag-Se-Swagat-Song--Tiger-Zinda-Hai--Salman-Khan--Katrina-Kaif.mp4%"};

        Log.i("Files", "Video files" + Arrays.toString(selectionArgs));
        videoCursorActivity = getActivity().getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
//        System.out.println("MAPing "+dirPath + "=media="+ MediaStore.Video.Media.EXTERNAL_CONTENT_URI );

        int totalvideoscount = videoCursorActivity.getCount();
//        folders_list.clear();
        if (videoCursorActivity != null) {

            while (videoCursorActivity.moveToNext()) {
                filename = videoCursorActivity.getString(
                        videoCursorActivity.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                title = videoCursorActivity.getString(
                        videoCursorActivity.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                String dura = videoCursorActivity.getString(
                        videoCursorActivity.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                String artist = videoCursorActivity.getString(
                        videoCursorActivity.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
                album = videoCursorActivity.getString(
                        videoCursorActivity.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM));
                String desc = videoCursorActivity.getString(
                        videoCursorActivity.getColumnIndexOrThrow(MediaStore.Video.Media.DESCRIPTION));
                String res = videoCursorActivity.getString(
                        videoCursorActivity.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION));
                int size = videoCursorActivity.getInt(
                        videoCursorActivity.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                int videoId = videoCursorActivity.getInt(videoCursorActivity.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                Uri albumArtUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoId);
                Log.i("", "" + albumArtUri);

                Log.i("", "Total SOngs" + totalvideoscount);
                Log.i("Title", "" + title);
                Log.i("DATA", "" + filename);


                VideoSongs songs = new VideoSongs();
                songs.setData(filename);
                songs.setImage(albumArtUri.toString());
                songs.setDuration(milliSecondsToTimer(Long.parseLong(dura)));
                songs.setName(title);
                songs.setArtist(artist);
                songs.setSize(getFileSize(size));
                videoActivitySongsList.add(songs);
            }
        }
        videoCursorActivity.close();
    }

    public static String getFileSize(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

// Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
// Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

// Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

// return timer string
        return finalTimerString;
    }


    private boolean checkStoragePermission() {
        return ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;


    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (checkStoragePermission()) {

                    new GetAudioListAsynkTask(getActivity()).execute();
                } else {
//            if (checkStoragePermission()) {

//                    new GetAudioListAsynkTask(this).execute();
//            } else {
                    requestStoragePermission();
//            }
                }
            } else {
                checkStoragePermission();
            }
        }
    }
}