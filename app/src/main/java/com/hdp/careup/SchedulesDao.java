package com.hdp.careup;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.google.type.DateTime;

import java.util.List;

@Dao
public interface SchedulesDao {
    @Query("SELECT * FROM schedules WHERE stat = '1'")
    List<Schedules> getAllSchedules();

//    @Query("SELECT * FROM schedules WHERE start = :start AND stat = '1'")
//    List<Schedules> getSchedulesStartAt(DateTime start);

    @Query("SELECT * FROM schedules WHERE stat = '1' AND id = :id")
    Schedules getSchedule(int id);

    @Insert
    void insert(Schedules... schedules);

//    @Update
//    void update(int id);

//    @Delete
//    void delete(int id);


}
