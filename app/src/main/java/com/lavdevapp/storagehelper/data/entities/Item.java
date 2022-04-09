package com.lavdevapp.storagehelper.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "items")
public class Item {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int date;

    @ColumnInfo(name = "item_name")
    public String itemName;

    @ColumnInfo(name = "parent_group_name")
    public String parentGroupName;

    @ColumnInfo(defaultValue = "0")
    public int quantity;
}
