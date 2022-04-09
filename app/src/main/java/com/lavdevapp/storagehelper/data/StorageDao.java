package com.lavdevapp.storagehelper.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.lavdevapp.storagehelper.data.containers.GroupWithTotals;
import com.lavdevapp.storagehelper.data.containers.ItemWithTotals;
import com.lavdevapp.storagehelper.data.containers.YearAndMonthItemTotals;
import com.lavdevapp.storagehelper.data.entities.EntryGroup;
import com.lavdevapp.storagehelper.data.entities.EntryItem;
import com.lavdevapp.storagehelper.data.entities.StorageItem;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

@Dao
public interface StorageDao {

    //---Insert----------------------------------------------------------------------------------------

    @Insert
    Completable insertEntryGroup(EntryGroup entryGroup);

    @Transaction
    @Query("INSERT OR IGNORE INTO groups(date, group_name) " +
            "SELECT :date as date, group_name " +
            "FROM entry_groups " +
            "WHERE NOT EXISTS " +
            "(SELECT date, group_name FROM groups WHERE date = :date AND groups.group_name = entry_groups.group_name)")
    Completable insertEntryGroupsInGroups(int date);

    @Transaction
    @Query("INSERT OR IGNORE INTO items(date, item_name, parent_group_name, quantity) " +
            "SELECT :date as date, item_name, parent_group_name, quantity " +
            "FROM entry_items " +
            "WHERE NOT EXISTS " +
            "(SELECT item_name, parent_group_name FROM items WHERE date = :date AND items.item_name = entry_items.item_name AND items.parent_group_name = entry_items.parent_group_name)")
    Completable insertEntryItemsInItems(int date);

    @Query("INSERT INTO entry_items(item_name, parent_group_name, quantity) " +
            "VALUES (:itemName, :parentName, 0)")
    void insertEntryItems(String itemName, String parentName);

    @Insert
    Completable insertStorageItem(StorageItem storageItem);

    //---Update----------------------------------------------------------------------------------------

    @Update
    Completable updateEntryItem(EntryItem item);

    @Update
    Completable updateStorageItem(StorageItem item);

    @Query("UPDATE OR FAIL entry_items " +
            "SET quantity = 0")
    Completable nullifyAllEntryItems();

    @Query("UPDATE OR FAIL entry_items " +
            "SET quantity = quantity + 1 " +
            "WHERE id = :id")
    Completable incrementEntryItem(int id);

    @Query("UPDATE OR FAIL entry_items " +
            "SET quantity = quantity - 1 " +
            "WHERE id = :id " +
            "AND quantity != 0")
    Completable decrementEntryItem(int id);

    @Transaction
    @Query("UPDATE OR IGNORE items " +
            "SET quantity = quantity + " +
            "(SELECT quantity FROM entry_items WHERE entry_items.item_name = items.item_name AND entry_items.parent_group_name = items.parent_group_name) " +
            "WHERE date = :date")
    Completable updateItemsByEntryItemsValues(int date);

    @Query("UPDATE OR FAIL storage " +
            "SET quantity = 0")
    Completable nullifyAllStorageItems();

    @Query("UPDATE OR FAIL storage " +
            "SET quantity = quantity + 1 " +
            "WHERE item_name = :itemName")
    Completable incrementStorageItem(String itemName);

    @Query("UPDATE OR FAIL storage " +
            "SET quantity = quantity - 1 " +
            "WHERE item_name = :itemName " +
            "AND quantity != 0")
    Completable decrementStorageItem(String itemName);

    @Transaction
    @Query("UPDATE OR IGNORE storage " +
            "SET quantity = quantity - (SELECT SUM(quantity) FROM entry_items WHERE storage.item_name = entry_items.item_name)")
    Completable removeUsedItemsFromStorage();

    //---Delete----------------------------------------------------------------------------------------

    @Query("DELETE FROM groups " +
            "WHERE date % 10000 = :year")
    Completable deleteGroupsByYear(int year);

    @Query("DELETE FROM items " +
            "WHERE date % 10000 = :year")
    Completable deleteItemsByYear(int year);

    @Query("DELETE FROM groups " +
            "WHERE date / 10000 = :month")
    Completable deleteGroupsByMonth(int month);

    @Query("DELETE FROM items " +
            "WHERE date / 10000 = :month")
    Completable deleteItemsByMonth(int month);

    @Query("DELETE FROM entry_groups " +
            "WHERE group_name = :groupName")
    Completable deleteEntryGroup(String groupName);

    @Delete
    Completable deleteEntryItem(EntryItem entryItem);

    @Query("DELETE FROM entry_items " +
            "WHERE item_name = :itemName")
    Completable deleteEntryItemByName(String itemName);

    @Query("DELETE FROM entry_items " +
            "WHERE parent_group_name = :parentGroupName")
    Completable deleteEntryItemsByParentGroupName(String parentGroupName);

    @Delete
    Completable deleteStorageItem(StorageItem storageItems);

    //---Select----------------------------------------------------------------------------------------

    @Transaction
    @Query("SELECT month_group_name as group_name, month_items_total_quantity, year_items_total_quantity " +
            "FROM " +
            "(SELECT parent_group_name as month_group_name, SUM(quantity) as month_items_total_quantity FROM items WHERE date = :date AND items.date = :date GROUP BY month_group_name) " +
            "INNER JOIN " +
            "(SELECT parent_group_name as year_group_name, SUM(quantity) as year_items_total_quantity FROM items WHERE date % 10000 = :date % 10000 AND items.date % 10000 = :date % 10000 GROUP BY year_group_name) " +
            "ON month_group_name = year_group_name")
    Single<List<GroupWithTotals>> getGroupsWithTotalCounts(int date);

    @Query("SELECT * FROM entry_groups " +
            "ORDER BY group_name " +
            "LIMIT 1")
    Maybe<EntryGroup> getFirstEntryGroup();

    @Query("SELECT * FROM entry_groups")
    Observable<List<EntryGroup>> getObservableEntryGroups();

    @Transaction
    @Query("SELECT count(*) " +
            "FROM " +
            "(SELECT 0 FROM entry_items LIMIT 1)")
    Single<Integer> checkIfEntryItemsEmpty();

    @Transaction
    @Query("SELECT month_item_name as item_name, month_items_total_quantity, year_items_total_quantity " +
            "FROM " +
            "(SELECT item_name as month_item_name, SUM(quantity) as month_items_total_quantity FROM items WHERE date = :date AND items.date = :date AND parent_group_name = :groupName GROUP BY month_item_name) " +
            "INNER JOIN " +
            "(SELECT item_name as year_item_name, SUM(quantity) as year_items_total_quantity FROM items WHERE date % 10000 = :date % 10000 AND items.date % 10000 = :date % 10000 AND parent_group_name = :groupName GROUP BY year_item_name) " +
            "ON month_item_name = year_item_name")
    Single<List<ItemWithTotals>> getItemsWithTotalCounts(int date, String groupName);

    @Query("SELECT * FROM entry_items " +
            "WHERE parent_group_name = :groupName")
    Observable<List<EntryItem>> getObservableEntryItems(String groupName);

    @Query("SELECT DISTINCT date % 10000 as year " +
            "FROM groups")
    Single<List<Integer>> getYears();

    @Query("SELECT DISTINCT date / 10000 as month " +
            "FROM groups " +
            "WHERE date % 10000 = :year")
    Single<List<Integer>> getMonths(int year);

    @Transaction
    @Query("SELECT SUM(quantity) as year_items_total_quantity, " +
            "(SELECT SUM(quantity) FROM items WHERE items.date % 10000 = :year AND items.date / 10000 = :month) as month_items_total_quantity " +
            "FROM items " +
            "WHERE items.date % 10000 = :year")
    Single<YearAndMonthItemTotals> getYearAndMothItemTotals(int year, int month);

    @Query("SELECT storage.quantity - SUM(entry_items.quantity) " +
            "FROM entry_items, storage " +
            "WHERE entry_items.item_name = :itemName " +
            "AND storage.item_name = :itemName")
    Maybe<Integer> getStorageRemainingAmount(String itemName);

    @Query("SELECT storage.quantity - SUM(entry_items.quantity) + " +
            "(SELECT quantity FROM entry_items WHERE item_name = :itemName AND parent_group_name = :parentGroupName)" +
            "FROM entry_items, storage " +
            "WHERE entry_items.item_name = :itemName " +
            "AND storage.item_name = :itemName")
    Maybe<Integer> getStoragePotentialRemainingAmount(String itemName, String parentGroupName);

    @Transaction
    @Query("SELECT item_name FROM storage " +
            "WHERE item_name " +
            "NOT IN " +
            "(SELECT item_name FROM entry_items WHERE parent_group_name = :groupName)")
    Single<List<String>> getUnusedItemsFromStorage(String groupName);

    @Query("SELECT * FROM storage")
    Observable<List<StorageItem>> getObservableStorageItems();
}
