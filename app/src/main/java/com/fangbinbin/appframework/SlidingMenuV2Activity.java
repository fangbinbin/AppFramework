package com.fangbinbin.appframework;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.fangbinbin.appframework.view.SlidingMenu;


public class SlidingMenuV2Activity extends Activity {

    private SlidingMenu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sliding_menu_v2);
        mMenu = (SlidingMenu) findViewById(R.id.id_menu);
    }

    public void toggleMenu(View view)
    {
        mMenu.toggle();
    }
}
