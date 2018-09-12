package com.avatlantik.smartmilking.component;

import com.avatlantik.smartmilking.activity.MilkingActivity;
import com.avatlantik.smartmilking.activity.MilkingReportActivity;
import com.avatlantik.smartmilking.modules.ActivityUtilsApiModule;
import com.avatlantik.smartmilking.modules.DataApiModule;
import com.avatlantik.smartmilking.modules.NetworkUtilsApiModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {DataApiModule.class, ActivityUtilsApiModule.class,
        NetworkUtilsApiModule.class})
public interface DIComponent {
    void inject(MilkingActivity milkingActivity);
    void inject(MilkingReportActivity milkingReportActivity);
}
