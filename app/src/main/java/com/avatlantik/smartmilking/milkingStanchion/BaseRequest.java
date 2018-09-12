package com.avatlantik.smartmilking.milkingStanchion;

abstract class BaseRequest {

    public static final byte[] OK = {111,107};

    protected abstract byte[] toBytes(byte[] buffer);

    byte[] toBytes() {
        byte[] buffer = new byte[8];
        buffer[0] = 0x08;
        buffer[7] = 0x09;
        return toBytes(buffer);
    }

    static boolean equals(byte[] f1, byte[] f2) {

        if (f1.length != f2.length)
            return false;
        else {
            for (int i = 0; i < f1.length; i++) {
                if (f1[i] != f2[i])
                    return false;
            }
            return true;
        }
    }

}
