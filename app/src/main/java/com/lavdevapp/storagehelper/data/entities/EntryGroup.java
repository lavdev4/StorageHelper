package com.lavdevapp.storagehelper.data.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "entry_groups")
public class EntryGroup {

    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "group_name")
    public String groupName;

    public EntryGroup(@NonNull String groupName) {
        this.groupName = groupName;
    }
}