package com.example.labrador;

public class AlarmModel {
    private String alarmName;
    private String alarmTime;

    public AlarmModel(String alarmName, String alarmTime) {
        this.alarmName = alarmName;
        this.alarmTime = alarmTime;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public String getAlarmTime() {
        return alarmTime;
    }
}

