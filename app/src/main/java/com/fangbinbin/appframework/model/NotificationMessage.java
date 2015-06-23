package com.fangbinbin.appframework.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * NotificationMessage
 *
 * Model class for recent activity notification messages.
 */

public class NotificationMessage {

    public NotificationMessage(String fromUserId, String message,
                               String userImageUrl) {
        super();
        this.mFromUserId = fromUserId;
        this.mMessage = message;
        this.mUserImageUrl = userImageUrl;
    }

    @SerializedName("from_user_id")
    @Expose private String mFromUserId;
    @SerializedName("message")
    @Expose private String mMessage;
    @SerializedName("user_image_url")
    @Expose private String mUserImageUrl;
    @SerializedName("avatar_thumb_file_id")
    @Expose private String mUserAvatarFileId;

    private String mTargetId;
    private int mCount;


    public NotificationMessage() {
    }

    public String getFromUserId() {
        return mFromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.mFromUserId = fromUserId;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }

    @Override
    public String toString() {
        return "NotificationMessage [_fromUserId=" + mFromUserId
                + ", _message=" + mMessage + "]";
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

    public String getUserImageUrl() {
        return mUserImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.mUserImageUrl = userImageUrl;
    }

    public String getUserAvatarFileId() {
        return mUserAvatarFileId;
    }

    public void setUserAvatarFileId(String mUserAvatarFileId) {
        this.mUserAvatarFileId = mUserAvatarFileId;
    }

}