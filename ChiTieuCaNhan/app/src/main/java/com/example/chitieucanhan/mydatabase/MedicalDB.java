package com.example.chitieucanhan.mydatabase;

import android.content.Context;

import androidx.room.Room;

import java.util.List;

public class MedicalDB {
    private static RemindDatabase sInstance;

    public static synchronized RemindDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context.getApplicationContext(),
                            RemindDatabase.class, "databaseRemind")
                    .build();
        }
        return sInstance;
    }

    // Cập nhật các phương thức để sử dụng DAO của Room

    public void addMedicine(MedicineEntity medicine) {
        sInstance.medicineDao().insertMedicine(medicine);
    }

    public void deleteMedicine(int medId) {
        sInstance.medicineDao().deleteMedicine(medId);
    }

    public void setEnable(int id, int b) {
        MedicineEntity medicine = sInstance.medicineDao().getMedicine(id);
        if (medicine != null) {
            medicine.ENABLE = b;
            sInstance.medicineDao().updateMedicine(medicine);
        }
    }

    public List<MedicineEntity> getAllMedicine() {
        return sInstance.medicineDao().getAllMedicine();
    }

    public MedicineEntity getMedicine(int medId) {
        return sInstance.medicineDao().getMedicine(medId);
    }
}
