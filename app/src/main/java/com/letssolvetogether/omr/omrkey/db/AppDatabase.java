package com.letssolvetogether.omr.omrkey.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {OMRKey.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract OMRKeyDao omrKeyDao();
}
