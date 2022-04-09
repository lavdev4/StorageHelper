package com.lavdevapp.storagehelper.models.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lavdevapp.storagehelper.data.StorageRepository;
import com.lavdevapp.storagehelper.data.containers.GroupWithTotals;
import com.lavdevapp.storagehelper.data.containers.ItemWithTotals;
import com.lavdevapp.storagehelper.data.containers.YearAndMonthItemTotals;
import com.lavdevapp.storagehelper.data.entities.EntryGroup;
import com.lavdevapp.storagehelper.data.entities.EntryItem;
import com.lavdevapp.storagehelper.data.entities.StorageItem;

import java.time.YearMonth;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

public class MainViewModel extends AndroidViewModel {

    private final MutableLiveData<EntryGroup> selectedGroup;
    private final MutableLiveData<YearMonth> selectedDate;
    private final StorageRepository repository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        selectedGroup = new MutableLiveData<>();
        selectedDate = new MutableLiveData<>();
        repository = new StorageRepository(application);
    }

    //---LiveData----------------------------------------------------------------------------------------

    public void setSelectedGroup(EntryGroup department) {
        selectedGroup.setValue(department);
    }

    public void setSelectedDate(YearMonth date) {
        selectedDate.setValue(date);
    }

    public MutableLiveData<EntryGroup> getSelectedGroup() {
        return selectedGroup;
    }

    public String getSelectedGroupName() {
        EntryGroup entryGroup = selectedGroup.getValue();
        return entryGroup != null ? entryGroup.groupName : null;
    }

    public MutableLiveData<YearMonth> getSelectedDate() {
        return selectedDate;
    }

    //---------------------------------------------------------------------------------------------------

    public Completable insertEntryGroup(EntryGroup group) {
        return repository.insertEntryGroup(group);
    }

    public Single<List<GroupWithTotals>> getGroupsWithTotalCounts(int date) {
        return repository.getGroupsWithTotalCounts(date);
    }

    public Single<List<ItemWithTotals>> getItemsWithTotalCounts(int date, String groupName) {
        return repository.getItemsWithTotalCounts(date, groupName);
    }

    public Observable<List<EntryGroup>> getObservableEntryGroups() {
        return repository.getObservableEntryGroups();
    }

    public Maybe<EntryGroup> getFirstEntryGroup() {
        return repository.getFirstEntryGroup();
    }

    public Observable<List<EntryItem>> getObservableEntryItems(String groupName) {
        return repository.getObservableEntryItems(groupName);
    }

    public Observable<List<StorageItem>> getObservableStorageItems() {
        return repository.getObservableStorageItems();
    }

    public Completable insertStorageItem(StorageItem item) {
        return repository.insertStorageItem(item);
    }

    public Completable incrementEntryItem(int id) {
        return repository.incrementEntryItem(id);
    }

    public Completable decrementEntryItem(int id) {
        return repository.decrementEntryItem(id);
    }

    public Completable updateEntryItem(EntryItem item) {
        return repository.updateEntryItem(item);
    }

    public Completable incrementStorageItem(String itemName) {
        return repository.incrementStorageItem(itemName);
    }

    public Completable decrementStorageItem(String itemName) {
        return repository.decrementStorageItem(itemName);
    }

    public Completable updateStorageItem(StorageItem item) {
        return repository.updateStorageItem(item);
    }

    public Completable nullifyAllStorageItems() {
        return repository.nullifyAllStorageItems();
    }

    public Completable nullifyAllEntryItems() {
        return repository.nullifyAllEntryItems();
    }

    public Maybe<Integer> getStorageRemainingAmount(String itemName) {
        return repository.getStorageRemainingAmount(itemName);
    }

    public Maybe<Integer> getStoragePotentialRemainingAmount(String itemName, String parentGroupName) {
        return repository.getStoragePotentialRemainingAmount(itemName, parentGroupName);
    }

    public Completable deleteStoredDataByYear(int year) {
        return repository.deleteStoredDataByYear(year);
    }

    public Completable deleteStoredDataByMonth(int month) {
        return repository.deleteStoredDataByMonth(month);
    }

    public Completable deleteEntryItem(EntryItem entryItem) {
        return repository.deleteEntryItem(entryItem);
    }

    public Completable deleteStorageItem(StorageItem storageItem) {
        return repository.deleteStorageItems(storageItem);
    }

    public Completable deleteEntryGroupByName(String groupName) {
        return repository.deleteEntryGroupByName(groupName);
    }

    public Single<List<String>> getUnusedItemsFromStorage(String groupName) {
        return repository.getUnusedItemsFromStorage(groupName);
    }

    public Completable insertEntryDataInStorageData(int date) {
        return repository.insertEntryDataInStorageData(date);
    }

    public Completable insertEntryItems(List<String> itemNames, String parentGroupName) {
        return repository.insertEntryItems(itemNames, parentGroupName);
    }

    public Single<List<Integer>> getYears() {
        return repository.getYears();
    }

    public Single<List<Integer>> getMonths(int year) {
        return repository.getMonths(year);
    }

    public Single<YearAndMonthItemTotals> getYearAndMothItemTotals(int year, int month) {
        return repository.getYearAndMothItemTotals(year, month);
    }
}
