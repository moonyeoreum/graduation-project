package com.example.labrador;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Locale;

public class VoiceRecognitionActivity extends Activity {
    private static final int SPEECH_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recognition);

        ToggleButton tb = findViewById(R.id.startVoiceRecognitionButton);

        //음성검색 toggle button
        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean status) {
                if(status){
                    //on의 경우 로직
                } else {
                    //off의 경우 로직
                }
            }
        });

    }

    // 음성 인식 시작 버튼 클릭 시 호출되는 메서드
    public void startVoiceRecognition(View view) {
        try {
            Log.d("VoiceRecognitionActivity", "음성 인식 시작 중...");
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "말하세요!");

            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Log.e("VoiceRecognitionActivity", "음성 인식 화면을 찾을 수 없습니다.", e);
            // 음성 인식 앱이 설치되지 않은 경우 처리
            Toast.makeText(this, "음성 인식 앱이 설치되지 않았습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 음성 인식 결과를 받아오는 메서드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String recognizedText = result.get(0);
                Log.d("VoiceRecognitionActivity", "인식된 텍스트: " + recognizedText);

                // 결과를 보여주는 액티비티로 이동
                goToSearchResultActivity(recognizedText);
            }
        }
    }

    // 검색 결과를 보여주는 SearchResultActivity 로 이동하는 메서드
    private void goToSearchResultActivity(String searchQuery) {
        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra("searchQuery", searchQuery);
        startActivity(intent);
    }
}





