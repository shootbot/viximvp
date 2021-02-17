package com.shootbot.viximvp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.shootbot.viximvp.R;
import com.shootbot.viximvp.models.User;
import com.shootbot.viximvp.network.ApiClient;
import com.shootbot.viximvp.network.ApiService;
import com.shootbot.viximvp.utilities.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.shootbot.viximvp.utilities.Constants.KEY_EMAIL;
import static com.shootbot.viximvp.utilities.Constants.KEY_FIRST_NAME;
import static com.shootbot.viximvp.utilities.Constants.KEY_LAST_NAME;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_DATA;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_INVITATION;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_INVITATION_ACCEPTED;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_INVITATION_CANCELED;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_INVITATION_REJECTED;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_INVITATION_RESPONSE;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_INVITER_TOKEN;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_MEETING_TYPE;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_REGISTRATION_IDS;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_TYPE;
import static com.shootbot.viximvp.utilities.Constants.getRemoteMessageHeaders;

public class OutgoingInvitationActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private String inviterToken = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_invitation);

        preferenceManager = new PreferenceManager(getApplicationContext());
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                inviterToken = task.getResult().getToken();
            }
        });

        ImageView imageMeetingType = findViewById(R.id.imageMeetingType);
        String meetingType = getIntent().getStringExtra("type");

        if ("video".equals(meetingType)) {
            imageMeetingType.setImageResource(R.drawable.ic_video);
        }

        TextView textFirstChar = findViewById(R.id.textFirstChar);
        TextView textUsername = findViewById(R.id.textUsername);
        TextView textEmail = findViewById(R.id.textEmail);

        User user = (User) getIntent().getSerializableExtra("user");
        if (user != null) {
            textFirstChar.setText(user.firstName.substring(0, 1));
            textUsername.setText(String.format("%s %s", user.firstName, user.lastName));
            textEmail.setText(user.email);
        }

        ImageView imageStopInvitation = findViewById(R.id.imageStopInvitation);
        imageStopInvitation.setOnClickListener(v -> {
            if (user != null) {
                cancelInvitation(user.token);
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                inviterToken = task.getResult().getToken();
                if (meetingType != null && user != null) {
                    initiateMeeting(meetingType, user.token);
                }
            }
        });
    }

    private void initiateMeeting(String meetingType, String receiverToken) {
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(REMOTE_MSG_TYPE, REMOTE_MSG_INVITATION);
            data.put(REMOTE_MSG_MEETING_TYPE, meetingType);
            data.put(KEY_FIRST_NAME, preferenceManager.getString(KEY_FIRST_NAME));
            data.put(KEY_LAST_NAME, preferenceManager.getString(KEY_LAST_NAME));
            data.put(KEY_EMAIL, preferenceManager.getString(KEY_EMAIL));
            data.put(REMOTE_MSG_INVITER_TOKEN, inviterToken);

            body.put(REMOTE_MSG_DATA, data);
            body.put(REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), REMOTE_MSG_INVITATION);
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void sendRemoteMessage(String remoteMessageBody, String type) {
        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
                getRemoteMessageHeaders(),
                remoteMessageBody)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()) {
                            if (type.equals(REMOTE_MSG_INVITATION)) {
                                Toast.makeText(OutgoingInvitationActivity.this, "Invitation sent successfully", Toast.LENGTH_SHORT).show();
                            } else if (type.equals(REMOTE_MSG_INVITATION_RESPONSE)) {
                                Toast.makeText(OutgoingInvitationActivity.this, "Invitation canceled", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            Toast.makeText(OutgoingInvitationActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Toast.makeText(OutgoingInvitationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void cancelInvitation(String receiverToken) {
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(REMOTE_MSG_TYPE, REMOTE_MSG_INVITATION_RESPONSE);
            data.put(REMOTE_MSG_INVITATION_RESPONSE, REMOTE_MSG_INVITATION_CANCELED);

            body.put(REMOTE_MSG_DATA, data);
            body.put(REMOTE_MSG_REGISTRATION_IDS, tokens);

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
                Toast.makeText(context, "Invitation accepted", Toast.LENGTH_SHORT).show();
            } else if (REMOTE_MSG_INVITATION_REJECTED.equals(type)) {
                Toast.makeText(context, "Invitation rejected", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter(REMOTE_MSG_INVITATION_RESPONSE)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(invitationResponseReceiver);
    }
}