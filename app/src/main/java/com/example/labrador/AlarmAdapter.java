package com.example.labrador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class AlarmAdapter extends BaseAdapter {
    private List<AlarmModel> alarmList;
    private Context context;

    public AlarmAdapter(Context context, List<AlarmModel> alarmList) {
        this.context = context;
        this.alarmList = alarmList;
    }

    @Override
    public int getCount() {
        return alarmList.size();
    }

    @Override
    public Object getItem(int position) {
        return alarmList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.alarm_list_item, parent, false);
        }

        TextView tvAlarmName = convertView.findViewById(R.id.tvAlarmName);
        TextView tvAlarmTime = convertView.findViewById(R.id.tvAlarmTime);

        AlarmModel alarm = (AlarmModel) getItem(position);

        if (alarm != null) {
            tvAlarmName.setText(alarm.getAlarmName());
            tvAlarmTime.setText(alarm.getAlarmTime());
        }

        return convertView;
    }
}
