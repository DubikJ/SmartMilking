package com.avatlantik.smartmilking.milkingStanchion;

public interface MilkingStancionParams {

    enum ConnectionStatus {
        CONNECTED,
        CONNECTION,
        NOT_CONNECTED,
        WIFI_OFF,
        NOT_CONNECTED_WIFI,
        ACCIDENT
    }
}
