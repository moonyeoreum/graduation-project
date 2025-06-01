package com.example.labrador;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMedicationActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private EditText medNameEditText;
    private TextView medInfoTextView;
    private Button searchButton;
    private Button addMedicationButton;
    private TextToSpeech textToSpeech;
    private boolean isTTSOn = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);

        // XML 레이아웃과 연결
        medNameEditText = findViewById(R.id.med_infor);
        searchButton = findViewById(R.id.searchButton);
        addMedicationButton = findViewById(R.id.med_add);

        // TextToSpeech 초기화
        textToSpeech = new TextToSpeech(this, this);
        isTTSOn = true; // 디폴트로 TTS 활성화

        // 의약품 검색 버튼 클릭 이벤트 처리
        searchButton.setOnClickListener(v -> {
            String medicineName = medNameEditText.getText().toString();

            // API 요청 URL 생성
            String apiUrl = buildApiUrl(medicineName);

            // Retrofit을 사용하여 의약품 정보 검색
            MedicineApiService apiService = RetrofitClient.getRetrofitInstance().create(MedicineApiService.class);
            Call<List<Medicine>> call = apiService.getMedicineData(
                    "", // entpName
                    medicineName,
                    "", // useMethodQesitm
                    "", // atpnQesitm
                    "", // itemImage
                    "aXv7rCgtp36oSyV4Zf6txIR%2FkMVc6ok3Z%2FGRpwjZRF4Nty5Eug0WLB5bLhHU%2Bmz9edIPy78I4TLqPGiddzBO2Q%3D%3D" // 서비스키 매개변수 이름에 실제 서비스키 입력
            );


            call.enqueue(new Callback<List<Medicine>>() {
                @Override
                public void onResponse(Call<List<Medicine>> call, Response<List<Medicine>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        // 응답이 성공하고 데이터가 비어 있지 않은 경우
                        Medicine data = response.body().get(0);
                        String medicineInfo = data.getItemName(); // 정확한 메서드를 사용하세요.
                        displayMedicationInfo(medicineInfo);
                    } else {
                        // API 요청이 실패하거나 데이터가 없는 경우 처리
                        Toast.makeText(AddMedicationActivity.this, "의약품 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Medicine>> call, Throwable t) {
                    // 통신 오류 처리
                    Toast.makeText(AddMedicationActivity.this, "통신 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // 의약품 추가 버튼 클릭 이벤트 처리
        addMedicationButton.setOnClickListener(v -> {
            String medicineName = medNameEditText.getText().toString();
            String medicineInfo = medInfoTextView.getText().toString();

            // Firebase Firestore에 의약품 정보 추가
            addMedicationToFirestore(medicineName, medicineInfo);
        });
    }

    private String buildApiUrl(String encodedSearchTerm) {
        String apiKey = getString(R.string.api_key);
        int numberOfRows = 100; // 원하는 행의 수
        try {
            return "https://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList?" +
                    "serviceKey=" + apiKey +
                    "&numOfRows=" + numberOfRows +
                    "&itemName=" + encodedSearchTerm +
                    "&type=xml";
        } catch (Exception e) {
            e.printStackTrace();
            return ""; // 예외 발생 시 빈 문자열 반환
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // 언어 설정 (예: 한국어)
            int langResult = textToSpeech.setLanguage(Locale.KOREAN);

            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                // 언어 데이터가 없거나 지원되지 않을 때 처리
                Toast.makeText(this, "TTS 지원 언어가 아닙니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void speakOut(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void displayMedicationInfo(String medicineInfo) {
        medInfoTextView.setText(medicineInfo);
        // 사용자가 음성 안내를 켠 경우에만 음성 출력
        if (isTTSOn) {
            speakOut("의약품 정보를 가져왔습니다.");
        }
    }

    private void addMedicationToFirestore(String medicineName, String medicineInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> medication = new HashMap<>();
        medication.put("medicineName", medicineName);
        medication.put("manufacturer", ""); // 제조사 정보 추가
        medication.put("dosage", ""); // 용법 및 용량 정보 추가
        medication.put("precautions", ""); // 주의사항 정보 추가

        db.collection("medications")
                .document("medicine") // 문서 ID를 "medicine"로 설정
                .set(medication)
                .addOnSuccessListener(aVoid -> {
                    // AlertDialog 표시
                    showAlertDialog();
                    // 사용자가 음성 안내를 켠 경우에만 음성 출력
                    if (isTTSOn) {
                        speakOut("의약품이 추가되었습니다. 확인되셨습니다.");
                    }
                })
                .addOnFailureListener(e -> {
                    // 추가 실패
                    Toast.makeText(AddMedicationActivity.this, "의약품 추가 실패", Toast.LENGTH_SHORT).show();
                });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setMessage("의약품이 추가되었습니다.");
        builder.setPositiveButton("확인", null);
        builder.setNegativeButton("취소", null);
        AlertDialog dialog = builder.create();
        dialog.show();
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



