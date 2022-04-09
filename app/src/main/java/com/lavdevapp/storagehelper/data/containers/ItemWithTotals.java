package com.lavdevapp.storagehelper.data.containers;

import androidx.room.ColumnInfo;

public class ItemWithTotals {

    @ColumnInfo(name = "item_name")
    public String itemName;

    @ColumnInfo(name = "month_items_total_quantity")
    public int monthItemsTotalQuantity;

    @ColumnInfo(name = "year_items_total_quantity")
    public int yearItemsTotalQuantity;
}
