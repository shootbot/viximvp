package com.shootbot.viximvp.utilities;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";

    public static final String IS_CALL_ACCEPTED = "is_call_accepted";

    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_FCM_TOKEN = "fcm_token";


    public static final String KEY_PREFERENCE_NAME = "videoMeetingPreference";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";

    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";

    public static final String REMOTE_MSG_TYPE = "type";
    public static final String REMOTE_MSG_INVITATION = "invitation";
    public static final String REMOTE_MSG_MEETING_TYPE = "meetingType";
    public static final String REMOTE_MSG_INVITER_TOKEN = "inviterToken";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static final String REMOTE_MSG_INVITATION_RESPONSE = "invitationResponse";
    public static final String REMOTE_MSG_INVITATION_ACCEPTED = "accepted";
    public static final String REMOTE_MSG_INVITATION_REJECTED = "rejected";
    public static final String REMOTE_MSG_INVITATION_CANCELED = "canceled";

    public static final String REMOTE_MSG_MEETING_ROOM = "meetingRoom";

    public static Map<String, String> getRemoteMessageHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(
                REMOTE_MSG_AUTHORIZATION,
                "key=AAAAeIZ_bbI:APA91bEwzIbOQIgRwa5U5rql98c4Z8HoNHAe3fAuEL8EBhcWl_vIGTT_8kCzSzq7pgSwnV98lz-zCjMfRpqWBOrqFNFdIY3m8XEpZDs9yv1nKKyZIw7D438BJ9nROH5bc-1_0ICbSV_U");
        headers.put(REMOTE_MSG_CONTENT_TYPE, "application/json");
        return headers;
    }

}
