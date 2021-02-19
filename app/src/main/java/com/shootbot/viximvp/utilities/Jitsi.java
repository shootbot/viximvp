package com.shootbot.viximvp.utilities;

import android.content.Context;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class Jitsi {

    public static void launchConference(Context context, String room, String type) throws MalformedURLException {
        URL serverUrl = new URL("https://meet.jit.si");
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
