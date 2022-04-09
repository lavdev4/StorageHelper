package com.lavdevapp.storagehelper.data.containers;

import androidx.room.ColumnInfo;

public class GroupWithTotals {

    @ColumnInfo(name = "group_name")
    public String groupName;

    @ColumnInfo(name = "month_items_total_quantity")
    public int monthItemsTotalQuantity;

    @ColumnInfo(name = "year_items_total_quantity")
    public int yearItemsTotalQuantity;
}
