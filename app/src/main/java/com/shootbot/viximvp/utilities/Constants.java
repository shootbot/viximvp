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
    public static final String KEY_PUSHY_TOKEN = "pushy_token";
    public static final String KEY_OBJECT_ID = "objectId";


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
    public static final String REMOTE_MSG_TO = "to";

    public static final String REMOTE_MSG_INVITATION_RESPONSE = "invitationResponse";
    public static final String REMOTE_MSG_INVITATION_ACCEPTED = "accepted";
    public static final String REMOTE_MSG_INVITATION_REJECTED = "rejected";
    public static final String REMOTE_MSG_INVITATION_CANCELED = "canceled";

    public static final String REMOTE_MSG_MEETING_ROOM = "meetingRoom";

    public static final String FCM_API_URL = "https://fcm.googleapis.com/fcm/";

    // todo pushy говорит о том что нельзя оставлять секретный ключ на клиентах, а нужно хранить на бэкенде
    public static final String SECRET_API_KEY = "6b6d69ade4e0fe8976c679de925c357e7098676990427669880d0f80d6d9d4e7";
    public static final String PUSHY_API_URL = "https://api.pushy.me/";

    public static int REQUEST_CODE_BATTERY_OPTIMIZATION = 1;

}
