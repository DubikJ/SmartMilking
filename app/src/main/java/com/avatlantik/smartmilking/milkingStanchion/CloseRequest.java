package com.avatlantik.smartmilking.milkingStanchion;

public class CloseRequest extends BaseRequest {
    @Override
    protected byte[] toBytes(byte[] buffer) {
        buffer[1] = 0x07;
        return buffer;
    }
}
