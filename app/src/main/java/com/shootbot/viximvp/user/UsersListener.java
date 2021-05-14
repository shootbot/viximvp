package com.shootbot.viximvp.user;

public interface  UsersListener {

    void initiateMeeting(User receiver, String meetingType);

    void onMultipleUsersAction(boolean isMultipleUsersSelected);
}
