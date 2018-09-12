package com.avatlantik.smartmilking.modules;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.avatlantik.smartmilking.model.db.DataBase;
import com.avatlantik.smartmilking.model.db.MilkingDao;
import com.avatlantik.smartmilking.model.db.MilkingRecordDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.avatlantik.smartmilking.common.Consts.NAME_DATA_BASE_ROOM;

@Module
public class DataApiModule {

    private Application application;

    public DataApiModule(Application application) {
        this.application = application;
    }

    @Singleton
    @Provides
    public Application provideContext(){
        return application;
    }

    @Provides
    @Singleton
    public DataBase getDataRepository() {
        return Room.databaseBuilder(application.getBaseContext(), DataBase.class, NAME_DATA_BASE_ROOM).build();
    }

    @Singleton
    @Provides
    public MilkingDao provideMilkingDao(DataBase dataBase){
        return dataBase.milkingDao();
    }

    @Singleton
    @Provides
    public MilkingRecordDao provideMilkingRecordDao(DataBase dataBase){
        return dataBase.milkingRecordDao();
    }
}
