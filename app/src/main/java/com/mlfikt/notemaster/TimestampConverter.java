package com.mlfikt.notemaster;

import androidx.room.TypeConverter;
import com.google.firebase.Timestamp;

public class TimestampConverter {
    @TypeConverter
    public static Timestamp fromTimestamp(Long value) {
        return value == null ? null : new Timestamp(value, 0);
    }

    @TypeConverter
    public static Long toTimestamp(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.getSeconds();
    }
}
