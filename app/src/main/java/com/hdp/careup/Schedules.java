package com.hdp.careup;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import com.google.type.DateTime;

import java.sql.Time;
import java.util.List;

@Entity(tableName = "schedules")
public class Schedules {

    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "title")
    String title;

    @ColumnInfo(name = "child_id")
    String childId;

//    @ColumnInfo(name = "start")
//    DateTime start;

//    @ColumnInfo(name = "end")
//    DateTime end;

    @ColumnInfo(name = "lat")
    long lat;

    @ColumnInfo(name = "lang")
    long lang;

    @ColumnInfo(name = "stat")
    int stat;
}
