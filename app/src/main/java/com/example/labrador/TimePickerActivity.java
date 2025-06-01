package com.example.labrador;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

public class TimePickerActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private EditText etAlarmName;
    private GridLayout gridDays;
    private RadioGroup radioGroupInterval;
    private TextToSpeech textToSpeech;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);

        // Firebase 데이터베이스 초기화
        databaseReference = FirebaseDatabase.getInstance().getReference("alarms");

        // UI 요소 초기화
        timePicker = findViewById(R.id.timePicker);
        etAlarmName = findViewById(R.id.etAlarmName);
        gridDays = findViewById(R.id.gridDays);
        radioGroupInterval = findViewById(R.id.radioGroupInterval);
        Button btnSetAlarm = findViewById(R.id.btnSetAlarm);

        // TTS 초기화
        initTTS();

        // 알람 등록 버튼 클릭 이벤트 처리
        btnSetAlarm.setOnClickListener(v -> setAlarm());

        // 요일 선택 체크박스 이벤트 처리
        for (int i = 0; i < gridDays.getChildCount(); i++) {
            View child = gridDays.getChildAt(i);
            if (child instanceof CheckBox) {
                final CheckBox checkBox = (CheckBox) child;
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    // 요일 체크박스 상태 변경 시 동작
                    if (isChecked) {
                        // 체크된 경우, 간격 선택 라디오버튼 활성화
                        radioGroupInterval.setVisibility(View.VISIBLE);
                    } else {
                        // 체크 해제된 경우, 간격 선택 라디오버튼 비활성화
                        radioGroupInterval.clearCheck();
                        radioGroupInterval.setVisibility(View.GONE);
                    }
                });
            }
        }
    }

    private void initTTS() {
        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.getDefault());
            }
        });
    }

    private void setAlarm() {
        // 사용자가 선택한 시간 및 알람 이름 가져오기
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        String alarmName = etAlarmName.getText().toString();

        // 사용자가 선택한 요일 가져오기
        StringBuilder selectedDays = new StringBuilder();
        for (int i = 0; i < gridDays.getChildCount(); i++) {
            View child = gridDays.getChildAt(i);
            if (child instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) child;
                if (checkBox.isChecked()) {
                    selectedDays.append(checkBox.getText().toString()).append(" ");
                }
            }
        }

        // 사용자가 선택한 간격 가져오기
        int selectedIntervalId = radioGroupInterval.getCheckedRadioButtonId();
        RadioButton selectedInterval = findViewById(selectedIntervalId);
        String interval = selectedInterval.getText().toString();

        // 알람 등록
        registerAlarm(hour, minute, selectedDays.toString().trim(), interval, alarmName);

        // Firebase에 알람 정보 저장
        saveAlarmToFirebase(alarmName, hour, minute, selectedDays.toString().trim(), interval);

        // TTS로 알람 설정 완료 메시지 읽어주기
        speakAlarmSetMessage(alarmName);

        // 예를 들어, 선택된 정보를 Toast 메시지로 표시
        String toastMessage = "알람 등록\n"
                + "시간: " + hour + ":" + minute + "\n"
                + "이름: " + alarmName + "\n"
                + "요일: " + selectedDays.toString().trim() + "\n"
                + "간격: " + interval;
        Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();
    }

    private void registerAlarm(int hour, int minute, String selectedDays, String interval, String alarmName) {
        // 알람 매니저를 통해 알람 설정
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("alarmName", alarmName);

        // PendingIntent를 사용하여 알람을 구별
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                generateRequestCode(alarmName),
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Calendar 객체를 사용하여 알람 시간 설정
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // 알람이 매주 반복되도록 설정
        setAlarmRepeatDays(calendar, selectedDays);

        // 알람 간격 설정
        setAlarmInterval(alarmManager, interval, calendar.getTimeInMillis(), pendingIntent);
    }

    private void setAlarmRepeatDays(Calendar calendar, String selectedDays) {
        // 선택한 요일에 알람 반복 설정
        String[] daysOfWeek = {"일", "월", "화", "수", "목", "금", "토"};
        for (int i = 0; i < daysOfWeek.length; i++) {
            if (selectedDays.contains(daysOfWeek[i])) {
                int dayOfWeek = i + 1; // Calendar.DAY_OF_WEEK에 맞게 설정
                calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);

                // 선택한 요일에 알람이 울리도록 설정
                if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    calendar.add(Calendar.DAY_OF_YEAR, 7); // 이미 지난 경우 다음 주로 설정
                }
                break;
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private void setAlarmInterval(AlarmManager alarmManager, String interval, long triggerTime, PendingIntent pendingIntent) {
        long intervalMillis;
        switch (interval) {
            case "3시간 간격":
                intervalMillis = 3 * 60 * 60 * 1000; // 3시간
                break;
            case "4시간 간격":
                intervalMillis = 4 * 60 * 60 * 1000; // 4시간
                break;
            case "5시간 간격":
                intervalMillis = 5 * 60 * 60 * 1000; // 5시간
                break;
            default:
                intervalMillis = 0;
                break;
        }

        // 알람 등록
        if (intervalMillis > 0) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    intervalMillis,
                    pendingIntent
            );
        } else {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
            );
        }
    }

    private void saveAlarmToFirebase(String alarmName, int hour, int minute, String selectedDays, String interval) {
        // Firebase에 알람 정보 저장
        String key = databaseReference.push().getKey();
        if (key != null) {
            AlarmInfo alarmInfo = new AlarmInfo(alarmName, hour, minute, selectedDays, interval);
            databaseReference.child(key).setValue(alarmInfo);
        }
    }

    // 알람을 식별하기 위한 고유한 requestCode 생성 메서드
    private int generateRequestCode(String alarmName) {
        // 간단한 해시값을 이용하여 requestCode 생성
        return alarmName.hashCode();
    }

    private void speakAlarmSetMessage(String alarmName) {
        String ttsMessage = alarmName + " 알람 설정이 완료되었습니다.";
        textToSpeech.speak(ttsMessage, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    protected void onDestroy() {
        // TTS 리소스 해제
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
