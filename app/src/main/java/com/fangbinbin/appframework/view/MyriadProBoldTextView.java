package com.fangbinbin.appframework.view;

import com.fangbinbin.appframework.tools.SetFontFace;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/**
 * MyriadProBoldTextView
 *
 * TextView with text in MyriadProBold font.
 */

public class MyriadProBoldTextView extends TextView {

    private String font="fonts/Roboto-Bold.ttf";
    private Typeface mTypeface;

    public MyriadProBoldTextView(Context context) {
        super(context);
        mTypeface=SetFontFace.getFont();
        this.setTypeface(mTypeface, Typeface.BOLD);
        if(Build.VERSION.RELEASE.equals("2.3.7")){
            this.setGravity(Gravity.LEFT);
        }
    }

    public MyriadProBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTypeface=SetFontFace.getFont();
        this.setTypeface(mTypeface, Typeface.BOLD);
        if(Build.VERSION.RELEASE.equals("2.3.7")){
            this.setGravity(Gravity.LEFT);
        }
    }

    public MyriadProBoldTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTypeface=SetFontFace.getFont();
        this.setTypeface(mTypeface, Typeface.BOLD);
        if(Build.VERSION.RELEASE.equals("2.3.7")){
            this.setGravity(Gravity.LEFT);
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}