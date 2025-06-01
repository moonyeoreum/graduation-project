package com.example.labrador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MedicineAdapter extends ArrayAdapter<Medicine> {

    private final Context context;
    private final int resource;
    private final List<Medicine> medicineList;

    public MedicineAdapter(Context context, int resource, List<Medicine> medicineList) {
        super(context, resource, medicineList);
        this.context = context;
        this.resource = resource;
        this.medicineList = medicineList;
    }

    // 생략...

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resource, parent, false);

            holder = new ViewHolder();
            holder.itemNameTextView = convertView.findViewById(R.id.itemName);
            holder.entpNameTextView = convertView.findViewById(R.id.entpName);
            holder.etcOtcCodeTextView = convertView.findViewById(R.id.etcOtcCode);
            holder.medicineImageView = convertView.findViewById(R.id.itemImage);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Medicine currentMedicine = medicineList.get(position);

        // 텍스트 뷰에 데이터 설정
        holder.itemNameTextView.setText(currentMedicine.getItemName());
        holder.entpNameTextView.setText(currentMedicine.getEntpName());
        holder.etcOtcCodeTextView.setText(currentMedicine.getEtcOtcCode());

        // 이미지 뷰에 아이템 이미지 설정 (Glide 사용)
        String itemImageUrl = currentMedicine.getItemImage();
        if (itemImageUrl != null && !itemImageUrl.isEmpty()) {
            Glide.with(context)
                    .load(itemImageUrl)
                    .error(R.drawable.default_image) // 에러 시 기본 이미지 설정
                    .into(holder.medicineImageView);
        } else {
            holder.medicineImageView.setImageResource(R.drawable.default_image);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView itemNameTextView;
        TextView entpNameTextView;
        TextView etcOtcCodeTextView;
        ImageView medicineImageView;
    }
}