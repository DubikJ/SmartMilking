package com.avatlantik.smartmilking.model.db;

import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface MilkingRecordDao {
    @Query("SELECT * FROM milking_record")
    Flowable<List<MilkingRecord>> getAll();

    @RawQuery(observedEntities = MilkingRecord.class)
    Flowable<List<MilkingRecord>> getByQuery(SimpleSQLiteQuery query);

    @Query("SELECT sum(litres) FROM milking_record WHERE date >= :dateStart AND date <= :dateEnd")
    Flowable<Double> getSumLitresByPeriod(long dateStart, long dateEnd);

//    @Query("SELECT sum(litres) FROM milking_record")
//    Flowable<Double> getSumLitresByPeriod();

    @Query("SELECT COUNT(*) from milking_record")
    int count();

    @Query("SELECT * FROM milking_record WHERE id = :id")
    MilkingRecord getById(int id);

    @Query("SELECT * FROM milking_record WHERE id_cow = :idCow")
    Flowable<List<MilkingRecord>> getByCowId(int idCow);

    @Insert
    void insert(MilkingRecord milkingRecord);

    @Update
    void update(MilkingRecord milkingRecord);

    @Insert
    void insertList(List<MilkingRecord> milkingRecordList);

    @Update
    void updateList(List<MilkingRecord> milkingRecordList);

    @Query("DELETE FROM milking_record")
    void deleteAll();
}
