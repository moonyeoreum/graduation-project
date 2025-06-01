package com.example.labrador;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DismissAlarmActivity extends AppCompatActivity {
    private TextView tvAlarmName;
    private TextView tvAlarmTime;
    private Button btnDismissAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dismiss_alarm_activity);

        // UI 요소 초기화
        tvAlarmName = findViewById(R.id.tvAlarmName);
        tvAlarmTime = findViewById(R.id.tvAlarmTime);
        btnDismissAlarm = findViewById(R.id.btnDismissAlarm);

        // 인텐트로 전달된 데이터 받기
        final String alarmName = getIntent().getStringExtra("alarmName");
        final String alarmTime = getIntent().getStringExtra("alarmTime");

        // 받아온 데이터로 텍스트뷰 업데이트
        tvAlarmName.setText("알람 이름: " + alarmName);
        tvAlarmTime.setText("알람 시간: " + alarmTime);

        // 알람 해제 버튼 클릭 이벤트 처리
        btnDismissAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 여기에 알람 해제 기능을 구현
                // 예를 들어, 알람 매니저를 사용하여 알람을 해제하는 등의 동작 수행

                // 알람을 식별하기 위한 고유한 requestCode 생성
                int requestCode = generateRequestCode(alarmName, alarmTime);

                // 알람 매니저를 통해 해당 알람을 해제하는 코드
                cancelAlarm(getApplicationContext(), requestCode);

                // 알람 해제 후 현재 액티비티 종료
                finish();
            }
        });
    }

    // 알람을 식별하기 위한 고유한 requestCode 생성 메서드
    private int generateRequestCode(String alarmName, String alarmTime) {
        // 간단한 해시값을 이용하여 requestCode 생성
        return (alarmName + alarmTime).hashCode();
    }

    // 알람을 해제하는 메서드
    private void cancelAlarm(Context context, int requestCode) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}