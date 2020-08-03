package com.letssolvetogether.omr.omrkey.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface OMRKeyDao {

    @Query("SELECT * FROM omrkey WHERE omrkeyid = :omrkeyid")
    OMRKey findById(int omrkeyid);

    @Insert(onConflict = REPLACE)
    void insertOMRKey(OMRKey omrKey);
}