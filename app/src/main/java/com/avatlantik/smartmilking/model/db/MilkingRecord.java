package com.avatlantik.smartmilking.model.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "milking_record",
        indices = {@Index(value = {"id_place", "id_mashine", "id_cow", "date"}, unique = true)})
public class MilkingRecord implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "id_place")
    private int idPlace;

    @ColumnInfo(name = "id_mashine")
    private int idMachine;

    @ColumnInfo(name = "id_cow")
    private int idCow;

    @ColumnInfo(name = "litres")
    private double litres;

    @ColumnInfo(name = "date")
    private long date;

    public MilkingRecord(int idPlace, int idMachine, int idCow, double litres, long date) {
        this.idPlace = idPlace;
        this.idMachine = idMachine;
        this.idCow = idCow;
        this.litres = litres;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPlace() {
        return idPlace;
    }

    public void setIdPlace(int idPlace) {
        this.idPlace = idPlace;
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int idPlace;
        private int idMachine;
        private int idCow;
        private double litres;
        private long date;

        public Builder idPlace(int idPlace) {
            this.idPlace = idPlace;
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

        public Builder date(long date) {
            this.date = date;
            return this;
        }

        public MilkingRecord build() {
            return new MilkingRecord(idPlace, idMachine, idCow, litres, date);
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.idPlace);
        dest.writeInt(this.idMachine);
        dest.writeInt(this.idCow);
        dest.writeDouble(this.litres);
        dest.writeLong(this.date);
    }

    protected MilkingRecord(Parcel in) {
        this.idPlace = in.readInt();
        this.idMachine = in.readInt();
        this.idCow = in.readInt();
        this.litres = in.readDouble();
        this.date = in.readLong();
    }

    public static final Creator<MilkingRecord> CREATOR = new Creator<MilkingRecord>() {
        @Override
        public MilkingRecord createFromParcel(Parcel source) {
            return new MilkingRecord(source);
        }

        @Override
        public MilkingRecord[] newArray(int size) {
            return new MilkingRecord[size];
        }
    };

    @Override
    public String toString() {
        return "";
    }
}
