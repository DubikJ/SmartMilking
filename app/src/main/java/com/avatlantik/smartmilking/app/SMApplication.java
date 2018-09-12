package com.avatlantik.smartmilking.app;

import android.app.Application;

import com.avatlantik.smartmilking.component.DIComponent;
import com.avatlantik.smartmilking.component.DaggerDIComponent;
import com.avatlantik.smartmilking.logs.ReportHandler;
import com.avatlantik.smartmilking.modules.ActivityUtilsApiModule;
import com.avatlantik.smartmilking.modules.DataApiModule;
import com.avatlantik.smartmilking.modules.NetworkUtilsApiModule;
import com.avatlantik.smartmilking.utils.PropertyUtils;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.IOException;
import java.util.Properties;

import static com.avatlantik.smartmilking.common.Consts.APPLICATION_PROPERTIES;

public class SMApplication extends Application {

    DIComponent diComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
        diComponent = DaggerDIComponent.builder()
                .dataApiModule(new DataApiModule(this))
                .activityUtilsApiModule(new ActivityUtilsApiModule())
                .networkUtilsApiModule(new NetworkUtilsApiModule(this))
                .build();

        initReportHandler();
    }

    public DIComponent getComponent() {
        return diComponent;
    }

    private void initReportHandler(){
        String mail;

        try {
            Properties properties = PropertyUtils.getProperties(APPLICATION_PROPERTIES, this);
            mail = properties.getProperty("dev-mail");
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read auth properties", e);
        }

        ReportHandler.install(this, mail);

    }
}
