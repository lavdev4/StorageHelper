package com.lavdevapp.storagehelper.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "groups")
public class Group {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int date;

    @ColumnInfo(name = "group_name")
    public String groupName;
}
