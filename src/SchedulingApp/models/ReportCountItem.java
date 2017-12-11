package SchedulingApp.models;

/**
 * Report Count Item. A report count item is a component of ReportCount. It is a
 * combination of an element's name and aggregate count.
 * 
 * @see ReportCount
 * @author Dale Richards <dric123@wgu.edu>
 */
public class ReportCountItem {
    // Item count
    private int count;
    
    // Name of the element
    private final String title;
    
    /**
     * Constructor
     * 
     * @param title 
     */
    public ReportCountItem(String title){
        this.title = title;
        this.count = 1;
    }
    
    /**
     * Get the current count
     * 
     * @return count
     */
    public int getCount() {
        return this.count;
    }
    
    /**
     * Get the element title
     * 
     * @return title
     */
    public String getTitle() {
        return this.title;
    }
    
    /**
     * Increment the count
     */
    public void increment() {
        this.count++;
    }
}
