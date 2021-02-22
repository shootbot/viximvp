package com.shootbot.viximvp.listeners;

import com.shootbot.viximvp.models.User;

public interface  UsersListener {

    void initiateMeeting(User user, String meetingType);

    void onMultipleUsersAction(boolean isMultipleUsersSelected);
}
