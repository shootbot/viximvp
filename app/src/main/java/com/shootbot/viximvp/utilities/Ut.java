package com.shootbot.viximvp.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    public static Map<String, String> getPushRequestHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(REMOTE_MSG_CONTENT_TYPE, "application/json");
        headers.put("Accept", "application/json");
        return headers;
    }
}
