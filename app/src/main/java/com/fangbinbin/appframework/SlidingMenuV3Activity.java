package com.fangbinbin.appframework;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.fangbinbin.appframework.view.SlidingMenuV3;

public class SlidingMenuV3Activity extends Activity
{
    private SlidingMenuV3 mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sliding_menu_v3);
        mMenu = (SlidingMenuV3) findViewById(R.id.id_menu);

    }

    public void toggleMenu(View view)
    {
        mMenu.toggle();
    }
}
