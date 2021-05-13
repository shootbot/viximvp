package com.shootbot.viximvp.listeners;

import com.shootbot.viximvp.models.User;

public interface  UsersListener {

    void initiateMeeting(User receiver, String meetingType);

    void onMultipleUsersAction(boolean isMultipleUsersSelected);
}
