package com.shootbot.viximvp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.shootbot.viximvp.R;
import com.shootbot.viximvp.utilities.PreferenceManager;

import java.util.List;
import java.util.regex.Pattern;

import static com.shootbot.viximvp.utilities.Constants.KEY_EMAIL;
import static com.shootbot.viximvp.utilities.Constants.KEY_FIRST_NAME;
import static com.shootbot.viximvp.utilities.Constants.KEY_IS_SIGNED_IN;
import static com.shootbot.viximvp.utilities.Constants.KEY_LAST_NAME;
import static com.shootbot.viximvp.utilities.Constants.KEY_PASSWORD;
import static com.shootbot.viximvp.utilities.Constants.KEY_USER_ID;

// import com.google.firebase.firestore.DocumentSnapshot;

public class SignInActivity extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +  // как минимум 1 цифра
                    // "(?=.*[A-Z])" +  // минимум одна большая буква
                    "(?=.*[a-zA-Z])" +  // любая буква
                    "(?=\\\\S+$)" +  // no white spaces
                    ".{6,}" + // как минимум 6 символов
                    "$");
    private EditText inputEmail, inputPassword;
    private MaterialButton buttonSignIn;
    private ProgressBar signInProgressBar;
    private PreferenceManager preferenceManager;
    boolean isAllFieldsChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(KEY_IS_SIGNED_IN)) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }

        findViewById(R.id.textSignUp).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        signInProgressBar = findViewById(R.id.signInProgressBar);

        buttonSignIn.setOnClickListener(v -> {
            if (CheckAllFields()) {
                signIn();
            }
            isAllFieldsChecked = CheckAllFields();

        });
    }

    private void signIn() {
        buttonSignIn.setVisibility(View.INVISIBLE);
        signInProgressBar.setVisibility(View.VISIBLE);

        // FirebaseFirestore database = FirebaseFirestore.getInstance();
        // database.collection(KEY_COLLECTION_USERS)
        //         .whereEqualTo(KEY_EMAIL, inputEmail.getText().toString())
        //         .whereEqualTo(KEY_PASSWORD, inputPassword.getText().toString())
        //         .get()
        //         .addOnCompleteListener(task -> {
        //             if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
        //                 Log.d("FCM", "Sign in ok, size() = " + task.getResult().getDocuments().size());
        //                 DocumentSnapshot snapshot = task.getResult().getDocuments().get(0);
        //                 preferenceManager.putBoolean(KEY_IS_SIGNED_IN, true);
        //                 preferenceManager.putString(KEY_USER_ID, snapshot.getId());
        //                 preferenceManager.putString(KEY_FIRST_NAME, snapshot.getString(KEY_FIRST_NAME));
        //                 preferenceManager.putString(KEY_LAST_NAME, snapshot.getString(KEY_LAST_NAME));
        //                 preferenceManager.putString(KEY_EMAIL, snapshot.getString(KEY_EMAIL));
        //                 Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        //                 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //                 startActivity(intent);
        //             } else {
        //                 signInProgressBar.setVisibility(View.INVISIBLE);
        //                 buttonSignIn.setVisibility(View.VISIBLE);
        //                 Log.d("FCM", "Невозможно войти");
        //                 Toast.makeText(SignInActivity.this, "Пользователь не зарегистрирован", Toast.LENGTH_SHORT).show();
        //             }
        //         });

        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
        query.whereEqualTo(KEY_EMAIL, inputEmail.getText().toString());
        query.whereEqualTo(KEY_PASSWORD, inputPassword.getText().toString());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> result, ParseException e) {
                if (e == null) {
                    Log.d("parse", "check credentials ok: " + result.size());
                    if (result.size() > 0) {
                        ParseObject user = result.get(0);
                        preferenceManager.putBoolean(KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(KEY_USER_ID, user.getObjectId());
                        preferenceManager.putString(KEY_FIRST_NAME, user.getString(KEY_FIRST_NAME));
                        preferenceManager.putString(KEY_LAST_NAME, user.getString(KEY_LAST_NAME));
                        preferenceManager.putString(KEY_EMAIL, user.getString(KEY_EMAIL));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                } else {
                    Log.d("parse", "check credentials error: " + e.getMessage());
                    signInProgressBar.setVisibility(View.INVISIBLE);
                    buttonSignIn.setVisibility(View.VISIBLE);
                    Log.d("FCM", "Невозможно войти");
                    Toast.makeText(SignInActivity.this, "Пользователь не зарегистрирован", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private boolean CheckAllFields() {


        if (inputEmail.length() == 0) {
            inputEmail.setError("Введите адрес электронной почты.");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()) {
            inputEmail.setError("Введен неверный логин");
            return false;
        }

        if (inputPassword.length() == 0) {
            inputPassword.setError("Введите пароль");
            return false;
        } else if (inputPassword.length() < 6) {
            inputPassword.setError("Введен неверный пароль");
            return false;
        }

        // after all validation return true.
        return true;
    }
}