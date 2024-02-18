package com.example.chitieucanhan.mydatabase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "MEDICINE")
public class MedicineEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String MED_NAME;
    public int QTY;
    public String DATE_TIME;
    public String DAYS;
    public int ENABLE;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMED_NAME() {
        return MED_NAME;
    }

    public void setMED_NAME(String MED_NAME) {
        this.MED_NAME = MED_NAME;
    }

    public int getQTY() {
        return QTY;
    }

    public void setQTY(int QTY) {
        this.QTY = QTY;
    }

    public String getDATE_TIME() {
        return DATE_TIME;
    }

    public void setDATE_TIME(String DATE_TIME) {
        this.DATE_TIME = DATE_TIME;
    }

    public String getDAYS() {
        return DAYS;
    }

    public void setDAYS(String DAYS) {
        this.DAYS = DAYS;
    }

    public int getENABLE() {
        return ENABLE;
    }

    public void setENABLE(int ENABLE) {
        this.ENABLE = ENABLE;
    }

//    public MedicineEntity(int id, String MED_NAME, int QTY, String DATE_TIME, String DAYS, int ENABLE) {
//        this.id = id;
//        this.MED_NAME = MED_NAME;
//        this.QTY = QTY;
//        this.DATE_TIME = DATE_TIME;
//        this.DAYS = DAYS;
//        this.ENABLE = ENABLE;
//    }
public static MedicineEntity createDefault() {
    MedicineEntity medicineEntity = new MedicineEntity();
    medicineEntity.MED_NAME = "Default Name";
    medicineEntity.QTY = 0;
    medicineEntity.DATE_TIME = "2022-01-01";
    medicineEntity.DAYS = "Monday";
    medicineEntity.ENABLE = 1;
    return medicineEntity;
}
}

