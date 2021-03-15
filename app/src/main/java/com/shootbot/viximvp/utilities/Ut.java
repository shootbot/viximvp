package com.shootbot.viximvp.utilities;

import android.content.Context;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

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


}
