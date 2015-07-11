package com.fangbinbin.appframework;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

    private Button btnSignInActivity;
    private Button btnPasscodeActivity;
    private Button btnSlidingMenuActivity;
    private Button btnSlidingMenuV2Activity;
    private Button btnSlidingMenuV3Activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignInActivity = (Button)findViewById(R.id.btnSignInActivity);
        btnSignInActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        btnPasscodeActivity = (Button)findViewById(R.id.btnPasscodeActivity);
        btnPasscodeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PasscodeActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        btnSlidingMenuActivity = (Button)findViewById(R.id.btnSlidingMenuActivity);
        btnSlidingMenuActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SlidingMenuActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        btnSlidingMenuV2Activity = (Button)findViewById(R.id.btnSlidingMenuV2Activity);
        btnSlidingMenuV2Activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SlidingMenuV2Activity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        btnSlidingMenuV3Activity = (Button)findViewById(R.id.btnSlidingMenuV3Activity);
        btnSlidingMenuV3Activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SlidingMenuV3Activity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
