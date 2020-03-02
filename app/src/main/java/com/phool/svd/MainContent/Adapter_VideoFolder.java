package com.phool.svd.MainContent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.phool.svd.R;
import com.phool.svd.onItemClickListener;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by duskysol on 1/5/2019.
 */

public class Adapter_VideoFolder extends RecyclerView.Adapter<Adapter_VideoFolder.ViewHolder> {
    private static WeakReference<Adapter_VideoFolder> wrActivity = null;
    private int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1234;
    private int lastPosition = -1;
    ArrayList<Model_Video> al_video;
    Context context;
    Activity activity;
    private android.app.AlertDialog alertDialog, alertDialogs;

    public Adapter_VideoFolder(Context context, ArrayList<Model_Video> al_video, Activity activity) {
        this.al_video = al_video;
        this.context = context;
        this.activity = activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView iv_image;
        RelativeLayout rl_select;
        public Button b3;
        onItemClickListener itemClickListener;

        public ViewHolder(View v) {
            super(v);
            iv_image = (ImageView) v.findViewById(R.id.iv_video_thumb);
            v.setOnClickListener(this);

        }

        public void setItemClickListener(onItemClickListener ic) {
            this.itemClickListener = ic;
        }

        @Override
        public void onClick(View v) {
            this.itemClickListener.onClick(v, getLayoutPosition());
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_video, parent, false);
        ViewHolder viewHolder1 = new ViewHolder(view);
        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(final ViewHolder Vholder, final int position) {
        Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
        Vholder.itemView.startAnimation(animation);
        lastPosition = position;
        Glide.with(context).load("file://" + al_video.get(position).getStr_thumb()).placeholder(R.drawable.video_icon).skipMemoryCache(false).into(Vholder.iv_image);
        Vholder.setItemClickListener(new onItemClickListener() {
            @Override
            public void onClick(View view, final int position) {
                try {
                    final String filename = al_video.get(position).getStr_thumb().substring(al_video.get(position).getStr_path().lastIndexOf("/") + 1);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                            .setTitle("Video : " + filename)
                            .setMessage("What would you like to do for this video?")
                            .setNegativeButton("Play", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String videoName = String.valueOf(al_video.get(position));
                                    String s = "file://" + al_video.get(position).getStr_thumb();
                                    Intent intent = new Intent(context, AndroidVideoPlayer.class);
                                    intent.putExtra("videoUri", s);
                                    intent.putExtra("title", videoName);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                    Log.i("videofilename", s);
                                    context.startActivity(intent);
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                            ContentResolver resolver = context.getContentResolver();
                                            Uri uri = getFileUri(context, al_video.get(position).getStr_path());
                                            int a = resolver.delete(uri, null, null);
                                            if (a > 0) {
                                                Toast.makeText(context, "Video" + filename + " deleted  successfully", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "Video" + filename + " not deleted  successfully", Toast.LENGTH_SHORT).show();
                                            }
//                                    String root = Environment.getExternalStorageDirectory().toString() + "/FacebookVideos/";
//                                    String root2 = Environment.getExternalStorageDirectory().toString() + "/DCIM/.thumbnails/";
//                                    File file = new File(root + filename);
//                                    File file2 = new File(root2 + filename);
//                                    deleteCache(context);
//                                    deleteDir(file);
//                                    Log.i("root", root);
//                                    Log.i("filename", filename);
//                                    deleteVideo(root + file);
//                                    file.delete();
//                                    file2.delete();
//                                    if (file.exists()) {
//                                        try {
//                                            file.getCanonicalFile().delete();
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//                                        if (file.exists()) {
//                                            context.getApplicationContext().deleteFile(file.getName());
//                                        }
//                                    }

//                                    File dir = context.getFilesDir();
//                                    File file = new File(dir, al_video.get(position).getStr_path());
//                                    File file2 = new File(dir,"/storage/emulated/0/DCIM/.thumbnails/"+"941397659402845");
//                                    file.delete();
//                                    file2.delete();
                                            Adapter_VideoFolder.this.al_video.remove(position);
                                            Adapter_VideoFolder.this.notifyDataSetChanged();
                                            dialog.dismiss();

                                    }catch (SecurityException e){}
                                }
                            }).setNeutralButton("Share", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent shareIntent = createShareForecastIntent(al_video.get(position).getStr_path());
                                    context.startActivity(Intent.createChooser(shareIntent, "Video: " + filename));
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
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
        });

    }

    @Override
    public int getItemCount() {
        return al_video.size();

    }

    private Intent createShareForecastIntent(String videofile) {

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("video/mp4");
        File fileToShare = new File(videofile);
        Uri uri = Uri.fromFile(fileToShare);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        return sharingIntent;
    }

    private void deleteVideo(String videoUrl) {
        File videoFile = new File(videoUrl);
        if (!videoFile.delete()) {
            Log.e("files", "Failed to delete " + videoUrl);
        } else {
            MediaScannerConnection.scanFile(context, new String[]{videoUrl}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });
        }
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
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


    public class AsyncDeleteVideos extends AsyncTask<List<Long>, Void, Void> {
        protected void onPreExecute() {
        }

        @SafeVarargs
        protected final Void doInBackground(List<Long>... args) {
            deleteVideos(args[0]);
            return null;
        }

        protected void onPostExecute(Void result) {

        }
    }

    public void deleteVideos(List<Long> videos) {
        for (Long video : videos) {
            this.context.getContentResolver().delete(ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, video.longValue()), null, null);
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private Uri getFileUri(Context context, String fullname) {
        // Note: check outside this class whether the OS version is >= 11
        Uri uri = null;
        Cursor cursor = null;
        ContentResolver contentResolver = null;

        try {
            contentResolver = context.getContentResolver();
            if (contentResolver == null)
                return null;
            uri = MediaStore.Files.getContentUri("external");
            String[] projection = new String[2];
            projection[0] = "_id";
            projection[1] = "_data";
            String selection = "_data = ? ";    // this avoids SQL injection
            String[] selectionParams = new String[1];
            selectionParams[0] = fullname;
            String sortOrder = "_id";
            cursor = contentResolver.query(uri, projection, selection, selectionParams, sortOrder);

            if (cursor != null) {
                try {
                    if (cursor.getCount() > 0) // file present!
                    {
                        cursor.moveToFirst();
                        int dataColumn = cursor.getColumnIndex("_data");
                        String s = cursor.getString(dataColumn);
                        if (!s.equals(fullname))
                            return null;
                        int idColumn = cursor.getColumnIndex("_id");
                        long id = cursor.getLong(idColumn);
                        uri = MediaStore.Files.getContentUri("external", id);
                    } else // file isn't in the media database!
                    {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("_data", fullname);
                        uri = MediaStore.Files.getContentUri("external");
                        uri = contentResolver.insert(uri, contentValues);
                    }
                } catch (Throwable e) {
                    uri = null;
                } finally {
                    cursor.close();
                }
            }
        } catch (Throwable e) {
            uri = null;
        }
        return uri;
    }

}
