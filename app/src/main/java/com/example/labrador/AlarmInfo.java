package com.example.labrador;

public class AlarmInfo {
    private String alarmName;
    private int hour;
    private int minute;
    private String selectedDays; // 요일 정보를 저장할 수 있도록 수정
    private String interval;

    // 생성자, getter, setter 등을 추가

    public AlarmInfo() {
        // Firebase에서 객체를 읽을 때 필요한 기본 생성자
    }

    public AlarmInfo(String alarmName, int hour, int minute, String selectedDays, String interval) {
        this.alarmName = alarmName;
        this.hour = hour;
        this.minute = minute;
        this.selectedDays = selectedDays;
        this.interval = interval;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getSelectedDays() {
        return selectedDays;
    }

    public void setSelectedDays(String selectedDays) {
        this.selectedDays = selectedDays;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }
}
