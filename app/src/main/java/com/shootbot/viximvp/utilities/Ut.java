package com.shootbot.viximvp.utilities;

import android.content.Context;
import android.os.AsyncTask;

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

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public static boolean isRegistered(Context context) {
        // todo
        return true;
    }

    public static void listenPushes(Context context) {
        // todo
    }

    public static void pubMessage(String message) {
        new PubTask().execute(message);
    }

    static class PubTask extends AsyncTask<String, Void, String> {
        private Exception exception;

        protected String doInBackground(String... message) {
            try {
                URL url = new URL("http://10.0.2.2:9080/pub/?id=ch1");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);
                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = message[0].getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }

                }

                return response.toString();
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if (exception != null) {
                exception.printStackTrace();
            } else {
                System.out.println(response);
            }
        }
    }
}
