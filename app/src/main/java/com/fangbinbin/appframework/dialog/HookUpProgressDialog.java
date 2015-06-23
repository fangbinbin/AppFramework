package com.fangbinbin.appframework.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fangbinbin.appframework.R;

/**
 * HookUpProgressDialog
 *
 * Shows custom HookUp progress dialog with animation.
 */

public class HookUpProgressDialog extends Dialog {

    private TextView mTvProgressTitle;
    private ImageView mIvAnimation;
    private AnimationDrawable mCloverAnimation;

    public HookUpProgressDialog(Context context) {
        super(context, R.style.Theme_Transparent);

        this.setContentView(R.layout.progress_dialog);
        mTvProgressTitle = (TextView) this.findViewById(R.id.tvProgressTitle);
        mTvProgressTitle.setVisibility(View.GONE);
        this.setCancelable(false);
        mIvAnimation = (ImageView) findViewById(R.id.ivAnim);
        mCloverAnimation = (AnimationDrawable) mIvAnimation.getDrawable();

    }

    /**
     * Sets custom progress title
     *
     * @param progressTitle
     */
    public void setTitle(final String progressTitle) {
        if (mTvProgressTitle.getVisibility() == View.GONE) {
            mTvProgressTitle.setVisibility(View.VISIBLE);
            mTvProgressTitle.setText(progressTitle);
        }
    }

    /**
     * Shows progress dialog with custom progress title
     *
     * @param progressTitle
     */
    public void show(final String progressTitle) {
        if (mTvProgressTitle.getVisibility() == View.GONE) {
            mTvProgressTitle.setVisibility(View.VISIBLE);
            mTvProgressTitle.setText(progressTitle);
        }
        HookUpProgressDialog.this.show();
    }

    @Override
    public void show() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            public void run() {

                mCloverAnimation.start();

            }
        }, 10);
        super.show();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.v("Dialog focus change", "" + hasFocus);
        if (!hasFocus) this.dismiss();
        super.onWindowFocusChanged(hasFocus);
    }

}