package com.shootbot.viximvp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shootbot.viximvp.R;
import com.shootbot.viximvp.network.ApiClient;
import com.shootbot.viximvp.network.ApiService;

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

public class IncomingInvitationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_invitation);

        ImageView imageMeetingType = findViewById(R.id.imageMeetingType);
        String meetingType = getIntent().getStringExtra(REMOTE_MSG_MEETING_TYPE);

        if ("video".equals(meetingType)) {
            imageMeetingType.setImageResource(R.drawable.ic_video);
        }

        TextView textFirstChar = findViewById(R.id.textFirstChar);
        TextView textUsername = findViewById(R.id.textUsername);
        TextView textEmail = findViewById(R.id.textEmail);

        String firstName = getIntent().getStringExtra(KEY_FIRST_NAME);
        if (firstName != null) {
            textFirstChar.setText(firstName.substring(0, 1));
        }

        textUsername.setText(String.format(
                "%s %s",
                firstName,
                getIntent().getStringExtra(KEY_LAST_NAME)
        ));

        textEmail.setText(getIntent().getStringExtra(KEY_EMAIL));

        ImageView imageAcceptInvitation = findViewById(R.id.imageAcceptInvitation);
        imageAcceptInvitation.setOnClickListener(v -> sendInvitationResponse(
                REMOTE_MSG_INVITATION_ACCEPTED,
                getIntent().getStringExtra(REMOTE_MSG_INVITER_TOKEN)));

        ImageView imageRejectInvitation = findViewById(R.id.imageRejectInvitation);
        imageRejectInvitation.setOnClickListener(v -> sendInvitationResponse(
                REMOTE_MSG_INVITATION_REJECTED,
                getIntent().getStringExtra(REMOTE_MSG_INVITER_TOKEN)));
    }

    private void sendInvitationResponse(String type, String receiverToken) {
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(REMOTE_MSG_TYPE, REMOTE_MSG_INVITATION_RESPONSE);
            data.put(REMOTE_MSG_INVITATION_RESPONSE, type);

            body.put(REMOTE_MSG_DATA, data);
            body.put(REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), type);
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
                            if (type.equals(REMOTE_MSG_INVITATION_ACCEPTED)) {
                                Toast.makeText(IncomingInvitationActivity.this, "Invitation accepted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(IncomingInvitationActivity.this, "Invitation rejected", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(IncomingInvitationActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Toast.makeText(IncomingInvitationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(REMOTE_MSG_INVITATION_RESPONSE);
            if (REMOTE_MSG_INVITATION_CANCELED.equals(type)) {
                Toast.makeText(context, "Invitation canceled", Toast.LENGTH_SHORT).show();
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