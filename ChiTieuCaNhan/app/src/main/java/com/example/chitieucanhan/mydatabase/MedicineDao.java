package com.example.chitieucanhan.mydatabase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MedicineDao {
    @Insert
    Long insertMedicine(MedicineEntity medicine);

    @Query("DELETE FROM MEDICINE WHERE id = :medId")
    void deleteMedicine(int medId);

    @Update
    void updateMedicine(MedicineEntity medicine);

    @Query("SELECT * FROM MEDICINE")
    List<MedicineEntity> getAllMedicine();

    @Query("SELECT * FROM MEDICINE WHERE id = :medId")
    MedicineEntity getMedicine(int medId);

}
