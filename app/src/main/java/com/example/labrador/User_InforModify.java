package com.example.labrador;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

// 회원 정보의 정보 수정 자바
public class User_InforModify extends AppCompatActivity {
    private SwitchMaterial ONOFFButton;
    private ToggleButton btnMan;
    private ToggleButton btnWoman;
    private ToggleButton btnVisual;
    private ToggleButton btnNonVisual;
    private ToggleButton btnPregnant;
    private ToggleButton btnNonPregnant;
    private Button btnPrevious;
    private Button btnInforModifyComplete;
    private TextToSpeech textToSpeech;
    private boolean isTTSOn = false;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    // 음성 메시지 상수 정의
    private static final String MESSAGE_PREGNANT = "임신";
    private static final String MESSAGE_NON_PREGNANT = "비임신";
    private static final String MESSAGE_MALE = "남성";
    private static final String MESSAGE_FEMALE = "여성";
    private static final String MESSAGE_VISUAL = "시각";
    private static final String MESSAGE_NON_VISUAL = "비시각";
    private static final String MESSAGE_SUCCESS = "정보수정이 완료되었습니다.";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_infor_modify);

        // UI 요소 초기화
        ONOFFButton = findViewById(R.id.ONOFF_Button);
        btnMan = findViewById(R.id.btn_man);
        btnWoman = findViewById(R.id.btn_woman);
        btnVisual = findViewById(R.id.btn_visual);
        btnNonVisual = findViewById(R.id.btn_non_visual);
        btnPregnant = findViewById(R.id.btn_pregnant);
        btnNonPregnant = findViewById(R.id.btn_non_pregnant);
        btnPrevious = findViewById(R.id.btn_previous);
        btnInforModifyComplete = findViewById(R.id.btn_infor_modify_complete);

        // TextToSpeech 초기화
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.KOREAN);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Text-to-Speech 언어가 한국어가 지원되지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Text-to-Speech 언어가 한국어가 지원되지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 스위치 버튼 리스너 설정
        ONOFFButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isTTSOn = isChecked;
            if (isChecked) {
                // 스위치 버튼이 켜진 경우 음성 안내를 시작하는 음성 메시지를 재생
                speakVoiceMessage("음성 안내를 시작합니다.");
            }
            updateVoiceGuideSetting(isChecked);
        });

        // 각 토글 버튼에 대한 음성 처리
        handleToggleButtonAction(btnPregnant, MESSAGE_PREGNANT);
        handleToggleButtonAction(btnNonPregnant, MESSAGE_NON_PREGNANT);
        handleToggleButtonAction(btnMan, MESSAGE_MALE);
        handleToggleButtonAction(btnWoman, MESSAGE_FEMALE);
        handleToggleButtonAction(btnVisual, MESSAGE_VISUAL);
        handleToggleButtonAction(btnNonVisual, MESSAGE_NON_VISUAL);

        // 정보수정 버튼 클릭 처리
        handlebtnInforModifyCompleteButton();

        // 이전 화면 버튼 클릭 처리
        handlePreviousButton();
    }

    // 토글 버튼 클릭 이벤트를 처리하는 함수
    private void handleToggleButtonAction(ToggleButton button, String onMessage) {
        button.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isTTSOn) {
                String message = isChecked ? onMessage : "취소";
                speakVoiceMessage(message);
            }
        });
    }

    // 음성 안내 메시지를 재생하는 함수
    private void speakVoiceMessage(String message) {
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    // 정보수정 버튼 클릭 이벤트를 처리하는 함수
    private void handlebtnInforModifyCompleteButton() {
        btnInforModifyComplete.setOnClickListener(v -> {
            if (isTTSOn) {
                speakVoiceMessage(MESSAGE_SUCCESS);
            }
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                saveUserDataToFirestore(userId);
            }
        });
    }

    // 사용자 정보를 Firestore에 저장하는 함수
    private void saveUserDataToFirestore(String userId) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("isPregnant", btnPregnant.isChecked());
        userData.put("isVisual", btnVisual.isChecked());
        userData.put("isMale", btnMan.isChecked());

        firestore.collection("users")
                .document(userId)
                .set(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showRegistrationSuccessDialog();
                    } else {
                        Toast.makeText(User_InforModify.this, "사용자 데이터를 Firestore에 저장하는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 정보수정 성공 다이얼로그 표시
    private void showRegistrationSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(MESSAGE_SUCCESS)
                .setPositiveButton("확인", (dialog, which) -> {
                    Intent intent = new Intent(this, UserProfileActivity.class);
                    startActivity(intent);
                    finish();
                })
                .show();
    }

    // 이전 화면 버튼 클릭 이벤트를 처리하는 함수
    private void handlePreviousButton() {
        btnPrevious.setOnClickListener(v -> {
            startActivity(new Intent(this, UserProfileActivity.class));
            finish();
        });
    }

    // 음성 안내 설정 업데이트 함수
    private void updateVoiceGuideSetting(boolean isVoiceGuideOn) {
        // 여기에 음성 안내 설정을 업데이트하는 코드 추가
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}

