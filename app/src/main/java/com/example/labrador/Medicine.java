package com.example.labrador;

import android.os.Parcel;
import android.os.Parcelable;

public class Medicine implements Parcelable {

    private String itemName;
    private String entpName;
    private String etcOtcCode;
    private String itemImage;
    private String useMethodQesitm;
    private String atpnQesitm;

    public Medicine() {
        // 기본 생성자
    }

    public Medicine(String itemName, String entpName, String etcOtcCode, String itemImage, String useMethodQesitm, String atpnQesitm) {
        this.itemName = itemName;
        this.entpName = entpName;
        this.etcOtcCode = etcOtcCode;
        this.itemImage = itemImage;
        this.useMethodQesitm = useMethodQesitm;
        this.atpnQesitm = atpnQesitm;
    }

    protected Medicine(Parcel in) {
        itemName = in.readString();
        entpName = in.readString();
        etcOtcCode = in.readString();
        itemImage = in.readString();
        useMethodQesitm = in.readString();
        atpnQesitm = in.readString();
    }

    public static final Creator<Medicine> CREATOR = new Creator<Medicine>() {
        @Override
        public Medicine createFromParcel(Parcel in) {
            return new Medicine(in);
        }

        @Override
        public Medicine[] newArray(int size) {
            return new Medicine[size];
        }
    };

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getEntpName() {
        return entpName;
    }

    public void setEntpName(String entpName) {
        this.entpName = entpName;
    }

    public String getEtcOtcCode() {
        return etcOtcCode;
    }

    public void setEtcOtcCode(String etcOtcCode) {
        this.etcOtcCode = etcOtcCode;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public String getUseMethodQesitm() {
        return useMethodQesitm;
    }

    public void setUseMethodQesitm(String useMethodQesitm) {
        this.useMethodQesitm = useMethodQesitm;
    }

    public String getAtpnQesitm() {
        return atpnQesitm;
    }

    public void setAtpnQesitm(String atpnQesitm) {
        this.atpnQesitm = atpnQesitm;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemName);
        dest.writeString(entpName);
        dest.writeString(etcOtcCode);
        dest.writeString(itemImage);
        dest.writeString(useMethodQesitm);
        dest.writeString(atpnQesitm);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
