package com.avatlantik.smartmilking.milkingStanchion;

import com.avatlantik.smartmilking.utils.BCDParser;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class MilkingRequest extends BaseRequest implements Serializable {

    private int idMashine;
    private int idCow;

    MilkingRequest(int idMashine, int idCow) {
        this.idMashine = idMashine;
        this.idCow = idCow;
    }

    @Override
    protected byte[] toBytes(byte[] buffer) {

        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        byteBuffer.put(1, BCDParser.toBcd(2));
        byteBuffer.put(2, BCDParser.toBcd(idMashine));

        byte[] dateByte = BCDParser.encodeInt(getArrayInt());
        for (int i = 0; i < dateByte.length; i++) {
            byteBuffer.put(i + 3, dateByte[i]);
        }

        return byteBuffer.array();
    }

    private int[] getArrayInt(){
        int j = 0;
        int len = Integer.toString(idCow).length();
        int[] arr = new int[len];
        while(idCow!=0) {
            arr[len-j-1] = idCow%10;
            idCow = idCow/10;
            j++;
        }

        int lenArray = 4;
        j =0;
        int[] array = new int[lenArray];
        for (int i = lenArray; i >=0; i--) {
            if((arr.length-j-1)>=0) {
                array[array.length - j - 1] = arr[arr.length - j - 1];
            }
            j++;
        }

        return array;
    }
}
