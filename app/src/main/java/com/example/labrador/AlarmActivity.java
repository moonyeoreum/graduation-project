package com.example.labrador;

// AlarmActivity.java
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlarmActivity extends AppCompatActivity {
    private TextView tvCurrentTime;
    private Button btnAddAlarm;
    private ListView listViewAlarms;
    private List<AlarmModel> alarmList;
    private AlarmAdapter alarmAdapter;
    private static final int REQUEST_CODE_ADD_ALARM = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // UI 요소 초기화
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        btnAddAlarm = findViewById(R.id.btnAddAlarm);
        listViewAlarms = findViewById(R.id.listViewAlarms);

        // 알람 리스트 초기화
        alarmList = new ArrayList<>();
        alarmAdapter = new AlarmAdapter(this, alarmList);
        listViewAlarms.setAdapter(alarmAdapter);

        // 현재 시간 표시
        updateTime();

        // 알람 추가 버튼 클릭 이벤트 처리
        btnAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 알람 추가 버튼을 클릭하면 알람 세팅 엑티비티로 이동
                Intent intent = new Intent(AlarmActivity.this, TimePickerActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_ALARM);
            }
        });

        // 리스트뷰 아이템 클릭 이벤트 처리 (알람 해제 등의 동작을 추가할 수 있음)
        listViewAlarms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 여기에 리스트뷰 아이템 클릭 시 동작을 추가
                // 예를 들어, 알람 해제 다이얼로그를 띄워 해당 알람을 해제하거나 삭제하는 동작 수행
            }
        });
    }

    // 현재 시간을 텍스트뷰에 표시하는 메서드
    private void updateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        tvCurrentTime.setText("현재 시간: " + currentTime);
    }

    // onActivityResult 메서드 추가
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADD_ALARM && resultCode == RESULT_OK) {
            // 알람 추가 액티비티에서 결과를 받아서 처리하는 부분
            // 결과로 받은 알람 정보를 리스트에 추가하고 어댑터를 갱신
            String alarmName = data.getStringExtra("alarmName");
            String alarmTime = data.getStringExtra("alarmTime");

            // 새로운 알람 모델 생성
            AlarmModel newAlarm = new AlarmModel(alarmName, alarmTime);

            // 알람 리스트에 추가
            alarmList.add(newAlarm);

            // 어댑터 갱신
            alarmAdapter.notifyDataSetChanged();
        }
    }
}
