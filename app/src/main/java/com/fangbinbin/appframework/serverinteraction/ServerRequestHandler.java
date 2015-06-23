package com.fangbinbin.appframework.serverinteraction;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.fangbinbin.appframework.utils.FrameworkApp;
import com.fangbinbin.appframework.model.ActivitySummary;
import com.fangbinbin.appframework.model.Group;
import com.fangbinbin.appframework.model.User;
import com.fangbinbin.appframework.management.FileManagement;
import com.fangbinbin.appframework.management.UsersManagement;
import com.fangbinbin.appframework.utils.Const;
import com.fangbinbin.appframework.utils.Utils;

/**
 * ServerRequest
 *
 * Creates and sends requests to server.
 */

public class ServerRequestHandler {

    private final static String groupCategoryCacheKey = "groupCategoryCacheKey";
    private static String TAG = "ServerRequestHandler: ";
    private static ServerRequestHandler sServerRequestHandler;
    private static String sUrl;
    private static String sAuthUrl;
    //    private static JSONParser sJsonParser = new JSONParser();
    private static HashMap<String,String> keyValueCache= new HashMap<String,String>();

    public ServerRequestHandler() {

        /* ServerRequestHandler credentials */

        sUrl = FrameworkApp.getInstance().getBaseUrlWithApi();
        setAuthUrl(FrameworkApp.getInstance().getBaseUrlWithSufix(Const.AUTH_URL));
        sServerRequestHandler = this;

        new ConnectionHandler();
    }

    public static void saveToMemCache(String key,String value){
        keyValueCache.put(key, value);
    }

    public static String getFromMemCache(String key){
        return keyValueCache.get(key);
    }

    public static ServerRequestHandler getServerRequestHandler() {
        return sServerRequestHandler;
    }

    public static String getUrl() {
        return sUrl;
    }

    public static void setUrl(String url) {
        sUrl = url;
    }

    public static String getAuthUrl() {
        return sAuthUrl;
    }

    public static void setAuthUrl(String authUrl) {
        ServerRequestHandler.sAuthUrl = authUrl;
    }

//***** UPLOAD FILE ****************************

    /**
     * Upload file
     *
     * @param filePath
     * @return file ID
     * @throws IOException
     * @throws ClientProtocolException
     * @throws JSONException
     * @throws UnsupportedOperationException
     */

    public static String uploadFile(String filePath) throws FrameworkException, ClientProtocolException, IOException, UnsupportedOperationException, JSONException  {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (filePath != null && !filePath.equals("")) {
            params.add(new BasicNameValuePair(Const.FILE, filePath));
            String fileId = ConnectionHandler.getIdFromFileUploader(FrameworkApp.getInstance().getBaseUrlWithSufix(Const.FILE_UPLOADER_URL), params);
            return fileId;
        }
        return null;
    }

    public static void uploadFileAsync(String filePath, ResultListener<String> resultListener, Context context, boolean showProgressBar) {
        new FrameworkAsyncTask<Void, Void, String>(new UploadFile(filePath), resultListener, context, showProgressBar).execute();
    }

    private static class UploadFile implements Command<String>
    {
        String filePath;

        public UploadFile (String filePath)
        {
            this.filePath = filePath;
        }

        @Override
        public String execute() throws JSONException, IOException, IllegalStateException, FrameworkException {
            return uploadFile(filePath);
        }
    }

//***** DOWNLOAD FILE ****************************

    /**
     * Download file
     *
     * @param fileId
     * @param file
     * @return
     * @throws IOException
     * @throws ClientProtocolException
     * @throws JSONException
     * @throws IllegalStateException
     * @throws FrameworkForbiddenException
     */
    public static File downloadFile(String fileId, File file) throws FrameworkException, ClientProtocolException, IOException, IllegalStateException, JSONException, FrameworkForbiddenException {

        ConnectionHandler.getFile(FrameworkApp.getInstance().getBaseUrlWithSufix(Const.FILE_DOWNLOADER_URL) + Const.FILE + "=" + fileId, file,
                UsersManagement.getLoginUser().getId(), UsersManagement.getLoginUser().getToken());
        return file;
    }

    /**
     * @param fileId
     * @param file
     * @param resultListener
     * @param context
     * @param showProgressBar
     */
    public static void downloadFileAsync(String fileId, File file, ResultListener<File> resultListener, Context context, boolean showProgressBar) {
        new FrameworkAsyncTask<Void, Void, File>(new DownloadFile(fileId, file), resultListener, context, showProgressBar).execute();
    }

    private static class DownloadFile implements Command<File>
    {
        String fileId;
        File file;

        public DownloadFile(String fileId, File file) {
            this.fileId = fileId;
            this.file = file;
        }

        @Override
        public File execute() throws JSONException, IOException, FrameworkException, IllegalStateException, FrameworkForbiddenException {
            return downloadFile(fileId, file);
        }
    }

//***** UNREGISTER PUSH TOKEN ****************************

    /**
     * Unregister push token
     *
     * @param userId
     * @return
     * @throws JSONException
     * @throws FrameworkException
     * @throws IOException
     * @throws IllegalStateException
     * @throws ClientProtocolException
     * @throws FrameworkForbiddenException
     */
    public static String unregisterPushToken(String userId) throws ClientProtocolException, IllegalStateException, IOException, FrameworkException, JSONException, FrameworkForbiddenException {
        String result = ConnectionHandler.getString(FrameworkApp.getInstance().getBaseUrlWithSufix(Const.UNREGISTER_PUSH_URL) + Const.USER_ID + "=" + userId,
                UsersManagement.getLoginUser().getId());
        return result;
    }

    public static void unregisterPushTokenAsync (String userId, ResultListener<String> resultListener, Context context, boolean showProgressBar) {
        new FrameworkAsyncTask<Void, Void, String>(new UnregisterPushToken(userId), resultListener, context, showProgressBar).execute();
    }

    private static class UnregisterPushToken implements Command<String> {

        String userId;

        public UnregisterPushToken(String userId) {
            this.userId = userId;
        }

        @Override
        public String execute() throws JSONException, IOException, IllegalStateException, FrameworkException, FrameworkForbiddenException {
            return unregisterPushToken(userId);
        }
    }

//***** AUTH ****************************

    /**
     * @param email
     * @param password
     * @return
     * @throws IOException
     * @throws JSONException
     * @throws IllegalStateException
     * @throws FrameworkException
     * @throws FrameworkForbiddenException
     */
    public static String auth(String email, String password) throws IOException, JSONException, IllegalStateException, FrameworkException, FrameworkForbiddenException {

        JSONObject jPost = new JSONObject();

        jPost.put("email", email);
        jPost.put("password", FileManagement.md5(password));

        JSONObject json = ConnectionHandler.postAuth(jPost);

        User user = null;

        user = ParseServerResponseHelper.parseSingleUserObjectWithoutRowParam(json);

        if (user != null) {

            FrameworkApp.getPreferences().setUserToken(user.getToken());
            FrameworkApp.getPreferences().setUserEmail(user.getEmail());
            FrameworkApp.getPreferences().setUserId(user.getId());
            FrameworkApp.getPreferences().setUserPassword(password);

            UsersManagement.setLoginUser(user);
            UsersManagement.setToUser(user);
            UsersManagement.setToGroup(null);

            return Const.LOGIN_SUCCESS;
        } else {
            return Const.LOGIN_ERROR;
        }
    }

    public static void authAsync(String email, String password, ResultListener<String> resultListener, Context context, boolean showProgressBar) {
        new FrameworkAsyncTask<Void, Void, String>(new ServerRequestHandler.Auth(email, password), resultListener, context, showProgressBar).execute();
    }

    private static class Auth implements Command<String>
    {
        String email;
        String password;

        public Auth (String email, String password)
        {
            this.email = email;
            this.password = password;
        }

        @Override
        public String execute() throws JSONException, IOException, IllegalStateException, FrameworkException, FrameworkForbiddenException {
            return auth(email, password);
        }
    }

//***** CREATE USER ****************************

    /**
     * @param name
     * @param email
     * @param password
     * @return
     * @throws JSONException
     * @throws ClientProtocolException
     * @throws IOException
     * @throws IllegalStateException
     * @throws FrameworkException
     * @throws FrameworkForbiddenException
     */
    public static String createUser(String name, String email, String password) throws JSONException, ClientProtocolException, IOException, IllegalStateException, FrameworkException, FrameworkForbiddenException {

        JSONObject userJson = new JSONObject();

        userJson.put(Const.NAME, name);
        userJson.put(Const.PASSWORD, FileManagement.md5(password));
        userJson.put(Const.TYPE, Const.USER);
        userJson.put(Const.EMAIL, email);
        userJson.put(Const.LAST_LOGIN, Utils.getCurrentDateTime());
        userJson.put(Const.TOKEN_TIMESTAMP, Utils.getCurrentDateTime() / 1000);
        userJson.put(Const.TOKEN, Utils.generateToken());
        userJson.put(Const.MAX_CONTACT_COUNT, Const.MAX_CONTACTS);
        userJson.put(Const.MAX_FAVORITE_COUNT, Const.MAX_FAVORITES);
        userJson.put(Const.ONLINE_STATUS, Const.ONLINE);

        Log.e("Json", userJson.toString());

        return ParseServerResponseHelper.createUser(ConnectionHandler.postJsonObject("createUser",userJson,
                Const.CREATE_USER, ""));
    }

    public static void createUserAsync(String name, String email, String password, ResultListener<String> resultListener, Context context, boolean showProgressBar) {
        new FrameworkAsyncTask<Void, Void, String>(new ServerRequestHandler.CreateUser(name, email, password), resultListener, context, showProgressBar).execute();
    }

    private static class CreateUser implements Command<String>
    {
        String name;
        String email;
        String password;

        public CreateUser(String name, String email, String password)
        {
            this.name = name;
            this.email = email;
            this.password = password;
        }

        @Override
        public String execute() throws JSONException, IOException, IllegalStateException, FrameworkException, FrameworkForbiddenException {
            return createUser(name, email, password);
        }
    }

//***** FIND USER BY NAME ****************************

    /**
     * @param username
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws JSONException
     * @throws FrameworkException
     * @throws FrameworkForbiddenException
     * @throws IllegalStateException
     */
    private static User findUserByName(String username) throws ClientProtocolException, IOException, JSONException, FrameworkException, IllegalStateException, FrameworkForbiddenException {

        try {
            username = URLEncoder.encode(username, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        final String url = FrameworkApp.getInstance().getBaseUrlWithSufix(Const.FIND_USER_BY_NAME) + username;

        JSONObject jsonObject = ConnectionHandler.getJsonObject(url, null);

        User user = ParseServerResponseHelper.parseSingleUserObjectWithoutRowParam(jsonObject);

        return user;
    }

    public static void findUserByNameAsync(String username, ResultListener<User> resultListener, Context context, boolean showProgressBar) {
        new FrameworkAsyncTask<Void, Void, User>(new ServerRequestHandler.FindUserByName(username), resultListener, context, showProgressBar).execute();
    }

    private static class FindUserByName implements Command<User>
    {
        String username;

        public FindUserByName (String username)
        {
            this.username = username;
        }

        @Override
        public User execute() throws JSONException, IOException, FrameworkException, IllegalStateException, FrameworkForbiddenException {
            return findUserByName(username);
        }
    }

//***** FIND USER BY MAIL ****************************

    /**
     * Find user by email
     *
     * @param email
     * @return
     * @throws FrameworkException
     * @throws JSONException
     * @throws IOException
     * @throws ClientProtocolException
     * @throws FrameworkForbiddenException
     * @throws IllegalStateException
     */
    public static User findUserByEmail(String email) throws ClientProtocolException, IOException, JSONException, FrameworkException, IllegalStateException, FrameworkForbiddenException {

        try {
            email = URLEncoder.encode(email, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        JSONObject json = null;

        final String url = FrameworkApp.getInstance().getBaseUrlWithSufix(Const.FIND_USER_BY_EMAIL) + email;
        User user = null;

        if (UsersManagement.getLoginUser() != null) {
            json = ConnectionHandler.getJsonObject(url, UsersManagement.getLoginUser().getId());
            user = ParseServerResponseHelper.parseSingleUserObjectWithoutRowParam(json);
        } else {
            json = ConnectionHandler.getJsonObject(url, "");
            user = ParseServerResponseHelper.parseSingleUserObjectWithoutRowParam(json);
        }

        return user;
    }

    public static void findUserByEmailAsync(String email, ResultListener<User> resultListener, Context context, boolean showProgressBar) {
        new FrameworkAsyncTask<Void, Void, User>(new ServerRequestHandler.FindUserByEmail(email), resultListener, context, showProgressBar).execute();
    }

    private static class FindUserByEmail implements Command<User>
    {
        String email;

        public FindUserByEmail(String email)
        {
            this.email = email;
        }

        @Override
        public User execute() throws JSONException, IOException, FrameworkException, IllegalStateException, FrameworkForbiddenException {
            return findUserByEmail(email);
        }
    }

//***** FIND USER BY ID ***********************************

    /**
     * Find user by id
     *
     * @param id
     * @return
     * @throws JSONException
     * @throws FrameworkException
     * @throws IOException
     * @throws ClientProtocolException
     * @throws FrameworkForbiddenException
     * @throws IllegalStateException
     */
    public static User findUserById(String id) throws JSONException, ClientProtocolException, IOException, FrameworkException, IllegalStateException, FrameworkForbiddenException {

        try {
            id = URLEncoder.encode(id, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }

        String url = FrameworkApp.getInstance().getBaseUrlWithSufix(Const.FIND_USER_BY_ID) + id;
        JSONObject json = ConnectionHandler.getJsonObject(url, UsersManagement.getLoginUser().getId());
        User user = ParseServerResponseHelper.parseSingleUserObjectWithoutRowParam(json);

        return user;
    }

    public static void findUserByIdAsync(String id, ResultListener<User> resultListener, Context context, boolean showProgressBar) {
        new FrameworkAsyncTask<Void, Void, User>(new ServerRequestHandler.FindUserById(id), resultListener, context, showProgressBar).execute();
    }

    public static class FindUserById implements Command<User>
    {
        String id;

        public FindUserById (String id)
        {
            this.id = id;
        }

        @Override
        public User execute() throws JSONException, IOException, FrameworkException, IllegalStateException, FrameworkForbiddenException {
            return findUserById(id);
        }
    }

//******* UPDATE USER ***************************

    /**
     * Method used for updating user attributes
     *
     * If you add some new attributes to user object you must also add code to
     * add that data to userJson
     *
     * @param user
     * @return user object
     * @throws FrameworkException
     * @throws JSONException
     * @throws ClientProtocolException
     * @throws IOException
     * @throws IllegalStateException
     * @throws FrameworkForbiddenException
     */
    public static boolean updateUser(User user) throws JSONException, ClientProtocolException, IllegalStateException, IOException, FrameworkException, FrameworkForbiddenException {

        JSONObject userJson = new JSONObject();
        List<String> contactIds = new ArrayList<String>();
        List<String> groupIds = new ArrayList<String>();

        JSONObject json = null;

        /* General user info */
        userJson.put(Const._ID, user.getId());
        userJson.put(Const._REV, user.getRev());
        userJson.put(Const.EMAIL, user.getEmail());
        userJson.put(Const.NAME, user.getName());
        userJson.put(Const.TYPE, Const.USER);
        userJson.put(Const.PASSWORD, FileManagement.md5(FrameworkApp.getPreferences().getUserPassword()));
        userJson.put(Const.LAST_LOGIN, user.getLastLogin());
        userJson.put(Const.ABOUT, user.getAbout());
        userJson.put(Const.BIRTHDAY, user.getBirthday());
        userJson.put(Const.GENDER, user.getGender());
        userJson.put(Const.TOKEN, FrameworkApp.getPreferences().getUserToken());
        userJson.put(Const.TOKEN_TIMESTAMP, user.getTokenTimestamp());
        userJson.put(Const.ANDROID_PUSH_TOKEN, FrameworkApp.getPreferences().getUserPushToken());
        userJson.put(Const.ONLINE_STATUS, user.getOnlineStatus());
        userJson.put(Const.AVATAR_FILE_ID, user.getAvatarFileId());
        userJson.put(Const.MAX_CONTACT_COUNT, user.getMaxContactCount());
        userJson.put(Const.AVATAR_THUMB_FILE_ID, user.getAvatarThumbFileId());

        /* Set users favorite contacts */
        JSONArray contactsArray = new JSONArray();
        contactIds = user.getContactIds();
        if (!contactIds.isEmpty()) {
            for (String id : contactIds) {
                contactsArray.put(id);
            }
        }
        if (contactsArray.length() > 0) {
            userJson.put(Const.CONTACTS, contactsArray);
        }

        /* Set users favorite groups */
        JSONArray groupsArray = new JSONArray();
        groupIds = user.getGroupIds();

        if (!groupIds.isEmpty()) {
            for (String id : groupIds) {
                groupsArray.put(id);
            }
        }

        if (groupsArray.length() > 0) {
            userJson.put(Const.FAVORITE_GROUPS, groupsArray);
        }

        json = ConnectionHandler.postJsonObject(Const.UPDATE_USER, userJson, user.getId(), user.getToken());

        return ParseServerResponseHelper.updateUser(json, contactIds, groupIds);
    }

    public static void updateUserAsync (User user, ResultListener<Boolean> resultListener, Context context, boolean showProgressBar) {
        new FrameworkAsyncTask<Void, Void, Boolean>(new UpdateUser(user), resultListener, context, showProgressBar).execute();;
    }

    private static class UpdateUser implements Command<Boolean>
    {
        User user;

        public UpdateUser (User user)
        {
            this.user = user;
        }

        @Override
        public Boolean execute() throws JSONException, IOException, IllegalStateException, FrameworkException, FrameworkForbiddenException {
            return updateUser(user);
        }
    }

//************ FIND USER CONTACTS ***********************


    /**
     * @param id
     * @return
     * @throws JSONException
     * @throws IOException
     * @throws FrameworkException
     * @throws FrameworkForbiddenException
     * @throws IllegalStateException
     */
    public static List<User> findUserContacts(String id) throws JSONException, IOException, FrameworkException, IllegalStateException, FrameworkForbiddenException {

        List<User> contacts = new ArrayList<User>();

        User user = findUserById(id);
        List<String> contactIds = user.getContactIds();
        for (String contactId : contactIds) {
            contacts.add(findUserById(contactId));
        }

        return contacts;
    }

    public static void findUserContactsAsync (String id, ResultListener<List<User>> resultListener, Context context, boolean showProgressBar) {
        new FrameworkAsyncTask<Void, Void, List<User>>(new FindUserContacts(id), resultListener, context, showProgressBar).execute();
    }

    public static class FindUserContacts implements Command<List<User>>
    {
        String id;

        public FindUserContacts (String id)
        {
            this.id = id;
        }

        @Override
        public List<User> execute() throws JSONException, IOException, FrameworkException, IllegalStateException, FrameworkForbiddenException {
            return findUserContacts(id);
        }
    }

    //************ ADD USER CONTACT ***********************

    public static void addUserContactAsync (final String userId, final ResultListener<Boolean> resultListener, final Context context, final boolean showProgressBar)
    {
        new FrameworkAsyncTask<Void, Void, Boolean>(new AddUserContact(userId), resultListener, context, showProgressBar).execute();
    }

    private static class AddUserContact implements Command<Boolean>
    {
        String userId;

        public AddUserContact(String userId) {
            this.userId = userId;
        }

        @Override
        public Boolean execute() throws JSONException, IOException, FrameworkException, IllegalStateException, FrameworkForbiddenException {
            addUser(userId);
            return true;
        }
    }

    private static void addUser (String contactId) throws JSONException, ClientProtocolException, IllegalStateException, IOException, FrameworkException, FrameworkForbiddenException {
        User user = UsersManagement.getLoginUser();

        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put(Const._ID, user.getId());
        jsonRequest.put(Const.USER_ID, contactId);

        JSONObject jsonResponse = ConnectionHandler.postJsonObject(Const.ADD_CONTACT, jsonRequest, user.getId(), user.getToken());
        UsersManagement.setLoginUser(ParseServerResponseHelper.parseSingleUserObjectWithoutRowParam(jsonResponse));
    }

    //************ REMOVE USER CONTACT ***********************

    public static void removeUserContactAsync (String userId, ResultListener<Boolean> resultListener, Context context, boolean showProgressBar) {
        new FrameworkAsyncTask<Void, Void, Boolean>(new RemoveUserContact(userId), resultListener, context, showProgressBar).execute();
    }

    private static class RemoveUserContact implements Command<Boolean>
    {
        String userId;

        public RemoveUserContact(String userId) {
            this.userId = userId;
        }

        @Override
        public Boolean execute() throws JSONException, IOException, FrameworkException, IllegalStateException, FrameworkForbiddenException {
            removeContact(userId);
            return true;
        }
    }

    private static void removeContact (String contactId) throws JSONException, ClientProtocolException, IllegalStateException, IOException, FrameworkException, FrameworkForbiddenException {
        User user = UsersManagement.getLoginUser();

        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put(Const._ID, user.getId());
        jsonRequest.put(Const.USER_ID, contactId);

        JSONObject jsonResponse = ConnectionHandler.postJsonObject(Const.REMOVE_CONTACT, jsonRequest, user.getId(), user.getToken());
        UsersManagement.setLoginUser(ParseServerResponseHelper.parseSingleUserObjectWithoutRowParam(jsonResponse));
    }

    /**
     * Find all groups
     *
     * @return
     * @throws FrameworkException
     * @throws JSONException
     * @throws IOException
     * @throws ClientProtocolException
     * @throws FrameworkForbiddenException
     * @throws IllegalStateException
     */
    public static List<Group> findAllGroups() throws ClientProtocolException, IOException, JSONException, FrameworkException, IllegalStateException, FrameworkForbiddenException {

        JSONObject json= ConnectionHandler.getJsonObject(FrameworkApp.getInstance().getBaseUrlWithSufix(Const.FIND_GROUP_BY_NAME), UsersManagement.getLoginUser().getId());
        return ParseServerResponseHelper.parseMultiGroupObjects(json);
    }

    public static void findAllGroupsAsync (ResultListener<List<Group>> resultListener, Context context, boolean showProgressBar)
    {
        new FrameworkAsyncTask<Void, Void, List<Group>>(new FindAllGroups(), resultListener, context, showProgressBar).execute();
    }

    private static class FindAllGroups implements Command<List<Group>>
    {
        @Override
        public List<Group> execute() throws JSONException, IOException, FrameworkException, IllegalStateException, FrameworkForbiddenException {
            return findAllGroups();
        }
    }

//************************* FIND GROUP BY ID ****************************
    /**
     * Find group by id
     *
     * @return
     * @throws FrameworkException
     * @throws JSONException
     * @throws IOException
     * @throws ClientProtocolException
     * @throws FrameworkForbiddenException
     * @throws IllegalStateException
     */

    public static Group findGroupById(String id) throws ClientProtocolException, IOException, JSONException, FrameworkException, IllegalStateException, FrameworkForbiddenException {

        try {
            id = URLEncoder.encode(id, "UTF-8");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
            return null;
        }

        JSONObject json = ConnectionHandler.getJsonObject(FrameworkApp.getInstance().getBaseUrlWithSufix(Const.FIND_GROUP_BY_ID) + id, UsersManagement.getLoginUser().getId());

        return ParseServerResponseHelper.parseSingleGroupObjectWithoutRowParam(json);
    }

    public static void findGroupByIdAsync(String id, ResultListener<Group> resultListener, Context context, boolean showProgressBar)
    {
        new FrameworkAsyncTask<Void, Void, Group>(new FindGroupById(id), resultListener, context, showProgressBar).execute();
    }

    public static class FindGroupById implements Command<Group>
    {
        String id;

        public FindGroupById (String id)
        {
            this.id = id;
        }

        @Override
        public Group execute() throws JSONException, IOException, FrameworkException, IllegalStateException, FrameworkForbiddenException {
            return findGroupById(id);
        }
    }

//********************** FIND GROUP BY NAME ************************

    /**
     * Find group/groups by name
     *
     * @param name
     * @return
     * @throws FrameworkException
     * @throws JSONException
     * @throws IOException
     * @throws ClientProtocolException
     * @throws FrameworkForbiddenException
     * @throws IllegalStateException
     */
    //TODO: it should be only one group.... or search by name???
    public static List<Group> findGroupsByName(String name) throws ClientProtocolException, IOException, JSONException, FrameworkException, IllegalStateException, FrameworkForbiddenException {

        try {
            name = URLEncoder.encode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

            return null;
        }

        String url = FrameworkApp.getInstance().getBaseUrlWithSufix(Const.FIND_GROUP_BY_NAME) + name;

        JSONObject json = ConnectionHandler.getJsonObject(url, UsersManagement.getLoginUser().getId());

        return ParseServerResponseHelper.parseMultiGroupObjects(json);
    }

    public static void findGroupsByNameAsync (String name, ResultListener<List<Group>> resultListener, Context context, boolean showProgressBar) {
        new FrameworkAsyncTask<Void, Void, List<Group>>(new FindGroupsByName(name), resultListener, context, showProgressBar).execute();
    }

    public static class FindGroupsByName implements Command<List<Group>>
    {
        String name;

        public FindGroupsByName(String name)
        {
            this.name = name;
        }

        @Override
        public List<Group> execute() throws JSONException, IOException, FrameworkException, IllegalStateException, FrameworkForbiddenException {
            return findGroupsByName(name);
        }
    }

//**************** FIND USER FAVORITE GROUPS **********************

    /**
     * Find users favorite groups
     *
     * @param id
     * @return
     * @throws FrameworkException
     * @throws IOException
     * @throws JSONException
     * @throws ClientProtocolException
     * @throws FrameworkForbiddenException
     * @throws IllegalStateException
     */
    public static List<Group> findUserFavoriteGroups(String id) throws ClientProtocolException, JSONException, IOException, FrameworkException, IllegalStateException, FrameworkForbiddenException {

        ArrayList<Group> groups = new ArrayList<Group>();

        User user = findUserById(id);

        for (String groupId : user.getGroupIds()) {
            groups.add(findGroupById(groupId));
        }

        return groups;
    }

    public static void findUserFavoriteGroupsAsync (String id, ResultListener<List<Group>> resultListener, Context context, boolean showProgressBar) {
        new FrameworkAsyncTask<Void, Void, List<Group>>(new FindUserFavoriteGroups(id), resultListener, context, showProgressBar).execute();
    }

    private static class FindUserFavoriteGroups implements Command<List<Group>>
    {
        String id;

        public FindUserFavoriteGroups (String id)
        {
            this.id = id;
        }

        @Override
        public List<Group> execute() throws JSONException, IOException, FrameworkException, IllegalStateException, FrameworkForbiddenException {
            return findUserFavoriteGroups(id);
        }
    }

//***** FIND USER ACTIVITY SUMMARY ***************************

    /**
     * Find activity summary
     *
     * @param id
     * @return
     * @throws FrameworkException
     * @throws JSONException
     * @throws IOException
     * @throws ClientProtocolException
     * @throws FrameworkForbiddenException
     * @throws IllegalStateException
     */
    private static ActivitySummary findUserActivitySummary(String id) throws ClientProtocolException, IOException, JSONException, FrameworkException, IllegalStateException, FrameworkForbiddenException {
        String url = FrameworkApp.getInstance().getBaseUrlWithSufix(Const.FIND_USERACTIVITY_SUMMARY);
        JSONObject json = ConnectionHandler.getJsonObject(url, id);
        return ParseServerResponseHelper.parseSingleActivitySummaryObject(json);
    }

    public static void findUserActivitySummary(String id, ResultListener<ActivitySummary> resultListener, Context context, boolean showProgressBar) {
        new FrameworkAsyncTask<Void, Void, ActivitySummary>(new ServerRequestHandler.FindUserActivitySummary(id), resultListener, context, showProgressBar).execute();
    }

    private static class FindUserActivitySummary implements Command<ActivitySummary>
    {
        String id;

        public FindUserActivitySummary (String id)
        {
            this.id = id;
        }

        @Override
        public ActivitySummary execute() throws JSONException, IOException, FrameworkException, IllegalStateException, FrameworkForbiddenException {
            return findUserActivitySummary(id);
        }
    }

// ************** CREATE GROUP ***********************

    public static String createGroup(Group group) throws JSONException, IllegalStateException, IOException, FrameworkException, FrameworkForbiddenException {

        JSONObject groupJson = new JSONObject();

        groupJson.put(Const.NAME, group.getName());
        groupJson.put(Const.GROUP_PASSWORD, FileManagement.md5(group.getPassword()));
        groupJson.put(Const.TYPE, Const.GROUP);
        groupJson.put(Const.USER_ID, UsersManagement.getLoginUser().getId());
        groupJson.put(Const.DESCRIPTION, group.getDescription());
        groupJson.put(Const.AVATAR_FILE_ID, group.getAvatarFileId());
        groupJson.put(Const.AVATAR_THUMB_FILE_ID, group.getAvatarThumbFileId());
        groupJson.put(Const.CATEGORY_ID, group.getCategoryId());
        groupJson.put(Const.CATEGORY_NAME, group.getCategoryName());
        groupJson.put(Const.DELETED, false);

        return ParseServerResponseHelper.createGroup(ConnectionHandler.postJsonObject(Const.CREATE_GROUP, groupJson,
                UsersManagement.getLoginUser().getId(), UsersManagement.getLoginUser().getToken()));
    }

    public static void createGroupAsync(Group group, ResultListener<String> resultListener, Context context, boolean showProgressBar) {
        new FrameworkAsyncTask<Void, Void, String>(new CreateGroup(group), resultListener, context, showProgressBar).execute();
    }

    private static class CreateGroup implements Command<String>
    {
        Group group;

        public CreateGroup(Group group)
        {
            this.group = group;
        }

        @Override
        public String execute() throws JSONException, IOException, IllegalStateException, FrameworkException, FrameworkForbiddenException {
            return createGroup(group);
        }
    }

//*************** UPDATE GROUP ************************

    /**
     * Update a group you own
     *
     * @param group
     * @return
     * @throws FrameworkException
     * @throws IOException
     * @throws JSONException
     * @throws IllegalStateException
     * @throws ClientProtocolException
     * @throws FrameworkForbiddenException
     */
    public static boolean updateGroup(Group group) throws ClientProtocolException, IllegalStateException, JSONException, IOException, FrameworkException, FrameworkForbiddenException {

        JSONObject groupJson = new JSONObject();

        groupJson.put(Const.NAME, group.getName());
        groupJson.put(Const.GROUP_PASSWORD, FileManagement.md5(group.getPassword()));
        groupJson.put(Const.TYPE, Const.GROUP);
        groupJson.put(Const.USER_ID, UsersManagement.getLoginUser().getId());
        groupJson.put(Const.DESCRIPTION, group.getDescription());
        groupJson.put(Const.AVATAR_FILE_ID, group.getAvatarFileId());
        groupJson.put(Const.AVATAR_THUMB_FILE_ID, group.getAvatarThumbFileId());
        groupJson.put(Const._REV, group.getRev());
        groupJson.put(Const._ID, group.getId());
        groupJson.put(Const.CATEGORY_ID, group.getCategoryId());
        groupJson.put(Const.CATEGORY_NAME, group.getCategoryName());
        groupJson.put(Const.DELETED, group.isDeleted());

        //TODO: Check if this works automagiclly
//        if (group.isDeleted()) {
//        	List<UserGroup> usersGroup = new ArrayList<UserGroup>(findUserGroupByIds(group.getId(),
//                    UsersManagement.getLoginUser().getId()));
//            if (usersGroup != null) {
//                for (UserGroup userGroup : usersGroup) {
//                    ServerRequestHandler.deleteGroup(userGroup.getId(), userGroup.getRev());
//                }
//            }
//        }

        JSONObject newGroupJson = ConnectionHandler.postJsonObject(Const.UPDATE_GROUP, groupJson, UsersManagement.getLoginUser().getId(), UsersManagement.getLoginUser().getToken());
        return ParseServerResponseHelper.updateGroup(newGroupJson);
    }

    public static void updateGroupAsync (Group group, ResultListener<Boolean> resultListener, Context context, boolean showProgressBar) {
        new FrameworkAsyncTask<Void, Void, Boolean>(new UpdateGroup(group), resultListener, context, showProgressBar).execute();
    }

    private static class UpdateGroup implements Command<Boolean> {

        Group group;

        public UpdateGroup(Group group) {
            this.group = group;
        }

        @Override
        public Boolean execute() throws JSONException, IOException, IllegalStateException, FrameworkException, FrameworkForbiddenException {
            return updateGroup(group);
        }
    }

    // ***************** ADD FAVORITE GROUP ***************************

    /**
     * Add favorite user groups to current logged in user
     *
     * @param groupId
     * @return
     * @throws FrameworkException
     * @throws IOException
     * @throws JSONException
     * @throws ClientProtocolException
     * @throws FrameworkForbiddenException
     * @throws IllegalStateException
     */
    public static void addFavoriteGroup(String groupId) throws ClientProtocolException, JSONException, IOException, FrameworkException, IllegalStateException, FrameworkForbiddenException {

        JSONObject create = new JSONObject();
        create.put(Const.GROUP_ID, groupId);

        JSONObject userJson = ConnectionHandler.postJsonObject(Const.SUBSCRIBE_GROUP, create, UsersManagement.getLoginUser().getId(), UsersManagement.getLoginUser().getToken());
        UsersManagement.setLoginUser(ParseServerResponseHelper.parseSingleUserObjectWithoutRowParam(userJson));
    }

    public static void addFavoriteGroupAsync (final String groupId, final ResultListener<Boolean> resultListener, final Context context, final boolean showProgressBar) {
        new FrameworkAsyncTask<Void, Void, Boolean>(new AddFavoriteGroup(groupId), resultListener, context, showProgressBar).execute();
    }

    private static class AddFavoriteGroup implements Command<Boolean> {

        String groupId;

        public AddFavoriteGroup(String groupId) {
            this.groupId = groupId;
        }

        @Override
        public Boolean execute() throws JSONException, IOException, FrameworkException, IllegalStateException, FrameworkForbiddenException {
            addFavoriteGroup(groupId);
            return true;
        }
    }

// ******************** REMOVE FAVORITE GROUP *************************

    /**
     * Remove a group from favorite user groups of current logged in user
     *
     * @param groupId
     * @return
     * @throws FrameworkException
     * @throws IOException
     * @throws JSONException
     * @throws ClientProtocolException
     * @throws FrameworkForbiddenException
     * @throws IllegalStateException
     */
    public static void removeFavoriteGroup(String groupId) throws ClientProtocolException, JSONException, IOException, FrameworkException, IllegalStateException, FrameworkForbiddenException {

        JSONObject create = new JSONObject();
        create.put(Const.GROUP_ID, groupId);

        JSONObject userJson = ConnectionHandler.postJsonObject(Const.UNSUBSCRIBE_GROUP, create, UsersManagement.getLoginUser().getId(), UsersManagement.getLoginUser().getToken());
        UsersManagement.setLoginUser(ParseServerResponseHelper.parseSingleUserObjectWithoutRowParam(userJson));
    }

    public static void removeFavoriteGroupAsync (String groupId, ResultListener<Boolean> resultListener, Context context, boolean showProgressBar) {
        new FrameworkAsyncTask<Void, Void, Boolean>(new RemoveFavoriteGroup(groupId), resultListener, context, showProgressBar).execute();
    }

    private static class RemoveFavoriteGroup implements Command<Boolean> {

        String groupId;

        public RemoveFavoriteGroup(String groupId) {
            this.groupId = groupId;
        }

        @Override
        public Boolean execute() throws JSONException, IOException, FrameworkException, IllegalStateException, FrameworkForbiddenException {
            removeFavoriteGroup(groupId);
            return true;
        }
    }

// **************** DELETE GROUP *********************************

    private static boolean deleteGroup(String id, String rev) throws JSONException, ClientProtocolException, IllegalStateException, IOException, FrameworkException, FrameworkForbiddenException {

        JSONObject create = new JSONObject();
        create.put(Const._ID, id);

        JSONObject delete = ConnectionHandler.postJsonObject(Const.DELETE_GROUP, create, UsersManagement.getLoginUser().getId(), UsersManagement.getLoginUser().getToken());

        return ParseServerResponseHelper.deleteUserGroup(delete);
    }

    public static void deleteGroupAsync (String id, ResultListener<Boolean> resultListener, Context context, boolean showProgressBar) {
        new FrameworkAsyncTask<Void, Void, Boolean>(new DeleteGroup(id), resultListener, context, showProgressBar).execute();
    }

    private static class DeleteGroup implements Command<Boolean>{

        String id;

        public DeleteGroup (String id) {
            this.id = id;
        }

        @Override
        public Boolean execute() throws JSONException, IOException,
                FrameworkException, IllegalStateException, FrameworkForbiddenException {
            return deleteGroup(id, "");
        }

    }
// *********** GET BITMAP OBJECT ********************

//    /**
//     * Get a bitmap object
//     *
//     * @param url
//     * @return
//     */
//    @Deprecated
//    public static Bitmap getBitmapObject(String url) {
//
//        return ConnectionHandler.getBitmapObject(url, UsersManagement.getLoginUser().getId(),
//                UsersManagement.getLoginUser().getToken());
//    }
//
//    public static void getBitmapObjectAsync(String url, ResultListener<Bitmap> resultListener, Context context, boolean showProgressBar) {
//    	new FrameworkAsyncTask<Void, Void, Bitmap>(new GetBitmapObject(url), resultListener, context, showProgressBar).execute();
//    }
//
//    public static class GetBitmapObject implements Command<Bitmap>{
//
//    	String url;
//
//    	public GetBitmapObject (String url)
//    	{
//    		this.url = url;
//    	}
//
//		@Override
//		public Bitmap execute() throws JSONException, IOException {
//			return getBitmapObject(url);
//		}
//    }

}
