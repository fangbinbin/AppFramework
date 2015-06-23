package com.fangbinbin.appframework;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.fangbinbin.appframework.serverinteraction.ServerRequestHandler;
import com.fangbinbin.appframework.serverinteraction.ResultListener;
import com.fangbinbin.appframework.model.User;
//import com.cloverstudio.spika.extendables.SideBarActivity;
import com.fangbinbin.appframework.management.UsersManagement;
import com.fangbinbin.appframework.utils.Const;
import com.fangbinbin.appframework.utils.FrameworkApp;
import com.fangbinbin.appframework.utils.Preferences;
//import com.crittercism.app.Crittercism;

/**
 * SplashScreenActivity
 *
 * Displays splash screen for 2 seconds.
 */

public class SplashScreenActivity extends Activity {

    private String mSavedEmail;
    private String mSavedPassword;
    public static SplashScreenActivity sInstance = null;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SplashScreenActivity.sInstance = this;
        setContentView(R.layout.activity_splash_screen);

		/* Initiate Crittercism */
        //Crittercism.init(getApplicationContext(), Const.CRITTERCISM_APP_ID);

        new ServerRequestHandler();
        // new UsersManagement();

        /*if (FrameworkApp.hasNetworkConnection()) {

            if (checkIfUserSignIn()) {
                mSavedEmail = FrameworkApp.getPreferences().getUserEmail();
                mSavedPassword = FrameworkApp.getPreferences().getUserPassword();

                mUser = new User();

                ServerRequestHandler.authAsync(mSavedEmail, mSavedPassword, new AuthListener(), SplashScreenActivity.this, false);
            } else {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        startActivity(new Intent(SplashScreenActivity.this,
                                SignInActivity.class));
                        finish();
                    }
                }, 2000);
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreenActivity.this,
                            RecentActivityActivity.class);
                    intent.putExtra(Const.SIGN_IN, true);
                    SplashScreenActivity.this.startActivity(intent);
                    Toast.makeText(SplashScreenActivity.this,
                            getString(R.string.no_internet_connection),
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            }, 2000);
        }*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this,
                        MainActivity.class);
                SplashScreenActivity.this.startActivity(intent);
                finish();
            }
        }, 2000);
    }

    private boolean checkIfUserSignIn() {
        boolean isSessionSaved = false;
        Preferences prefs = FrameworkApp.getPreferences();
        if (prefs.getUserEmail() == null && prefs.getUserPassword() == null) {
            isSessionSaved = false;
        } else if (prefs.getUserEmail().equals("")
                && prefs.getUserPassword().equals("")) {
            isSessionSaved = false;
        } else {
            isSessionSaved = true;
        }
        return isSessionSaved;
    }

    private void signIn(User u) {

        UsersManagement.setLoginUser(u);
        UsersManagement.setToUser(u);
        UsersManagement.setToGroup(null);

        boolean openPushNotification = getIntent().getBooleanExtra(
                Const.PUSH_INTENT, false);

        /*Intent intent = new Intent(SplashScreenActivity.this,
                RecentActivityActivity.class);
        if (openPushNotification)  {
            intent = getIntent();
            intent.setClass(SplashScreenActivity.this,
                    RecentActivityActivity.class);
        }*/

        Intent intent = new Intent(SplashScreenActivity.this,
                MainActivity.class);
        if (openPushNotification)  {
            intent = getIntent();
            intent.setClass(SplashScreenActivity.this,
                    MainActivity.class);
        }

        //parse URI hookup://user/[ime korisnika] and hookup://group/[ime grupe]
        Uri userUri = getIntent().getData();
        //If opened from link
        if (userUri != null) {
            String scheme = userUri.getScheme(); 		// "hookup"
            String host = userUri.getHost(); 		// "user" or "group"
            if (host.equals("user")) {
                List<String> params = userUri.getPathSegments();
                String userName = params.get(0); 	// "ime korisnika"
                intent.putExtra(Const.USER_URI_INTENT, true);
                intent.putExtra(Const.USER_URI_NAME, userName);
            } else if (host.equals("group")) {
                List<String> params = userUri.getPathSegments();
                String groupName = params.get(0); 	// "ime grupe"
                intent.putExtra(Const.GROUP_URI_INTENT, true);
                intent.putExtra(Const.GROUP_URI_NAME, groupName);
            }
        }

        intent.putExtra(Const.SIGN_IN, true);
        SplashScreenActivity.this.startActivity(intent);

        finish();
    }

    private void checkPassProtect (User user) {

        if (FrameworkApp.getPreferences().getPasscodeProtect())
        {
            Intent passcode = new Intent(SplashScreenActivity.this,
                    PasscodeActivity.class);
            passcode.putExtra("protect", true);
            SplashScreenActivity.this.startActivityForResult(passcode, 0);
        }
        else
        {
            signIn(user);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            signIn(mUser);
        }
        else {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean authentificationOk(User user) {

        boolean authentificationOk = false;

        if (user.getEmail() != null && !user.getEmail().equals("")) {
            if (user.getEmail().equals(mSavedEmail)) {
                authentificationOk = true;
            }
        }
        return authentificationOk;
    }

    private class AuthListener implements ResultListener<String>
    {
        @Override
        public void onResultsSucceded(String result) {
            boolean tokenOk = result.equals(Const.LOGIN_SUCCESS);
            mUser = UsersManagement.getLoginUser();
            if (tokenOk && mUser!=null) {
                if (authentificationOk(mUser)) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            checkPassProtect(mUser);
                        }
                    }, 2000);
                }
            } else {
                //SideBarActivity.appLogout(false, false, true);
            }
        }
        @Override
        public void onResultsFail() {
            //SideBarActivity.appLogout(false, true, false);
        }
    }
}
