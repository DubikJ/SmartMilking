package com.avatlantik.smartmilking.modules;

import com.avatlantik.smartmilking.utils.ActivityUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityUtilsApiModule {

    public ActivityUtilsApiModule() {
    }

    @Provides
    @Singleton
    public ActivityUtils getActivityUtils() {
        return new ActivityUtils();
    }
}
