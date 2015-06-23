package com.fangbinbin.appframework.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fangbinbin.appframework.serverinteraction.ConnectionHandler;
import com.fangbinbin.appframework.management.UsersManagement;
import com.fangbinbin.appframework.utils.Utils;

/**
 * FileDir
 *
 * Used for managing files from application's file directory.
 */

public class FileDir {

    private File fileDir;

    public FileDir(Context context) {
        // Find the dir to save emoticons
        fileDir = context.getFilesDir();
        if (!fileDir.exists())
            fileDir.mkdirs();
    }

    public File getFile(String filename) {
        File file = new File(fileDir, filename);
        return file;
    }

    public Bitmap getBitmap(String filename) {
        Bitmap bitmap = null;
        File file = new File(fileDir, filename);
        if (file != null) {
            bitmap = decodeFile(file);
        }
        return bitmap;
    }

    public void saveFile(String filename, String url) {
        try {
            File file = new File(fileDir, filename);
            InputStream is = ConnectionHandler.httpGetRequest(url,
                    UsersManagement.getLoginUser().getId());
            OutputStream os = new FileOutputStream(file);
            Utils.copyStream(is, os);
            os.close();
            is.close();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    private Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();

            int REQUIRED_SIZE = 100;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            if (Utils.isOsVersionHigherThenGingerbread()) {
                o2.inPreferredConfig = Bitmap.Config.ARGB_8888;
            } else {
                o2.inPreferredConfig = Bitmap.Config.RGB_565;
            }
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clear() {
        File[] files = fileDir.listFiles();
        if (files == null)
            return;
        for (File f : files)
            f.delete();
    }

}