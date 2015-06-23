package com.fangbinbin.appframework.model;

/**
 * ActivitySummary
 *
 * Model class for user activity summary.
 */

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActivitySummary {

    public ActivitySummary(String id, String rev, String type, String userId) {
        super();
        this.mId = id;
        this.mRev = rev;
        this.mType = type;
        this.mUserId = userId;
    }

    @SerializedName("_id")
    @Expose
    private String mId;
    @SerializedName("_rev")
    @Expose
    private String mRev;
    @SerializedName("type")
    @Expose
    private String mType;
    @SerializedName("user_id")
    @Expose
    private String mUserId;

    private int mTotalNotificationCount;
    private List<RecentActivity> mRecentActivityList = new ArrayList<RecentActivity>();

    public ActivitySummary() {
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getRev() {
        return mRev;
    }

    public void setRev(String rev) {
        this.mRev = rev;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        this.mUserId = userId;
    }

    public int getTotalNotificationCount() {
        mTotalNotificationCount = 0;
        for (RecentActivity recentActivity : mRecentActivityList) {
            for (Notification notification : recentActivity.getNotifications()) {
                mTotalNotificationCount = mTotalNotificationCount
                        + notification.getCount();
            }
        }
        return mTotalNotificationCount;
    }

    public void setTotalNotificationCount(int totalNotificationCount) {
        this.mTotalNotificationCount = totalNotificationCount;
    }

    public List<RecentActivity> getRecentActivityList() {
        return mRecentActivityList;
    }

    public void setRecentActivityList(List<RecentActivity> recentActivityList) {
        this.mRecentActivityList = recentActivityList;
    }

}