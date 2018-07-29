package com.letssolvetogether.omr.omrkey.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface OMRKeyDao {

    @Query("SELECT * FROM omrkey WHERE omrkeyid = :omrkeyid")
    OMRKey findById(int omrkeyid);

    @Insert(onConflict = REPLACE)
    void insertOMRKey(OMRKey omrKey);
}