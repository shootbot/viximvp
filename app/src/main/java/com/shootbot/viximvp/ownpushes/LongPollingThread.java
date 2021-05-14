package com.shootbot.viximvp.ownpushes;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class LongPollingThread implements Runnable {
    private Context context;
    private String deviceToken;

    public LongPollingThread(Context context, String deviceToken) {
        this.context = context;
        this.deviceToken = deviceToken;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        String lastModified = null;
        String etag = null;

        while (true) {
            try {
                URL serverAddress = new URL("http://10.0.2.2:9080/sub/ch1");
                connection = null;

                connection = (HttpURLConnection) serverAddress.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(600000);

                if (lastModified != null) {
                    connection.setRequestProperty("If-Modified-Since", lastModified);
                }

                if (etag != null) {
                    connection.setRequestProperty("If-None-Match", etag);
                }

                connection.connect();

                System.out.println("response code: " + connection.getResponseCode());

                BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line + '\n');
                }
                String message = sb.toString();

                lastModified = connection.getHeaderField("Last-Modified");
                etag = connection.getHeaderField("Etag");

                System.out.println("message: " + message);
                System.out.println(String.format("Last-Modified: %s Etag: %s", lastModified, etag));

                if (connection.getResponseCode() == 200 && isForMe(message)) {
                    Intent intent = new Intent("com.shootbot.viximvp.ownpushes.NEW_PUSH");
                    putExtraInIntent(intent, message);
                    context.sendBroadcast(intent);
                }
            } catch (ProtocolException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            } catch (MalformedURLException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                    connection = null;
                }
            }
        }
    }

    private boolean isForMe(String message) {
        Message messageObj = getMessageObj(message);

        if (messageObj == null || !messageObj.getTo().contains(deviceToken)) {
            Log.d("isForMe", "false");
            return false;
        }
        Log.d("isForMe", "true");
        return true;
    }

    private void putExtraInIntent(Intent intent, String message) {
        intent.putExtra("message", message);
        Message messageObj = getMessageObj(message);

        MessageData data = messageObj.getData();
        if (data != null) {
            intent.putExtra("type", data.getType());
            intent.putExtra("invitationResponse", data.getInvitationResponse());
            intent.putExtra("meetingType", data.getMeetingType());
            intent.putExtra("first_name", data.getFirst_name());
            intent.putExtra("last_name", data.getLast_name());
            intent.putExtra("email", data.getEmail());
            intent.putExtra("inviterToken", data.getInviterToken());
            intent.putExtra("meetingRoom", data.getMeetingRoom());
        }
    }

    @Nullable
    private Message getMessageObj(String message) {
        Type type = new TypeToken<Message>() {
        }.getType();
        Message messageObj = new Gson().fromJson(message, type);
        return messageObj;
    }
}
