package com.shootbot.viximvp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.shootbot.viximvp.R;

import static com.shootbot.viximvp.utilities.Constants.KEY_EMAIL;
import static com.shootbot.viximvp.utilities.Constants.KEY_FIRST_NAME;
import static com.shootbot.viximvp.utilities.Constants.KEY_LAST_NAME;
import static com.shootbot.viximvp.utilities.Constants.REMOTE_MSG_MEETING_TYPE;

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
    }
}