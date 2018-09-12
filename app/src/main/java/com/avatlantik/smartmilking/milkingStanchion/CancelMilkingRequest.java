package com.avatlantik.smartmilking.milkingStanchion;

import com.avatlantik.smartmilking.utils.BCDParser;

import java.io.Serializable;

public class CancelMilkingRequest extends BaseRequest implements Serializable {

    private int idMashine;

    CancelMilkingRequest(int idMashine) {
        this.idMashine = idMashine;
    }

    @Override
    protected byte[] toBytes(byte[] buffer) {
        buffer[1] = 0x06;
        buffer[2] = BCDParser.toBcd(idMashine);
        return buffer;
    }
}
