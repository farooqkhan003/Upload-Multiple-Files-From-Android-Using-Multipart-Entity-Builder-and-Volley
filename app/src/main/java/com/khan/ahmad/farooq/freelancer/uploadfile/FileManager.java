package com.khan.ahmad.farooq.freelancer.uploadfile;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import java.io.File;

import Template.Template;

/**
 * Created by Farooq Khan on 14-Jul-16.
 */
public class FileManager {
    static final String[] imageType = {"jpg", "png", "jpeg"};
    static final String[] videoType = {"mp4"};
    static final String[] audioType = {"mp3"};
    private static File mImageDir, mVideoDir, mMiscDir;


    //Conversi size dari bytes ke human readable format seperti MB, GB dll
    public static String getSize(long bytes, boolean si) {
        Log.i("Size", bytes + "");
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    //Mendapatkan type file, untuk respon pada preview, 3 respon yang akan dihandle untuk projek ini
    public static int fileType(File file) {
        for (String type : imageType) {
            if (file.getName().toLowerCase().endsWith(type)) {
                return Template.Code.CAMERA_IMAGE_CODE;
            }
        }

        for (String type : videoType) {
            if (file.getName().toLowerCase().endsWith(type)) {
                return Template.Code.CAMERA_VIDEO_CODE;
            }
        }

        for (String type : audioType) {
            if (file.getName().toLowerCase().endsWith(type)) {
                return Template.Code.AUDIO_CODE;
            }
        }
        return Template.Code.FILE_MANAGER_CODE;

    }

    //Mendapatkan path dari uri
    public static String getPath(Context context, int type, Uri uri) {
        if (type == Template.Code.FILE_MANAGER_CODE || type == Template.Code.AUDIO_CODE) {
            return getPath(context, uri);
        } else {
            return uri.getPath();
        }
    }

    //Cek directory local distorage, jika belum ada, maka akan dibuat
    private static boolean checkDirectory() {
        File baseDir = new File(Environment.getExternalStorageDirectory(), "Andevindo");
        mImageDir = new File(baseDir.getPath() + File.separator + "Images");
        mVideoDir = new File(baseDir.getPath() + File.separator + "Videos");
        mMiscDir = new File(baseDir.getPath() + File.separator + "Misc");
        if (!baseDir.exists()) {
            if (!baseDir.mkdirs()) {

                return false;
            } else {


                if (!mImageDir.exists()) {
                    if (!mImageDir.mkdirs()) {

                        return false;
                    }
                }


                if (!mVideoDir.exists()) {
                    if (!mVideoDir.mkdirs()) {

                        return false;
                    }
                }


                if (!mMiscDir.exists()) {
                    if (!mMiscDir.mkdirs()) {

                        return false;
                    }
                }

            }
        }
        return true;
    }

    //Mendapatkan directory
    private static File getDirectory(int type) {
        Log.i("dir", checkDirectory() + "");
        if (checkDirectory()) {
            if (type == Template.Code.CAMERA_IMAGE_CODE) {
                return mImageDir;
            } else if (type == Template.Code.CAMERA_VIDEO_CODE) {
                return mVideoDir;
            } else {
                return mMiscDir;
            }
        } else {
            return null;
        }

    }

    //Set lokasi dan nama file untuk file hasil dari camera(foto dan video)
    public static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {
        File mediaFile;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        if (type == Template.Code.CAMERA_IMAGE_CODE) {
            mediaFile = new File(getDirectory(type).getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == Template.Code.CAMERA_VIDEO_CODE) {
            mediaFile = new File(getDirectory(type).getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
