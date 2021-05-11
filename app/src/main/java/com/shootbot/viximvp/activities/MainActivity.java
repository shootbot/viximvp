package com.shootbot.viximvp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.shootbot.viximvp.R;
import com.shootbot.viximvp.adapters.UsersAdapter;
import com.shootbot.viximvp.listeners.UsersListener;
import com.shootbot.viximvp.models.User;
import com.shootbot.viximvp.network.PushService;
import com.shootbot.viximvp.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import me.pushy.sdk.Pushy;

import static com.shootbot.viximvp.utilities.Constants.KEY_EMAIL;
import static com.shootbot.viximvp.utilities.Constants.DEVICE_TOKEN;
import static com.shootbot.viximvp.utilities.Constants.KEY_FIRST_NAME;
import static com.shootbot.viximvp.utilities.Constants.KEY_IS_SIGNED_IN;
import static com.shootbot.viximvp.utilities.Constants.KEY_LAST_NAME;
import static com.shootbot.viximvp.utilities.Constants.KEY_OBJECT_ID;
import static com.shootbot.viximvp.utilities.Constants.KEY_USER_ID;
import static com.shootbot.viximvp.utilities.Constants.REQUEST_CODE_BATTERY_OPTIMIZATION;

// import com.google.firebase.iid.FcmBroadcastProcessor;
// import com.google.firebase.iid.FirebaseInstanceId;

// import com.google.firebase.firestore.DocumentReference;
// import com.google.firebase.firestore.FieldValue;
// import com.google.firebase.firestore.FirebaseFirestore;
// import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivity extends AppCompatActivity implements UsersListener {
    private PreferenceManager preferenceManager;
    private List<User> users;
    private UsersAdapter usersAdapter;
    private TextView textErrorMessage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView imageConference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Pushy.listen(this);
        Intent i = new Intent(getApplicationContext(), PushService.class);
        startService(i);

        preferenceManager = new PreferenceManager(getApplicationContext());

        imageConference = findViewById(R.id.imageConference);

        TextView textTitle = findViewById(R.id.textTitle);
        textTitle.setText(String.format(
                "%s %s",
                preferenceManager.getString(KEY_FIRST_NAME),
                preferenceManager.getString(KEY_LAST_NAME)
        ));

        findViewById(R.id.textSignOut).setOnClickListener(v -> signOut());

        RecyclerView usersRecyclerView = findViewById(R.id.usersRecyclerView);
        textErrorMessage = findViewById(R.id.textErrorMessage);

        users = new ArrayList<>();
        usersAdapter = new UsersAdapter(users, this);
        usersRecyclerView.setAdapter(usersAdapter);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::loadUsers);

        loadUsers();
        checkForBatteryOptimizations();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private void loadUsers() {
        textErrorMessage.setVisibility(View.INVISIBLE);
        swipeRefreshLayout.setRefreshing(true);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
        query.findInBackground((objects, e) -> {
            swipeRefreshLayout.setRefreshing(false);
            String myUserId = preferenceManager.getString(KEY_USER_ID);
            if (e == null) {
                Log.d("parse", "users loaded: " + objects.size());
                users.clear();
                for (ParseObject userObject : objects) {
                    if (myUserId.equals(userObject.getObjectId())) continue;
                    User user = new User();
                    user.firstName = userObject.getString(KEY_FIRST_NAME);
                    user.lastName = userObject.getString(KEY_LAST_NAME);
                    user.email = userObject.getString(KEY_EMAIL);
                    user.token = userObject.getString(DEVICE_TOKEN);
                    user.isSignedIn = userObject.getBoolean(KEY_IS_SIGNED_IN);
                    if (user.isSignedIn) {
                        users.add(user);
                    }
                }
                usersAdapter.notifyDataSetChanged();
            } else {
                Log.d("parse", "load users error: " + e.getMessage());
                Toast.makeText(MainActivity.this, "Error: can't update user list", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signOut() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
        query.whereEqualTo(KEY_OBJECT_ID, preferenceManager.getString(KEY_USER_ID));
        query.findInBackground((objects, e) -> {
            if (e == null) {
                Log.d("parse", "search user ok: " + objects.size());
                for (ParseObject userObject : objects) {
                    userObject.put(KEY_IS_SIGNED_IN, false);
                    userObject.saveInBackground(e1 -> {
                        if (e1 == null) {
                            Log.d("parse", "token delete ok");
                            preferenceManager.clearPreferences();
                            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                            finish();
                        } else {
                            Log.d("parse", "token delete error: " + e1.getMessage());
                            Toast.makeText(MainActivity.this, R.string.unable_to_sign_out, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Log.d("parse", "search user error: " + e.getMessage());
                Toast.makeText(MainActivity.this, R.string.unable_to_sign_out, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void initiateMeeting(User callable, String meetingType) {
        if (callable.token == null || callable.token.trim().isEmpty()) {
            Toast.makeText(
                    this,
                    callable.firstName + " " + callable.lastName + getString(R.string.is_not_available),
                    Toast.LENGTH_SHORT)
                    .show();
        } else {
            Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
            intent.putExtra("user", callable);
            intent.putExtra("type", meetingType);
            startActivity(intent);
        }
    }

    @Override
    public void onMultipleUsersAction(boolean isMultipleUsersSelected) {
        if (isMultipleUsersSelected) {
            imageConference.setVisibility(View.VISIBLE);
            imageConference.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
                intent.putExtra("selectedUsers", new Gson().toJson(usersAdapter.getSelectedUsers()));
                intent.putExtra("type", "video");
                intent.putExtra("isMultiple", true);
                startActivity(intent);
            });
        } else {
            imageConference.setVisibility(View.GONE);
        }
    }

    private void checkForBatteryOptimizations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            if (!powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.warning);
                builder.setMessage(R.string.battery_optimization_enabled);
                builder.setPositiveButton(R.string.disable, (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    startActivityForResult(intent, REQUEST_CODE_BATTERY_OPTIMIZATION);
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                builder.create().show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BATTERY_OPTIMIZATION) {
            checkForBatteryOptimizations();
        }
    }
}