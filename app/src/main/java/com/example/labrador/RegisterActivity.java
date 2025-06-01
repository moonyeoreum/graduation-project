package com.example.labrador;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText, confirmPasswordEditText, nameEditText, birthEditText;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseHelper = new FirebaseHelper(); // FirebaseHelper 클래스의 인스턴스 생성

        usernameEditText = findViewById(R.id.ent_email);
        passwordEditText = findViewById(R.id.ent_pw);
        confirmPasswordEditText = findViewById(R.id.ent_pw2);
        nameEditText = findViewById(R.id.ent_name);
        birthEditText = findViewById(R.id.ent_date);

        Button nextButton = findViewById(R.id.btn_next);
        Button backButton = findViewById(R.id.btn_back);

        nextButton.setOnClickListener(view -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();
            String name = nameEditText.getText().toString();
            String birth = birthEditText.getText().toString();

            if (validateInput(username, password, confirmPassword, name, birth)) {
                firebaseHelper.checkDuplicateEmail(username, new FirebaseHelper.OnEmailCheckListener() {
                    @Override
                    public void onEmailExists() {
                        showToast("이미 가입된 이메일 주소입니다.");
                    }

                    @Override
                    public void onEmailAvailable() {
                        startSignupActivity(username, password, name, birth);
                    }

                    @Override
                    public void onEmailCheckError(Exception e) {
                        handleEmailCheckError(e);
                    }
                });
            }
        });

        backButton.setOnClickListener(view -> {
            navigateToLoginActivity();
        });
    }

    private boolean validateInput(String username, String password, String confirmPassword, String name, String birth) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty() || birth.isEmpty()) {
            showToast("모든 필드를 입력해주세요.");
            return false;
        }

        if (!PasswordValidator.isPasswordValid(password)) {
            showToast("비밀번호가 강도가 부족합니다.");
            return false;
        }

        if (!isValidDate(birth, "yyyy-MM-dd")) {
            showToast("올바른 날짜 형식을 입력하세요 (yyyy-MM-dd).");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showToast("비밀번호가 일치하지 않습니다.");
            return false;
        }

        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void startSignupActivity(String username, String password, String name, String birth) {
        Intent intent = new Intent(this, SignupActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        intent.putExtra("name", name);
        intent.putExtra("birth", birth);
        startActivity(intent);
    }

    private void handleEmailCheckError(Exception e) {
        e.printStackTrace();
        Log.e("MyApp", "이메일 중복 확인 오류: " + e.getMessage());
        showToast("이메일 중복 확인 중에 오류가 발생했습니다.");
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private boolean isValidDate(String date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        dateFormat.setLenient(false);
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date parsedDate = dateFormatter.parse(date);
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("MyApp", "예외 발생: " + e.getMessage());
            showToast("올바른 날짜 형식을 입력해주세요.");
            return false;
        }
    }
}
