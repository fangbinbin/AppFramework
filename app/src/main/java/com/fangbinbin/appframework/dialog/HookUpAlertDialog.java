package com.fangbinbin.appframework.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fangbinbin.appframework.R;
import com.fangbinbin.appframework.utils.FrameworkApp;

/**
 * HookUpAlertDialog
 *
 * Shows alert with buttons options OK and CLOSE, lets user define custom actions for those buttons.
 */

public class HookUpAlertDialog extends Dialog {

    private TextView mTvAlertMessage;
    private Button mBtnDialog;

    private Context mContext;

    public HookUpAlertDialog(final Context context) {
        super(context, R.style.Theme_Transparent);
        mContext = context;

        this.setContentView(R.layout.hookup_alert_dialog);

        mTvAlertMessage = (TextView) this.findViewById(R.id.tvMessage);
        mTvAlertMessage.setTypeface(FrameworkApp.getTfMyriadPro());

        mBtnDialog = (Button) this.findViewById(R.id.btnDialog);
        mBtnDialog.setTypeface(FrameworkApp.getTfMyriadProBold(), Typeface.BOLD);
        mBtnDialog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                HookUpAlertDialog.this.dismiss();

            }
        });

    }

    /**
     * Sets custom alert message
     *
     * @param alertMessage
     */
    public void setMessage(final String alertMessage) {
        mTvAlertMessage.setText(alertMessage);
    }

    public void setButton(ButtonType buttonType) {
        switch (buttonType) {
            case OK:
                mBtnDialog.setText(mContext.getString(R.string.OK));
                mBtnDialog.setBackgroundResource(R.drawable.rounded_rect_positive_selector);
                break;
            case CLOSE:
                mBtnDialog.setText(mContext.getString(R.string.CLOSE));
                mBtnDialog.setBackgroundResource(R.drawable.rounded_rect_alert_selector);
                break;
            default:
                break;
        }
    }

    /**
     * Shows dialog with custom alert message
     *
     * @param alertMessage
     */
    public void show(String alertMessage) {
        mTvAlertMessage.setText(alertMessage);
        HookUpAlertDialog.this.show();
    }

    public void show(String alertMessage, ButtonType buttonType) {
        setButton(buttonType);
        mTvAlertMessage.setText(alertMessage);
        HookUpAlertDialog.this.show();
    }

    public enum ButtonType {
        OK, CLOSE
    }

}