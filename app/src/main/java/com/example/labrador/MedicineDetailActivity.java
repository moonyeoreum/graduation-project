package com.example.labrador;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

import java.util.Locale;

public class MedicineDetailActivity extends AppCompatActivity {

    private Button informationButton, alarmButton;
    private TextView itemNameTextView, entpNameTextView, etcOtcCodeTextView, useMethodQesitmTextView, atpnQesitmTextView;
    private ImageView itemImageView;

    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_detail);

        informationButton = findViewById(R.id.informationButton);
        alarmButton = findViewById(R.id.alarmButton);
        itemNameTextView = findViewById(R.id.itemNameTextView);
        entpNameTextView = findViewById(R.id.entpNameTextView);
        etcOtcCodeTextView = findViewById(R.id.etcOtcCodeTextView);
        useMethodQesitmTextView = findViewById(R.id.useMethodQesitmTextView);
        atpnQesitmTextView = findViewById(R.id.atpnQesitmTextView);
        itemImageView = findViewById(R.id.itemImageView);

        Medicine selectedMedicine = getIntent().getParcelableExtra("selectedMedicine");

        if (selectedMedicine != null) {
            displayMedicineDetails(selectedMedicine);
        } else {
            finish();
        }

        // 알람 버튼 클릭 이벤트 처리
        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 알람 화면으로 이동하는 코드
                Intent intent = new Intent(MedicineDetailActivity.this, AlarmActivity.class);
                startActivity(intent);
            }
        });
    }

    private void displayMedicineDetails(Medicine medicine) {
        itemNameTextView.setText(medicine.getItemName());
        entpNameTextView.setText(medicine.getEntpName());
        etcOtcCodeTextView.setText(medicine.getEtcOtcCode());
        useMethodQesitmTextView.setText(medicine.getUseMethodQesitm());
        atpnQesitmTextView.setText(medicine.getAtpnQesitm());

        // Load image using Glide
        Glide.with(this)
                .load(medicine.getItemImage())
                .placeholder(R.drawable.default_image) // default image placeholder
                .error(R.drawable.default_image) // image load error placeholder
                .into(itemImageView);

        informationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MedicineDetailActivity.this, "정보 확인 중...", Toast.LENGTH_SHORT).show();

                // TODO: 실제로 회원 정보를 확인하여 복용 가능 여부 판단
                boolean canTakeMedicine = checkUserMedicationEligibility();

                if (canTakeMedicine) {
                    showNotification("회원님이 복용할 수 있는 의약품입니다.");
                    speak("회원님이 복용할 수 있는 의약품입니다.");
                } else {
                    showNotification("회원님이 복용할 수 없는 의약품입니다.");
                    speak("회원님이 복용할 수 없는 의약품입니다.");
                }
            }
        });

        // TextToSpeech 초기화
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });
    }

    // TODO: 실제로 회원 정보를 확인하여 반환하도록 수정
    private boolean checkUserMedicationEligibility() {
        // 예시로 항상 true를 반환하도록 작성
        return true;
    }

    private void showNotification(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void speak(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
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



