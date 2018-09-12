package com.avatlantik.smartmilking.milkingStanchion;

import java.io.Serializable;

public class PullRequest extends BaseRequest implements Serializable {

    @Override
    protected byte[] toBytes(byte[] buffer) {
        buffer[1] = 0x05;
        return buffer;
    }
}
