package com.fangbinbin.appframework;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fangbinbin.appframework.serverinteraction.ServerRequestHandler;
import com.fangbinbin.appframework.serverinteraction.ResultListener;
import com.fangbinbin.appframework.model.ActivitySummary;
import com.fangbinbin.appframework.dialog.HookUpAlertDialog;
import com.fangbinbin.appframework.dialog.HookUpAlertDialog.ButtonType;
import com.fangbinbin.appframework.dialog.HookUpDialog;
import com.fangbinbin.appframework.dialog.HookUpProgressDialog;
import com.fangbinbin.appframework.dialog.Tutorial;
import com.fangbinbin.appframework.management.FileManagement;
import com.fangbinbin.appframework.management.UsersManagement;
import com.fangbinbin.appframework.utils.Const;
import com.fangbinbin.appframework.utils.FrameworkApp;
import com.fangbinbin.appframework.utils.Utils;
//import com.cloverstudio.spika.view.SimpleAutoFitTextView;

/**
 * SignInActivity
 *
 * Allows user to sign in, sign up or receive an email with password if user
 * is already registered with a valid email.
 */

public class SignInActivity extends Activity {

    private static final int REQUEST_CODE_LIST_SERVERS = 11;

    private EditText mEtSignInEmail;
    private EditText mEtSignInPassword;
    private EditText mEtSignUpName;
    private EditText mEtSignUpEmail;
    private EditText mEtSignUpPassword;
    private EditText mEtSendPasswordEmail;
    private Button mBtnActive;
    private Button mBtnInactive;
    private Button mBtnForgotPassword;
    private Button mBtnBack;
    private Button mBtnSendPassword;
    private LinearLayout mLlSignIn;
    private LinearLayout mLlSignUp;
    private TextView mTvTitle;
    private TextView mTvSelectServer;

    private String mSignInEmail;
    private String mSignInPassword;
    private String mSignUpName;
    private String mSignUpEmail;
    private String mSignUpPassword;
    private String mSendPasswordEmail;

    private boolean mUserCreated = false;
    private boolean mUserSignedIn = false;
    private LinearLayout mLlForgotPassword;
    private static SignInActivity sInstance = null;
    private Screen mActiveScreen;
    private HookUpDialog mSendPasswordDialog;

    private HookUpProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Log.e("Dir", FileManagement._mainDirName);

        initialization();
        sInstance = this;
        FrameworkApp.gOpenFromBackground = true;

        showTutorial(getString(R.string.tutorial_login));

        moveTaskToBack(false);
    }

    private void showTutorial(String textTutorial) {
        if (FrameworkApp.getPreferences().getShowTutorial(
                Utils.getClassNameInStr(this))) {
            Tutorial.show(this, textTutorial);
            FrameworkApp.getPreferences().setShowTutorial(false,
                    Utils.getClassNameInStr(this));
        }
    }

    private void initialization() {

        // initialize singletons ServerRequestHandler & UsersManagement
        new ServerRequestHandler();
        new UsersManagement();
        new FileManagement(getApplicationContext());

        mSendPasswordDialog = new HookUpDialog(this);
        mSendPasswordDialog.setOnButtonClickListener(HookUpDialog.BUTTON_OK,
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //ServerRequestHandler.sendPassword(mEtSendPasswordEmail.getText().toString(), new SendPasswordListener() ,SignInActivity.this, true);
                        mSendPasswordDialog.dismiss();
                    }
                });
        mSendPasswordDialog.setOnButtonClickListener(
                HookUpDialog.BUTTON_CANCEL, new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mSendPasswordDialog.dismiss();

                    }
                });

        mEtSignInEmail = (EditText) findViewById(R.id.etSignInEmail);
        mEtSignInPassword = (EditText) findViewById(R.id.etSignInPassword);
        mEtSignUpName = (EditText) findViewById(R.id.etSignUpName);
        mEtSignUpEmail = (EditText) findViewById(R.id.etSignUpEmail);
        mEtSignUpPassword = (EditText) findViewById(R.id.etSignUpPassword);
        mEtSendPasswordEmail = (EditText) findViewById(R.id.etForgotPasswordEmail);
        //mTvSelectServer = (TextView) findViewById(R.id.tvServerSelect);
        mBtnBack = (Button) findViewById(R.id.btnBack);
        mBtnBack.setTypeface(FrameworkApp.getTfMyriadProBold(), Typeface.BOLD);
        mBtnBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mActiveScreen == Screen.FORGOT_PASSWORD) {
                    setActiveScreen(Screen.SIGN_IN);
                }

            }
        });
        mBtnActive = (Button) findViewById(R.id.btnActive);
        mBtnActive.setTypeface(FrameworkApp.getTfMyriadProBold(), Typeface.BOLD);
        mBtnActive
                .setBackgroundResource(R.drawable.rounded_rect_positive_selector);
        mBtnInactive = (Button) findViewById(R.id.btnInactive);
        mBtnInactive.setTypeface(FrameworkApp.getTfMyriadProBold(), Typeface.BOLD);
        mBtnInactive
                .setBackgroundResource(R.drawable.rounded_rect_neutral_selector);
        mBtnForgotPassword = (Button) findViewById(R.id.btnForgotPassword);
        mBtnForgotPassword
                .setTextColor(new ColorStateList(new int[][]{
                        new int[]{android.R.attr.state_pressed},
                        new int[]{}}, new int[]{Color.rgb(190, 190, 190),
                        Color.rgb(125, 125, 125),}));
        mBtnForgotPassword.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setActiveScreen(Screen.FORGOT_PASSWORD);

            }
        });
        mBtnSendPassword = (Button) findViewById(R.id.btnSendPassword);
        mBtnSendPassword.setTypeface(FrameworkApp.getTfMyriadProBold(),
                Typeface.BOLD);
        mBtnSendPassword.setVisibility(View.GONE);



        mBtnSendPassword.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isEmailValid(mEtSendPasswordEmail.getText().toString()))
                {
                    mSendPasswordDialog.show(getString(R.string.confirm_email)
                            + "\n" + mEtSendPasswordEmail.getText().toString());
                } else {

                    final HookUpDialog dialog = new HookUpDialog(
                            SignInActivity.this);
                    dialog.showOnlyOK(getString(R.string.email_not_valid));
                }
            }
        });

        mLlSignIn = (LinearLayout) findViewById(R.id.llSignInBody);
        mLlSignIn.setVisibility(View.VISIBLE);
        mLlSignUp = (LinearLayout) findViewById(R.id.llSignUpBody);
        mLlSignUp.setVisibility(View.GONE);
        mLlForgotPassword = (LinearLayout) findViewById(R.id.llForgotPasswordBody);
        mLlForgotPassword.setVisibility(View.GONE);
        mTvTitle = (TextView) findViewById(R.id.tvSignInTitle);
        mTvTitle.setText(getString(R.string.SIGN_IN));
        mTvTitle.setTypeface(FrameworkApp.getTfMyriadPro());

        mEtSignInEmail.setTypeface(FrameworkApp.getTfMyriadPro());
        mEtSignInPassword.setTypeface(FrameworkApp.getTfMyriadPro());
        mEtSignUpName.setTypeface(FrameworkApp.getTfMyriadPro());
        mEtSignUpEmail.setTypeface(FrameworkApp.getTfMyriadPro());
        mEtSignUpPassword.setTypeface(FrameworkApp.getTfMyriadPro());
        mEtSendPasswordEmail.setTypeface(FrameworkApp.getTfMyriadPro());
        //mTvSelectServer.setTypeface(FrameworkApp.getTfMyriadPro());

        //mTvSelectServer.setText(FrameworkApp.getPreferences().getUserServerName());
        /*findViewById(R.id.rlServerField).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SignInActivity.this, ServersListActivity.class), REQUEST_CODE_LIST_SERVERS);
            }
        });*/

        getEmailAndPasswordFromIntent();
        checkToken();

        setActiveScreen(Screen.SIGN_IN);

        //check if token is expired to set edittexts from shared preferences
        if(getIntent().getBooleanExtra(getString(R.string.token_expired_error), false) == true){
            mEtSignInEmail.setText(FrameworkApp.getPreferences().getUserEmail());
            mEtSignInPassword.setText(FrameworkApp.getPreferences().getUserPassword());
        }
    }

    private void getEmailAndPasswordFromIntent() {
        String passwordFromPrefs = getIntent().getStringExtra(
                "password_from_prefs");
        String emailFromPrefs = getIntent().getStringExtra("email_from_prefs");
        if (passwordFromPrefs != null && emailFromPrefs != null) {
            mEtSignInEmail.setText(emailFromPrefs);
            mEtSignInPassword.setText(passwordFromPrefs);
        }
    }

    private void checkToken() {
        if (isInvalidToken()) {
            final HookUpAlertDialog invalidTokenDialog = new HookUpAlertDialog(
                    this);
            invalidTokenDialog.show(getString(R.string.invalid_token_message),
                    ButtonType.CLOSE);
        }
    }

    private boolean isInvalidToken() {
        return getIntent().getBooleanExtra("invalid_token", false);
    }

    private void setActiveScreen(Screen activeScreen) {
        mActiveScreen = activeScreen;
        switch (activeScreen) {
            case SIGN_IN:
                mTvTitle.setText(getString(R.string.SIGN_IN));
                mLlSignIn.setVisibility(View.VISIBLE);
                mLlSignUp.setVisibility(View.GONE);
                mBtnActive.setVisibility(View.VISIBLE);
                mBtnInactive.setVisibility(View.VISIBLE);
                mBtnForgotPassword.setVisibility(View.VISIBLE);
                mBtnActive.setText(getString(R.string.SIGN_IN));
                mBtnInactive.setText(getString(R.string.SIGN_UP));
                mBtnBack.setVisibility(View.GONE);
                mLlForgotPassword.setVisibility(View.GONE);
                mBtnSendPassword.setVisibility(View.GONE);
                mBtnActive.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        mSignInEmail = mEtSignInEmail.getText().toString();
                        mSignInPassword = mEtSignInPassword.getText().toString();

                        if (!mSignInPassword.equals("") && !mSignInEmail.equals("")) {
                            //ServerRequestHandler.authAsync(mSignInEmail, mSignInPassword, new AuthListener(), SignInActivity.this, true);
                        }
                    }
                });
                mBtnInactive.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (mLlSignIn.getVisibility() == View.VISIBLE) {
                            SignInActivity.this.setActiveScreen(Screen.SIGN_UP);
                        }

                    }
                });
                break;
            case SIGN_UP:
                mTvTitle.setText(getString(R.string.SIGN_UP));
                mLlSignUp.setVisibility(View.VISIBLE);
                mLlSignIn.setVisibility(View.GONE);
                mBtnActive.setVisibility(View.VISIBLE);
                mBtnInactive.setVisibility(View.VISIBLE);
                mBtnForgotPassword.setVisibility(View.VISIBLE);
                mBtnActive.setText(getString(R.string.SIGN_UP));
                mBtnInactive.setText(getString(R.string.SIGN_IN));
                mBtnBack.setVisibility(View.GONE);
                mLlForgotPassword.setVisibility(View.GONE);
                mBtnSendPassword.setVisibility(View.GONE);
                mBtnActive.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        mSignUpName = mEtSignUpName.getText().toString();
                        mSignUpEmail = mEtSignUpEmail.getText().toString();
                        mSignUpPassword = mEtSignUpPassword.getText().toString();

                        if (isNameValid(mSignUpName) && isEmailValid(mSignUpEmail) && isPasswordValid(mSignUpPassword)) {
                            //ServerRequestHandler.createUserAsync(mSignUpName, mSignUpEmail, mSignUpPassword, new CreateUserListener(), SignInActivity.this, true);
//						checkAvailability(mSignUpName, mSignUpEmail);
                        }

                    }
                });
                mBtnInactive.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (mLlSignUp.getVisibility() == View.VISIBLE) {
                            SignInActivity.this.setActiveScreen(Screen.SIGN_IN);
                        }

                    }
                });
                break;
            case FORGOT_PASSWORD:
                mTvTitle.setText(getString(R.string.FORGOT_PASSWORD));
                mLlSignUp.setVisibility(View.GONE);
                mLlSignIn.setVisibility(View.GONE);
                mBtnActive.setVisibility(View.GONE);
                mBtnInactive.setVisibility(View.GONE);
                mBtnForgotPassword.setVisibility(View.GONE);
                mBtnBack.setVisibility(View.VISIBLE);
                mLlForgotPassword.setVisibility(View.VISIBLE);
                mBtnSendPassword.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private boolean isNameValid(String name) {
        String nameResult = Utils.checkName(this, name);
        if (!nameResult.equals(getString(R.string.name_ok))) {

            final HookUpDialog dialog = new HookUpDialog(SignInActivity.this);
            dialog.showOnlyOK(nameResult);

            return false;
        } else {
            return true;
        }
    }

    private boolean isPasswordValid(String password) {
        String passwordResult = Utils.checkPassword(this, password);
        if (!passwordResult.equals(getString(R.string.password_ok))) {

            final HookUpDialog dialog = new HookUpDialog(SignInActivity.this);
            dialog.showOnlyOK(passwordResult);

            return false;

        } else {
            return true;
        }
    }

    private boolean isEmailValid(String email) {
        String emailResult = Utils.checkEmail(this, email);

        if (!emailResult.equals(getString(R.string.email_ok))) {

            final HookUpDialog dialog = new HookUpDialog(SignInActivity.this);
            dialog.showOnlyOK(emailResult);

            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (mActiveScreen == Screen.FORGOT_PASSWORD) {
                setActiveScreen(Screen.SIGN_IN);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (mActiveScreen == Screen.FORGOT_PASSWORD) {
            setActiveScreen(Screen.SIGN_IN);
        } else {
            super.onBackPressed();
        }
    }

    private void signIn() {
        if (UsersManagement.getLoginUser() != null) {
            //ServerRequestHandler.findUserActivitySummary(UsersManagement.getLoginUser().getId(), new FindUserActivitySummaryListener(), SignInActivity.this, true);
        }
    }

    public static SignInActivity getInstance() {
        return sInstance;
    }

    @Override
    protected void onDestroy() {
        sInstance = null;
        mLlSignIn = null;
        mLlSignUp = null;
        super.onDestroy();
    }

    private enum Screen {
        SIGN_IN, SIGN_UP, FORGOT_PASSWORD
    }

    private class AuthListener implements ResultListener<String>
    {

        @Override
        public void onResultsSucceded(String result) {
            if (result == Const.LOGIN_SUCCESS)
            {
                signIn();
            }
            else
            {
                final HookUpDialog dialog = new HookUpDialog(SignInActivity.this);
                dialog.showOnlyOK(getString(R.string.no_user_registered));
            }
        }

        @Override
        public void onResultsFail() {
        }
    }

    private class CreateUserListener implements ResultListener<String>
    {
        @Override
        public void onResultsSucceded(String result) {
            if (result != null)
            {
                //ServerRequestHandler.authAsync(mSignUpEmail, mSignUpPassword, new AuthListener(), SignInActivity.this, true);
            }
            else
            {
                final HookUpDialog dialog = new HookUpDialog(SignInActivity.this);
                dialog.showOnlyOK(getString(R.string.an_internal_error_has_occurred));
            }
        }

        @Override
        public void onResultsFail() {
        }
    }

    private class FindUserActivitySummaryListener implements ResultListener<ActivitySummary>
    {
        @Override
        public void onResultsSucceded(ActivitySummary result) {
            ActivitySummary loginUserActivitySummary = result;
            UsersManagement.getLoginUser().setActivitySummary(loginUserActivitySummary);

            /*Intent intent = new Intent(SignInActivity.this, RecentActivityActivity.class);
            intent.putExtra(Const.SIGN_IN, true);
            SignInActivity.this.startActivity(intent);*/

            if (FrameworkApp.getPreferences().getPasscodeProtect() == true) {
                Intent passcode = new Intent(SignInActivity.this, PasscodeActivity.class);
                passcode.putExtra("protect", true);
                SignInActivity.this.startActivity(passcode);
            }
            SignInActivity.this.finish();
        }

        @Override
        public void onResultsFail() {
        }
    }

    private class SendPasswordListener implements ResultListener<Void>
    {
        @Override
        public void onResultsSucceded(Void result) {
            final HookUpAlertDialog emailSentDialog = new HookUpAlertDialog(
                    SignInActivity.this);
            emailSentDialog.show(getString(R.string.email_sent), ButtonType.OK);
        }

        @Override
        public void onResultsFail() {
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_LIST_SERVERS){
            if(resultCode == RESULT_OK){
                if(data != null){
                    mTvSelectServer.setText(data.getStringExtra(Const.SERVER_NAME));
                    mTvSelectServer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    ((SimpleAutoFitTextView)mTvSelectServer).callOnMeasure();
                }
            }
        }
    }*/
}