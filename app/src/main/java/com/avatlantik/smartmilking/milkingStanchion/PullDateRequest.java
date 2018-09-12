package com.avatlantik.smartmilking.milkingStanchion;
import com.avatlantik.smartmilking.utils.BCDParser;

import org.joda.time.LocalDate;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class PullDateRequest extends BaseRequest implements Serializable {

    @Override
    protected byte[] toBytes(byte[] buffer) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        byteBuffer.put(1, (byte) 0x01);

        byte[] dateByte = BCDParser.encodeInt(getArrayDate(LocalDate.now()));
        for (int i = 0; i < dateByte.length; i++) {
            byteBuffer.put(i + 2, dateByte[i]);
        }

        return byteBuffer.array();
    }

    private int[] getArrayDate(LocalDate localDate){
        int[] arrayDate = new int[5];
        arrayDate[0] = localDate.getDayOfMonth();
        arrayDate[1] = localDate.getMonthOfYear();
        arrayDate[2] = localDate.getYear()-2000;
        arrayDate[3] = localDate.toDateTimeAtCurrentTime().getHourOfDay();
        arrayDate[4] = localDate.toDateTimeAtCurrentTime().getMinuteOfHour();

        return arrayDate;
    }
}
