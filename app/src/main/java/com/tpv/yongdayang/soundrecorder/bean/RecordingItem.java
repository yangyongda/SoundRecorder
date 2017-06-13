package com.tpv.yongdayang.soundrecorder.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by YongDa.Yang on 2017/6/5.
 */

public class RecordingItem implements Parcelable{
    private String Name; // 文件名
    private String FilePath; //文件路径
    private int Id; //ID
    private int Length; // 长度
    private long Time; // 录音日期

    public RecordingItem()
    {
    }

    protected RecordingItem(Parcel in) {
        Name = in.readString();
        FilePath = in.readString();
        Id = in.readInt();
        Length = in.readInt();
        Time = in.readLong();
    }

    public static final Creator<RecordingItem> CREATOR = new Creator<RecordingItem>() {
        @Override
        public RecordingItem createFromParcel(Parcel in) {
            return new RecordingItem(in);
        }

        @Override
        public RecordingItem[] newArray(int size) {
            return new RecordingItem[size];
        }
    };

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getLength() {
        return Length;
    }

    public void setLength(int length) {
        Length = length;
    }

    public long getTime() {
        return Time;
    }

    public void setTime(long time) {
        Time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeInt(Length);
        dest.writeLong(Time);
        dest.writeString(FilePath);
        dest.writeString(Name);
    }
}
