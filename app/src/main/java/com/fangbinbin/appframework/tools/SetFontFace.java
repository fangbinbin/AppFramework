package com.fangbinbin.appframework.tools;

import android.content.Context;
import android.graphics.Typeface;

/**
 * SetFontFace
 *
 * Creates typeface from assets.
 */

public class SetFontFace {

    static Typeface face;
    static Typeface faceBold;

    public SetFontFace(){

    }

    public void setFont(String font, Context context){
        SetFontFace.face = Typeface.createFromAsset(context.getAssets(),
                font);
    }

    public void setFontBold(String fontBold, Context context){
        SetFontFace.faceBold = Typeface.createFromAsset(context.getAssets(),
                fontBold);
    }

    public static Typeface getFont(){
        return face;
    }

    public static Typeface getFontBold(){
        return faceBold;
    }

}