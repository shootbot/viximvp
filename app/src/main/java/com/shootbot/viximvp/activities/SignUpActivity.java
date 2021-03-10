package com.shootbot.viximvp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shootbot.viximvp.R;
import com.shootbot.viximvp.utilities.Constants;
import com.shootbot.viximvp.utilities.PreferenceManager;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.shootbot.viximvp.utilities.Constants.*;

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

    boolean isAllFieldsChecked = false;
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

            if(CheckAllFields()) {
                signUp();
            }
            isAllFieldsChecked = CheckAllFields();

//            if (inputFirstName.getText().toString().trim().isEmpty()) {
//                Toast.makeText(SignUpActivity.this, "Введите имя", Toast.LENGTH_SHORT).show();
//            } else if (inputLastName.getText().toString().trim().isEmpty()) {
//                Toast.makeText(SignUpActivity.this, "Введите фамилию", Toast.LENGTH_SHORT).show();
//            } else if (inputEmail.getText().toString().trim().isEmpty()) {
//                Toast.makeText(SignUpActivity.this, "Введите адрес электронной почты.", Toast.LENGTH_SHORT).show();
//            } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()) {
//                Toast.makeText(SignUpActivity.this, "Не валидный адрес электронной почты.", Toast.LENGTH_SHORT).show();
//            } else if (!PASSWORD_PATTERN.matcher(inputPassword.getText().toString()).matches()) {
//                Toast.makeText(SignUpActivity.this, "Длина пароля должна быть от 6 до 12 символов.", Toast.LENGTH_SHORT).show();
//            } else if (!inputPassword.getText().toString().trim().equals(inputConfirmPassword.getText().toString().trim())) {
//                Toast.makeText(SignUpActivity.this, "Пароли не совпадают.", Toast.LENGTH_SHORT).show();
//            } else {
//                signUp();
//            }
        });
    }

    private void signUp() {
        buttonSignUp.setVisibility(View.INVISIBLE);
        signUpProgressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put(KEY_FIRST_NAME, inputFirstName.getText().toString());
        user.put(KEY_LAST_NAME, inputLastName.getText().toString());
        user.put(KEY_EMAIL, inputEmail.getText().toString());
        user.put(KEY_PASSWORD, inputPassword.getText().toString());

        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(KEY_USER_ID, documentReference.getId());
                    preferenceManager.putString(KEY_FIRST_NAME, inputFirstName.getText().toString());
                    preferenceManager.putString(KEY_LAST_NAME, inputLastName.getText().toString());
                    preferenceManager.putString(KEY_EMAIL, inputEmail.getText().toString());
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    signUpProgressBar.setVisibility(View.INVISIBLE);
                    buttonSignUp.setVisibility(View.VISIBLE);
                    Toast.makeText(SignUpActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private boolean CheckAllFields() {
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
        } else if(!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()) {
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

        // after all validation return true.
        return true;
    }
}