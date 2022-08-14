package com.hdp.careup;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Schedules.class}, version = 1)
public abstract class AppDB extends RoomDatabase {
    public abstract SchedulesDao SchedulesDao();
}
