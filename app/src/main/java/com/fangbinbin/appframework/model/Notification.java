package com.fangbinbin.appframework.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Notification
 *
 * Model class for recent activity notifications.
 */

public class Notification {

    public Notification(String targetId, int count) {
        super();
        this.mTargetId = targetId;
        this.mCount = count;
    }

    @SerializedName("target_id")
    @Expose private String mTargetId;
    @SerializedName("count")
    @Expose private int mCount;

    private List<NotificationMessage> mNotificationMessages = new ArrayList<NotificationMessage>();

    public Notification() {
    }

    public String getTargetId() {
        return mTargetId;
    }

    public void setTargetId(String targetId) {
        this.mTargetId = targetId;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        this.mCount = count;
    }

    public List<NotificationMessage> getMessages() {
        return mNotificationMessages;
    }

    public void setMessages(List<NotificationMessage> notificationMessages) {
        this.mNotificationMessages = notificationMessages;
    }

}