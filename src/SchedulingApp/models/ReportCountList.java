package SchedulingApp.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * ReportCountList. ReportCountList is an observable list of ReportCountItem.
 * It's a list of unique elements and their associated counts. Ideally this would
 * subclass some existing iterable.
 * 
 * @see ReportCountList
 * @author Dale Richards <dric123@wgu.edu>
 */
public class ReportCountList {
    private ObservableList<ReportCountItem> items = FXCollections.observableArrayList();
 
    public ReportCountList() {
    }
    
    public void add(ReportCountItem item) {
        this.items.add(item);
    }
    
    public int indexOf(String item) {
        int index = 0;
        for (ReportCountItem i : this.items) {
            if (i.getTitle().equals(item)) {
                return index;
            }
            
            index++;
        }

        return -1;
    }
    
    public ReportCountItem get(int index) {
        return this.items.get(index);
    }
    
    public ObservableList<ReportCountItem> list() {
        return this.items;
    }
    
    public int size() {
        return this.items.size();
    }
}
