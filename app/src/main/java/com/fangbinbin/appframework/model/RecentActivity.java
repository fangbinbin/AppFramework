package com.fangbinbin.appframework.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * RecentActivity
 *
 * Model class for recent activity categories.
 */

public class RecentActivity {

    public RecentActivity(String name, String targetType) {
        super();
        this.mName = name;
        this.mTargetType = targetType;
    }

    public RecentActivity() {
    }

    @SerializedName("name")
    @Expose private String mName;
    @SerializedName("target_type")
    @Expose private String mTargetType;

    private List<Notification> mNotifications = new ArrayList<Notification>();
    private int mNotificationCount;

    public String get_name() {
        return mName;
    }

    public void set_name(String name) {
        this.mName = name;
    }

    public String getTargetType() {
        return mTargetType;
    }

    public void set_targetType(String targetType) {
        this.mTargetType = targetType;
    }

    public List<Notification> getNotifications() {
        return mNotifications;
    }

    public void set_notifications(List<Notification> notifications) {
        this.mNotifications = notifications;
    }

    @Override
    public String toString() {
        return "RecentActivity [_name=" + mName + ", _targetType="
                + mTargetType + ", _notifications=" + mNotifications + "]";
    }

    public int getNotificationCount() {
        mNotificationCount = 0;
        for (Notification notification : this.getNotifications()) {
            mNotificationCount+=notification.getCount();
        }
        return mNotificationCount;
    }

    public void setNotificationCount(int mNotificationCount) {
        this.mNotificationCount = mNotificationCount;
    }

}