package com.lavdevapp.storagehelper.data.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "storage")
public class StorageItem {

    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "item_name")
    public String itemName;

    @ColumnInfo(defaultValue = "0")
    public int quantity;

    public StorageItem(@NonNull String itemName) {
        this.itemName = itemName;
    }
}
