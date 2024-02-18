package com.example.chitieucanhan.mydatabase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {MedicineEntity.class}, version = 1)
public abstract class RemindDatabase extends RoomDatabase {
    public abstract MedicineDao medicineDao();
}

