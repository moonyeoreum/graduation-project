package com.example.labrador;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private SwitchMaterial ONOFFButton;
    private ToggleButton btnMan;
    private ToggleButton btnWoman;
    private ToggleButton btnVisual;
    private ToggleButton btnNonVisual;
    private ToggleButton btnPregnant;
    private ToggleButton btnNonPregnant;
    private Button btnPrevious;
    private Button btnRegister;
    private TextToSpeech textToSpeech;
    private boolean isTTSOn = false;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseFirestore firestore;
    private String userId;

    // 음성 메시지 상수 정의
    private static final String MESSAGE_PREGNANT = "임신";
    private static final String MESSAGE_NON_PREGNANT = "비임신";
    private static final String MESSAGE_MALE = "남성";
    private static final String MESSAGE_FEMALE = "여성";
    private static final String MESSAGE_VISUAL = "시각";
    private static final String MESSAGE_NON_VISUAL = "비시각";
    private static final String MESSAGE_SUCCESS = "회원가입이 완료되었습니다.";
    private static final String MESSAGE_FAILURE = "회원가입에 실패하셨습니다.";

    // 이메일과 비밀번호를 저장할 변수 선언
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Firebase 초기화
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();

        // RegisterActivity에서 전달한 데이터를 추출
        Intent intent = getIntent();
        if (intent != null) {
            email = intent.getStringExtra("username");
            password = intent.getStringExtra("password");
        }

        // UI 요소 초기화
        ONOFFButton = findViewById(R.id.ONOFF_Button);
        btnMan = findViewById(R.id.btn_man);
        btnWoman = findViewById(R.id.btn_woman);
        btnVisual = findViewById(R.id.btn_visual);
        btnNonVisual = findViewById(R.id.btn_non_visual);
        btnPregnant = findViewById(R.id.btn_pregnant);
        btnNonPregnant = findViewById(R.id.btn_non_pregnant);
        btnPrevious = findViewById(R.id.btn_previous);
        btnRegister = findViewById(R.id.btn_register);

        // Text-to-Speech 초기화
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

        // Realtime Database 경로
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

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

        // 회원가입 버튼 클릭 처리
        handleRegisterButton();

        // 이전 화면 버튼 클릭 처리
        btnPrevious.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
    }

    private void handleToggleButtonAction(ToggleButton button, String onMessage) {
        button.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isTTSOn) {
                String message = isChecked ? onMessage : "취소";
                textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }

    private void handleRegisterButton() {
        btnRegister.setOnClickListener(v -> {
            if (isTTSOn) {
                // 회원가입 요청을 비동기로 수행
                registerUserWithEmailAndPassword();
            }
        });
    }

    private void registerUserWithEmailAndPassword() {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // 회원가입 성공 시 사용자 데이터 Firestore에 저장
                        saveUserDataToFirestore(userId);
                        // 메시지를 알림 다이얼로그로 표시
                        showRegistrationResultDialog(true, MESSAGE_SUCCESS);
                    } else {
                        // 메시지를 알림 다이얼로그로 표시
                        showRegistrationResultDialog(false, MESSAGE_FAILURE);
                    }
                });
    }

    private void showRegistrationResultDialog(boolean isSuccess, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("확인", (dialog, which) -> {
                    if (isSuccess) {
                        if (isTTSOn) {
                            // 음성 안내가 켜져 있을 때만 음성으로 메시지를 읽음
                            speakVoiceMessage(message);
                        }
                        // 회원가입이 성공한 경우에만 메인 엑티비티로 이동
                        Intent intent = new Intent(this, MainActivity2.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }



    private void saveUserDataToFirestore(String userId) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("isPregnant", btnPregnant.isChecked());
        userData.put("isVisual", btnVisual.isChecked());
        userData.put("isMale", btnMan.isChecked());

        firestore.collection("users").document(userId).set(userData)
                .addOnSuccessListener(aVoid -> {
                    showRegistrationResultDialog(true, MESSAGE_SUCCESS);

                })
                .addOnFailureListener(e -> {
                    showRegistrationResultDialog(false, MESSAGE_FAILURE);

                    Toast.makeText(this, "사용자 데이터를 Firestore에 저장하는데 실패했습니다: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void speakVoiceMessage(String message) {
        if (isTTSOn) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    private void updateVoiceGuideSetting(boolean isEnabled) {
        if (userId != null) {
            // Firebase Realtime Database에 음성 안내 설정 업데이트
            DatabaseReference voiceGuideRef = databaseReference.child("users").child(userId).child("voiceGuideEnabled");
            voiceGuideRef.setValue(isEnabled);
        } else {
            // userId가 null인 경우에 대한 처리 추가
            Log.e("SignupActivity", "userId is null");
        }
    }
}
