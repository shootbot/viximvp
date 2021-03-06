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
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.shootbot.viximvp.R;
import com.shootbot.viximvp.utilities.PreferenceManager;

import static com.shootbot.viximvp.utilities.Constants.DEVICE_TOKEN;
import static com.shootbot.viximvp.utilities.Constants.KEY_EMAIL;
import static com.shootbot.viximvp.utilities.Constants.KEY_FIRST_NAME;
import static com.shootbot.viximvp.utilities.Constants.KEY_IS_SIGNED_IN;
import static com.shootbot.viximvp.utilities.Constants.KEY_LAST_NAME;
import static com.shootbot.viximvp.utilities.Constants.KEY_PASSWORD;
import static com.shootbot.viximvp.utilities.Constants.KEY_USER_ID;


public class SignInActivity extends AppCompatActivity {
    private EditText inputEmail, inputPassword;
    private MaterialButton buttonSignIn;
    private ProgressBar signInProgressBar;
    private PreferenceManager preferenceManager;

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
            if (isAllFieldsOk()) {
                signIn();
            }
        });
    }

    private void signIn() {
        buttonSignIn.setVisibility(View.INVISIBLE);
        signInProgressBar.setVisibility(View.VISIBLE);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
        query.whereEqualTo(KEY_EMAIL, inputEmail.getText().toString());
        query.whereEqualTo(KEY_PASSWORD, inputPassword.getText().toString());
        query.findInBackground((result, e) -> {
            if (e == null && result.size() > 0) {
                Log.d("signIn", "credentials check: ok");
                ParseObject user = result.get(0);

                user.put(KEY_IS_SIGNED_IN, true);
                user.saveInBackground();

                preferenceManager.putBoolean(KEY_IS_SIGNED_IN, true);
                preferenceManager.putString(KEY_USER_ID, user.getObjectId());
                preferenceManager.putString(KEY_FIRST_NAME, user.getString(KEY_FIRST_NAME));
                preferenceManager.putString(KEY_LAST_NAME, user.getString(KEY_LAST_NAME));
                preferenceManager.putString(KEY_EMAIL, user.getString(KEY_EMAIL));
                preferenceManager.putString(DEVICE_TOKEN, user.getString(KEY_EMAIL));
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                signInProgressBar.setVisibility(View.INVISIBLE);
                buttonSignIn.setVisibility(View.VISIBLE);
                Log.d("signIn", "Ошибка входа " + (e == null ? "" : e.getMessage()));
                Toast.makeText(SignInActivity.this, "Ошибка входа", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isAllFieldsOk() {
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

        return true;
    }
}