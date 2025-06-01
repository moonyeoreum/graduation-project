package com.example.labrador;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 음성 검색 버튼을 찾습니다.
        Button voiceRecognitionButton = findViewById(R.id.startVoiceRecognitionButton);

        // 로그인 버튼을 찾습니다.
        Button loginButton = findViewById(R.id.loginButton);

        // 음성 검색 버튼 클릭 이벤트 처리
        voiceRecognitionButton.setOnClickListener(view -> {
            // 음성 검색 화면으로 이동
            Intent intent = new Intent(MainActivity.this, VoiceRecognitionActivity.class);
            startActivity(intent);
        });

        // 로그인 버튼 클릭 이벤트 처리
        loginButton.setOnClickListener(view -> {
            // 로그인 화면으로 이동
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
