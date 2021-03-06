package com.fangbinbin.appframework.utils;

import java.io.File;
import java.util.List;
import java.util.UUID;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

import com.fangbinbin.appframework.tools.FileDir;
import com.fangbinbin.appframework.R;
//import com.cloverstudio.spika.lazy.ImageLoader;
import com.fangbinbin.appframework.utils.Const;
import com.fangbinbin.appframework.utils.Logger;
import com.fangbinbin.appframework.utils.Preferences;

/**
 * SpikaApp
 *
 * Basic Application class, holds references to often used single instance
 * objects and methods related to application like application background check.
 */

public class FrameworkApp extends Application {

    private static FrameworkApp sInstance;

    private Typeface mTfMyriadPro;
    private Typeface mTfMyriadProBold;
    private Preferences mPreferences;
    public static boolean gOpenFromBackground;
    private FileDir mFileDir;
    private String mBaseUrl;

    private TranslateAnimation mSlideOutLeft;
    private TranslateAnimation mSlideOutRight;
    private TranslateAnimation mSlideInLeft;
    private TranslateAnimation mSlideInRight;

    private final TranslateAnimation mSlideFromTop = new TranslateAnimation(
            TranslateAnimation.RELATIVE_TO_PARENT, 0,
            TranslateAnimation.RELATIVE_TO_PARENT, 0,
            TranslateAnimation.RELATIVE_TO_SELF, (float) -1.0,
            TranslateAnimation.RELATIVE_TO_SELF, (float) 0);

    private final TranslateAnimation mSlideOutTop = new TranslateAnimation(
            TranslateAnimation.RELATIVE_TO_PARENT, 0,
            TranslateAnimation.RELATIVE_TO_PARENT, 0,
            TranslateAnimation.RELATIVE_TO_SELF, (float) 0,
            TranslateAnimation.RELATIVE_TO_SELF, (float) -1.0);

    public static TranslateAnimation getSlideFromTop() {
        return sInstance.mSlideFromTop;
    }

    public static TranslateAnimation getSlideOutTop() {
        return sInstance.mSlideOutTop;
    }

    private int mTransport;

    public static FrameworkApp getInstance() {
        return sInstance;
    }

    private LocalBroadcastManager mLocalBroadcastManager;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mPreferences = new Preferences(this);
        mPreferences.clearFlagsForTutorialEachBoot(getApplicationContext()
                .getPackageName());
        gOpenFromBackground = true;
        mFileDir = new FileDir(this);

        //ImageLoader.initImageLoaderInstance(this);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

        // Create typefaces
        mTfMyriadPro = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Regular.ttf");
        mTfMyriadProBold = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Bold.ttf");

        setTransportBasedOnScreenDensity(42);

        // Example interpolator; could use linear or accelerate interpolator
        // instead
        final AccelerateDecelerateInterpolator accDecInterpolator = new AccelerateDecelerateInterpolator();
        final LinearInterpolator linearInterpolator = new LinearInterpolator();
        final int slidingDuration = getResources().getInteger(
                R.integer.sliding_duration);

        // Set up animations
        mSlideInLeft = new TranslateAnimation(-mTransport, 0, 0, 0);
        mSlideInLeft.setDuration(slidingDuration);
        // mSlideInLeft.setFillAfter(true); // hmm not sure
        mSlideInLeft.setFillEnabled(false);
        mSlideInLeft.setInterpolator(linearInterpolator);

        mSlideOutRight = new TranslateAnimation(0, mTransport, 0, 0);
        mSlideOutRight.setDuration(slidingDuration);
        mSlideOutRight.setFillAfter(true);
        mSlideOutRight.setFillEnabled(true);
        mSlideOutRight.setInterpolator(linearInterpolator);

        mSlideOutLeft = new TranslateAnimation(0, -mTransport, 0, 0);
        mSlideOutLeft.setDuration(slidingDuration);
        mSlideOutLeft.setInterpolator(linearInterpolator);

        mSlideInRight = new TranslateAnimation(mTransport, 0, 0, 0);
        mSlideInRight.setDuration(slidingDuration);
        mSlideInRight.setFillAfter(true);
        mSlideInRight.setFillEnabled(true);
        mSlideInRight.setInterpolator(linearInterpolator);

        mSlideFromTop.setFillAfter(true);
        mSlideFromTop.setFillEnabled(true);
        mSlideFromTop.setDuration(this.getResources().getInteger(
                android.R.integer.config_mediumAnimTime));
        mSlideFromTop.setInterpolator(linearInterpolator);

        mSlideOutTop.setDuration(this.getResources().getInteger(
                android.R.integer.config_mediumAnimTime));
        mSlideOutTop.setInterpolator(linearInterpolator);

        String strUUID = UUID.randomUUID().toString();
        Logger.debug("uuid", strUUID);

        mBaseUrl=mPreferences.getUserServerURL();
    }

    public static Typeface getTfMyriadPro() {
        return sInstance.mTfMyriadPro;
    }

    public static void setTfMyriadPro(Typeface tfMyriadPro) {
        sInstance.mTfMyriadPro = tfMyriadPro;
    }

    public static Typeface getTfMyriadProBold() {
        return sInstance.mTfMyriadProBold;
    }

    public static void setTfMyriadProBold(Typeface tfMyriadProBold) {
        sInstance.mTfMyriadProBold = tfMyriadProBold;
    }

    public static TranslateAnimation getSlideOutLeft() {
        return sInstance.mSlideOutLeft;
    }

    public static void setSlideOutLeft(TranslateAnimation slideOutLeft) {
        sInstance.mSlideOutLeft = slideOutLeft;
    }

    public static TranslateAnimation getSlideOutRight() {
        return sInstance.mSlideOutRight;
    }

    public static void setSlideOutRight(TranslateAnimation slideOutRight) {
        sInstance.mSlideOutRight = slideOutRight;
    }

    public static TranslateAnimation getSlideInLeft() {
        return sInstance.mSlideInLeft;
    }

    public static void setSlideInLeft(TranslateAnimation slideInLeft) {
        sInstance.mSlideInLeft = slideInLeft;
    }

    public static TranslateAnimation getSlideInRight() {
        return sInstance.mSlideInRight;
    }

    public static void setSlideInRight(TranslateAnimation slideInRight) {
        sInstance.mSlideInRight = slideInRight;
    }

    public static int getTransport() {
        return sInstance.mTransport;
    }

    public static void setTransport(int transport) {
        sInstance.mTransport = transport;
    }

    /**
     * Sets sliding transport based on screen density and required offset width
     * <p>
     * Depends on width of side offset in dp
     *
     * @param sideWidthInDp
     */
    public static void setTransportBasedOnScreenDensity(int sideWidthInDp) {
        WindowManager wm = (WindowManager) sInstance
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        double sideWidthInPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, sideWidthInDp, sInstance
                        .getResources().getDisplayMetrics());

        double transportRate = 1 - sideWidthInPx / metrics.widthPixels;
        sInstance.mTransport = (int) Math.floor(metrics.widthPixels
                * transportRate);
    }

    public static class ForegroundCheckAsync extends
            AsyncTask<Context, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Context... params) {
            final Context context = params[0];
            return isAppOnForeground(context);
        }

        private boolean isAppOnForeground(Context context) {
            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                    .getRunningAppProcesses();
            if (appProcesses == null) {
                return false;
            }
            final String packageName = context.getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        && appProcess.processName.equals(packageName)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Checks whether this app has mobile or wireless connection
     *
     * @return
     */
    public static boolean hasNetworkConnection() {
        boolean hasConnectedWifi = false;
        boolean hasConnectedMobile = false;

        final ConnectivityManager connectivityManager = (ConnectivityManager) sInstance
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo[] netInfo = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    hasConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    hasConnectedMobile = true;
        }
        boolean hasNetworkConnection = hasConnectedWifi || hasConnectedMobile;
        return hasNetworkConnection;
    }

    public static Preferences getPreferences() {
        return sInstance.mPreferences;
    }

    public static void setPreferences(Preferences preferences) {
        sInstance.mPreferences = preferences;
    }

    public static void clearCache() {
        File cache = sInstance.getCacheDir();
        cache.delete();
        //ImageLoader.getImageLoader().clearCache();
        sInstance.mFileDir.clear();
    }

    public static FileDir getFileDir() {
        return sInstance.mFileDir;
    }

    public static void setFileDir(FileDir fileDir) {
        sInstance.mFileDir = fileDir;
    }

    public static LocalBroadcastManager getLocalBroadcastManager() {
        return sInstance.mLocalBroadcastManager;
    }

    public static void setLocalBroadcastManager(
            LocalBroadcastManager localBroadcastManager) {
        sInstance.mLocalBroadcastManager = localBroadcastManager;
    }

    public void setBaseUrl(String url, String name){
        getPreferences().setUserServerURL(url);
        if(name != null && !name.equals("")){
            getPreferences().setUserServerName(name);
        }else{
            getPreferences().setUserServerName(url);
        }
        mBaseUrl=getPreferences().getUserServerURL();
    }

    public String getBaseUrl(){
        return mBaseUrl+"/";
    }

    public String getBaseUrlWithApi(){
        return mBaseUrl+"/"+Const.API_FOLDER;
    }

    public String getBaseUrlWithSufix(String sufix){
        return mBaseUrl+"/"+ Const.API_FOLDER +sufix;
    }
}