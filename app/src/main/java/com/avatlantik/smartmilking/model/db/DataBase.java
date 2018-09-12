package com.avatlantik.smartmilking.model.db;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Milking.class, MilkingRecord.class},
        version = 1, exportSchema = false)
public abstract class DataBase extends RoomDatabase {
    public abstract MilkingDao milkingDao();
    public abstract MilkingRecordDao milkingRecordDao();

    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }
}