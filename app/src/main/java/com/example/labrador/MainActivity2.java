package com.example.labrador;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // 음성 검색 버튼을 찾습니다.
        Button voiceRecognitionButton = findViewById(R.id.startVoiceRecognitionButton);

        // Text Search 버튼을 찾습니다.
        Button textSearchButton = findViewById(R.id.searchButton);

        // 마이 버튼을 찾습니다.
        Button myButton = findViewById(R.id.myButton);

        // 음성 검색 버튼 클릭 이벤트 처리
        voiceRecognitionButton.setOnClickListener(view -> openVoiceRecognitionActivity());

        // Text Search 버튼 클릭 이벤트 처리
        textSearchButton.setOnClickListener(view -> openNameSearchResultActivity());

        // 마이 버튼 클릭 이벤트 처리
        myButton.setOnClickListener(view -> openUserProfileActivity());
    }

    private void openVoiceRecognitionActivity() {
        // 음성 검색 화면으로 이동
        Intent intent = new Intent(this, VoiceRecognitionActivity.class);
        startActivity(intent);
    }

    private void openNameSearchResultActivity() {
        // Text Search 화면으로 이동
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    private void openUserProfileActivity() {
        // 나의 정보 화면으로 이동
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }
}
