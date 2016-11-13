package de.ea.winterpokal.model;

/**
 * Created by ea on 12.11.2016.
 */

public class Category {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

   private String id;
   private String title;

}
