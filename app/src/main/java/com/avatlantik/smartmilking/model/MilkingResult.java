package com.avatlantik.smartmilking.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class MilkingResult implements Parcelable {

    public static final int STATUS_WORKED = 0;
    public static final int STATUS_ACCIDENT = 1;
    public static final int STATUS_ClOSED = -1;
    public static final int WISHING_ALLOWED = 0;
    public static final int WISHING_FORBIDDEN = 1;

    private int status;
    private int wishingAloowed;
    private Date date;
    private boolean vacuumPumpStart;
    private boolean milkPumpStart;
    private double litres1;
    private double litres2;
    private double litres3;
    private double litres4;
    private boolean milkingStart1;
    private boolean milkingStart2;
    private boolean milkingStart3;
    private boolean milkingStart4;

    public MilkingResult(int status, int wishingAloowed, Date date, boolean vacuumPumpStart,
                         boolean milkPumpStart, double litres1,
                         double litres2, double litres3, double litres4,
                         boolean milkingStart1, boolean milkingStart2,
                         boolean milkingStart3, boolean milkingStart4) {
        this.status = status;
        this.wishingAloowed = wishingAloowed;
        this.date = date;
        this.vacuumPumpStart = vacuumPumpStart;
        this.milkPumpStart = milkPumpStart;
        this.litres1 = litres1;
        this.litres2 = litres2;
        this.litres3 = litres3;
        this.litres4 = litres4;
        this.milkingStart1 = milkingStart1;
        this.milkingStart2 = milkingStart2;
        this.milkingStart3 = milkingStart3;
        this.milkingStart4 = milkingStart4;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getWishingAloowed() {
        return wishingAloowed;
    }

    public void setWishingAloowed(int wishingAloowed) {
        this.wishingAloowed = wishingAloowed;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isVacuumPumpStart() {
        return vacuumPumpStart;
    }

    public void setVacuumPumpStart(boolean vacuumPumpStart) {
        this.vacuumPumpStart = vacuumPumpStart;
    }

    public boolean isMilkPumpStart() {
        return milkPumpStart;
    }

    public void setMilkPumpStart(boolean milkPumpStart) {
        this.milkPumpStart = milkPumpStart;
    }

    public double getLitres1() {
        return litres1;
    }

    public void setLitres1(double litres1) {
        this.litres1 = litres1;
    }

    public double getLitres2() {
        return litres2;
    }

    public void setLitres2(double litres2) {
        this.litres2 = litres2;
    }

    public double getLitres3() {
        return litres3;
    }

    public void setLitres3(double litres3) {
        this.litres3 = litres3;
    }

    public double getLitres4() {
        return litres4;
    }

    public void setLitres4(double litres4) {
        this.litres4 = litres4;
    }

    public boolean isMilkingStart1() {
        return milkingStart1;
    }

    public void setMilkingStart1(boolean milkingStart1) {
        this.milkingStart1 = milkingStart1;
    }

    public boolean isMilkingStart2() {
        return milkingStart2;
    }

    public void setMilkingStart2(boolean milkingStart2) {
        this.milkingStart2 = milkingStart2;
    }

    public boolean isMilkingStart3() {
        return milkingStart3;
    }

    public void setMilkingStart3(boolean milkingStart3) {
        this.milkingStart3 = milkingStart3;
    }

    public boolean isMilkingStart4() {
        return milkingStart4;
    }

    public void setMilkingStart4(boolean milkingStart4) {
        this.milkingStart4 = milkingStart4;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int status;
        private int wishingAloowed;
        private Date date;
        private boolean vacuumPumpStart;
        private boolean milkPumpStart;
        private double litres1;
        private double litres2;
        private double litres3;
        private double litres4;
        private boolean milkingStart1;
        private boolean milkingStart2;
        private boolean milkingStart3;
        private boolean milkingStart4;

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder wishingAloowed(int wishingAloowed) {
            this.wishingAloowed = wishingAloowed;
            return this;
        }

        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        public Builder vacuumPumpStart(boolean vacuumPumpStart) {
            this.vacuumPumpStart = vacuumPumpStart;
            return this;
        }

        public Builder milkPumpStart(boolean milkPumpStart) {
            this.milkPumpStart = milkPumpStart;
            return this;
        }

        public Builder litres1(double litres) {
            this.litres1 = litres;
            return this;
        }

        public Builder litres2(double litres) {
            this.litres2 = litres;
            return this;
        }

        public Builder litres3(double litres) {
            this.litres3 = litres;
            return this;
        }

        public Builder litres4(double litres) {
            this.litres4 = litres;
            return this;
        }

        public Builder milkingStart1(boolean milkingStart) {
            this.milkingStart1 = milkingStart;
            return this;
        }

        public Builder milkingStart2(boolean milkingStart) {
            this.milkingStart2 = milkingStart;
            return this;
        }

        public Builder milkingStart3(boolean milkingStart) {
            this.milkingStart3 = milkingStart;
            return this;
        }

        public Builder milkingStart4(boolean milkingStart) {
            this.milkingStart4 = milkingStart;
            return this;
        }

        public MilkingResult build() {
            return new MilkingResult(status, wishingAloowed, date, vacuumPumpStart, milkPumpStart,
                    litres1, litres2, litres3, litres4,
            milkingStart1, milkingStart2, milkingStart3, milkingStart4);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.status);
        dest.writeInt(this.wishingAloowed);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
        dest.writeInt(this.vacuumPumpStart ? 1 : 0);
        dest.writeInt(this.milkPumpStart ? 1 : 0);
        dest.writeDouble(this.litres1);
        dest.writeDouble(this.litres2);
        dest.writeDouble(this.litres3);
        dest.writeDouble(this.litres4);
        dest.writeInt(this.milkingStart1 ? 1 : 0);
        dest.writeInt(this.milkingStart2 ? 1 : 0);
        dest.writeInt(this.milkingStart3 ? 1 : 0);
        dest.writeInt(this.milkingStart4 ? 1 : 0);
    }

    protected MilkingResult(Parcel in) {
        this.status = in.readInt();
        this.wishingAloowed = in.readInt();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.vacuumPumpStart = (in.readInt() == 0) ? false : true;
        this.milkPumpStart = (in.readInt() == 0) ? false : true;
        this.litres1 = in.readDouble();
        this.litres2 = in.readDouble();
        this.litres3 = in.readDouble();
        this.litres4 = in.readDouble();
        this.milkingStart1 = (in.readInt() == 0) ? false : true;
        this.milkingStart2 = (in.readInt() == 0) ? false : true;
        this.milkingStart3 = (in.readInt() == 0) ? false : true;
        this.milkingStart4 = (in.readInt() == 0) ? false : true;
    }

    public static final Creator<MilkingResult> CREATOR = new Creator<MilkingResult>() {
        @Override
        public MilkingResult createFromParcel(Parcel source) {
            return new MilkingResult(source);
        }

        @Override
        public MilkingResult[] newArray(int size) {
            return new MilkingResult[size];
        }
    };

    @Override
    public String toString() {
        return "";
    }
}
