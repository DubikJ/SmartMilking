package com.avatlantik.smartmilking.rfid;

public interface JackScanInterface {

    void onConnect(String deviceSn);

    void onDisconnect();

    void onStatusChange(JackScan.JackScanStatus jackScanStatus, int percentVolume);

    void onResult(String result);

    void onVolumeChange(int percentVolume);

    void onStartScan(boolean started, int status);

    void onEndScan();

    void onCheckBattery(String result);
}
