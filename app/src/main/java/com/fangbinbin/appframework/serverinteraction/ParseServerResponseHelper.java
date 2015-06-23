package com.fangbinbin.appframework.serverinteraction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fangbinbin.appframework.model.ActivitySummary;
import com.fangbinbin.appframework.model.Group;
import com.fangbinbin.appframework.model.Notification;
import com.fangbinbin.appframework.model.NotificationMessage;
import com.fangbinbin.appframework.model.RecentActivity;
import com.fangbinbin.appframework.model.User;
import com.fangbinbin.appframework.management.UsersManagement;
import com.fangbinbin.appframework.utils.Const;
import com.fangbinbin.appframework.utils.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

/**
 * ParseServerResponseHelper
 *
 * Used for parsing JSON response from server.
 */
public class ParseServerResponseHelper {

    private static String TAG = "ParseServerResponseHelper: ";

    private static final Gson sGsonExpose = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation().create();

    /**
     * Parse a single user JSON object
     *
     * @param json
     * @return
     * @throws JSONException
     */
    public static User parseSingleUserObject(JSONObject json)
            throws JSONException {
        User user = null;
        ArrayList<String> contactsIds = new ArrayList<String>();

        if (json != null) {

            if (json.has(Const.ERROR)) {
                appLogout(null, false, isInvalidToken(json));
                return null;
            }

            JSONArray rows = json.getJSONArray(Const.ROWS);
            JSONObject row = rows.getJSONObject(0);
            JSONObject userJson = row.getJSONObject(Const.VALUE);

            user = sGsonExpose.fromJson(userJson.toString(), User.class);

            if (userJson.has(Const.FAVORITE_GROUPS)) {
                JSONArray favorite_groups = userJson
                        .getJSONArray(Const.FAVORITE_GROUPS);

                List<String> groups = new ArrayList<String>();

                for (int i = 0; i < favorite_groups.length(); i++) {
                    groups.add(favorite_groups.getString(i));
                }

                user.setGroupIds(groups);
            }

            if (userJson.has(Const.CONTACTS)) {
                JSONArray contacts = userJson.getJSONArray(Const.CONTACTS);

                for (int i = 0; i < contacts.length(); i++) {
                    contactsIds.add(contacts.getString(i));
                }

                user.setContactIds(contactsIds);
            }
        }

        return user;
    }

    /**
     * Parse a single user JSON object
     *
     * @param userJson
     * @return
     * @throws JSONException
     * @throws FrameworkException
     */
    public static User parseSingleUserObjectWithoutRowParam(JSONObject userJson)
            throws JSONException, FrameworkException {
        User user = null;
        ArrayList<String> contactsIds = new ArrayList<String>();

        if (userJson != null) {

            if (userJson.length() == 0) {
                return null;
            }

            if (userJson.has(Const.ERROR)) {
                appLogout(null, false, isInvalidToken(userJson));
                throw new FrameworkException(ConnectionHandler.getError(userJson));
            }

            user = sGsonExpose.fromJson(userJson.toString(), User.class);

            if (userJson.has(Const.FAVORITE_GROUPS)) {
                JSONArray favorite_groups = userJson
                        .getJSONArray(Const.FAVORITE_GROUPS);

                List<String> groups = new ArrayList<String>();

                for (int i = 0; i < favorite_groups.length(); i++) {
                    groups.add(favorite_groups.getString(i));
                }

                user.setGroupIds(groups);
            }

            if (userJson.has(Const.CONTACTS)) {
                JSONArray contacts = userJson.getJSONArray(Const.CONTACTS);

                for (int i = 0; i < contacts.length(); i++) {
                    contactsIds.add(contacts.getString(i));
                }

                user.setContactIds(contactsIds);
            }
        }

        return user;
    }

    /**
     * Parse multi JSON objects of type user
     *
     * @param json
     * @return
     * @throws JSONException
     */
    public static List<User> parseMultiUserObjects(JSONObject json) throws JSONException {

        List<User> users = null;
        ArrayList<String> contactsIds = new ArrayList<String>();

        if (json != null) {

            if (json.has(Const.ERROR)) {
                appLogout(null, false, isInvalidToken(json));
                return null;
            }

            users = new ArrayList<User>();

            // Get the element that holds the users ( JSONArray )
            JSONArray rows = json.getJSONArray(Const.ROWS);

            for (int i = 0; i < rows.length(); i++) {

                JSONObject row = rows.getJSONObject(i);
                JSONObject userJson = row.getJSONObject(Const.VALUE);

                User user = new User();

                user = sGsonExpose
                        .fromJson(userJson.toString(), User.class);

                if (userJson.has(Const.CONTACTS)) {

                    JSONArray contacts = userJson
                            .getJSONArray(Const.CONTACTS);

                    for (int j = 0; j < contacts.length(); j++) {
                        contactsIds.add(contacts.getString(j));
                    }

                    user.setContactIds(contactsIds);
                }

                if (userJson.has(Const.FAVORITE_GROUPS)) {
                    JSONArray favorite_groups = userJson
                            .getJSONArray(Const.FAVORITE_GROUPS);

                    List<String> groups = new ArrayList<String>();

                    for (int k = 0; k < favorite_groups.length(); k++) {
                        groups.add(favorite_groups.getString(k));
                    }

                    user.setGroupIds(groups);
                }

                users.add(user);
            }
        }

        return users;
    }

    /**
     * Parse multi JSON objects of type user for search users
     *
     * @param jsonArray
     * @return
     * @throws JSONException
     */
    public static List<User> parseSearchUsersResult(JSONArray jsonArray) throws JSONException {

        List<User> users = null;
        ArrayList<String> contactsIds = new ArrayList<String>();

        if (jsonArray != null) {

            users = new ArrayList<User>();

            // Get the element that holds the users ( JSONArray )

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject userJson = jsonArray.getJSONObject(i);

                User user = new User();

                user = sGsonExpose
                        .fromJson(userJson.toString(), User.class);


                if (userJson.has(Const.CONTACTS)) {

                    JSONArray contacts = userJson
                            .getJSONArray(Const.CONTACTS);

                    for (int j = 0; j < contacts.length(); j++) {
                        contactsIds.add(contacts.getString(j));
                    }

                    user.setContactIds(contactsIds);
                }

                if (userJson.has(Const.FAVORITE_GROUPS)) {
                    JSONArray favorite_groups = userJson
                            .getJSONArray(Const.FAVORITE_GROUPS);

                    List<String> groups = new ArrayList<String>();

                    for (int k = 0; k < favorite_groups.length(); k++) {
                        groups.add(favorite_groups.getString(k));
                    }

                    user.setGroupIds(groups);
                }

                users.add(user);
            }
        }

        return users;
    }

    /**
     * Parse multi JSON objects of type group for search groups
     *
     * @param jsonArray
     * @return
     * @throws JSONException
     */
    public static List<Group> parseSearchGroupsResult(JSONArray jsonArray) throws JSONException {

        List<Group> groups = null;

        if (jsonArray != null) {

            groups = new ArrayList<Group>();

            // Get the element that holds the groups ( JSONArray )

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject groupJson = jsonArray.getJSONObject(i);

                Group group = new Group();

                group = sGsonExpose.fromJson(groupJson.toString(),
                        Group.class);

                groups.add(group);
            }

        }

        return groups;
    }

    /**
     * Parses a single activity summary JSON object
     *
     * @param json
     * @return
     * @throws JSONException
     * @throws FrameworkException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public static ActivitySummary parseSingleActivitySummaryObject(
            JSONObject json) throws JSONException, ClientProtocolException, IOException, FrameworkException {

        ActivitySummary activitySummary = null;

        if (json != null) {

            if (json.has(Const.ERROR)) {
                appLogout(null, false, isInvalidToken(json));
                return null;
            }

            JSONArray rows = json.getJSONArray(Const.ROWS);

            if (rows.length() > 0) {
                JSONObject row = rows.getJSONObject(0);
                JSONObject activitySummaryJson = row
                        .getJSONObject(Const.VALUE);

                activitySummary = new ActivitySummary();
                activitySummary = sGsonExpose.fromJson(
                        activitySummaryJson.toString(),
                        ActivitySummary.class);

                if (activitySummaryJson.has(Const.RECENT_ACTIVITY)) {
                    JSONObject recentActivityListJson = activitySummaryJson
                            .getJSONObject(Const.RECENT_ACTIVITY);
                    List<RecentActivity> recentActivityList = ParseServerResponseHelper
                            .parseMultiRecentActivityObjects(recentActivityListJson);
                    activitySummary
                            .setRecentActivityList(recentActivityList);
                }
            }
        }

        return activitySummary;
    }

    /**
     * Parses multi RecentActivity JSON Objects
     *
     * @param recentActivityListJson
     * @return
     * @throws FrameworkException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public static List<RecentActivity> parseMultiRecentActivityObjects(
            JSONObject recentActivityListJson) throws ClientProtocolException, IOException, FrameworkException {

        List<RecentActivity> recentActivityList = new ArrayList<RecentActivity>();

        @SuppressWarnings("unchecked")
        Iterator<String> iterator = recentActivityListJson.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            try {
                JSONObject recentActivityJson = recentActivityListJson
                        .getJSONObject(key);
                RecentActivity recentActivity = new RecentActivity();
                recentActivity = sGsonExpose.fromJson(
                        recentActivityJson.toString(), RecentActivity.class);

                if (recentActivityJson.has(Const.NOTIFICATIONS)) {
                    JSONArray notificationsJson = recentActivityJson
                            .getJSONArray(Const.NOTIFICATIONS);
                    recentActivity
                            .set_notifications(parseMultiNotificationObjects(notificationsJson));
                }
                recentActivityList.add(recentActivity);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return recentActivityList;
    }

    /**
     * Parses multi notification objects
     *
     * @param notificationsAry
     * @return
     * @throws FrameworkException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public static List<Notification> parseMultiNotificationObjects(
            JSONArray notificationsAry) throws ClientProtocolException, IOException, FrameworkException {

        List<Notification> notifications = new ArrayList<Notification>();

        for(int i = 0 ; i < notificationsAry.length() ; i++){

            try {
                JSONObject notificationJson = (JSONObject) notificationsAry.get(i);
                Notification notification = new Notification();
                notification = sGsonExpose.fromJson(
                        notificationJson.toString(), Notification.class);

                if (notificationJson.has(Const.MESSAGES)) {
                    JSONArray messagesAry = notificationJson
                            .getJSONArray(Const.MESSAGES);
                    notification
                            .setMessages(parseMultiNotificationMessageObjects(
                                    messagesAry, notification.getTargetId()));
                }

                notifications.add(notification);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return notifications;
    }

    /**
     * Parses multi notification message objects
     *
     * @param messagesArray
     * @return
     * @throws FrameworkException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public static List<NotificationMessage> parseMultiNotificationMessageObjects(
            JSONArray messagesArray, String targetId) throws ClientProtocolException, IOException, FrameworkException {

        List<NotificationMessage> messages = new ArrayList<NotificationMessage>();

        for(int i = 0; i < messagesArray.length() ; i++){
            try {
                JSONObject messageJson = (JSONObject) messagesArray.get(i);
                NotificationMessage notificationMessage = new NotificationMessage();
                notificationMessage = sGsonExpose.fromJson(
                        messageJson.toString(), NotificationMessage.class);
                notificationMessage.setTargetId(targetId);
//				notificationMessage.setUserAvatarFileId(CouchDB
//						.findAvatarFileId(notificationMessage.getFromUserId()));
                messages.add(notificationMessage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return messages;
    }

    /**
     * Parse user JSON objects from get user contacts call
     *
     * @param json
     * @return
     * @throws JSONException
     */
    public static List<User> parseUserContacts(JSONObject json) throws JSONException {

        List<User> users = null;

        if (json != null) {

            if (json.has(Const.ERROR)) {
                appLogout(null, false, isInvalidToken(json));
                return null;
            }

            users = new ArrayList<User>();

            // Get the element that holds the users ( JSONArray )
            JSONArray rows = json.getJSONArray(Const.ROWS);

            for (int i = 0; i < rows.length(); i++) {

                JSONObject row = rows.getJSONObject(i);
                if (!row.isNull(Const.DOC)) {
                    JSONObject userJson = row.getJSONObject(Const.DOC);

                    User user = new User();

                    user = sGsonExpose.fromJson(userJson.toString(),
                            User.class);

                    if (userJson.has(Const.FAVORITE_GROUPS)) {
                        JSONArray favorite_groups = userJson
                                .getJSONArray(Const.FAVORITE_GROUPS);

                        List<String> groups = new ArrayList<String>();

                        for (int z = 0; z < favorite_groups.length(); z++) {
                            groups.add(favorite_groups.getString(z));
                        }

                        user.setGroupIds(groups);
                    }

                    if (userJson.has(Const.CONTACTS)) {
                        JSONArray contacts = userJson
                                .getJSONArray(Const.CONTACTS);

                        List<String> contactsIds = new ArrayList<String>();

                        for (int j = 0; j < contacts.length(); j++) {
                            contactsIds.add(contacts.getString(j));
                        }
                        user.setContactIds(contactsIds);
                    }

                    users.add(user);
                }
            }
        }

        return users;
    }

    /**
     * Create user response object
     *
     * @param json
     * @return
     * @throws JSONException
     */
    public static String createUser(JSONObject json) throws JSONException {

        boolean ok = false;
        String id = null;

        if (json != null) {

            if (json.has(Const.ERROR)) {
                appLogout(null, false, isInvalidToken(json));
                return null;
            }

            ok = json.getBoolean(Const.OK);
            id = json.getString(Const.ID);
        }

        if (!ok) {
            Logger.error(TAG + "createUser", "error in creating user");
        }

        return id;
    }

    /**
     * Update user response object, the Const.REV value is important in order to
     * continue using the application
     *
     * If you are updating contacts or favorites on of them should be null
     *
     * @param json
     * @return
     * @throws JSONException
     */
    public static boolean updateUser(JSONObject json, List<String> contactsIds,
                                     List<String> groupsIds) throws JSONException {

        String rev = "";

        if (json != null) {

            if (json.has(Const.ERROR)) {
                appLogout(false, true, isInvalidToken(json));
                return false;
            }

            rev = json.getString(Const._REV);

            UsersManagement.getLoginUser().setRev(rev);

            if (null != contactsIds) {
                UsersManagement.getLoginUser().setContactIds(
                        contactsIds);
            }

            if (null != groupsIds) {
                UsersManagement.getLoginUser().setGroupIds(groupsIds);
            }

            return true;
        }

        return false;
    }

    /**
     * JSON response from creating a group
     *
     * @param json
     * @return
     * @throws JSONException
     */
    public static String createGroup(JSONObject json) throws JSONException {

        boolean ok = false;
        String id = null;

        if (json != null) {

            if (json.has(Const.ERROR)) {
                appLogout(null, false, isInvalidToken(json));
                return null;
            }

            ok = json.getInt(Const.OK) == 1 ? true : false;
            id = json.getString(Const.ID);
        }

        if (!ok) {
            Logger.error(TAG + "createGroup", "error in creating a group");
            return null;
        }

        return id;
    }

    /**
     * JSON response from deleting a group
     *
     * @param json
     * @return
     * @throws JSONException
     */
    public static boolean deleteGroup(JSONObject json) throws JSONException {

        boolean ok = false;

        if (json != null) {

            if (json.has(Const.ERROR)) {
                appLogout(false, false, isInvalidToken(json));
                return false;
            }

            ok = json.getBoolean(Const.OK);
        }

        return ok;
    }

    public static String findAvatarFileId(JSONObject json) throws JSONException {
        String avatarFileId = null;

        if (json != null) {

            if (json.has(Const.ERROR)) {
                appLogout(null, false, isInvalidToken(json));
                return null;
            }

            JSONArray rows = json.getJSONArray(Const.ROWS);

            for (int i = 0; i < rows.length(); i++)
            {
                JSONObject row = rows.getJSONObject(i);
                avatarFileId = row.getString(Const.VALUE);
            }
        }

        return avatarFileId;
    }

    /**
     * JSON response from deleting a user group
     *
     * @param json
     * @return
     * @throws JSONException
     */
    public static boolean deleteUserGroup(JSONObject json) throws JSONException {

        boolean ok = false;

        if (json != null) {

            if (json.has(Const.ERROR)) {
                appLogout(false, false, isInvalidToken(json));
                return false;
            }

            ok = json.getBoolean(Const.OK);
        }

        return ok;
    }

    /**
     * JSON response from creating a user group
     *
     * @param json
     * @return
     * @throws JSONException
     */
    public static String createUserGroup(JSONObject json) throws JSONException {

        boolean ok = false;
        String id = null;

        if (json != null) {

            if (json.has(Const.ERROR)) {
                appLogout(null, false, isInvalidToken(json));
                return null;
            }

            ok = json.getBoolean(Const.OK);
            id = json.getString(Const.ID);
        }

        if (!ok) {
            Logger.error(TAG + "createUserGroup", "error in creating a group");
            return null;
        }

        return id;
    }

    /**
     * JSON response from creating a comment
     *
     * @param json
     * @return
     * @throws JSONException
     */
    public static String createComment(JSONObject json) throws JSONException {

        boolean ok = false;
        String id = null;

        if (json != null) {

            if (json.has(Const.ERROR)) {
                appLogout(null, false, isInvalidToken(json));
                return null;
            }

            ok = json.getInt(Const.OK) == 1 ? true : false;
            id = json.getString(Const.ID);
        }

        if (!ok) {
            Logger.error(TAG + "createComment", "error in creating comment");
            return null;
        }

        return id;
    }

    /**
     * JSON response from updating a group you own
     *
     * @param json
     * @return
     * @throws JSONException
     */
    public static boolean updateGroup(JSONObject json) throws JSONException {

        boolean ok = false;

        if (json != null) {

            if (json.has(Const.ERROR)) {
                appLogout(false, false, isInvalidToken(json));
                return false;
            }


            ok = json.getInt(Const.OK) == 1 ? true : false;

			/* Important */
            UsersManagement.getToGroup().setRev(json.getString(Const.REV));
        }

        if (!ok) {
            Logger.error(TAG + "updateGroup", "error in updating a group");
        }

        return ok;
    }

    /**
     * Parse single JSON object of type Group
     *
     * @param json
     * @return
     * @throws JSONException
     */
    public static Group parseSingleGroupObject(JSONObject json) throws JSONException {

        Group group = null;

        if (json != null) {

            if (json.has(Const.ERROR)) {
                appLogout(null, false, isInvalidToken(json));
                return null;
            }

            JSONArray rows = json.getJSONArray(Const.ROWS);
            JSONObject row = rows.getJSONObject(0);

            JSONObject groupJson = row.getJSONObject(Const.VALUE);
            group = sGsonExpose.fromJson(groupJson.toString(), Group.class);
        }

        return group;
    }

    /**
     * Parse single JSON object of type Group
     *
     * @param json
     * @return
     */
    public static Group parseSingleGroupObjectWithoutRowParam(JSONObject json) {

        Group group = null;

        if (json != null) {

            if (json.has(Const.ERROR)) {
                appLogout(null, false, isInvalidToken(json));
                return null;
            }

            if (json.length() == 0) {
                return null;
            }

            if (json.has(Const.NAME)) {
                group = sGsonExpose.fromJson(json.toString(), Group.class);
            }
        }

        return group;
    }

    /**
     * Parse multi JSON objects of type Group
     *
     * @param json
     * @return
     * @throws JSONException
     */
    public static List<Group> parseMultiGroupObjects(JSONObject json) throws JSONException {

        List<Group> groups = null;

        if (json != null) {

            if (json.has(Const.ERROR)) {
                appLogout(null, false, isInvalidToken(json));
                return null;
            }

            groups = new ArrayList<Group>();

            JSONArray rows = json.getJSONArray(Const.ROWS);

            for (int i = 0; i < rows.length(); i++) {

                JSONObject row = rows.getJSONObject(i);
                String key = row.getString(Const.KEY);

                if (!key.equals(Const.NULL)) {

                    JSONObject groupJson = row.getJSONObject(Const.VALUE);

                    Group group = sGsonExpose.fromJson(
                            groupJson.toString(), Group.class);

                    groups.add(group);
                }
            }
        }

        return groups;
    }

    /**
     * Parse favorite groups JSON objects
     *
     * @param json
     * @return
     * @throws JSONException
     */
    public static List<Group> parseFavoriteGroups(JSONObject json) throws JSONException {

        List<Group> groups = null;

        if (json != null) {

            if (json.has(Const.ERROR)) {
                appLogout(null, false, isInvalidToken(json));
                return null;
            }

            groups = new ArrayList<Group>();

            JSONArray rows = json.getJSONArray(Const.ROWS);

            for (int i = 0; i < rows.length(); i++) {

                JSONObject row = rows.getJSONObject(i);

                JSONObject groupJson = row.getJSONObject(Const.DOC);

                String type = groupJson.getString(Const.TYPE);
                if (!type.equals(Const.GROUP)) {
                    continue;
                }

                Group group = sGsonExpose.fromJson(
                        groupJson.toString(), Group.class);

                groups.add(group);
            }
        }

        return groups;
    }

    private static boolean isInvalidToken(JSONObject json) {
        if (json.has(Const.MESSAGE)) {
            try {
                String errorMessage = json.getString(Const.MESSAGE);
                if (errorMessage.equalsIgnoreCase(Const.INVALID_TOKEN)) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Object appLogout(Object object, boolean isUserUpdateConflict, boolean isInvalidToken) {
        //SideBarActivity.appLogout(isUserUpdateConflict, true, isInvalidToken);
        return object;
    }

}
