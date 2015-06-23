package com.fangbinbin.appframework.management;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

/**
 * FileManagement
 *
 * Helper class for managing application's files.
 */

public class FileManagement {

    private static String TAG = "FileManagement";

    public static String _mainDirName = Environment
            .getExternalStorageDirectory().toString()
            + "/Android/data/com.fangbinbin.appframework/";

    private static String _cacheDirName = "downloads/images";
    // private static String _cameraCacheDirName = "camera_cache/images";
    public static File _cacheDir;
    public static File _cameraCacheDir;

    public static FileManagement _fileManagement;

    public FileManagement(Context context) {
        CreateCacheDir(context);
        _fileManagement = this;
    }

    public static boolean FileExists(String url) {

        String filename = md5(url) + ".jpg";

        File f = null;
        if (!filename.equals("")) {
            f = new File(_cacheDir, filename);
        } else {
            // TODO: Handle situation when file is not created!
        }

        return f.exists();
    }

    public static File CreateFile(String url, InputStream is)
            throws IOException {

        String filename = md5(url) + ".jpg";
        File f = null;
        if (!filename.equals("")) {
            if (_cacheDir == null) {
                Log.v(TAG, "null");
            }
            Log.v(TAG, _cacheDir.getPath());
            f = new File(_cacheDir, filename);
        } else {
            // TODO: Handle situation when file is not created!
        }

        Bitmap mBitmap = BitmapFactory.decodeStream(is);
        FileOutputStream os = new FileOutputStream(f);
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        os.flush();
        os.close();

        return f;
    }

    public static File GetFile(String filename) {
        filename = md5(filename) + ".jpg";
        File f = null;
        if (!filename.equals("")) {
            f = new File(_cacheDir, filename);
        } else {
            // TODO: Handle situation when file is not created!
        }

        return f;
    }

    private static void CreateCacheDir(Context context) {
        if (_cacheDir == null) {
            _cacheDir = new File(_mainDirName, _cacheDirName);
            if (!_cacheDir.exists()) {
                _cacheDir.mkdirs();
            }
        }
        if (_cameraCacheDir == null) {
			/*
			 * _cameraCacheDir = new File(_mainDirName, _cameraCacheDirName);
			 * if(!_cameraCacheDir.exists()){ _cameraCacheDir.mkdirs(); }
			 */
            File cacheDir = null;

            if (android.os.Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED))
                cacheDir = context.getExternalCacheDir();
            else
                cacheDir = context.getCacheDir();
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
                cacheDir.deleteOnExit();
            }

            _cameraCacheDir = cacheDir;
        }
    }

    public static String md5(String s) {
        if (s.equals("")) return "";
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(s.getBytes());
            byte[] a = digest.digest();
            int len = a.length;
            StringBuilder sb = new StringBuilder(len << 1);
            for (int i = 0; i < len; i++) {
                sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(a[i] & 0x0f, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
        return "";
    }
}