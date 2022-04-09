package com.lavdevapp.storagehelper.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.lavdevapp.storagehelper.data.entities.EntryGroup;
import com.lavdevapp.storagehelper.data.entities.EntryItem;
import com.lavdevapp.storagehelper.data.entities.Group;
import com.lavdevapp.storagehelper.data.entities.Item;
import com.lavdevapp.storagehelper.data.entities.StorageItem;

@Database(entities = {Group.class, Item.class, StorageItem.class, EntryGroup.class, EntryItem.class},
        version = 1, exportSchema = false)
public abstract class StorageDatabase extends RoomDatabase {
    private static volatile StorageDatabase INSTANCE;

    public abstract StorageDao getDao();

    private static StorageDatabase buildDatabase(Context context) {
        return Room.databaseBuilder(context, StorageDatabase.class, "storage_helper_database").build();
    }

    public static StorageDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (StorageDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = buildDatabase(context);
                }
            }
        }
        return INSTANCE;
    }
}
