package com.shootbot.viximvp.listeners;

import com.shootbot.viximvp.models.User;

public interface  UsersListener {

    void initiateVideoMeeting(User user);

    void initiateAudioMeeting(User user);

    void onMultipleUsersAction(boolean isMultipleUsersSelected);
}
