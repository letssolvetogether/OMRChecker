package com.letssolvetogether.omr.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class OMRKey {
    @PrimaryKey
    private int omrkeyid;

    @ColumnInfo(name = "correct_answers")
    private String strCorrectAnswers;

    public int getOmrkeyid() {
        return omrkeyid;
    }

    public void setOmrkeyid(int omrkeyid) {
        this.omrkeyid = omrkeyid;
    }

    public String getStrCorrectAnswers() {
        return strCorrectAnswers;
    }

    public void setStrCorrectAnswers(String strCorrectAnswers) {
        this.strCorrectAnswers = strCorrectAnswers;
    }
}