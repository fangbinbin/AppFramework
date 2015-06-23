package com.fangbinbin.appframework.management;

import com.fangbinbin.appframework.model.Group;
import com.fangbinbin.appframework.model.User;

/**
 * UsersManagement
 *
 * Holds reference to login user, user/group which wall is currently opened by login user.
 */

public class UsersManagement {

    private static UsersManagement sUsersManagementInstance;

    private static User sFromUser;
    private static User sToUser;
    private static User supportUser;
    private static Group sToGroup;

    public UsersManagement() {
        setUsersManagement(this);
    }

    public static boolean isTheSameUser() {
        boolean retVal = false;

        if (sToUser != null && sFromUser != null) {
            if (sToUser.getId().equals(sFromUser.getId())) {
                retVal = true;
            }
        }
        return retVal;
    }

    public static User getFromUser() {
        return sFromUser;
    }

    public static void setFromUser(User fromUser) {
        UsersManagement.sFromUser = fromUser;
    }

    public static User getToUser() {
        return sToUser;
    }

    public static void setToUser(User toUser) {
        UsersManagement.sToUser = toUser;
    }

    public static User getLoginUser() {
        return sFromUser;
    }

    public static void setLoginUser(User loginUser) {
        UsersManagement.sFromUser = loginUser;
    }

    public static User getSupportUser() {
        return UsersManagement.supportUser;
    }

    public static void setSupportUser(User loginUser) {
        UsersManagement.supportUser = loginUser;
    }

    public static Group getToGroup() {
        return sToGroup;
    }

    public static void setToGroup(Group toGroup) {
        UsersManagement.sToGroup = toGroup;
    }

    public static UsersManagement getUsersManagement() {
        return sUsersManagementInstance;
    }

    public static void setUsersManagement(UsersManagement usersManagement) {
        UsersManagement.sUsersManagementInstance = usersManagement;
    }
}