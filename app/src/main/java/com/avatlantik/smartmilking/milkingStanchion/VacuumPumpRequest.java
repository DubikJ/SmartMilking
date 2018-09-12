package com.avatlantik.smartmilking.milkingStanchion;

public class VacuumPumpRequest extends BaseRequest {

    private Boolean start;

    VacuumPumpRequest(Boolean start) {
        this.start = start;
    }

    @Override
    protected byte[] toBytes(byte[] buffer) {
        buffer[1] = 0x03;
        buffer[2] = (byte) (start ? 0x01 : 0x00);
        return buffer;
    }
}
