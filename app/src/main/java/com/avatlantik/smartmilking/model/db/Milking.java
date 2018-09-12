package com.avatlantik.smartmilking.model.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "milking")
public class Milking implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "id_mashine")
    private int idMachine;

    @ColumnInfo(name = "id_cow")
    private int idCow;

    @ColumnInfo(name = "litres")
    private double litres;

    @ColumnInfo(name = "milking_start")
    private boolean milkingStart;

    @ColumnInfo(name = "milking_end")
    private boolean milkingEnd;

    public Milking(int id, int idMachine, int idCow, double litres,
                   boolean milkingStart, boolean milkingEnd) {
        this.id = id;
        this.idMachine = idMachine;
        this.idCow = idCow;
        this.litres = litres;
        this.milkingStart = milkingStart;
        this.milkingEnd = milkingEnd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdMachine() {
        return idMachine;
    }

    public void setIdMachine(int idMachine) {
        this.idMachine = idMachine;
    }

    public int getIdCow() {
        return idCow;
    }

    public void setIdCow(int idCow) {
        this.idCow = idCow;
    }

    public double getLitres() {
        return litres;
    }

    public void setLitres(double litres) {
        this.litres = litres;
    }

    public boolean isMilkingStart() {
        return milkingStart;
    }

    public void setMilkingStart(boolean milkingStart) {
        this.milkingStart = milkingStart;
    }

    public boolean isMilkingEnd() {
        return milkingEnd;
    }

    public void setMilkingEnd(boolean milkingEnd) {
        this.milkingEnd = milkingEnd;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int id;
        private int idMachine;
        private int idCow;
        private double litres;
        private boolean milkingStart;
        private boolean milkingEnd;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder idMachine(int idMachine) {
            this.idMachine = idMachine;
            return this;
        }

        public Builder idCow(int idCow) {
            this.idCow = idCow;
            return this;
        }

        public Builder litres(double litres) {
            this.litres = litres;
            return this;
        }

        public Builder milkingStart(boolean milkingStart) {
            this.milkingStart = milkingStart;
            return this;
        }

        public Builder milkingEnd(boolean milkingEnd) {
            this.milkingEnd = milkingEnd;
            return this;
        }

        public Milking build() {
            return new Milking(id, idMachine, idCow, litres,
            milkingStart, milkingEnd);
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.idMachine);
        dest.writeInt(this.idCow);
        dest.writeDouble(this.litres);
        dest.writeInt(this.milkingStart ? 1 : 0);
        dest.writeInt(this.milkingEnd ? 1 : 0);
    }

    protected Milking(Parcel in) {
        this.id = in.readInt();
        this.idMachine = in.readInt();
        this.idCow = in.readInt();
        this.litres = in.readDouble();
        this.milkingStart = (in.readInt() == 0) ? false : true;
        this.milkingEnd = (in.readInt() == 0) ? false : true;
    }

    public static final Creator<Milking> CREATOR = new Creator<Milking>() {
        @Override
        public Milking createFromParcel(Parcel source) {
            return new Milking(source);
        }

        @Override
        public Milking[] newArray(int size) {
            return new Milking[size];
        }
    };

    @Override
    public String toString() {
        return "";
    }
}
