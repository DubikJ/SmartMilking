package com.avatlantik.smartmilking.model.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface MilkingDao {
    @Query("SELECT * FROM milking")
    Flowable<List<Milking>> getAll();

    @Query("SELECT COUNT(*) from milking")
    int count();

    @Query("SELECT * FROM milking WHERE id = :id")
    Milking getById(int id);

    @Query("SELECT * FROM milking WHERE id_cow = :idCow")
    Flowable<List<Milking>> getByCowId(int idCow);

    @Insert
    void insert(Milking milking);

    @Update
    void update(Milking milking);

    @Insert
    void insertList(List<Milking> milkingList);

    @Update
    void updateList(List<Milking> milkingList);

    @Query("DELETE FROM milking")
    void deleteAll();
}
