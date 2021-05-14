package com.shootbot.viximvp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shootbot.viximvp.R;
import com.shootbot.viximvp.user.User;
import com.shootbot.viximvp.network.ApiClient;
import com.shootbot.viximvp.network.ApiService;
import com.shootbot.viximvp.utilities.PreferenceManager;
import com.shootbot.viximvp.utilities.Ut;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.shootbot.viximvp.utilities.Constants.DEVICE_TOKEN;
import static com.shootbot.viximvp.utilities.Constants.KEY_EMAIL;
import static com.shootbot.viximvp.utilities.Constants.KEY_FIRST_NAME;
import static com.shootbot.viximvp.utilities.Constants.KEY_LAST_NAME;
import static com.shootbot.viximvp.utilities.Constants.KEY_USER_ID;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_DATA;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_INVITATION;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_INVITATION_ACCEPTED;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_INVITATION_CANCELED;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_INVITATION_REJECTED;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_INVITATION_RESPONSE;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_INVITER_TOKEN;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_MEETING_ROOM;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_MEETING_TYPE;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_TO;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_TYPE;

public class OutgoingInvitationActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private String inviterToken;
    private String meetingRoom;
    private String meetingType;
    private TextView textFirstChar;
    private TextView textUsername;
    private TextView textEmail;

    private int rejectionCount = 0;
    private int totalReceivers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_invitation);

        preferenceManager = new PreferenceManager(getApplicationContext());

        ImageView imageMeetingType = findViewById(R.id.imageMeetingType);
        meetingType = getIntent().getStringExtra("type");

        if (meetingType != null) {
            if ("video".equals(meetingType)) {
                imageMeetingType.setImageResource(R.drawable.ic_video);
            } else {
                imageMeetingType.setImageResource(R.drawable.ic_audio);
            }
        }

        textFirstChar = findViewById(R.id.textFirstChar);
        textUsername = findViewById(R.id.textUsername);
        textEmail = findViewById(R.id.textEmail);

        User receiver = (User) getIntent().getSerializableExtra("user");

        if (receiver != null) {
            textFirstChar.setText(receiver.firstName.substring(0, 1));
            textUsername.setText(String.format("%s %s", receiver.firstName, receiver.lastName));
            textEmail.setText(receiver.email);

            Log.d("OutgoingInv", "onCreate receiver first name: " + receiver.firstName);
            Log.d("OutgoingInv", "onCreate receiver token: " + receiver.token);
        } else {
            Log.d("OutgoingInv", "onCreate receiver is null");
        }

        ImageView imageStopInvitation = findViewById(R.id.imageStopInvitation);
        imageStopInvitation.setOnClickListener(v -> {
            if (getIntent().getBooleanExtra("isMultiple", false)) {
                Type type = new TypeToken<ArrayList<User>>() {
                }.getType();
                List<User> receivers = new Gson().fromJson(getIntent().getStringExtra("selectedUsers"), type);
                cancelInvitation(null, receivers);
            } else {
                if (receiver != null) {
                    cancelInvitation(receiver.token, null);
                }
            }
        });

        inviterToken = preferenceManager.getString(DEVICE_TOKEN);
        Log.d("OutgoingInv", "onCreate inviter token: " + inviterToken);
        if (meetingType != null) {
            if (getIntent().getBooleanExtra("isMultiple", false)) {
                Type type = new TypeToken<ArrayList<User>>() {
                }.getType();
                List<User> receivers = new Gson().fromJson(getIntent().getStringExtra("selectedUsers"), type);
                if (receivers != null) {
                    totalReceivers = receivers.size();
                }
                initiateMeeting(meetingType, null, receivers);
            } else {
                if (receiver != null) {
                    totalReceivers = 1;
                    initiateMeeting(meetingType, receiver.token, null);
                }
            }
        }
    }

    private void initiateMeeting(String meetingType, String receiverToken, List<User> receivers) {
        try {
            JSONArray tokens = new JSONArray();
            if (receiverToken != null) {
                tokens.put(receiverToken);
            }

            if (receivers != null && !receivers.isEmpty()) {
                StringBuilder usernames = new StringBuilder();
                for (int i = 0; i < receivers.size(); i++) {
                    User user = receivers.get(i);
                    tokens.put(user.token);
                    usernames.append(user.firstName + " " + user.lastName + "\n");
                }
                textFirstChar.setVisibility(View.GONE);
                textEmail.setVisibility(View.GONE);
                textUsername.setText(usernames.toString());
            }

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(REMOTE_MSG_TYPE, REMOTE_MSG_INVITATION);
            data.put(REMOTE_MSG_MEETING_TYPE, meetingType);
            data.put(KEY_FIRST_NAME, preferenceManager.getString(KEY_FIRST_NAME));
            data.put(KEY_LAST_NAME, preferenceManager.getString(KEY_LAST_NAME));
            data.put(KEY_EMAIL, preferenceManager.getString(KEY_EMAIL));
            data.put(REMOTE_MSG_INVITER_TOKEN, inviterToken);

            meetingRoom = preferenceManager.getString(KEY_USER_ID) + "_" + UUID.randomUUID().toString().substring(0, 5);
            data.put(REMOTE_MSG_MEETING_ROOM, meetingRoom);

            body.put(REMOTE_MSG_TO, tokens);
            body.put(REMOTE_MSG_DATA, data);

            sendRemoteMessage(body.toString(), REMOTE_MSG_INVITATION);
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void sendRemoteMessage(String remoteMessageBody, String type) {
        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
                Ut.getPushRequestHeaders(),
                remoteMessageBody)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()) {
                            if (type.equals(REMOTE_MSG_INVITATION)) {
                                Log.d("remote", "sent " + REMOTE_MSG_INVITATION);
                                // Toast.makeText(OutgoingInvitationActivity.this, "Invitation sent successfully", Toast.LENGTH_SHORT).show();
                            } else if (type.equals(REMOTE_MSG_INVITATION_RESPONSE)) {
                                Log.d("remote", "sent " + REMOTE_MSG_INVITATION_RESPONSE);
                                // Toast.makeText(OutgoingInvitationActivity.this, "Invitation canceled", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            Log.d("remote", "success: false, " + response.message());
                            Toast.makeText(OutgoingInvitationActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Log.d("remote", "success: false, " + t.getMessage());
                        Toast.makeText(OutgoingInvitationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void cancelInvitation(String receiverToken, List<User> receivers) {
        try {
            JSONArray tokens = new JSONArray();

            if (receiverToken != null) {
                tokens.put(receiverToken);
            }

            if (receivers != null && !receivers.isEmpty()) {
                for (User user : receivers) {
                    tokens.put(user.token);
                }
            }

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(REMOTE_MSG_TYPE, REMOTE_MSG_INVITATION_RESPONSE);
            data.put(REMOTE_MSG_INVITATION_RESPONSE, REMOTE_MSG_INVITATION_CANCELED);

            body.put(REMOTE_MSG_TO, tokens);
            body.put(REMOTE_MSG_DATA, data);

            sendRemoteMessage(body.toString(), REMOTE_MSG_INVITATION_RESPONSE);
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(REMOTE_MSG_INVITATION_RESPONSE);
            if (REMOTE_MSG_INVITATION_ACCEPTED.equals(type)) {
                try {
                    Ut.launchConference(OutgoingInvitationActivity.this, meetingRoom, meetingType);
                    finish();
                } catch (MalformedURLException e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else if (REMOTE_MSG_INVITATION_REJECTED.equals(type)) {
                rejectionCount++;
                if (rejectionCount == totalReceivers) {
                    // Toast.makeText(context, "Invitation rejected", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager
                .getInstance(getApplicationContext())
                .registerReceiver(invitationResponseReceiver, new IntentFilter(REMOTE_MSG_INVITATION_RESPONSE)
                );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager
                .getInstance(getApplicationContext())
                .unregisterReceiver(invitationResponseReceiver);
    }
}