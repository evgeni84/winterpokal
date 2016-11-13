package de.ea.winterpokal.model;

/**
 * Created by ea on 12.11.2016.
 */

public class WPEntryAddToken {
    WPEntry entry;
    Category category;
    WPUser user;

    public WPEntry getEntry() {
        return entry;
    }

    public void setEntry(WPEntry entry) {
        this.entry = entry;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public WPUser getUser() {
        return user;
    }

    public void setUser(WPUser user) {
        this.user = user;
    }
}
