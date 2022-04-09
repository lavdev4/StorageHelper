package com.lavdevapp.storagehelper.data;

import android.app.Application;

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
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class StorageRepository {

    private final StorageDao dao;
    private final Scheduler scheduler;
    private final Scheduler thread;

    public StorageRepository(Application application) {
        dao = StorageDatabase.getInstance(application).getDao();
        scheduler = Schedulers.io();
        thread = AndroidSchedulers.mainThread();
    }

    //---Dao--Insert----------------------------------------------------------------------------------------

    public Completable insertEntryGroup(EntryGroup entryGroup) {
        return dao.insertEntryGroup(entryGroup)
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    public Completable insertEntryDataInStorageData(int date) {
        return dao.checkIfEntryItemsEmpty()
                .flatMapCompletable(rowsCount -> {
                    if (rowsCount != 0) {
                        return dao.insertEntryGroupsInGroups(date)
                                .andThen(dao.updateItemsByEntryItemsValues(date))
                                .andThen(dao.insertEntryItemsInItems(date))
                                .andThen(dao.removeUsedItemsFromStorage())
                                .andThen(dao.nullifyAllEntryItems());
                    } else {
                        return Completable.complete();
                    }
                })
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    public Completable insertEntryItems(List<String> itemNames, String parentGroupName) {
        return Completable.fromAction(() -> {
            for (String itemName : itemNames) {
                dao.insertEntryItems(itemName, parentGroupName);
            }
        })
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    public Completable insertStorageItem(StorageItem item) {
        return dao.insertStorageItem(item)
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    //---Dao--Update----------------------------------------------------------------------------------------

    public Completable updateEntryItem(EntryItem item) {
        return dao.updateEntryItem(item)
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    public Completable updateStorageItem(StorageItem item) {
        return dao.updateStorageItem(item)
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    public Completable incrementEntryItem(int id) {
        return dao.incrementEntryItem(id)
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    public Completable decrementEntryItem(int id) {
        return dao.decrementEntryItem(id)
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    public Completable nullifyAllStorageItems() {
        return dao.nullifyAllStorageItems()
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    public Completable incrementStorageItem(String itemName) {
        return dao.incrementStorageItem(itemName)
                .subscribeOn(scheduler)
                .observeOn(thread);
    }
    public Completable decrementStorageItem(String itemName) {
        return dao.decrementStorageItem(itemName)
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    public Completable nullifyAllEntryItems() {
        return dao.nullifyAllEntryItems()
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    //---Dao--Delete----------------------------------------------------------------------------------------

    public Completable deleteStoredDataByYear(int year) {
        return dao.deleteGroupsByYear(year)
                .andThen(dao.deleteItemsByYear(year))
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    public Completable deleteStoredDataByMonth(int month) {
        return dao.deleteGroupsByMonth(month)
                .andThen(dao.deleteItemsByMonth(month))
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    public Completable deleteEntryGroupByName(String groupName) {
        return dao.deleteEntryGroup(groupName)
                .andThen(dao.deleteEntryItemsByParentGroupName(groupName))
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    public Completable deleteEntryItem(EntryItem entryItem) {
        return dao.deleteEntryItem(entryItem)
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    public Completable deleteStorageItems(StorageItem storageItem) {
        return dao.deleteStorageItem(storageItem)
                .andThen(dao.deleteEntryItemByName(storageItem.itemName))
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    //---Dao--Select----------------------------------------------------------------------------------------

    public Single<List<GroupWithTotals>> getGroupsWithTotalCounts(int date) {
        return dao.getGroupsWithTotalCounts(date)
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    public Maybe<EntryGroup> getFirstEntryGroup() {
        return dao.getFirstEntryGroup()
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    public Observable<List<EntryGroup>> getObservableEntryGroups() {
        return dao.getObservableEntryGroups()
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    public Single<List<ItemWithTotals>> getItemsWithTotalCounts(int date, String groupNames) {
        return dao.getItemsWithTotalCounts(date, groupNames)
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    public Observable<List<EntryItem>> getObservableEntryItems(String groupName) {
        return dao.getObservableEntryItems(groupName)
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    public Single<List<Integer>> getYears() {
        return dao.getYears()
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    public Single<List<Integer>> getMonths(int year) {
        return dao.getMonths(year)
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    public Single<YearAndMonthItemTotals> getYearAndMothItemTotals(int year, int month) {
        return dao.getYearAndMothItemTotals(year, month)
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    public Maybe<Integer> getStorageRemainingAmount(String itemName) {
        return dao.getStorageRemainingAmount(itemName)
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    public Maybe<Integer> getStoragePotentialRemainingAmount(String itemName, String parentGroupName) {
        return dao.getStoragePotentialRemainingAmount(itemName, parentGroupName)
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    public Single<List<String>> getUnusedItemsFromStorage(String groupName) {
        return dao.getUnusedItemsFromStorage(groupName)
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    public Observable<List<StorageItem>> getObservableStorageItems() {
        return dao.getObservableStorageItems()
                .subscribeOn(scheduler)
                .observeOn(thread);
    }

    //---Dao------------------------------------------------------------------------------------------------
}
