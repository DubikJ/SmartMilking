package com.avatlantik.smartmilking.milkingStanchion;

public class MilkPumpRequest extends BaseRequest {


    private Boolean start;

    MilkPumpRequest(Boolean start) {
        this.start = start;
    }

    @Override
    protected byte[] toBytes(byte[] buffer) {
        buffer[1] = 0x04;
        buffer[2] = (byte) (start ? 0x01 : 0x00);
        return buffer;
    }
}
