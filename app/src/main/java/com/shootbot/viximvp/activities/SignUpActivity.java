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
import com.shootbot.viximvp.R;
import com.shootbot.viximvp.utilities.PreferenceManager;

import java.util.regex.Pattern;

import static com.shootbot.viximvp.utilities.Constants.KEY_EMAIL;
import static com.shootbot.viximvp.utilities.Constants.KEY_FIRST_NAME;
import static com.shootbot.viximvp.utilities.Constants.KEY_IS_SIGNED_IN;
import static com.shootbot.viximvp.utilities.Constants.KEY_LAST_NAME;
import static com.shootbot.viximvp.utilities.Constants.KEY_PASSWORD;
import static com.shootbot.viximvp.utilities.Constants.KEY_USER_ID;

public class SignUpActivity extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +  // как минимум 1 цифра
                    // "(?=.*[A-Z])" +  // минимум одна большая буква
                    "(?=.*[a-zA-Z])" +  // любая буква
                    "(?=\\\\S+$)" +  // no white spaces
                    ".{6,}" + // как минимум 6 символов
                    "$");

    private EditText inputFirstName, inputLastName, inputEmail, inputPassword, inputConfirmPassword;
    private MaterialButton buttonSignUp;
    private ProgressBar signUpProgressBar;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        preferenceManager = new PreferenceManager(getApplicationContext());

        findViewById(R.id.imageBack).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.textSignIn).setOnClickListener(v -> onBackPressed());

        inputFirstName = findViewById(R.id.inputFirstName);
        inputLastName = findViewById(R.id.inputLastName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        signUpProgressBar = findViewById(R.id.signUpProgressBar);

        buttonSignUp.setOnClickListener(v -> {
            if (checkAllFields()) {
                signUp();
            }
        });
    }

    private void signUp() {
        buttonSignUp.setVisibility(View.INVISIBLE);
        signUpProgressBar.setVisibility(View.VISIBLE);

        ParseObject userObject = new ParseObject("User");
        userObject.put(KEY_FIRST_NAME, inputFirstName.getText().toString());
        userObject.put(KEY_LAST_NAME, inputLastName.getText().toString());
        userObject.put(KEY_EMAIL, inputEmail.getText().toString());
        userObject.put(KEY_PASSWORD, inputPassword.getText().toString());
        userObject.put(KEY_IS_SIGNED_IN, false);
        userObject.saveInBackground(e -> {
            if (e == null) {
                Log.d("parse", "register user ok");
                preferenceManager.putBoolean(KEY_IS_SIGNED_IN, true);
                preferenceManager.putString(KEY_USER_ID, userObject.getObjectId());
                preferenceManager.putString(KEY_FIRST_NAME, inputFirstName.getText().toString());
                preferenceManager.putString(KEY_LAST_NAME, inputLastName.getText().toString());
                preferenceManager.putString(KEY_EMAIL, inputEmail.getText().toString());
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Log.d("parse", "register user error: " + e.getMessage());
                signUpProgressBar.setVisibility(View.INVISIBLE);
                buttonSignUp.setVisibility(View.VISIBLE);
                Toast.makeText(SignUpActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkAllFields() {
        if (inputFirstName.length() == 0) {
            inputFirstName.setError("Введите имя");
            return false;
        }

        if (inputLastName.length() == 0) {
            inputLastName.setError("Введите фамилию");
            return false;
        }

        if (inputEmail.length() == 0) {
            inputEmail.setError("Введите адрес электронной почты.");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()) {
            inputEmail.setError("Невалидный адрес электронной почты.");
            return false;
        }

        if (inputPassword.length() == 0) {
            inputPassword.setError("Введите пароль");
            return false;
        } else if (inputPassword.length() < 6) {
            inputPassword.setError("Длина пароля должна быть от 6 до 12 символов.");
            return false;
        }
        if (!inputPassword.getText().toString().trim().equals(inputConfirmPassword.getText().toString().trim())) {
            inputConfirmPassword.setError("Пароли не совпадают.");
            return false;
        }

        return true;
    }
}