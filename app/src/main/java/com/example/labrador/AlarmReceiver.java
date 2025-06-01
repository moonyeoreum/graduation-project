package com.example.labrador;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 알람이 울릴 때 실행되는 부분
        String alarmName = intent.getStringExtra("alarmName");
        Toast.makeText(context, "알람 울림: " + alarmName, Toast.LENGTH_SHORT).show();
    }
}


