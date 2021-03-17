package com.shootbot.viximvp.utilities;

import android.content.Context;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_CONTENT_TYPE;

public class Ut {

    public static void launchConference(Context context, String room, String type) throws MalformedURLException {
        // постоянно будет читать файл, нужно исправить
        PropertyReader pr = new PropertyReader(context, "app.properties");

        URL serverUrl = new URL("https://" + pr.getProperty("conference_server"));
        JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
        builder.setServerURL(serverUrl);
        builder.setWelcomePageEnabled(false);
        builder.setRoom(room);
        if (type.equals("audio")) {
            // builder.setVideoMuted(true);
            builder.setAudioOnly(true);
        }

        JitsiMeetActivity.launch(context, builder.build());
    }

    // public static Map<String, String> getRemoteMessageHeaders() {
    //     Map<String, String> headers = new HashMap<>();
    //     headers.put(
    //             REMOTE_MSG_AUTHORIZATION,
    //             "key=AAAAeIZ_bbI:APA91bEwzIbOQIgRwa5U5rql98c4Z8HoNHAe3fAuEL8EBhcWl_vIGTT_8kCzSzq7pgSwnV98lz-zCjMfRpqWBOrqFNFdIY3m8XEpZDs9yv1nKKyZIw7D438BJ9nROH5bc-1_0ICbSV_U");
    //     headers.put(REMOTE_MSG_CONTENT_TYPE, "application/json");
    //     return headers;
    // }

    public static Map<String, String> getPushRequestHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(REMOTE_MSG_CONTENT_TYPE, "application/json");
        return headers;
    }


}
